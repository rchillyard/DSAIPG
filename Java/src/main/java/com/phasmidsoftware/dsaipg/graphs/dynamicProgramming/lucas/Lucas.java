package com.phasmidsoftware.dsaipg.graphs.dynamicProgramming.lucas;

import java.util.ArrayList;

/**
 * The Lucas class represents a numerical sequence called the Lucas series, which is similar to the Fibonacci sequence
 * but starts with different initial values. The sequence starts with 2 and 1, and each subsequent term is calculated
 * as the sum of the two preceding terms.
 */
public class Lucas {
    public Lucas() {
        lucas.add(0, 2L);
        lucas.add(1, 1L);
    }

    public long get(int n) {
        if (n < 0) throw new UnsupportedOperationException("Lucas.get is not supported for negative n");
        if (n < lucas.size()) return lucas.get(n);
        return evaluate(n);
    }

    public long bad(int n) {
        if (n < 0) throw new UnsupportedOperationException("Lucas.get is not supported for negative n");
        if (n == 0) return 2L;
        if (n == 1) return 1L;
        return bad(n - 2) + bad(n - 1);
    }

    private long evaluate(int n) {
        for (int i = lucas.size(); i <= n; i++) lucas.add(i, lucas.get(i - 2) + lucas.get(i - 1));
        return lucas.get(n);
    }

    final ArrayList<Long> lucas = new ArrayList<>();
}
