package restaurant.staff;

import restaurant.model.Customer;
import restaurant.model.Table;
import restaurant.model.WaitingQueue;

public class Host extends Staff {

    public Host() {
        this("Host");
    }

    public Host(String name) {
        super(name);
    }

    public void seatCustomer(Customer customer, Table table, WaitingQueue queue) {
        table.seatCustomer(customer);
        queue.removeCustomer(customer);
        markActed();
    }

    public void waitTurn() {
        markActed();
    }
}
