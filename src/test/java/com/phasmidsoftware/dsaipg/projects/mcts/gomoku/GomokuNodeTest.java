package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class GomokuNodeTest {

    @Test
    public void testChildrenAreGeneratedOnce() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        GomokuNode node = new GomokuNode(state);

        var children1 = node.children();
        var children2 = node.children(); // should be same, not regenerated

        assertEquals("Children should only be generated once", children1.size(), children2.size());
        assertTrue("Children list should not be empty", children1.size() > 0);
    }

    @Test
    public void testIsLeafBeforeAndAfterChildren() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        GomokuNode node = new GomokuNode(state);

        assertTrue("Should be leaf before expansion", node.isLeaf());
        node.children(); // trigger expansion
        assertFalse("Should not be leaf after expansion", node.isLeaf());
    }

    @Test
    public void testWinsAndPlayoutsAccumulate() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 1); // player 1
        GomokuMove move = new GomokuMove(4, 4, 1);
        GomokuNode parent = new GomokuNode(state);
        GomokuNode child = new GomokuNode(state, move, parent);

        child.record(1); // Simulate a win for player 1

        assertEquals(1, child.playouts());
        assertEquals(1, child.wins());
        assertEquals(1, parent.playouts()); // parent should also be updated
    }

    @Test
    public void testMoveIsStoredCorrectly() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 1);
        GomokuMove move = new GomokuMove(3, 3, 1);
        GomokuNode node = new GomokuNode(state, move, null);

        assertEquals(move, node.getMove());
    }

    @Test
    public void testParentAssignment() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 1);
        GomokuNode parent = new GomokuNode(state);
        GomokuNode child = new GomokuNode(state, new GomokuMove(2, 2, 1), parent);

        assertEquals(parent, child.getParent());

        GomokuNode newParent = new GomokuNode(state);
        child.setParent(newParent);
        assertEquals(newParent, child.getParent());
    }
}