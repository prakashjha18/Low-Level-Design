package VendingMachine;

/**
 * VendingMachine — The main context class that delegates behavior to the current State.
 * 
 * Design Patterns used:
 *   - State Pattern → behavior changes based on machine state (Idle, HasMoney, Dispensing)
 *   - Composition   → VendingMachine OWNS Inventory (inventory dies with the machine)
 * 
 * Key principle: The VendingMachine NEVER has if-else chains for state checks.
 * All behavior is delegated to the current state object.
 */
public class VendingMachine {
    private State currentState;
    private Inventory inventory;         // composition — owned by this machine
    private double balance;
    private Product selectedProduct;

    public VendingMachine() {
        this.inventory = new Inventory();  // created internally → composition
        this.currentState = new IdleState();
        this.balance = 0;
        this.selectedProduct = null;
    }

    // ──────────────────────────────────────────────
    // Public API — delegates everything to the state
    // ──────────────────────────────────────────────

    /**
     * Insert a coin/note into the machine.
     */
    public void insertCoin(Coin coin) {
        System.out.println("[State: " + currentState.getStateName() + "]");
        currentState.insertCoin(this, coin);
    }

    /**
     * Select a product by its code (e.g., "A1").
     */
    public void selectProduct(String productCode) {
        System.out.println("[State: " + currentState.getStateName() + "]");
        currentState.selectProduct(this, productCode);
    }

    /**
     * Dispense the selected product (called internally by state transitions).
     */
    public void dispense() {
        currentState.dispense(this);
    }

    /**
     * Cancel the current transaction and get a refund.
     */
    public void cancel() {
        System.out.println("[State: " + currentState.getStateName() + "]");
        currentState.cancel(this);
    }

    /**
     * Display the available products in the machine.
     */
    public void showProducts() {
        inventory.displayInventory();
    }

    // ──────────────────────────────────────────────
    // State management (package-private — used by states)
    // ──────────────────────────────────────────────

    void setState(State state) {
        this.currentState = state;
    }

    State getState() {
        return currentState;
    }

    // ──────────────────────────────────────────────
    // Balance management (package-private — used by states)
    // ──────────────────────────────────────────────

    void addBalance(int amount) {
        this.balance += amount;
    }

    double getBalance() {
        return balance;
    }

    void resetBalance() {
        this.balance = 0;
    }

    // ──────────────────────────────────────────────
    // Product selection (package-private — used by states)
    // ──────────────────────────────────────────────

    void setSelectedProduct(Product product) {
        this.selectedProduct = product;
    }

    Product getSelectedProduct() {
        return selectedProduct;
    }

    // ──────────────────────────────────────────────
    // Inventory access (package-private — used by states)
    // ──────────────────────────────────────────────

    Inventory getInventory() {
        return inventory;
    }
}
