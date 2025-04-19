package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class GomokuStateTest {
    @Test
    public void testNextMoveUpdatesBoard() {
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 1);
        GomokuMove move = new GomokuMove(4, 4, 1);

        GomokuState next = (GomokuState) state.next(move);
        assertEquals(2, next.getBoard()[4][4]);
        assertEquals(0, next.player());
    }

    @Test
    public void testHorizontalWin() {
        int[][] board = new int[Gomoku.SIZE][Gomoku.SIZE];
        for (int i = 0; i < 5; i++) board[3][i] = 2;
        GomokuState state = new GomokuState(board, 0);
        assertEquals(Optional.of(1), state.winner());
    }

    @Test
    public void testFullBoardIsTerminal() {
        int[][] board = new int[Gomoku.SIZE][Gomoku.SIZE];
        for (int i = 0; i < Gomoku.SIZE; i++)
            for (int j = 0; j < Gomoku.SIZE; j++)
                board[i][j] = (i + j) % 2 + 1;

        GomokuState state = new GomokuState(board, 0);
        assertTrue(state.isTerminal());
    }

    @Test
    public void testMoveGeneration() {
        int[][] board = new int[Gomoku.SIZE][Gomoku.SIZE];
        board[0][0] = 1;
        GomokuState state = new GomokuState(board, 0);
        assertFalse(state.moves(0).contains(new GomokuMove(0, 0, 0)));
        assertEquals(Gomoku.SIZE * Gomoku.SIZE - 1, state.moves(0).size());
    }
}
