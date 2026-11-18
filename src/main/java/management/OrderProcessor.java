package management;

import model.Order;
import model.OrderStatus;
import model.Table;
import model.TableStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Processes orders through the kitchen pipeline.
 * Coordinates between {@link KitchenQueue} and {@link TableManager} to advance
 * orders through their lifecycle states.
 */
public class OrderProcessor {
    private final KitchenQueue kitchenQueue;
    private final TableManager tableManager;

    /**
     * Constructs an OrderProcessor.
     *
     * @param kitchenQueue the queue of pending table numbers
     * @param tableManager the manager holding all tables
     */
    public OrderProcessor(KitchenQueue kitchenQueue, TableManager tableManager) {
        if (kitchenQueue == null) throw new IllegalArgumentException("KitchenQueue cannot be null");
        if (tableManager == null) throw new IllegalArgumentException("TableManager cannot be null");
        this.kitchenQueue = kitchenQueue;
        this.tableManager = tableManager;
    }

    /**
     * Places an order at a specific table and enqueues it in the kitchen queue.
     *
     * @param tableNumber the number of the table placing the order
     * @param order       the order being placed
     * @throws IllegalStateException    if the table is not free
     * @throws IllegalArgumentException if no table with that number exists
     */
    public void placeOrder(int tableNumber, Order order) {
        Table table = tableManager.getTableByNumber(tableNumber);
        if (table.getStatus() != TableStatus.FREE) {
            throw new IllegalStateException("Table " + tableNumber + " is not free.");
        }
        table.assignOrder(order);
        kitchenQueue.addTable(tableNumber);
    }

    /**
     * Advances the order at the front of the kitchen queue by one state.
     *
     * @return the table number that was processed, or empty if the queue was empty
     */
    public Optional<Integer> tickKitchen() {
        Optional<Integer> tableNumber = kitchenQueue.remove();
        tableNumber.ifPresent(this::advanceOrderAtTable);
        return tableNumber;
    }

    /**
     * Advances the order at a specific table by one state.
     *
     * @param tableNumber the number of the table whose order to advance
     * @throws IllegalStateException if there is no active order at that table,
     *                               or if the order is already served
     */
    public void advanceOrderAtTable(int tableNumber) {
        Table table = tableManager.getTableByNumber(tableNumber);
        Order order = table.getCurrentOrder();
        if (order == null) throw new IllegalStateException("No active order at table " + tableNumber);
        advanceOrder(order);
    }

    /**
     * Advances an order to the next state in the pipeline.
     *
     * @param order the order to advance
     * @throws IllegalStateException if the order has already been served
     */
    private void advanceOrder(Order order) {
        switch (order.getStatus()) {
            case PLACED     -> order.setStatus(OrderStatus.IN_KITCHEN);
            case IN_KITCHEN -> order.setStatus(OrderStatus.READY);
            case READY      -> {
                order.setStatus(OrderStatus.SERVED);
            }
            case SERVED     -> throw new IllegalStateException(
                    "Order " + order.getId() + " is already served.");
        }
    }

    /**
     * Advances the order with the specific id by one state
     * @param id the id of the order to advance
     * @throws IllegalArgumentException if no order with the specific id exists
     */
    public void advanceOrderWithId(int id) {
        advanceOrder(getCurrentOrders().stream().
                filter(o -> o.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such order with id: " + id)));
    }

    /**
     * @return the current orders being processed
     */
    public List<Order> getCurrentOrders() {
        return tableManager.getAllTables().stream()
                .map(Table::getCurrentOrder)
                .filter(Objects::nonNull)
                .toList();
    }
}