package restaurant.model;

import java.util.List;
import java.util.Random;
import java.util.Objects;

public class CriticCustomer extends Customer {

    public CriticCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public String getCustomerType() {
        return "Critic";
    }

    @Override
    public int getWaitingSatisfactionLoss() {
        return 12;
    }

    @Override
    public MenuItem chooseMenuItem(Menu menu, Random random) {
        Objects.requireNonNull(menu, "Menu cannot be null");
        Objects.requireNonNull(random, "Random cannot be null");
        List<MenuItem> items = menu.getItems();
        MenuItem cheapestItem = items.get(0);

        for (MenuItem item : items) {
            if (item.getPriceCents() < cheapestItem.getPriceCents()) {
                cheapestItem = item;
            }
        }

        return cheapestItem;
    }
}
