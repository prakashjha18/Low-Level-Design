package FoodOrderingSystem;

public class PlacedState implements OrderState {
    @Override
    public void nextState(Order order) {
        order.setState(new PreparingState());
        System.out.println("✅ Restaurant accepted order. Food is preparing...");
    }

    @Override
    public void cancelOrder(Order order) {
        System.out.println("❌ Order cancelled successfully. Full refund initiated.");
        // Normally transitions to a CancelledState, but keeping it simple
        order.setState(new CancelledState());
    }

    @Override
    public String getStateName() {
        return "PLACED";
    }
}
