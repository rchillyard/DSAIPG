/*package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class Main {

    // 白／黑两个 AI 的模拟次数
    private static final int WHITE_ITERS = 20000;
    private static final int BLACK_ITERS = 10000;

    public static void main(String[] args) {
        // 记录整局开始时间
        Instant gameStart = Instant.now();

        // 1) 初始空棋盘、白方先手
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        printBoard(state);

        int moveCount = 0;

        // 2) 对局循环
        while (!state.isTerminal()) {
            int pl    = state.player();
            int iters = pl == 0 ? WHITE_ITERS : BLACK_ITERS;

            // 记录本步开始时间
            Instant stepStart = Instant.now();

            // 每一步都重建一棵新树，绝对不会漏子
            GomokuNode root = new GomokuNode(state);
            GomokuMCTS  mcts = new GomokuMCTS(root);
            Move<Gomoku> mv = mcts.findNextMove(iters);

            // 计算本步耗时
            Duration stepDur = Duration.between(stepStart, Instant.now());
            System.out.printf(
                "Move #%d: Player %d (iters=%d) plays: %s   // step time: %d ms%n",
                ++moveCount, pl, iters, mv, stepDur.toMillis()
            );

            // 3) 更新主状态并打印
            state = (GomokuState) state.next(mv);
            printBoard(state);
        }

        // 4) 终局输出及总耗时
        Instant gameEnd = Instant.now();
        Duration gameDur = Duration.between(gameStart, gameEnd);
        System.out.printf("Game over. Total game time: %d ms%n", gameDur.toMillis());

        Optional<Integer> w = state.winner();
        if (w.isPresent()) {
            System.out.println("Winner is Player " + w.get() + "!");
        } else {
            System.out.println("It's a draw!");
        }
    }

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
*/

package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class Main {
    // 白／黑两个 AI 的模拟次数
    private static final int[] WHITE_ITERS_OPTIONS = {500};
    private static final int[] BLACK_ITERS_OPTIONS = {500, 1000, 2000, 5000,10000};
    private static final int GAMES_PER_CONFIG = 10; // 每种配置运行的游戏数

    public static void main(String[] args) {
        // 统计结果
        System.out.println("Running Gomoku simulation with different iteration counts");
        System.out.println("==========================================");
        
        // 遍历不同的迭代次数组合
        for (int whiteIters : WHITE_ITERS_OPTIONS) {
            for (int blackIters : BLACK_ITERS_OPTIONS) {
                int whiteWins = 0;
                int blackWins = 0;
                int draws = 0;
                long totalGameTime = 0;
                int totalMoves = 0;

                System.out.printf("\nConfiguration: WHITE_ITERS=%d, BLACK_ITERS=%d (%d games)%n", 
                                 whiteIters, blackIters, GAMES_PER_CONFIG);
                System.out.println("------------------------------------------");

                // 每种配置运行多次游戏以获得更准确的统计
                for (int gameNum = 1; gameNum <= GAMES_PER_CONFIG; gameNum++) {
                    //System.out.printf("Starting game %d/%d...%n", gameNum, GAMES_PER_CONFIG);
                    
                    // 运行一场比赛并收集结果
                    GameResult result = runOneGame(whiteIters, blackIters);
                    
                    // 更新统计
                    if (result.winner == 0) whiteWins++;
                    else if (result.winner == 1) blackWins++;
                    else draws++;
                    
                    totalGameTime += result.gameDurationMs;
                    totalMoves += result.moveCount;
                    
                    /*System.out.printf("Game %d finished: %s in %d moves (%d ms)%n", 
                                     gameNum, 
                                     result.winner == -1 ? "Draw" : "Player " + result.winner + " won",
                                     result.moveCount, 
                                     result.gameDurationMs);*/
                }

                // 计算并显示此配置的统计数据
                double avgTime = (double)totalGameTime / GAMES_PER_CONFIG;
                double avgMoves = (double)totalMoves / GAMES_PER_CONFIG;
                
                System.out.println("\nResults:");
                System.out.printf("White wins: %d (%.1f%%)%n", 
                                 whiteWins, (whiteWins * 100.0 / GAMES_PER_CONFIG));
                System.out.printf("Black wins: %d (%.1f%%)%n", 
                                 blackWins, (blackWins * 100.0 / GAMES_PER_CONFIG));
                System.out.printf("Draws: %d (%.1f%%)%n", 
                                 draws, (draws * 100.0 / GAMES_PER_CONFIG));
                System.out.printf("Average game duration: %.1f ms%n", avgTime);
                System.out.printf("Average moves per game: %.1f%n", avgMoves);
                System.out.println("==========================================");
            }
        }
    }
    
    /**
     * 运行一场比赛并返回结果
     */
    private static GameResult runOneGame(int whiteIters, int blackIters) {
        // 记录整局开始时间
        Instant gameStart = Instant.now();
        
        // 初始空棋盘、白方先手
        GomokuState state = new GomokuState(new int[Gomoku.SIZE][Gomoku.SIZE], 0);
        
        // 只在调试时打印棋盘
        if (isDebugMode()) {
            printBoard(state);
        }
        
        int moveCount = 0;
        // 对局循环
        while (!state.isTerminal()) {
            int pl = state.player();
            int iters = pl == 0 ? whiteIters : blackIters;
            
            // 记录本步开始时间
            Instant stepStart = Instant.now();
            
            // 每一步都重建一棵新树，绝对不会漏子
            GomokuNode root = new GomokuNode(state);
            GomokuMCTS mcts = new GomokuMCTS(root);
            Move<Gomoku> mv = mcts.findNextMove(iters);
            
            // 计算本步耗时
            Duration stepDur = Duration.between(stepStart, Instant.now());
            
            // 只在调试模式下打印详细步骤
            
            if (isDebugMode()) {
                System.out.printf(
                    "Move #%d: Player %d (iters=%d) plays: %s // step time: %d ms%n",
                    ++moveCount, pl, iters, mv, stepDur.toMillis());
            } else {
                moveCount++;
                // 简单进度显示
                /*if (moveCount % 10 == 0) {
                    System.out.print(".");
                }*/
            }
            
            // 更新主状态
            state = (GomokuState) state.next(mv);
            
            // 只在调试模式下打印棋盘
            if (isDebugMode()) {
                printBoard(state);
            }
        }
        
        // 计算总耗时
        Instant gameEnd = Instant.now();
        Duration gameDur = Duration.between(gameStart, gameEnd);
        long gameDurationMs = gameDur.toMillis();
        
        // 确定胜者
        Optional<Integer> w = state.winner();
        int winner = w.orElse(-1); // -1 表示平局
        
        /*if (!isDebugMode()) {
            System.out.println(); // 换行结束进度指示
        }*/
        
        return new GameResult(winner, moveCount, gameDurationMs);
    }
    
    /**
     * 是否处于调试模式（打印详细信息）
     */
    private static boolean isDebugMode() {
        // 设置为 true 可以看到详细的棋盘和移动信息
        return false;
    }
    
    /** 棋盘打印工具 */
    private static void printBoard(GomokuState state) {
        System.out.println("Board:");
        int[][] b = state.getBoard();
        // 打印列坐标
        System.out.print(" ");
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
    
    /**
     * 存储游戏结果的内部类
     */
    private static class GameResult {
        final int winner;       // 胜者：0=白方，1=黑方，-1=平局
        final int moveCount;    // 总步数
        final long gameDurationMs; // 游戏持续时间（毫秒）
        
        GameResult(int winner, int moveCount, long gameDurationMs) {
            this.winner = winner;
            this.moveCount = moveCount;
            this.gameDurationMs = gameDurationMs;
        }
    }
}
