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

    public static Menu createBaseMenu() {
        List<MenuItem> baseItems = new ArrayList<>();
        Dish salad = new Dish("Salad", 900, 1);
        Dish burger = new Dish("Burger", 1500, 1);
        Dish pasta = new Dish("Pasta", 1800, 2);
        baseItems.add(salad);
        baseItems.add(burger);
        baseItems.add(pasta);
        baseItems.add(new ComboMeal(salad, burger));
        baseItems.add(new ComboMeal(salad, pasta));
        baseItems.add(new ComboMeal(burger, pasta));
        return new Menu(baseItems);
    }
}
