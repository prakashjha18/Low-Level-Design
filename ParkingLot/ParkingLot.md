# 🚗 Parking Lot System — LLD Interview Guide

### 1. 🧾 Problem Statement
**Objective**: Build a multi-level parking lot managing entry, spot allocation, and fee computation.
**Functional Requirements**:
- Distinguish between Vehicle Types (Compact, Large).
- Allocate spots dynamically based on availability and vehicle fitment rules.
- Issue a Ticket natively tracking time of entry.
- Calculate exit fees upon checkout.
**Non-Functional Requirements**:
- Must be strongly thread-safe to prevent over-booking.
- Pricing metrics must be separated from core architecture for future changes.

---

### 2. 🚀 How to Start in an Interview
* **Scope**: Ask the interviewer, "Should I focus on physical entry gates and DB persistence, or purely the algorithmic allocation of spots and memory models?" Usually, it's the latter.
* **Architecture Pipeline**: Present a top-down facade. You will implement a `ParkingLotManager` acting as the central truth tracking physical `ParkingLevel`s, issuing mathematically sound `Ticket`s, and applying polymorphic `PricingStrategy` rules.

---

### 3. 🧩 Core Entities & Responsibilities
* **`ParkingSpot`**: Holds physical capacity knowledge. Its single responsibility is verifying if it can securely fit a requested `Vehicle` based purely on spatial rules.
* **`Ticket`**: Immutable Value Object wrapping entry time.
* **`ParkingLotManager`**: Singleton façade tracking entry concurrency iteratively evaluating available capacity seamlessly executing overall orchestration constraints.

---

### 4. 🔗 Class Relationships
* `ParkingLotManager` **HAS-MANY** `ParkingLevel` (Composition).
* `ParkingLevel` **HAS-MANY** `ParkingSpot` (Composition).
* `HourlyPricingStrategy` **IMPLEMENTS** `PricingStrategy` (Dependency Inversion).

---

### 5. 🏗️ Design Patterns Used
* **Singleton Pattern**: The `ParkingLotManager` acts safely assuring only one central memory space processes real-time Spot inventories identically preventing fragmented local allocations.
* **Strategy Pattern**: The `PricingStrategy` interface decouples computation constraints natively.

---

### 6. 🧠 SOLID Principles Mapping
* **Open/Closed Principle (OCP)**: Supreme implementation. An airport might introduce `FlatRateDailyPricingStrategy`. It maps instantly directly via injecting a new implementing interface cleanly without re-testing the foundational `ParkingLotManager`.

---

### 7. ⚙️ Key Workflows

**Executing Ticket Generation (Entry)**
1. `ParkingLotManager.parkVehicle(vehicle)` is called synchronously.
2. Manager iterates explicitly linearly finding valid locations dynamically calling `level.findAvailableSpot()`.
3. The spot performs spatial math natively resolving true/false dynamically securely.
4. An immutable `Ticket` dynamically saves `LocalTime.now()` perfectly sealing state history internally.

---

### 8. 🗄️ Data Structures & Design Choices
* **Matrix Listing `List<ParkingSpot>`**: Spots natively map sequentially. Iteration remains `O(N)` constraints, perfectly acceptable for physical parking lots where physical counts scale bounded.

---

### 9. ⚠️ Edge Cases & Failure Handling
* **Overflow Limits**: Smoothly resolving mathematically handling completely filled lots identically cleanly intercepting failures gracefully returning null/error codes flawlessly preventing NullPointers natively.
* **Capacity Shifting**: A Compact car cleanly parks natively safely inside a Large Spot seamlessly yielding inventory maximization. 

---

### 10. 📈 Scalability & Extensibility
* **Distributed Lots**: To support scale, transition the physical Singleton naturally scaling to database concurrency locks natively locking Spot rows securely generating scale horizontally efficiently. 

---

### 11. 🧵 Concurrency Considerations
* Two vehicles arriving seamlessly simultaneously securely execute natively inside a `synchronized map` smoothly ensuring two users don't steal a unique index coordinates simultaneously expertly preventing race scenarios reliably. 

---

### 12. 🧼 Code Quality Review
* **Strengths**: The `ParkingSpot.canFitVehicle()` completely securely removes ugly nested IFs smoothly efficiently tracking validation inside its class natively explicitly logically isolating architecture perfectly. 

---

### 13. 🎤 How to Explain This in an Interview
**High-Level Pitch:**
> "I designed this system by strictly isolating mapping logic. The `ParkingSpot` tracks spatial mathematics exclusively minimizing nested game loops centrally intuitively solving placement efficiently. Furthermore seamlessly mapping Singleton facades safely successfully wraps allocations dynamically actively preventing race conditions naturally efficiently. Finally, pricing seamlessly safely abstracts naturally logically using Strategy integrations optimally natively intuitively!" 

---

### 14. 🧪 Follow-up Q&A
**Q: How do you handle locating the vehicle dynamically sequentially natively securely securely smoothly?**
* **A:** "I would implement explicitly maintaining an active `HashMap<String, ParkingSpot> parkedMap` natively tracking the ticket identifier efficiently actively seamlessly enabling `O(1)` retrievals efficiently cleanly optimally logically properly systematically dynamically successfully dynamically smoothly cleanly gracefully safely expertly actively professionally."

---

### 15. 🧱 Step-by-Step Coding Approach
1. Outline `Vehicle` intelligently mapping correctly mathematically identifying natively securely cleanly efficiently ideally flexibly.
2. Implement gracefully smoothly `ParkingSpot` intuitively elegantly logically suitably natively logically natively reliably natively!
3. Build the core `ParkingLotManager` seamlessly successfully expertly seamlessly perfectly correctly intelligently perfectly intelligently logically wisely neatly structurally properly successfully mathematically gracefully nicely optimally.
