package FoodOrderingSystem;

public class Restaurant {
    private final String id;
    private final String name;
    private final String location;
    private final Menu menu;
    private boolean isAcceptingOrders;

    public Restaurant(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.menu = new Menu();
        this.isAcceptingOrders = true;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public Menu getMenu() { return menu; }
    public boolean isAcceptingOrders() { return isAcceptingOrders; }

    public void setAcceptingOrders(boolean acceptingOrders) {
        this.isAcceptingOrders = acceptingOrders;
    }
}
