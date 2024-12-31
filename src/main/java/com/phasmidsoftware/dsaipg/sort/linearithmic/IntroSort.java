/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.Config;

import java.util.Arrays;

/**
 * A class that implements the Introsort algorithm, a hybrid sorting algorithm
 * that begins with quicksort and switches to heapsort when the recursion depth
 * exceeds a predetermined threshold or insertion sort when the sub-array size
 * becomes small.
 *
 * @param <X> the type of elements to be sorted, which must extend Comparable.
 */
public class IntroSort<X extends Comparable<X>> extends QuickSort_DualPivot<X> {

    /**
     * Constructor for QuickSort_3way
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public IntroSort(Helper<X> helper) {
        super(helper);
    }

    /**
     * Constructor for QuickSort_3way
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs to perform.
     * @param config the configuration.
     */
    public IntroSort(int N, int nRuns, Config config) {
        super(DESCRIPTION, N, nRuns, config);
    }

    /**
     * Constructor for IntroSort using a configuration instance.
     *
     * @param config the configuration object that provides settings for the sort.
     */
    public IntroSort(Config config) {
        this(new NonInstrumentingComparableHelper<>(DESCRIPTION, config));
    }

    /**
     * Sorts the given array using the IntroSort algorithm.
     * This method performs an in-place sort if makeCopy is false.
     * Otherwise, a copy of the array is made before sorting.
     *
     * @param xs       the array of elements to be sorted
     * @param makeCopy true if a copy of the array should be created before sorting; false if the original array should be sorted in-place
     * @return the sorted array, which is either the same as the input array (if makeCopy is false) or a new sorted copy of the array (if makeCopy is true)
     */
    public X[] sort(X[] xs, boolean makeCopy) {
        getHelper().init(xs.length);
        depthThreshold = 2 * floor_lg(xs.length);
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        int from = 0, to = result.length;
        sort(result, from, to, 0);
        return result;
    }

    /**
     * @param xs   an array of Xs.
     * @param from the index of the first element to sort.
     * @param to   the index of the first element not to sort.
     */
    public void sort(X[] xs, int from, int to) {
        sort(xs, from, to, 2 * floor_lg(to - from));
    }

    /**
     * Protected method to determine to terminate the recursion of this quick sort.
     * NOTE that in this implementation, the depth is ignored.
     *
     * @param xs    the complete array from which this sub-array derives.
     * @param from  the index of the first element to sort.
     * @param to    the index of the first element not to sort.
     * @param depth the current depth of the recursion.
     * @return true if there is no further work to be done.
     */
    protected boolean terminator(X[] xs, int from, int to, int depth) {
        if (to - from <= sizeThreshold) {
            if (to > from + 1)
                getInsertionSort().sort(xs, from, to);
            return true;
        }

        if (depth >= depthThreshold) {
            heapSort(xs, from, to);
            return true;
        }

        return false;
    }

    public static final String DESCRIPTION = "Intro sort";

    /*
     * Heapsort algorithm
     */
    private void heapSort(X[] a, int from, int to) {
        Helper<X> helper = getHelper();
        int n = to - from;
        for (int i = n / 2; i >= 1; i = i - 1) {
            downHeap(a, i, n, from, helper);
        }
        for (int i = n; i > 1; i = i - 1) {
            helper.swap(a, from, from + i - 1);
            downHeap(a, 1, i - 1, from, helper);
        }
    }

    private void downHeap(X[] a, int i, int n, int lo, Helper<X> helper) {
        X d = a[lo + i - 1];
        int child;
        while (i <= n / 2) {
            child = 2 * i;
            if (helper.instrumented()) {
                if (child < n && helper.compare(a, lo + child - 1, lo + child) < 0) child++;
                if (helper.compare(d, a[lo + child - 1]) >= 0) break;
            } else {
                if (child < n && a[lo + child - 1].compareTo(a[lo + child]) < 0) child++;
                if (d.compareTo(a[lo + child - 1]) >= 0) break;
            }
            helper.incrementFixes(1);
            a[lo + i - 1] = a[lo + child - 1];
            i = child;
        }
        a[lo + i - 1] = d;
    }

    private static int floor_lg(int a) {
        return (int) (Math.floor(Math.log(a) / Math.log(2)));
    }

    private int depthThreshold = Integer.MAX_VALUE;

    private static final int sizeThreshold = 16;
}