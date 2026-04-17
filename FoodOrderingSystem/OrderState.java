package FoodOrderingSystem;

/**
 * State interface for the Order lifecycle.
 * Defines the contract for what transitions are possible from any given state.
 */
public interface OrderState {
    void nextState(Order order);
    void cancelOrder(Order order);
    String getStateName();
}
