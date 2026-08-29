package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import com.google.common.collect.ImmutableList;
import com.phasmidsoftware.dsaipg.adt.bqs.Dictionary;
import com.phasmidsoftware.dsaipg.adt.bqs.Dictionary_Hash;
import com.phasmidsoftware.dsaipg.graphs.dag.DiGraph;
import com.phasmidsoftware.dsaipg.graphs.dag.Edge;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The coin chooser: the fewest coins making a given value, by dynamic programming
 * over an explicit dependency graph.
 * <p>
 * This is Figure 10.11 of the book. A vertex is a remaining value — a sub-problem,
 * "how few coins make x" — and an edge from x to x - c records the choice of one
 * coin of denomination c. Every edge therefore costs exactly one coin, so the
 * fewest coins making v is the shortest path from v to 0.
 * <p>
 * The recurrence the graph expresses:
 * <pre>
 *   m(0) = 0
 *   m(x) = min over i of { m(x - c[i]) : x &gt;= c[i] } + 1
 * </pre>
 * Because every edge goes to a strictly smaller value, the graph is acyclic — the
 * book notes there is "no complication due to cycles" — and descending value order
 * is a topological order, so a single memoised pass suffices. Time and space are
 * both Theta(v), as the book says.
 * <p>
 * NOTE the graph is built rather than left implicit in the recursion, so that the
 * structure the book draws is something a caller can inspect and a test can assert
 * about; see {@link #getGraph}. An edge exists only where the coin fits, so no
 * sub-problem is visited for a denomination too large to use.
 * <p>
 * NOTE the book presents this as an improvement on "applying the Bellman-Ford
 * algorithm naively", which is O(vw). The improvement is not in avoiding the graph
 * but in walking it once, in an order that needs no relaxation.
 */
public class Coins {

    /**
     * A count of each denomination, and the total number of coins.
     */
    class Solution implements Comparable<Solution> {
        public int compareTo(Solution o) {
            return Integer.compare(n, o.n);
        }

        /**
         * @param i the index of a denomination to add one of.
         * @return a new Solution with one more coin of that denomination.
         */
        public Solution increment(int i) {
            if (n == Integer.MAX_VALUE) return this;
            int[] copied = Arrays.copyOf(counts, counts.length);
            copied[i]++;
            return new Solution(n + 1, copied);
        }

        public void validate() {
            if (this.n == Integer.MAX_VALUE) return;
            if (getN() != this.n) throw new RuntimeException("validation error: " + this);
        }

        int getTotal() {
            List<Integer> values = Coins.this.coins;
            int total = 0;
            for (int i = 0; i < values.size(); i++) total += counts[i] * values.get(i);
            return total;
        }

        int getN() {
            int n = 0;
            for (int i = 0; i < Coins.this.coins.size(); i++) n += counts[i];
            return n;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Solution solution)) return false;
            return n == solution.n && Arrays.equals(counts, solution.counts);
        }

        public int hashCode() {
            int result = Objects.hash(n);
            result = 31 * result + Arrays.hashCode(counts);
            return result;
        }

        @Override
        public String toString() {
            return "Solution{" +
                    "coins=" + n +
                    ", value=" + getTotal() +
                    ", counts=" + Arrays.toString(counts) +
                    '}';
        }

        final int n;
        final int[] counts;

        public Solution(int n, int[] counts) {
            this.n = n;
            this.counts = counts;
        }
    }

    /**
     * @param amount the value to make.
     * @return the Solution using the fewest coins.
     */
    Solution number(int amount) {
        return mu(amount);
    }

    /**
     * @return how many sub-problems have been solved and remembered.
     */
    int subProblems() {
        return memo.size();
    }

    /**
     * The dependency graph, as far as it has been built.
     * <p>
     * A vertex is a remaining value; an edge from x to y is attributed with the
     * INDEX of the denomination whose use takes x to y. Exposed because it is the
     * structure the book draws, and being able to look at it is the point of
     * building it.
     *
     * @return the graph.
     */
    DiGraph<Integer, Integer> getGraph() {
        return graph;
    }

    /**
     * Extend the graph to cover every value reachable from amount.
     *
     * @param amount the value to make.
     */
    private void buildGraph(int amount) {
        for (int x = built + 1; x <= amount; x++)
            for (int i = 0; i < coins.size(); i++)
                if (coins.get(i) <= x)
                    graph.addEdge(new Edge<>(x, x - coins.get(i), i));
        if (amount > built) built = amount;
    }

    /**
     * Solve for one value, using the memoized solutions of its dependencies.
     *
     * @param amount the value to make.
     * @return the best Solution for it.
     */
    Solution mu(int amount) {
        Solution result = memo.get(amount);
        if (result != null) return result;
        if (amount < 0) return nullSolution;
        if (amount == 0) return zero;
        // NOTE mu extends the graph itself, so it still stands alone -- CoinsTest
        // calls it directly, without going through number().
        buildGraph(amount);
        result = nullSolution;
        // NOTE the options come from the graph. Each outgoing edge is one coin
        // spent, and its attribute says which denomination that was.
        for (Edge<Integer, Integer> edge : graph.adjacent(amount)) {
            Solution option = mu(edge.getTo()).increment(edge.getAttributes());
            if (result.compareTo(option) > 0) result = option;
        }
        result.validate();
        memo.put(amount, result);
        return result;
    }

    public Coins(List<Integer> coins) {
        this.coins = coins;
        // NOTE built here, not at the field, because zeros() now depends on coins
        // and a field initialiser runs before the constructor body. The old zeros()
        // returned a hard-coded four-element array, so it needed nothing.
        this.zero = new Solution(0, zeros());
        this.nullSolution = new Solution(Integer.MAX_VALUE, zeros());
    }

    public Coins() {
        this(US);
    }

    /**
     * @return a count of zero for each denomination.
     * <p>
     * NOTE one entry per denomination, however many there are. A fixed length
     * would give an ArrayIndexOutOfBoundsException from Solution.increment for any
     * coin list of a different size.
     */
    int[] zeros() {
        return new int[coins.size()];
    }

    final static List<Integer> US = ImmutableList.of(1, 5, 10, 25);
    private final List<Integer> coins;
    final Solution zero;
    private final Solution nullSolution;
    private final DiGraph<Integer, Integer> graph = new DiGraph<>();
    /** the largest value the graph has been built out to */
    private int built = 0;
    // The following is to memoize the sub-solutions: key is of type Key, and value is value (an Integer).
    private final Dictionary<Integer, Solution> memo = new Dictionary_Hash<>();
}
