package management;

import model.Table;
import model.TableStatus;
import model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Manages the collection of {@link Table}s in the restaurant.
 * Provides operations for seating guests, freeing tables, and querying table states.
 */
public class TableManager {
    private final List<Table> tables;

    /**
     * Constructs a TableManager and initialises it with a given number of tables.
     *
     * @param tableCount the number of tables in the restaurant
     */
    public TableManager(int tableCount) {
        if (tableCount <= 0) throw new IllegalArgumentException("Number of tables should be positive.");

        this.tables = new ArrayList<>();
        for (int i = 1; i <= tableCount; ++i) {
            tables.add(new Table(i));
        }
    }

    /**
     * Returns the first available free table, if one exists.
     *
     * @return an Optional containing a free Table, or empty if none are available
     */
    public Optional<Table> getFreeTable() {
        return tables.stream()
                .filter(table -> table.getStatus() == TableStatus.FREE)
                .findFirst();
    }

    /**
     * Seats guests at the first available free table and assigns their order.
     *
     * @param order the order to assign to the table
     * @return the Table that was assigned, or empty if no free table was available
     */
    public Optional<Table> seatGuests(Order order) {
        Optional<Table> freeTable = getFreeTable();

        freeTable.ifPresent(table -> table.assignOrder(order));

        return freeTable;
    }

    /**
     * Retrieves a table by its number.
     *
     * @param tableNumber the number of the table to find
     * @return the matching Table
     * @throws IllegalArgumentException if no table with that number exists
     */
    public Table getTableByNumber(int tableNumber) {
        return tables.stream()
                .filter(table -> table.getNumber() == tableNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such table with number: " + tableNumber));

    }

    /**
     * Clears a table by its number, marking it as free.
     *
     * @param tableNumber the number of the table to clear
     * @throws IllegalArgumentException if no table with that number exists
     */
    public void clearTable(int tableNumber) {
        getTableByNumber(tableNumber).clear();
    }

    /** @return an unmodifiable view of all tables */
    public List<Table> getAllTables() {
        return Collections.unmodifiableList(tables);
    }
}
