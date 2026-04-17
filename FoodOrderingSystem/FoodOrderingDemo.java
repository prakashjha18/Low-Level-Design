package FoodOrderingSystem;

import java.util.HashMap;
import java.util.Map;

public class FoodOrderingDemo {
    public static void main(String[] args) {
        System.out.println("\n=== 🍔 Swiggy/Zomato LLD Demo (Interview Optimized) ===\n");

        OrderService orderService = OrderService.getInstance();

        // 1. Setup Restaurant & Menu
        Restaurant meghana = new Restaurant("R01", "Meghana Foods");
        MenuItem biryani = new MenuItem("M1", "Chicken Biryani", 300.0);
        MenuItem paneer = new MenuItem("M2", "Paneer Tikka", 250.0);
        meghana.addMenuItem(biryani);
        meghana.addMenuItem(paneer);

        // --- Scenario 1: Happy Path ---
        System.out.println("--- Scenario 1: Successful Order Processing ---");
        Map<MenuItem, Integer> cart1 = new HashMap<>();
        cart1.put(biryani, 2);
        cart1.put(paneer, 1);

        Order order1 = orderService.placeOrder("Prakash", meghana, cart1);

        // Sequence of valid transitions
        orderService.updateOrderStatus(order1.getOrderId(), OrderStatus.PREPARING);
        orderService.updateOrderStatus(order1.getOrderId(), OrderStatus.OUT_FOR_DELIVERY);
        orderService.updateOrderStatus(order1.getOrderId(), OrderStatus.DELIVERED);

        // Trying to cancel after delivery (Should fail)
        orderService.updateOrderStatus(order1.getOrderId(), OrderStatus.CANCELLED);


        // --- Scenario 2: Cancellation Path ---
        System.out.println("\n--- Scenario 2: Late Cancellation Prevention ---");
        Map<MenuItem, Integer> cart2 = new HashMap<>();
        cart2.put(biryani, 1);
        
        Order order2 = orderService.placeOrder("Ramesh", meghana, cart2);
        orderService.updateOrderStatus(order2.getOrderId(), OrderStatus.PREPARING);
        
        // Try to cancel after preparation started (Should fail)
        orderService.updateOrderStatus(order2.getOrderId(), OrderStatus.CANCELLED);
    }
}
