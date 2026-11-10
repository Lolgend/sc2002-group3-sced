package restaurant.model;

import restaurant.staff.Cashier;
import restaurant.staff.Chef;
import restaurant.staff.Waiter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class Restaurant {
    public static final int MAX_TURNS = GameConfig.DEFAULT_MAX_TURNS;
    public static final int NUM_TABLES = GameConfig.DEFAULT_NUM_TABLES;
    public static final int TARGET_PAID_CUSTOMERS = GameConfig.DEFAULT_TARGET_PAID_CUSTOMERS;
    public static final int TARGET_REVENUE_CENTS = GameConfig.DEFAULT_TARGET_REVENUE_CENTS;
    public static final double TARGET_AVG_SATISFACTION = GameConfig.DEFAULT_TARGET_AVG_SATISFACTION;
    public static final long DEFAULT_SEED = GameConfig.DEFAULT_SEED;

    private final GameConfig config;
    private final WaitingQueue waitingQueue;
    private final List<Table> tables;
    private final Menu menu;
    private final List<Order> orders;
    private final Set<Integer> recordedPaymentOrderIds = new HashSet<>();
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
        this(new GameConfig());
    }

    public Restaurant(long seed) {
        this(new GameConfig(seed));
    }

    public Restaurant(GameConfig config) {
        this.config = Objects.requireNonNull(config, "GameConfig cannot be null");
        this.waitingQueue = new WaitingQueue();
        List<Table> tableList = new ArrayList<>();
        for (int i = 1; i <= config.getNumTables(); i++) {
            tableList.add(new Table(i));
        }
        this.tables = Collections.unmodifiableList(tableList);
        this.menu = Menu.createBaseMenu();
        this.orders = new ArrayList<>();
        this.waiter = new Waiter("Waiter");
        this.chef = new Chef("Chef");
        this.cashier = new Cashier("Cashier");
        this.random = new Random(config.getSeed());

        this.nextCustomerId = 1;
        this.nextOrderId = 1;
        this.nextReadyToPaySeq = 1;

        this.revenueCents = 0;
        this.paidCustomersCount = 0;
        this.totalPaidSatisfaction = 0;
        this.abandonedCustomersCount = 0;
    }

    public GameConfig getConfig() {
        return config;
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

    public int getServedCustomersCount() {
        int servedCount = getPaidCustomersCount();

        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.SERVED) {
                servedCount++;
            }
        }

        return servedCount;
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

        if (!orders.contains(order)) {
            throw new IllegalArgumentException(
                    "Cannot record payment for an order outside this restaurant.");
        }

        Customer customer = order.getCustomer();

        if (order.getStatus() != OrderStatus.PAID
                || customer.getState() != CustomerState.PAID) {
            throw new IllegalStateException(
                    "Order and customer must both be PAID before recording payment.");
        }

        Integer satisfactionAtPayment = customer.getSatisfactionAtPayment();

        if (!recordedPaymentOrderIds.add(order.getId())) {
            throw new IllegalStateException(
                    "Payment has already been recorded for order #" + order.getId());
        }

        this.revenueCents += order.getFinalPriceCents();
        this.paidCustomersCount++;
        this.totalPaidSatisfaction += satisfactionAtPayment;
    }

    // Checks if a customer arrives on the given turn.
     
    public Customer processArrival(int turn) {
        if (!config.isArrivalTurn(turn)) {
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
     * Evaluates victory condition after Turn 18.
     */
    public boolean isVictory() {
        return config.isVictory(paidCustomersCount, revenueCents, getAveragePaidSatisfaction());
    }
}
