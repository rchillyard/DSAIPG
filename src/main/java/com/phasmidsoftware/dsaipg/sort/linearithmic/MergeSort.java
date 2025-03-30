package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.SortException;
import com.phasmidsoftware.dsaipg.sort.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.util.Config;

import java.util.Arrays;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.*;

/**
 * Class MergeSort implements the Merge Sort algorithm with optional optimizations.
 *
 * @param <X> the underlying comparable type.
 */
public class MergeSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    public static final String DESCRIPTION = "MergeSort";

    /**
     * Constructor for MergeSort using an explicit Helper instance.
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public MergeSort(Helper<X> helper) {
        super(helper);
        insertionSort = setupInsertionSort(helper);
    }

    /**
     * Constructor for MergeSort with configuration parameters.
     *
     * @param N      the number of elements expected to be sorted.
     * @param nRuns  the expected number of runs.
     * @param config the configuration settings.
     */
    public MergeSort(int N, int nRuns, Config config) {
        super(DESCRIPTION + getConfigString(config), N, nRuns, config);
        insertionSort = setupInsertionSort(getHelper());
    }

    /**
     * Sets up the InsertionSort instance for small subarrays.
     *
     * @param helper Helper instance to clone.
     * @return an InsertionSort instance.
     */
    private InsertionSort<X> setupInsertionSort(final Helper<X> helper) {
        return new InsertionSort<>(helper.clone("MergeSort: insertion sort"));
    }

    /**
     * Public method to sort an array using MergeSort.
     *
     * @param xs       the input array.
     * @param makeCopy if true, creates a copy before sorting.
     * @return the sorted array.
     */
    public X[] sort(X[] xs, boolean makeCopy) {
        getHelper().init(xs.length);
        additionalMemory(xs.length);
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        sort(result, 0, result.length);
        additionalMemory(-xs.length);
        return result;
    }

    /**
     * Sorts a subarray using MergeSort.
     *
     * @param a    the array to be sorted.
     * @param from start index.
     * @param to   end index (exclusive).
     */
    public void sort(X[] a, int from, int to) {
        Config config = getHelper().getConfig();
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);
        @SuppressWarnings("unchecked") X[] aux = noCopy ? getHelper().copyArray(a) : (X[]) new Comparable[a.length];
        sort(a, aux, from, to);
    }

    /**
     * Recursive MergeSort implementation with optional optimizations.
     */
    private void sort(X[] a, X[] aux, int from, int to) {
        if (to - from <= getHelper().cutoff()) {
            insertionSort.sort(a, from, to);
            return;
        }

        int mid = from + (to - from) / 2;
        sort(a, aux, from, mid);
        sort(a, aux, mid, to);
        merge(a, aux, from, mid, to);
    }

    /**
     * Merges two sorted subarrays.
     *
     * @param sorted the original array.
     * @param result the auxiliary array.
     * @param from   starting index.
     * @param mid    midpoint index.
     * @param to     ending index (exclusive).
     */
    private void merge(X[] sorted, X[] result, int from, int mid, int to) {
        System.arraycopy(sorted, from, result, from, to - from);
        
        int i = from, j = mid;
        for (int k = from; k < to; k++) {
            if (i >= mid) {
                sorted[k] = result[j++];
            } else if (j >= to) {
                sorted[k] = result[i++];
            } else if (getHelper().less(result[j], result[i])) {
                sorted[k] = result[j++];
            } else {
                sorted[k] = result[i++];
            }
        }
    }

    // Configuration keys
    public static final String MERGESORT = "mergesort";
    public static final String NOCOPY = "nocopy";
    public static final String INSURANCE = "insurance";

    /**
     * Generates a string representation of the configuration settings.
     */
    private static String getConfigString(Config config) {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.getBoolean(MERGESORT, INSURANCE)) stringBuilder.append(" with insurance comparison");
        if (config.getBoolean(MERGESORT, NOCOPY)) stringBuilder.append(" with no copy");
        int cutoff = config.getInt(HELPER, CUTOFF, CUTOFF_DEFAULT);
        if (cutoff != CUTOFF_DEFAULT) {
            if (cutoff == 1) stringBuilder.append(" with no cutoff");
            else stringBuilder.append(" with cutoff ").append(cutoff);
        }
        return stringBuilder.toString();
    }

    private final InsertionSort<X> insertionSort;

    // Memory tracking fields
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

    /**
     * Calculates the memory factor used during sorting.
     */
    public Double getMemoryFactor() {
        if (arrayMemory == -1)
            throw new SortException("Array memory has not been set");
        return 1.0 * maxMemory / arrayMemory;
    }
}