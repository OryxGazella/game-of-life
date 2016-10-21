package soy.frank.game.life;

import java.util.Arrays;
import java.util.List;

/* This is a dummy implementation, you should not really be tied to the
* data structures or anything in here, maybe map to and from them if you like
* try not to look at the implementation of the animator until you have
* implemented the game of life on your own.*/
class GameOfLife {
    static final List<LivingCell> seed = Arrays.asList(
            LivingCell.of(1, 1),
            LivingCell.of(2, 1),
            LivingCell.of(3, 1),
            LivingCell.of(2, 2),
            LivingCell.of(3, 2),
            LivingCell.of(4, 2)
    );

    private static final List<LivingCell> alternate = Arrays.asList(
            LivingCell.of(1, 1),
            LivingCell.of(1, 2),
            LivingCell.of(2, 0),
            LivingCell.of(4, 2),
            LivingCell.of(4, 1),
            LivingCell.of(3, 3)
    );

    static List<LivingCell> apply(List<LivingCell> previousFrame) {
        if(previousFrame == seed) return alternate;

        return seed;
    }
}
