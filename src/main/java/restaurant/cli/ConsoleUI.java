package restaurant.cli;

import restaurant.model.Customer;
import restaurant.model.GameResult;
import restaurant.model.Order;
import restaurant.model.Restaurant;
import restaurant.model.Table;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleUI() {
        this(System.in, System.out);
    }

    public ConsoleUI(InputStream in, PrintStream out) {
        this.scanner = new Scanner(Objects.requireNonNull(in, "InputStream cannot be null"));
        this.out = Objects.requireNonNull(out, "PrintStream cannot be null");
    }

    public static String formatMoney(int cents) {
        return String.format("$%.2f", cents / 100.0);
    }

    /**
     * Start-of-Turn Display:
     * - Turn number
     * - Revenue & metrics
     * - Waiting queue
     * - Occupied tables (customer satisfaction & order status)
     * - Available staff tasks
     */
    public void displayStartOfTurn(int turn, Restaurant restaurant) {
        out.println();
        out.println("================================================================================");
        out.printf("                              TURN %d OF %d%n", turn, restaurant.getConfig().getMaxTurns());
        out.println("================================================================================");
        out.printf("  Revenue: %-8s | Paid Customers: %-2d | Avg Satisfaction: %-4s | Abandoned: %-2d%n",
                formatMoney(restaurant.getRevenueCents()),
                restaurant.getPaidCustomersCount(),
                restaurant.getAveragePaidSatisfactionString(),
                restaurant.getAbandonedCustomersCount());
        out.println("--------------------------------------------------------------------------------");

        // Waiting Queue
        List<Customer> queue = restaurant.getWaitingQueue().getCustomers();
        out.printf("  Waiting Queue (%d customer%s):%n", queue.size(), queue.size() == 1 ? "" : "s");
        if (queue.isEmpty()) {
            out.println("    (Empty)");
        } else {
            for (Customer c : queue) {
                out.printf("    - Customer #%d [%s | Satisfaction: %d]%n",
                        c.getId(), c.getCustomerType(), c.getSatisfaction());
            }
        }

        // Tables
        out.println("  Dining Tables:");
        for (Table table : restaurant.getTables()) {
            if (table.isFree()) {
                out.printf("    - Table #%d: [FREE]%n", table.getTableNumber());
            } else {
                Customer c = table.getCurrentCustomer();
                Order o = table.getCurrentOrder();
                String orderDesc;
                if (o == null) {
                    orderDesc = "No order taken yet";
                } else {
                    orderDesc = String.format("Order #%d (%s, %d/%d units, status: %s)",
                            o.getId(), o.getMenuItem().getName(),
                            o.getCompletedPrepUnits(), o.getTotalPrepUnits(), o.getStatus());
                }
                out.printf("    - Table #%d: Customer #%d [%s | Sat: %d | State: %s] | %s%n",
                        table.getTableNumber(), c.getId(), c.getCustomerType(),
                        c.getSatisfaction(), c.getState(), orderDesc);
            }
        }

        // Staff Capacity
        out.println("  Staff Ready:");
        out.println("    - Waiter : 1 action available (Seat, Take Order, Serve, or Wait)");
        out.println("    - Chef   : 1 cooking unit available (Prepare Order, or Wait)");
        out.println("    - Cashier: Automatic (Collects payment from oldest ready customer)");
        out.println("--------------------------------------------------------------------------------");
    }

    /**
     * Prompts the manager to select from a list of legal actions.
     * Validates input: non-numeric, out-of-range, and empty inputs explain the error
     */
    public int promptMenuChoice(String promptTitle, List<String> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Action options cannot be empty");
        }

        while (true) {
            out.println();
            out.println("--- " + promptTitle + " ---");
            for (int i = 0; i < options.size(); i++) {
                out.printf("  [%d] %s%n", i, options.get(i));
            }
            out.printf("Select option (0-%d): ", options.size() - 1);

            String line;
            if (!scanner.hasNextLine()) {
                out.println("0 (EOF reached)");
                return 0;
            }
            line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                out.println("Invalid input: choice cannot be empty. Please enter a valid number.");
                continue;
            }

            try {
                int choice = Integer.parseInt(line);
                if (choice >= 0 && choice < options.size()) {
                    return choice;
                } else {
                    out.printf("Invalid choice: '%d' is out of range. Please enter a number between 0 and %d.%n",
                            choice, options.size() - 1);
                }
            } catch (NumberFormatException e) {
                out.printf("Invalid input: '%s' is not a valid number. Please enter a number between 0 and %d.%n",
                        line, options.size() - 1);
            }
        }
    }

    public void displayArrival(Customer customer, int turn) {
        out.printf(">> [Arrival] Customer #%d (%s) arrived and joined the waiting queue (Satisfaction: %d).%n",
                customer.getId(), customer.getCustomerType(), customer.getSatisfaction());
    }

    public void displayWaiterAction(String description) {
        out.printf(">> [Waiter] %s%n", description);
    }

    public void displayOrderTaken(Order order) {
        out.printf("   Customer #%d ordered %s (%s, %d prep unit%s).%n",
                order.getCustomer().getId(),
                order.getMenuItem().getName(),
                formatMoney(order.getFinalPriceCents()),
                order.getTotalPrepUnits(),
                order.getTotalPrepUnits() == 1 ? "" : "s");
    }

    public void displayChefAction(String description) {
        out.printf(">> [Chef] %s%n", description);
    }

    public void displayOrderCooked(Order order) {
        if (order.getStatus() == restaurant.model.OrderStatus.READY) {
            out.printf("   Order #%d (%s) is now READY to be served!%n",
                    order.getId(), order.getMenuItem().getName());
        } else {
            out.printf("   Order #%d (%s) cooking progress: %d/%d units.%n",
                    order.getId(), order.getMenuItem().getName(),
                    order.getCompletedPrepUnits(), order.getTotalPrepUnits());
        }
    }

    public void displayPaymentCollected(Order order) {
        out.printf(">> [Cashier] Collected %s from Customer #%d at Table #%d (Satisfaction recorded: %d). Table #%d is now free.%n",
                formatMoney(order.getFinalPriceCents()),
                order.getCustomer().getId(),
                order.getTable().getTableNumber(),
                order.getCustomer().getSatisfactionAtPayment(),
                order.getTable().getTableNumber());
    }

    public void displayEatingFinished(Customer customer, Table table) {
        out.printf(">> [Eating] Customer #%d at Table #%d finished eating and is now READY_TO_PAY.%n",
                customer.getId(), table.getTableNumber());
    }

    public void displayAbandonment(Customer customer) {
        out.printf(
                ">> [Abandonment] Customer #%d (%s) lost all satisfaction and left without paying!%n",
                customer.getId(), customer.getCustomerType());
    }

    /**
     * Renders End-of-Turn Results.
     */
    public void displayTurnSummary(int turn, Restaurant restaurant) {
        out.println();
        out.printf("--- End of Turn %d Summary ---%n", turn);
        out.printf("  Served Customers        : %d%n", restaurant.getServedCustomersCount());
        out.printf("  Paid Customers          : %d%n", restaurant.getPaidCustomersCount());
        out.printf("  Unhappy Departures      : %d%n", restaurant.getAbandonedCustomersCount());
        out.printf("  Current Revenue         : %s%n", formatMoney(restaurant.getRevenueCents()));
        out.printf("  Average Satisfaction    : %s%n", restaurant.getAveragePaidSatisfactionString());
        out.printf("  Turns Used              : %d / %d%n", turn, restaurant.getConfig().getMaxTurns());
    }

    /**
     * Renders Final Game Result from structured evaluation.
     * Prevents game rule evaluation logic from leaking into UI boundaries.
     */
    public void displayFinalResult(GameResult result) {
        out.println();
        out.println("================================================================================");
        out.println("                               GAME OVER");
        out.println("================================================================================");

        if (result.isVictory()) {
            out.println("                         *** VICTORY! ***");
            out.println("  Congratulations Manager! All restaurant objectives have been achieved!");
        } else {
            out.println("                          *** DEFEAT ***");
            out.println("  The restaurant failed to meet all performance targets.");
        }
        out.println("--------------------------------------------------------------------------------");
        out.println("Final Objectives Breakdown:");

        out.printf("  1. Paid Customers    : %d / %d required       [%s]%n",
                result.paidCustomersCount(), result.targetPaidCustomers(),
                result.paidCustomersTargetMet() ? "PASS" : "FAIL");

        out.printf("  2. Total Revenue     : %s / %s required    [%s]%n",
                formatMoney(result.revenueCents()),
                formatMoney(result.targetRevenueCents()),
                result.revenueTargetMet() ? "PASS" : "FAIL");

        out.printf("  3. Avg Satisfaction  : %s / %.1f required      [%s]%n",
                result.getAveragePaidSatisfactionString(),
                result.targetAverageSatisfaction(),
                result.satisfactionTargetMet() ? "PASS" : "FAIL");

        out.printf("  Unhappy Departures   : %d%n", result.abandonedCustomersCount());
        out.println("================================================================================");
    }
}
