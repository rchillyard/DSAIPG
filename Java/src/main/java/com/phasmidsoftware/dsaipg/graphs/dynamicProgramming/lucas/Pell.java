package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.lucas;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * The Pell numbers, memoised: 0, 1, 2, 5, 12, 29, ...
 * <p>
 * x[i] = x[i-2] + 2 * x[i-1].
 * <p>
 * NOTE BigInteger, and here it was not merely theoretical. These grow by a factor
 * of about 2.414 each term, so they pass a 64-bit long at around n = 62 — and
 * PellTest asserted {@code get(90) == 7052354271195710746}, which is not the 90th
 * Pell number but what a long holds once that value has wrapped. The true value is
 * 9,960,168,529,794,442,859,224,531,878,561,050, which the test now asserts.
 */
public class Pell {

    /**
     * @param n which term.
     * @return the nth Pell number, computed once and remembered.
     * @throws UnsupportedOperationException if n is negative.
     */
    public BigInteger get(int n) {
        if (n < 0) throw new UnsupportedOperationException("Pell.get is not supported for negative n");
        if (n < pell.size()) return pell.get(n);
        return evaluate(n);
    }

    public Pell() {
        pell.add(0, BigInteger.ZERO);
        pell.add(1, BigInteger.ONE);
    }

    private BigInteger evaluate(int n) {
        for (int i = pell.size(); i <= n; i++)
            pell.add(i, pell.get(i - 2).add(BigInteger.TWO.multiply(pell.get(i - 1))));
        return pell.get(n);
    }

    final List<BigInteger> pell = new ArrayList<>();
}
