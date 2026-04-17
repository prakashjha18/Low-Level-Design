package FoodOrderingSystem;

public class MenuItem {
    private final String id;
    private final String name;
    private final String description;
    private final double price;
    private boolean isAvailable;

    public MenuItem(String id, String name, String description, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isAvailable = true;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return String.format("%s - ₹%.2f (%s)", name, price, isAvailable ? "Available" : "Sold Out");
    }
}
