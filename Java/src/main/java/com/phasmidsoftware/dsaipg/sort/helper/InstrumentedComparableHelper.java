package com.phasmidsoftware.dsaipg.sort.helper;

import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.Random;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.getSeed;

/**
 * Helper class for sorting methods with instrumentation of compares and swaps, and in addition, bounds checks.
 * This Helper class may be used for analyzing sort methods but will run at slightly slower speeds than the superclass.
 *
 * @param <X> the underlying type (must be Comparable).
 */
public class InstrumentedComparableHelper<X extends Comparable<X>> extends InstrumentedComparatorHelper<X> {

    /**
     * Creates a duplicate of the current Helper instance configured with the specified description and number of elements.
     *
     * @param description       the description of the cloned Helper (for humans).
     * @param N                 the number of elements expected to be handled by the cloned Helper.
     * @param shareInstrumenter
     * @return a new instance of Helper with the same configuration as the original, but updated with the provided parameters.
     */
    public Helper<X> clone(String description, int N, boolean shareInstrumenter) {
        Instrument instrumenter = shareInstrumenter ? this.instrumenter : new Instrumenter(config);
        return new InstrumentedComparableHelper<>(description, N, random, nRuns, instrumenter, config);
    }

    /**
     * Constructor for explicit random number generator.
     *
     * @param description  the description of this Helper (for humans).
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param random       a random number generator.
     * @param nRuns        an (explicit) number of runs (for statistics).
     * @param instrumenter an implementer of Instrument.
     * @param config       the configuration (note that the seed value is ignored).
     */
    public InstrumentedComparableHelper(String description, int n, Random random, int nRuns, final Instrument instrumenter, Config config) {
        super(description, Comparable::compareTo, n, random, nRuns, instrumenter, config);
    }

    /**
     * Constructor to create an InstrumentedComparableHelper instance with an inferred number of runs from configuration.
     *
     * @param description the description of this Helper (for humans).
     * @param n           the number of elements expected to be sorted. This value is mutable and can be set after the constructor.
     * @param config      the configuration object.
     */
    public InstrumentedComparableHelper(String description, int n, Config config) {
        this(description, n, getRunsConfig(config), config);
    }

    /**
     * Constructor to create a Helper
     *
     * @param description the description of this Helper (for humans).
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param nRuns       an (explicit) number of runs (for statistics).
     * @param config      The configuration.
     */
    public InstrumentedComparableHelper(String description, int n, int nRuns, Config config) {
        this(description, n, getSeed(config), nRuns, config);
    }

    /**
     * Constructor to create a Helper
     *
     * @param description the description of this Helper (for humans).
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param seed        the seed for the random number generator.
     * @param nRuns       the explicit number of runs expected.
     * @param config      the configuration.
     */
    public InstrumentedComparableHelper(String description, int n, long seed, int nRuns, Config config) {
        this(description, n, new Random(seed), nRuns, new Instrumenter(config), config);
    }

    /**
     * Constructor to create an instance of InstrumentedComparableHelper.
     *
     * @param description the description of this Helper (for humans).
     * @param nElements   the number of elements expected to be sorted.
     * @param seed        the seed for the random number generator.
     * @param config      the configuration object.
     */
    public InstrumentedComparableHelper(String description, int nElements, long seed, Config config) {
        this(description, nElements, new Random(seed), getRunsConfig(config), new Instrumenter(config), config);
    }

    /**
     * Constructor to create a Helper with a random seed and an n value of 0.
     * <p>
     * NOTE: this constructor is used only by unit tests
     *
     * @param description the description of this Helper (for humans).
     */
    public InstrumentedComparableHelper(String description, Config config) {
        this(description, 0, config);
    }
}