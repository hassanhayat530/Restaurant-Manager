package management;

import model.Order;
import model.Table;
import model.TableStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TableManagerTest {

    private TableManager manager;

    @BeforeEach
    void setUp() {
        manager = new TableManager(3);
    }

    @Test
    void constructor_validCount_createsCorrectNumberOfTables() {
        var tables = manager.getAllTables();
        assertEquals(3, tables.size());
    }

    @Test
    void constructor_invalidCount_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new TableManager(0));
    }

    @Test
    void getFreeTable_whenTablesAreAvailable_returnsTable() {
        Optional<Table> result = manager.getFreeTable();
        assertTrue(result.isPresent());
    }

    @Test
    void seatGuests_withFreeTable_tableBecomesOccupied() {
        Order order = new Order(1);
        manager.seatGuests(order);

        Optional<Table> nowOccupied = manager.getAllTables().stream()
                .filter(t -> t.getStatus() == TableStatus.OCCUPIED)
                .findFirst();
        assertTrue(nowOccupied.isPresent());
    }

    @Test
    void clearTable_afterSeating_tableBecomeseFree() {
        Order order = new Order(1);
        Optional<Table> seated = manager.seatGuests(order);

        assertTrue(seated.isPresent()); // check the arrange step worked
        int tableNumber = seated.get().getNumber();

        manager.clearTable(tableNumber);

        assertEquals(TableStatus.FREE, manager.getTableByNumber(tableNumber).getStatus());
    }

    @Test
    void getTableByNumber_invalidNumber_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> manager.getTableByNumber(99));
    }

    @Test
    void seatGuests_withOccupiedTable_returnsEmpty() {
        for (int i = 0; i < manager.getAllTables().size(); ++i) {
            Order order = new Order(i + 1);
            Optional<Table> seated = manager.seatGuests(order);

            assertTrue(seated.isPresent());
        }

        assertTrue((manager.seatGuests(new Order(4)).isEmpty()));
    }
}