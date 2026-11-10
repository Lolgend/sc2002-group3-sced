package restaurant.staff;

import restaurant.model.Customer;
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
        Objects.requireNonNull(customer, "Customer cannot be null");
        Objects.requireNonNull(table, "Table cannot be null");
        Objects.requireNonNull(menu, "Menu cannot be null");
        Objects.requireNonNull(random, "Random cannot be null");

        MenuItem chosenItem = customer.chooseMenuItem(menu, random);
        int finalPriceCents = customer.calculateBillCents(chosenItem);
        Order order = new Order(orderId, customer, table, chosenItem, finalPriceCents);
        table.assignOrder(order);
        markActed();
        return order;
    }

    public void serveOrder(Order order, int turn) {
        Objects.requireNonNull(order, "Order cannot be null");

        order.markServed();
        order.getCustomer().markServed(turn);
        markActed();
    }

    public void waitTurn() {
        markActed();
    }
}
