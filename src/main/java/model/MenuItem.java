package model;

/**
 * Represents a single item available on the restaurant menu.
 */
public class MenuItem {
    private final String name;
    private final double price;

    /**
     * Constructs a new MenuItem.
     *
     * @param name  the display name of the item
     * @param price the price of the item in currency units
     */
    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    /** @return the name of this menu item */
    public String getName() { return name; }

    /** @return the price of this menu item */
    public double getPrice() { return price; }
}