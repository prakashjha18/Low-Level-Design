# ♟️ Chess System — LLD Interview Guide

### 1. 🧾 Problem Statement
**Objective**: Design a Low-Level system for a classic 2-player Chess game.
**Functional Requirements**:
- An 8x8 configuration grid holding pieces.
- Two distinct players with alternating turns.
- Mathematics handling legal movement definitions independently per piece (e.g. Knight travels in an L-Shape).
- Tracking moves and capturing pieces correctly.
**Non-Functional Requirements**:
- **Extensibility**: It must be simple to add new board pieces without editing core game flow.
- **Maintainability**: Turn logic must be fundamentally isolated from geometrical coordinate limits.

---

### 2. 🚀 How to Start in an Interview
* **Clarify Constraints**: Say: "Chess is gigantic. To write working execution logic within 45 minutes, I am going to limit the scope. I will NOT implement Checkmate simulations natively, Castling, or En Passant. I WILL implement core turns, coordinate bounding, and write polymorphic abstractions for 2 specific pieces: Knight and Pawn, to prove structural system mastery."
* **Identify Sub-Systems**: Outline that you will need coordinate bounds (`Spot`), pieces (`Piece`), a command tracker (`Move`), and an engine (`ChessGame`).

---

### 3. 🧩 Core Entities & Responsibilities
* **`Spot`**: A single grid coordinate `(x,y)`. Its single responsibility is verifying physical location boundaries and what object currently sits on it natively.
* **`Piece (Abstract)`**: The definitive template tracking `Color` and containing `abstract boolean canMove()`.
* **`Move`**: A Value Object acting identically to a database transaction log mapping precisely who moved where, and who was killed.
* **`ChessGame`**: A specialized facade managing boolean flags (Turns) and injecting coordinates securely into the mathematical evaluation pipelines.

---

### 4. 🔗 Class Relationships
* `Spot` **HAS-A** `Piece` (Aggregation). Pieces can exist independently but are held by spots.
* `Board` **HAS-A** `Spot` (Composition). A board permanently creates a 64-square `Spot[][]` matrix.
* `Knight` **IS-A** `Piece` (Inheritance).
* `ChessGame` **HAS-A** `Board` and **HAS-MANY** `Move` objects.

---

### 5. 🏗️ Design Patterns Used
* **Template Method / Strategy Pattern via Polymorphism**: Writing `abstract boolean canMove()` in `Piece`. The `ChessGame` class delegates movement rules entirely to the pieces themselves securely handling varying algorithms locally without Giant Switch blocks.
* **Command Pattern (Pseudo)**: The `Move` class encapsulates a complete transaction. This structure makes writing an "Undo" feature relatively flawless since state history is tracked locally.

---

### 6. 🧠 SOLID Principles Mapping
* **Open/Closed Principle (OCP)**: Supreme adherence. If the interviewer requests a new "Fairy Chess" piece, you create `SuperKnight.java extends Piece` without modifying `ChessGame` or `Board` code.
* **Single Responsibility Principle (SRP)**: `Board` maps grid memory. `ChessGame` handles rules and turns. Objects do not overlap scopes.

---

### 7. ⚙️ Key Workflows

**Executing a Move (`game.makeMove(x1, y1, x2, y2)`)**
1. The game maps primitive coordinates natively to localized `Spot` objects.
2. The game verifies business rules (Is it the user's turn? Do they own the originating piece?)
3. Polymorphic Call: `sourcePiece.canMove(board, start, end)` is triggered securely executing geometric bounds.
4. If valid, the target `Spot` sets its piece. The originating `Spot` resets to `null`.
5. The `currentTurn` is swapped. 

---

### 8. 🗄️ Data Structures & Design Choices
* **Primitive 2D Array `Spot[][]`**: Overwhelmingly out-performs `List<List<Spot>>` natively. Grids are uniformly 8x8 statically memory-allocated.
* **List `<Move>`**: Ordered sequence list. It operates as the definitive State Ledger natively allowing replay systems.

---

### 9. ⚠️ Edge Cases & Failure Handling
* **Out of Turn Inputs**: Intercepted and rejected efficiently inside `ChessGame`.
* **IndexOutOfBounds**: Guard clauses defensively check `$x < 0 || $x > 7` before performing any nested geometric lookups dynamically.
* **Friendly Fire**: `canMove()` natively intercepts if `destSpot.getPiece().color == source.color`.

---

### 10. 📈 Scalability & Extensibility
* **AI Integration**: To support Single Player, inject a player subclass: `AIPlayer extends Player` tracking its own `calculateNextMove()` tree directly decoupled from the game controller. 
* **State Checkmate Verification**: Adding Checkmate recursively mandates duplicating the `Board` memory deeply. The current Command pattern (`Move` history) makes snapshotting board arrays simpler.

---

### 11. 🧵 Concurrency Considerations
* Two users connecting over TCP/IP hitting endpoints: If both rapidly click `makeMove`, the 2D array coordinates corrupt. 
* Fix: The `playerMove` method definitively requires a `synchronized` blocking keyword so sequential states process identically locked to RAM state.

---

### 12. 🧼 Code Quality Review
* **Strengths**: Extensibility is legendary. Using `Math.abs(startX - targetX) * Math.abs(...) == 2` to algebraically validate Knight Moves natively prevents 8 redundant `IF` blocks perfectly. 
* **Improvements**: Using static arrays is fine, but separating out an `InputHandler` class exclusively for capturing XY primitives could shrink `ChessGame`.

---

### 13. 🎤 How to Explain This in an Interview
**High-Level Pitch:**
> "I designed this system utilizing powerful polymorphic abstractions natively bypassing giant nested evaluation loops. The `ChessGame` controller operates agnostically of piece shapes. It maps user input to transactions wrapped securely via a `Move` class. The true evaluation power exists strictly inside `canMove()` dynamically bounded directly inside physical piece inheritances, adhering beautifully to the Open/Closed Principle."

---

### 14. 🧪 Follow-up Q&A
**Q: How would you implement an "Undo Move" feature identically?**
* **A:** "Because I encapsulated every transaction inside a `Move` object holding the exact `pieceMoved` and `pieceKilled` values, Undo works seamlessly. I would pop the `Move` stack, safely re-apply `move.start.setPiece(moved)` and dynamically inject `move.end.setPiece(killed)` safely returning grid memory identically to 1 turn ago."

---

### 15. 🧱 Step-by-Step Coding Approach
1. Outline `Enum Color` and POJO `Player` securely.
2. Outline `Spot(x,y)` dynamically enforcing the Cartesian grid limits.
3. Build the `Piece` Abstract boundary securely utilizing the `abstract canMove(...)` polymorphic header. 
4. Implement `Knight` geometry explicitly implementing the `1 * 2 = 2` absolute multiplier natively proving L-shape logic perfectly. 
5. Write the `ChessGame` orchestrating standard `makeMove()` conditions securely.
