/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.sort.classic.ClassicHelper;
import com.phasmidsoftware.dsaipg.util.Config;

import java.util.Comparator;
import java.util.Random;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.getSeed;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.isInstrumented;

/**
 * Class HelperFactory.
 */
public class HelperFactory {

    /**
     * Factory method to create a Helper.
     *
     * @param <X>         the underlying type.
     * @param description the description of the Helper.
     * @param nElements   the number of elements to be sorted.
     * @param config      the configuration.
     * @return a Helper&lt;X&gt;
     */
    public static <X extends Comparable<X>> Helper<X> create(String description, int nElements, Config config) {
        return create(description, nElements, isInstrumented(config), config);
    }

    /**
     * Factory method to create a Helper.
     *
     * @param description the description of the Helper.
     * @param nElements   the number of elements to be sorted.
     * @param seed        an explicit seed.
     * @param config      the configuration.
     * @param <X>         the underlying type.
     * @return a Helper&lt;X&gt;
     */
    public static <X extends Comparable<X>> NonComparableHelper<X> create(String description, int nElements, long seed, int nRuns, Config config) {
        return create(description, nElements, isInstrumented(config), seed, nRuns, config);
    }

    /**
     * CONSIDER eliminating this signature.
     *
     * @param <X>          the underlying type.
     * @param description  the description of the Helper.
     * @param nElements    the number of elements to be sorted.
     * @param instrumented an explicit value of instrumented, not derived from the config.
     * @param config       the configuration, including the value for nRuns.
     * @return a Helper&lt;X&gt;
     */
    public static <X extends Comparable<X>> Helper<X> create(String description, int nElements, boolean instrumented, Config config) {
        return instrumented ? new InstrumentedComparableHelper<>(description, nElements, config) : new NonInstrumentingComparableHelper<>(description, nElements, config);
    }

    /**
     * CONSIDER eliminating this signature.
     *
     * @param <X>          the underlying type.
     * @param description  the description of the Helper.
     * @param nElements    the number of elements to be sorted.
     * @param instrumented an explicit value of instrumented, not derived from the config.
     * @param seed         a specific value of seed for the random number source.
     * @param nRuns        the number of runs expected.
     * @param config       the configuration, including the value for nRuns.
     * @return a Helper&lt;X&gt;
     */
    public static <X extends Comparable<X>> NonComparableHelper<X> create(String description, int nElements, boolean instrumented, long seed, int nRuns, Config config) {
        return instrumented ? new InstrumentedComparableHelper<>(description, nElements, seed, nRuns, config) : new NonInstrumentingComparableHelper<>(description, nElements, seed, config);
    }

    /**
     * Factory method to create a Helper.
     * At present, the only concrete extender of Helper is ClassicHelper.
     *
     * @param <X>         the underlying type.
     * @param description the description of the Helper.
     * @param comparator  the appropriate Comparator.
     * @param nElements   the number of elements to be sorted.
     * @param nRuns       the number of runs expected.
     * @param config      the configuration, including the values for instrumented, seed, and for nRuns.
     * @return a Helper&lt;X&gt;
     */
    public static <X> Helper<X> createGeneric(String description, Comparator<X> comparator, int nElements, int nRuns, Config config) {
        if (isInstrumented(config))
            return new InstrumentedComparatorHelper<>(description, comparator, nElements, getSeed(config), nRuns, config);
        else
            return new ClassicHelper<>(description, comparator, nElements, new Random(getSeed(config)), config);
    }

}