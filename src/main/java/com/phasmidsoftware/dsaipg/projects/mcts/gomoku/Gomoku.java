package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Game;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

public class Gomoku implements Game<Gomoku> {
    public static final int SIZE = 9;  // 9×9
    public static final int WIN  = 5;

    @Override
    public State<Gomoku> start() {
        return new GomokuState(new int[SIZE][SIZE], opener());
    }

    @Override
    public int opener() {
        return 0;  // 白先手
    }
}