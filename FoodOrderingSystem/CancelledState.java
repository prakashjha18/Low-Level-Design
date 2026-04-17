package FoodOrderingSystem;

public class CancelledState implements OrderState {
    @Override
    public void nextState(Order order) {
        System.out.println("ℹ️ Order is cancelled. No further state transitions.");
    }

    @Override
    public void cancelOrder(Order order) {
        System.out.println("ℹ️ Order is already cancelled.");
    }

    @Override
    public String getStateName() {
        return "CANCELLED";
    }
}
