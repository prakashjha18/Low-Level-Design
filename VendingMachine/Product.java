package VendingMachine;

/**
 * Represents a product available in the vending machine.
 * Immutable value object — once created, product details don't change.
 */
public class Product {
    private final String code;      // e.g., "A1", "B2"
    private final String name;
    private final double price;

    public Product(String code, String name, double price) {
        if (code == null || code.isEmpty()) throw new IllegalArgumentException("Product code is required");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Product name is required");
        if (price <= 0) throw new IllegalArgumentException("Price must be positive");

        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return String.format("[%s] %s - ₹%.2f", code, name, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return code.equals(product.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
