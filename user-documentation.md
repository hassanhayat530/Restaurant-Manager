# Restaurant Management Simulation:
# User Documentation

## Overview

This is a console-based restaurant management simulation. It allows you to manage
tables, place and process orders, track kitchen workflow, and generate billing
receipts through an interactive console menu. Completed orders and session
summaries are automatically recorded to log files in the project root.

---

## Requirements

- Java 25 or later
- Maven 3.6 or later

---

## Building the Project

Navigate to the project root (the folder containing `pom.xml`) and run:

```
mvn package
```

This compiles the project and produces a runnable JAR file at:

```
target/RestaurantManager-1.0.jar
```

---

## Running the Application

From the project root, run:

```
java -jar target/RestaurantManager-1.0.jar
```

You will be greeted with the main menu:

```
=== Restaurant Manager ===
1. View tables
2. Seat guests
3. Tick kitchen
4. View kitchen queue
5. Mark order ready
6. Mark order served
7. Bill a table
0. Exit
Enter choice:
```

Type the number for the action you want and press Enter.

---

## Running the Tests

```
mvn test
```

All tests should pass with no failures.

---

## Menu Options

### 1. View Tables
Displays the current status of every table in the restaurant. Each line shows
the table number, its status (`FREE`, `OCCUPIED`, or `RESERVED`), and if occupied,
the current order number and its status.

Example output:
```
--- Tables ---
Table 1: OCCUPIED | Order #1 [IN_KITCHEN]
Table 2: FREE
Table 3: FREE
Table 4: FREE
Table 5: FREE
```

---

### 2. Seat Guests
Finds the first available free table and takes an order for it. You will be
shown the menu and prompted to add items one at a time by number and quantity.
Enter `0` when you are done adding items.

Example:
```
Seating guests at table 1

--- Menu ---
1. Burger          $8.99
2. Pizza           $10.99
3. Pasta           $9.49
4. Salad           $6.99
5. Soda            $2.49

Add item number (0 to finish): 1
Quantity: 2
Added 2x Burger
Add item number (0 to finish): 5
Quantity: 2
Added 2x Soda
Add item number (0 to finish): 0
Order #1 placed for table 1.
```

If no tables are free, you will be told so and returned to the main menu.
If you finish adding items without adding anything, the seating is cancelled.

---

### 3. Tick Kitchen
Picks up the order that has been waiting the longest in the kitchen queue and
advances it from `PLACED` to `IN_KITCHEN`. This simulates the kitchen beginning
to prepare the order.

Example:
```
Ticked kitchen: table 1 | order #1 is now [IN_KITCHEN]
```

If the kitchen queue is empty, you will be told so.

---

### 4. View Kitchen Queue
Shows the table number at the front of the kitchen queue i.e. the order that
will be picked up next when you tick the kitchen.

Example:
```
Next in kitchen queue: table 2
```

---

### 5. Mark Order Ready
Displays all orders currently being prepared in the kitchen and lets you select
one to mark as ready. This simulates the kitchen finishing preparation.

Example:
```
--- Orders ---
[1] Order #1
Select order (0 to go back):
1
Marked order #1 as [READY]
```

---

### 6. Mark Order Served
Displays all orders that are ready to be served and lets you select one to mark
as served. This simulates a waiter delivering the order to the table.

Example:
```
--- Orders ---
[1] Order #1
Select order (0 to go back):
1
Marked order #1 as [SERVED]
```

---

### 7. Bill a Table
Displays all tables with a served order and lets you select one to bill.
You will be prompted to enter a tip percentage (enter `0` for no tip).
A formatted receipt is then printed, the table is cleared, and the order
is recorded to the log file.

Example:
```
--- Tables Ready to Bill ---
[1] Table 1  (Order #1)
Select table (0 to go back): 1
Enter tip percentage (e.g. 15 for 15%, 0 for no tip): 10

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
Table 1 has been cleared.
```

---

## Log Files

The application automatically maintains two CSV log files in the project root.
Both files are created on the first run and appended to on every subsequent run.

### order_log.csv
Records every completed order. Each row represents one billed order with the
following columns:

```
order_id, table_number, items, subtotal, tax, tip, total, timestamp
```

Example:
```
order_id,table_number,items,subtotal,tax,tip,total,timestamp
1,1,Burger x2|Soda x2,22.96,3.44,2.30,28.70,2026-05-21 14:30:00
2,3,Pizza x1,10.99,1.65,0.00,12.64,2026-05-21 14:45:00
```

### session_log.csv
Records a one-line summary for each session (each run of the application).
Written automatically when you exit via option `0`. Each row contains the
following columns:

```
session, date, orders_completed, total_revenue
```

Example:
```
session,date,orders_completed,total_revenue
1,2026-05-21 14:50:00,2,41.34
2,2026-05-22 09:30:00,5,201.78
```

Note that the session summary is only written when you exit via option `0`.
Closing the terminal window or force-quitting the application may result in
the current session not being recorded.

---

## Full Example Session

The following is a complete walkthrough of a typical session from seating to billing.

**Step 1**: Select `2` to seat guests. Add 2x Burger and 2x Soda. Confirm the order is placed.

**Step 2**: Select `1` to view tables. Table 1 should show `OCCUPIED | Order #1 [PLACED]`.

**Step 3**: Select `3` to tick the kitchen. The output should confirm order #1 is now `IN_KITCHEN`.

**Step 4**: Select `5` to mark the order ready. Select order #1. It is now `READY`.

**Step 5**: Select `6` to mark the order served. Select order #1. It is now `SERVED`.

**Step 6**: Select `7` to bill the table. Select table 1, enter a tip percentage, and the receipt will be printed. Table 1 is now `FREE`.

**Step 7**: Select `1` to view tables and confirm table 1 is free again.

**Step 8**: Select `0` to exit. The session summary is written to `session_log.csv`.

---
 
## Notes

- The simulation starts with **5 tables** numbered 1 through 5.
- Tax is fixed at **15%** and is always applied.
- A table cannot be seated until it is `FREE`.
- An order cannot be billed until it has reached the `SERVED` state.
- Orders are processed by the kitchen in the order they were placed when you tick kitchen. (first in, first out).
- Both log files are created automatically in the project root on first run.