# 🚗 Parking Lot System — Low-Level Design

## Problem Statement

> Design a Parking Lot System. The parking lot has multiple levels, multiple vehicle types (Compact, Large), and issues tickets upon entry. On exit, it calculates a fee based on the vehicle type and duration of stay.

---

## 🎯 Interview Approach (Maximized for 45-Mins)

The parking lot is the most asked LLD problem of all time. You cannot get bogged down building generic entry/exit gate architectures. You must focus entirely on:
1. Object-Oriented Spot Allocation (`canFitVehicle()`).
2. Concurrency Safety (`synchronized` methods).
3. Strategy Pattern (Extensible Pricing rules).

---

## 🏗️ Core Design Strategies

### 1. `ParkingSpot.canFitVehicle()` (The Magic Method)
- **Why?** Many candidates write `if-else` loops heavily checking `if vehicle == CAR` or `if vehicle == BIKE`. This violates the Open-Closed Principle (OCP).
- **How?** The validation logic is cleanly localized inside the `ParkingSpot` class. A `LARGE` spot can fit a `TRUCK` *and* a `CAR`. But a `COMPACT` spot strictly assumes a `CAR`. The board logic simply asks the spot: `canFitVehicle(vehicle)`. If yes, we park it.

### 2. Singleton Facade (`ParkingLotManager`)
- **Why?** There shouldn't be multiple instances of the system issuing overlapping tickets or attempting to allocate the identical spot from memory simultaneously. 
- **How?** `getInstance()` ensures a single point of entry. Furthermore, `parkVehicle` and `unparkVehicle` are marked with `synchronized` to handle exact thread-race conditions natively. 

### 3. Startegy Pattern (`PricingStrategy`)
- **Why?** Pricing for airports differs completely from regular malls. The pricing algorithm varies entirely from the Parking Lot data structure.
- **How?** Created an interface `PricingStrategy` implemented by `HourlyPricingStrategy`. If the mall asks for "First hour free", we create `FreeFirstHourPricingStrategy` without editing the core.

---

## 🔐 Edge Cases Handled Confidently

| Edge Case | Solution implemented |
|-----------|----------------------|
| **Overflow Capacity** | Correctly outputs error and returns `null` ticket if no valid spots remain across ALL levels. |
| **Space Reuse Efficiency** | A `CAR` correctly parks in a `LARGE` spot if no `COMPACT` spots remain, maximizing inventory yield. |
| **Double Booking (Concurrency)** | The `parkVehicle` logic locks the manager instance using `synchronized` so two cars don't steal the exact same spot millisecond-by-millisecond. |

---

## UML Quick Sketch

```text
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│ ParkingLotMgr   │◇──────│  ParkingLevel   │◇──────│  ParkingSpot    │
└─────────────────┘       └─────────────────┘       └─────────────────┘
 (Singleton Core)                │                           ◇
         │ delegates             │                           │
         ▼                       ▼                           ▼
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│ PricingStrategy │       │     Ticket      │       │     Vehicle     │
└─────────────────┘       └─────────────────┘       └─────────────────┘
         △                                                   △
         │                                                   │
┌─────────────────┐                           ┌──────────────┴──────────┐
│  HourlyPricing  │                           │          │              │
└─────────────────┘                           ▼          ▼              ▼
                                           ┌─────┐    ┌─────┐        ┌─────┐
                                           │ Car │    │Truck│        │Bike │
                                           └─────┘    └─────┘        └─────┘
```
