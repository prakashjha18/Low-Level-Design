package FoodOrderingSystem;

/**
 * Observer interface for the Observer Pattern.
 * Actors who care about order status changes will implement this.
 */
public interface OrderObserver {
    void update(Order order);
}
