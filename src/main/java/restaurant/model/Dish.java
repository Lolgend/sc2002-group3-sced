package restaurant.model;

import java.util.Objects;

public class Dish implements MenuItem {
    private final String name;
    private final int priceCents;
    private final int prepUnits;

    public Dish(String name, int priceCents, int prepUnits) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Dish name cannot be null or empty");
        }
        if (priceCents < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + priceCents);
        }
        if (prepUnits < 1) {
            throw new IllegalArgumentException("Prep units must be at least 1: " + prepUnits);
        }
        this.name = name;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPriceCents() {
        return priceCents;
    }

    @Override
    public int getPrepUnits() {
        return prepUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dish dish)) return false;
        return priceCents == dish.priceCents &&
                prepUnits == dish.prepUnits &&
                Objects.equals(name, dish.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, priceCents, prepUnits);
    }

    @Override
    public String toString() {
        return String.format("%s ($%.2f, %d prep unit%s)",
                name, priceCents / 100.0, prepUnits, prepUnits == 1 ? "" : "s");
    }
}
