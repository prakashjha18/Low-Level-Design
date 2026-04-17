package ChessSystem;

public class ChessDemo {
    public static void main(String[] args) {
        System.out.println("\n=== ♟️ Chess Game LLD Demo ===\n");

        Player p1 = new Player("Alice", Color.WHITE);
        Player p2 = new Player("Bob", Color.BLACK);

        ChessGame game = new ChessGame(p1, p2);
        
        System.out.println("Initial Board State:");
        game.getBoard().display();

        // 1. Move White Pawn forward legally
        game.playerMove(p1, 6, 0, 5, 0); 
        System.out.println("\nBoard after Move 1:");
        game.getBoard().display();

        // 2. Try moving Black Piece out of turn (Should fail)
        game.playerMove(p1, 1, 0, 2, 0); // Alice tries to move Bob's pawn

        // 3. Move White Knight in invalid L-Shape (Should fail)
        // Correct Knight move from 7,1 is 5,0 or 5,2
        game.playerMove(p2, 7, 1, 6, 1); 

        // 4. Move Black Knight properly (Should pass)
        // Black Knight from 0,1 jumping to 2,0
        game.playerMove(p2, 0, 1, 2, 0);
        System.out.println("\nBoard after Move 4:");
        game.getBoard().display();
    }
}
