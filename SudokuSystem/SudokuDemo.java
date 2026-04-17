package SudokuSystem;

public class SudokuDemo {
    public static void main(String[] args) {
        System.out.println("\n=== 🧩 Sudoku LLD Demo ===\n");

        int[][] puzzleGrid = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        SudokuGame game = new SudokuGame(puzzleGrid);
        
        System.out.println("Initial Board state:");
        game.displayGame();

        System.out.println("\n--- 1. Making Legal Move ---");
        // Row 0, Col 2 -> The number 4 does NOT exist in the 1st row, 3rd column, or top-left box.
        game.makeMove(0, 2, 4);

        System.out.println("\n--- 2. Making Illegal Move (Row Collision) ---");
        // Placing a 5 at (0,3). Row 0 already contains a 5.
        game.makeMove(0, 3, 5);

        System.out.println("\n--- 3. Letting the Board Auto-Solve ---");
        game.solveAutomatically();
        
        System.out.println("\nFinal Solved Board:");
        game.displayGame();
    }
}
