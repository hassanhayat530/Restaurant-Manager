package management;

import model.Order;
import model.OrderItem;

/**
 * Handles billing for completed orders.
 * Calculates subtotals, applies tax, and prints a formatted receipt to the console.
 */
public class BillingService {

    private static final double TAX_RATE = 0.15;

    private final TableManager tableManager;

    /**
     * Constructs a BillingService.
     *
     * @param tableManager the manager holding all tables
     */
    public BillingService(TableManager tableManager) {
        if (tableManager == null) throw new IllegalArgumentException("TableManager cannot be null");
        this.tableManager = tableManager;
    }

    /**
     * Calculates the subtotal for an order before tax and tip.
     *
     * @param order the order to calculate
     * @return the subtotal in currency units
     */
    public double calculateSubtotal(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");
        return order.getItems().stream()
                .mapToDouble(item -> item.getItem().getPrice() * item.getQuantity())
                .sum();
    }

    /**
     * Calculates the tax amount for a given subtotal.
     *
     * @param subtotal the pre-tax amount
     * @return the tax amount
     */
    public double calculateTax(double subtotal) {
        return subtotal * TAX_RATE;
    }

    /**
     * Calculates the tip amount for a given subtotal and tip percentage.
     *
     * @param subtotal      the pre-tax amount
     * @param tipPercentage the tip percentage (e.g. 15 for 15%)
     * @return the tip amount
     */
    public double calculateTip(double subtotal, double tipPercentage) {
        if (tipPercentage < 0) throw new IllegalArgumentException("Tip percentage cannot be negative");
        return subtotal * (tipPercentage / 100.0);
    }

    /**
     * Prints a formatted receipt to the console and clears the table.
     *
     * @param tableNumber   the table number being billed
     * @param tipPercentage the tip percentage to apply
     * @throws IllegalStateException if the table has no current order
     */
    public void printReceiptAndClearTable(int tableNumber, double tipPercentage) {
        Order order = tableManager.getTableByNumber(tableNumber).getCurrentOrder();
        if (order == null) throw new IllegalStateException("No order at table " + tableNumber);

        double subtotal = calculateSubtotal(order);
        double tax      = calculateTax(subtotal);
        double tip      = calculateTip(subtotal, tipPercentage);
        double total    = subtotal + tax + tip;

        System.out.println("+----------------------------------+");
        System.out.println("|       RESTAURANT RECEIPT         |");
        System.out.println("+----------------------------------+");
        System.out.printf( "|  Order #%-4d  Table %-2d           |%n", order.getId(), tableNumber);
        System.out.println("+----------------------------------+");

        for (OrderItem item : order.getItems()) {
            double lineTotal = item.getItem().getPrice() * item.getQuantity();
            System.out.printf("|  %-14s x%-2d    $%7.2f  |%n",
                    item.getItem().getName(), item.getQuantity(), lineTotal);
        }

        System.out.println("+----------------------------------+");
        System.out.printf( "|  Subtotal              $%7.2f  |%n", subtotal);
        System.out.printf( "|  Tax (%.0f%%)             $%7.2f  |%n", TAX_RATE * 100, tax);
        System.out.printf( "|  Tip (%2.0f%%)             $%7.2f  |%n", tipPercentage, tip);
        System.out.println("+----------------------------------+");
        System.out.printf( "|  TOTAL                 $%7.2f  |%n", total);
        System.out.println("+----------------------------------+");

        tableManager.getTableByNumber(tableNumber).clear();
        System.out.println("Table " + tableNumber + " has been cleared.");
    }
}