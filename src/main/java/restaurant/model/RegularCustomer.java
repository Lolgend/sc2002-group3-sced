package restaurant.model;

/**
 * Standard customer in Stage 1 with baseline patience (losing 8 points per waiting turn)
 * and standard 100% bill calculation.
 */
public class RegularCustomer extends Customer {

    public RegularCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public String getCustomerType() {
        return "Regular";
    }

    @Override
    public int getWaitingSatisfactionLoss() {
        return 8;
    }
}
