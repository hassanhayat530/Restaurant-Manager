package management;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {

    private TableManager tableManager;
    private BillingService billingService;
    private MenuItem burger;
    private MenuItem soda;

    @BeforeEach
    void setUp() {
        tableManager   = new TableManager(3);
        billingService = new BillingService(tableManager);
        burger         = new MenuItem("Burger", 8.00);
        soda           = new MenuItem("Soda",   2.00);
    }

    // --- calculateSubtotal ---

    @Test
    void calculateSubtotal_singleItem_returnsCorrectTotal() {
        Order order = new Order(1);
        order.addItem(new OrderItem(burger, 3));

        assertEquals(24.00, billingService.calculateSubtotal(order), 0.001);
    }

    @Test
    void calculateSubtotal_multipleItems_sumsAllLines() {
        Order order = new Order(1);
        order.addItem(new OrderItem(burger, 2)); // 16.00
        order.addItem(new OrderItem(soda,   4)); //  8.00

        assertEquals(24.00, billingService.calculateSubtotal(order), 0.001);
    }

    @Test
    void calculateSubtotal_emptyOrder_returnsZero() {
        Order order = new Order(1);
        assertEquals(0.00, billingService.calculateSubtotal(order), 0.001);
    }

    @Test
    void calculateSubtotal_nullOrder_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> billingService.calculateSubtotal(null));
    }

    // --- calculateTax ---

    @Test
    void calculateTax_nonZeroSubtotal_returnsCorrectAmount() {
        double tax = billingService.calculateTax(100.00);
        assertEquals(15.00, tax, 0.001);
    }

    @Test
    void calculateTax_zeroSubtotal_returnsZero() {
        assertEquals(0.00, billingService.calculateTax(0.00), 0.001);
    }

    // --- calculateTip ---

    @Test
    void calculateTip_fifteenPercent_returnsCorrectAmount() {
        double tip = billingService.calculateTip(100.00, 15.0);
        assertEquals(15.00, tip, 0.001);
    }

    @Test
    void calculateTip_zeroPercent_returnsZero() {
        assertEquals(0.00, billingService.calculateTip(100.00, 0.0), 0.001);
    }

    @Test
    void calculateTip_negativePercentage_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> billingService.calculateTip(100.00, -5.0));
    }

    // --- printReceiptAndClearTable ---

    @Test
    void printReceiptAndClearTable_clearsTableAfterBilling() {
        Order order = new Order(1);
        order.addItem(new OrderItem(burger, 1));
        tableManager.getTableByNumber(1).assignOrder(order);

        billingService.printReceiptAndClearTable(1, 0.0);

        assertNull(tableManager.getTableByNumber(1).getCurrentOrder());
        assertEquals(TableStatus.FREE, tableManager.getTableByNumber(1).getStatus());
    }

    @Test
    void printReceiptAndClearTable_tableWithNoOrder_throwsException() {
        // Table 1 exists but has no order assigned
        assertThrows(IllegalStateException.class,
                () -> billingService.printReceiptAndClearTable(1, 10.0));
    }

    @Test
    void printReceiptAndClearTable_invalidTableNumber_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> billingService.printReceiptAndClearTable(99, 10.0));
    }

    // --- constructor ---

    @Test
    void constructor_nullTableManager_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new BillingService(null));
    }
}