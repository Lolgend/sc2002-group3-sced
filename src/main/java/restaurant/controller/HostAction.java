package restaurant.controller;

import restaurant.model.Customer;
import restaurant.model.Table;

public class HostAction {
    private final boolean wait;
    private final String description;
    private final Customer customer;
    private final Table table;

    private HostAction(boolean wait, String description, Customer customer, Table table) {
        this.wait = wait;
        this.description = description;
        this.customer = customer;
        this.table = table;
    }

    public static HostAction createWait() {
        return new HostAction(true, "Wait (do nothing)", null, null);
    }

    public static HostAction createSeat(Customer customer, Table table) {
        String desc = String.format("Seat Customer #%d (%s, sat=%d) at Table #%d",
                customer.getId(), customer.getCustomerType(), customer.getSatisfaction(), table.getTableNumber());
        return new HostAction(false, desc, customer, table);
    }

    public boolean isWait() {
        return wait;
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

    @Override
    public String toString() {
        return description;
    }
}
