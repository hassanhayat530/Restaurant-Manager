/**
 * Contains the core data classes and enumerations for the restaurant simulation.
 *
 * <p>Classes in this package are only data holders with no business logic.
 * They represent the real-world entities of the simulation:</p>
 * <ul>
 *     <li>{@link model.MenuItem} - an item available on the restaurant menu.</li>
 *     <li>{@link model.OrderItem} - a menu item together with a quantity, as written on an order.</li>
 *     <li>{@link model.Order} - a collection of order items placed by guests at a table.</li>
 *     <li>{@link model.Table} - a physical table in the restaurant, holding at most one active order.</li>
 *     <li>{@link model.OrderStatus} - the lifecycle states an order passes through.</li>
 *     <li>{@link model.TableStatus} - the occupancy states a table can be in.</li>
 * </ul>
 */
package model;
