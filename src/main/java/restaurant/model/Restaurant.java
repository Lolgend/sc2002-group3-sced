package restaurant.model;

import restaurant.staff.Cashier;
import restaurant.staff.Chef;
import restaurant.staff.Waiter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Restaurant {
    public static final int MAX_TURNS = 18;
    public static final int NUM_TABLES = 2;
    public static final int[] ARRIVAL_TURNS = {1, 4, 7, 10, 13, 16};
    public static final int TARGET_PAID_CUSTOMERS = 4;
    public static final int TARGET_REVENUE_CENTS = 4500; // $45.00
    public static final double TARGET_AVG_SATISFACTION = 60.0;
    public static final long DEFAULT_SEED = 42L;

    private final WaitingQueue waitingQueue;
    private final List<Table> tables;
    private final Menu menu;
    private final List<Order> orders;
    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;
    private final Random random;

    private int nextCustomerId;
    private int nextOrderId;
    private int nextReadyToPaySeq;

    private int revenueCents;
    private int paidCustomersCount;
    private int totalPaidSatisfaction;
    private int abandonedCustomersCount;

    public Restaurant() {
        this(DEFAULT_SEED);
    }

    public Restaurant(long seed) {
        this.waitingQueue = new WaitingQueue();
        List<Table> tableList = new ArrayList<>();
        for (int i = 1; i <= NUM_TABLES; i++) {
            tableList.add(new Table(i));
        }
        this.tables = Collections.unmodifiableList(tableList);
        this.menu = Menu.createBaseMenu();
        this.orders = new ArrayList<>();
        this.waiter = new Waiter("Waiter");
        this.chef = new Chef("Chef");
        this.cashier = new Cashier("Cashier");
        this.random = new Random(seed);

        this.nextCustomerId = 1;
        this.nextOrderId = 1;
        this.nextReadyToPaySeq = 1;

        this.revenueCents = 0;
        this.paidCustomersCount = 0;
        this.totalPaidSatisfaction = 0;
        this.abandonedCustomersCount = 0;
    }

    public WaitingQueue getWaitingQueue() {
        return waitingQueue;
    }

    public List<Table> getTables() {
        return tables;
    }

    public Menu getMenu() {
        return menu;
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public Chef getChef() {
        return chef;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public Random getRandom() {
        return random;
    }

    public int getRevenueCents() {
        return revenueCents;
    }

    public int getPaidCustomersCount() {
        return paidCustomersCount;
    }

    public int getTotalPaidSatisfaction() {
        return totalPaidSatisfaction;
    }

    public int getAbandonedCustomersCount() {
        return abandonedCustomersCount;
    }

    public double getAveragePaidSatisfaction() {
        if (paidCustomersCount == 0) {
            return Double.NaN;
        }
        return (double) totalPaidSatisfaction / paidCustomersCount;
    }

    public String getAveragePaidSatisfactionString() {
        if (paidCustomersCount == 0) {
            return "N/A";
        }
        return String.format("%.1f", getAveragePaidSatisfaction());
    }

    public int allocateNextOrderId() {
        return nextOrderId++;
    }

    public int allocateNextReadyToPaySequence() {
        return nextReadyToPaySeq++;
    }

    public void recordOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        this.orders.add(order);
    }

    public void recordPayment(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        this.revenueCents += order.getFinalPriceCents();
        this.paidCustomersCount++;
        this.totalPaidSatisfaction += order.getCustomer().getSatisfaction();
    }

    /**
     * Checks if a customer arrives on the given turn.
     */
    public Customer processArrival(int turn) {
        boolean arrives = false;
        for (int arrivalTurn : ARRIVAL_TURNS) {
            if (arrivalTurn == turn) {
                arrives = true;
                break;
            }
        }
        if (!arrives) {
            return null;
        }
        Customer customer = createCustomer(nextCustomerId++, turn);
        waitingQueue.addCustomer(customer);
        return customer;
    }

    protected Customer createCustomer(int id, int turn) {
        return new RegularCustomer(id, turn);
    }

    public List<Table> processEatingCompletions(int turn) {
        List<Table> finishedTables = new ArrayList<>();
        for (Table table : tables) {
            if (!table.isFree()) {
                Customer customer = table.getCurrentCustomer();
                if (customer.hasFinishedEating(turn)) {
                    customer.markReadyToPay(allocateNextReadyToPaySequence());
                    finishedTables.add(table);
                }
            }
        }
        return finishedTables;
    }

    public List<Customer> processTurnEndDecayAndAbandonment() {
        List<Customer> abandonedThisTurn = new ArrayList<>();

        // Decay queued customers
        List<Customer> queued = new ArrayList<>(waitingQueue.getCustomers());
        for (Customer c : queued) {
            boolean abandoned = c.applyTurnEndSatisfactionDecay();
            if (abandoned) {
                waitingQueue.removeCustomer(c);
                abandonedCustomersCount++;
                abandonedThisTurn.add(c);
            }
        }

        // Decay seated/unserved customers at tables
        for (Table table : tables) {
            if (!table.isFree()) {
                Customer c = table.getCurrentCustomer();
                boolean abandoned = c.applyTurnEndSatisfactionDecay();
                if (abandoned) {
                    abandonedCustomersCount++;
                    abandonedThisTurn.add(c);
                    Order activeOrder = table.getCurrentOrder();
                    if (activeOrder != null && (activeOrder.getStatus() == OrderStatus.IN_PROGRESS
                            || activeOrder.getStatus() == OrderStatus.READY)) {
                        activeOrder.markCancelled();
                    }
                    table.freeTable();
                }
            }
        }

        return abandonedThisTurn;
    }

    /**
     * Evaluates victory condition after Turn 18:
     * - Paid customers >= 4
     * - Revenue >= $45.00 (4500 cents)
     * - Average satisfaction of paid customers >= 60.0
     */
    public boolean isVictory() {
        if (paidCustomersCount < TARGET_PAID_CUSTOMERS) {
            return false;
        }
        if (revenueCents < TARGET_REVENUE_CENTS) {
            return false;
        }
        if (paidCustomersCount == 0) {
            return false;
        }
        return getAveragePaidSatisfaction() >= TARGET_AVG_SATISFACTION;
    }
}
