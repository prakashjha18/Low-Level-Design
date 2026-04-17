package ChessSystem;

public class Board {
    private final Spot[][] boxes;

    public Board() {
        this.boxes = new Spot[8][8];
        initializeBoard();
    }

    public Spot getSpot(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        return boxes[x][y];
    }

    /**
     * Resets the board and forcefully attaches Pieces to Spots securely.
     */
    private void initializeBoard() {
        // Place Black Pawns & Knights
        boxes[0][1] = new Spot(0, 1, new Knight(Color.BLACK));
        boxes[0][6] = new Spot(0, 6, new Knight(Color.BLACK));
        for (int i = 0; i < 8; i++) {
            boxes[1][i] = new Spot(1, i, new Pawn(Color.BLACK));
        }

        // Place White Pawns & Knights
        for (int i = 0; i < 8; i++) {
            boxes[6][i] = new Spot(6, i, new Pawn(Color.WHITE));
        }
        boxes[7][1] = new Spot(7, 1, new Knight(Color.WHITE));
        boxes[7][6] = new Spot(7, 6, new Knight(Color.WHITE));

        // Fill remaining empty spots
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boxes[i][j] == null) {
                    boxes[i][j] = new Spot(i, j, null);
                }
            }
        }
    }

    public void display() {
        System.out.println("  0 1 2 3 4 5 6 7");
        for (int i = 0; i < 8; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 8; j++) {
                Piece p = boxes[i][j].getPiece();
                if (p == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(p.toString() + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
