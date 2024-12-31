/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.Config;

import java.io.IOException;

/**
 * Class InsertionSortOpt is an optimized version of InsertionSort..
 *
 * @param <X> the underlying comparable type.
 */
public class InsertionSortOpt<X extends Comparable<X>> extends InsertionSort<X> {

    /**
     * Constructor for any subclasses to use.
     *
     * @param description the description.
     * @param N           the number of elements expected.
     * @param nRuns       the expected number of runs.
     * @param config      the configuration.
     */
    public InsertionSortOpt(String description, int N, int nRuns, Config config) {
        super(description, N, nRuns, config);
    }

    /**
     * Constructor for InsertionSort
     *
     * @param N      the number elements we expect to sort.
     * @param config the configuration.
     */
    public InsertionSortOpt(int N, Config config) {
        super(DESCRIPTION, N, 1, config);
    }

    public InsertionSortOpt(Config config) {
        this(new NonInstrumentingComparableHelper<>(DESCRIPTION, config));
    }

    /**
     * Constructor for InsertionSort
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public InsertionSortOpt(Helper<X> helper) {
        super(helper);
    }

    /**
     * Sort the sub-array xs:from:to using insertion sort.
     *
     * @param xs   sort the array xs from "from" to "to".
     * @param from the index of the first element to sort
     * @param to   the index of the first element not to sort
     */
    public void sort(X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        for (int i = from + 1; i < to; i++) {
            helper.swapIntoSorted(xs, i);
        }
    }

    /**
     * This is used by unit tests.
     *
     * @param ys  the array to be sorted.
     * @param <Y> the underlying element type.
     */
    public static <Y extends Comparable<Y>> void mutatingInsertionSort(Y[] ys) throws IOException {
        try (InsertionSort<Y> sortOpt = new InsertionSortOpt<>(Config.load(InsertionSortOpt.class))) {
            sortOpt.mutatingSort(ys);
        }
    }

    public static final String DESCRIPTION = "Insertion sort optimized";

}