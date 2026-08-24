package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.lucas;

import java.util.ArrayList;

/**
 * The Fibonacci class provides a way to compute the Fibonacci sequence.
 * The Fibonacci sequence is a series of numbers where each number is the sum of the two preceding ones,
 * traditionally starting with 1 and 1.
 * <p>
 * This implementation uses memoization to optimize the computation by storing previously calculated results
 * in an internal list.
 */
public class Fibonacci {

    /**
     * Retrieves the nth Fibonacci number.
     * If the value is already computed and stored, it is returned directly.
     * Otherwise, the value is calculated, memoized, and returned.
     *
     * @param n The index of the Fibonacci number to retrieve. Must be non-negative.
     * @return The nth Fibonacci number.
     * @throws UnsupportedOperationException If n is negative.
     */
    public int get(int n) {
        if (n < 0) throw new UnsupportedOperationException("Fibonacci.get is not supported for negative n");
        if (n < fib.size()) return fib.get(n);
        return evaluate(n);
    }

    /**
     * Constructs a new Fibonacci object and initializes the base cases of the Fibonacci sequence.
     * The Fibonacci sequence always starts with the first two numbers as 1 and 1.
     * These values are precomputed and stored in the internal list for subsequent calculations.
     */
    public Fibonacci() {
        fib.add(0, 1);
        fib.add(1, 1);
    }

    private int evaluate(int n) {
        for (int i = fib.size(); i <= n; i++) fib.add(i, fib.get(i - 2) + fib.get(i - 1));
        return fib.get(n);
    }

    final ArrayList<Integer> fib = new ArrayList<>();
}
