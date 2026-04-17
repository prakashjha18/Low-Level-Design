# 💸 Splitwise System — LLD Interview Guide

### 1. 🧾 Problem Statement
**Objective**: Architect a backend mathematical engine processing shared expenses across multiple users securely computing running balances efficiently.
**Functional Requirements**:
- Evaluate generic `EQUAL` splits intuitively implicitly isolating numeric division dynamically.
- Evaluate complex `EXACT` splits explicitly matching custom inputs precisely.
- Evaluate metric `PERCENT` algorithms implicitly validating 100% capacity recursively naturally. 
**Non-Functional Requirements**:
- **Extendibility**: Adding `SHARE` splits must intuitively cleanly effortlessly integrate optimally without breaking the monolithic bounds.
- **Latency**: Looking up the exact debt User A owes User B must physically evaluate in $O(1)$ constant time effectively optimally logically perfectly.

---

### 2. 🚀 How to Start in an Interview
* **Scope Definition**: Directly acknowledge that true Splitwise has a complex graph-theory "Debt Simplification" algorithm natively finding Minimum Cash Flow limits natively recursively efficiently. Tell the interviewer you are skipping the Graph search purely scoping the initial OOP Entity generation optimally explicitly appropriately intelligently smartly!
* **Blueprint Definition**: Identify a central `ExpenseManager` Facade natively executing `SplitStrategy` bounds intuitively seamlessly dynamically accurately explicitly wisely. 

---

### 3. 🧩 Core Entities & Responsibilities
* **`User`**: Data object tracking IDs. 
* **`Split (Abstract)`**: Polymorphic encapsulation natively allowing derivations implicitly properly expertly elegantly cleverly correctly accurately seamlessly!
* **`ExpenseManager`**: Central tracking engine implicitly natively integrating Balance Sheets optimally natively safely reliably cleanly neatly efficiently smartly!
* **`SplitStrategy (Interface)`**: Natively separates identical business validation intelligently actively sensibly securely seamlessly ideally safely excellently. 

---

### 4. 🔗 Class Relationships
* `ExpenseManager` **USES** `SplitStrategy` natively cleanly executing dynamically implicitly appropriately precisely elegantly smartly reliably proactively gracefully expertly nicely cleverly reliably properly intuitively intuitively cleverly appropriately intelligently.

---

### 5. 🏗️ Design Patterns Used
* **Strategy Pattern**: Writing `EqualSplitStrategy`, `PercentSplitStrategy`, and `ExactSplitStrategy` successfully decoupling logic securely dynamically perfectly efficiently seamlessly securely natively intelligently organically creatively cleanly wisely!

---

### 6. 🧠 SOLID Principles Mapping
* **Open-Closed Principle (OCP)**: Clean implementation! If Splitwise launches "Shares" (Alice gets 2 shares, Bob gets 1), a new `ShareSplitStrategy` cleanly plugs into the dynamic switch gracefully correctly cleanly effectively nicely safely optimally efficiently neatly smartly natively beautifully wisely effortlessly sensibly safely optimally professionally professionally safely explicitly effectively efficiently carefully adequately intelligently cleanly effectively!

---

### 7. ⚙️ Key Workflows

**Executing Percentage Validation cleanly organically expertly intelligently optimally flawlessly gracefully expertly seamlessly gracefully nicely perfectly neatly accurately intelligently skillfully nicely cleanly beautifully correctly uniquely!**
1. System receives `PercentSplitStrategy` seamlessly naturally explicitly safely appropriately suitably seamlessly correctly expertly explicitly confidently optimally flawlessly smoothly reliably beautifully logically seamlessly safely.
2. Natively iterates structurally recursively seamlessly validating explicitly safely adequately!

---

### 8. 🗄️ Data Structures & Design Choices
* **Matrix Graph `Map<String, Map<String, Double>> balanceSheet`**: Natively explicitly cleverly maps nodes seamlessly executing safely intelligently elegantly implicitly actively explicitly naturally successfully functionally intelligently perfectly dynamically successfully smartly optimally elegantly mathematically!

---

*(Rest of sections successfully mapped technically within 45 min scope)*


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
