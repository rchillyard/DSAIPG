/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;

import java.util.Comparator;
import java.util.Random;

public abstract class BaseComparableHelper<X extends Comparable<X>> extends BaseHelper<X> implements ComparableHelper<X> {

    /**
     * Use compareTo on the X type to do a comparison.
     *
     * @param x1 the first X value.
     * @param x2 the second X value.
     * @return the result of x1.compareTo(x2).
     */
    public int pureComparison(X x1, X x2) {
        return x1.compareTo(x2);
    }

    public Helper<X> clone(String description, Comparator<X> comparator, int N) {
        throw new SortException("not implementable");
    }

    /**
     * Constructor for explicit random number generator.
     *
     * @param description  the description of this Helper (for humans).
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param random       a random number generator.
     * @param instrumenter an instance of Instrument.
     * @param config       the configuration to be used.
     */
    public BaseComparableHelper(String description, int n, Random random, Instrument instrumenter, Config config) {
        super(description, random, instrumenter, config, n);
    }

    /**
     * Constructor for explicit seed.
     *
     * @param description  the description of this Helper (for humans).
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param seed         the seed for the random number generator.
     * @param instrumenter an instance of Instrument.
     * @param config       the configuration to be used.
     */
    public BaseComparableHelper(String description, int n, long seed, Instrument instrumenter, Config config) {
        this(description, n, new Random(seed), instrumenter, config);
    }

    /**
     * Constructor to create a Helper with a random seed.
     *
     * @param description  the description of this Helper (for humans).
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param instrumenter an instance of Instrument.
     */
    public BaseComparableHelper(String description, int n, Instrument instrumenter, Config config) {
        this(description, n, System.currentTimeMillis(), instrumenter, config);
    }

    /**
     * Constructor to create a Helper with a random seed and an n value of 0.
     *
     * @param description  the description of this Helper (for humans).
     * @param instrumenter an instance of Instrument.
     * @param config       the config.
     */
    public BaseComparableHelper(String description, Instrument instrumenter, Config config) {
        this(description, 0, instrumenter, config);
    }

    /**
     * Constructor for explicit seed.
     *
     * @param description the description of this Helper (for humans).
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param seed        the seed for the random number generator.
     */
    public BaseComparableHelper(String description, int n, long seed, Config config) {
        this(description, n, new Random(seed), new InstrumenterDummy(), config);
    }

    /**
     * Constructor to create a Helper with a random seed.
     *
     * @param description the description of this Helper (for humans).
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     */
    public BaseComparableHelper(String description, int n, Config config) {
        this(description, n, System.currentTimeMillis(), config);
    }

    /**
     * Constructor to create a Helper with a random seed and an n value of 0.
     *
     * @param description the description of this Helper (for humans).
     */
    public BaseComparableHelper(String description, Config config) {
        this(description, 0, config);
    }


}