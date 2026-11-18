package management;

import model.Receipt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Records completed orders to a CSV log file.
 *
 * <p>Logging is performed on a background daemon thread using a
 * {@link LinkedBlockingQueue} as a buffer. The main thread submits log entries
 * to the queue without blocking, and the background thread removes from the queue
 * and writes entries.</p>
 *
 * <p>The log file is written in CSV format with the following columns:</p>
 * <pre>
 *     order_id, table_number, items, subtotal, tax, tip, total, timestamp
 * </pre>
 */
public class LogService {

    private static final String LOG_FILE  = "order_log.csv";
    private static final String SEPARATOR = ",";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Sentinel value placed in the queue to signal the background thread to stop. */
    private static final String KILL = "SHUTDOWN";

    private final LinkedBlockingQueue<String> queue;
    private final Thread loggingThread;

    private static final String SESSION_LOG_FILE = "session_log.csv";
    private int completedOrders = 0;
    private double totalRevenue = 0.0;

    /**
     * Constructs a LogService and starts the background logging thread.
     * Writes a CSV header to the log file if it does not already exist.
     */
    public LogService() {
        this.queue = new LinkedBlockingQueue<>();
        writeHeaderIfAbsent();

        this.loggingThread = new Thread(this::processQueue);
        this.loggingThread.setDaemon(true);
        this.loggingThread.setName("log-worker");
        this.loggingThread.start();
    }

    /**
     * Submits a completed order to the log queue.
     * This method returns immediately without waiting for the entry to be written.
     *
     * @param receipt received from the billing service after completing the order.
     */
    public void logCompletedOrder(Receipt receipt) {
        completedOrders++;
        totalRevenue += receipt.total();
        try {
            queue.put(buildEntry(receipt));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Signals the background thread to finish writing all queued entries and stop.
     * Blocks until the background thread has finished.
     */
    public void shutdown() {
        try {
            queue.put(KILL);
            loggingThread.join();
            writeSessionSummary();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // --- Private helpers ---

    /**
     * The task run by the background thread.
     * Blocks on the queue and writes each entry to the log file until
     * a kill command is received.
     */
    private void processQueue() {
        while (true) {
            try {
                String entry = queue.take();
                if (entry.equals(KILL)) break;
                writeToFile(entry);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // drain any remaining entries after the kill command
        String remaining;
        while ((remaining = queue.poll()) != null) {
            if (!remaining.equals(KILL)) writeToFile(remaining);
        }
    }

    /**
     * Appends a single CSV line to the log file.
     *
     * @param entry the formatted CSV line to write
     */
    private void writeToFile(String entry) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(entry);
        } catch (IOException e) {
            System.err.println("[LogService] Failed to write log entry: " + e.getMessage());
        }
    }

    /**
     * Writes the CSV header line to the log file if the file does not already exist.
     */
    private void writeHeaderIfAbsent() {
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, false))) {
                writer.println("order_id,table_number,items,subtotal,tax,tip,total,timestamp");
            } catch (IOException e) {
                System.err.println("[LogService] Failed to write log header: " + e.getMessage());
            }
        }
    }

    /**
     * Formats an order into a single CSV line.
     *
     * @param receipt contains all relevant immutable data about order entry
     * @return a formatted CSV string
     */
    private String buildEntry(Receipt receipt) {
        String items = receipt.order().getItems().stream()
                .map(item -> item.getItem().getName() + " x" + item.getQuantity())
                .reduce((a, b) -> a + "|" + b)
                .orElse("none");

        return String.join(SEPARATOR,
                String.valueOf(receipt.orderId()),
                String.valueOf(receipt.tableNumber()),
                items,
                String.format("%.2f", receipt.subtotal()),
                String.format("%.2f", receipt.tax()),
                String.format("%.2f", receipt.tip()),
                String.format("%.2f", receipt.total()),
                receipt.formattedTimestamp()
        );
    }

    /**
     * Writes a single summary line for the current session to the session log file.
     * The session number is determined by counting existing entries in the file.
     * Called on the main thread after the logging thread has fully stopped.
     */
    private void writeSessionSummary() {
        writeSessionHeaderIfAbsent();
        int sessionNumber = countExistingSessions() + 1;
        String summary = String.join(SEPARATOR,
                String.valueOf(sessionNumber),
                LocalDateTime.now().format(FORMATTER),
                String.valueOf(completedOrders),
                String.format("%.2f", totalRevenue)
        );
        try (PrintWriter writer = new PrintWriter(new FileWriter(SESSION_LOG_FILE, true))) {
            writer.println(summary);
        } catch (IOException e) {
            System.err.println("[LogService] Failed to write session summary: " + e.getMessage());
        }
    }

    /**
     * Writes the CSV header to the session log file if it does not already exist.
     */
    private void writeSessionHeaderIfAbsent() {
        File file = new File(SESSION_LOG_FILE);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(SESSION_LOG_FILE, false))) {
                writer.println("session,date,orders_completed,total_revenue");
            } catch (IOException e) {
                System.err.println("[LogService] Failed to write session log header: " + e.getMessage());
            }
        }
    }

    /**
     * Counts the number of existing session entries in the session log file
     * by counting non-header lines. Used to determine the next session number.
     *
     * @return the number of sessions already recorded, or 0 if the file is new
     */
    private int countExistingSessions() {
        File file = new File(SESSION_LOG_FILE);
        if (!file.exists()) return 0;
        try (Scanner fileScanner = new Scanner(file)) {
            int count = 0;
            boolean firstLine = true;
            while (fileScanner.hasNextLine()) {
                fileScanner.nextLine();
                if (firstLine) { firstLine = false; continue; } // skip header
                count++;
            }
            return count;
        } catch (IOException e) {
            System.err.println("[LogService] Failed to read session log: " + e.getMessage());
            return 0;
        }
    }
}