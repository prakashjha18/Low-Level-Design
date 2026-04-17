package VendingMachine;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

/**
 * Manages the stock of products in the vending machine.
 * Composition relationship with VendingMachine — inventory doesn't exist without the machine.
 */
public class Inventory {
    private final Map<String, Product> products;   // code → Product
    private final Map<String, Integer> stock;      // code → quantity

    public Inventory() {
        this.products = new HashMap<>();
        this.stock = new HashMap<>();
    }

    /**
     * Add or restock a product in the inventory.
     */
    public void addProduct(Product product, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");

        products.put(product.getCode(), product);
        stock.merge(product.getCode(), quantity, Integer::sum);
    }

    /**
     * Get a product by its code.
     * @return Product or null if not found
     */
    public Product getProduct(String code) {
        return products.get(code);
    }

    /**
     * Check if a product is available (exists and in stock).
     */
    public boolean isAvailable(String code) {
        return stock.getOrDefault(code, 0) > 0;
    }

    /**
     * Get current stock count for a product.
     */
    public int getStock(String code) {
        return stock.getOrDefault(code, 0);
    }

    /**
     * Reduce stock by 1 after dispensing.
     * @throws IllegalStateException if product is out of stock
     */
    public void reduceStock(String code) {
        if (!isAvailable(code)) {
            throw new IllegalStateException("Product " + code + " is out of stock");
        }
        stock.put(code, stock.get(code) - 1);
    }

    /**
     * Get all available products (read-only view).
     */
    public Map<String, Product> getAllProducts() {
        return Collections.unmodifiableMap(products);
    }

    /**
     * Display the current inventory with stock levels.
     */
    public void displayInventory() {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║           🏪 AVAILABLE PRODUCTS              ║");
        System.out.println("╠══════════════════════════════════════════════╣");

        if (products.isEmpty()) {
            System.out.println("║  (No products available)                     ║");
        } else {
            for (Map.Entry<String, Product> entry : products.entrySet()) {
                Product p = entry.getValue();
                int qty = stock.getOrDefault(entry.getKey(), 0);
                String status = qty > 0 ? "✅ In Stock (" + qty + ")" : "❌ Sold Out";
                System.out.printf("║  %-30s %s%n", p.toString(), status);
            }
        }

        System.out.println("╚══════════════════════════════════════════════╝");
    }
}
