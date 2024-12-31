/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.Utilities;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

/**
 * CONSIDER deleting this class.
 *
 * @param <X>
 */
public class NonInstrumentingComparatorHelper<X> extends BaseComparatorHelper<X> {
    public boolean instrumented() {
        return false;
    }

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     *
     * @param x1 the first object to be compared.
     * @param x2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is less than, equal to, or greater than the
     * second.
     */
    @Override
    public int compare(X x1, X x2) {
        return pureComparison(x1, x2);
    }

    public X[] random(int m, Class<X> clazz, Function<Random, X> f) {
        if (m <= 0)
            throw new HelperException("Helper.random: requesting zero random elements (helper not initialized?)");
        randomArray = Utilities.fillRandomArray(clazz, random, m, f);
        return randomArray;
    }

    /**
     * @param n the size to be managed.
     * @throws HelperException if n is inconsistent.
     */
    public void init(int n) {
        if (this.n == 0 || this.n == n) this.n = n;
        else throw new HelperException("Helper: n is already set to a different value");
    }

    public Helper<X> clone(String description, int N) {
        return new NonInstrumentingComparatorHelper<>(description, getComparator(), N, random, new InstrumenterDummy(), config);
    }

    @Override
    public Helper<X> clone(String description, Comparator<X> comparator, int N) {
        return new NonInstrumentingComparatorHelper<>(description, comparator, N, random, new InstrumenterDummy(), config);
    }

    /**
     * Constructor for explicit random number generator.
     *
     * @param description  the description of this Helper (for humans).
     * @param comparator   the Comparator of X to be used.
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param random       a random number generator.
     * @param instrumenter an implementation of Instrument.
     */
    public NonInstrumentingComparatorHelper(String description, Comparator<X> comparator, int n, Random random, Instrument instrumenter, Config config) {
        super(description, comparator, n, random, instrumenter, config);
    }

    /**
     * Constructor for explicit seed.
     *
     * @param description  the description of this Helper (for humans).
     * @param comparator   the Comparator of X to be used.
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param seed         the seed for the random number generator.
     * @param instrumenter an implementation of Instrument.
     */
    public NonInstrumentingComparatorHelper(String description, Comparator<X> comparator, int n, long seed, Instrument instrumenter, Config config) {
        this(description, comparator, n, new Random(seed), instrumenter, config);
    }

    /**
     * Constructor to create a Helper with a random seed.
     *
     * @param description  the description of this Helper (for humans).
     * @param comparator   the Comparator of X to be used.
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param instrumenter an implementation of Instrument.
     */
    public NonInstrumentingComparatorHelper(String description, Comparator<X> comparator, int n, Instrument instrumenter, Config config) {
        this(description, comparator, n, System.currentTimeMillis(), instrumenter, config);
    }

    /**
     * Constructor to create a Helper with a random seed and an n value of 0.
     *
     * @param description  the description of this Helper (for humans).
     * @param comparator   the Comparator of X to be used.
     * @param instrumenter an implementation of Instrument.
     * @param config       the config.
     */
    public NonInstrumentingComparatorHelper(String description, Comparator<X> comparator, Instrument instrumenter, Config config) {
        this(description, comparator, 0, instrumenter, config);
    }

    public static final String INSTRUMENT = "instrument";

    /**
     * Keep track of the random array that was generated. This is available via the InstrumentedHelper class.
     */
    protected X[] randomArray;

    public static class HelperException extends RuntimeException {

        public HelperException(String message) {
            super(message);
        }

        public HelperException(String message, Throwable cause) {
            super(message, cause);
        }

        public HelperException(Throwable cause) {
            super(cause);
        }

        public HelperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}