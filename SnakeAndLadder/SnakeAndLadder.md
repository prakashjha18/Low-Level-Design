# 🐍🪜 Snake & Ladder — Low-Level Design (Interview Optimized)

## Problem Statement

> Design a multiplayer Snake & Ladder game. The board varies in size. The game has snakes that bring a player down and ladders that take a player up. The game runs turn-by-turn. The first player to reach the final cell exactly wins.

---

## 🎯 Interview Constraints (45 Mins)

If you over-engineer this (adding State patterns, Observers, multiple interface implementations), you will **run out of time**. The goal of this LLD is to show that you can write **clean, highly functional, object-oriented code** quickly while handling the core edge cases perfectly.

---

## 🏗️ Core Entities

1. **`Player`**: POJO holding ID, Name, and `currentPosition`.
2. **`Dice`**: Handles random rolling. Injecting `numberOfDice` parameterizes it so it's flexible for the future.
3. **`Jumper` (The "Aha!" Moment)**: 
   - Instead of writing a `Snake` class and a `Ladder` class, we write a single `Jumper` class containing a `startPoint` and `endPoint`. 
   - A snake is just a jump where `start > end`. A ladder is just a jump where `start < end`.
   - **Why interviewers love this:** It massively reduces code duplication and simplifies board lookups entirely.
4. **`Board`**: Holds the board size and an O(1) `HashMap<Integer, Jumper>` to instantly resolve if a cell has a snake or ladder.
5. **`Game`**: The orchestrator. It uses a **Queue** to rotate the turns between players cleanly.

---

## 🗣️ How to explain this in an Interview

### 1. Handling Turns (The Queue approach)
**Candidate Script:** 
> *"To handle turn-management between an arbitrary number of players, I'll use a `Queue`. The top player gets polled off the queue, takes their turn, and if they didn't win the game, they are pushed back to the bottom of the queue. If they win, they are left out of the queue. This is drastically cleaner than using an array and maintaining a `currentPlayerIndex` variable which causes off-by-one errors."*

### 2. Handling Snakes vs Ladders (The Jumper approach)
**Candidate Script:**
> *"Often people create abstract entities for 'BoardItem' and extend it into Snakes and Ladders. I think that's overkill. Structurally, a Snake and a Ladder do the exact same thing: they instantly transport a player from Point A to Point B. I've designed a `Jumper` class. We store these in a map on the `Board`. When a player lands on `NextPos`, we just query the map: `board.get(NextPos)`. This handles traversing UP and DOWN completely seamlessly in O(1) time."*

### 3. Handling Dice Constraints
**Candidate Script:**
> *"The `Dice` class is abstracted so that we initialize it with `numberOfDice`. If the rule later says 'roll 2 dice', my codebase requires zero changes to the `Game` loops—the change is entirely contained inside `Dice.roll()`."*

---

## 🔐 Edge Cases Handled Confidently

| Edge Case | Solution |
|-----------|----------|
| **Overshooting the finish line** | Checked with `nextPos > boardSize`. The move is invalidated and turn is skipped. |
| **Invalid Snakes/Ladders** | Validations in `Board.addJumper()` ensure entities can't start at index 1 or the winning node. |
| **Two snakes on one box** | Caught during initialization. We use a HashMap (`HashMap::containsKey()`) which enforces exactly one jumper per square! |

## UML Quick Sketch

```text
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     Player      │       │      Game       │◆──────│      Dice       │
└─────────────────┘       └─────────────────┘       └─────────────────┘
                                   ◇
                                   │ Queue
                                   ▼
┌─────────────────┐       ┌─────────────────┐
│     Jumper      │◁──────│      Board      │
└─────────────────┘       └─────────────────┘
                            (stores map of 
                             spot -> jumper)
```
