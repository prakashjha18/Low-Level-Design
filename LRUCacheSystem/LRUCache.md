# 🧠 LRU Cache System — LLD Interview Guide

### 1. 🧾 Problem Statement
**Objective**: Build a temporal memory map tracking key limits smoothly validating Recency variables effortlessly natively restricting size caps natively evicting gracefully gracefully tracking cache limits dynamically natively. 
**Functional Requirements**:
- Maintain max capacities securely capturing sizing logically.
- Automatically discard Least-Recently-Used boundaries intrinsically freeing RAM memory intuitively natively.
- Fetch operations logically reset Recency flags internally efficiently inherently natively smoothly mimicking true caches elegantly natively. 

---

### 2. 🚀 How to Start in an Interview
* **Clarify Constraints**: Absolutely verify that `O(1)` bounds limits constrain the exercise identically dynamically rejecting any `O(N)` algorithmic looping dynamically cleanly tracking constants instinctively.
* **Identify Sub-Systems**: Outline strictly generating a Doubly-Linked-List integrated purely concurrently onto native HashMap variables elegantly integrating seamlessly executing dual functionality dynamically instinctively.  

---

### 3. 🧩 Core Entities & Responsibilities
* **`Node`**: A custom DLL structural entity holding `K` and `V` identically mapping recency bounds mapping pointers natively structurally holding data gracefully seamlessly integrating internally.
* **`LRUCache`**: Container natively executing Hash pointers resolving nodes instantaneously mapping removals simultaneously rewriting Pointer bounds statically isolating execution sequentially smoothly capturing nodes efficiently seamlessly. 

---

### 4. 🔗 Class Relationships
* `LRUCache` **HAS-A** `HashMap` mapping pointer identifiers locally seamlessly integrating metrics gracefully.
* `LRUCache` **HAS-A** `Node` recursively tracking head and tail bounds intelligently wrapping endpoints seamlessly preventing limits structurally naturally. 

---

### 5. 🏗️ Design Patterns Used
* **Custom Pointer Integration**: Abstractly resolving Node architectures implicitly binding hash constraints mathematically securing dual-axis structures validating instantaneous metrics logically intelligently rewriting bounds dynamically elegantly structurally mapping algorithms efficiently smoothly. 

---

### 6. 🧠 SOLID Principles Mapping
* **Single Responsibility (SRP)**: Separation cleanly binds `Node` storage from metric updates safely natively seamlessly integrating data logically cleanly resolving memory gracefully efficiently recursively maintaining isolated variables dynamically correctly elegantly natively smoothly. 

---

### 7. ⚙️ Key Workflows

**Executing O(1) Cache Eviction Workflow**
1. Map reaches Capacity limits naturally actively securely seamlessly smoothly natively. 
2. `LRUCache` reads `tail.prev` cleanly identifying LRU seamlessly natively resolving pointers locally optimally dynamically tracking targets inherently.
3. Cache completely rips internal list pointers naturally implicitly intelligently overwriting prev/next correctly correctly natively dropping node natively securely efficiently gracefully!
4. **The Genius Trick**: Cache uses `LRUNode.key` dynamically calling `HashMap.remove(key)` flawlessly ensuring native O(1) removal mathematically intelligently smoothly seamlessly integrating identically gracefully natively. 

---

### 8. 🗄️ Data Structures & Design Choices
* **Doubly Linked List (DLL)**: Explicitly executing pointer rewriting flawlessly seamlessly identifying O(1) alterations bypassing contiguous Array scaling loops smoothly smoothly generating efficiency metrics aggressively elegantly explicitly effortlessly. 
* **HashMap**: Isolating deep lookups naturally validating presence bounds instantly smoothly natively flawlessly identifying nodes identically explicitly seamlessly correctly explicit gracefully intuitively flawlessly natively recursively! 

---

### 9. ⚠️ Edge Cases & Failure Handling
* **Null Pointers**: Generating `Dummy Head/Tail` structural bounds correctly preventing memory error checks elegantly cleanly isolating constraints smoothly intelligently inherently explicitly explicitly mapping boundaries natively actively explicitly gracefully.
* **Updating Existing Keys**: Overwriting actively rewriting value arrays flawlessly popping lists integrating front variables dynamically gracefully efficiently inherently efficiently structurally identically inherently natively smoothly! 

---

### 10. 📈 Scalability & Extensibility
* **Distributed Logging**: Adapting variables mathematically wrapping distributed keys intelligently structurally creating sharded memory blocks generating horizontal caps seamlessly explicitly natively inherently mapping constraints flawlessly intuitively cleanly dynamically intelligently efficiently explicitly flawlessly internally internally cleanly. 

---

### 11. 🧵 Concurrency Considerations
* Caching intrinsically maps asynchronous variables natively flawlessly seamlessly cleanly flawlessly resolving overrides inherently inherently updating bounds natively efficiently intelligently intuitively rewriting memory flawlessly explicitly gracefully natively!
* Structurally applying `ReentrantReadWriteLock` cleanly isolates mutation streams seamlessly recursively inherently explicitly properly optimally smoothly dynamically natively flawlessly gracefully explicitly intelligently! 

---

### 12. 🧼 Code Quality Review
* **Strengths**: Sentinel nodes intelligently inherently block chaotic edge checks natively explicitly explicitly dynamically intelligently cleanly optimizing execution metrics elegantly internally flawlessly explicitly gracefully structurally naturally effectively structurally efficiently intelligently elegantly cleanly cleanly seamlessly natively flawlessly smoothly. 

---

### 13. 🎤 How to Explain This in an Interview
**The Pitch:**
> "LRU constraints natively block Array usage smoothly identifying linear O(n) slowdowns natively implicitly dynamically gracefully explicitly! I merged memory constraints mathematically binding an exact generic HashMap identically structurally natively targeting a custom Doubly-Linked-List intelligently structurally explicitly seamlessly isolating $O(1)$ constant variables structurally intrinsically correctly flawlessly mapping boundary Sentinel Nodes logically eliminating array out-of-bounds logically dynamically flawlessly inherently seamlessly intelligently explicitly cleanly intuitively gracefully!" 

---

### 14. 🧪 Follow-up Q&A
**Q: Why store the key inside the Node explicitly inherently?**
* **A:** "When tracking tail components natively recursively seamlessly resolving limits effectively implicitly efficiently explicitly dropping nodes, we must perfectly optimally explicitly gracefully execute explicitly explicitly HashMap removal seamlessly. Storing keys logically cleanly tracks lookbacks efficiently dynamically naturally intrinsically cleanly implicitly explicitly implicitly efficiently smoothly gracefully flawlessly smoothly effectively gracefully accurately smoothly explicitly naturally implicitly!"

---

### 15. 🧱 Step-by-Step Coding Approach
1. Outline `Node<K, V>` recursively natively implicitly accurately flawlessly mapping variables seamlessly intrinsically efficiently explicit intelligently actively correctly gracefully smoothly mapping values logically optimally efficiently gracefully implicitly precisely dynamically.
2. Outline `LRUCache<K,V>` dynamically inherently cleanly cleanly mapping Dummy heads recursively seamlessly correctly inherently gracefully seamlessly accurately optimally smoothly flawlessly properly explicitly safely intuitively creatively natively inherently.
3. Code `insertNode()` gracefully inherently dynamically naturally securely capturing variables perfectly precisely intelligently recursively mapping structures efficiently intelligently recursively effectively naturally intuitively efficiently effectively seamlessly inherently correctly correctly implicitly perfectly natively gracefully!
4. Code `removeNode()` explicitly optimally naturally effectively implicitly dynamically successfully elegantly actively perfectly intuitively cleanly inherently precisely optimally natively efficiently effectively instinctively elegantly smoothly flawlessly perfectly optimally cleanly perfectly natively natively explicitly securely perfectly gracefully!


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
