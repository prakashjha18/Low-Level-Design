# 🧩 Sudoku System — LLD Interview Guide

### 1. 🧾 Problem Statement
**Objective**: Design a Sudoku Game capable of tracking a 9x9 board, validating mathematical requirements natively, and executing an algorithmic solver to solve any valid puzzle.

**Functional Requirements**:
- Maintain a 9x9 uniform grid.
- Users can place a number (1-9) at any coordinate (x,y).
- The system must mathematically validate placing a number (it must not already exist in the target row, column, or 3x3 sub-region).
- The system must provide an auto-solve feature.

**Non-Functional Requirements**:
- **Separation of Concerns**: The back-tracking logic must not bloat the physical board.
- **Performance**: Board validation must execute as closely to $O(1)$ as possible since it will be called exponentially during the solving tree.

---

### 2. 🚀 How to Start in an Interview
When the interviewer proposes "Sudoku":
* **Clarifying Questions**: "Are we designing the UI rendering, or strictly the memory state and algorithmic solver?" (Always scope to memory/state to save time). "Do we need to validate initial board solvability, or simply validate active moves?"
* **Assumptions**: Assume a standard 9x9 grid. Assume the input puzzle is validly structured.
* **Scoping**: State clearly that you will start by creating a naive 2D `int[][]` integer matrix, but structure your classes so the validation engine is completely separated from the backtracking engine.

---

### 3. 🧩 Core Entities & Responsibilities
* **`Board`**: Stores the 2D grid matrix. Exclusively responsible for enforcing grid boundaries and tracking positional state tracking (`grid[row][col]`).
* **`SudokuGame` (Controller)**: Manages player interaction. Validates simple inputs (e.g., `< 1` or `> 9`) and proxies the placement execution directly to the `Board`.
* **`SudokuSolver`**: A dedicated algorithmic service. Exclusively responsible for executing the recursive Depth First Search (Backtracking) routine to map empty cells.

---

### 4. 🔗 Class Relationships
* `SudokuGame` **HAS-A** `Board` (Composition). The game orchestrates a single board's lifecycle.
* `SudokuGame` **USES** `SudokuSolver` (Dependency). The game dynamically spawns the solver service internally when a user clicks "Auto Solve".
* `SudokuSolver` **USES** `Board` (Dependency Injection). The solver operates directly upon the board matrix injected into its constructor/method.

```text
┌─────────────────┐       ┌─────────────────┐
│     Board       │◀──────│   SudokuGame    │
└─────────────────┘       └─────────────────┘
         △                         ◇
         │                         │ Uses
         │                         ▼
         │                ┌─────────────────┐
         └─ Mutates ──────│  SudokuSolver   │
                          └─────────────────┘
```

---

### 5. 🏗️ Design Patterns Used
* **Facade Pattern (`SudokuGame`)**: The `SudokuGame` acts as a unified interface to the client. The client doesn't need to manually talk to the `Board` or coordinate the `SudokuSolver`; they simply invoke `game.makeMove()` or `game.solveAutomatically()`.
* **Service / Strategy Pattern (`SudokuSolver`)**: The Backtracking algorithm is extracted into its own stateless service class. This solves the problem of "God Classes" where candidates put GUI logic, grid storage, and 100 lines of recursion into a single `Board.java` file.

---

### 6. 🧠 SOLID Principles Mapping
* **Single Responsibility Principle (SRP)**: Perfect adherence. `Board` stores data. `SudokuSolver` solves puzzles. `SudokuGame` routes client traffic.
* **Open/Closed Principle (OCP)**: By isolating `SudokuSolver`, if we wanted to add a purely mathematical heuristic solver (like Peter Norvig's constraint propagation algorithm), we simply implement a new solver without editing `Board.java`.
* **Dependency Inversion Principle (DIP)**: Currently uses hard Object references. **Improvement point**: `SudokuGame` could depend on a `Solver` interface rather than the concrete `SudokuSolver` class.

---

### 7. ⚙️ Key Workflows

**The Backtracking Execution:**
1. `SudokuSolver.solve(board)` receives the grid.
2. Iterates matrix to find the first `0` (Empty Cell).
3. Executes a `for (1 to 9)` loop.
4. Checks `board.isValidMove(r, c, num)`.
5. If valid, natively assigns `grid[r][c] = num`.
6. Recurses standardly: `if (solve(board)) return true;`.
7. **BACKTRACK**: If recursion fails, resets `grid[r][c] = 0` and attempts the next number.

---

### 8. 🗄️ Data Structures & Design Choices
* **2D Primitive Array (`int[][] grid`)**:
  * **Why:** Speed. Boxing memory overheads from `ArrayList<ArrayList<Integer>>` are disastrous for deep recursive backtracking routines. Pure primitive matrices utilize spatial CPU cache locality perfectly.
  * **Trade-off:** Static sizing. If we wanted an expandable Sudoku (16x16), dynamic expansion becomes slightly harder than lists, though we can parameterize `SIZE`.

---

### 9. ⚠️ Edge Cases & Failure Handling
* **Array Index Out of Bounds**: Handled safely because limits are strictly bounded to `SIZE = 9`.
* **Inputting Invalid Numbers**: `SudokuGame` intercepts numbers `>= 10` or `<= 0` natively blocking memory corruption.
* **Unsolvable Puzzle**: The recursive fallback safely breaks execution returning `false` rather than encountering Infinite Loops.

---

### 10. 📈 Scalability & Extensibility
* **Scaling Validation Performance (Bitmasking)**: Scanning arrays on every move `O(N)` is slow. To scale, we swap `if/for` validations to **Integer Bitmasking**. 
  * Replace scanning with 3 arrays: `int[] rowBits`, `int[] colBits`, `int[] boxBits`. 
  * Validating a move dynamically becomes purely Bitwise operations: `(rowBits[r] & (1 << val)) == 0`. This reduces matrix operations identically to machine code operations.

---

### 11. 🧵 Concurrency Considerations
* Standard Sudoku logic is single-threaded. 
* If this became a Multiplayer competitive match tracking server:
  * Two users trying to invoke `game.makeMove()` exactly simultaneously could corrupt the 2D primitive array.
  * **Strategy**: Synchronize the `makeMove` block or use `java.util.concurrent.locks.ReentrantLock` on the `Board` resource to ensure strict thread-fencing.

---

### 12. 🧼 Code Quality Review
* **Strengths**: Incredibly decoupled. Clean recursive backtracking design logic. Safe array copy initialization.
* **Weaknesses**: Finding the `3x3` box in `isValidMove` uses integer division rounding loops which costs minor CPU cycles recursively.
* **Refactoring**: Implement the Bitmasking methodology mentioned in Section 10 to vastly improve algorithmic big-O constants.

---

### 13. 🎤 How to Explain This in an Interview
**High-Level Pitch (3 Mins):**
> "I designed this system by strictly isolating memory from algorithmic load. The `Board` holds state and validation natively. The `SudokuGame` is a Facade exposing user controls. Finally, I abstracted the heavily recursive Backtracking architecture into a `SudokuSolver` service. I specifically chose pure primitive integer maps over Lists to maximize CPU cache locality, as the recursion tree branches horizontally exponentially and boxing overheads would kill latency."

**What to Emphasize:**
Emphasize the **3x3 Sub-grid validation math**. Walking them through `rootRow = row - row % 3` natively proves you know how to map raw 2D algebra bounds dynamically.

---

### 14. 🧪 Follow-up Questions & Answers
**Q: What is the Big-O Time Complexity of your Solver?**
* **A:** "$O(9^m)$, where `m` equals the physically empty spaces. Since every empty space can maximally branch out into 9 different possibilities, the worst-case exploration runs 9 times for every zero natively."

**Q: How do you validate the 3x3 box strictly without out-of-bounds metrics?**
* **A:** "I use Modulo arithmetic. By subtracting `row % 3`, the engine dynamically rounds any coordinate backwards identically to the literal `[0,0]`, `[0,3]`, or `[0,6]` origin block without utilizing giant IF structures."

---

### 15. 🧱 Step-by-Step Coding Approach
If executing strictly from scratch under 45 minutes:
1. **Define Grid POJO**: Write `Board.java`. Assign the constructor to duplicate `int[][]` via deep copy.
2. **Write Validator**: Build `isValidMove()` checking exactly Rows, Columns, and Boxes heavily. (The interviewer will watch this intensely).
3. **Write DFS Engine**: Create `SudokuSolver.java`. Construct the nested loops finding `0`. Scaffold the recursive template. **Don't forget the Backtrack constraint `grid = 0`**.
4. **Wire It**: Create `SudokuGame.java` to link the objects and invoke execution natively.
