/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.sort.elementary;

import java.io.IOException;
import java.util.Comparator;

import com.phasmidsoftware.dsaipg.sort.Helper;
import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.getRunsConfig;
import com.phasmidsoftware.dsaipg.sort.Sort;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.Config_Benchmark;

/**
 * A class for performing insertion sort using a comparator, extending
 * functionality from SortWithHelper. This includes methods for initialization
 * and invocation of insertion sort, along with specific utilities like counting
 * inversions.
 *
 * @param <X> the type of elements to be sorted, which can be compared using a
 * provided comparator.
 */
public class InsertionSortComparator<X> extends SortWithHelper<X> {

    /**
     * Constructor for InsertionSortComparator, which initializes the comparator
     * with the provided helper.
     *
     * @param helper the Helper object to be used for managing the sorting
     * process.
     */
    public InsertionSortComparator(Helper<X> helper) {
        super(helper);
    }

    /**
     * Constructor for any subclasses to use.
     *
     * @param description the description.
     * @param comparator the comparator to use.
     * @param N the number of elements expected.
     * @param nRuns the number of runs to be expected (this is only significant
     * when instrumenting).
     * @param config the configuration.
     */
    protected InsertionSortComparator(String description, Comparator<X> comparator, int N, int nRuns, Config config) {
        super(description, comparator, N, nRuns, config);
    }

    /**
     * Constructor for InsertionSort
     *
     * @param N the number elements we expect to sort.
     * @param nRuns the number of runs to be expected (this is only significant
     * when instrumenting).
     * @param config the configuration.
     */
    public InsertionSortComparator(Comparator<X> comparator, int N, int nRuns, Config config) {
        this(DESCRIPTION, comparator, N, nRuns, config);
    }

    /**
     * Sort the sub-array xs:from:to using insertion sort.
     *
     * @param xs sort the array xs from "from" to "to".
     * @param from the index of the first element to sort
     * @param to the index of the first element not to sort
     */
    public void sort(X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        for (int i = from + 1; i < to; i++) {
            int j = i;
            while (j > from && helper.less(xs, j, j - 1)) {
                helper.swapStableConditional(xs, j);
                j--;
            }
        }
    }

    public static final String DESCRIPTION = "Insertion sort";

    /**
     * Sorts the given array in-place using the provided insertion sort
     * comparator.
     *
     * @param <T> the generic type parameter that extends Comparable.
     * @param ts the array of elements to be sorted, where elements must
     * implement {@code Comparable}. The method modifies this array directly to
     * produce the sorted order.
     * @throws RuntimeException if an IOException occurs during the sorting
     * process.
     */
    public static <T extends Comparable<T>> void sort(T[] ts) {
        try (InsertionSortComparator<T> sort = new InsertionSortComparator<>(DESCRIPTION, Comparable::compareTo, ts.length, 1, Config.load(InsertionSortComparator.class))) {
            sort.mutatingSort(ts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a case-insensitive string sorter using an insertion sort
     * comparator.
     *
     * @param n the expected number of elements to be sorted.
     * @param config the configuration object containing necessary settings.
     * @return a {@code SortWithHelper<String>} instance configured for
     * case-insensitive string sorting.
     */
    public static Sort<String> stringSorterCaseInsensitive(int n, Config config) {
        return new InsertionSortComparator<>(DESCRIPTION, String.CASE_INSENSITIVE_ORDER, n, getRunsConfig(config), config);
    }

    /**
     * This method is designed to count inversions in quadratic time, using
     * insertion sort.
     *
     * @param ts an array of comparable T elements.
     * @param <T> the underlying type of the elements.
     * @return the number of inversions in ts, which remains unchanged.
     */
    public static <T> long countInversions(T[] ts, Comparator<T> comparator) {
        final Config config = Config_Benchmark.setupConfigFixes();
        try (InsertionSortComparator<T> sorter = new InsertionSortComparator<>(comparator, ts.length, getRunsConfig(config), config)) {
            Helper<T> helper = sorter.getHelper();
            sorter.sort(ts, true);
            return helper.getFixes();
        }
    }

}
