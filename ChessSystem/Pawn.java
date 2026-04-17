package ChessSystem;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        if (end.getPiece() != null && end.getPiece().getColor() == this.getColor()) {
            return false; // Can't eat own team
        }

        int xDiff = end.getX() - start.getX();
        int yDiff = end.getY() - start.getY();

        // White moves up (negative X index depending on array setup, assuming 0 is Top and 7 is Bottom)
        // Actually, let's keep it simple. Assuming pawns only move 1 step straight.
        // Interview tip: You don't need to perfect the En Passant or first-jump logic.
        // Just prove you understand how to split math by Color.
        
        int forwardDirection = (this.getColor() == Color.WHITE) ? -1 : 1;

        // Standard forward move by 1 (MUST be empty)
        if (yDiff == 0 && xDiff == forwardDirection && end.getPiece() == null) {
            return true;
        }

        // Diagonal kill move
        if (Math.abs(yDiff) == 1 && xDiff == forwardDirection && end.getPiece() != null) {
            return true; // Enemy exists here, valid kill!
        }

        return false;
    }

    @Override
    public String toString() {
        return getColor() == Color.WHITE ? "♙" : "♟";
    }
}
