package restaurant.model;

public class VIPCustomer extends Customer {

    public VIPCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public String getCustomerType() {
        return "VIP";
    }

    @Override
    public int getWaitingSatisfactionLoss() {
        return 5;
    }

    @Override
    public int calculateBillCents(MenuItem item) {
        return (int) Math.round(super.calculateBillCents(item) * 0.9);
    }
}