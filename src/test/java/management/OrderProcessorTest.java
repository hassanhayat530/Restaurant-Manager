package management;

import model.Order;
import model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessorTest {

    private TableManager tableManager;
    private KitchenQueue kitchenQueue;
    private OrderProcessor orderProcessor;

    @BeforeEach
    void setUp() {
        tableManager   = new TableManager(3);
        kitchenQueue   = new KitchenQueue();
        orderProcessor = new OrderProcessor(kitchenQueue, tableManager);
    }

    // --- placeOrder ---

    @Test
    void placeOrder_assignsOrderToCorrectTable() {
        Order order = new Order(1);
        orderProcessor.placeOrder(1, order);
        assertEquals(order, tableManager.getTableByNumber(1).getCurrentOrder());
    }

    @Test
    void placeOrder_enqueuesTableNumber() {
        orderProcessor.placeOrder(1, new Order(1));
        assertEquals(Optional.of(1), kitchenQueue.peek());
    }

    @Test
    void placeOrder_onOccupiedTable_throwsException() {
        orderProcessor.placeOrder(1, new Order(1));
        assertThrows(IllegalStateException.class, () -> orderProcessor.placeOrder(1, new Order(2)));
    }

    // --- tickKitchen ---

    @Test
    void tickKitchen_onEmptyQueue_returnsEmpty() {
        assertTrue(orderProcessor.tickKitchen().isEmpty());
    }

    @Test
    void tickKitchen_advancesOrderFromPlacedToInKitchen() {
        Order order = new Order(1);
        orderProcessor.placeOrder(1, order);

        orderProcessor.tickKitchen();

        assertEquals(OrderStatus.IN_KITCHEN, order.getStatus());
    }

    @Test
    void tickKitchen_returnsCorrectTableNumber() {
        orderProcessor.placeOrder(2, new Order(1));
        Optional<Integer> result = orderProcessor.tickKitchen();
        assertEquals(Optional.of(2), result);
    }

    @Test
    void tickKitchen_processesFifoOrder() {
        Order order1 = new Order(1);
        Order order2 = new Order(2);
        orderProcessor.placeOrder(1, order1);
        orderProcessor.placeOrder(2, order2);

        orderProcessor.tickKitchen(); // should process table 1 first

        assertEquals(OrderStatus.IN_KITCHEN, order1.getStatus());
        assertEquals(OrderStatus.PLACED,     order2.getStatus()); // table 2 untouched
    }

    // --- advanceOrderAtTable / state transitions ---

    @Test
    void advanceOrderAtTable_progressesThroughAllStates() {
        Order order = new Order(1);
        orderProcessor.placeOrder(1, order);

        orderProcessor.advanceOrderAtTable(1);
        assertEquals(OrderStatus.IN_KITCHEN, order.getStatus());

        orderProcessor.advanceOrderAtTable(1);
        assertEquals(OrderStatus.READY, order.getStatus());

        orderProcessor.advanceOrderAtTable(1);
        assertEquals(OrderStatus.SERVED, order.getStatus());
    }

    @Test
    void advanceOrderAtTable_cannotSkipFromPlacedToReady() {
        Order order = new Order(1);
        orderProcessor.placeOrder(1, order);

        orderProcessor.advanceOrderAtTable(1);

        // After one tick it must be IN_KITCHEN, not READY or SERVED
        assertEquals(OrderStatus.IN_KITCHEN, order.getStatus());
        assertNotEquals(OrderStatus.READY,   order.getStatus());
        assertNotEquals(OrderStatus.SERVED,  order.getStatus());
    }

    @Test
    void advanceOrderAtTable_onServedOrder_throwsException() {
        Order order = new Order(1);
        orderProcessor.placeOrder(1, order);

        orderProcessor.advanceOrderAtTable(1); // -> IN_KITCHEN
        orderProcessor.advanceOrderAtTable(1); // -> READY
        orderProcessor.advanceOrderAtTable(1); // -> SERVED

        assertThrows(IllegalStateException.class, () -> orderProcessor.advanceOrderAtTable(1));
    }

    @Test
    void advanceOrderAtTable_onTableWithNoOrder_throwsException() {
        // Table 1 exists but has no order assigned
        assertThrows(IllegalStateException.class, () -> orderProcessor.advanceOrderAtTable(1));
    }
}