package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.*;

public class GomokuNode implements Node<Gomoku> {

    private final GomokuState      state;
    private final GomokuMove       move;    // 从父节点落到本节点的那步
    private GomokuNode             parent;  // 可变，用于 advanceRoot
    private final List<GomokuNode> children = new ArrayList<>();
    private int                    wins=0, visits=0;

    public GomokuNode(GomokuState state) {
        this(state,null,null);
    }
    public GomokuNode(GomokuState state, GomokuMove move, GomokuNode parent) {
        this.state  = state;
        this.move   = move;
        this.parent = parent;
    }

    @Override public boolean isLeaf()           { return children.isEmpty(); }
    @Override public State<Gomoku> state()      { return state; }
    @Override public boolean white()            { return state.player()==0; }

    @Override
    public Collection<Node<Gomoku>> children() {
        if (children.isEmpty() && !state.isTerminal()) {
            for (Move<Gomoku> m: state.moves(state.player())) {
                GomokuState ns = (GomokuState)state.next(m);
                children.add(new GomokuNode(ns,(GomokuMove)m,this));
            }
        }
        return Collections.unmodifiableList(children);
    }

    @Override public void addChild(State<Gomoku> s)     { throw new UnsupportedOperationException(); }
    @Override public void backPropagate()               { throw new UnsupportedOperationException(); }
    @Override public int wins()                         { return wins; }
    @Override public int playouts()                     { return visits; }

    public void record(int result) {
        visits++;
        if (move!=null && result==move.player()) wins++;
        if (parent!=null) parent.record(result);
    }

    public GomokuMove getMove() { return move; }
    public void setParent(GomokuNode p) { this.parent=p; }
}