# Restaurant Manager

A console-based restaurant management simulation written in Java. You play the role of the person running the floor: seat guests, take their orders, push orders through the kitchen, serve them, and print the bill at the end.

Everything happens through a simple interactive menu in the terminal.

## Features

- 5 tables, numbered 1 to 5, each either `FREE`, `OCCUPIED`, or `RESERVED`
- Take orders from a built-in menu (burger, pizza, pasta, salad, soda) with quantities
- A first-in, first-out kitchen queue, so the order that has waited longest gets picked up first
- A clear order lifecycle: `PLACED` -> `IN_KITCHEN` -> `READY` -> `SERVED`
- Billing with a fixed 15% tax and an optional tip, printed as a formatted receipt
- Tables are cleared automatically after billing

## Requirements

- Java 25 or later
- Maven 3.6 or later

## Build

From the project root (the folder with `pom.xml`):

```
mvn package
```

This compiles everything and produces a runnable JAR at `target/RestaurantManager-1.0.jar`.

## Run

```
java -jar target/RestaurantManager-1.0.jar
```

You'll see the main menu:

```
=== Restaurant Manager ===
1. View tables
2. Seat guests
3. Tick kitchen (PLACED -> IN_KITCHEN)
4. View kitchen queue
5. Mark order ready (IN_KITCHEN -> READY)
6. Mark order served (READY -> SERVED)
7. Bill a table
0. Exit
Enter choice:
```

Type a number and press Enter.

## Tests

```
mvn test
```

All tests should pass.

## A typical session

1. Choose `2` to seat guests. They get the first free table, and you add items by number and quantity (enter `0` when done).
2. Choose `3` to tick the kitchen. The oldest waiting order moves to `IN_KITCHEN`.
3. Choose `5` to mark that order `READY`.
4. Choose `6` to mark it `SERVED`.
5. Choose `7` to bill the table, enter a tip percentage (0 for none), and get your receipt.

The receipt looks like this:

```
+----------------------------------+
|       RESTAURANT RECEIPT         |
+----------------------------------+
|  Order #1     Table 1            |
+----------------------------------+
|  Burger       x2      $  17.98   |
|  Soda         x2      $   4.98   |
+----------------------------------+
|  Subtotal              $  22.96  |
|  Tax (15%)             $   3.44  |
|  Tip (10%)             $   2.30  |
+----------------------------------+
|  TOTAL                 $  28.70  |
+----------------------------------+
```

For a full walkthrough of every menu option, see the user documentation.

## Rules to know about

- A table can only be seated when it is `FREE`. If none are free, you're told so and sent back to the menu.
- If you start seating guests but don't add any items, the seating is cancelled.
- An order can only be billed once it has reached `SERVED`.
- Tax is always 15% and can't be turned off.