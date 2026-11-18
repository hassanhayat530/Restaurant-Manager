package management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class KitchenQueueTest {

    private KitchenQueue queue;

    @BeforeEach
    void setUp() {
        queue = new KitchenQueue();
    }

    // --- addTable / remove ---

    @Test
    void remove_afterOneAdd_returnsAddedTableNumber() {
        queue.addTable(3);
        assertEquals(Optional.of(3), queue.remove());
    }

    @Test
    void remove_emptyQueue_returnsEmpty() {
        assertTrue(queue.remove().isEmpty());
    }

    @Test
    void remove_multipleEntries_returnsFifoOrder() {
        queue.addTable(1);
        queue.addTable(2);
        queue.addTable(3);

        assertEquals(Optional.of(1), queue.remove());
        assertEquals(Optional.of(2), queue.remove());
        assertEquals(Optional.of(3), queue.remove());
    }

    @Test
    void remove_removesItemFromQueue() {
        queue.addTable(1);
        queue.remove();

        assertTrue(queue.remove().isEmpty()); // queue should now be empty
    }

    // --- peek ---

    @Test
    void peek_emptyQueue_returnsEmpty() {
        assertTrue(queue.peek().isEmpty());
    }

    @Test
    void peek_nonEmptyQueue_returnsFirstWithoutRemoving() {
        queue.addTable(5);
        queue.addTable(6);

        assertEquals(Optional.of(5), queue.peek()); // first item
        assertEquals(Optional.of(5), queue.peek()); // still there after peek
    }

    @Test
    void peek_doesNotModifyQueue() {
        queue.addTable(1);
        queue.peek();
        queue.peek();

        // after two peeks the item should still be removable
        assertEquals(Optional.of(1), queue.remove());
    }

    // --- isEmpty ---

    @Test
    void isEmpty_onNewQueue_returnsTrue() {
        assertTrue(queue.isEmpty());
    }

    @Test
    void isEmpty_afterAdd_returnsFalse() {
        queue.addTable(1);
        assertFalse(queue.isEmpty());
    }

    @Test
    void isEmpty_afterAddAndRemove_returnsTrue() {
        queue.addTable(1);
        queue.remove();
        assertTrue(queue.isEmpty());
    }
}