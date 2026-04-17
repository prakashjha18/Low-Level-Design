package ChessSystem;

public abstract class Piece {
    private final Color color;
    private boolean isKilled;

    public Piece(Color color) {
        this.color = color;
        this.isKilled = false;
    }

    public Color getColor() { return color; }
    public boolean isKilled() { return isKilled; }
    public void setKilled(boolean killed) { isKilled = killed; }

    /**
     * Determines whether the piece can legally traverse from Start to End.
     * Note: This does NOT execute the move, merely validates mathematical bounding.
     */
    public abstract boolean canMove(Board board, Spot start, Spot end);
}
