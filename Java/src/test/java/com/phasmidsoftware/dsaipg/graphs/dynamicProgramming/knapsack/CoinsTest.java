package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import com.phasmidsoftware.dsaipg.graphs.dag.Edge;
import java.util.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class CoinsTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();


    @Test
    public void value0() {
        Coins coins = new Coins();
        assertEquals(coins.zero, coins.number(0));
        assertEquals(0, coins.subProblems());
    }

    @Test
    public void value1() {
        Coins coins = new Coins();
        assertEquals(coins.zero.increment(0), coins.number(1));
        assertEquals(1, coins.subProblems());
    }

    @Test
    public void value2() {
        Coins coins = new Coins();
        assertEquals(coins.zero.increment(0).increment(1), coins.number(6));
        assertEquals(6, coins.subProblems());
    }

    @Test
    public void value87() {
        Coins coins = new Coins();
        int amount = 87;
        int[] counts = {2, 0, 1, 3}; // two pennies, one dime, three quarters.
        Coins.Solution expected = coins.new Solution(6, counts);
        assertEquals(expected, coins.number(amount));
        assertEquals(amount, coins.subProblems());
    }

    @Test
    public void mu0() {
        Coins coins = new Coins();
        assertEquals(coins.zero, coins.mu(0));
    }

    @Test
    public void mu1() {
        Coins coins = new Coins();
        assertEquals(coins.zero.increment(0), coins.mu(1));
    }

    @Test
    public void mu2() {
        Coins coins = new Coins();
        assertEquals(coins.zero.increment(0).increment(1), coins.mu(6));
    }

    /**
     * The dependency graph is Figure 10.11 of the book: a vertex is a remaining
     * value, an edge from x to x - c is the choice of one coin of denomination c.
     * Building it explicitly is what makes the structure the book draws something a
     * test can assert about.
     */
    @Test
    public void theGraphHasAVertexPerSubProblem() {
        Coins coins = new Coins();
        coins.number(6);
        Set<Integer> vertices = new TreeSet<>();
        for (Integer v : coins.getGraph().vertices()) vertices.add(v);
        assertEquals("every value from 0 to 6 is a sub-problem",
                Set.of(0, 1, 2, 3, 4, 5, 6), vertices);
    }

    /**
     * An edge exists exactly where a coin fits, and its attribute says which.
     */
    @Test
    public void theEdgesAreTheCoinChoices() {
        Coins coins = new Coins();
        coins.number(6);
        Map<Integer, Integer> destinationToCoinIndex = new TreeMap<>();
        for (Edge<Integer, Integer> e : coins.getGraph().adjacent(6))
            destinationToCoinIndex.put(e.getTo(), e.getAttributes());
        // from 6, only the 1c and 5c coins fit; 10c and 25c do not
        assertEquals(Map.of(5, 0, 1, 1), destinationToCoinIndex);
    }

    /**
     * Every edge goes to a strictly smaller value, so the graph is acyclic -- the
     * book's "no complication due to cycles" -- and descending order is a
     * topological order, which is why one memoised pass suffices.
     */
    @Test
    public void everyEdgeGoesDownwards() {
        Coins coins = new Coins();
        coins.number(30);
        for (Edge<Integer, Integer> e : coins.getGraph().edges())
            assertTrue("edge " + e + " does not decrease the value", e.getTo() < e.getFrom());
    }

    /**
     * The book's worked example: 87c in six coins, being 3x25 + 1x10 + 2x1.
     */
    @Test
    public void theBooksWorkedExample() {
        Coins coins = new Coins();
        Coins.Solution solution = coins.number(87);
        assertEquals(6, solution.n);
        assertEquals(87, solution.getTotal());
        assertArrayEquals("2 x 1c, 0 x 5c, 1 x 10c, 3 x 25c",
                new int[]{2, 0, 1, 3}, solution.counts);
        assertEquals("time and space are Theta(v)", 87, coins.subProblems());
    }

    /**
     * A denomination list of a size other than four.
     * <p>
     * zeros() must give one count per denomination, however many there are: a
     * fixed length of four makes Solution.increment throw
     * ArrayIndexOutOfBoundsException for any longer list.
     */
    @Test
    public void aCoinListOfADifferentSize() {
        Coins coins = new Coins(List.of(1, 2, 5, 10, 20, 50));
        Coins.Solution solution = coins.number(38);
        assertEquals("20 + 10 + 5 + 2 + 1", 5, solution.n);
        assertEquals(38, solution.getTotal());
    }

    /**
     * With no 1c coin some values cannot be made at all. The book notes that "this
     * implies that there is one value of ci that is 1; otherwise, the problem might
     * not be solvable".
     */
    @Test
    public void anUnmakeableValue() {
        Coins coins = new Coins(List.of(5, 10));
        assertEquals(Integer.MAX_VALUE, coins.number(3).n);
    }
}