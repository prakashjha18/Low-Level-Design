package FoodOrderingSystem;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private final String orderId;
    private final String customerName;
    private final Restaurant restaurant;
    private final Map<MenuItem, Integer> items;
    private double totalAmount;
    private OrderStatus status;

    public Order(String orderId, String customerName, Restaurant restaurant) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.restaurant = restaurant;
        this.items = new HashMap<>();
        this.status = OrderStatus.PLACED;
    }

    public void addItem(MenuItem item, int quantity) {
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public void calculateTotal() {
        this.totalAmount = 0;
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            totalAmount += entry.getKey().getPrice() * entry.getValue();
        }
    }

    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public OrderStatus getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
