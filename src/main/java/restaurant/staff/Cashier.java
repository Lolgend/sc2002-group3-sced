package restaurant.staff;

import restaurant.model.Customer;
import restaurant.model.CustomerState;
import restaurant.model.Order;
import restaurant.model.Table;

import java.util.List;
import java.util.Objects;

/**
 * Front-of-house staff responsible for automatically collecting payment from at most one
 * READY_TO_PAY customer per turn, prioritizing the oldest customer ready to pay.
 */
public class Cashier extends Staff {

    public Cashier() {
        this("Cashier");
    }

    public Cashier(String name) {
        super(name);
    }

    public Order processOldestReadyPayment(List<Table> tables) {
        Objects.requireNonNull(tables, "Tables list cannot be null");

        Table oldestReadyTable = null;
        int lowestSequence = Integer.MAX_VALUE;

        for (Table table : tables) {
            if (!table.isFree()) {
                Customer customer = table.getCurrentCustomer();
                if (customer.getState() == CustomerState.READY_TO_PAY) {
                    if (customer.getReadyToPaySequence() < lowestSequence) {
                        lowestSequence = customer.getReadyToPaySequence();
                        oldestReadyTable = table;
                    }
                }
            }
        }

        if (oldestReadyTable == null) {
            return null;
        }

        checkAvailability();
        Customer customer = oldestReadyTable.getCurrentCustomer();
        Order order = oldestReadyTable.getCurrentOrder();

        customer.markPaid();
        order.markPaid();
        oldestReadyTable.freeTable();
        markActed();

        return order;
    }
}
