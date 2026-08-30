package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.lucas;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * The Lucas numbers, memoised: 2, 1, 3, 4, 7, 11, ...
 * <p>
 * The same recurrence as Fibonacci, seeded 2 and 1.
 * <p>
 * NOTE BigInteger rather than long, for the reason given in Fibonacci: these
 * sequences exist to grow, and a silent ceiling is the wrong behaviour for them.
 */
public class Lucas {

    /**
     * @param n which term.
     * @return the nth Lucas number, computed once and remembered.
     * @throws UnsupportedOperationException if n is negative.
     */
    public BigInteger get(int n) {
        if (n < 0) throw new UnsupportedOperationException("Lucas.get is not supported for negative n");
        if (n < lucas.size()) return lucas.get(n);
        return evaluate(n);
    }

    /**
     * The same sequence by naive recursion, which recomputes both predecessors
     * every time and so costs exponentially. Kept because contrasting it with
     * {@link #get} is the point of this package.
     *
     * @param n which term.
     * @return the nth Lucas number.
     * @throws UnsupportedOperationException if n is negative.
     */
    public BigInteger bad(int n) {
        if (n < 0) throw new UnsupportedOperationException("Lucas.get is not supported for negative n");
        if (n == 0) return BigInteger.TWO;
        if (n == 1) return BigInteger.ONE;
        return bad(n - 2).add(bad(n - 1));
    }

    public Lucas() {
        lucas.add(0, BigInteger.TWO);
        lucas.add(1, BigInteger.ONE);
    }

    private BigInteger evaluate(int n) {
        for (int i = lucas.size(); i <= n; i++) lucas.add(i, lucas.get(i - 2).add(lucas.get(i - 1)));
        return lucas.get(n);
    }

    final List<BigInteger> lucas = new ArrayList<>();
}
