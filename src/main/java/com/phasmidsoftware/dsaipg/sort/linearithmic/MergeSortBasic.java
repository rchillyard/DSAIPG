/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.*;
import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.Config_Benchmark;

import java.util.Arrays;

import static com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort.INSURANCE;
import static com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort.MERGESORT;

public class MergeSortBasic<X extends Comparable<X>> extends SortWithComparableHelper<X> implements HasAdditionalMemory {

    public static final String DESCRIPTION = "MergeSort";

    /**
     * Constructor for MergeSort
     * <p>
     * NOTE this is used only by unit tests, using its own instrumented helper.
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public MergeSortBasic(Helper<X> helper) {
        super(helper);
        // TODO use impersonat (like in MergeSort)
        insertionSort = new InsertionSort<>(helper);
    }

    /**
     * Constructor for MergeSort
     *
     * @param N      the number elements we expect to sort.
     * @param config the configuration.
     */
    public MergeSortBasic(int N, Config config) {
        super(DESCRIPTION + ":" + getConfigString(config), N, 1, config);
        // TODO use impersonat (like in MergeSort)
        insertionSort = new InsertionSort<>(getHelper());
    }

    private static String getConfigString(Config config) {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.getBoolean(MERGESORT, INSURANCE)) stringBuilder.append(" with insurance comparison");
        return stringBuilder.toString();
    }

    public X[] sort(X[] xs, boolean makeCopy) {
        getHelper().init(xs.length);
        additionalMemory(xs.length);
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        // CONSIDER don't copy but just allocate according to the xs/aux interchange optimization
        aux = Arrays.copyOf(xs, xs.length);
        sort(result, 0, result.length);
        additionalMemory(-xs.length);
        return result;
    }

    public void sort(X[] a, int from, int to) {
        Helper<X> helper = getHelper();
        if (to <= from + helper.cutoff()) {
            insertionSort.sort(a, from, to);
            return;
        }
        final int n = to - from;
        int mid = from + n / 2;
        sort(a, from, mid);
        sort(a, mid, to);
        helper.copyBlock(a, from, aux, from, n);
        merge(aux, a, from, mid, to);
    }

    /**
     * This method is designed to count inversions in linearithmic time, using merge sort.
     *
     * @param ys  an array of comparable Y elements.
     * @param <Y> the underlying type of the elements.
     * @return the number of inversions in ys, which remains unchanged.
     */
    public static <Y extends Comparable<Y>> long countInversions(Y[] ys) {
        final Config config = Config_Benchmark.setupConfigFixes();
        try (MergeSortBasic<Y> sorter = new MergeSortBasic<>(ys.length, config)) {
            sorter.init(ys.length);
            Y[] sorted = sorter.sort(ys, true); // CONSIDER passing false
            InstrumentedComparatorHelper<Y> helper = (InstrumentedComparatorHelper<Y>) sorter.getHelper();
            return helper.getFixes();
        }
    }

    private void merge(X[] aux, X[] a, int lo, int mid, int hi) {
        final Helper<X> helper = getHelper();
        int i = lo;
        int j = mid;
        int k = lo;
        for (; k < hi; k++)
            if (i >= mid) helper.copy(aux, j++, a, k);
            else if (j >= hi) helper.copy(aux, i++, a, k);
            else if (helper.less(aux[j], aux[i])) {
                helper.incrementFixes(mid - i);
                helper.copy(aux, j++, a, k);
            } else helper.copy(aux, i++, a, k);
    }


    private X[] aux = null;
    private final InsertionSort<X> insertionSort;


    private int arrayMemory = -1;
    private int additionalMemory;
    private int maxMemory;

    public void setArrayMemory(int n) {
        if (arrayMemory == -1) {
            arrayMemory = n;
            additionalMemory(n);
        }
    }

    public void additionalMemory(int n) {
        additionalMemory += n;
        if (maxMemory < additionalMemory) maxMemory = additionalMemory;
    }

    public Double getMemoryFactor() {
        if (arrayMemory == -1)
            throw new SortException("Array memory has not been set");
        return 1.0 * maxMemory / arrayMemory;
    }
}
