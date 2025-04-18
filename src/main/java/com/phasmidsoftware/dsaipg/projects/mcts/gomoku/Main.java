package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.Optional;

public class Main {

    private static final int WHITE_ITERS = 10000;
    private static final int BLACK_ITERS = 000;

    public static void main(String[] args) {
        // 1) 初始空棋盘
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        printBoard(state);

        // 2) 对局循环：每一步都重建 MCTS 树，绝对不会漏子
        while (!state.isTerminal()) {
            int pl = state.player();
            int iters = pl == 0 ? WHITE_ITERS : BLACK_ITERS;

            // 新树 → 新 MCTS → 选子
            GomokuNode root = new GomokuNode(state);
            GomokuMCTS  mcts = new GomokuMCTS(root);
            Move<Gomoku> mv = mcts.findNextMove(iters);

            System.out.printf("Player %d (iters=%d) plays: %s%n", pl, iters, mv);

            // 3) 只更新一次主状态
            state = (GomokuState) state.next(mv);
            printBoard(state);
        }

        // 4) 终局输出
        Optional<Integer> w = state.winner();
        if (w.isPresent()) {
            System.out.println("Game over. Winner is Player " + w.get() + "!");
        } else {
            System.out.println("Game over. It's a draw!");
        }
    }

    /** 简单的棋盘打印 */
    private static void printBoard(GomokuState state) {
        System.out.println("Board:");
        int[][] b = state.getBoard();
        for (int i = 0; i < Gomoku.SIZE; i++) {
            for (int j = 0; j < Gomoku.SIZE; j++) {
                char c = b[i][j] == 0 ? '.'
                         : b[i][j] == 1 ? '○'
                         : '●';
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}