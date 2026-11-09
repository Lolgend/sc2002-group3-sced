package restaurant.model;

import java.util.Objects;

/**
 * Tracks a customer's ordered menu item, locked price, kitchen progress, and lifecycle status.
 */
public class Order {
    private final int id;
    private final Customer customer;
    private final Table table;
    private final MenuItem menuItem;
    private final int finalPriceCents;
    private final int totalPrepUnits;
    private int completedPrepUnits;
    private OrderStatus status;

    public Order(int id, Customer customer, Table table, MenuItem menuItem, int finalPriceCents) {
        if (id <= 0) {
            throw new IllegalArgumentException("Order ID must be positive: " + id);
        }
        this.id = id;
        this.customer = Objects.requireNonNull(customer, "Customer cannot be null");
        this.table = Objects.requireNonNull(table, "Table cannot be null");
        this.menuItem = Objects.requireNonNull(menuItem, "MenuItem cannot be null");
        if (finalPriceCents < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + finalPriceCents);
        }
        this.finalPriceCents = finalPriceCents;
        this.totalPrepUnits = menuItem.getPrepUnits();
        this.completedPrepUnits = 0;
        this.status = OrderStatus.IN_PROGRESS;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Table getTable() {
        return table;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getFinalPriceCents() {
        return finalPriceCents;
    }

    public int getTotalPrepUnits() {
        return totalPrepUnits;
    }

    public int getCompletedPrepUnits() {
        return completedPrepUnits;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void advancePreparation() {
        if (status != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot advance preparation of order in status: " + status);
        }
        completedPrepUnits++;
        if (completedPrepUnits >= totalPrepUnits) {
            status = OrderStatus.READY;
        }
    }

    public void markServed() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException("Cannot serve order that is not READY; current status: " + status);
        }
        status = OrderStatus.SERVED;
    }

    public void markPaid() {
        if (status != OrderStatus.SERVED) {
            throw new IllegalStateException("Cannot pay order that is not SERVED; current status: " + status);
        }
        status = OrderStatus.PAID;
    }

    public void markCancelled() {
        if (status != OrderStatus.IN_PROGRESS && status != OrderStatus.READY) {
            throw new IllegalStateException("Cannot cancel order in status: " + status);
        }
        status = OrderStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return String.format("Order #%d (%s for Customer #%d at Table #%d, %d/%d units, status=%s, $%.2f)",
                id, menuItem.getName(), customer.getId(), table.getTableNumber(),
                completedPrepUnits, totalPrepUnits, status, finalPriceCents / 100.0);
    }
}
