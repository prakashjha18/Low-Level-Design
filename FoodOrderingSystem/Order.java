package FoodOrderingSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Order acts as the Context in State Pattern and Subject in Observer Pattern.
 */
public class Order implements OrderSubject {
    private final String id;
    private final User user;
    private final Restaurant restaurant;
    private final Map<MenuItem, Integer> items;
    private double totalAmount;
    
    private DeliveryExecutive deliveryExecutive;
    private OrderState currentState;
    private final List<OrderObserver> observers;

    public Order(String id, User user, Restaurant restaurant) {
        this.id = id;
        this.user = user;
        this.restaurant = restaurant;
        this.items = new HashMap<>();
        this.observers = new ArrayList<>();
        
        // Initial state is PLACED
        this.currentState = new PlacedState();
        
        // The user automatically watches their own order
        addObserver(user);
    }

    public void addItem(MenuItem item, int quantity) {
        if (!item.isAvailable()) {
            throw new IllegalArgumentException("Item " + item.getName() + " is currently unavailable.");
        }
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public void checkout(PricingStrategy pricingStrategy) {
        if (items.isEmpty()) {
            System.out.println("⚠️ Cannot place an empty order.");
            return;
        }
        this.totalAmount = pricingStrategy.calculateTotal(items);
        System.out.println("💰 Total calculated: ₹" + totalAmount);
        
        // Notify observers that order is placed
        notifyObservers();
    }

    // State delegation methods
    public void advanceStatus() {
        currentState.nextState(this);
    }

    public void cancelOrder() {
        currentState.cancelOrder(this);
    }

    // Used by concrete states to change the context's state
    void setState(OrderState state) {
        this.currentState = state;
        // Status changed, notify customer (and delivery executive)
        notifyObservers();
    }
    
    public void assignDeliveryExecutive(DeliveryExecutive exec) {
        this.deliveryExecutive = exec;
        System.out.println("👨‍🚀 Delivery Executive " + exec.getName() + " assigned.");
        // Add delivery exec as an observer so they get notified when food is ready
        addObserver(exec);
    }

    // Observer Pattern implementation
    @Override
    public void addObserver(OrderObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (OrderObserver observer : observers) {
            observer.update(this);
        }
    }

    // Getters
    public String getId() { return id; }
    public User getUser() { return user; }
    public OrderState getState() { return currentState; }
    public DeliveryExecutive getDeliveryExecutive() { return deliveryExecutive; }
    public double getTotalAmount() { return totalAmount; }
}
