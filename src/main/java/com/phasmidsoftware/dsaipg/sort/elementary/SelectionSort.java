/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.sort.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.util.Config;

import java.io.IOException;

/**
 * A class that implements the Selection Sort algorithm for arrays of elements
 * that implement the Comparable interface. This sorting algorithm works by
 * repeatedly selecting the smallest element from an unsorted section of the
 * array and moving it to the sorted section.
 *
 * @param <X> the type of elements to be sorted, expected to be Comparable.
 */
public class SelectionSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    /**
     * Constructor for SelectionSort
     *
     * @param N      the number elements we expect to sort.
     * @param config the configuration.
     */
    public SelectionSort(int N, Config config) {
        super(DESCRIPTION, N, 1, config);
    }

    /**
     * Constructor for SelectionSort that initializes the sort with a NonInstrumentingComparableHelper
     * using a specified configuration.
     *
     * @param config the configuration to be applied for the sort.
     */
    public SelectionSort(Config config) {
        this(new NonInstrumentingComparableHelper<>(DESCRIPTION, config));
    }

    /**
     * Constructor for SelectionSort
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public SelectionSort(Helper<X> helper) {
        super(helper);
    }

    /**
     * Sorts the specified array within the given range using the Selection Sort algorithm.
     * The method repeatedly selects the smallest element from the unsorted section of the array
     * and moves it to the sorted section.
     *
     * @param xs   the array to be sorted.
     * @param from the starting index (inclusive) of the range to be sorted.
     * @param to   the ending index (exclusive) of the range to be sorted.
     */
    public void sort(X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        for (int i = from; i < to - 1; i++) {
            int min = i;
            for (int j = i + 1; j < to; j++)
                if (helper.less(xs[j], xs[min]))
                    min = j;
            helper.swap(xs, i, min);
        }
    }

    /**
     * This is used by unit tests.
     *
     * @param ys  the array to be sorted.
     * @param <Y> the underlying element type.
     */
    public static <Y extends Comparable<Y>> void mutatingSelectionSort(Y[] ys) throws IOException {
        try (SortWithComparableHelper<Y> sort = new SelectionSort<>(Config.load(SelectionSort.class))) {
            sort.mutatingSort(ys);
        }
    }

    public static final String DESCRIPTION = "Selection sort";

}