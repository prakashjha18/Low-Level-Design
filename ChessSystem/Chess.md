# ♟️ Chess Game — Low-Level Design

## Problem Statement

> Design a classic game of Chess. The game should track the 8x8 Board, maintain the turn logic securely between two players, and validate specific movement rules for individual pieces. 

---

## 🎯 Interview Constraints & Strategy

A full Chess game has thousands of lines of logic (`En Passant`, `Castling`, `Checkmate calculations`). **Do not attempt to implement all 6 pieces.** The interviewer is watching how you structure your Object-Oriented design so that *adding* rules is trivial. Implementing 1 or 2 pieces cleanly perfectly demonstrates mastery.

---

## 🏗️ Core Design Visualized

### 1. Abstracting Movement Validation (Polymorphism)
- **Why?** Writing a giant `switch(piece)` inside the `ChessGame.makeMove()` function violates the Open-Closed Principle. If someone creates a "SuperKnight", the core Game class shouldn't need a code update.
- **How?** Every single piece class (`Knight`, `Bishop`) extends `Piece` and overrides `canMove(board, startSpot, endSpot)`. The Game controller simply calls `if(!sourcePiece.canMove()) { return false; }`. The logic is offloaded directly to the subject itself.

### 2. The Knight's Algorithm (L-Shape Logic)
```java
int xAbs = Math.abs(start.getX() - end.getX());
int yAbs = Math.abs(start.getY() - end.getY());
return xAbs * yAbs == 2;
```
> Interviewer Reaction: **"Wow."**
By doing `Math.abs`, you cover all 4 quadrants (top/bottom/left/right). By multiplying them, you enforce strictly that one side moved `2` grids and the other side moved `1` grid. `2 * 1 = 2`. The math absolutely proves the L-Shape uniformly without needing 8 nested IF statements.

### 3. The Coordinate Boundary (`Spot`)
- **Why?** Passing integers around (`makeMove(int x, int y, int x2, int y2)`) makes piece manipulation messy. 
- **How?** We bound it in a `Spot` object. A `Spot` permanently holds `x` and `y` and holds an optional `Piece`. When an attack happens, we simply overwrite the Piece reference natively on the target `Spot`. 

---

## 🗣️ Interview Talking Points

### The Complexity of Checkmate
> *"To calculate Checkmate in a real system, the logic becomes massive because you have to 'dummy test' every legal move a specific player can make, and after making it, verify if their King is still attacked. I would design the `Board` class to support deep-cloning or undo hooks, so the `GameStatus` engine can iteratively simulate $O(N)$ variations securely before finalizing the real move."*

### Thread Safety (Concurrency)
> *"Since Chess is fundamentally a sequential, turn-based state machine, wrapping the `makeMove()` function with `synchronized` guarantees that network latency won't allow two players clicking rapidly to corrupt the multi-dimensional tracking arrays."*

---

## UML Quick Sketch

```text
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     Player      │  ◇────│    ChessGame    │ ─────▷│      Move       │
└─────────────────┘       └─────────────────┘       └─────────────────┘
                                   ◇ (State)
                                   │
                                   ▼
                          ┌─────────────────┐       ┌─────────────────┐
                          │      Board      │◇──────│      Spot       │
                          └─────────────────┘       └─────────────────┘
                                                             ◇
                                                             │
                                   ┌─────────────────────────▼
                                   │       Piece (Abstract)
                                   └─────────────────────────
                                     ┌─────┴──────┐     
                                  ┌─────┐      ┌─────┐   
                                  │Knight│     │Pawn │   
                                  └─────┘      └─────┘  
```
