package model;

/**
 * Represents a physical table in the restaurant.
 * A table holds at most one active {@link Order} at a time.
 */
public class Table {
    private final int number;
    private TableStatus status;
    private Order currentOrder;

    /**
     * Constructs a new Table with no active order and an initial status of {@link TableStatus#FREE}.
     *
     * @param number the table number, used to identify it in the restaurant
     */
    public Table(int number) {
        if (number <= 0) throw new IllegalArgumentException("Table number must be positive");
        this.number = number;
        this.status = TableStatus.FREE;
        this.currentOrder = null;
    }

    /**
     * Assigns an order to this table and marks it as {@link TableStatus#OCCUPIED}.
     *
     * @param order the order to assign
     */
    public void assignOrder(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");
        this.currentOrder = order;
        this.status = TableStatus.OCCUPIED;
    }

    /**
     * Clears the current order and marks the table as {@link TableStatus#FREE}.
     */
    public void clear() {
        this.currentOrder = null;
        this.status = TableStatus.FREE;
    }

    /** @return the table number */
    public int getNumber() { return number; }

    /** @return the current status of this table */
    public TableStatus getStatus() { return status; }

    /**
     * Sets the status of this table directly, for cases such as reservations.
     *
     * @param status the new status to set
     */
    public void setStatus(TableStatus status) {
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
        this.status = status;
    }

    /** @return the current order assigned to this table, or {@code null} if none */
    public Order getCurrentOrder() { return currentOrder; }
}