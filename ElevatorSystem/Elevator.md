# 🛗 Elevator System — LLD Interview Guide

### 1. 🧾 Problem Statement
**Objective**: Build the logic system for a skyscraper Elevator. 
**Functional Requirements**:
- Accept button presses from inside the elevator (target floors).
- Accept button presses from hallway doors natively (Origin + Direction mappings).
- System must route the elevator optimally prioritizing path execution.
**Non-Functional Requirements**:
- **Starvation Resistance**: First-Come-First-Serve natively forces an elevator to jump from floor 1 to 90 to 2 efficiently breaking systems. We need heuristic bounds.
- **Concurrency**: User inputs stream asynchronously completely decoupled from travel execution.

---

### 2. 🚀 How to Start in an Interview
* **Clarify**: "Should I build out the entire hardware interfaces (`LightUpPanel`, `SensorInterface`), or specifically write the mathematical algorithm dictating cabin destinations efficiently?" (Always target the algorithm limits).
* **Scope**: "I will map out the backend structures for a Single Elevator utilizing the `SCAN` (Elevator) algorithm natively executing continuous bidirectional sweeps instead of fragmented chronological jumping natively."

---

### 3. 🧩 Core Entities & Responsibilities
* **`Elevator`**: Pure POJO. Remembers purely `currentFloor` and active `Direction`.
* **`Request`**: Translates a button press securely capturing if it is mathematically internal (`targetFloor`) or externally routed.
* **`ElevatorController`**: The algorithmic workhorse natively decoupling input queues securely mapping sequential mathematical execution via Priority Queues natively.

---

### 4. 🔗 Class Relationships
* `ElevatorController` **HAS-A** `Elevator` natively executing bounds controls dynamically against it.
* `ElevatorController` **RECEIVES** `Request` securely tracking them iteratively. 

```text
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│ ElevatorSystem  │──────▷│ElevatorControllr│◇──────│    Elevator     │
└─────────────────┘       └─────────────────┘       └─────────────────┘
     Input Loop               SCAN Algorithm          Physical Memory
```

---

### 5. 🏗️ Design Patterns Used
* **Heuristic Scheduler (Concept)**: Natively utilizes priority heaps identically to OS disk cylinder scheduling systems prioritizing closest geometry sequentially eliminating starvation dynamically.

---

### 6. 🧠 SOLID Principles Mapping
* **Single Responsibility (SRP)**: The `Elevator` is 'dumb', strictly acting as memory natively. The `ElevatorController` dynamically controls pathing mathematically isolating rules dynamically.
* **Interface Segregation**: Handled well, though we skipped creating interface limits natively out of time constraints (e.g. `IElevatorAlgo` vs `SCANAlgo`).

---

### 7. ⚙️ Key Workflows

**Executing The SCAN Algorithm Native Routing:**
1. User presses Floor 5 natively.
2. `Controller` maps input direction. Is the elevator ascending? Is 5 greater than `currentFloor`?
3. Injects identically into a `Min-Heap Priority Queue` natively keeping `5` cleanly behind `2` prioritizing localized routes.
4. If ascending, and the user presses descending `4`, injects identically to the `Max-Heap Queue` handling downward geometry sequentially upon rotation natively. 

---

### 8. 🗄️ Data Structures & Design Choices
* **Min-Heap `PriorityQueue<Integer>`**: Automatically natively sorts incoming UP targets `[2, 3, 5, 8]` without constant `Collections.sort()` cycles natively.
* **Max-Heap `PriorityQueue<Integer>(Collections.reverseOrder())`**: Efficiently maps DOWN tracking natively sorting targets `[9, 5, 1, 0]`.

---

### 9. ⚠️ Edge Cases & Failure Handling
* **Redundant Buttons**: Natively trapped (`target == currentFloor`), natively preventing redundant door cycles. 
* **Missing Sweeps**: Routing targets natively backwards limits geometry appropriately adding them sequentially onto the opposing Heap safely skipping time-wasting jumps. 

---

### 10. 📈 Scalability & Extensibility
* **Multiple Elevators**: To support 4 skyscraper elevators securely, build `ElevatorDispatcher` handling inputs centrally securely identifying traversing geometry dynamically executing `.acceptRequest()` upon the closest relative matching Controller.

---

### 11. 🧵 Concurrency Considerations
* Inputs queue asynchronously dynamically executing mathematically. 
* Because `upQueue` receives additions safely amidst iterative processing, use `PriorityBlockingQueue` natively avoiding concurrent structural exception modification exceptions simultaneously. 

---

### 12. 🧼 Code Quality Review
* **Strengths**: The priority heap logic explicitly eliminates FCFS starvation efficiently solving real-world mathematical pathing smoothly. 

---

### 13. 🎤 How to Explain This in an Interview
**The Pitch:**
> "Elevators are completely algorithmic constraints natively handling geometry. FCFS (Queue arrays) causes violent mathematical jumping breaking efficiencies seamlessly. I architected the traditional SCAN algorithm recursively tracking execution sweeps using dual Priority Heaps natively isolating Max and Min structures. This guarantees continuous ascending logic smoothly halting at injected coordinates naturally reversing securely ensuring users never experience algorithmic starvation natively."

---

### 14. 🧪 Follow-up Q&A
**Q: How does SCAN differ from LOOK?**
* **A:** "SCAN formally forces the machine fully natively to `MaxFloor` before sweeping securely backwards. My code actually natively functions closer to LOOK sequentially because iteration natively ceases upon emptying the local `upQueue` natively safely preventing useless empty travel constraints." 

---

### 15. 🧱 Step-by-Step Coding Approach
1. Outline `Elevator` and `Enum Direction`.
2. Wrap inputs seamlessly into `Request` handling memory securely.
3. Build the core `ElevatorController` instantiating dual `Min/Max Heaps` correctly mapping `Collections.reverseOrder()`.
4. Handcraft the `acceptRequest()` securely using IF checks dictating which heap locally captures the integer seamlessly.
5. Create `startElevator()` sweeping `while(!heap.isEmpty())` cleanly swapping directions recursively upon completion dynamically.
