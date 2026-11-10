package restaurant.staff;

import restaurant.model.Order;
import restaurant.model.OrderStatus;

import java.util.Objects;

/**
 * Kitchen staff responsible for advancing at most one active order by one preparation unit per turn.
 */
public class Chef extends Staff {

    public Chef() {
        this("Chef");
    }

    public Chef(String name) {
        super(name);
    }

    /**
     * Prepares an in-progress order by 1 unit.
     */
    public void prepareOrder(Order order) {
        checkAvailability();
        Objects.requireNonNull(order, "Order cannot be null");

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot prepare order #" + order.getId() + ": status is " + order.getStatus() + ", expected IN_PROGRESS.");
        }

        order.advancePreparation();
        markActed();
    }

    public void waitTurn() {
        markActed();
    }
}
