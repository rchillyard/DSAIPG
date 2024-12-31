/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.sort.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.util.Config;

/**
 * The InsertionSort class implements the insertion sort algorithm for sorting an array of elements
 * that implement the Comparable interface. It extends the SortWithComparableHelper class and provides
 * various constructors for configuration and helper injection.
 *
 * @param <X> the type of elements to be sorted, which must implement the Comparable interface.
 */
public class InsertionSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    /**
     * Constructor for any subclasses to use.
     *
     * @param description the description.
     * @param N           the number of elements expected.
     * @param nRuns       the expected number of runs.
     * @param config      the configuration.
     */
    public InsertionSort(String description, int N, int nRuns, Config config) {
        super(description, N, nRuns, config);
    }

    /**
     * Constructor for InsertionSort
     *
     * @param N      the number elements we expect to sort.
     * @param config the configuration.
     */
    public InsertionSort(int N, Config config) {
        this(DESCRIPTION, N, 1, config);
    }

    /**
     * Constructor for InsertionSort with a given configuration.
     * Utilizes a NonInstrumentingComparableHelper to set up sorting.
     *
     * @param config the configuration to be used for this instance of InsertionSort.
     */
    public InsertionSort(Config config) {
        this(NonInstrumentingComparableHelper.create(DESCRIPTION, config));
    }

    /**
     * Constructor for InsertionSort
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public InsertionSort(Helper<X> helper) {
        super(helper);
    }

    /**
     * Default constructor for the InsertionSort class.
     * Initializes the InsertionSort instance with a NonInstrumentingComparableHelper.
     * This helper is configured specifically for the InsertionSort class.
     */
    public InsertionSort() {
        this(NonInstrumentingComparableHelper.getHelper(InsertionSort.class));
    }

    /**
     * Sort the subarray xs:from:to using insertion sort.
     *
     * @param xs   sort the array xs from "from" to "to".
     * @param from the index of the first element to sort
     * @param to   the index of the first element not to sort
     */
    public void sort(X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        X a = helper.get(xs, from);
        for (int i = from + 1; i < to; i++) a = doInsert(xs, from, i, a, helper.get(xs, i), helper);
    }

    public static final String DESCRIPTION = "Insertion sort";

    /**
     * Sorts an array of elements using the InsertionSort algorithm.
     * The provided array is sorted in-place in ascending order based on the natural ordering of the elements.
     *
     * @param <T> the type of elements in the array, which must implement the Comparable interface.
     * @param ts the array of elements to be sorted.
     */
    public static <T extends Comparable<T>> void sort(T[] ts) {
        try (InsertionSort<T> sort = new InsertionSort<>()) {
            sort.mutatingSort(ts);
        }
    }

    /**
     * Performs the insertion operation for sorting, moving elements into their correct position
     * within the specified range in the array.
     *
     * @param xs      the array being sorted
     * @param from    the starting index of the range to be sorted
     * @param i       the current index where the comparison and shifting take place
     * @param a       the current element being evaluated for insertion
     * @param b       the next element in the sequence
     * @param helper  the Helper instance used to perform operations such as swapping and retrieving elements
     * @return the next element in the sequence after the insertion has been completed
     */
    private static <X extends Comparable<X>> X doInsert(X[] xs, int from, int i, X a, X b, Helper<X> helper) {
        X aNext = b;
        int j = i;
        while (true) {
            boolean swapped = helper.swapConditional(xs, a, j - 1, j, b);
            if (!swapped) break;
            if (aNext == b) aNext = a;
            j--;
            if (j == from) break;
            a = helper.get(xs, j - 1);
        }
        return aNext;
    }
}