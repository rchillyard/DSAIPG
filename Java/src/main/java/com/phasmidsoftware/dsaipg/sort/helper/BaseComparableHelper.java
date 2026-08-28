package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.Comparator;
import java.util.Random;

/**
 * A base class that provides helper functionality for objects that implement the Comparable interface.
 * This abstract class extends the BaseHelper class and implements the ComparableHelper interface.
 * It provides constructors to handle different initialization cases and includes utility methods for comparison.
 *
 * @param <X> the type of the elements that this helper will work with, extending Comparable.
 */
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

    /**
     * Creates a new instance of Helper with the specified parameters.
     *
     * @param description       the description of the Helper for identification purposes.
     * @param comparator        the Comparator used for comparing elements of type X.
     * @param N                 the expected number of elements to be handled by the Helper.
     * @param shareInstrumenter ignored.
     * @return never returns.
     * @throws SortException always.
     */
    public Helper<X> clone(String description, Comparator<X> comparator, int N, boolean shareInstrumenter) {
        // NOTE unlike BaseComparatorHelper, whose equivalent throw was an
        // unimplemented placeholder and is now abstract, this one is a genuine
        // refusal and stays. A Comparable Helper orders elements by their own
        // compareTo, so it holds no comparator and there is nothing for a
        // substitute to replace: honouring one would mean returning a Helper from
        // the other family entirely. Use a Comparator Helper if the ordering needs
        // to vary.
        throw new SortException("clone: a Comparable Helper sorts by natural ordering, "
                + "so it cannot take a Comparator: use a Comparator Helper instead");
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
