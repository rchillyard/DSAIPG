package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KnapsackTest {

    final Knapsack.Item itemA = new Knapsack.Item("A", 2, 1);
    final Knapsack.Item itemB = new Knapsack.Item("B", 1, 2);

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