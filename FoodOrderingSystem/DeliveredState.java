package FoodOrderingSystem;

public class DeliveredState implements OrderState {
    @Override
    public void nextState(Order order) {
        System.out.println("ℹ️ Order is already delivered. No further state transitions.");
    }

    @Override
    public void cancelOrder(Order order) {
        System.out.println("❌ Cannot cancel. Order is already delivered.");
    }

    @Override
    public String getStateName() {
        return "DELIVERED";
    }
}
