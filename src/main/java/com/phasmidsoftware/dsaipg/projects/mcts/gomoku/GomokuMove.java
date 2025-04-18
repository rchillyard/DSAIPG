package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

public class GomokuMove implements Move<Gomoku> {
    public final int row, col, player;

    public GomokuMove(int row, int col, int player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    @Override
    public int player() {
        return player;
    }

    @Override
    public String toString() {
        return String.format("[%d:(%d,%d)]", player, row, col);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GomokuMove)) return false;
        GomokuMove m = (GomokuMove) o;
        return m.player == player && m.row == row && m.col == col;
    }

    @Override
    public int hashCode() {
        return player * 31 * 31 + row * 31 + col;
    }
}