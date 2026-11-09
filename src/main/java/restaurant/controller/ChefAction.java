package restaurant.controller;

import restaurant.model.Order;

import java.util.Objects;

/**
 * Value object representing a selectable action for the Chef.
 */
public class ChefAction {
    private final boolean wait;
    private final String description;
    private final Order order;

    private ChefAction(boolean wait, String description, Order order) {
        this.wait = wait;
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.order = order;
    }

    public static ChefAction createWait() {
        return new ChefAction(true, "Wait (do nothing)", null);
    }

    public static ChefAction createCook(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        String desc = String.format("Cook Order #%d (%s, progress %d/%d) for Customer #%d at Table #%d",
                order.getId(), order.getMenuItem().getName(),
                order.getCompletedPrepUnits(), order.getTotalPrepUnits(),
                order.getCustomer().getId(), order.getTable().getTableNumber());
        return new ChefAction(false, desc, order);
    }

    public boolean isWait() {
        return wait;
    }

    public String getDescription() {
        return description;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return description;
    }
}
