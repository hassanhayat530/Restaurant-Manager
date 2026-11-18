/**
 * Contains service classes responsible for managing restaurant operations.
 *
 * <p>Classes in this package coordinate business logic and enforce rules
 * that individual model objects cannot enforce themselves:</p>
 * <ul>
 *     <li>{@link management.TableManager} - owns the collection of all tables,
 *         handles seating guests, and enforces that a table must be free before
 *         an order can be assigned to it.</li>
 *     <li>{@link management.KitchenQueue} - maintains a FIFO queue of table numbers
 *         representing orders waiting to be picked up by the kitchen.</li>
 *     <li>{@link management.OrderProcessor} - coordinates between {@code KitchenQueue}
 *         and {@code TableManager} to move orders through the kitchen pipeline,
 *         enforcing that state transitions happen in the correct order.</li>
 *     <li>{@link management.BillingService} - calculates subtotals, tax, and tip
 *         for a completed order, prints a formatted receipt to the console,
 *         and clears the table once billing is complete.</li>
 * </ul>
 */
package management;