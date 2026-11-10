package restaurant.staff;

import restaurant.model.Order;

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

        order.advancePreparation();
        markActed();
    }

    public void waitTurn() {
        checkAvailability();
        markActed();
    }
}
