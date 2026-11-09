package restaurant.model;

import java.util.Objects;

/**
 * Represents a single dining table in the restaurant.
 * Holds at most one Customer and their active Order until paid or abandoned.
 */
public class Table {
    private final int tableNumber;
    private Customer currentCustomer;
    private Order currentOrder;

    public Table(int tableNumber) {
        if (tableNumber <= 0) {
            throw new IllegalArgumentException("Table number must be positive: " + tableNumber);
        }
        this.tableNumber = tableNumber;
        this.currentCustomer = null;
        this.currentOrder = null;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public boolean isFree() {
        return currentCustomer == null;
    }

    public void seatCustomer(Customer customer) {
        if (!isFree()) {
            throw new IllegalStateException("Table #" + tableNumber + " is already occupied by Customer #" + currentCustomer.getId());
        }
        this.currentCustomer = Objects.requireNonNull(customer, "Customer cannot be null");
        customer.markSeated();
    }

    public void assignOrder(Order order) {
        if (isFree()) {
            throw new IllegalStateException("Cannot assign order to empty Table #" + tableNumber);
        }
        if (this.currentOrder != null) {
            throw new IllegalStateException("Table #" + tableNumber + " already has an active order #" + this.currentOrder.getId());
        }
        this.currentOrder = Objects.requireNonNull(order, "Order cannot be null");
        this.currentCustomer.markOrderPlaced();
    }

    public void freeTable() {
        this.currentCustomer = null;
        this.currentOrder = null;
    }

    @Override
    public String toString() {
        if (isFree()) {
            return String.format("Table #%d [FREE]", tableNumber);
        }
        String orderInfo = currentOrder == null ? "No order yet" : currentOrder.toString();
        return String.format("Table #%d [Occupied by %s | %s]", tableNumber, currentCustomer, orderInfo);
    }
}
