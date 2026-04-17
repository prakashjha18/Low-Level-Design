package ChessSystem;

/**
 * Value object recording history and state of a singular movement action.
 */
public class Move {
    public final Player player;
    public final Spot start;
    public final Spot end;
    public final Piece pieceMoved;
    public final Piece pieceKilled;

    public Move(Player player, Spot start, Spot end) {
        this.player = player;
        this.start = start;
        this.end = end;
        this.pieceMoved = start.getPiece();
        this.pieceKilled = end.getPiece(); // null if empty
    }

    public Piece getPieceKilled() {
        return pieceKilled;
    }

    @Override
    public String toString() {
        return player.getName() + " moved " + pieceMoved.getClass().getSimpleName() + 
               " from (" + start.getX() + "," + start.getY() + ") to (" + 
               end.getX() + "," + end.getY() + ")" + 
               (pieceKilled != null ? " [KILLED " + pieceKilled.getClass().getSimpleName() + "]" : "");
    }
}
