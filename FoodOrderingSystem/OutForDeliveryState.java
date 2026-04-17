package FoodOrderingSystem;

public class OutForDeliveryState implements OrderState {
    @Override
    public void nextState(Order order) {
        order.setState(new DeliveredState());
        System.out.println("🎉 Order delivered successfully to " + order.getUser().getName());
    }

    @Override
    public void cancelOrder(Order order) {
        System.out.println("❌ Order cannot be cancelled. Already out for delivery.");
    }

    @Override
    public String getStateName() {
        return "OUT_FOR_DELIVERY";
    }
}
