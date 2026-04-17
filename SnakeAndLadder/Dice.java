package SnakeAndLadder;

import java.util.concurrent.ThreadLocalRandom;

public class Dice {
    private final int numberOfDice;

    public Dice(int numberOfDice) {
        this.numberOfDice = numberOfDice;
    }

    /**
     * Rolls all dice and returns the total sum.
     */
    public int roll() {
        int totalSum = 0;
        int min = 1;
        int max = 6;
        
        for (int i = 0; i < numberOfDice; i++) {
            totalSum += ThreadLocalRandom.current().nextInt(min, max + 1);
        }
        
        return totalSum;
    }
}
