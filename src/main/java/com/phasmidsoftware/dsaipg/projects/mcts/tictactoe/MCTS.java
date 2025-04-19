package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

public class MCTS {

    private final Node<TicTacToe> root;

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    public static void main(String[] args) {
        MCTS mcts = new MCTS(new TicTacToeNode(new TicTacToe().new TicTacToeState()));
        Node<TicTacToe> root = mcts.root;

        // Run 10000 simulations
        for (int i = 0; i < 10000; i++) {
            mcts.runSimulation(root);
        }

        // Get best next move after simulations
        Node<TicTacToe> best = mcts.bestChild(root);
        System.out.println("Best move found:");
        System.out.println(best.state());
    }

    public void runSimulation(Node<TicTacToe> node) {
        List<Node<TicTacToe>> path = new ArrayList<>();
        Node<TicTacToe> current = node;
        path.add(current);

        // Selection
        while (!current.isLeaf() && !current.children().isEmpty()) {
            current = select(current);
            path.add(current);
        }

        // Expansion
        if (!current.isLeaf() && current.children().isEmpty()) {
            expand(current);
            if (!current.children().isEmpty()) {
                current = select(current);
                path.add(current);
            }
        }

        // Simulation
        int score = simulate(current.state());

        // Backpropagation
        for (Node<TicTacToe> n : path) {
            if (n instanceof TicTacToeNode tttNode) {
                tttNode.addPlayout(score);
            }
        }
    }

    private void expand(Node<TicTacToe> node) {
        for (Move<TicTacToe> move : node.state().moves(node.state().player())) {
            State<TicTacToe> childState = node.state().next(move);
            node.addChild(childState);
        }
    }

    public int simulate(State<TicTacToe> state) {
        State<TicTacToe> current = state;
        int currentPlayer = current.player();

        while (!current.isTerminal()) {
            Move<TicTacToe> move = current.chooseMove(current.player());
            current = current.next(move);
        }

        Optional<Integer> winner = current.winner();
        if (winner.isEmpty()) return 1; // draw
        return winner.get() == currentPlayer ? 2 : 0; // win:2, lose:0
    }

    private Node<TicTacToe> select(Node<TicTacToe> node) {
        double c = Math.sqrt(2); // exploration constant
        return node.children().stream()
                .max(Comparator.comparing(child -> {
                    double w = child.wins();
                    double n = child.playouts();
                    double N = node.playouts();
                    if (n == 0) return Double.MAX_VALUE;
                    return w / n + c * Math.sqrt(Math.log(N + 1) / n);
                }))
                .orElseThrow();
    }

    public Node<TicTacToe> bestChild(Node<TicTacToe> node) {
        return node.children().stream()
                .max(Comparator.comparingDouble(child -> (double) child.wins() / child.playouts()))
                .orElseThrow();
    }
}