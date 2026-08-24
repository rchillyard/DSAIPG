package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.general.Utilities;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.HELPER;
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.isInstrumented;

/**
 * A helper class extending BaseComparableHelper to assist with sorting and comparisons
 * without instrumentation. It simplifies and abstracts operations for working with
 * Comparable types, adding constructors and methods for managing elements and randomness.
 *
 * @param <X> the type of elements this helper works with, extending Comparable.
 */
public class NonInstrumentingComparatorHelper<X> extends BaseComparatorHelper<X> {

    /**
     * Compares two objects of type X using a predefined comparator.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument
     * is less than, equal to, or greater than the second.
     */
    public int compare(X o1, X o2) {
        return getComparator().compare(o1, o2);
    }

    public static final String INSTRUMENT = "instrument";

    /**
     * Static method to get a Helper configured for the given class.
     *
     * @param clazz the class for configuration.
     * @param <Y>   the type.
     * @return a Helper&lt;Y&gt;
     */
    public static <Y> Helper<Y> getHelper(final Comparator<Y> comparator, final Class<?> clazz) {
        try {
            return new NonInstrumentingComparatorHelper<>("Standard ComparableHelper", comparator, Config.load(clazz));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Factory method to create an instance of a {@code Helper} for a given description and configuration.
     * Depending on the configuration, it returns either an instrumented or non-instrumenting helper.
     *
     * @param <X>         the type parameter, which must implement {@code Comparable<X>}.
     * @param description a brief description of the helper instance being created.
     * @param config      the configuration object used to determine the type of helper to create.
     * @return an instance of {@code Helper<X>}, either instrumented or non-instrumenting based on the configuration.
     */
    public static <X> Helper<X> create(String description, Comparator<X> comparator, Config config) {
        return (isInstrumented(config)) ? new InstrumentedComparatorHelper<>(description, comparator, config) : new NonInstrumentingComparatorHelper<>(description, comparator, config);
    }

    /**
     * Determines whether instrumentation is enabled for this helper.
     *
     * @return false as instrumentation is not supported by this helper.
     */
    public boolean instrumented() {
        return false;
    }

    /**
     * Performs any necessary post-processing on the provided array of elements.
     * This implementation does nothing by default and can be overridden as needed.
     *
     * @param xs an array of elements of type X to be post-processed
     */
    @Override
    public void postProcess(X[] xs) {
        super.postProcess(xs);
        if (checkSorted && !isSorted(xs))
            throw new HelperException("NonInstrumentingComparatorHelper.postProcess: array is not sorted");
    }

    /**
     * Creates a new instance of {@code NonInstrumentingComparableHelper} based on the given description and size.
     *
     * @param description       a brief description for the helper instance being cloned.
     * @param N                 the number of elements relevant to the helper instance.
     * @param shareInstrumenter
     * @return a cloned instance of {@code Helper<X>}, specifically a {@code NonInstrumentingComparableHelper}.
     */
    public Helper<X> clone(String description, int N, boolean shareInstrumenter) {
        return new NonInstrumentingComparatorHelper<>(description, getComparator(), N, config);
    }

    /**
     * Generates an array of random elements of type X.
     *
     * @param m     the number of random elements to generate; must be greater than 0.
     * @param clazz the class object representing the type X.
     * @param f     a function that takes a Random object and generates a value of type X.
     * @return an array of type X filled with random elements generated using the provided function.
     * @throws HelperException if m is less than or equal to 0.
     */
    public X[] random(int m, Class<X> clazz, Function<Random, X> f) {
        if (m <= 0)
            throw new HelperException("Helper.random: requesting zero random elements (helper not initialized?)");
        randomArray = Utilities.fillRandomArray(clazz, random, m, f);
        return randomArray;
    }

    /**
     * Provides a string representation of the NonInstrumentingComparableHelper instance.
     * The representation includes the description of the helper, the number of elements,
     * and whether it is instrumented or not.
     *
     * @return a string containing the helper description, number of elements, and instrumentation status.
     */
    @Override
    public String toString() {
        // CONSIDER swapping order of description and Helper for... (see also overrides)
        return "Helper for " + description + " with " + n + " elements" + (instrumented() ? " instrumented" : "");
    }

    /**
     * Constructor for explicit random number generator.
     *
     * @param description the description of this Helper (for humans).
     * @param comparator
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param random      a random number generator.
     */
    public NonInstrumentingComparatorHelper(String description, Comparator<X> comparator, int n, Random random, Config config) {
        super(description, comparator, n, random, new InstrumenterDummy(), config);
        checkSorted = config.getBoolean(HELPER, "checksorted");
    }

    /**
     * Constructor for explicit seed.
     *
     * @param description the description of this Helper (for humans).
     * @param comparator
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param seed        the seed for the random number generator.
     */
    public NonInstrumentingComparatorHelper(String description, Comparator<X> comparator, int n, long seed, Config config) {
        this(description, comparator, n, new Random(seed), config);
    }

    /**
     * Constructor to create a Helper with a random seed.
     *
     * @param description the description of this Helper (for humans).
     * @param comparator
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     */
    public NonInstrumentingComparatorHelper(String description, Comparator<X> comparator, int n, Config config) {
        this(description, comparator, n, System.currentTimeMillis(), config);
    }

    /**
     * Constructor to create a Helper with a random seed and an n value of 0.
     *
     * @param description the description of this Helper (for humans).
     * @param comparator
     */
    public NonInstrumentingComparatorHelper(String description, Comparator<X> comparator, Config config) {
        this(description, comparator, 0, config);
    }

    /**
     * Keep track of the random array that was generated. This is available via the InstrumentedHelper class.
     */
    protected X[] randomArray;

    private final boolean checkSorted;

    /**
     * A custom runtime exception used within the context of helper-related operations.
     * HelperException is intended to signal specific runtime errors that occur within
     * the functionalities of the NonInstrumentingComparableHelper class or its associated methods.
     */
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
