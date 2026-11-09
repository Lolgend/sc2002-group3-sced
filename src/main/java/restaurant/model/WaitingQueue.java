package restaurant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Line of arrived customers waiting to be selected and seated at a dining table.
 */
public class WaitingQueue {
    private final List<Customer> customers = new ArrayList<>();

    public void addCustomer(Customer customer) {
        Objects.requireNonNull(customer, "Customer cannot be null");
        customers.add(customer);
    }

    public boolean removeCustomer(Customer customer) {
        return customers.remove(customer);
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public boolean isEmpty() {
        return customers.isEmpty();
    }

    public int size() {
        return customers.size();
    }

    @Override
    public String toString() {
        if (customers.isEmpty()) {
            return "Waiting Queue: Empty";
        }
        StringBuilder sb = new StringBuilder("Waiting Queue (").append(customers.size()).append("):\n");
        for (int i = 0; i < customers.size(); i++) {
            sb.append("  [").append(i + 1).append("] ").append(customers.get(i)).append("\n");
        }
        return sb.toString().trim();
    }
}
