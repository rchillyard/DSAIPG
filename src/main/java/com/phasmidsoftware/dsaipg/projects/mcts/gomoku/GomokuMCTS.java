package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.Comparator;
import java.util.stream.Collectors;

public class GomokuMCTS {

    private GomokuNode root;

    public GomokuMCTS(GomokuNode root) {
        this.root = root;
    }

    public GomokuNode getRoot() {
        return root;
    }

    public void advanceRoot(Move<Gomoku> move) {
        for (GomokuNode c: root.children().stream().map(n->(GomokuNode)n).collect(Collectors.toList())) {
            if (c.getMove().equals(move)) {
                c.setParent(null);
                root = c;
                return;
            }
        }
        GomokuState ns = (GomokuState)root.state().next(move);
        root = new GomokuNode(ns);
    }

    public Move<Gomoku> findNextMove(int iterations) {
        for (int i=0;i<iterations;i++) {
            GomokuNode node = select(root);
            if (!node.state().isTerminal()) node.children();
            int res = simulate((GomokuState)node.state());
            node.record(res);
        }
        GomokuNode best = root.children().stream()
            .map(n->(GomokuNode)n)
            .max(Comparator.comparingInt(GomokuNode::playouts))
            .orElseThrow();
        best.setParent(null);
        root = best;
        return best.getMove();
    }

    private GomokuNode select(GomokuNode node) {
        while (!node.state().isTerminal() && !node.isLeaf()) {
            final int tot = node.playouts();
            node = node.children().stream()
                 .map(n->(GomokuNode)n)
                 .max(Comparator.comparingDouble(c->uct(c,tot)))
                 .orElseThrow();
        }
        return node;
    }

    private double uct(GomokuNode c,int tot) {
        if (c.playouts()==0) return Double.MAX_VALUE;
        double e = Math.sqrt(2*Math.log(tot)/c.playouts());
        double x = c.wins()/(double)c.playouts();
        return x+e;
    }

    private int simulate(GomokuState start) {
        State<Gomoku> sim = start;
        while (!sim.isTerminal()) {
            Move<Gomoku> m = sim.chooseMove(sim.player());
            sim = sim.next(m);
        }
        return sim.winner().orElse(-1);
    }
}