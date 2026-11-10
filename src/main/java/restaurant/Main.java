package restaurant;

import restaurant.cli.ConsoleUI;
import restaurant.controller.GameController;
import restaurant.model.Restaurant;

public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("                     RESTAURANT RUSH - STAGE 2 (BASE SETTING)");
        System.out.println("================================================================================");
        System.out.println("Welcome to Restaurant Rush! You are the Manager of a bustling 2-table restaurant.");
        System.out.println("Manage your Waiter, Chef, and Cashier over 18 turns to achieve victory:");
        System.out.println("  - Serve at least 4 paying customers");
        System.out.println("  - Earn at least $45.00 in revenue");
        System.out.println("  - Maintain at least 60.0 average customer satisfaction");
        System.out.println("================================================================================");

        Restaurant restaurant = new Restaurant();
        ConsoleUI ui = new ConsoleUI();
        GameController controller = new GameController(restaurant, ui);

        controller.runGame();
    }
}
