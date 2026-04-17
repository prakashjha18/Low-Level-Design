package LRUCacheSystem;

/**
 * Node for the Doubly Linked List.
 * It strictly holds both the key and the value so that during eviction
 * from the TAIL of the list, we instantly know which KEY to remove from the HashMap!
 */
public class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev;
    Node<K, V> next;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
