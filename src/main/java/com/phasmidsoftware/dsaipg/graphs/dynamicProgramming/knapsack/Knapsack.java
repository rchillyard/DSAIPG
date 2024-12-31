/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import com.google.common.collect.ImmutableList;
import com.phasmidsoftware.dsaipg.adt.bqs.Dictionary;
import com.phasmidsoftware.dsaipg.adt.bqs.Dictionary_Hash;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Class to implement 0-1 Knapsack problem using dynamic programming.
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

    int subProblems() {
        return memo.size();
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
        Solution r = mu(kappa - 1, omega);
        Item item = items.get(kappa - 1);
        if (item.weight > omega) value = r;
        else {
            Solution l = mu(kappa - 1, omega - item.weight).increment(item);
            value = l.compareTo(r) > 0 ? l : r;
        }
        memo.put(key, value);
        return value;
    }

    public Knapsack(List<Item> items) {
        this.items = items;
    }

    private final List<Item> items;
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
            if (value == -1) return this;
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

        private final int kappa;
        private final int omega;
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