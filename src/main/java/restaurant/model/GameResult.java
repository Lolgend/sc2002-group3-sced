package restaurant.model;

public record GameResult(
        boolean isVictory,
        int paidCustomersCount,
        int targetPaidCustomers,
        boolean paidCustomersTargetMet,
        int revenueCents,
        int targetRevenueCents,
        boolean revenueTargetMet,
        double averagePaidSatisfaction,
        double targetAverageSatisfaction,
        boolean satisfactionTargetMet,
        int abandonedCustomersCount
) {
    public String getAveragePaidSatisfactionString() {
        if (Double.isNaN(averagePaidSatisfaction)) {
            return "N/A";
        }
        return String.format("%.1f", averagePaidSatisfaction);
    }
}
