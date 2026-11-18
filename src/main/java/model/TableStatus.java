package model;

/**
 * Represents the occupancy state of a {@link Table}.
 */
public enum TableStatus {
    /** The table is unoccupied and available. */
    FREE,
    /** The table is currently occupied by guests. */
    OCCUPIED,
    /** The table has been reserved for incoming guests. */
    RESERVED
}