package model;

/**
 * Represents a single order item written on an order.
 */
public class OrderItem {
    private final MenuItem item;
    private final int quantity;

    /**
     * Constructs a new OrderItem.
     *
     * @param item the menu item that is ordered
     * @param quantity the quantity of the menu item ordered
     */
    public OrderItem(MenuItem item, int quantity) {
        if (item == null) throw new IllegalArgumentException("Item cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");

        this.item = item;
        this.quantity = quantity;
    }

    /** @return the menu item */
    public MenuItem getItem() { return item; }

    /** @return the quantity of the menu item ordered */
    public int getQuantity() { return quantity; }
}
