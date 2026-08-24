package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.lucas;

import java.util.ArrayList;

/**
 * The Pell class represents a numerical sequence called the Pell sequence.
 * The Pell numbers are a series of integers where each number is defined by the recurrence relation:
 * P(n) = 2 * P(n-1) + P(n-2), with the initial values P(0) = 0 and P(1) = 1.
 * <p>
 * This implementation uses memoization to compute Pell numbers efficiently, storing results
 * in an internal list to avoid redundant calculations.
 */
public class Pell {

    /**
     * Retrieves the nth Pell number.
     * If the value is already computed and stored in the internal list, it is returned directly.
     * Otherwise, the value is calculated using the recurrence relation, memoized, and returned.
     *
     * @param n The index of the Pell number to retrieve. Must be non-negative.
     * @return The nth Pell number.
     * @throws UnsupportedOperationException If n is negative.
     */
    public long get(int n) {
        if (n < 0) throw new UnsupportedOperationException("Pell.get is not supported for negative n");
        if (n < pell.size()) return pell.get(n);
        return evaluate(n);
    }

    /**
     * Constructs a new Pell object and initializes the base cases of the Pell sequence.
     * The Pell sequence begins with the first two numbers P(0) = 0 and P(1) = 1.
     * These values are precomputed and stored in the internal list for subsequent calculations.
     */
    public Pell() {
        pell.add(0, 0L);
        pell.add(1, 1L);
    }

    private long evaluate(int n) {
        for (int i = pell.size(); i <= n; i++) pell.add(i, pell.get(i - 2) + 2 * pell.get(i - 1));
        return pell.get(n);
    }

    final ArrayList<Long> pell = new ArrayList<>();
}
