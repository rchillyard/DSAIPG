package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.lucas;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * The Fibonacci numbers, memoised: 1, 1, 2, 3, 5, 8, ...
 * <p>
 * NOTE these are BigIntegers. They used to be ints, which overflow silently at
 * n = 47 — fib(47) is 4,807,526,976 against an int maximum of 2,147,483,647 — and
 * the test stopped at get(7), so nothing ever showed it. A sequence class whose
 * whole purpose is to produce large terms should not have a ceiling it does not
 * mention, and the Python port has none, its integers being arbitrary precision.
 */
public class Fibonacci {

    /**
     * @param n which term.
     * @return the nth Fibonacci number.
     * @throws UnsupportedOperationException if n is negative.
     */
    public BigInteger get(int n) {
        if (n < 0) throw new UnsupportedOperationException("Fibonacci.get is not supported for negative n");
        if (n < fib.size()) return fib.get(n);
        return evaluate(n);
    }

    public Fibonacci() {
        fib.add(0, BigInteger.ONE);
        fib.add(1, BigInteger.ONE);
    }

    private BigInteger evaluate(int n) {
        for (int i = fib.size(); i <= n; i++) fib.add(i, fib.get(i - 2).add(fib.get(i - 1)));
        return fib.get(n);
    }

    final List<BigInteger> fib = new ArrayList<>();
}
