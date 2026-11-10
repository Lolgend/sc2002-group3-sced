package restaurant.model;

import java.util.Arrays;

public class GameConfig {
    public static final int DEFAULT_MAX_TURNS = 18;
    public static final int DEFAULT_NUM_TABLES = 2;
    private static final int[] DEFAULT_ARRIVAL_TURNS = {1, 4, 7, 10, 13, 16};
    public static final int DEFAULT_TARGET_PAID_CUSTOMERS = 4;
    public static final int DEFAULT_TARGET_REVENUE_CENTS = 4500; // $45.00
    public static final double DEFAULT_TARGET_AVG_SATISFACTION = 60.0;
    public static final long DEFAULT_SEED = 42L;

    private final int maxTurns;
    private final int numTables;
    private final int[] arrivalTurns;
    private final int targetPaidCustomers;
    private final int targetRevenueCents;
    private final double targetAvgSatisfaction;
    private final long seed;

    public GameConfig() {
        this(DEFAULT_MAX_TURNS, DEFAULT_NUM_TABLES, DEFAULT_ARRIVAL_TURNS,
                DEFAULT_TARGET_PAID_CUSTOMERS, DEFAULT_TARGET_REVENUE_CENTS,
                DEFAULT_TARGET_AVG_SATISFACTION, DEFAULT_SEED);
    }

    public GameConfig(long seed) {
        this(DEFAULT_MAX_TURNS, DEFAULT_NUM_TABLES, DEFAULT_ARRIVAL_TURNS,
                DEFAULT_TARGET_PAID_CUSTOMERS, DEFAULT_TARGET_REVENUE_CENTS,
                DEFAULT_TARGET_AVG_SATISFACTION, seed);
    }

    public GameConfig(int maxTurns, int numTables, int[] arrivalTurns,
                      int targetPaidCustomers, int targetRevenueCents,
                      double targetAvgSatisfaction, long seed) {
        if (maxTurns <= 0) {
            throw new IllegalArgumentException("Max turns must be positive: " + maxTurns);
        }
        if (numTables <= 0) {
            throw new IllegalArgumentException("Number of tables must be positive: " + numTables);
        }
        if (arrivalTurns == null || arrivalTurns.length == 0) {
            throw new IllegalArgumentException("Arrival turns cannot be null or empty");
        }
        this.maxTurns = maxTurns;
        this.numTables = numTables;
        this.arrivalTurns = Arrays.copyOf(arrivalTurns, arrivalTurns.length);
        this.targetPaidCustomers = targetPaidCustomers;
        this.targetRevenueCents = targetRevenueCents;
        this.targetAvgSatisfaction = targetAvgSatisfaction;
        this.seed = seed;
    }

    public static GameConfig createBaseConfig() {
        return new GameConfig();
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public int getNumTables() {
        return numTables;
    }

    public int[] getArrivalTurns() {
        return Arrays.copyOf(arrivalTurns, arrivalTurns.length);
    }

    public int getTargetPaidCustomers() {
        return targetPaidCustomers;
    }

    public int getTargetRevenueCents() {
        return targetRevenueCents;
    }

    public double getTargetAvgSatisfaction() {
        return targetAvgSatisfaction;
    }

    public long getSeed() {
        return seed;
    }

    public boolean isArrivalTurn(int turn) {
        for (int arrivalTurn : arrivalTurns) {
            if (arrivalTurn == turn) {
                return true;
            }
        }
        return false;
    }

    public boolean isPaidTargetMet(int paidCount) {
        return paidCount >= targetPaidCustomers;
    }

    public boolean isRevenueTargetMet(int revenueCents) {
        return revenueCents >= targetRevenueCents;
    }

    public boolean isSatisfactionTargetMet(double avgSatisfaction) {
        return !Double.isNaN(avgSatisfaction) && avgSatisfaction >= targetAvgSatisfaction;
    }

    public boolean isVictory(int paidCount, int revenueCents, double avgSatisfaction) {
        return isPaidTargetMet(paidCount)
                && isRevenueTargetMet(revenueCents)
                && isSatisfactionTargetMet(avgSatisfaction);
    }
}
