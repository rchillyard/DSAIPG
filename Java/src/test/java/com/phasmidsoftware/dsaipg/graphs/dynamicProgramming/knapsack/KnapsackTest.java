package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import com.google.common.collect.ImmutableList;
import com.phasmidsoftware.dsaipg.graphs.dag.Edge;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class KnapsackTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    final Knapsack.Item itemA = new Knapsack.Item("A", 2, 1);
    final Knapsack.Item itemB = new Knapsack.Item("B", 1, 2);

    // The three objects of Figure 10.12, with their weights and values.
    final Knapsack.Item itemX = new Knapsack.Item("x", 10, 40);
    final Knapsack.Item itemY = new Knapsack.Item("y", 3, 20);
    final Knapsack.Item itemZ = new Knapsack.Item("z", 5, 30);

    @Test
    public void testIncrement() {
        List<Knapsack.Item> list = ImmutableList.of(itemA);
        Knapsack knapsack = new Knapsack(list);
        Knapsack.Solution solution = Knapsack.empty;
        Knapsack.Solution solutionA = solution.increment(itemA);
        assertEquals(itemA.value, solutionA.value);
        assertEquals(1, solutionA.items.size());
        assertEquals(itemA, solutionA.items.get(0));
    }

    @Test
    public void value0() {
        List<Knapsack.Item> list = ImmutableList.of();
        Knapsack knapsack = new Knapsack(list);
        assertEquals(Knapsack.empty, knapsack.value(10));
    }

    @Test
    public void value1() {
        List<Knapsack.Item> list = ImmutableList.of(itemA);
        Knapsack knapsack = new Knapsack(list);
        assertEquals(new Knapsack.Solution(1, list), knapsack.value(2));
    }

    @Test
    public void value2AB() {
        List<Knapsack.Item> list = ImmutableList.of(itemA, itemB);
        Knapsack knapsack = new Knapsack(list);
        assertEquals(Knapsack.Solution.of(itemB), knapsack.value(2));
    }

    @Test
    public void value2BA() {
        List<Knapsack.Item> list = ImmutableList.of(itemB, itemA);
        Knapsack knapsack = new Knapsack(list);
        assertEquals(Knapsack.Solution.of(itemB), knapsack.value(2));
    }

    @Test
    public void mu0() {
        List<Knapsack.Item> list = ImmutableList.of();
        Knapsack knapsack = new Knapsack(list);
        Knapsack.Solution solution = knapsack.mu(0, 10);
        assertEquals(0, solution.value);
        assertTrue(solution.items.isEmpty());
    }

    @Test
    public void mu1A() {
        List<Knapsack.Item> list = ImmutableList.of(itemA);
        Knapsack knapsack = new Knapsack(list);
        Knapsack.Solution solution1 = knapsack.mu(1, 1);
        assertEquals(0, solution1.value);
        assertTrue(solution1.items.isEmpty());
        Knapsack.Solution solution2 = knapsack.mu(1, 2);
        assertEquals(1, solution2.value);
        assertEquals(ImmutableList.of(itemA), solution2.items);
    }

    @Test
    public void mu1B() {
        List<Knapsack.Item> list = ImmutableList.of(itemB);
        Knapsack knapsack = new Knapsack(list);
        Knapsack.Solution solution1 = knapsack.mu(1, 1);
        assertEquals(itemB.value, solution1.value);
        assertEquals(ImmutableList.of(itemB), solution1.items);
        Knapsack.Solution solution2 = knapsack.mu(1, 2);
        assertEquals(itemB.value, solution2.value);
        assertEquals(ImmutableList.of(itemB), solution2.items);
    }

    /**
     * The book's worked example (Figure 10.12): a knapsack carrying at most 10,
     * and three objects. Packing x uses the whole capacity for a value of 40; y
     * and z together weigh only 8 and are worth 50, which is the optimum.
     */
    @Test
    public void figure10_12() {
        Knapsack knapsack = new Knapsack(ImmutableList.of(itemX, itemY, itemZ));
        Knapsack.Solution solution = knapsack.value(10);
        assertEquals(50, solution.value);
        assertEquals(ImmutableList.of(itemY, itemZ), solution.items);
        assertEquals("the optimal packing leaves two of the ten unused",
                8, solution.items.stream().mapToInt(i -> i.weight).sum());
    }

    /**
     * The edges out of a sub-problem are the two decisions about item kappa: leave
     * it (no attribute) or pack it (attributed with the item). The second is
     * present only when the item fits.
     */
    @Test
    public void graphRecordsTheTwoDecisions() {
        Knapsack knapsack = new Knapsack(ImmutableList.of(itemX, itemY, itemZ));
        knapsack.value(10);
        // At (3, 10) we decide about z, which weighs 5 and therefore fits.
        assertEquals(Map.of(new Knapsack.Key(2, 10), "leave", new Knapsack.Key(2, 5), "take"),
                decisions(knapsack, 3, 10));
        // At (1, 7) we decide about x, which weighs 10: too heavy, so only one edge.
        assertEquals(Map.of(new Knapsack.Key(0, 7), "leave"), decisions(knapsack, 1, 7));
    }

    /**
     * Every edge takes kappa down by exactly one, which is what makes the graph
     * acyclic whatever the weights happen to be.
     */
    @Test
    public void graphIsAcyclic() {
        Knapsack knapsack = new Knapsack(ImmutableList.of(itemX, itemY, itemZ));
        knapsack.value(10);
        for (Edge<Knapsack.Key, Knapsack.Item> edge : knapsack.graph().edges())
            assertEquals(edge.toString(), edge.getFrom().kappa - 1, edge.getTo().kappa);
    }

    /**
     * The graph holds only the sub-problems the search actually reached. The book
     * warns that a naive bottom-up solution "will evaluate all nW values of m,
     * including many that will never be needed"; here n is 3 and W is 10, so a
     * naive sweep would visit 30 sub-problems and we visit far fewer.
     */
    @Test
    public void graphIsBuiltLazily() {
        Knapsack knapsack = new Knapsack(ImmutableList.of(itemX, itemY, itemZ));
        assertEquals("nothing is built until we ask", 0, knapsack.graph().vertices().size());
        knapsack.value(10);
        assertEquals(12, knapsack.graph().vertices().size());
    }

    /**
     * Reports the decisions available at a sub-problem, as a map from the
     * sub-problem each one leads to. Not a list, because the adjacency bag hands
     * the edges out in a random order.
     */
    private static Map<Knapsack.Key, String> decisions(Knapsack knapsack, int kappa, int omega) {
        Map<Knapsack.Key, String> result = new HashMap<>();
        for (Edge<Knapsack.Key, Knapsack.Item> edge : knapsack.graph().adjacent(new Knapsack.Key(kappa, omega)))
            result.put(edge.getTo(), edge.getAttributes() == null ? "leave" : "take");
        return result;
    }

    @Test
    public void valueGoogle() {
        // see https://developers.google.com/optimization/pack/knapsack#java_1
        final int[] values = {360, 83, 59, 130, 431, 67, 230, 52, 93, 125, 670, 892, 600, 38, 48, 147,
                78, 256, 63, 17, 120, 164, 432, 35, 92, 110, 22, 42, 50, 323, 514, 28, 87, 73, 78, 15, 26,
                78, 210, 36, 85, 189, 274, 43, 33, 10, 19, 389, 276, 312};

        final int[] weights = {7, 0, 30, 22, 80, 94, 11, 81, 70, 64, 59, 18, 0, 36, 3, 8, 15, 42, 9,
                0, 42, 47, 52, 32, 26, 48, 55, 6, 29, 84, 2, 4, 18, 56, 7, 29, 93, 44, 71, 3, 86, 66, 31,
                65, 0, 79, 20, 65, 52, 13};

        final int[] packed = {0, 1, 3, 4, 6, 10, 11, 12, 14, 15, 16, 17, 18, 19, 21, 22, 24, 27, 28, 29, 30, 31,
                32, 34, 38, 39, 41, 42, 44, 47, 48, 49};

        int n = values.length;
        Knapsack.Item[] items = new Knapsack.Item[n];
        for (int i = 0; i < n; i++)
            items[i] = new Knapsack.Item("Item " + i, weights[i], values[i]);
        List<Knapsack.Item> list = Arrays.asList(items);
        final Knapsack.Item[] itemsPacked = new Knapsack.Item[packed.length];
        for (int i = 0; i < packed.length; i++) itemsPacked[i] = items[packed[i]];
        Knapsack knapsack = new Knapsack(list);
        Knapsack.Solution solution = knapsack.value(850);
        assertEquals(7534, solution.value);
        assertEquals(Knapsack.Solution.of(Arrays.stream(itemsPacked).toList()).items, solution.items);
        assertEquals(31373, knapsack.subProblems());
    }

    @Test
    public void valueRandom100() {
        Random random = new Random(0L);
        int n = 100;
        Knapsack.Item[] items = new Knapsack.Item[n];
        for (int i = 0; i < n; i++)
            items[i] = new Knapsack.Item("Item " + i, random.nextInt(25), random.nextInt(10));
        List<Knapsack.Item> list = Arrays.asList(items);
        Knapsack knapsack = new Knapsack(list);
        assertEquals(28, knapsack.value(5).value);
        assertEquals(518, knapsack.subProblems());
        assertEquals(36, knapsack.value(10).value);
        assertEquals(995, knapsack.subProblems());
        assertEquals(44, knapsack.value(15).value);
        assertEquals(1479, knapsack.subProblems());
        assertEquals(50, knapsack.value(20).value);
        assertEquals(1968, knapsack.subProblems());
        checkKnapsack(knapsack, n, 40, 71);
        checkKnapsack(knapsack, n, 80, 109);
    }

    @Test
    public void valueRandom200() {
        Random random = new Random(0L);
        int n = 200;
        Knapsack.Item[] items = new Knapsack.Item[n];
        for (int i = 0; i < n; i++)
            items[i] = new Knapsack.Item("Item " + i, random.nextInt(25), random.nextInt(10));
        Knapsack knapsack = new Knapsack(Arrays.asList(items));
        checkKnapsack(knapsack, n, 5, 48);
        checkKnapsack(knapsack, n, 10, 63);
        checkKnapsack(knapsack, n, 15, 75);
        checkKnapsack(knapsack, n, 20, 85);
        checkKnapsack(knapsack, n, 40, 119);
        checkKnapsack(knapsack, n, 80, 164);
    }

    private static void checkKnapsack(Knapsack knapsack, int n, int w, int expected) {
        assertEquals(expected, knapsack.value(w).value);
        double expected40 = w * n;
        assertEquals(expected40, knapsack.subProblems(), expected40 / 16.0);
    }
}