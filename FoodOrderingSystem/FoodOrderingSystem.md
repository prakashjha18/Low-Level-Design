# 🍔 Food Ordering Platform (Swiggy / Zomato) — Interview Optimized

## Problem Statement

> Design a Food Ordering system where a user can place an order, and the system progresses through varying real-time statuses (`PLACED`, `PREPARING`, `OUT_FOR_DELIVERY`, `DELIVERED`).

---

## 🎯 Interview Approach (Maximized for 45-Minute Coding)

A full-fledged Food Ordering system uses a deeply networked `Observer Pattern` coupled with a massive `State Pattern` (having 6 different classes for State behavior). **If you attempt to write a 14-file OOP hierarchy in a 45-minute coding interview, you will run out of time.**

This architecture is deliberately compacted into **6 files**. It demonstrates robust state enforcement, data decoupling, and clean object relations while remaining physically codable within a tightly timed whiteboard/IDE session.

---

## 🏗️ Core Design Strategies

### 1. The Facade Pattern (`OrderService.java`)
- **Why?** Instead of writing Observers/Subjects, which creates 3 extra files, we centralize the "Action" inside a Singleton Service layer. 
- **How?** `OrderService` natively manages the transitions. Whenever `updateOrderStatus` is called, it inherently triggers the `notifyCustomer()` method. It behaves like an Observer visually to the user, without the heavy interface implementation overhead. 

### 2. Strict State-Machine Enforcement
- You cannot jump backwards functionally unless authorized. 
- **Late Cancellation Lock:** If `status == PREPARING`, the system natively rejects `$cancelRequest()`. An interviewer is specifically looking for this logic trap. Passing it proves business-logic awareness.
- **Terminal Lock:** Once an order hits `DELIVERED` or `CANCELLED`, it is permanently locked out of further state mutations.

### 3. Aggregation (`Order` -> `Restaurant` & `MenuItem`)
- An Order does not own the objects. It uses a `Map<MenuItem, Integer>` to track the quantity of items purchased decoupled from the physical `Restaurant` instance yielding extreme memory-efficiency.

---

## 🗣️ Interview Talking Points

### "If you had more time, how would you upgrade the State Logic?"
> *"Currently, the status changes are handled via `if-else` guard clauses inside the Service class based on an Enum mapping. If the application states grew (e.g. `QUALITY_CHECK`, `DRIVER_ASSIGNED`), these `if-statements` would violate the Open/Closed Principle. I would refactor the Enum into a formal `State Pattern` where each Status is its own Class (`PlacedState.java`) dictating its distinct transition rules independently."*

### "How would you handle Pricing scaling? (Surge / Discounts)"
> *"Right now, `Order.calculateTotal()` executes a hardcoded multiplication rule. To handle Surge Pricing or Promocodes cleanly, I would implement the **Strategy Pattern**. I'd inject an interface `PricingStrategy` into the checkout flow so that varying algorithms could be hot-swapped without altering the Order entity."*

---

## UML Quick Sketch

```text
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│  OrderService   │◇──────│      Order      │◇──────│   OrderStatus   │
└─────────────────┘       └─────────────────┘       └─────────────────┘
  (Singleton Core)      (Tracks Total/Status)           Enum Tracker
                                   ◇
                                   │ delegates
                                   ▼
                          ┌─────────────────┐       ┌─────────────────┐
                          │   Restaurant    │◇──────│    MenuItem     │
                          └─────────────────┘       └─────────────────┘
```
