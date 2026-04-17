package FoodOrderingSystem;

public class PreparingState implements OrderState {
    @Override
    public void nextState(Order order) {
        if (order.getDeliveryExecutive() == null) {
            System.out.println("⚠️ Cannot dispatch without assigning a delivery executive first!");
            return;
        }
        order.setState(new OutForDeliveryState());
        System.out.println("🛵 Food packed and handed over to " + order.getDeliveryExecutive().getName());
    }

    @Override
    public void cancelOrder(Order order) {
        System.out.println("❌ Order cannot be cancelled now. Restaurant is already preparing the food.");
    }

    @Override
    public String getStateName() {
        return "PREPARING";
    }
}
