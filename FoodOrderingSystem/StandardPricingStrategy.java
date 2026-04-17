package FoodOrderingSystem;

import java.util.Map;

// Calculates total price without any discounts
public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public double calculateTotal(Map<MenuItem, Integer> items) {
        double total = 0.0;
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        // Add flat delivery fee
        return total + 50.0; 
    }
}
