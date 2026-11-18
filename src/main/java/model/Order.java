package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a customer order consisting of one or more {@link OrderItem}s.
 * An order progresses through states defined by {@link OrderStatus}.
 */
public class Order {
    private final int id;
    private final List<OrderItem> items;
    private OrderStatus status;

    /**
     * Constructs a new Order with no items and an initial status of {@link OrderStatus#PLACED}.
     *
     * @param id the unique identifier for this order
     */
    public Order(int id) {
        if (id < 0) throw new IllegalArgumentException("Order ID cannot be negative");
        this.id = id;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PLACED;
    }

    /**
     * Adds an item to this order.
     *
     * @param item the order item to add
     */
    public void addItem(OrderItem item) {
        if (item == null) throw new IllegalArgumentException("Item cannot be null");
        items.add(item);
    }

    /** @return the unique ID of this order */
    public int getId() { return id; }

    /** @return an unmodifiable view of the items in this order */
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }

    /** @return the current status of this order */
    public OrderStatus getStatus() { return status; }

    /**
     * Updates the status of this order.
     *
     * @param status the new status to set
     */
    public void setStatus(OrderStatus status) {
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
        this.status = status;
    }
}