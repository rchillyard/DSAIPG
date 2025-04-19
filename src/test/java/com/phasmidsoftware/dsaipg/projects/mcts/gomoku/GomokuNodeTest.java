package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class GomokuNodeTest {
     @Test
    public void testChildrenGenerated() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        GomokuNode node = new GomokuNode(state);

        assertTrue(node.isLeaf());
        node.children(); // triggers children generation
        assertFalse(node.isLeaf());
        assertEquals(Gomoku.SIZE * Gomoku.SIZE, node.children().size());
    }

    @Test
    public void testRecordPropagation() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        GomokuMove move = new GomokuMove(2, 2, 1);
        GomokuNode parent = new GomokuNode(state);
        GomokuNode child = new GomokuNode(state, move, parent);

        child.record(1); // Player 1 wins

        assertEquals(1, child.playouts());
        assertEquals(1, child.wins());
        assertEquals(1, parent.playouts());
    }
}
