package FoodOrderingSystem;

public class FoodOrderingDemo {
    public static void main(String[] args) {
        System.out.println("\n=== 🍔 Swiggy/Zomato LLD Demo ===\n");

        // 1. Setup Entities
        User user = new User("U001", "Prakash", "9876543210", "123 Tech Lane");
        Restaurant restaurant = new Restaurant("R001", "Meghana Foods", "Koramangala");
        DeliveryExecutive exec = new DeliveryExecutive("D001", "Ramesh Driver");

        // 2. Setup Menu
        MenuItem biryani = new MenuItem("M1", "Chicken Biryani", "Spicy and delicious", 300.0);
        MenuItem paneer = new MenuItem("M2", "Paneer Butter Masala", "Rich gravy", 250.0);
        MenuItem naan = new MenuItem("M3", "Butter Naan", "Soft bread", 50.0);
        
        restaurant.getMenu().addMenuItem(biryani);
        restaurant.getMenu().addMenuItem(paneer);
        restaurant.getMenu().addMenuItem(naan);
        
        System.out.println("Welcome to " + restaurant.getName() + "!");
        restaurant.getMenu().displayMenu();

        // 3. Setup Order
        System.out.println("\n--- SCENARIO 1: Happy Path (Placed -> Prepared -> Delivered) ---");
        Order order1 = new Order("ORD-1001", user, restaurant);
        order1.addItem(biryani, 2);
        order1.addItem(naan, 3);
        
        // Use a PricingStrategy
        PricingStrategy standardPricing = new StandardPricingStrategy();
        order1.checkout(standardPricing);

        // 4. Advance State
        System.out.println("\n[Action]: Restaurant accepts and starts preparing");
        order1.advanceStatus(); // Placed -> Preparing

        System.out.println("\n[Action]: Trying to dispatch without delivery executive");
        order1.advanceStatus(); // Fails, needs delivery exec

        System.out.println("\n[Action]: Assigning Delivery Executive");
        order1.assignDeliveryExecutive(exec);

        System.out.println("\n[Action]: Food is ready, dispatching!");
        order1.advanceStatus(); // Preparing -> OutForDelivery

        System.out.println("\n[Action]: Attempting to cancel while out for delivery");
        order1.cancelOrder(); // Cannot cancel

        System.out.println("\n[Action]: Executive reaches customer location");
        order1.advanceStatus(); // OutForDelivery -> Delivered


        System.out.println("\n\n--- SCENARIO 2: Cancellation Path ---");
        Order order2 = new Order("ORD-1002", user, restaurant);
        order2.addItem(paneer, 1);
        order2.checkout(standardPricing);

        System.out.println("\n[Action]: Customer cancels immediately after placing");
        order2.cancelOrder();

        System.out.println("\n[Action]: Try to advance cancelled order");
        order2.advanceStatus(); // No further transitions

    }
}
