package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.ArrayList;
import java.util.List;

import static com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparatorHelper.getRunsConfig;
import static com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort.getConfigString;

/**
 * Class QuickSort_DualPivot which extends QuickSort.
 *
 * @param <X> the underlying comparable type.
 */
public class QuickSort_DualPivot<X extends Comparable<X>> extends QuickSort<X> {

    public static final String DESCRIPTION = "QuickSort dual pivot";

    /**
     * Creates and returns a Dual Pivot partitioner for use in sorting algorithms.
     * The returned partitioner leverages the dual-pivot partitioning strategy for dividing
     * the input data into multiple smaller partitions, which is commonly used in optimized quicksort implementations.
     *
     * @return a Partitioner instance using a dual-pivot strategy initialized with the associated helper.
     */
    public Partitioner<X> createPartitioner() {
        return new Partitioner_DualPivot(getHelper());
    }

    /**
     * Constructor for QuickSort_DualPivot.
     *
     * @param description a short description of the sorting instance.
     * @param N           the number of elements expected to be sorted.
     * @param nRuns       the number of times the sort operation will run for benchmarking or testing.
     * @param config      the configuration settings for the sorting process.
     */
    public QuickSort_DualPivot(String description, int N, int nRuns, Config config) {
        super(description, N, nRuns, config);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_3way
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public QuickSort_DualPivot(Helper<X> helper) {
        super(helper);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_3way
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs.
     * @param config the configuration.
     */
    public QuickSort_DualPivot(int N, int nRuns, Config config) {
        this(DESCRIPTION, N, nRuns, config);
    }

    /**
     * Constructor for QuickSort_3way
     *
     * @param N      the number elements we expect to sort.
     * @param config the configuration.
     */
    public QuickSort_DualPivot(int N, Config config) {
        this(DESCRIPTION, N, getRunsConfig(config), config);
    }

    /**
     * This class implements a dual-pivot partitioning strategy for use in sorting algorithms, such as quicksort.
     * Dual-pivot partitioning is an optimization of the classic quicksort partitioning approach, utilizing two pivots
     * to divide the input data into three distinct regions for more efficient sorting.
     */
    public class Partitioner_DualPivot implements Partitioner<X> {

        /**
         * Constructor for Partitioner_DualPivot.
         * This constructor initializes the Partitioner_DualPivot instance with a provided helper.
         *
         * @param helper a Helper instance that provides utility methods and support for the partitioning process.
         */
        public Partitioner_DualPivot(Helper<X> helper) {
            this.helper = helper;
        }

        /**
         * Method to partition the given partition into smaller partitions.
         *
         * @param partition the partition to divide up.
         * @return a list of partitions, whose length depends on the sorting method being used.
         */
        public List<Partition<X>> partition(Partition<X> partition) {
            int n = partition.to - partition.from;
            if (n < 3) throw new SortException("cannot use DualPivot partitioning when size is less than 3");
            final X[] xs = partition.xs;
            final int p1 = partition.from;
            final int p2 = partition.to - 1;
            helper.swapConditional(xs, p1, p2);
            int lt = p1 + 1;
            int gt = p2 - 1;
            int i = lt;
            X v1 = xs[p1];
            X v2 = xs[p2];
            // NOTE: we are trying to avoid checking on instrumented for every time in the inner loop for performance reasons (probably a silly idea).
            // NOTE: if we were using Scala, it would be easy to set up a comparer function and a swapper function. With java, it's possible but much messier.
            if (helper.instrumented()) {
                X xlt = helper.get(xs, lt);
                X xgt = helper.get(xs, gt);
                X x = xs[i]; // no hit since i = lt
                while (i <= gt) {
                    // Each time around the loop, we invoke: 2, 1, or 1 hits; 1, 2, or 2 lookups
                    if (helper.compare(x, v1) < 0) { // no hits, one lookup
                        helper.swap(xs, xlt, lt++, i++, x); // no hits or lookups
                        x = helper.get(xs, i); // one hit
                        xlt = helper.get(xs, lt); // one hit (CONSIDER is this correct?)
                        if (i == gt) xgt = x;
                    } else if (helper.compare(x, v2) > 0) { // no hits, one lookup (but it's already in cache)
                        helper.swap(xs, x, i, gt--, xgt); // no hits or lookups
                        if (i == lt) xlt = xgt;
                        x = xgt;
                        xgt = helper.get(xs, gt); // one hit
                    } else {
                        i++;
                        x = helper.get(xs, i); // one hit
                    }
                }
                helper.swap(xs, p1, --lt);
                helper.swap(xs, p2, ++gt);
            } else {
                while (i <= gt) {
                    X x = xs[i];
                    if (x.compareTo(v1) < 0) {
                        swap(xs, lt++, i++);
                    } else if (x.compareTo(v2) > 0) {
                        swap(xs, i, gt--);
                    } else i++;
                }
                swap(xs, p1, --lt);
                swap(xs, p2, ++gt);
            }

            List<Partition<X>> partitions = new ArrayList<>();
            partitions.add(new Partition<>(xs, p1, lt));
            partitions.add(new Partition<>(xs, lt + 1, gt));
            partitions.add(new Partition<>(xs, gt + 1, p2 + 1));
            return partitions;
        }

        /**
         * Swaps the elements at the specified positions in the given array.
         * CONSIDER invoke swap in Helper.
         *
         * @param ys the array in which the elements need to be swapped
         * @param i  the index of the first element to be swapped
         * @param j  the index of the second element to be swapped
         */
        private void swap(X[] ys, int i, int j) {
            X temp = ys[i];
            ys[i] = ys[j];
            ys[j] = temp;
        }

        private final Helper<X> helper;
    }

    public QuickSort_DualPivot(Config config) {
        // For example, default to N=0, nRuns=1 (or read them from config)
        super(DESCRIPTION + getConfigString(config), 0, 1, config);
        InsertionSort<X> insertionSort = setupInsertionSort(getHelper());
    }
}
