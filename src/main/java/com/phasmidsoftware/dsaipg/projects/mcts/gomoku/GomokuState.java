package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

public class GomokuState implements State<Gomoku> {

    private final int[][] board;    // 0=空,1=白,2=黑
    private final int      player;  // 下一手：0=白,1=黑
    private final Random   rnd = new Random();

    public GomokuState(int[][] board, int player) {
        this.board  = board;
        this.player = player;
    }

    @Override public Gomoku game()  { return new Gomoku(); }
    @Override public int     player(){ return player; }

    @Override
    public Optional<Integer> winner() {
        for (int i = 0; i < Gomoku.SIZE; i++) {
            for (int j = 0; j < Gomoku.SIZE; j++) {
                int p = board[i][j];
                if (p==0) continue;
                if (check(i,j,1,0,p) || check(i,j,0,1,p)
                 || check(i,j,1,1,p) || check(i,j,1,-1,p))
                    return Optional.of(p-1);
            }
        }
        return Optional.empty();
    }

    private boolean check(int x,int y,int dx,int dy,int p) {
        for (int k=0;k<Gomoku.WIN;k++) {
            int nx = x+dx*k, ny = y+dy*k;
            if (nx<0||nx>=Gomoku.SIZE||ny<0||ny>=Gomoku.SIZE||board[nx][ny]!=p)
                return false;
        }
        return true;
    }

    @Override
    public boolean isTerminal() {
        // 只看有人赢或棋盘满，不再用 moves().isEmpty()
        if (winner().isPresent()) return true;
        for (int i=0;i<Gomoku.SIZE;i++)
            for (int j=0;j<Gomoku.SIZE;j++)
                if (board[i][j]==0) return false;
        return true;
    }

    @Override public Random random() { return rnd; }

    @Override
    public Collection<Move<Gomoku>> moves(int player) {
        // 1) 连五必胜
        Optional<Move<Gomoku>> win = findImmediateWin(player);
        if (win.isPresent()) return Collections.singletonList(win.get());
        // 2) 堵对手连五
        Optional<Move<Gomoku>> block = findImmediateWin(1-player);
        if (block.isPresent()) return Collections.singletonList(block.get());
        // 3) 全盘所有空位（兜底，永不空）
        List<Move<Gomoku>> list = new ArrayList<>();
        for (int i=0;i<Gomoku.SIZE;i++)
            for (int j=0;j<Gomoku.SIZE;j++)
                if (board[i][j]==0) list.add(new GomokuMove(i,j,player));
        return list;
    }

    @Override
    public Move<Gomoku> chooseMove(int player) {
        Collection<Move<Gomoku>> c = moves(player);
        int idx = rnd.nextInt(c.size());
        return new ArrayList<>(c).get(idx);
    }

    public Optional<Move<Gomoku>> findImmediateWin(int player) {
        int p = player+1;
        for (int i=0;i<Gomoku.SIZE;i++) {
            for (int j=0;j<Gomoku.SIZE;j++) {
                if (board[i][j]!=0) continue;
                board[i][j] = p;
                boolean ok = check(i,j,1,0,p)
                          || check(i,j,0,1,p)
                          || check(i,j,1,1,p)
                          || check(i,j,1,-1,p);
                board[i][j] = 0;
                if (ok) return Optional.of(new GomokuMove(i,j,player));
            }
        }
        return Optional.empty();
    }

    @Override
    public State<Gomoku> next(Move<Gomoku> move) {
        GomokuMove m = (GomokuMove)move;
        int[][] copy = new int[Gomoku.SIZE][Gomoku.SIZE];
        for (int i=0;i<Gomoku.SIZE;i++)
            System.arraycopy(board[i],0,copy[i],0,Gomoku.SIZE);
        copy[m.row][m.col] = m.player+1;
        return new GomokuState(copy,1-player);
    }

    public int[][] getBoard() { return board; }
}