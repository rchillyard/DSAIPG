/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.classic.ClassicHelper;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.io.IOException;
import java.util.Comparator;
import java.util.Random;

/**
 * Sorter which delegates to Timsort via Arrays.sort.
 * NOTE that there is no facility for instrumentation with this class.
 *
 * @param <X> The underlying type that extends Comparable<X>
 */
public class TimSortWrapper<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    public static final String DESCRIPTION = "Timsort";

    /**
     * Sorts a specified portion of an array in ascending order.
     * This method rearranges the elements in the specified index range of the given array
     * into ascending order according to their natural ordering.
     * Delegates to Arrays.sort
     *
     * @param xs   the array to be sorted
     * @param from the index of the first element to be included in the sort (inclusive)
     * @param to   the index of the first element not to be included in the sort (exclusive)
     */
    public void sort(X[] xs, int from, int to) {
        Comparator<X> comparator = helper.getComparator();
        TimSort.sort(xs, from, to, helper);
    }

    /**
     * Constructor for TimSortWrapper
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public TimSortWrapper(Helper<X> helper) {
        super(helper);
    }

    /**
     * Constructor for TimSortWrapper
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs to be expected (this is only significant when instrumenting).
     * @param config the configuration.
     */
    public TimSortWrapper(int N, int nRuns, Config config) {
        super(DESCRIPTION, N, nRuns, config);
    }

    /**
     * Default constructor for TimSortWrapper.
     * <p>
     * Constructs a new TimSortWrapper instance using a NonInstrumentingComparableHelper with a description
     * and configuration specific to the TimSortWrapper class. This constructor also reads the necessary
     * configuration data from the associated Config class. Note that the configuration loaded is
     * specific to TimSortWrapper and no instrumentation capabilities are enabled for the helper.
     *
     * @throws IOException if there is an issue loading the configuration for the TimSortWrapper class.
     */
    public TimSortWrapper() throws IOException {
        this(new NonInstrumentingComparableHelper<>(DESCRIPTION, Config.load(TimSortWrapper.class)));
    }

    /**
     * Creates a case-insensitive string sorting instance.
     * This method returns an implementation of {@code SortWithHelper<String>} that sorts strings
     * using {@link String#CASE_INSENSITIVE_ORDER}.
     *
     * @param N      the expected number of elements to be sorted.
     * @param config the configuration object to be used for sorting and helper setup.
     * @return an instance of {@code SortWithHelper<String>} configured for case-insensitive sorting.
     */
    public static SortWithHelper<String> CaseInsensitiveSort(int N, Config config) {
        return new SortWithHelper<>(new ClassicHelper<>(DESCRIPTION, String.CASE_INSENSITIVE_ORDER, N, new Random(), config)) {
            public void sort(String[] xs, int from, int to) {
                TimSort.sort(xs, from, to, helper);
            }
        };
    }

    /**
     * A class for sorting arrays using a comparator provided by an associated Helper instance.
     * This class extends the abstract class SortWithHelper, enabling sorting functionality
     * through interaction with a custom, external Helper to define sorting criteria.
     *
     * @param <T> The type of the elements to be sorted.
     */
    static class ComparatorSort<T> extends SortWithHelper<T> {
        /**
         * Constructs a ComparatorSort instance with the given Helper.
         *
         * @param helper the Helper instance that provides the comparator used for sorting.
         */
        public ComparatorSort(Helper<T> helper) {
            super(helper);
        }

        /**
         * Sorts the specified range of the given array using a comparator obtained from the Helper instance.
         *
         * @param xs   the array to be sorted; elements within the specified range are sorted in-place
         * @param from the index of the first element (inclusive) to sort
         * @param to   the index of the last element (exclusive) to sort
         *             (i.e., sorting will stop before the element at this index)
         */
        public void sort(T[] xs, int from, int to) {
            TimSort.sort(xs, from, to, helper);
        }
    }
}

