package LRUCacheSystem;

public class LRUCacheDemo {
    public static void main(String[] args) {
        System.out.println("\n=== 🧠 LRU Cache LLD Demo ===\n");

        // Create an LRU Cache with Capacity = 3
        System.out.println("Initializing Cache with Capacity = 3...\n");
        LRUCache<Integer, String> cache = new LRUCache<>(3);

        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");
        cache.displayCache(); // Order: C, B, A

        System.out.println("⬇️ Accessing Key 1. This should move '1' to the front.");
        cache.get(1);
        cache.displayCache(); // Order: A, C, B

        System.out.println("⬇️ Inserting Key 4. The Cache is full. The LRU element (Key 2) will be evicted.");
        cache.put(4, "D");
        cache.displayCache(); // Order: D, A, C

        System.out.println("⬇️ Inserting Key 5. The LRU element (Key 3) will be evicted.");
        cache.put(5, "E");
        cache.displayCache(); // Order: E, D, A

        System.out.println("⬇️ Fetching Key 2 (which was evicted earlier). Should print null.");
        System.out.println("Value of Key 2: " + cache.get(2) + "\n");
    }
}
