package ChessSystem;

public class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        // Prevent moving onto a spot occupied by our own team
        if (end.getPiece() != null && end.getPiece().getColor() == this.getColor()) {
            return false;
        }

        int xAbs = Math.abs(start.getX() - end.getX());
        int yAbs = Math.abs(start.getY() - end.getY());

        // The absolute mathematical beauty of the Knight's L-shape:
        // Any 2x1 or 1x2 movement multiplies strictly to 2!
        return xAbs * yAbs == 2;
    }
    
    @Override
    public String toString() {
        return getColor() == Color.WHITE ? "♘" : "♞";
    }
}
