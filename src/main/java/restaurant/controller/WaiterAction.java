package restaurant.controller;

import restaurant.model.Customer;
import restaurant.model.Order;
import restaurant.model.Table;

import java.util.Objects;

/**
 * Value object representing a selectable action for the Waiter.
 */
public class WaiterAction {
    private final WaiterActionType type;
    private final String description;
    private final Customer customer;
    private final Table table;
    private final Order order;

    private WaiterAction(WaiterActionType type, String description, Customer customer, Table table, Order order) {
        this.type = Objects.requireNonNull(type, "Action type cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.customer = customer;
        this.table = table;
        this.order = order;
    }

    public static WaiterAction createWait() {
        return new WaiterAction(WaiterActionType.WAIT, "Wait (do nothing)", null, null, null);
    }

    public static WaiterAction createSeat(Customer customer, Table table) {
        Objects.requireNonNull(customer, "Customer cannot be null");
        Objects.requireNonNull(table, "Table cannot be null");
        String desc = String.format("Seat Customer #%d (%s, sat=%d) at Table #%d",
                customer.getId(), customer.getCustomerType(), customer.getSatisfaction(), table.getTableNumber());
        return new WaiterAction(WaiterActionType.SEAT_CUSTOMER, desc, customer, table, null);
    }

    public static WaiterAction createTakeOrder(Customer customer, Table table) {
        Objects.requireNonNull(customer, "Customer cannot be null");
        Objects.requireNonNull(table, "Table cannot be null");
        String desc = String.format("Take order from Customer #%d (%s, sat=%d) at Table #%d",
                customer.getId(), customer.getCustomerType(), customer.getSatisfaction(), table.getTableNumber());
        return new WaiterAction(WaiterActionType.TAKE_ORDER, desc, customer, table, null);
    }

    public static WaiterAction createServeOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        String desc = String.format("Serve Order #%d (%s) to Customer #%d at Table #%d",
                order.getId(), order.getMenuItem().getName(), order.getCustomer().getId(), order.getTable().getTableNumber());
        return new WaiterAction(WaiterActionType.SERVE_ORDER, desc, order.getCustomer(), order.getTable(), order);
    }

    public WaiterActionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Table getTable() {
        return table;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return description;
    }
}
