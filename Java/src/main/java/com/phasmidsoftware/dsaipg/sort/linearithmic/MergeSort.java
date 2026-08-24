package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.Arrays;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.*;

/**
 * A generic implementation of the MergeSort algorithm for sorting elements of type X,
 * where X extends Comparable<X>. This class provides optimized sorting techniques such as
 * insurance and no-copy optimizations, offering scalable sorting solutions. It makes use of
 * a helper class for monitoring and performing additional utilities during the sorting process.
 *
 * @param <X> The type of elements to be sorted, which must implement the Comparable interface.
 */
public class MergeSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    public static final String DESCRIPTION = "MergeSort";

    /**
     * Constructor for MergeSort
     * <p>
     * NOTE this is used only by unit tests, using its own instrumented helper.
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public MergeSort(Helper<X> helper) {
        super(helper);
        insertionSort = setupInsertionSort(helper);
    }

    /**
     * Constructor for MergeSort
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the expected number of runs.
     * @param config the configuration.
     */
    public MergeSort(int N, int nRuns, Config config) {
        super(DESCRIPTION + getConfigString(config), N, nRuns, config);
        insertionSort = setupInsertionSort(getHelper());
    }

    /**
     * Sorts the given array in-place or by creating a copy, depending on the parameter makeCopy.
     * This method initializes a sorting helper, allocates additional memory as necessary,
     * and performs the sort.
     *
     * @param xs       the array to be sorted
     * @param makeCopy if true, the array will be copied before sorting. Otherwise, sorting is done in-place.
     * @return the sorted array; either the modified original array (if makeCopy is false) or a new sorted array (if makeCopy is true)
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
     * Sorts the specified portion of the array using the MergeSort algorithm.
     *
     * @param a    the array to be sorted
     * @param from the starting index of the range to sort, inclusive
     * @param to   the ending index of the range to sort, exclusive
     */
    public void sort(X[] a, int from, int to) {
        Config config = helper.getConfig();
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);
        @SuppressWarnings("unchecked") X[] aux = noCopy ? helper.copyArray(a) : (X[]) new Comparable[a.length];
        sort(a, aux, from, to, 0);
    }

    /**
     * Sets the memory for the array if it hasn't been set previously.
     * If `arrayMemory` has not been initialized (i.e., equals -1), it sets its value to `n`.
     * Additionally, allocates and updates additional memory using the `additionalMemory` method.
     *
     * @param n the amount of memory to be set for the array.
     */
    public void setArrayMemory(int n) {
        if (arrayMemory == -1) {
            arrayMemory = n;
            additionalMemory(n);
        }
    }

    /**
     * Updates the value of additionalMemory by adding the provided amount and adjusts maxMemory if necessary.
     *
     * @param n the amount of memory to be added to additionalMemory.
     */
    public void additionalMemory(int n) {
        additionalMemory += n;
        if (maxMemory < additionalMemory) maxMemory = additionalMemory;
    }

    /**
     * Computes the memory factor, which represents the ratio of the maximum memory available
     * to the array memory size. This calculation helps in determining the efficiency or feasibility
     * of operations concerning memory usage.
     *
     * @return the memory factor as a Double.
     * Throws a SortException if the array memory has not been set (i.e., arrayMemory == -1).
     */
    public Double getMemoryFactor() {
        if (arrayMemory == -1)
            throw new SortException("Array memory has not been set");
        return 1.0 * maxMemory / arrayMemory;
    }

    /**
     * Sets up an instance of InsertionSort using a cloned helper with a specific description.
     *
     * @param helper an instance of Helper to be cloned and used by the InsertionSort instance.
     * @return an instance of InsertionSort configured with the cloned helper.
     */
    private InsertionSort<X> setupInsertionSort(final Helper<X> helper) {
        return new InsertionSort<>(helper.clone("MergeSort: insertion sort", true));
    }

    /**
     * Sorts the given range of the array using merge sort with optional optimizations for insurance and no-copy.
     * When called with no-copy true, both primary and secondary partitions
     * (but not necessary the entire arrays) will be identical.
     * When called with no-copy false, the secondary array will be undefined.
     *
     * @param primary   the primary array.
     *                  If the partition is small, insertion sort will be applied to this array.
     * @param secondary the auxiliary array used for intermediate storage during sorting.
     * @param from      the starting index (inclusive) of the range to be sorted.
     * @param to        the ending index (exclusive) of the range to be sorted.
     * @param depth
     */
    private void sort(X[] primary, X[] secondary, int from, int to, int depth) {
        getHelper().registerDepth(depth);
        Config config = helper.getConfig();
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY); // XXX I recommend that you test noCopy before testing insurance.
        boolean insurance = config.getBoolean(MERGESORT, INSURANCE);
        assert !noCopy || Arrays.compare(primary, from, to, secondary, from, to) == 0 : "MergeSort::sort: partitions are not the same";
        if (to <= from + helper.cutoff()) { // XXX check that a cutoff value of 1 effectively stops the cutoff mechanism.
            insertionSort.sort(primary, from, to);
            return;
        }

        // TO BE IMPLEMENTED  : implement merge sort with no-copy and insurance optimizations (use helper.less and helper.copyBlock)
                throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
    }

    /**
     * Merges two sorted partitions (subarrays) into a combined sorted partition (subarray).
     * The first partition is defined as [from, mid) and the second partition as [mid, to).
     * Elements from these partitions are copied into the result array in sorted order.
     * CONSIDER combine with MergeSortBasic, perhaps.
     *
     * @param sorted The source array containing the two sorted partitions to be merged.
     * @param result The destination array where the merged results will be stored.
     * @param from   The starting index (inclusive) of the first partition.
     * @param mid    The ending index (exclusive) of the first partition and the starting index of the second partition.
     * @param to     The ending index (exclusive) of the second partition.
     */
    private void merge(X[] sorted, X[] result, int from, int mid, int to) {
        int i = from;
        int j = mid;
        helper.incrementLookups(to - from); // NOTE this is optimistic
//        System.out.println(((BaseHelper<?>) helper).showInterimStats("merge start: "+from+" "+mid+" "+to));
        X v = helper.get(sorted, i);
        X w = helper.get(sorted, j);
        for (int k = from; k < to; k++) {
            if (i >= mid) {
                helper.copy(w, result, k);
                if (++j < to)
                    w = helper.get(sorted, j);
            } else if (j >= to) {
                helper.copy(v, result, k);
                if (++i < mid)
                    v = helper.get(sorted, i);
            } else if (helper.inverted(v, w)) {
                helper.incrementFixes(mid - i);
                helper.copy(w, result, k);
                if (++j < to)
                    w = helper.get(sorted, j);
            } else {
                helper.copy(v, result, k);
                if (++i < mid)
                    v = helper.get(sorted, i);
            }
        }
//        System.out.println(((BaseHelper<?>) helper).showInterimStats("merge end: "+from+" "+mid+" "+to));
    }

    public static final String MERGESORT = "mergesort";
    public static final String NOCOPY = "nocopy";
    public static final String INSURANCE = "insurance";

    /**
     * Builds a configuration string based on the provided configuration settings.
     * The configuration string describes certain properties such as whether
     * insurance comparison, no-copy, or specific cutoff values are enabled.
     *
     * @param config the configuration object used to determine the settings for the string.
     * @return a string representing the configuration settings.
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
    private int arrayMemory = -1;
    private int additionalMemory;
    private int maxMemory;
}

