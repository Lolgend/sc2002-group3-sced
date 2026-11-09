package restaurant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Menu {
    private final List<MenuItem> items;

    public Menu(List<? extends MenuItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Menu must contain at least one item");
        }
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }

    public List<MenuItem> getItems() {
        return items;
    }

    /**
     * - Salad: $9.00 (900 cents), 1 prep unit
     * - Burger: $15.00 (1500 cents), 1 prep unit
     * - Pasta: $18.00 (1800 cents), 2 prep units
     */
    public static Menu createBaseMenu() {
        List<MenuItem> baseItems = new ArrayList<>();
        baseItems.add(new Dish("Salad", 900, 1));
        baseItems.add(new Dish("Burger", 1500, 1));
        baseItems.add(new Dish("Pasta", 1800, 2));
        return new Menu(baseItems);
    }
}
