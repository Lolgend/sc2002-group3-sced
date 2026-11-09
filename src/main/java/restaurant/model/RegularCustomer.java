package restaurant.model;

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
