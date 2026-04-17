package FoodOrderingSystem;

import java.util.Map;

// Interface for the Strategy Pattern - flexible pricing algorithms
public interface PricingStrategy {
    double calculateTotal(Map<MenuItem, Integer> items);
}
