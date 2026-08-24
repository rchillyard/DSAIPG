package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.sort.generic.HasAdditionalMemory;
import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparatorHelper;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.config.Config_Benchmark;

import java.util.Arrays;

import static com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort.INSURANCE;
import static com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort.MERGESORT;

/**
 * MergeSortBasic is a simple implementation of the Merge Sort algorithm,
 * designed to work with elements that implement the Comparable interface.
 * It extends SortWithComparableHelper to utilize helper-based mechanisms for sorting.
 *
 * @param <X> The type of elements to be sorted, extending Comparable.
 */
public class MergeSortBasic<X extends Comparable<X>> extends SortWithComparableHelper<X> implements HasAdditionalMemory {

    public static final String DESCRIPTION = "MergeSort";

    /**
     * Sorts an array of elements in-place or creates a sorted copy of the array, based on the makeCopy parameter.
     * The sorting is achieved using a merge sort algorithm.
     *
     * @param xs       the array of elements to be sorted
     * @param makeCopy a boolean flag indicating whether to sort in-place (false) or return a new sorted copy of the array (true)
     * @return the sorted array; it is the same array if makeCopy is false, or a new sorted copy if makeCopy is true
     */
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

    /**
     * Sorts the portion of the array {@code a} from index {@code from} to index {@code to} using a merge sort algorithm.
     * The method recursively divides the array into subarrays, sorts them, and then merges them together in sorted order.
     *
     * @param a    the array to be sorted
     * @param from the starting index of the portion of the array to be sorted
     * @param to   the ending index (exclusive) of the portion of the array to be sorted
     */
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
     * Sets the memory to be used for the array during sorting. If the `arrayMemory` field is uninitialized (set to -1),
     * this method initializes it with the specified value and updates the additional memory to reflect the change.
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
     * Updates the additional memory used during the process by a specified amount.
     * If the updated additional memory exceeds the current maximum memory,
     * the maximum memory is also updated to reflect the new peak usage.
     *
     * @param n the amount of additional memory to be added. It represents the memory increment in units.
     */
    public void additionalMemory(int n) {
        additionalMemory += n;
        if (maxMemory < additionalMemory) maxMemory = additionalMemory;
    }

    /**
     * Calculates and returns the memory factor, which is the ratio of maximum memory
     * to the array memory. This factor reflects the available memory relative to the
     * memory allocated for the array.
     *
     * @return the memory factor as a Double.
     * @throws SortException if the array memory has not been set.
     */
    public Double getMemoryFactor() {
        if (arrayMemory == -1)
            throw new SortException("Array memory has not been set");
        return 1.0 * maxMemory / arrayMemory;
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
        // TODO use impersonate (like in MergeSort)
        insertionSort = new InsertionSort<>(getHelper());
    }

    /**
     * Constructs a string configuration based on the provided configuration settings.
     *
     * @param config an instance of Config containing settings for the method to interpret.
     * @return a string that represents the constructed configuration based on certain conditions in the input Config.
     */
    private static String getConfigString(Config config) {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.getBoolean(MERGESORT, INSURANCE)) stringBuilder.append(" with insurance comparison");
        return stringBuilder.toString();
    }

    /**
     * Merges two sorted subarrays into a single sorted array.
     *
     * @param aux an auxiliary array used for merging.
     * @param a the array to be merged.
     * @param from the starting index of the first subarray.
     * @param mid the ending index of the first subarray and the starting index of the second subarray.
     * @param to the ending index of the second subarray (exclusive)
     */
    private void merge(X[] aux, X[] a, int from, int mid, int to) {
        final Helper<X> helper = getHelper();
        int i = from;
        int j = mid;
        int k = from;
        for (; k < to; k++)
            if (i >= mid) helper.copy(aux, j++, a, k);
            else if (j >= to) helper.copy(aux, i++, a, k);
            else if (helper.inverted(aux, i, j)) {
                helper.incrementFixes(mid - i);
                helper.copy(aux, j++, a, k);
            } else helper.copy(aux, i++, a, k);
    }

    private X[] aux = null;
    private final InsertionSort<X> insertionSort;
    private int arrayMemory = -1;
    private int additionalMemory;
    private int maxMemory;
}

