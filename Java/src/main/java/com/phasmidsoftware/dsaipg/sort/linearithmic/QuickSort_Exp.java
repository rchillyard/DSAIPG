package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.ArrayList;
import java.util.List;

import static com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparatorHelper.DEFAULT_RUNS;

/**
 * A specialized implementation of the QuickSort algorithm with
 * experimental partitioning. This class extends the base QuickSort class
 * and introduces an experimental partitioner for dividing the array into
 * partitions.
 *
 * @param <X> The type of elements to be sorted, which must implement Comparable.
 */
public class QuickSort_Exp<X extends Comparable<X>> extends QuickSort<X> {

    public static final String DESCRIPTION = "QuickSort basic";

    /**
     * Creates an instance of a Partitioner for use with this sorting implementation.
     *
     * @return a Partitioner object of type Partitioner_Exp, configured with the associated Helper.
     */
    public Partitioner<X> createPartitioner() {
        return new Partitioner_Exp(getHelper());
    }

    /**
     * Constructor for QuickSort_Exp.
     *
     * @param description a textual description of the execution.
     * @param N           the number of elements expected to be sorted.
     * @param nRuns       the number of times the sort execution is to be run.
     * @param config      the configuration settings for the sort algorithm.
     */
    public QuickSort_Exp(String description, int N, final int nRuns, Config config) {
        super(description, N, nRuns, config);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_Classic
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public QuickSort_Exp(Helper<X> helper) {
        super(helper);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_Classic
     *
     * @param N      the number elements we expect to sort.
     * @param config the configuration.
     */
    public QuickSort_Exp(int N, Config config) {
        this(DESCRIPTION, N, DEFAULT_RUNS, config);
    }

    /**
     * Constructor for QuickSort_Classic
     *
     * @param config the configuration.
     */
    public QuickSort_Exp(Config config) {
        this(0, config);
    }

    /**
     * The Partitioner_Exp class is an implementation of the Partitioner interface used for dividing an array
     * or partition into two sub-partitions. This class follows the principles of Hoare's partitioning scheme,
     * commonly used in quicksort algorithms.
     */
    public class Partitioner_Exp implements Partitioner<X> {

        /**
         * Method to partition the given partition into smaller partitions.
         *
         * @param partition the partition to divide up.
         * @return a list of partitions, whose length depends on the sorting method being used.
         */
        public List<Partition<X>> partition(Partition<X> partition) {
            final X[] xs = partition.xs;
            final int from = partition.from;
            final int to = partition.to;
            final int hi = to - 1;
            int mid = from + (to - from) / 2;
            helper.swap(xs, from, mid);
            X v = xs[from];
            int i = from;
            int j = to;
            // NOTE: we are trying to avoid checking on instrumented for every time in the inner loop for performance reasons (probably a silly idea).
            // NOTE: if we were using Scala, it would be easy to set up a comparer function and a swapper function. With java, it's possible but much messier.
            if (helper.instrumented()) {
                // NOTE helper.get, not a bare xs[...]. The scans read one element
                // per step and every one of them must be counted. Reading the
                // array directly and charging a single hit for the whole
                // partition made this sort report 72% of the accesses it made,
                // which is not a statistic anyone can use.
                helper.incrementHits(1); // for the pivot, read above
                while (true) {
                    while (i < hi && helper.notInverted(helper.get(xs, ++i), v)) {
                    }
                    while (j > from && helper.notInverted(v, helper.get(xs, --j))) {
                    }
                    if (i >= j) break;
                    helper.swap(xs, i, j);
                }
                if (from != j) helper.swap(xs, from, j);
            } else {
                while (true) {
                    // NOTE pureComparison, NOT compareTo. This branch exists only to
                    // skip the counting, not to change the ordering: compareTo ignores
                    // the Helper's comparator, so an uninstrumented sort with a custom
                    // comparator came out in the natural ordering instead.
                    while (i < hi && helper.pureComparison(xs[++i], v) < 0) {
                    }
                    while (j > from && helper.pureComparison(xs[--j], v) > 0) {
                    }
                    if (i >= j) break;
                    swap(xs, i, j);
                }
                swap(xs, from, j);
            }

            List<Partition<X>> partitions = new ArrayList<>();
            partitions.add(new Partition<>(xs, from, j));
            partitions.add(new Partition<>(xs, j + 1, to));
            return partitions;
        }

        /**
         * Constructor for the Partitioner_Exp class.
         * Initializes the partitioner with the provided Helper instance.
         *
         * @param helper an instance of the Helper class which provides utility methods such as comparison
         *               and instrumentation for sorting algorithms. The helper supports operations required
         *               for partitioning, such as swapping elements and evaluating comparisons.
         */
        public Partitioner_Exp(Helper<X> helper) {
            this.helper = helper;
        }

        /**
         * Swaps two elements in the specified array.
         * CONSIDER using help.swap
         *
         * @param ys the array in which elements are to be swapped
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
}

