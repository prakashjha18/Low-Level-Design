package SnakeAndLadder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private final int size;
    private final Map<Integer, Jumper> jumpers; // Map of startPoint -> Jumper

    public Board(int size) {
        this.size = size;
        this.jumpers = new HashMap<>();
    }

    public int getSize() { return size; }

    public void addJumper(Jumper jumper) {
        // Validation: No jumper should start from start(1) or end(size) of board
        if (jumper.getStartPoint() <= 1 || jumper.getStartPoint() >= size) {
            throw new IllegalArgumentException("Jumper cannot start at beginning or end of board.");
        }
        // Validation: No two jumpers should start at the same point
        if (jumpers.containsKey(jumper.getStartPoint())) {
            throw new IllegalArgumentException("A jumper already exists at point " + jumper.getStartPoint());
        }
        
        jumpers.put(jumper.getStartPoint(), jumper);
    }
    
    public void addJumpers(List<Jumper> jumperList) {
        for (Jumper j : jumperList) {
            addJumper(j);
        }
    }

    /**
     * Given an initial position, it checks if there is a snake or ladder and computes the final position.
     */
    public int getFinalPosition(int position) {
        if (jumpers.containsKey(position)) {
            Jumper jumper = jumpers.get(position);
            if (jumper.isSnake()) {
                System.out.println("   🐍 Oh no! Swallowed by a snake at " + position + " -> going down to " + jumper.getEndPoint());
            } else {
                System.out.println("   🪜 Yay! Climbed a ladder at " + position + " -> going up to " + jumper.getEndPoint());
            }
            return jumper.getEndPoint();
        }
        return position;
    }
}
