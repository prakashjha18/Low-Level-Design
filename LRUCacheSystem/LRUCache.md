# 🧠 LRU (Least Recently Used) Cache — Low-Level Design

## 🤔 What is an LRU Cache?
An **LRU (Least Recently Used) Cache** is a data structure with a fixed size (capacity). It stores data temporarily to provide blazing fast retrieval. 
When the cache gets **full**, and a new item needs to be added, the cache must inherently "forget" an item to make room. 

The **Eviction Policy** of an LRU cache is simply: **Throw away the item that was accessed the longest time ago.**

*Real-world examples:*
- Web Browsers discarding the oldest cached images when RAM fills up.
- Redis database configured with the `allkeys-lru` eviction policy.

---

## 🎯 The Interview Goal: $O(1)$ Time Complexity
To pass an interview, the `get()` and `put()` methods **must** run in $O(1)$ constant time.
If you scan an array to look for an item -> `O(n)`. FAILED.
If you use an array and shift all elements over when an item is deleted -> `O(n)`. FAILED.

### The Winning Architecture: `HashMap` + `Doubly Linked List`
To achieve absolute $O(1)$ time, we combine two data structures:

| Data Structure | Function | Time Complexity |
|----------------|----------|-----------------|
| **HashMap** | Instantly finds the physical Node in memory by its Key. | $O(1)$ lookup |
| **Doubly Linked List** | Tracks the "Recency". Most recent sits at the `HEAD`. Least recent sits at the `TAIL`. Instantly reorganizes connections without array-shifting. | $O(1)$ inserts/removals |

---

## 🏗️ Core Design Visualized

Imagine a Capacity of `3`. We do: `put(1, A), put(2, B), put(3, C)`.

```text
       ┌───────────┐         ┌───────┐   ┌───────┐   ┌───────┐         ┌───────────┐
       │ Dummy Head│ <─────> │ 3: C  │<─>│ 2: B  │<─>│ 1: A  │ <─────> │ Dummy Tail│
       └───────────┘         └───────┘   └───────┘   └───────┘         └───────────┘
```

**Scenario 1: `get(1)` is called.**
- We lookup Key 1 in the map instantly. We rip Node `1:A` entirely out of the list, and rewire it back in directly next to the `Dummy Head`.
- New List: `H <-> 1:A <-> 3:C <-> 2:B <-> T`

**Scenario 2: `put(4, D)` is called (Cache Full).**
- Which node is the Least Recently Used? It's simply `Tail.prev` (which is `2:B`).
- We delete `2:B` instantly.
- **CRITICAL detail:** Notice our Node class holds BOTH the `key` and the `value`. Why does the Node need the `key`? Because after we delete Node `2:B` from the Doubly Linked List, we STILL need to delete it from the HashMap! By storing the key in the node (`node.key`), we instantly do `hashMap.remove(node.key)` in $O(1)$ time. 
- Finally, we insert `4:D` at the HEAD!

---

## 🗣️ Interview Talking Points

### "Why Dummy Head and Dummy Tail?"
> *"I've used sentinel nodes (dummy head and dummy tail) because they act as boundaries. By doing this, we never ever have to write defensive checks like `if (head == null)` or `if (current.prev != null)` when inserting or removing nodes. The code stays incredibly elegant and prevents NullPointerExceptions."*

### Thread Safety (Concurrency)
> *"This implementation is purely single-threaded. To make this thread-safe (like `ConcurrentHashMap`), we cannot just wrap `put()` in a `synchronized` block blindly, or throughput will tank. A production setup would use a **ReadWriteLock**, or leverage `java.util.LinkedHashMap(capacity, loadFactor, accessOrder)` which already has LRU functionality built exactly into the JDK."*
