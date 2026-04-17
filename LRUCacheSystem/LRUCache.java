package LRUCacheSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * LRU Cache Implementation using HashMap + Doubly Linked List.
 * 
 * Time Complexity Expected:
 * get() : O(1)
 * put() : O(1)
 */
public class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    
    // Dummy head and tail nodes to avoid NullPointer validations during inserts/removals
    private final Node<K, V> head;
    private final Node<K, V> tail;

    public LRUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.capacity = capacity;
        this.cache = new HashMap<>();
        
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        
        // Initially empty list: head <-> tail
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Get a value from the cache.
     * If the key exists, it signifies it was recently used, 
     * so we move it to the front of the list (right after head).
     */
    public V get(K key) {
        if (!cache.containsKey(key)) {
            return null; // Cache miss
        }

        Node<K, V> node = cache.get(key);
        // Move to the front: this item was just accessed!
        removeNode(node);
        insertNodeAtHead(node);
        
        return node.value;
    }

    /**
     * Put a value inside the cache.
     * If capacity is reached, evict the LEAST recently used item (the one near tail).
     */
    public void put(K key, V value) {
        if (cache.containsKey(key)) {
            // Update value and move to front
            Node<K, V> node = cache.get(key);
            node.value = value;
            removeNode(node);
            insertNodeAtHead(node);
        } else {
            if (cache.size() >= capacity) {
                // Cache is full! Evict the tail (Least Recently Used)
                Node<K, V> lruNode = tail.prev;
                removeNode(lruNode);
                cache.remove(lruNode.key); // O(1) because we stored `key` inside the Node!
                System.out.println("❌ Evicted Key: " + lruNode.key);
            }
            
            // Insert the new element at the front
            Node<K, V> newNode = new Node<>(key, value);
            cache.put(key, newNode);
            insertNodeAtHead(newNode);
        }
    }

    // --- Doubly Linked List Helper Methods (O(1) operations) ---

    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertNodeAtHead(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    public void displayCache() {
        System.out.print("Cache List (Most Recent to Least Recent): [HEAD] <-> ");
        Node<K, V> current = head.next;
        while (current != tail) {
            System.out.print("{" + current.key + ":" + current.value + "} <-> ");
            current = current.next;
        }
        System.out.println("[TAIL]\n");
    }
}
