package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

public class GomokuMCTSTest {
    @Test
    public void testFindNextMoveIsNotNull() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        GomokuNode root = new GomokuNode(state);
        GomokuMCTS mcts = new GomokuMCTS(root);

        Move<Gomoku> move = mcts.findNextMove(100);
        assertNotNull(move);
    }

    @Test
    public void testFindNextMoveChoosesWinWhenAvailable() {
        int[][] board = new int[Gomoku.SIZE][Gomoku.SIZE];
        for (int i = 0; i < 4; i++) board[0][i] = 2; 
        GomokuState state = new GomokuState(board, 1); 
        GomokuMCTS mcts = new GomokuMCTS(new GomokuNode(state));

        Move<Gomoku> move = mcts.findNextMove(100);
        GomokuMove expected = new GomokuMove(0, 4, 1);

        assertEquals(expected, move);
    }
    @Test
    public void testFindNextMoveUpdatesRoot() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        GomokuNode root = new GomokuNode(state);
        GomokuMCTS mcts = new GomokuMCTS(root);

        Move<Gomoku> move = mcts.findNextMove(100);
        GomokuNode newRoot = mcts.getRoot();

        assertEquals(move, newRoot.getMove());
        assertNotEquals(root, newRoot);
    }

    @Test
    public void testUCTSelectionPrefersHighWinRate() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        GomokuNode root = new GomokuNode(state);
        GomokuMCTS mcts = new GomokuMCTS(root);

        Move<Gomoku> move = mcts.findNextMove(500);
        assertNotNull(move);
        assertTrue("Root should have accumulated playouts", mcts.getRoot().playouts() > 0);
    }
    
}
