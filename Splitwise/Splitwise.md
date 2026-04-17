# 💸 Splitwise — Low-Level Design

## Problem Statement

> Design an application like Splitwise. Users can add expenses. An expense can be split among multiple users equally, by exact amounts, or by percentages. The system should maintain correct running balances between all users.

---

## 🏗️ Core Design Strategies

### 1. The Strategy Pattern (The Calculation Engine)
- **Why?** Since calculating splits has fundamentally different rules depending on the type (`EQUAL`, `EXACT`, `PERCENT`), placing this all inside a single `addExpense` method creates a bloated, unmaintainable if-else block.
- **How?** Extracted into a `SplitStrategy` interface with `calculateAmounts(amount, splits)` and `validateRequest()`.  
  - If we introduce a `SHARE` split tomorrow (e.g. 2 shares to Alice, 1 share to Bob), we just implement `ShareSplitStrategy` with **zero modifications** to the `ExpenseManager`. **(Open-Closed Principle)**

### 2. The Balance Sheet (Data Structure)
- **Why?** Constantly recalculating who owes what by iterating over every past expense is O(N) and slow. We need instant balance lookups.
- **How?** Used a Graph-like adjacency matrix represented via `Map<String, Map<String, Double>>`. 
  - Ex: `balances.get("Alice").get("Bob") = 50.0` translates to "Bob owes Alice 50".
  - If Alice owes Bob 50, it is represented as `balances.get("Alice").get("Bob") = -50.0`.
  - Adding a new split instantly adjusts this running tally in O(1) time.

### 3. Inheritance / Polymorphism 
- `Split` acts as an abstract base class keeping the generic constraints (`User` and total calculated `amount`).
- `ExactSplit` inherits and configures amounts natively.
- `PercentSplit` introduces a new field `percent`.

---

## 🗣️ Interview Talking Points

### The Float precision issue
> *"In a real-world financial system, using `double` leads to precision loss (e.g., `100 / 3 = 33.3333333`). I've used `double` here for interview speed, but explicitly pointed out that I would use `BigDecimal` in production. Also, for odd splits (e.g., 100 on 3 people), my `EqualSplitStrategy` rounds up and assigns the remainder mathematically to the first person, so no cents vanish from the system."*

### Simplifying algorithm limits
> *"For this scope, I'm just recording the bidirectional debts (Alice owes Bob, Bob owes Charlie). A common follow-up question is the `simplifyDebts()` algorithm, which requires converting these balances into a network flow graph and running Min-Cash-Flow algorithms, which enters High-Level Design / Hard LeetCode territory. I've designed the `balanceSheet` explicitly so it can be easily supplied to such an algorithm."*

---

## 🔐 Edge Cases Handled Confidently

| Edge Case | Solution implemented |
|-----------|----------------------|
| **Odd division (100 / 3 users)** | Rounded to 2 decimals (`33.33`), remaining `0.01` is pushed to the first split to maintain data integrity. |
| **Invalid Percentages** | Handled natively by `<SplitStrategy>.validateRequest()` failing fast before touching any balances if percentages do not equal 100. |
| **User adding themself to expense**| The `payBy == payTo` loop iteration safely uses `continue`, skipping the math so a user doesn't owe themselves money. |

---

## UML Quick Sketch

```text
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     User        │──────▷│ ExpenseManager  │       │  ExpenseType    │
└─────────────────┘       └─────────────────┘       └─────────────────┘
                                   ◇
                                   │ delegates
                                   ▼
┌─────────────────┐       ┌─────────────────┐
│     Split       │◀──────│  SplitStrategy  │
└─────────────────┘       └─────────────────┘
      △       △                    △
      │       │                    │ implements
      │       │           ┌────────┴────────┐
┌─────────┐ ┌─────────┐   │                 │
│ExactSplit││EqualSplit│  │EqualSplitStrategy│...
└─────────┘ └─────────┘   └─────────────────┘
```
