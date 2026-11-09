package restaurant.model;

/**
 * State lifecycle of a Customer in Restaurant Rush.
 */
public enum CustomerState {
    WAITING_IN_QUEUE,
    SEATED,
    WAITING_FOR_FOOD,
    EATING,
    READY_TO_PAY,
    PAID,
    ABANDONED
}
