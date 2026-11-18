package ui;

import management.KitchenQueue;
import management.OrderProcessor;
import management.TableManager;
import management.BillingService;
import management.LogService;
import model.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Entry point for the restaurant management simulation.
 * Drives the main console loop and delegates actions to the management layer.
 */
public class Main {

    private static final List<MenuItem> MENU = List.of(
            new MenuItem("Burger", 8.99),
            new MenuItem("Pizza", 10.99),
            new MenuItem("Pasta", 9.49),
            new MenuItem("Salad", 6.99),
            new MenuItem("Soda", 2.49)
    );

    private static final TableManager tableManager     = new TableManager(5);
    private static final KitchenQueue kitchenQueue     = new KitchenQueue();
    private static final OrderProcessor orderProcessor = new OrderProcessor(kitchenQueue, tableManager);
    private static final LogService logService         = new LogService();
    private static final BillingService billingService = new BillingService(tableManager);
    private static final Scanner scanner               = new Scanner(System.in);
    private static int nextOrderId                     = 1;

    /**
     * Launches the restaurant simulation.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMainMenu();
            String input = scanner.nextLine().trim();
            System.out.println();
            switch (input) {
                case "1" -> viewTables();
                case "2" -> seatGuests();
                case "3" -> tickKitchen();
                case "4" -> viewKitchenQueue();
                case "5" -> advanceOrder(OrderStatus.READY);
                case "6" -> advanceOrder(OrderStatus.SERVED);
                case "7" -> billTable();
                case "0" -> {
                    System.out.println("Goodbye!");
                    logService.shutdown();
                    running = false;
                }
                default  -> System.out.println("Invalid option, try again.");
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("=== Restaurant Manager ===");
        System.out.println("1. View tables");
        System.out.println("2. Seat guests");
        System.out.println("3. Tick kitchen");
        System.out.println("4. View kitchen queue");
        System.out.println("5. Mark an order ready");
        System.out.println("6. Mark an order served");
        System.out.println("7. Bill a table");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    private static void viewTables() {
        System.out.println("--- Tables ---");
        for (Table table : tableManager.getAllTables()) {
            String orderInfo = "";
            if (table.getCurrentOrder() != null) {
                Order o = table.getCurrentOrder();
                orderInfo = " | Order #" + o.getId() + " [" + o.getStatus() + "]";
            }
            System.out.println("Table " + table.getNumber() + ": " + table.getStatus() + orderInfo);
        }
    }

    private static void seatGuests() {
        Optional<Table> freeTable = tableManager.getFreeTable();
        if (freeTable.isEmpty()) {
            System.out.println("No free tables available.");
            return;
        }

        Table table = freeTable.get();
        System.out.println("Seating guests at table " + table.getNumber());

        Order order = new Order(nextOrderId++);
        printMenuItems();

        while (true) {
            System.out.print("Add item number (0 to finish): ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) break;

            try {
                int index = Integer.parseInt(input) - 1;
                if (index < 0 || index >= MENU.size()) {
                    System.out.println("Invalid item number.");
                    continue;
                }
                System.out.print("Quantity: ");
                int qty = Integer.parseInt(scanner.nextLine().trim());
                if (qty <= 0) { System.out.println("Quantity must be positive."); continue; }
                order.addItem(new OrderItem(MENU.get(index), qty));
                System.out.println("Added " + qty + "x " + MENU.get(index).getName());
            }
            catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        if (order.getItems().isEmpty()) {
            System.out.println("No items added - guests not seated.");
            nextOrderId--; // reclaim the ID since we're not using this order
            return;
        }

        orderProcessor.placeOrder(table.getNumber(), order);
        System.out.println("Order #" + order.getId() + " placed for table " + table.getNumber() + ".");
    }

    private static void printMenuItems() {
        System.out.println("\n--- Menu ---");
        for (int i = 0; i < MENU.size(); i++) {
            System.out.printf("%d. %-15s $%.2f%n", i + 1, MENU.get(i).getName(), MENU.get(i).getPrice());
        }
    }

    private static void tickKitchen() {
        Optional<Integer> processed = orderProcessor.tickKitchen();
        if (processed.isEmpty()) {
            System.out.println("Kitchen queue is empty - nothing to tick.");
            return;
        }
        int tableNumber = processed.get();
        Order order = tableManager.getTableByNumber(tableNumber).getCurrentOrder();
        System.out.println("Ticked kitchen: table " + tableNumber
                + " | order #" + order.getId()
                + " is now [" + order.getStatus() + "]");
    }

    private static void viewKitchenQueue() {
        Optional<Integer> next = kitchenQueue.peek();
        if (next.isEmpty()) {
            System.out.println("Kitchen queue is empty.");
        } else {
            System.out.println("Next in kitchen queue: table " + next.get());
        }
    }

    // --- Advance Order ---

    private static void advanceOrder(OrderStatus nextOrderStatus) {
        List<Order> currentOrders = orderProcessor.getCurrentOrders();
        OrderStatus requiredCurrentStatus;

        switch (nextOrderStatus) {
            case OrderStatus.READY  -> requiredCurrentStatus = OrderStatus.IN_KITCHEN;
            case OrderStatus.SERVED -> requiredCurrentStatus = OrderStatus.READY;
            default                 -> throw new IllegalStateException("You can only manually mark order ready or mark order served");
        }

        List<Order> availableOrders =
                currentOrders.stream()
                .filter(order -> order.getStatus().equals(requiredCurrentStatus))
                .toList();

        if (availableOrders.isEmpty()) {
            System.out.println("No available orders to advance to [" + nextOrderStatus + "]");
            return;
        }

        for (int i = 0; i < availableOrders.size(); ++i) {
            Order order = availableOrders.get(i);
            int selectionNum = i + 1;
            System.out.println("[" + selectionNum + "] " + "Order #" + order.getId());
        }

        while (true) {
            System.out.println("Select Order (0 to go back): ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) break;
            try {
                int index = Integer.parseInt(input) - 1;
                if (index < 0 || index >= availableOrders.size()) {
                    System.out.println("Invalid order number.");
                    continue;
                }

                Order selectedOrder = availableOrders.get(index);
                orderProcessor.advanceOrderWithId(selectedOrder.getId());

                System.out.println("Marked order #" + selectedOrder.getId() + " as [" + nextOrderStatus + "]");
                break;
            }
            catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // --- Billing ---

    private static void billTable() {
        List<Table> servedTables = tableManager.getAllTables().stream()
                .filter(t -> t.getCurrentOrder() != null
                        && t.getCurrentOrder().getStatus() == OrderStatus.SERVED)
                .toList();

        if (servedTables.isEmpty()) {
            System.out.println("No tables with a served order ready to be billed.");
            return;
        }

        System.out.println("--- Tables Ready to Bill ---");
        for (int i = 0; i < servedTables.size(); i++) {
            Table t = servedTables.get(i);
            System.out.printf("[%d] Table %d  (Order #%d)%n",
                    i + 1, t.getNumber(), t.getCurrentOrder().getId());
        }

        Table selected = null;
        while (selected == null) {
            System.out.print("Select table (0 to go back): ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) return;
            try {
                int index = Integer.parseInt(input) - 1;
                if (index < 0 || index >= servedTables.size()) {
                    System.out.println("Invalid selection.");
                    continue;
                }
                selected = servedTables.get(index);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        double tipPercentage = promptTip();
        Receipt receipt = billingService.printReceiptAndClearTable(selected.getNumber(), tipPercentage);
        logService.logCompletedOrder(receipt);
    }

    private static double promptTip() {
        while (true) {
            System.out.print("Enter tip percentage (e.g. 15 for 15%, 0 for no tip): ");
            String input = scanner.nextLine().trim();
            try {
                double tip = Double.parseDouble(input);
                if (tip < 0) {
                    System.out.println("Tip cannot be negative.");
                    continue;
                }
                return tip;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}