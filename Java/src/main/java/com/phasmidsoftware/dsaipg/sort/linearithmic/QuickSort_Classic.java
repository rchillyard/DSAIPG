package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.helper.BaseHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.ArrayList;
import java.util.List;

import static com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparatorHelper.getRunsConfig;

/**
 * Implementation of the QuickSort algorithm with a basic partitioning strategy.
 * This class extends a generic QuickSort class, utilizing a simple partitioner
 * to divide the array into smaller partitions for sorting.
 *
 * @param <X> the type of elements to be sorted, which must implement Comparable.
 */
public class QuickSort_Classic<X extends Comparable<X>> extends QuickSort<X> {

    public static final String DESCRIPTION = "QuickSort classic";

    /**
     * Constructor for QuickSort_Classic.
     *
     * @param description a description of the QuickSort instance.
     * @param N           the number of elements expected to be sorted.
     * @param nRuns       the number of times the sorting algorithm should run.
     * @param config      the configuration settings for the sorting algorithm.
     */
    public QuickSort_Classic(String description, int N, final int nRuns, Config config) {
        super(description, N, nRuns, config);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_Classic
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public QuickSort_Classic(Helper<X> helper) {
        super(helper);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_Classic
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs.
     * @param config the configuration.
     */
    public QuickSort_Classic(int N, final int nRuns, Config config) {
        this(DESCRIPTION, N, nRuns, config);
    }

    /**
     * Constructor for QuickSort_Classic
     *
     * @param config the configuration.
     */
    public QuickSort_Classic(Config config) {
        this(0, getRunsConfig(config), config);
    }

    /**
     * Constructor for QuickSort_Classic class.
     *
     * @param n      the number of elements to be sorted.
     * @param config the configuration settings for the sorting algorithm.
     */
    public QuickSort_Classic(int n, Config config) {
        this(n, getRunsConfig(config), config);
    }

    /**
     * Creates and returns a basic partitioner instance for the QuickSort algorithm.
     * The created partitioner utilizes a helper object to assist with the partitioning process.
     *
     * @return a {@code Partitioner<X>} instance, specifically a {@code Partitioner_Basic<X>},
     * which divides data into partitions for efficient sorting.
     */
    public Partitioner<X> createPartitioner() {
        return new Partitioner_Basic<>(getHelper());
    }

    /**
     * A basic implementation of the Partitioner interface that partitions an array into smaller subarrays for sorting.
     * This implementation works with types that are comparable, enabling partitioning based on comparison.
     *
     * @param <Y> the type of elements that are being partitioned, which must extend Comparable.
     */
    public static class Partitioner_Basic<Y extends Comparable<Y>> implements Partitioner<Y> {

        /**
         * Method to partition the given partition into smaller partitions.
         *
         * @param partition the partition to divide up.
         * @return a list of partitions, whose length depends on the sorting method being used.
         */
        public List<Partition<Y>> partition(Partition<Y> partition) {
            final Y[] ys = partition.xs;
            final int from = partition.from;
            final int hi = partition.to - 1;
            Y v = helper.get(ys, from);
            int i = from;
            int j = partition.to;
            // NOTE: we are trying to avoid checking on instrumented for every time in the inner loop for performance reasons (probably a silly idea).
            // NOTE: if we were using Scala, it would be easy to set up a comparer function and a swapper function. With java, it's possible but much messier.
            if (helper.instrumented()) {
//                System.out.println(((BaseHelper<?>) helper).showInterimStats("start: "+partition));
                while (true) {
                    // NOTE this rather strange mechanism is just to ensure that we keep track of all array accesses.
                    XValue x = new XValue();
                    while (i < hi && x.update(ys, ++i) && helper.notInverted(x.x, v)) {
                    }
                    XValue y = new XValue();
                    while (j > from && y.update(ys, --j) && helper.notInverted(v, y.x)) {
                    }
                    if (i >= j) break;
                    helper.swapVW(x.x, y.x, ys, i, j);
                }
                if (from != j) helper.swapV(v, ys, from, j);
//                System.out.println(((BaseHelper<?>) helper).showInterimStats("end: "+partition));
            } else {
                while (true) {
                    // NOTE pureComparison, NOT compareTo. This branch exists only to
                    // skip the counting, not to change the ordering: compareTo ignores
                    // the Helper's comparator, so an uninstrumented sort with a custom
                    // comparator came out in the natural ordering instead.
                    while (i < hi && helper.pureComparison(ys[++i], v) < 0) {
                    }
                    while (j > from && helper.pureComparison(ys[--j], v) > 0) {
                    }
                    if (i >= j) break;
                    swap(ys, i, j);
                }
                swap(ys, from, j);
            }

            List<Partition<Y>> partitions = new ArrayList<>();
            partitions.add(new Partition<>(ys, from, j));
            partitions.add(new Partition<>(ys, j + 1, partition.to));
            return partitions;
        }

        /**
         * Constructor for creating a basic partitioner with a specified helper.
         *
         * @param helper a Helper instance of type Y used to perform various partitioning-related tasks.
         */
        public Partitioner_Basic(Helper<Y> helper) {
            this.helper = helper;
        }

        /**
         * Auxiliary class to help with the instrumenting case.
         * In particular, we minimize the number of hits.
         */
        class XValue {
            /**
             * Updates the instance variable with a specific element from the given array and increments a counter for hits.
             *
             * @param xs the array of type Y elements from which the value to update is selected
             * @param i the index of the element in the array to update the instance variable with
             * @return true always, indicating a successful update operation
             */
            public boolean update(Y[] xs, int i) {
                helper.incrementHits(1);
                helper.incrementLookups(1);
                this.x = xs[i];
                return true;
            }

            Y x;

            /**
             * Constructs an XValue instance and assigns the given value to the instance variable x.
             *
             * @param x an object of type Y to initialize the instance variable x
             */
            public XValue(Y x) {
                this.x = x;
            }

            /**
             * Default constructor for the XValue class.
             * Initializes the instance variable to null by invoking the parameterized constructor with a null argument.
             */
            public XValue() {
                this(null);
            }
        }

        private void swap(Y[] ys, int i, int j) {
            Y temp = ys[i];
            ys[i] = ys[j];
            ys[j] = temp;
        }

        private final Helper<Y> helper;
    }
}

