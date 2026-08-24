/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.generic.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.config.Config;

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
     * Sorts the specified array within the given range using the Selection Sort algorithm.
     * The method repeatedly selects the smallest element from the unsorted section of the array
     * and moves it to the sorted section.
     *
     * Total operations:
     * swaps: O(n -1)
     * hits: n * (n - 1) / 2 + n - 1 + swaps * 4
     * compares: (n - 1) * n / 2
     *
     * @param xs   the array to be sorted.
     * @param from the starting index (inclusive) of the range to be sorted.
     * @param to   the ending index (exclusive) of the range to be sorted.
     */
    public void sort(X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        helper.incrementLookups(to - from); // one lookup per element (maybe this is a bit optimistic in real life)
        for (int i = from; i < to - 1; i++) { // n - 1 iterations
            int min = locateMinimum(xs, i, to, helper);
            // XXX no point in swapping if element i IS the minimum element
            if (i != min) helper.swap(xs, i, min); // O(4 h, 1 l, 1 s)
        }
    }

    /**
     * Locates the index of the minimum element in a specified subarray range.
     * Utilizes a helper instance to compare elements to determine the minimum.
     *
     * @param xs     the array containing the elements.
     * @param from   the starting index (inclusive) of the range to search for the minimum.
     * @param to     the ending index (exclusive) of the range to search for the minimum.
     * @param helper a helper instance providing comparison logic.
     * @return the index of the minimum element within the specified range.
     */
    int locateMinimum(X[] xs, int from, int to, Helper<X> helper) {
        int k = from;
        X min = helper.get(xs, k); // one hit, one lookup
        for (int j = from + 1; j < to; j++) { // n - 1 iterations
            X x = helper.get(xs, j);  // one hit, one lookup
            if (helper.inverted(min, x)) { // one comparison
                k = j;
                min = x;
            }
        }
        return k;
    }

    /**
     * Primary constructor for SelectionSort
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public SelectionSort(Helper<X> helper) {
        super(helper);
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
     * Constructor for any subclasses to use.
     *
     * @param N      the number of elements expected.
     * @param nRuns  the expected number of runs.
     * @param config the configuration.
     */
    public SelectionSort(int N, int nRuns, Config config) {
        super(DESCRIPTION, N, nRuns, config);
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
