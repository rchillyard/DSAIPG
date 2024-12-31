/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Config;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.getSeed;

/**
 * An abstract class that extends SortWithHelper and is designed for sorting algorithms that work with elements
 * implementing the Comparable interface. It provides constructors that can be used to initialize sorting helpers
 * with configurations or custom helper objects.
 *
 * @param <X> the type of elements handled by this helper, expected to be Comparable.
 */
public abstract class SortWithComparableHelper<X extends Comparable<X>> extends SortWithHelper<X> {

    /**
     * Constructor for initializing a SortWithComparableHelper instance with a given helper.
     *
     * @param helper the helper object used to assist with sorting operations. It provides
     *               utility methods and configurations for the sorting process.
     */
    public SortWithComparableHelper(Helper<X> helper) {
        super(helper);
    }

    /**
     * Constructs a SortWithComparableHelper with a specified description, size, number of runs, and configuration.
     * It uses a factory to create a helper instance with the provided parameters.
     *
     * @param description a string describing the helper or sorting process.
     * @param N           the size of the dataset to be processed by the helper.
     * @param nRuns       the number of runs for the sorting process or benchmark.
     * @param config      the configuration object containing additional settings like seed, etc.
     */
    public SortWithComparableHelper(String description, int N, int nRuns, Config config) {
        this(HelperFactory.create(description, N, getSeed(config), nRuns, config));
        closeHelper = true;
    }
}