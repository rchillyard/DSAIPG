package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import com.google.common.collect.ImmutableList;
import com.phasmidsoftware.dsaipg.adt.bqs.Dictionary;
import com.phasmidsoftware.dsaipg.adt.bqs.Dictionary_Hash;
import com.phasmidsoftware.dsaipg.graphs.dag.DiGraph;
import com.phasmidsoftware.dsaipg.graphs.dag.Edge;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Class to implement the 0-1 Knapsack problem using dynamic programming, expressed
 * as a walk over an explicit graph of sub-problems.
 * <p>
 * A vertex is a sub-problem: the {@link Key} (kappa, omega) asks for the greatest
 * value obtainable from the first <code>kappa</code> items within a weight of
 * <code>omega</code>. An edge is a decision about item number <code>kappa</code>,
 * and it carries as its attribute the item that decision packs -- or
 * <code>null</code> where the decision is to leave that item behind. So the two
 * recursive cases given in the book become the two edges out of a vertex:
 * <ul>
 *     <li>leave item kappa: an edge to (kappa - 1, omega) with no item attached;</li>
 *     <li>take item kappa: an edge to (kappa - 1, omega - w_kappa) attributed with
 *     the item -- present only when the item actually fits.</li>
 * </ul>
 * The value of a packing is the sum of the values on the path, so the answer is the
 * <i>longest</i> path from (n, W) to a base case. Contrast {@link Coins}, whose
 * edges each cost exactly one coin and whose answer is therefore a shortest path.
 * <p>
 * Every edge decreases kappa by exactly one, so the graph is acyclic however the
 * weights fall, and the recursion terminates at kappa = 0.
 * <p>
 * NOTE the edges out of a vertex are built as that vertex is first visited, not in
 * advance. This is the point the book makes about the bottom-up method: solving
 * naively "will evaluate all nW values of m, including many that will never be
 * needed". Building lazily means the graph holds exactly the sub-problems the
 * search reached -- for the Google data set, 31373 of a possible 42500.
 * <p>
 * NOTE also that this walk does not depend on the order in which the adjacency bag
 * offers the two edges, which matters because a Bag_Array iterates in a random
 * order. The two decisions are told apart by whether an edge carries an item, and
 * a tie between them is settled in favour of leaving the item behind.
 */
public class Knapsack {

    /**
     * Method to get the maximum possible value, given the max weight allowable.
     *
     * @param max the maximum weight that can be packed.
     * @return the maximum value.
     */
    Solution value(int max) {
        return mu(items.size(), max);
    }

    /**
     * Returns the total number of sub-problems that have been memoized.
     *
     * @return the number of memoized sub-problems
     */
    int subProblems() {
        return memo.size();
    }

    /**
     * The graph of sub-problems, as far as it has been explored.
     * <p>
     * It is empty until the first call of {@link #value} or {@link #mu}, and it
     * grows with each call that reaches a sub-problem not seen before.
     *
     * @return the dependency graph.
     */
    DiGraph<Key, Item> graph() {
        return graph;
    }

    /**
     * Recursive (private) method. It's package-protected here to make it easy to test.
     *
     * @param kappa an index which is at least 1 and at most n where n is the size of items.
     * @param omega the omega value (a weight).
     * @return the maximum value achievable using only the first <code>kappa</code> items and
     * a weight not exceeding <code>omega</code>.
     */
    Solution mu(int kappa, int omega) {
        Key key = new Key(kappa, omega);
        Solution value = memo.get(key);
        if (value != null) return value;
        if (kappa < 1) return empty;
        addEdges(key);
        Solution leave = empty, take = null;
        for (Edge<Key, Item> edge : graph.adjacent(key)) {
            Key to = edge.getTo();
            Item item = edge.getAttributes();
            if (item == null) leave = mu(to.kappa, to.omega);
            else take = mu(to.kappa, to.omega).increment(item);
        }
        value = take != null && take.compareTo(leave) > 0 ? take : leave;
        memo.put(key, value);
        return value;
    }

    /**
     * Add the edges leading out of a sub-problem: the decision to leave item kappa
     * behind, and -- if it fits -- the decision to pack it.
     * <p>
     * Called exactly once per sub-problem, because {@link #mu} memoizes the result
     * of every vertex whose edges it builds and returns early thereafter.
     *
     * @param key the sub-problem, whose kappa is at least 1.
     */
    private void addEdges(Key key) {
        Item item = items.get(key.kappa - 1);
        graph.addEdge(new Edge<>(key, new Key(key.kappa - 1, key.omega), null));
        if (item.weight <= key.omega)
            graph.addEdge(new Edge<>(key, new Key(key.kappa - 1, key.omega - item.weight), item));
    }

    public Knapsack(List<Item> items) {
        this.items = items;
    }

    private final List<Item> items;
    // The graph of sub-problems and the decisions that connect them (see the class comment).
    private final DiGraph<Key, Item> graph = new DiGraph<>();
    // The following is to memoize the sub-solutions: key is of type Key, and value is value (an Integer).
    private final Dictionary<Key, Solution> memo = new Dictionary_Hash<>();
    final static Solution empty = new Solution(0, ImmutableList.of());

    /**
     * Inner class Solution which represents a (sub) solution of the problem.
     */
    static class Solution implements Comparable<Solution> {

        public int compareTo(Solution o) {
            return Integer.compare(value, o.value);
        }

        public Solution increment(Item item) {
            List<Item> list = Stream.concat(items.stream(), Stream.of(item)).toList();
            return new Solution(value + item.value, list);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Solution solution)) return false;
            return value == solution.value && Objects.equals(items, solution.items);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, items);
        }

        @Override
        public String toString() {
            return "Solution{" + "value=" + value + ", items=" + items + '}';
        }

        final int value;
        final List<Item> items;

        public Solution(int value, List<Item> items) {
            this.value = value;
            this.items = items;
        }

        public static Solution of(List<Item> items) {
            Solution result = empty;
            for (Item item : items) result = result.increment(item);
            return result;
        }

        public static Solution of(Item item) {
            return of(ImmutableList.of(item));
        }
    }

    /**
     * Inner class <code>Key</code> which represents the parameters of a subproblem (in this case, evaluating mu).
     * <p>
     * It serves both as the key under which a sub-solution is memoized and as a
     * vertex of the dependency graph -- these are the same thing, which is why the
     * book recommends a hash table with "the key from k and w".
     */
    static class Key {
        @Override
        public String toString() {
            return "Key{" + kappa + ", " + omega + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key key)) return false;
            return kappa == key.kappa && Objects.equals(omega, key.omega);
        }

        @Override
        public int hashCode() {
            return Objects.hash(kappa, omega);
        }

        public Key(int kappa, int omega) {
            this.kappa = kappa;
            this.omega = omega;
        }

        final int kappa;
        final int omega;
    }

    public static class Item {
        @Override
        public String toString() {
            return id + '(' + weight + ", " + value + ')';
        }

        public Item(String id, int weight, int value) {
            this.id = id;
            this.weight = weight;
            this.value = value;
        }

        final String id;
        final int weight;
        final int value;
    }
}
