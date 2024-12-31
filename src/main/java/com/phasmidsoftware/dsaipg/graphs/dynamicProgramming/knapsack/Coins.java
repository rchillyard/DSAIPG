/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.knapsack;

import com.google.common.collect.ImmutableList;
import com.phasmidsoftware.dsaipg.adt.bqs.Dictionary;
import com.phasmidsoftware.dsaipg.adt.bqs.Dictionary_Hash;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class to implement the Coin-change problem using dynamic programming.
 */
public class Coins {

    /**
     * Inner class to represent a (sub) solution of the Coins problem.
     */
    class Solution implements Comparable<Solution> {

        public int compareTo(Solution o) {
            return Integer.compare(n, o.n);
        }

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

        /**
         * The number of coins in this Solution.
         */
        final int n;
        /**
         * The numbers of each denomination in this Solution.
         */
        final int[] counts;

        public Solution(int n, int[] counts) {
            this.n = n;
            this.counts = counts;
        }
    }

    /**
     * Method to get the minimum number of coins required to total <code>amount</code>.
     *
     * @param amount the amount to be given in change.
     * @return the minimum number of coins required.
     */
    Solution number(int amount) {
        return mu(amount);
    }

    int subProblems() {
        return memo.size();
    }

    /**
     * Recursive (private) method. It's package-protected here to make it easy to test.
     *
     * @param amount the amount of change required.
     * @return the minimum number of coins which can represent the amount.
     */
    Solution mu(int amount) {
        Solution result = memo.get(amount);
        if (result != null) return result;
        if (amount < 0) return nullSolution;
        if (amount == 0) return zero;
        Solution[] subsolutions = new Solution[coins.size()];
        for (int i = 0; i < subsolutions.length; i++) subsolutions[i] = mu(amount - coins.get(i)).increment(i);
        result = nullSolution;
        for (Solution option : subsolutions) if (result.compareTo(option) > 0) result = option;
        result.validate();
        memo.put(amount, result);
        return result;
    }

    public Coins(List<Integer> coins) {
        this.coins = coins;
    }

    public Coins() {
        this(US);
    }

    /**
     * CONSIDER adding a parameter for the number of denominations.
     *
     * @return an array of all zeros.
     */
    int[] zeros() {
        return new int[]{0, 0, 0, 0};
    }

    final Solution zero = new Solution(0, zeros());

    final static List<Integer> US = ImmutableList.of(1, 5, 10, 25);

    private final Solution nullSolution = new Solution(Integer.MAX_VALUE, zeros());
    private final List<Integer> coins;

    // The following is to memoize the sub-solutions: key is of type Key, and value is value (an Integer).
    private final Dictionary<Integer, Solution> memo = new Dictionary_Hash<>();

}