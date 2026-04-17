# 🧩 Sudoku System — Low-Level Design

## Problem Statement

> Design a Sudoku Game. The system should manage a 9x9 board, validate user inputs, enforce Sudoku's row/column/sub-grid mathematical constraints exactly, and crucially, feature an automated solver via Backtracking.

---

## 🎯 Interview Approach (Maximized for Algorithm Discussion)

Designing Sudoku is a hybrid between highly clean OOP design and raw algorithmic capability. Interviewers ask for Sudoku not because they expect dozens of design patterns, but because they want to see **how beautifully you design the Backtracking Logic.**

---

## 🏗️ Core Design Strategies

### 1. `isValidMove(row, col, value)` (O(1)* Check)
- **Why?** Constantly scanning the board is heavy. We isolated pure Sudoku mathematical validity directly onto the `Board` class. 
- **Methodology:** We conduct three isolated checks continuously.
  1. Does `value` exist in `grid[row][0...8]`? 
  2. Does `value` exist in `grid[0...8][col]`?
  3. Finding the 3x3 box matrix. The formula `boxRowStart = row - (row % 3)` zeroes out the coordinate beautifully preventing array out of bounds.

### 2. Separation of Concerns (`Board` vs `SudokuSolver`)
- **Why?** Many candidates jam the Backtracking algorithm directly onto the `Board` classes making it bloated. 
- **How?** The `Board` is *dumb*. It only knows how to store integers safely. The `SudokuSolver` acts purely as an aggressive algorithmic Service that is injected a Board, and tries to bruteforce a mathematical win condition locally.

### 3. The Backtracking Masterclass
- A raw algorithmic recursive call that operates in $O(9^m)$ time format where `m` is the number of empty spots. 
- **The Core Loop:** 
  1. Find an empty cell (`0`)
  2. For `num` from `1` to `9`:
  3. If legal -> **Commit** the choice directly onto the array.
  4. Recurse entirely -> If `true`, the puzzle is solved.
  5. If `false` -> **Backtrack!** Undo the choice (`grid[r][c] = 0`).
  6. Return `false` and collapse the tree if `1-9` completely fail.

---

## 🗣️ Interview Talking Points

### Data Structure Optimization
> *"Currently, checking `isValidMove()` scans strings/arrays natively making it `O(N)` where N is 9. To optimize this to massive real-time `O(1)` validation, especially inside the core recursive loop, I would implement `Bitmasking`. By using three integer arrays (a `rows[]`, a `cols[]`, and a `boxes[]` array) acting as logical flags representing binary states `0000000000`, checking boolean validity goes straight to machine-code memory."*

## UML Quick Sketch

```text
┌─────────────────┐       ┌─────────────────┐
│     Board       │◀──────│   SudokuGame    │
└─────────────────┘       └─────────────────┘
         △                         ◇
         │                         │ Passes board to
         │                         ▼
         │                ┌─────────────────┐
         └────────────────│  SudokuSolver   │
         Validates via    └─────────────────┘
                           (Backtracking Engine)
```
