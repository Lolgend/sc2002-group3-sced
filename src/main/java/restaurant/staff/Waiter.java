package restaurant.staff;

import restaurant.model.Customer;
import restaurant.model.CustomerState;
import restaurant.model.Menu;
import restaurant.model.MenuItem;
import restaurant.model.Order;
import restaurant.model.Table;
import restaurant.model.WaitingQueue;

import java.util.Objects;
import java.util.Random;

public class Waiter extends Staff {

    public Waiter() {
        this("Waiter");
    }

    public Waiter(String name) {
        super(name);
    }

    public void seatCustomer(Customer customer, Table table, WaitingQueue queue) {
        checkAvailability();
        Objects.requireNonNull(customer, "Customer cannot be null");
        Objects.requireNonNull(table, "Table cannot be null");
        Objects.requireNonNull(queue, "WaitingQueue cannot be null");

        if (!queue.getCustomers().contains(customer)) {
            throw new IllegalStateException("Customer #" + customer.getId()
                    + " is not in the waiting queue.");
        }

        table.seatCustomer(customer);
        queue.removeCustomer(customer);
        markActed();
    }

    public Order takeOrder(int orderId, Customer customer, Table table, Menu menu, Random random) {
        checkAvailability();
        Objects.requireNonNull(customer, "Customer cannot be null");
        Objects.requireNonNull(table, "Table cannot be null");
        Objects.requireNonNull(menu, "Menu cannot be null");
        Objects.requireNonNull(random, "Random cannot be null");

        if (table.getCurrentCustomer() != customer) {
            throw new IllegalStateException("Customer #" + customer.getId() + " is not seated at Table #" + table.getTableNumber());
        }
        if (customer.getState() != CustomerState.SEATED) {
            throw new IllegalStateException("Cannot take order: Customer #" + customer.getId() + " is in state " + customer.getState());
        }

        MenuItem chosenItem = customer.chooseMenuItem(menu, random);
        int finalPriceCents = chosenItem.getPriceCents();
        Order order = new Order(orderId, customer, table, chosenItem, finalPriceCents);
        table.assignOrder(order);
        markActed();
        return order;
    }

    public void serveOrder(Order order, int turn) {
        checkAvailability();
        Objects.requireNonNull(order, "Order cannot be null");

        Customer customer = order.getCustomer();
        Table table = order.getTable();

        if (customer.getState() != CustomerState.WAITING_FOR_FOOD) {
            throw new IllegalStateException("Cannot serve customer in state: "
                    + customer.getState());
        }
        if (table.getCurrentCustomer() != customer
                || table.getCurrentOrder() != order) {
            throw new IllegalStateException(
                    "Order does not match the table's current customer and order.");
        }

        order.markServed();
        customer.markServed(turn);
        markActed();
    }

    public void waitTurn() {
        checkAvailability();
        markActed();
    }
}
