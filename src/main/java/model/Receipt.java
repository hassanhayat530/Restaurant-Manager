package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An immutable record representing a completed billing transaction.
 *
 * <p>A {@code Receipt} is produced by {@link management.BillingService}
 * after billing a table, and can be passed to other management.
 *
 * @param orderId     the unique ID of the order that was billed
 * @param tableNumber the number of the table that was billed
 * @param order       the order that was billed
 * @param subtotal    the total before tax and tip
 * @param tax         the tax amount applied
 * @param tip         the tip amount applied
 * @param total       the final amount charged
 * @param timestamp   the date and time the receipt was generated
 */
public record Receipt(
        int orderId,
        int tableNumber,
        Order order,
        double subtotal,
        double tax,
        double tip,
        double total,
        LocalDateTime timestamp
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Returns the timestamp formatted as a human-readable string.
     *
     * @return the formatted timestamp
     */
    public String formattedTimestamp() {
        return timestamp.format(FORMATTER);
    }
}