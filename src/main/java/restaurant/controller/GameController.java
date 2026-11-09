package restaurant.controller;

import restaurant.cli.ConsoleUI;
import restaurant.model.Customer;
import restaurant.model.CustomerState;
import restaurant.model.GameConfig;
import restaurant.model.GameResult;
import restaurant.model.Order;
import restaurant.model.OrderStatus;
import restaurant.model.Restaurant;
import restaurant.model.Table;
import restaurant.staff.Cashier;
import restaurant.staff.Chef;
import restaurant.staff.Waiter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controller orchestrating the 18-turn game loop and the 6-step turn execution sequence.
 */
public class GameController {
    private final Restaurant restaurant;
    private final ConsoleUI ui;

    public GameController(Restaurant restaurant, ConsoleUI ui) {
        this.restaurant = Objects.requireNonNull(restaurant, "Restaurant cannot be null");
        this.ui = Objects.requireNonNull(ui, "ConsoleUI cannot be null");
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public ConsoleUI getUi() {
        return ui;
    }

    /**
     * Runs the full 18-turn game loop with no early termination.
     */
    public void runGame() {
        for (int turn = 1; turn <= restaurant.getConfig().getMaxTurns(); turn++) {
            runTurn(turn);
        }
        ui.displayFinalResult(evaluateGameResult());
    }

    public GameResult evaluateGameResult() {
        GameConfig config = restaurant.getConfig();
        double avgSat = restaurant.getAveragePaidSatisfaction();
        int paidCount = restaurant.getPaidCustomersCount();
        int revCents = restaurant.getRevenueCents();
        int abandonedCount = restaurant.getAbandonedCustomersCount();

        boolean victory = config.isVictory(paidCount, revCents, avgSat);

        return new GameResult(
                victory,
                paidCount,
                config.getTargetPaidCustomers(),
                config.isPaidTargetMet(paidCount),
                revCents,
                config.getTargetRevenueCents(),
                config.isRevenueTargetMet(revCents),
                avgSat,
                config.getTargetAvgSatisfaction(),
                config.isSatisfactionTargetMet(avgSat),
                abandonedCount
        );
    }

    /**
     * Executes a single turn through all 6 sequential steps.
     */
    public void runTurn(int turn) {
        // Reset staff capacities at the start of each turn
        restaurant.getWaiter().resetTurnState();
        restaurant.getChef().resetTurnState();
        restaurant.getCashier().resetTurnState();

        // Step 1: Arrivals
        executeStep1Arrivals(turn);

        // Step 2 & 3: Manager Choices, Kitchen, and Serving
        executeStep2And3StaffActions(turn);

        // Step 4: Eating and Payment (Two-Phase order: Cashier first, then eating completion)
        executeStep4EatingAndPayment(turn);

        // Step 5: Waiting and Abandonment
        executeStep5WaitingAndAbandonment();

        // Step 6: End-of-Turn Summary
        executeStep6EndSummary(turn);
    }

    private void executeStep1Arrivals(int turn) {
        Customer arrived = restaurant.processArrival(turn);
        if (arrived != null) {
            ui.displayArrival(arrived, turn);
        }
        ui.displayStartOfTurn(turn, restaurant);
    }

    private void executeStep2And3StaffActions(int turn) {
        // --- 1. Waiter Phase ---
        List<WaiterAction> waiterActions = buildLegalWaiterActions();
        List<String> waiterDescriptions = new ArrayList<>();
        for (WaiterAction a : waiterActions) {
            waiterDescriptions.add(a.getDescription());
        }

        int waiterChoice = ui.promptMenuChoice("Assign Waiter Task", waiterDescriptions);
        WaiterAction chosenWaiterAction = waiterActions.get(waiterChoice);
        executeWaiterAction(chosenWaiterAction, turn);

        // --- 2. Chef Phase ---
        List<ChefAction> chefActions = buildLegalChefActions();
        List<String> chefDescriptions = new ArrayList<>();
        for (ChefAction a : chefActions) {
            chefDescriptions.add(a.getDescription());
        }

        int chefChoice = ui.promptMenuChoice("Assign Chef Task", chefDescriptions);
        ChefAction chosenChefAction = chefActions.get(chefChoice);
        executeChefAction(chosenChefAction);
    }

    public List<WaiterAction> buildLegalWaiterActions() {
        List<WaiterAction> actions = new ArrayList<>();
        // Option 0 is always Wait
        actions.add(WaiterAction.createWait());

        // Seating actions (if at least one table is free)
        Table lowestFreeTable = getLowestNumberedFreeTable();
        if (lowestFreeTable != null) {
            for (Customer c : restaurant.getWaitingQueue().getCustomers()) {
                actions.add(WaiterAction.createSeat(c, lowestFreeTable));
            }
        }

        // Take order actions for seated customers
        for (Table table : restaurant.getTables()) {
            if (!table.isFree()) {
                Customer c = table.getCurrentCustomer();
                if (c.getState() == CustomerState.SEATED) {
                    actions.add(WaiterAction.createTakeOrder(c, table));
                }
            }
        }

        // Serve order actions for READY orders
        for (Table table : restaurant.getTables()) {
            if (!table.isFree()) {
                Order order = table.getCurrentOrder();
                if (order != null && order.getStatus() == OrderStatus.READY) {
                    actions.add(WaiterAction.createServeOrder(order));
                }
            }
        }

        return actions;
    }

    private Table getLowestNumberedFreeTable() {
        for (Table table : restaurant.getTables()) {
            if (table.isFree()) {
                return table;
            }
        }
        return null;
    }

    private void executeWaiterAction(WaiterAction action, int turn) {
        Waiter waiter = restaurant.getWaiter();
        switch (action.getType()) {
            case WAIT -> {
                waiter.waitTurn();
                ui.displayWaiterAction("Waiter waited (did nothing).");
            }
            case SEAT_CUSTOMER -> {
                waiter.seatCustomer(action.getCustomer(), action.getTable(), restaurant.getWaitingQueue());
                ui.displayWaiterAction(action.getDescription());
            }
            case TAKE_ORDER -> {
                int orderId = restaurant.allocateNextOrderId();
                Order order = waiter.takeOrder(orderId, action.getCustomer(), action.getTable(),
                        restaurant.getMenu(), restaurant.getRandom());
                restaurant.recordOrder(order);
                ui.displayWaiterAction(action.getDescription());
                ui.displayOrderTaken(order);
            }
            case SERVE_ORDER -> {
                waiter.serveOrder(action.getOrder(), turn);
                ui.displayWaiterAction(action.getDescription());
            }
        }
    }

    public List<ChefAction> buildLegalChefActions() {
        List<ChefAction> actions = new ArrayList<>();
        // Option 0 is always Wait
        actions.add(ChefAction.createWait());

        // Cook actions for IN_PROGRESS orders
        for (Table table : restaurant.getTables()) {
            if (!table.isFree()) {
                Order order = table.getCurrentOrder();
                if (order != null && order.getStatus() == OrderStatus.IN_PROGRESS) {
                    actions.add(ChefAction.createCook(order));
                }
            }
        }

        return actions;
    }

    private void executeChefAction(ChefAction action) {
        Chef chef = restaurant.getChef();
        if (action.isWait()) {
            chef.waitTurn();
            ui.displayChefAction("Chef waited (did nothing).");
        } else {
            chef.prepareOrder(action.getOrder());
            ui.displayChefAction(action.getDescription());
            ui.displayOrderCooked(action.getOrder());
        }
    }

    private void executeStep4EatingAndPayment(int turn) {
        // Sub-step 4a: Cashier collects payment from at most one already-READY_TO_PAY customer
        Cashier cashier = restaurant.getCashier();
        Order paidOrder = cashier.processOldestReadyPayment(restaurant.getTables());
        if (paidOrder != null) {
            restaurant.recordPayment(paidOrder);
            ui.displayPaymentCollected(paidOrder);
        }

        // Sub-step 4b: Customers served on an earlier turn finish eating
        List<Table> finishedTables = restaurant.processEatingCompletions(turn);
        for (Table table : finishedTables) {
            ui.displayEatingFinished(table.getCurrentCustomer(), table);
        }
    }

    private void executeStep5WaitingAndAbandonment() {
        List<Customer> abandoned = restaurant.processTurnEndDecayAndAbandonment();
        for (Customer c : abandoned) {
            ui.displayAbandonment(c, "leaving immediately");
        }
    }

    private void executeStep6EndSummary(int turn) {
        ui.displayTurnSummary(turn, restaurant);
    }
}
