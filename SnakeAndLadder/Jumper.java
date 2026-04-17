package SnakeAndLadder;

/**
 * 🎯 Interview Pro-Tip:
 * Instead of creating separate 'Snake' and 'Ladder' classes, recognize that they
 * functionally do the exact same thing: jump the player from a Start point to an End point.
 * Snake: start > end
 * Ladder: start < end
 */
public class Jumper {
    private final int startPoint;
    private final int endPoint;

    public Jumper(int startPoint, int endPoint) {
        if (startPoint == endPoint) {
            throw new IllegalArgumentException("Start and End point cannot be the same.");
        }
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public int getStartPoint() { return startPoint; }
    public int getEndPoint() { return endPoint; }
    
    public boolean isSnake() {
        return startPoint > endPoint;
    }
    
    public boolean isLadder() {
        return startPoint < endPoint;
    }
}
