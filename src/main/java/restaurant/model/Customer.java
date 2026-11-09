package restaurant.model;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Abstract base class for all restaurant customers.
 * Provides polymorphic hooks for customer behavior (loss rate, menu choice, billing)
 * enabling Stage 2 VIP and Critic extensions without modifying caller code (ADR-0001).
 */
public abstract class Customer {
    private final int id;
    private final int arrivalTurn;
    private int satisfaction;
    private CustomerState state;
    private Integer satisfactionAtPayment;
    private int readyToPaySequence;
    private int servedTurn;

    protected Customer(int id, int arrivalTurn) {
        if (id <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive: " + id);
        }
        if (arrivalTurn <= 0) {
            throw new IllegalArgumentException("Arrival turn must be positive: " + arrivalTurn);
        }
        this.id = id;
        this.arrivalTurn = arrivalTurn;
        this.satisfaction = 100;
        this.state = CustomerState.WAITING_IN_QUEUE;
        this.satisfactionAtPayment = null;
        this.readyToPaySequence = -1;
        this.servedTurn = -1;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTurn() {
        return arrivalTurn;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerState getState() {
        return state;
    }

    public Integer getSatisfactionAtPayment() {
        return satisfactionAtPayment;
    }

    public int getReadyToPaySequence() {
        return readyToPaySequence;
    }

    public int getServedTurn() {
        return servedTurn;
    }

    /**
     * @return display string for customer type ("Regular", "VIP", "Critic")
     */
    public abstract String getCustomerType();

    /**
     * @return satisfaction points lost at the end of each unserved turn
     */
    public abstract int getWaitingSatisfactionLoss();

    /**
     * Polymorphic customer menu item selection.
     * Stage 1 regular customer chooses uniformly at random from available items.
     */
    public MenuItem chooseMenuItem(Menu menu, Random random) {
        Objects.requireNonNull(menu, "Menu cannot be null");
        Objects.requireNonNull(random, "Random cannot be null");
        List<MenuItem> items = menu.getItems();
        return items.get(random.nextInt(items.size()));
    }

    /**
     * Polymorphic bill calculation hook.
     * Regular customer pays 100% of base price. Overridden by VIP customer in Stage 2.
     */
    public int calculateBillCents(int basePriceCents) {
        return basePriceCents;
    }

    /**
     * Checks if the customer is still waiting for service (queue, seated waiting for order, or waiting for food).
     */
    public boolean isWaitingForService() {
        return state == CustomerState.WAITING_IN_QUEUE
                || state == CustomerState.SEATED
                || state == CustomerState.WAITING_FOR_FOOD;
    }

    /**
     * Applies turn-end satisfaction decay if the customer is waiting for service.
     * Clamps satisfaction to [0, 100]. If satisfaction reaches 0, customer abandons.
     *
     * @return true if the customer abandoned on this decay, false otherwise
     */
    public boolean applyTurnEndSatisfactionDecay() {
        if (!isWaitingForService()) {
            return false;
        }
        this.satisfaction = Math.max(0, this.satisfaction - getWaitingSatisfactionLoss());
        if (this.satisfaction == 0) {
            this.state = CustomerState.ABANDONED;
            return true;
        }
        return false;
    }

    public void markSeated() {
        if (state != CustomerState.WAITING_IN_QUEUE) {
            throw new IllegalStateException("Cannot seat customer in state: " + state);
        }
        this.state = CustomerState.SEATED;
    }

    public void markOrderPlaced() {
        if (state != CustomerState.SEATED) {
            throw new IllegalStateException("Cannot place order for customer in state: " + state);
        }
        this.state = CustomerState.WAITING_FOR_FOOD;
    }

    public void markServed(int turn) {
        if (state != CustomerState.WAITING_FOR_FOOD) {
            throw new IllegalStateException("Cannot serve customer in state: " + state);
        }
        this.state = CustomerState.EATING;
        this.servedTurn = turn;
    }

    public void markReadyToPay(int sequenceNumber) {
        if (state != CustomerState.EATING) {
            throw new IllegalStateException("Cannot transition to READY_TO_PAY from state: " + state);
        }
        this.state = CustomerState.READY_TO_PAY;
        this.readyToPaySequence = sequenceNumber;
    }

    public void markPaid() {
        if (state != CustomerState.READY_TO_PAY) {
            throw new IllegalStateException("Cannot pay for customer in state: " + state);
        }
        this.state = CustomerState.PAID;
        this.satisfactionAtPayment = this.satisfaction;
    }

    @Override
    public String toString() {
        return String.format("Customer #%d (%s, sat=%d, state=%s)",
                id, getCustomerType(), satisfaction, state);
    }
}
