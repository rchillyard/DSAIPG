package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.Comparator;
import java.util.Random;

/**
 * Abstract class that extends from BaseHelper and implements NonComparableHelper
 * to provide additional functionality for handling comparisons of elements of type X
 * using a specified Comparator. It is intended to be used as a base class for helpers
 * that perform sorting or similar operations relying on comparison logic.
 *
 * @param <X> The type of elements to be compared.
 */
public abstract class BaseComparatorHelper<X> extends BaseHelper<X> implements NonComparableHelper<X> {

    /**
     * Retrieves the comparator used for comparing instances of type X.
     *
     * @return the Comparator of type X.
     */
    public Comparator<X> getComparator() {
        return comparator;
    }

    /**
     * Use compareTo on the X type to do a comparison.
     *
     * @param x1 the first X value.
     * @param x2 the second X value.
     * @return the result of x1.compareTo(x2).
     */
    public int pureComparison(X x1, X x2) {
        return comparator.compare(x1, x2);
    }

    /**
     * Creates and returns a clone of the Helper with the specified Comparator.
     * <p>
     * NOTE abstract, not a throwing implementation. It used to throw
     * SortException("not implementable"), which turned "this subclass has not
     * implemented clone" from a compile error into a runtime one — and that is
     * exactly what happened: NonInstrumentingComparatorHelper never overrode it,
     * inherited the throw, and nobody noticed until ClassicHelper stopped carrying
     * a copy of its own. A class holding a Comparator can always clone itself with
     * a different one, so there is nothing here to refuse.
     *
     * @param description       a description of the Helper.
     * @param comparator        the Comparator of type X to be used for comparisons.
     * @param N                 the number of elements expected to be sorted.
     * @param shareInstrumenter
     * @return a cloned instance of Helper parameterized with type X.
     */
    public abstract Helper<X> clone(String description, Comparator<X> comparator, int N, boolean shareInstrumenter);

    /**
     * Constructor for explicit random number generator.
     *
     * @param description  the description of this Helper (for humans).
     * @param comparator   the Comparator of X to be used.
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param random       a random number generator.
     * @param instrumenter an instance of Instrument.
     * @param config       the configuration to be used.
     */
    public BaseComparatorHelper(String description, Comparator<X> comparator, int n, Random random, Instrument instrumenter, Config config) {
        super(description, random, instrumenter, config, n);
        this.comparator = comparator;
    }

    /**
     * Constructor for explicit seed.
     *
     * @param description  the description of this Helper (for humans).
     * @param comparator   the Comparator of X to be used.
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param seed         the seed for the random number generator.
     * @param instrumenter an instance of Instrument.
     * @param config       the configuration to be used.
     */
    public BaseComparatorHelper(String description, Comparator<X> comparator, int n, long seed, Instrument instrumenter, Config config) {
        this(description, comparator, n, new Random(seed), instrumenter, config);
    }

    /**
     * Constructor to create a Helper with a random seed.
     *
     * @param description  the description of this Helper (for humans).
     * @param comparator   the Comparator of X to be used.
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param instrumenter an instance of Instrument.
     */
    public BaseComparatorHelper(String description, Comparator<X> comparator, int n, Instrument instrumenter, Config config) {
        this(description, comparator, n, System.currentTimeMillis(), instrumenter, config);
    }

    /**
     * Constructor to create a Helper with a random seed and an n value of 0.
     *
     * @param description  the description of this Helper (for humans).
     * @param comparator   the Comparator of X to be used.
     * @param instrumenter an instance of Instrument.
     * @param config       the config.
     */
    public BaseComparatorHelper(String description, Comparator<X> comparator, Instrument instrumenter, Config config) {
        this(description, comparator, 0, instrumenter, config);
    }

    private final Comparator<X> comparator;
}
