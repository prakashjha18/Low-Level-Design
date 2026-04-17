package SudokuSystem;

public class Board {
    private static final int SIZE = 9;
    private final int[][] grid;

    public Board(int[][] initialGrid) {
        if (initialGrid.length != SIZE || initialGrid[0].length != SIZE) {
            throw new IllegalArgumentException("Sudoku board must be 9x9");
        }
        
        // Deep copy to prevent external array mutation
        this.grid = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(initialGrid[i], 0, this.grid[i], 0, SIZE);
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getSize() {
        return SIZE;
    }

    /**
     * Set a value at a specific position.
     */
    public boolean setValue(int row, int col, int value) {
        if (isValidMove(row, col, value)) {
            grid[row][col] = value;
            return true;
        }
        return false;
    }

    /**
     * Validates if placing a number at (row, col) violates purely the board rules.
     */
    public boolean isValidMove(int row, int col, int value) {
        // 1. Check Row
        for (int c = 0; c < SIZE; c++) {
            if (grid[row][c] == value) {
                return false;
            }
        }

        // 2. Check Column
        for (int r = 0; r < SIZE; r++) {
            if (grid[r][col] == value) {
                return false;
            }
        }

        // 3. Check 3x3 Sub-box
        int boxRowStart = row - row % 3;
        int boxColStart = col - col % 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (grid[r][c] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isFull() {
        for (int[] row : grid) {
            for (int val : row) {
                if (val == 0) return false;
            }
        }
        return true;
    }

    public void display() {
        System.out.println("\n--- Sudoku Board ---");
        for (int r = 0; r < SIZE; r++) {
            if (r % 3 == 0 && r != 0) {
                System.out.println("---------------------");
            }
            for (int c = 0; c < SIZE; c++) {
                if (c % 3 == 0 && c != 0) {
                    System.out.print("| ");
                }
                System.out.print((grid[r][c] == 0 ? "." : grid[r][c]) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
