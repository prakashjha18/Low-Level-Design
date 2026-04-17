package VendingMachine;

/**
 * Demo class showcasing all Vending Machine scenarios.
 * 
 * Run this to see the complete flow:
 *   javac VendingMachine/*.java
 *   java VendingMachine.VendingMachineDemo
 */
public class VendingMachineDemo {

    public static void main(String[] args) {

        // ── Setup the machine ──
        VendingMachine machine = new VendingMachine();

        // Stock the inventory
        machine.getInventory().addProduct(new Product("A1", "Coca Cola", 40), 5);
        machine.getInventory().addProduct(new Product("A2", "Pepsi", 35), 3);
        machine.getInventory().addProduct(new Product("B1", "Lays Chips", 20), 10);
        machine.getInventory().addProduct(new Product("B2", "KitKat", 30), 2);
        machine.getInventory().addProduct(new Product("C1", "Mineral Water", 15), 8);
        machine.getInventory().addProduct(new Product("C2", "Snickers", 50), 0); // out of stock!

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║       🏭 VENDING MACHINE - LLD DEMO         ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        // ────────────────────────────────────────
        // Scenario 1: Happy path — select and buy
        // ────────────────────────────────────────
        System.out.println("\n━━━ SCENARIO 1: Successful Purchase ━━━");
        machine.showProducts();

        machine.insertCoin(Coin.TWENTY);
        machine.insertCoin(Coin.TWENTY);
        machine.selectProduct("A1");  // Coca Cola ₹40 — exact change

        // ────────────────────────────────────────
        // Scenario 2: Purchase with change returned
        // ────────────────────────────────────────
        System.out.println("\n━━━ SCENARIO 2: Purchase with Change ━━━");

        machine.insertCoin(Coin.FIFTY);
        machine.selectProduct("B1");  // Lays ₹20 → change ₹30

        // ────────────────────────────────────────
        // Scenario 3: Insufficient balance
        // ────────────────────────────────────────
        System.out.println("\n━━━ SCENARIO 3: Insufficient Balance ━━━");

        machine.insertCoin(Coin.TEN);
        machine.selectProduct("B2");  // KitKat ₹30 — only ₹10 inserted
        machine.cancel();             // get refund

        // ────────────────────────────────────────
        // Scenario 4: Out-of-stock product
        // ────────────────────────────────────────
        System.out.println("\n━━━ SCENARIO 4: Out of Stock ━━━");

        machine.insertCoin(Coin.FIFTY);
        machine.selectProduct("C2");  // Snickers — out of stock
        machine.cancel();             // refund

        // ────────────────────────────────────────
        // Scenario 5: Invalid product code
        // ────────────────────────────────────────
        System.out.println("\n━━━ SCENARIO 5: Invalid Product Code ━━━");

        machine.insertCoin(Coin.TEN);
        machine.selectProduct("Z9");  // doesn't exist
        machine.cancel();

        // ────────────────────────────────────────
        // Scenario 6: Select without inserting money
        // ────────────────────────────────────────
        System.out.println("\n━━━ SCENARIO 6: No Money Inserted ━━━");

        machine.selectProduct("A1");  // should prompt to insert money

        // ────────────────────────────────────────
        // Scenario 7: Cancel with no transaction
        // ────────────────────────────────────────
        System.out.println("\n━━━ SCENARIO 7: Cancel with No Transaction ━━━");

        machine.cancel();  // nothing to cancel

        // ────────────────────────────────────────
        // Scenario 8: Multiple coins then buy
        // ────────────────────────────────────────
        System.out.println("\n━━━ SCENARIO 8: Multiple Coins then Buy ━━━");

        machine.insertCoin(Coin.TEN);
        machine.insertCoin(Coin.TEN);
        machine.insertCoin(Coin.FIVE);
        machine.insertCoin(Coin.FIVE);
        machine.selectProduct("B2");  // KitKat ₹30 — inserted ₹30 exactly

        // ── Final inventory check ──
        System.out.println("\n━━━ FINAL INVENTORY ━━━");
        machine.showProducts();

        System.out.println("\n✅ All scenarios completed successfully!");
    }
}
