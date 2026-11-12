package restaurant.model;

public class HappyHour {
    private static final int DURATION_TURNS = 2;
    private static final double DISCOUNT_MULTIPLIER = 0.8;
    private static final int SATISFACTION_RECOVERY = 15;

    private boolean used;
    private int remainingTurns;

    public boolean isAvailable() {
        return !used;
    }

    public boolean isActive() {
        return remainingTurns > 0;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }

    public int getSatisfactionRecovery() {
        return SATISFACTION_RECOVERY;
    }

    public void activate() {
        if (used) {
            throw new IllegalStateException("Happy Hour can only be activated once per game.");
        }
        used = true;
        remainingTurns = DURATION_TURNS;
    }

    public void endTurn() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    public int applyDiscount(int priceCents) {
        if (!isActive()) {
            return priceCents;
        }
        return (int) Math.round(priceCents * DISCOUNT_MULTIPLIER);
    }
}
