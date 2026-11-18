package management;

import java.util.ArrayDeque;
import java.util.Optional;

/**
 * Manages a queue of pending table numbers in the FIFO(first in, first out) order.
 */
public class KitchenQueue {
    private final ArrayDeque<Integer> queue =  new ArrayDeque<>();

    /**
     * Adds a table number to the back of the queue.
     *
     * @param tableNumber the number of the table that just ordered
     */
    public void addTable(int tableNumber) {
        queue.addLast(tableNumber);
    }

    /**
     * Removes the table number from the front of the queue.
     *
     * @return the table number that has been waiting the longest, or empty if the queue is empty
     */
    public Optional<Integer> remove() {
        return Optional.ofNullable(queue.poll());
    }

    /**
     * Looks at the next item in the queue without removing it.
     *
     * @return the next item in the queue. Otherwise, empty if the queue is empty.
     */
    public Optional<Integer> peek() {
        return Optional.ofNullable(queue.peek());
    }

    /**
     * @return true if the queue is empty, else false.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
