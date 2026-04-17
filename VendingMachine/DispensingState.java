package VendingMachine;

/**
 * DISPENSING STATE — Product is being dispensed.
 * 
 * This is a transient state — the machine enters it briefly,
 * dispenses the product, returns change, and goes back to IdleState.
 * 
 * Allowed actions:
 *   ❌ insertCoin     → not allowed during dispensing
 *   ❌ selectProduct  → not allowed during dispensing
 *   ✅ dispense       → dispense product, return change, → IdleState
 *   ❌ cancel         → too late to cancel
 */
public class DispensingState implements State {

    @Override
    public void insertCoin(VendingMachine machine, Coin coin) {
        System.out.println("⚠️ Please wait. Dispensing in progress...");
    }

    @Override
    public void selectProduct(VendingMachine machine, String productCode) {
        System.out.println("⚠️ Please wait. Dispensing in progress...");
    }

    @Override
    public void dispense(VendingMachine machine) {
        Product product = machine.getSelectedProduct();

        if (product == null) {
            System.out.println("❌ Error: No product selected. Returning to idle.");
            machine.resetBalance();
            machine.setState(new IdleState());
            return;
        }

        // Reduce inventory
        machine.getInventory().reduceStock(product.getCode());

        // Calculate change
        double change = machine.getBalance() - product.getPrice();

        // Dispense
        System.out.println("\n🎉 ═══════════════════════════════════════");
        System.out.println("   Dispensing: " + product.getName());
        System.out.println("   Paid: ₹" + machine.getBalance());
        System.out.printf("   Price: ₹%.2f%n", product.getPrice());
        if (change > 0) {
            System.out.printf("   Change returned: ₹%.2f%n", change);
        }
        System.out.println("   ✅ Please collect your item!");
        System.out.println("═══════════════════════════════════════════\n");

        // Reset machine state
        machine.resetBalance();
        machine.setSelectedProduct(null);
        machine.setState(new IdleState());
    }

    @Override
    public void cancel(VendingMachine machine) {
        System.out.println("⚠️ Too late to cancel. Product is being dispensed.");
    }

    @Override
    public String getStateName() {
        return "DISPENSING";
    }
}
