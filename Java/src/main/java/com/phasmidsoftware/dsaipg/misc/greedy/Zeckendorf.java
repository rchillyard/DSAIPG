package com.phasmidsoftware.dsaipg.misc.greedy;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class models Zeckendorf's Theorem: <a href="https://en.wikipedia.org/wiki/Zeckendorf%27s_theorem">...</a>
 * It is an example of a greedy algorithm.
 * This class does all its work directly (without using the Greedy class);
 * for an example of Zeckendorf which does use Greedy, please see GreedyTest.java
 */
public class Zeckendorf {

    /**
     * Get the Zeckendorf representation of x
     *
     * @param x a positive number
     * @return a list of longs, each of which is a non-consecutive Fibonacci number, and which sum to x
     */
    public Iterable<Long> get(long x) {
        fibonacci.ensure(x);
        return getZeckendorfRepresentation(x);
    }

    /**
     * Computes the Zeckendorf representation of a given positive number.
     * Zeckendorf representation expresses a number as the sum of non-consecutive Fibonacci numbers.
     *
     * @param x a positive number for which the Zeckendorf representation is to be calculated.
     * @return an Iterable of long values, where each value is a distinct non-consecutive Fibonacci
     * number that sums to the given number x.
     */
    // This method gets the Zeckendorf representation for x
    private Iterable<Long> getZeckendorfRepresentation(long x) {
        Collection<Long> result = new ArrayList<>();
        long remainder = x;
        while (remainder > 0) {
            long greedy = fibonacci.getLargest(remainder);
            result.add(greedy);
            remainder = remainder - greedy;
        }
        return result;
    }

    /**
     * Default constructor for the Zeckendorf class.
     *
     * This constructor initializes an instance of the Fibonacci class.
     * The Fibonacci class is used to compute and cache Fibonacci numbers needed
     * for Zeckendorf decomposition.
     */
    public Zeckendorf() {
        fibonacci = new Fibonacci();
    }

    private final Fibonacci fibonacci;

}
