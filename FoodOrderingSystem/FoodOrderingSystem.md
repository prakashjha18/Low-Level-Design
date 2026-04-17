# 🍔 Food Ordering Platform (Swiggy / Zomato) — Low-Level Design

## Problem Statement

> Design a Food Ordering system where a user can browse a restaurant's menu, place an order, get assigned a delivery executive, and track the real-time status of their order.

---

## Requirements

### Functional Requirements
1. **Restaurant Management**: Restaurants have Menus containing MenuItems.
2. **Order Placement**: Users can create an Order with multiple items.
3. **Pricing Strategy**: System uses Strategies (e.g., standard pricing, discount, surge) to calculate the total amount.
4. **Order Tracking**: The order goes through multiple states (`PLACED`, `PREPARING`, `OUT_FOR_DELIVERY`, `DELIVERED`).
5. **Real-time Notifications**: Users (and delivery executives) must be notified immediately when an order's status changes.

---

## 🏗️ Design Patterns Used

### 1. State Pattern (Order State Machine)
- **Why?** An Order has complex transition rules. For example, you can't cancel an order if it is `OUT_FOR_DELIVERY`. An order requires a `DeliveryExecutive` before it can transition from `PREPARING` to `OUT_FOR_DELIVERY`.
- **How?** The `Order` context object delegates behavior to concrete states: `PlacedState`, `PreparingState`, `OutForDeliveryState`, `DeliveredState`, and `CancelledState`.

### 2. Observer Pattern (Notification System)
- **Why?** Multiple parties (Customer, Delivery Executive, Restaurant app) need to know when the status of the order changes. Tight-coupling them to the `Order` class is bad.
- **How?** `Order` implements `OrderSubject`. `User` and `DeliveryExecutive` implement `OrderObserver`. When the order transitions from `PREPARING` to `OUT_FOR_DELIVERY`, it simply calls `notifyObservers()`. 

### 3. Strategy Pattern (Pricing)
- **Why?** Pricing logic changes frequently (Surge pricing, Flat rate delivery, applying Promo Codes).
- **How?** `PricingStrategy` interface has implementations like `StandardPricingStrategy` and `SurgePricingStrategy`.

---

## class Diagram / Core Entities

```text
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     User        │──────▷│     Order       │◁──────│    Restaurant   │
└─────────────────┘       └─────────────────┘       └─────────────────┘
         △                         ◇                         ◇
         │ implements              │                         │
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│  OrderObserver  │◁──────│    OrderState   │       │      Menu       │
└─────────────────┘       └─────────────────┘       └─────────────────┘
         △                         △                         ◇
         │ implements              │ implements              │
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│DeliveryExecutive│       │  PlacedState    │       │    MenuItem     │
└─────────────────┘       │  PreparingState │       └─────────────────┘
                          │ OutForDelivery  │
                          │ DeliveredState  │
                          └─────────────────┘
```

---

## 🔐 Edge Cases Handled Confidently

1. **Missing Delivery Executive:** A `PREPARING` order attempts to transition to `OUT_FOR_DELIVERY`. The state catches that `order.getDeliveryExecutive() == null` and blocks the transition, preventing real-world logistical errors.
2. **Late Cancellations:** A customer tries to call `cancelOrder()` while the order is `OUT_FOR_DELIVERY`. The specific state immediately rejects the cancellation request gracefully.
3. **Empty Orders:** Handled by validating `items.isEmpty()` in the `checkout()` method. Unavailability of an item throws an error when trying to add.

---

## 🗣️ Interview Talking Points

- **"I chose the State pattern over a giant switch-case because..."** -> It adheres strictly to the Open/Closed Principle. If Zomato adds a new state ("QUALITY_CHECK"), I just add a new `OrderState` class. The main `Order` class remains completely untouched.
- **"I decoupled notifications using the Observer Pattern because..."** -> The order processing logic shouldn't care *how* alerts are sent. We can add a `PushNotificationSystem` as an Observer tomorrow without changing Order logic.
- **"I isolated the pricing because..."** -> Calculating taxes and delivery fees often changes based on distance or weather. Passing a `PricingStrategy` makes the checkout system extremely extensible.
