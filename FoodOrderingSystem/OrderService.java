package FoodOrderingSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller/Service Class.
 * Centralizes exactly how orders are placed and prevents invalid state transitions.
 */
public class OrderService {
    private static OrderService instance;
    private final Map<String, Order> orderDatabase;

    private OrderService() {
        orderDatabase = new HashMap<>();
    }

    public static synchronized OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    public Order placeOrder(String customerName, Restaurant restaurant, Map<MenuItem, Integer> requestedItems) {
        if (requestedItems == null || requestedItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place an empty order.");
        }

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Order newOrder = new Order(orderId, customerName, restaurant);

        for (Map.Entry<MenuItem, Integer> entry : requestedItems.entrySet()) {
            newOrder.addItem(entry.getKey(), entry.getValue());
        }

        newOrder.calculateTotal();
        orderDatabase.put(orderId, newOrder);

        System.out.println("✅ Order Placed Successfully! ID: " + orderId + " | Total: ₹" + newOrder.getTotalAmount());
        notifyCustomer(newOrder);
        return newOrder;
    }

    /**
     * Updates order status heavily enforcing state machine rules
     * so it cannot jump from PLACED directly to DELIVERED.
     */
    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = orderDatabase.get(orderId);
        if (order == null) throw new IllegalArgumentException("Order not found.");

        OrderStatus currentStatus = order.getStatus();

        // Enforce State Transitions (The Crux of the logic)
        if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELLED) {
            System.out.println("❌ Cannot change status. Order is already in terminal state: " + currentStatus);
            return;
        }

        if (newStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.PLACED) {
            System.out.println("❌ Cannot cancel order. Restaurant has already started preparing the food.");
            return;
        }

        order.setStatus(newStatus);
        notifyCustomer(order);
    }

    private void notifyCustomer(Order order) {
        System.out.println("🔔 [ALERT: " + order.getCustomerName() + "] -> Your order " + order.getOrderId() + " is now: " + order.getStatus());
    }
}
