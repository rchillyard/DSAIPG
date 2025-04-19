package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class GomokuTest {
     @Test
    public void testOpenerReturnsValidPlayer() {
        Gomoku game = new Gomoku();
        int opener = game.opener();
        assertTrue(opener == 0 || opener == 1);
    }
}
