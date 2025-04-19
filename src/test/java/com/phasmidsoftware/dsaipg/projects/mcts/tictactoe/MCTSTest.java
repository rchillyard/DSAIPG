package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;

public class MCTSTest {
    @Test
    public void testBestMoveAfterSimulation() {
        TicTacToe.TicTacToeState initialState = new TicTacToe().new TicTacToeState();
        TicTacToeNode root = new TicTacToeNode(initialState);
        MCTS mcts = new MCTS(root);

        // run 1000 simulations
        for (int i = 0; i < 1000; i++) {
            mcts.runSimulation(root);
        }

        Node<TicTacToe> best = mcts.bestChild(root);

        // check that we found a valid state
        assertNotNull(best);
        assertNotEquals(0, best.playouts());
        assertTrue(best.wins() >= 0);

        System.out.println("Best move after simulation:");
        System.out.println(best.state());
    }

    @Test
    public void testSimulationReturnsValidScore() {
        TicTacToe.TicTacToeState initialState = new TicTacToe().new TicTacToeState();
        MCTS mcts = new MCTS(new TicTacToeNode(initialState));

        int score = mcts.simulate(initialState);

        // Score must be 0 (lose), 1 (draw), or 2 (win)
        assertTrue(score == 0 || score == 1 || score == 2);
    }

    @Test
    public void testRunSimulationIncreasesPlayouts() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        MCTS mcts = new MCTS(root);
        int before = root.playouts();

        mcts.runSimulation(root);

        int after = root.playouts();
        assertTrue("Playouts should increase after simulation", after > before);
    }
}
