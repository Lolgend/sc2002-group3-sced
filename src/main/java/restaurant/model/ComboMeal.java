package restaurant.model;

public class ComboMeal implements MenuItem {
    private final MenuItem firstItem;
    private final MenuItem secondItem;

    public ComboMeal(MenuItem firstItem, MenuItem secondItem) {
        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    public MenuItem getFirstItem() {
        return firstItem;
    }

    public MenuItem getSecondItem() {
        return secondItem;
    }

    @Override
    public String getName() {
        return firstItem.getName() + " + " + secondItem.getName() + " Combo";
    }

    @Override
    public int getPriceCents() {
        return (int) Math.round((firstItem.getPriceCents() + secondItem.getPriceCents()) * 0.9);
    }

    @Override
    public int getPrepUnits() {
        return firstItem.getPrepUnits() + secondItem.getPrepUnits();
    }
}
