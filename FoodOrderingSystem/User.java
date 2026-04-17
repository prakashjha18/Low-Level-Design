package FoodOrderingSystem;

public class User implements OrderObserver {
    private final String id;
    private final String name;
    private final String phone;
    private final String address;

    public User(String id, String name, String phone, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }

    @Override
    public void update(Order order) {
        System.out.println("🔔 [NOTIFICATION to " + name + "]: Your order #" 
            + order.getId() + " status is now -> " + order.getState().getStateName());
    }
}
