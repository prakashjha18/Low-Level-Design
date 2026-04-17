package SudokuSystem;

/**
 * Service specifically engineered to handle Backtracking logic.
 * Decoupled from the Board so the Board isn't bloated with recursive overhead.
 */
public class SudokuSolver {

    /**
     * Mutates the board dynamically to find a solved state.
     * Backtracking Algorithm (O(9^m) where m is the number of empty cells)
     */
    public boolean solve(Board board) {
        int[][] grid = board.getGrid();
        int size = board.getSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                
                // If it's an empty cell
                if (grid[r][c] == 0) {
                    // Try massive variations
                    for (int n = 1; n <= 9; n++) {
                        if (board.isValidMove(r, c, n)) {
                            grid[r][c] = n; // Commit choice

                            if (solve(board)) { // Drill deep
                                return true; // Successfully solved the subtree
                            } else {
                                grid[r][c] = 0; // Backtrack! Choice failed down the line
                            }
                        }
                    }
                    return false; // Exhausted 1-9 and couldn't solve. Return up.
                }
            }
        }
        return true; // Execution reached the end without finding any '0', meaning solved!
    }
}
