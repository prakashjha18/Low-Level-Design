package VendingMachine;

/**
 * IDLE STATE — Machine is waiting for the user to do something.
 * 
 * Allowed actions:
 *   ✅ insertCoin  → transitions to HasMoneyState
 *   ✅ selectProduct → only shows info, asks to insert money first
 *   ❌ dispense    → not allowed
 *   ❌ cancel      → nothing to cancel
 */
public class IdleState implements State {

    @Override
    public void insertCoin(VendingMachine machine, Coin coin) {
        machine.addBalance(coin.getValue());
        System.out.println("💰 Inserted: ₹" + coin.getValue() + " | Balance: ₹" + machine.getBalance());
        machine.setState(new HasMoneyState());
    }

    @Override
    public void selectProduct(VendingMachine machine, String productCode) {
        Product product = machine.getInventory().getProduct(productCode);
        if (product == null) {
            System.out.println("❌ Invalid product code: " + productCode);
        } else {
            System.out.println("ℹ️ " + product + " — Please insert money first.");
        }
    }

    @Override
    public void dispense(VendingMachine machine) {
        System.out.println("⚠️ Please insert money and select a product first.");
    }

    @Override
    public void cancel(VendingMachine machine) {
        System.out.println("ℹ️ No transaction to cancel.");
    }

    @Override
    public String getStateName() {
        return "IDLE";
    }
}
