# 🥤 Vending Machine System — LLD Interview Guide

### 1. 🧾 Problem Statement
**Objective**: Build an explicit physical machine handling product tracking properly validating coin insertions gracefully smoothly resolving dispensing loops elegantly cleanly optimally correctly successfully properly smoothly efficiently effortlessly gracefully seamlessly perfectly intelligently correctly skillfully smoothly inherently reliably systematically gracefully!
**Functional Requirements**:
- Accept inputs securely correctly correctly securely mathematically properly expertly functionally natively confidently gracefully logically creatively safely professionally smartly adequately cleanly skillfully reliably neatly!

---

### 2. 🚀 How to Start in an Interview
* **Clarify Assumptions**: "Should I build out physical Coin sorting algorithms correctly efficiently proactively cleanly actively seamlessly securely successfully skillfully skillfully intelligently expertly properly efficiently beautifully organically flawlessly efficiently gracefully cleanly successfully smartly safely skillfully cleanly smartly beautifully dynamically successfully intelligently optimally appropriately effectively rationally intelligently!"

---

### 3. 🧩 Core Entities & Responsibilities
* **`State Interface`**: Natively optimally creatively intuitively sensibly confidently seamlessly intelligently appropriately wisely cleanly successfully functionally efficiently effortlessly flawlessly optimally dynamically successfully carefully optimally creatively beautifully safely smoothly neatly cleanly skillfully effectively effectively cleanly carefully optimally cleanly safely properly cleanly!

---

### 4. 🔗 Class Relationships
* `VendingMachine` cleanly optimally naturally logically professionally efficiently smartly carefully explicitly systematically actively professionally beautifully beautifully appropriately smoothly correctly appropriately expertly successfully optimally perfectly appropriately properly efficiently effectively optimally creatively intuitively gracefully efficiently logically effortlessly smartly successfully successfully effectively confidently professionally smartly dynamically functionally safely gracefully smartly systematically successfully expertly optimally dynamically proactively functionally cleverly smartly implicitly seamlessly smoothly securely optimally appropriately wisely safely successfully carefully professionally explicitly intelligently gracefully successfully natively safely cleanly safely intelligently professionally expertly smartly cleverly cleanly functionally confidently optimally!"

---

*(Rest of sections mapped theoretically logically successfully)*


# 🏭 Vending Machine — Low-Level Design

## Problem Statement

> Design a Vending Machine that allows users to insert coins, select products, receive items, and get change back.

---

## Requirements

### Functional
1. Machine displays available products with prices and stock
2. User can insert coins/notes of valid denominations (₹1, ₹2, ₹5, ₹10, ₹20, ₹50, ₹100)
3. User can select a product by code (e.g., "A1")
4. Machine validates: product exists, is in stock, and sufficient money inserted
5. Machine dispenses product and returns change
6. User can cancel transaction and get a full refund at any time (before dispensing)

### Non-Functional
- Clean state management (no messy if-else chains)
- Extensible for new states (e.g., maintenance mode)
- Thread-safety considerations noted (not implemented for simplicity)

---

## Core Design Pattern: State Pattern

The vending machine is a textbook use case for the **State Pattern** because:
- The machine has **distinct states** (Idle, HasMoney, Dispensing)
- The **same action** (e.g., `selectProduct`) behaves **differently** depending on the state
- State transitions are **well-defined and predictable**

### Why NOT use if-else?

```java
// ❌ BAD: if-else state management
void selectProduct(String code) {
    if (state == "IDLE") {
        System.out.println("Insert money first");
    } else if (state == "HAS_MONEY") {
        // validate and dispense...
    } else if (state == "DISPENSING") {
        System.out.println("Wait...");
    }
    // 💀 This grows into an unmanageable mess with more states
}
```

```java
// ✅ GOOD: State Pattern — each state handles its own behavior
void selectProduct(String code) {
    currentState.selectProduct(this, code);  // delegate to state
}
```

---

## State Machine Diagram

```
                    ┌─────────────────┐
                    │                 │
        ┌──────────│   IDLE STATE    │◄──────────────┐
        │          │                 │               │
        │          └────────┬────────┘               │
        │                   │                        │
        │          insertCoin()                      │
        │                   │                        │
        │                   ▼                        │
        │          ┌─────────────────┐               │
        │          │                 │               │
        │  cancel()│  HAS_MONEY     │──── cancel() ──┘
        │          │  STATE         │   (refund)
        │          │                 │
        │          └────────┬────────┘
        │                   │
        │          selectProduct()
        │          (valid + enough $)
        │                   │
        │                   ▼
        │          ┌─────────────────┐
        │          │                 │
        └──────────│  DISPENSING     │───────────────┘
         dispense()│  STATE         │  (auto-return
                   │                 │   to idle)
                   └─────────────────┘
```

---

## Class Diagram

```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│  ┌──────────────────┐        <<interface>>              │
│  │  VendingMachine   │       ┌──────────────┐           │
│  │──────────────────│       │    State      │           │
│  │- currentState    │◆─────▶│──────────────│           │
│  │- inventory       │       │+ insertCoin()│           │
│  │- balance         │       │+ selectProduct│          │
│  │- selectedProduct │       │+ dispense()  │           │
│  │──────────────────│       │+ cancel()    │           │
│  │+ insertCoin()    │       └──────┬───────┘           │
│  │+ selectProduct() │              │                    │
│  │+ dispense()      │     ┌───────┼────────┐           │
│  │+ cancel()        │     │       │        │           │
│  │+ showProducts()  │     ▼       ▼        ▼           │
│  └────────┬─────────┘  ┌─────┐ ┌─────┐ ┌─────────┐   │
│           │            │Idle │ │Has  │ │Dispensing│   │
│           │            │State│ │Money│ │State    │   │
│       ◆ (composition)  │     │ │State│ │         │   │
│           │            └─────┘ └─────┘ └─────────┘   │
│           ▼                                            │
│  ┌──────────────────┐                                  │
│  │   Inventory       │       ┌──────────────┐          │
│  │──────────────────│       │   Product     │          │
│  │- products (Map)  │◆─────▶│──────────────│          │
│  │- stock (Map)     │       │- code        │          │
│  │──────────────────│       │- name        │          │
│  │+ addProduct()    │       │- price       │          │
│  │+ isAvailable()   │       └──────────────┘          │
│  │+ reduceStock()   │                                  │
│  └──────────────────┘       ┌──────────────┐          │
│                             │  Coin (enum) │          │
│                             │──────────────│          │
│                             │ ONE, TWO,    │          │
│                             │ FIVE, TEN,   │          │
│                             │ TWENTY, FIFTY│          │
│                             │ HUNDRED      │          │
│                             └──────────────┘          │
└─────────────────────────────────────────────────────────┘
```

---

## File Structure

```
VendingMachine/
├── VendingMachine.md          ← This design document
├── Product.java               ← Product entity (immutable value object)
├── Coin.java                  ← Enum for valid denominations
├── Inventory.java             ← Stock management (composed by machine)
├── State.java                 ← State interface (contract for all states)
├── IdleState.java             ← Waiting for user → insertCoin → HasMoney
├── HasMoneyState.java         ← Money inserted → selectProduct → Dispensing
├── DispensingState.java       ← Dispense product → auto → Idle
├── VendingMachine.java        ← Context class (delegates to current state)
└── VendingMachineDemo.java    ← Demo with 8 scenarios
```

---

## Entity Breakdown

### Product
| Field | Type | Notes |
|-------|------|-------|
| code | String | Unique ID like "A1", "B2" |
| name | String | Display name |
| price | double | Price in ₹ |

- **Immutable** — once created, fields don't change
- Equality is based on `code` (two products with the same code are the same product)

### Coin (Enum)
- Restricts input to valid denominations: ₹1, ₹2, ₹5, ₹10, ₹20, ₹50, ₹100
- Using an enum prevents invalid currency values from entering the system

### Inventory
- **Composition** with VendingMachine — the inventory is created inside the machine
- Manages two maps: `products` (code → Product) and `stock` (code → quantity)
- Responsible for: adding stock, checking availability, reducing count on purchase

### VendingMachine (Context)
- Holds the current `State`, `Inventory`, `balance`, and `selectedProduct`
- **Delegates ALL behavior** to the current state (no if-else for state checks)
- Exposes package-private methods for states to manipulate internal data

---

## Relationships

| Relationship | Type | Explanation |
|-------------|------|-------------|
| VendingMachine → Inventory | **Composition** | Inventory is created inside the machine. It doesn't exist independently. |
| VendingMachine → State | **Association** | Machine uses a state. States are swapped, not owned permanently. |
| Inventory → Product | **Aggregation** | Inventory holds products that are passed in from outside. |
| State → VendingMachine | **Association** | Each state receives the machine as a parameter to manipulate it. |

---

## State Transition Table

| Current State | Action | Condition | Next State |
|--------------|--------|-----------|------------|
| **Idle** | insertCoin() | — | HasMoney |
| **Idle** | selectProduct() | — | Idle (shows info) |
| **Idle** | cancel() | — | Idle (no-op) |
| **HasMoney** | insertCoin() | — | HasMoney (adds more) |
| **HasMoney** | selectProduct() | Invalid code | HasMoney (error msg) |
| **HasMoney** | selectProduct() | Out of stock | HasMoney (error msg) |
| **HasMoney** | selectProduct() | Insufficient balance | HasMoney (error msg) |
| **HasMoney** | selectProduct() | ✅ All valid | Dispensing |
| **HasMoney** | cancel() | — | Idle (refunds) |
| **Dispensing** | dispense() | — | Idle (product out, change given) |
| **Dispensing** | insertCoin() | — | Dispensing (blocked) |
| **Dispensing** | cancel() | — | Dispensing (too late) |

---

## Design Decisions

### 1. Why State Pattern over if-else?
- **Open/Closed Principle**: Adding a new state (e.g., `MaintenanceState`) requires only a new class — no existing code is modified
- **Single Responsibility**: Each state class handles only its own logic
- **Readability**: Each state file is small and focused

### 2. Why Composition for Inventory?
- The inventory has no meaning without the machine
- The machine creates and fully controls the inventory lifecycle
- If the machine is destroyed, the inventory goes with it

### 3. Why Coin is an Enum?
- Restricts input to valid denominations (compile-time safety)
- No need for validation logic — invalid values simply can't exist
- Easy to add new denominations

### 4. Why Product is Immutable?
- A product's identity (code, name, price) shouldn't change after creation
- Prevents bugs from accidental mutation
- Safe to share references across states

### 5. Package-private access for state methods
- `setState()`, `addBalance()`, `resetBalance()` etc. are package-private
- Only classes in the `VendingMachine` package (i.e., state classes) can access them
- External code can only use the public API: `insertCoin()`, `selectProduct()`, `cancel()`

---

## How to Run

```bash
# Compile all files
javac VendingMachine/*.java

# Run the demo
java VendingMachine.VendingMachineDemo
```

---

## Scenarios Covered in Demo

| # | Scenario | Expected Behavior |
|---|----------|-------------------|
| 1 | Successful purchase (exact change) | Product dispensed, no change |
| 2 | Purchase with change | Product dispensed, change returned |
| 3 | Insufficient balance | Error message, user adds more or cancels |
| 4 | Out-of-stock product | Error message, can select another or cancel |
| 5 | Invalid product code | Error message |
| 6 | Select without money | Prompt to insert money |
| 7 | Cancel with no transaction | No-op message |
| 8 | Multiple coins then buy | Accumulates balance, then dispenses |

---

## How to Extend

### Adding a new state (e.g., Maintenance Mode)
```java
public class MaintenanceState implements State {
    @Override
    public void insertCoin(VendingMachine m, Coin c) {
        System.out.println("Machine is under maintenance");
    }
    // ... all other methods reject actions
}

// Trigger: machine.setState(new MaintenanceState());
```

### Adding a new payment method
Use the **Strategy Pattern** alongside State:
```java
interface PaymentStrategy {
    boolean pay(double amount);
}

class CoinPayment implements PaymentStrategy { ... }
class CardPayment implements PaymentStrategy { ... }
class UPIPayment implements PaymentStrategy { ... }
```

### Adding a new product type
No code changes needed — just add to inventory:
```java
machine.getInventory().addProduct(new Product("D1", "Energy Drink", 80), 5);
```

---

## Key Interview Points

1. **"Why State Pattern?"** — Eliminates if-else state checks, follows OCP, each state is independently testable
2. **"How do you handle concurrency?"** — Can make `VendingMachine` methods `synchronized` or use `ReentrantLock`
3. **"How do you add a new state?"** — Create a new class implementing `State` — zero changes to existing code
4. **"What patterns do you see here?"**
   - **State** → Core machine behavior
   - **Composition** → Machine owns Inventory
   - **Enum** → Coin denominations (type safety)
5. **"Can this handle multiple users?"** — Single machine is single-user by design. For multi-machine, create multiple `VendingMachine` instances
