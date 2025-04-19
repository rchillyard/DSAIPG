package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

public class GomokuMoveTest {
     @Test
    public void testEqualsAndHashCode() {
        GomokuMove m1 = new GomokuMove(1, 2, 0);
        GomokuMove m2 = new GomokuMove(1, 2, 0);
        GomokuMove m3 = new GomokuMove(1, 2, 1);

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotEquals(m1, m3);
    }

    @Test
    public void testToStringFormat() {
        GomokuMove move = new GomokuMove(3, 5, 1);
        assertEquals("[1:(3,5)]", move.toString());
    }
}
