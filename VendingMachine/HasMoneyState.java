package VendingMachine;

/**
 * HAS MONEY STATE — User has inserted money, can select product or add more.
 * 
 * Allowed actions:
 *   ✅ insertCoin     → add more money, stay in this state
 *   ✅ selectProduct  → if enough balance, transition to DispensingState
 *   ❌ dispense       → not allowed directly (must select product first)
 *   ✅ cancel         → refund and go back to IdleState
 */
public class HasMoneyState implements State {

    @Override
    public void insertCoin(VendingMachine machine, Coin coin) {
        machine.addBalance(coin.getValue());
        System.out.println("💰 Inserted: ₹" + coin.getValue() + " | Balance: ₹" + machine.getBalance());
    }

    @Override
    public void selectProduct(VendingMachine machine, String productCode) {
        Inventory inventory = machine.getInventory();
        Product product = inventory.getProduct(productCode);

        // Validate product exists
        if (product == null) {
            System.out.println("❌ Invalid product code: " + productCode);
            return;
        }

        // Validate product is in stock
        if (!inventory.isAvailable(productCode)) {
            System.out.println("❌ Sorry, " + product.getName() + " is out of stock.");
            return;
        }

        // Validate sufficient balance
        if (machine.getBalance() < product.getPrice()) {
            double deficit = product.getPrice() - machine.getBalance();
            System.out.printf("❌ Insufficient balance. %s costs ₹%.2f. Please insert ₹%.2f more.%n",
                    product.getName(), product.getPrice(), deficit);
            return;
        }

        // All checks passed — set selected product and transition to dispensing
        machine.setSelectedProduct(product);
        System.out.println("✅ Selected: " + product.getName());
        machine.setState(new DispensingState());

        // Automatically trigger dispensing
        machine.dispense();
    }

    @Override
    public void dispense(VendingMachine machine) {
        System.out.println("⚠️ Please select a product first.");
    }

    @Override
    public void cancel(VendingMachine machine) {
        double refund = machine.getBalance();
        machine.resetBalance();
        machine.setSelectedProduct(null);
        machine.setState(new IdleState());
        System.out.println("↩️ Transaction cancelled. Refunded: ₹" + refund);
    }

    @Override
    public String getStateName() {
        return "HAS_MONEY";
    }
}
