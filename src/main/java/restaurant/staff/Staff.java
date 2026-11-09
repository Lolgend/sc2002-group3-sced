package restaurant.staff;

import java.util.Objects;

/**
 * Abstract base class for restaurant staff members.
 * Tracks staff identity and single-action-per-turn capacity constraints.
 */
public abstract class Staff {
    private final String name;
    private boolean actedThisTurn;

    protected Staff(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Staff name cannot be null or empty");
        }
        this.name = name;
        this.actedThisTurn = false;
    }

    public String getName() {
        return name;
    }

    public boolean hasActedThisTurn() {
        return actedThisTurn;
    }

    public void resetTurnState() {
        this.actedThisTurn = false;
    }

    protected void markActed() {
        if (this.actedThisTurn) {
            throw new IllegalStateException(name + " has already acted on this turn.");
        }
        this.actedThisTurn = true;
    }
}
