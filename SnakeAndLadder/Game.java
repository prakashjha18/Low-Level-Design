package SnakeAndLadder;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Game {
    private final Board board;
    private final Dice dice;
    private final Queue<Player> playersQueue; // Queue handles Turn-by-Turn logic elegantly

    public Game(Board board, Dice dice, List<Player> players) {
        this.board = board;
        this.dice = dice;
        this.playersQueue = new LinkedList<>(players);
    }

    public void start() {
        System.out.println("\n🎲 Game Started!\n");

        while (playersQueue.size() > 1) { // Stop when only 1 player remains (they lost)
            Player currentPlayer = playersQueue.poll();
            
            int diceRoll = dice.roll();
            int currentPos = currentPlayer.getCurrentPosition();
            int nextPos = currentPos + diceRoll;

            System.out.println("▶ " + currentPlayer.getName() + " rolled a " + diceRoll + 
                               " (Current pos: " + currentPos + ")");

            // Check if player jumped out of board boundary
            if (nextPos > board.getSize()) {
                System.out.println("   ❌ Invalid move. Needs exactly " + (board.getSize() - currentPos) + " to win. Staying at " + currentPos);
                playersQueue.offer(currentPlayer); // Put back in queue
                continue;
            }

            // Check for snakes or ladders
            nextPos = board.getFinalPosition(nextPos);
            currentPlayer.setCurrentPosition(nextPos);
            System.out.println("   📍 " + currentPlayer.getName() + " moved to " + nextPos);

            // Check Win Condition
            if (nextPos == board.getSize()) {
                System.out.println("🎉🎉🎉 " + currentPlayer.getName() + " HAS WON THE GAME! 🎉🎉🎉\n");
                // Do not add them back to the queue
            } else {
                playersQueue.offer(currentPlayer); // Add back to queue for next turn
            }
        }

        if (!playersQueue.isEmpty()) {
            System.out.println("💀 " + playersQueue.poll().getName() + " lost the game.");
        }
    }
}
