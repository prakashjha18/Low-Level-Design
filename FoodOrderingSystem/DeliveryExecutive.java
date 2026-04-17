package FoodOrderingSystem;

public class DeliveryExecutive implements OrderObserver {
    private final String id;
    private final String name;
    private boolean isAvailable;

    public DeliveryExecutive(String id, String name) {
        this.id = id;
        this.name = name;
        this.isAvailable = true;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    @Override
    public void update(Order order) {
        if (order.getState().getStateName().equals("OUT_FOR_DELIVERY")) {
            System.out.println("🛵 [DELIVERY ALERT]: " + name + ", you have picked up Order #" + order.getId() + ". Proceeding to deliver.");
        }
    }
}
