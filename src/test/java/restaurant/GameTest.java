package restaurant;

import restaurant.cli.ConsoleUI;
import restaurant.controller.ChefAction;
import restaurant.controller.GameController;
import restaurant.controller.WaiterAction;
import restaurant.controller.WaiterActionType;
import restaurant.model.Customer;
import restaurant.model.CustomerState;
import restaurant.model.Dish;
import restaurant.model.Menu;
import restaurant.model.MenuItem;
import restaurant.model.Order;
import restaurant.model.OrderStatus;
import restaurant.model.RegularCustomer;
import restaurant.model.Restaurant;
import restaurant.model.Table;
import restaurant.staff.Cashier;
import restaurant.staff.Chef;
import restaurant.staff.Waiter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Self-contained offline test suite for Restaurant Rush Stage 1.
 * Requires pure JDK 17+ with zero external dependencies.
 * Run with: java -ea -cp out restaurant.GameTest
 */
public class GameTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("Running Restaurant Rush Stage 1 Test Suite...");

        runTest("testBaseMenu", GameTest::testBaseMenu);
        runTest("testCustomerDecayAndBilling", GameTest::testCustomerDecayAndBilling);
        runTest("testOrderStateTransitions", GameTest::testOrderStateTransitions);
        runTest("testSection33SingleCustomerWalkthrough", GameTest::testSection33SingleCustomerWalkthrough);
        runTest("testTwoPhaseStep4CashierTiming", GameTest::testTwoPhaseStep4CashierTiming);
        runTest("testAbandonmentAndOrderCancellation", GameTest::testAbandonmentAndOrderCancellation);
        runTest("testInputValidationResilience", GameTest::testInputValidationResilience);
        runTest("testDeterministicDefeatScenario", GameTest::testDeterministicDefeatScenario);
        runTest("testDeterministicVictoryScenario", GameTest::testDeterministicVictoryScenario);

        System.out.printf("%nTest Results: %d/%d tests passed.%n", testsPassed, testsRun);
        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED SUCCESSFULLY!");
        }
    }

    private static void runTest(String name, Runnable test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.printf("  [PASS] %s%n", name);
        } catch (Throwable t) {
            System.err.printf("  [FAIL] %s: %s%n", name, t.getMessage());
            t.printStackTrace(System.err);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(String.format("Assertion failed: %s (expected <%s>, got <%s>)",
                    message, expected, actual));
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(String.format("Assertion failed: %s (expected <%d>, got <%d>)",
                    message, expected, actual));
        }
    }

    /**
     * Verifies that the base menu contains Salad, Burger, and Pasta with exact prices and prep units.
     */
    public static void testBaseMenu() {
        Menu menu = Menu.createBaseMenu();
        List<MenuItem> items = menu.getItems();
        assertEquals(3, items.size(), "Base menu must contain exactly 3 items");

        MenuItem salad = items.get(0);
        assertEquals("Salad", salad.getName(), "Item 1 name");
        assertEquals(900, salad.getPriceCents(), "Salad price");
        assertEquals(1, salad.getPrepUnits(), "Salad prep units");

        MenuItem burger = items.get(1);
        assertEquals("Burger", burger.getName(), "Item 2 name");
        assertEquals(1500, burger.getPriceCents(), "Burger price");
        assertEquals(1, burger.getPrepUnits(), "Burger prep units");

        MenuItem pasta = items.get(2);
        assertEquals("Pasta", pasta.getName(), "Item 3 name");
        assertEquals(1800, pasta.getPriceCents(), "Pasta price");
        assertEquals(2, pasta.getPrepUnits(), "Pasta prep units");
    }

    /**
     * Verifies RegularCustomer loss of 8 per unserved turn and full price bill calculation.
     */
    public static void testCustomerDecayAndBilling() {
        Customer customer = new RegularCustomer(1, 1);
        assertEquals("Regular", customer.getCustomerType(), "Customer type");
        assertEquals(8, customer.getWaitingSatisfactionLoss(), "Loss rate");
        assertEquals(100, customer.getSatisfaction(), "Initial satisfaction");
        assertEquals(CustomerState.WAITING_IN_QUEUE, customer.getState(), "Initial state");
        assertEquals(1500, customer.calculateBillCents(1500), "Regular customer pays 100%");

        boolean abandoned = customer.applyTurnEndSatisfactionDecay();
        assertTrue(!abandoned, "Should not abandon on first decay");
        assertEquals(92, customer.getSatisfaction(), "Satisfaction after 1 decay");
    }

    /**
     * Verifies strict order state transitions and invalid state exceptions.
     */
    public static void testOrderStateTransitions() {
        Customer customer = new RegularCustomer(1, 1);
        Table table = new Table(1);
        table.seatCustomer(customer);
        Dish pasta = new Dish("Pasta", 1800, 2);
        Order order = new Order(1, customer, table, pasta, 1800);
        table.assignOrder(order);

        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus(), "Initial order status");
        assertEquals(0, order.getCompletedPrepUnits(), "Initial prep units");

        // Advance 1 unit
        order.advancePreparation();
        assertEquals(1, order.getCompletedPrepUnits(), "Prep units after 1 cook");
        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus(), "Status after 1 cook");

        // Advance 2nd unit -> READY
        order.advancePreparation();
        assertEquals(2, order.getCompletedPrepUnits(), "Prep units after 2 cooks");
        assertEquals(OrderStatus.READY, order.getStatus(), "Status after completing all units");

        // Cannot advance when already READY
        boolean caughtAdvance = false;
        try {
            order.advancePreparation();
        } catch (IllegalStateException e) {
            caughtAdvance = true;
        }
        assertTrue(caughtAdvance, "Advancing READY order must throw IllegalStateException");

        // Serve order
        order.markServed();
        assertEquals(OrderStatus.SERVED, order.getStatus(), "Status after serving");

        // Pay order
        order.markPaid();
        assertEquals(OrderStatus.PAID, order.getStatus(), "Status after paying");
    }

    /**
     * Verifies the exact Section 3.3 Example: One customer from arrival to payment.
     * Turn 1: Arrives (100). Waiter seats. Turn end: sat = 92.
     * Turn 2: Waiter takes Pasta order. Chef does unit 1/2. Turn end: sat = 84.
     * Turn 3: Chef does unit 2/2 -> READY. Turn end: sat = 76.
     * Turn 4: Waiter serves -> SERVED. Turn end: sat = 76.
     * Turn 5: Customer finishes eating -> READY_TO_PAY. Turn end: sat = 76.
     * Turn 6: Cashier collects $18.00 -> PAID, table freed, sat 76 recorded.
     */
    public static void testSection33SingleCustomerWalkthrough() {
        Restaurant restaurant = new Restaurant(42L);
        Waiter waiter = restaurant.getWaiter();
        Chef chef = restaurant.getChef();
        Cashier cashier = restaurant.getCashier();
        Table table = restaurant.getTables().get(0);

        // Turn 1
        waiter.resetTurnState();
        chef.resetTurnState();
        cashier.resetTurnState();
        Customer customer = restaurant.processArrival(1);
        assertEquals(100, customer.getSatisfaction(), "Turn 1 arrival sat");
        waiter.seatCustomer(customer, table, restaurant.getWaitingQueue());
        restaurant.processTurnEndDecayAndAbandonment();
        assertEquals(92, customer.getSatisfaction(), "Turn 1 end sat");
        assertEquals(CustomerState.SEATED, customer.getState(), "Turn 1 end state");

        // Turn 2
        waiter.resetTurnState();
        chef.resetTurnState();
        cashier.resetTurnState();
        // Force customer to order Pasta for the walkthrough
        Dish pasta = new Dish("Pasta", 1800, 2);
        Order order = new Order(1, customer, table, pasta, 1800);
        table.assignOrder(order);
        restaurant.recordOrder(order);
        chef.prepareOrder(order);
        restaurant.processTurnEndDecayAndAbandonment();
        assertEquals(84, customer.getSatisfaction(), "Turn 2 end sat");
        assertEquals(1, order.getCompletedPrepUnits(), "Turn 2 completed units");
        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus(), "Turn 2 order status");

        // Turn 3
        waiter.resetTurnState();
        chef.resetTurnState();
        cashier.resetTurnState();
        chef.prepareOrder(order);
        assertEquals(OrderStatus.READY, order.getStatus(), "Turn 3 ready status");
        restaurant.processTurnEndDecayAndAbandonment();
        assertEquals(76, customer.getSatisfaction(), "Turn 3 end sat");
        assertEquals(CustomerState.WAITING_FOR_FOOD, customer.getState(), "Turn 3 end state");

        // Turn 4: Waiter serves
        waiter.resetTurnState();
        chef.resetTurnState();
        cashier.resetTurnState();
        waiter.serveOrder(order, 4);
        assertEquals(CustomerState.EATING, customer.getState(), "Turn 4 served state");
        // Eating check (served on turn 4, so servedTurn < 4 is false -> does NOT transition to ready to pay yet)
        assertTrue(customer.getServedTurn() >= 4, "Customer served this turn");
        restaurant.processTurnEndDecayAndAbandonment();
        assertEquals(76, customer.getSatisfaction(), "Turn 4 end sat (no decay when eating)");

        // Turn 5: Customer finishes eating
        waiter.resetTurnState();
        chef.resetTurnState();
        cashier.resetTurnState();
        // Cashier acts before eating completion: Cashier finds no READY_TO_PAY
        Order paidTurn5 = cashier.processOldestReadyPayment(restaurant.getTables());
        assertTrue(paidTurn5 == null, "Cashier cannot collect on Turn 5 because customer is not READY_TO_PAY yet");
        // Eating completion: servedTurn (4) < currentTurn (5) -> becomes READY_TO_PAY
        customer.markReadyToPay(restaurant.allocateNextReadyToPaySequence());
        assertEquals(CustomerState.READY_TO_PAY, customer.getState(), "Turn 5 state is READY_TO_PAY");
        restaurant.processTurnEndDecayAndAbandonment();
        assertEquals(76, customer.getSatisfaction(), "Turn 5 end sat");

        // Turn 6: Cashier collects $18.00
        waiter.resetTurnState();
        chef.resetTurnState();
        cashier.resetTurnState();
        Order paidTurn6 = cashier.processOldestReadyPayment(restaurant.getTables());
        assertTrue(paidTurn6 != null, "Cashier collects payment on Turn 6");
        restaurant.recordPayment(paidTurn6);
        assertEquals(1800, restaurant.getRevenueCents(), "Revenue collected");
        assertEquals(1, restaurant.getPaidCustomersCount(), "Paid count");
        assertEquals(76, restaurant.getTotalPaidSatisfaction(), "Recorded satisfaction");
        assertTrue(table.isFree(), "Table freed on payment");
    }

    /**
     * Verifies ADR-0002 Two-Phase Step 4 Cashier Timing:
     * A customer finishing eating on Turn T cannot pay on Turn T; pays on Turn T+1 at earliest.
     */
    public static void testTwoPhaseStep4CashierTiming() {
        Restaurant restaurant = new Restaurant(42L);
        ConsoleUI dummyUi = new ConsoleUI(new ByteArrayInputStream("0\n0\n0\n0\n".getBytes()), new PrintStream(new ByteArrayOutputStream()));
        GameController controller = new GameController(restaurant, dummyUi);

        Table table = restaurant.getTables().get(0);
        Customer customer = new RegularCustomer(1, 1);
        table.seatCustomer(customer);
        Dish salad = new Dish("Salad", 900, 1);
        Order order = new Order(1, customer, table, salad, 900);
        table.assignOrder(order);
        restaurant.recordOrder(order);
        order.advancePreparation(); // READY

        // Serve on Turn 2
        restaurant.getWaiter().serveOrder(order, 2);
        assertEquals(CustomerState.EATING, customer.getState(), "Customer eating on Turn 2");

        // On Turn 2, Step 4 Cashier does nothing, eating does not complete because servedTurn (2) < 2 is false.
        // On Turn 3: Step 4a Cashier processes oldest READY_TO_PAY (none yet).
        // Then Step 4b checks servedTurn (2) < 3 -> transitions to READY_TO_PAY!
        // Cashier already acted on Turn 3, so customer cannot pay on Turn 3.
        // Customer pays on Turn 4!
        // Let's verify Cashier returns null on Turn 3:
        restaurant.getCashier().resetTurnState();
        Order paidTurn3 = restaurant.getCashier().processOldestReadyPayment(restaurant.getTables());
        assertTrue(paidTurn3 == null, "Cashier must not collect on Turn 3");

        // Eating completion on Turn 3:
        customer.markReadyToPay(restaurant.allocateNextReadyToPaySequence());
        assertEquals(CustomerState.READY_TO_PAY, customer.getState(), "Now READY_TO_PAY");

        // On Turn 4, Cashier collects:
        restaurant.getCashier().resetTurnState();
        Order paidTurn4 = restaurant.getCashier().processOldestReadyPayment(restaurant.getTables());
        assertTrue(paidTurn4 != null, "Cashier collects on Turn 4");
        assertEquals(900, paidTurn4.getFinalPriceCents(), "Paid 900 cents");
    }

    /**
     * Verifies that when satisfaction decays to 0, customer leaves, table is freed,
     * order is cancelled, and revenue is not credited.
     */
    public static void testAbandonmentAndOrderCancellation() {
        Restaurant restaurant = new Restaurant(42L);
        Table table = restaurant.getTables().get(0);
        Customer customer = new RegularCustomer(1, 1);
        table.seatCustomer(customer);
        Dish pasta = new Dish("Pasta", 1800, 2);
        Order order = new Order(1, customer, table, pasta, 1800);
        table.assignOrder(order);
        restaurant.recordOrder(order);

        // Decay customer satisfaction from 100 to 0 (13 decays of 8 points = 104 -> clamped to 0)
        for (int i = 0; i < 13; i++) {
            restaurant.processTurnEndDecayAndAbandonment();
        }

        assertEquals(0, customer.getSatisfaction(), "Customer satisfaction reached 0");
        assertEquals(CustomerState.ABANDONED, customer.getState(), "Customer state abandoned");
        assertTrue(table.isFree(), "Table must be freed upon abandonment");
        assertEquals(OrderStatus.CANCELLED, order.getStatus(), "Order must be cancelled");
        assertEquals(1, restaurant.getAbandonedCustomersCount(), "Abandoned count incremented");
        assertEquals(0, restaurant.getRevenueCents(), "Abandoned order must not contribute to revenue");
    }

    /**
     * Verifies that invalid inputs (empty, non-numeric, out-of-bounds) explain the error
     * and do not consume turns.
     */
    public static void testInputValidationResilience() {
        String simulatedInput = "\nabc\n99\n-1\n1\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        ConsoleUI ui = new ConsoleUI(in, out);
        List<String> options = List.of("Option Zero", "Option One", "Option Two");
        int choice = ui.promptMenuChoice("Test Prompt", options);

        assertEquals(1, choice, "Should successfully return 1 after rejecting invalid inputs");
        String output = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Invalid input: choice cannot be empty"), "Handled empty input");
        assertTrue(output.contains("is not a valid number"), "Handled non-numeric input");
        assertTrue(output.contains("is out of range"), "Handled out-of-range input");
    }

    /**
     * Verifies reproducible Defeat scenario:
     * Manager waits / ignores all tasks across all 18 turns.
     * All 6 customers abandon, 0 paid customers, $0 revenue -> Defeat.
     */
    public static void testDeterministicDefeatScenario() {
        // Player enters 0 (Wait) for every Waiter and Chef prompt across 18 turns
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 36; i++) {
            sb.append("0\n");
        }
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        Restaurant restaurant = new Restaurant(Restaurant.DEFAULT_SEED);
        ConsoleUI ui = new ConsoleUI(in, out);
        GameController controller = new GameController(restaurant, ui);

        controller.runGame();

        assertEquals(0, restaurant.getPaidCustomersCount(), "0 paid customers");
        assertEquals(0, restaurant.getRevenueCents(), "0 revenue");
        assertTrue(!restaurant.isVictory(), "Outcome must be DEFEAT");
        String consoleOutput = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(consoleOutput.contains("DEFEAT"), "Display shows DEFEAT");
    }

    /**
     * Verifies reproducible Victory scenario under DEFAULT_SEED = 42L.
     * Under seed 42, customers arriving on turns 1, 4, 7, 10, 13, 16 order dishes.
     * With an active manager priority strategy, at least 4 customers pay, revenue >= $45.00,
     * and average satisfaction >= 60.0.
     */
    public static void testDeterministicVictoryScenario() {
        // Let's create a game controller with scripted choices for a victory run
        // We will execute turn-by-turn with targeted choices:
        Restaurant restaurant = new Restaurant(Restaurant.DEFAULT_SEED);
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        // We can test the game controller using a dynamic input provider
        // Let's trace a winning strategy:
        // Priority:
        // 1. Serve any READY order
        // 2. Take order from any SEATED customer
        // 3. Seat any queued customer if table free
        // Chef: Cook any active order
        List<String> inputScript = generateWinningInputScript(Restaurant.DEFAULT_SEED);

        String fullInput = String.join("\n", inputScript) + "\n";
        InputStream in = new ByteArrayInputStream(fullInput.getBytes(StandardCharsets.UTF_8));
        ConsoleUI ui = new ConsoleUI(in, out);
        GameController controller = new GameController(restaurant, ui);

        controller.runGame();

        System.out.printf("    [Victory Run Metrics] Paid: %d, Revenue: $%.2f, AvgSat: %s, Abandoned: %d%n",
                restaurant.getPaidCustomersCount(),
                restaurant.getRevenueCents() / 100.0,
                restaurant.getAveragePaidSatisfactionString(),
                restaurant.getAbandonedCustomersCount());

        assertTrue(restaurant.getPaidCustomersCount() >= Restaurant.TARGET_PAID_CUSTOMERS,
                "Paid customers must be >= " + Restaurant.TARGET_PAID_CUSTOMERS);
        assertTrue(restaurant.getRevenueCents() >= Restaurant.TARGET_REVENUE_CENTS,
                "Revenue must be >= " + Restaurant.TARGET_REVENUE_CENTS);
        assertTrue(restaurant.getAveragePaidSatisfaction() >= Restaurant.TARGET_AVG_SATISFACTION,
                "Average satisfaction must be >= " + Restaurant.TARGET_AVG_SATISFACTION);
        assertTrue(restaurant.isVictory(), "Outcome must be VICTORY");
    }

    /**
     * Generates a deterministic sequence of manager choices that prioritizes serving,
     * order taking, seating, and cooking to achieve Victory under DEFAULT_SEED.
     */
    public static List<String> generateWinningInputScript(long seed) {
        Restaurant sim = new Restaurant(seed);
        List<String> script = new ArrayList<>();

        for (int turn = 1; turn <= Restaurant.MAX_TURNS; turn++) {
            sim.getWaiter().resetTurnState();
            sim.getChef().resetTurnState();
            sim.getCashier().resetTurnState();

            // Step 1: Arrival
            sim.processArrival(turn);

            // Step 2 & 3: Waiter action
            // Strategy:
            // Prefer SERVE_ORDER (highest priority to free tables and start eating)
            // Then TAKE_ORDER
            // Then SEAT_CUSTOMER
            // Else WAIT (0)
            List<WaiterAction> wActions = new ArrayList<>();
            wActions.add(WaiterAction.createWait());

            Table freeTable = null;
            for (Table t : sim.getTables()) {
                if (t.isFree()) {
                    freeTable = t;
                    break;
                }
            }
            if (freeTable != null) {
                for (Customer c : sim.getWaitingQueue().getCustomers()) {
                    wActions.add(WaiterAction.createSeat(c, freeTable));
                }
            }
            for (Table t : sim.getTables()) {
                if (!t.isFree() && t.getCurrentCustomer().getState() == CustomerState.SEATED) {
                    wActions.add(WaiterAction.createTakeOrder(t.getCurrentCustomer(), t));
                }
            }
            for (Table t : sim.getTables()) {
                if (!t.isFree() && t.getCurrentOrder() != null && t.getCurrentOrder().getStatus() == OrderStatus.READY) {
                    wActions.add(WaiterAction.createServeOrder(t.getCurrentOrder()));
                }
            }

            int chosenWaiterIdx = 0;
            // Search for SERVE
            for (int i = 0; i < wActions.size(); i++) {
                if (wActions.get(i).getType() == WaiterActionType.SERVE_ORDER) {
                    chosenWaiterIdx = i;
                    break;
                }
            }
            if (chosenWaiterIdx == 0) {
                // Search for TAKE_ORDER
                for (int i = 0; i < wActions.size(); i++) {
                    if (wActions.get(i).getType() == WaiterActionType.TAKE_ORDER) {
                        chosenWaiterIdx = i;
                        break;
                    }
                }
            }
            if (chosenWaiterIdx == 0) {
                // Search for SEAT
                for (int i = 0; i < wActions.size(); i++) {
                    if (wActions.get(i).getType() == WaiterActionType.SEAT_CUSTOMER) {
                        chosenWaiterIdx = i;
                        break;
                    }
                }
            }

            script.add(String.valueOf(chosenWaiterIdx));
            WaiterAction chosenW = wActions.get(chosenWaiterIdx);
            switch (chosenW.getType()) {
                case WAIT -> sim.getWaiter().waitTurn();
                case SEAT_CUSTOMER -> sim.getWaiter().seatCustomer(chosenW.getCustomer(), chosenW.getTable(), sim.getWaitingQueue());
                case TAKE_ORDER -> {
                    int oid = sim.allocateNextOrderId();
                    Order o = sim.getWaiter().takeOrder(oid, chosenW.getCustomer(), chosenW.getTable(), sim.getMenu(), sim.getRandom());
                    sim.recordOrder(o);
                }
                case SERVE_ORDER -> sim.getWaiter().serveOrder(chosenW.getOrder(), turn);
            }

            // Chef action:
            // Strategy: Cook oldest active IN_PROGRESS order
            List<ChefAction> cActions = new ArrayList<>();
            cActions.add(ChefAction.createWait());
            for (Table t : sim.getTables()) {
                if (!t.isFree() && t.getCurrentOrder() != null && t.getCurrentOrder().getStatus() == OrderStatus.IN_PROGRESS) {
                    cActions.add(ChefAction.createCook(t.getCurrentOrder()));
                }
            }

            int chosenChefIdx = 0;
            if (cActions.size() > 1) {
                chosenChefIdx = 1; // pick first available cooking task
            }
            script.add(String.valueOf(chosenChefIdx));
            ChefAction chosenC = cActions.get(chosenChefIdx);
            if (chosenC.isWait()) {
                sim.getChef().waitTurn();
            } else {
                sim.getChef().prepareOrder(chosenC.getOrder());
            }

            // Step 4: Eating and payment
            Order paid = sim.getCashier().processOldestReadyPayment(sim.getTables());
            if (paid != null) {
                sim.recordPayment(paid);
            }
            for (Table t : sim.getTables()) {
                if (!t.isFree()) {
                    Customer c = t.getCurrentCustomer();
                    if (c.getState() == CustomerState.EATING && c.getServedTurn() < turn) {
                        c.markReadyToPay(sim.allocateNextReadyToPaySequence());
                    }
                }
            }

            // Step 5: Decay
            sim.processTurnEndDecayAndAbandonment();
        }

        return script;
    }
}
