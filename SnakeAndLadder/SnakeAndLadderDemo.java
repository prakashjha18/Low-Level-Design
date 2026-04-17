package SnakeAndLadder;

import java.util.Arrays;

public class SnakeAndLadderDemo {
    public static void main(String[] args) {
        // 1. Setup Board (Size 100)
        Board board = new Board(100);

        // 2. Add Snakes and Ladders via Jumper class
        board.addJumpers(Arrays.asList(
            // Ladders (Start < End)
            new Jumper(4, 14),
            new Jumper(9, 31),
            new Jumper(21, 42),
            new Jumper(28, 84),
            new Jumper(51, 67),
            new Jumper(71, 91),
            
            // Snakes (Start > End)
            new Jumper(17, 7),
            new Jumper(54, 34),
            new Jumper(62, 19),
            new Jumper(64, 60),
            new Jumper(87, 24),
            new Jumper(93, 73),
            new Jumper(95, 75),
            new Jumper(99, 78)
        ));

        // 3. Setup Dice
        Dice dice = new Dice(1); // 1 normal dice

        // 4. Setup Players
        Player p1 = new Player("1", "Alice");
        Player p2 = new Player("2", "Bob");

        // 5. Start Game
        Game game = new Game(board, dice, Arrays.asList(p1, p2));
        game.start();
    }
}
