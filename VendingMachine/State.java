package VendingMachine;

/**
 * State interface for the Vending Machine.
 * 
 * Each state defines what happens when the user performs an action.
 * The machine delegates all behavior to the current state object.
 * 
 * State Pattern: behavior changes based on internal state without
 * messy if-else chains in the main class.
 */
public interface State {

    /**
     * User inserts a coin/note into the machine.
     */
    void insertCoin(VendingMachine machine, Coin coin);

    /**
     * User selects a product by its code.
     */
    void selectProduct(VendingMachine machine, String productCode);

    /**
     * Machine dispenses the selected product.
     */
    void dispense(VendingMachine machine);

    /**
     * User requests to cancel and get a refund.
     */
    void cancel(VendingMachine machine);

    /**
     * Returns the name of the current state (for display/logging).
     */
    String getStateName();
}
