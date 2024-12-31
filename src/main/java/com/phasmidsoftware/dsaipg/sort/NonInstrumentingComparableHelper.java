/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.Utilities;

import java.util.Random;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.isInstrumented;

public class NonInstrumentingComparableHelper<X extends Comparable<X>> extends BaseComparableHelper<X> {
    public boolean instrumented() {
        return false;
    }

    /**
     * Static method to get a Helper configured for the given class.
     *
     * @param clazz the class for configuration.
     * @param <Y>   the type.
     * @return a Helper&lt;Y&gt;
     */
    public static <Y extends Comparable<Y>> Helper<Y> getHelper(final Class<?> clazz) {
        try {
            return new NonInstrumentingComparableHelper<>("Standard ComparableHelper", Config.load(clazz));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Helper<X> clone(String description, int N) {
        return new NonInstrumentingComparableHelper<>(description, N, config);
    }

    public static <X extends Comparable<X>> Helper<X> create(String description, Config config) {
        return (isInstrumented(config)) ? new InstrumentedComparableHelper<>(description, config) : new NonInstrumentingComparableHelper<>(description, config);
    }

    public X[] random(int m, Class<X> clazz, Function<Random, X> f) {
        if (m <= 0)
            throw new HelperException("Helper.random: requesting zero random elements (helper not initialized?)");
        randomArray = Utilities.fillRandomArray(clazz, random, m, f);
        return randomArray;
    }

    @Override
    public String toString() {
        // CONSIDER swapping order of description and Helper for... (see also overrides)
        return "Helper for " + description + " with " + n + " elements" + (instrumented() ? " instrumented" : "");
    }

    /**
     * Constructor for explicit random number generator.
     *
     * @param description the description of this Helper (for humans).
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param random      a random number generator.
     */
    public NonInstrumentingComparableHelper(String description, int n, Random random, Config config) {
        super(description, n, random, new InstrumenterDummy(), config);
    }

    /**
     * Constructor for explicit seed.
     *
     * @param description the description of this Helper (for humans).
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param seed        the seed for the random number generator.
     */
    public NonInstrumentingComparableHelper(String description, int n, long seed, Config config) {
        this(description, n, new Random(seed), config);
    }

    /**
     * Constructor to create a Helper with a random seed.
     *
     * @param description the description of this Helper (for humans).
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     */
    public NonInstrumentingComparableHelper(String description, int n, Config config) {
        this(description, n, System.currentTimeMillis(), config);
    }

    /**
     * Constructor to create a Helper with a random seed and an n value of 0.
     *
     * @param description the description of this Helper (for humans).
     */
    public NonInstrumentingComparableHelper(String description, Config config) {
        this(description, 0, config);
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