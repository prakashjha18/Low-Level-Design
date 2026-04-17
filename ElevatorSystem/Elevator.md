# 🛗 Elevator System — Low-Level Design

## Problem Statement

> Design an Elevator System for a building. Users can press buttons in the hallway (UP/DOWN) and inside the elevator (target floor). The elevator should service requests optimally rather than strictly First-Come-First-Serve (FCFS) to prevent starvation and erratic jumping (e.g. going from Floor 0 -> 9 -> 1 -> 8).

---

## 🎯 Interview Constraints & Approach

An elevator can quickly spin out of control if you try designing the hardware buttons (`HallButton`, `ElevatorButton`, `IndicatorDisplay`) as individual objects. In an interview:
- **Skip the hardware objects.** 
- **Focus strictly on the Optimization Algorithm.** 

The interviewer is looking for one specific thing: **Can you implement the SCAN (Elevator) Algorithm using Priority Queues?**

---

## 🏗️ Core Design Strategies

### 1. The SCAN Algorithm (Using Priority Queues)
- **Why FCFS fails:** If the queue is `[9, 2, 8, 1]`, the elevator travels `9 floors UP`, `7 floors DOWN`, `6 floors UP`, `7 floors DOWN`. It destroys time and energy.
- **How SCAN fixes it:** The elevator travels in one direction (e.g. `UP`), stops at every requested floor on the way up, and only when the `UP` requests are exhausted, does it reverse direction `DOWN` to service the rest.
- **Data Structures Used:**
  1. `PriorityQueue<Integer> upQueue`: A **Min-Heap**. Automatically sorts requested floors in ascending order (e.g. 1 -> 4 -> 7 -> 9).
  2. `PriorityQueue<Integer> downQueue`: A **Max-Heap**. Automatically sorts requested floors in descending order (e.g. 8 -> 5 -> 2 -> 0).

### 2. Request Routing (The `acceptRequest` method)
When a request comes in, the controller smartly routes it based on the elevator's *current direction*.
- If going `UP` and the requested floor is *above* the current floor: add to `upQueue`.
- If going `UP`, but the requested floor is *below* us (we missed it): queue it in the `downQueue` for the next sweep!

---

## 🗣️ Interview Talking Points

### Handling System Expansion (Multiple Elevators)
> *"This design scopes a single elevator. To handle multiple elevators (like a bank of 4 in an office), I would use the **Strategy Pattern** on an `ElevatorDispatcher`. When a user presses a hallway button, the dispatcher uses an algorithm—like checking exactly which elevator is traveling in the requested direction and is physically closest—and assigns the `Request` strictly to that specific Elevator's Controller."*

### Thread Safety (Concurrency)
> *"Since requests come from independent user threads asynchronously (hallway buttons), the `upQueue` and `downQueue` must be strictly thread-safe. In a multi-threaded implementation, I would swap Java's standard `PriorityQueue` for a `PriorityBlockingQueue` to handle concurrent inserts without explicit `synchronized` blocks."*

---

## 🔐 Edge Cases Handled Confidently

| Edge Case | Solution implemented |
|-----------|----------------------|
| **Already at Floor** | Immediately skips executing motion if `targetFloor == currentFloor` to prevent unnecessary door cycles. |
| **Starvation Prevention** | A user going DOWN from floor 2 while the elevator is traversing UP to 9 will have their request routed to the `downQueue`. The elevator will strictly finish the UP sweep first before reversing. |

---

## UML Quick Sketch

```text
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     Request     │       │     Elevator    │       │ ElevatorManager │
└─────────────────┘       └─────────────────┘       └─────────────────┘
                                   △                         ◇
                                   │                         │
                                   ▼                         ▼
                          ┌─────────────────┐       ┌─────────────────┐
                          │    Direction    │       │ ElevatorControl │
                          └─────────────────┘       └─────────────────┘
                               Enum (UP/DOWN)         (SCAN Algo + Heaps)
```
