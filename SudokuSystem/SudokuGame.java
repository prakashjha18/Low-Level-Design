package SudokuSystem;

public class SudokuGame {
    private final Board board;

    public SudokuGame(int[][] initialBoardMatrix) {
        this.board = new Board(initialBoardMatrix);
    }

    public void displayGame() {
        board.display();
    }

    /**
     * User attempt to place a number
     */
    public boolean makeMove(int row, int col, int value) {
        System.out.println("🕹️ Attempting to place " + value + " at [" + row + "][" + col + "]...");
        if (value < 1 || value > 9) {
            System.out.println("❌ Invalid. Sudoku only accepts 1-9.");
            return false;
        }

        if (board.setValue(row, col, value)) {
            System.out.println("✅ Move successful!");
            checkVictory();
            return true;
        } else {
            System.out.println("❌ Move failed. Placed number violates Sudoku rules.");
            return false;
        }
    }

    public void solveAutomatically() {
        System.out.println("🤖 Running Backtracking Solver...");
        SudokuSolver solver = new SudokuSolver();
        if (solver.solve(board)) {
            System.out.println("🎉 The computer solved the board!");
        } else {
            System.out.println("💀 This board configuration is unsolvable.");
        }
    }

    private void checkVictory() {
        if (board.isFull()) {
            System.out.println("🏆 CONGRATULATIONS! Sudoku Board is fully solved!");
        }
    }
}
