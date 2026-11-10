package restaurant.model;

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
}
