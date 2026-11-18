package model;

/**
 * Represents the lifecycle states of an {@link Order}.
 */
public enum OrderStatus {
    /** The order has been placed but not yet sent to the kitchen. */
    PLACED,
    /** The order is currently being prepared in the kitchen. */
    IN_KITCHEN,
    /** The order is ready to be served to the table. */
    READY,
    /** The order has been delivered to the table. */
    SERVED
}