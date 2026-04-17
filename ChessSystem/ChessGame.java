package ChessSystem;

import java.util.ArrayList;
import java.util.List;

public class ChessGame {
    private final Player[] players;
    private final Board board;
    private Player currentTurn;
    private final List<Move> movesPlayed; // Tracks completely executed moves

    public ChessGame(Player p1, Player p2) {
        this.players = new Player[]{p1, p2};
        this.board = new Board();
        this.movesPlayed = new ArrayList<>();
        
        // White traditionally moves first in Chess
        this.currentTurn = (p1.getColor() == Color.WHITE) ? p1 : p2;
    }

    public Board getBoard() { return board; }

    public boolean playerMove(Player player, int startX, int startY, int endX, int endY) {
        System.out.println("🕹️ " + player.getName() + " attempts to move (" + startX + "," + startY + ") -> (" + endX + "," + endY + ")");
        
        Spot startBox = board.getSpot(startX, startY);
        Spot endBox = board.getSpot(endX, endY);
        Move move = new Move(player, startBox, endBox);

        return makeMove(move);
    }

    private boolean makeMove(Move move) {
        Piece sourcePiece = move.pieceMoved; // From Move creation
        
        // Validation Checks
        if (sourcePiece == null) {
            System.out.println("❌ Move failed: No piece at the starting spot.");
            return false;
        }

        if (move.player != currentTurn) {
            System.out.println("❌ Move failed: It is not " + move.player.getName() + "'s turn.");
            return false;
        }

        if (sourcePiece.getColor() != currentTurn.getColor()) {
            System.out.println("❌ Move failed: You cannot move the opponent's piece.");
            return false;
        }

        // Delegate exact mathematical matrix checks directly to the Piece itself! (Polymorphism)
        if (!sourcePiece.canMove(board, move.start, move.end)) {
            System.out.println("❌ Move failed: Invalid path geometry for " + sourcePiece.getClass().getSimpleName());
            return false;
        }

        // --- Execute the Move Structurally ---
        Piece destPiece = move.end.getPiece();
        if (destPiece != null) {
            destPiece.setKilled(true); // Mark as killed
        }

        // Transfer the piece in the matrix securely
        move.end.setPiece(sourcePiece);
        move.start.setPiece(null);

        movesPlayed.add(move);
        System.out.println("✅ " + move.toString());

        // Switch purely binary turns
        currentTurn = (currentTurn == players[0]) ? players[1] : players[0];

        return true;
    }
}
