/*package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

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
}*//* 
package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import java.util.Optional;

public class Main {
    // Give both players a reasonable number of iterations
    private static final int WHITE_ITERS = 10000;
    private static final int BLACK_ITERS = 10000;
    
    public static void main(String[] args) {
        // 1) Initialize empty board
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        printBoard(state);
        
        int moveCount = 0;
        
        // 2) Game loop: rebuild MCTS tree each turn to ensure no moves are missed
        while (!state.isTerminal()) {
            int pl = state.player();
            int iters = pl == 0 ? WHITE_ITERS : BLACK_ITERS;
            
            // Create new tree, new MCTS, select move
            GomokuNode root = new GomokuNode(state);
            GomokuMCTS mcts = new GomokuMCTS(root);
            Move<Gomoku> mv = mcts.findNextMove(iters);
            
            // Debug output to track player and move
            System.out.printf("Move #%d: Player %d (iters=%d) plays: %s%n", 
                             ++moveCount, pl, iters, mv);
            
            // 3) Update game state with the selected move
            GomokuState nextState = (GomokuState) state.next(mv);
            
            // Verify move validity by comparing board states
            if (boardsEqual(state.getBoard(), nextState.getBoard())) {
                System.err.println("Warning: Move didn't change board state!");
                // Additional error handling could go here
            }
            
            state = nextState;
            printBoard(state);
        }
        
        // 4) Game over output
        Optional<Integer> w = state.winner();
        if (w.isPresent()) {
            System.out.println("Game over. Winner is Player " + w.get() + "!");
        } else {
            System.out.println("Game over. It's a draw!");
        }
    }
    
 
    private static boolean boardsEqual(int[][] board1, int[][] board2) {
        for (int i = 0; i < Gomoku.SIZE; i++) {
            for (int j = 0; j < Gomoku.SIZE; j++) {
                if (board1[i][j] != board2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    

    private static void printBoard(GomokuState state) {
        System.out.println("Board:");
        int[][] b = state.getBoard();
        
        // Print column coordinates
        System.out.print("  ");
        for (int j = 0; j < Gomoku.SIZE; j++) {
            System.out.print((j % 10) + " ");
        }
        System.out.println();
        
        for (int i = 0; i < Gomoku.SIZE; i++) {
            // Print row coordinates
            System.out.print((i % 10) + " ");
            
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
}*/
package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import java.util.Optional;

public class Main {
    private static final int WHITE_ITERS = 10000;
    private static final int BLACK_ITERS = 1000; // Set to same value as white for fairness
    
    public static void main(String[] args) {
        // 1) 初始空棋盘
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        printBoard(state);
        
        int moveCount = 0;
        
        // 2) 对局循环：每一步都重建 MCTS 树，绝对不会漏子
        while (!state.isTerminal()) {
            int pl = state.player();
            int iters = pl == 0 ? WHITE_ITERS : BLACK_ITERS;
            
            // 新树 → 新 MCTS → 选子
            GomokuNode root = new GomokuNode(state);
            GomokuMCTS mcts = new GomokuMCTS(root);
            
            // 获取可用移动数量进行调试
            int availableMoves = state.moves(pl).size();
            System.out.printf("Player %d has %d available moves%n", pl, availableMoves);
            
            Move<Gomoku> mv = mcts.findNextMove(iters);
            System.out.printf("Move #%d: Player %d (iters=%d) plays: %s%n", 
                             ++moveCount, pl, iters, mv);
            
            // 保存当前状态的副本用于比较
            int[][] oldBoard = copyBoard(state.getBoard());
            
            // 3) 更新主状态
            GomokuState nextState = (GomokuState) state.next(mv);
            
            // 验证棋盘已更改
            if (boardsEqual(oldBoard, nextState.getBoard())) {
                System.err.println("ERROR: Move didn't change board state!");
                // 这种情况应该不会发生，因为我们已在GomokuState.next中添加了备用移动
            }
            
            state = nextState;
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
    
    /**
     * 创建棋盘副本
     */
    private static int[][] copyBoard(int[][] board) {
        int[][] copy = new int[Gomoku.SIZE][Gomoku.SIZE];
        for (int i=0; i<Gomoku.SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, Gomoku.SIZE);
        }
        return copy;
    }
    
    /**
     * 验证两个棋盘是否相同
     */
    private static boolean boardsEqual(int[][] board1, int[][] board2) {
        for (int i = 0; i < Gomoku.SIZE; i++) {
            for (int j = 0; j < Gomoku.SIZE; j++) {
                if (board1[i][j] != board2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /** 棋盘打印，添加坐标 */
    private static void printBoard(GomokuState state) {
        System.out.println("Board:");
        int[][] b = state.getBoard();
        
        // 打印列坐标
        System.out.print("  ");
        for (int j = 0; j < Gomoku.SIZE; j++) {
            System.out.print((j % 10) + " ");
        }
        System.out.println();
        
        for (int i = 0; i < Gomoku.SIZE; i++) {
            // 打印行坐标
            System.out.print((i % 10) + " ");
            
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