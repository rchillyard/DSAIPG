package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparableHelper;
import com.phasmidsoftware.dsaipg.sort.helper.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * QuickSort_3way is an implementation of QuickSort algorithm using the three-way partitioning strategy.
 * This specific variation is designed to efficiently handle duplicate keys by dividing the input
 * array into three segments during partitioning: less than, equal to, and greater than the pivot.
 *
 * @param <X> The type of elements that this QuickSort implementation will sort. It must extend Comparable.
 */
public class QuickSort_3way<X extends Comparable<X>> extends QuickSort<X> {

    public static final String DESCRIPTION = "QuickSort three way";

    /**
     * Creates a Partitioner instance that implements a three-way partitioning strategy.
     * Specifically, this method constructs a Partitioner_3Way object that uses the associated Helper instance.
     *
     * @return a Partitioner object implementing the three-way partitioning strategy for sorting.
     */
    public Partitioner<X> createPartitioner() {
        return new Partitioner_3Way(getHelper());
    }

    /**
     * Constructor for QuickSort_3way
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public QuickSort_3way(Helper<X> helper) {
        super(helper);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_3way that initializes a new instance of QuickSort_3way using a NonInstrumentingComparableHelper.
     *
     * @param config the configuration object used to create the helper instance.
     */
    public QuickSort_3way(Config config) {
        this(new NonInstrumentingComparableHelper<>(DESCRIPTION, config));
    }

    /**
     * Constructor for QuickSort_3way
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs to perform.
     * @param config the configuration.
     */
    public QuickSort_3way(int N, final int nRuns, Config config) {
        super(DESCRIPTION, N, nRuns, config);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_3way which always uses an instrumented helper with a specific seed.
     * <p>
     * NOTE used by unit tests.
     *  @param N    the number of elements to be sorted.
     *
     * @param seed the seed for the random number generator.
     */
    public QuickSort_3way(int N, long seed, Config config) {
        this(new InstrumentedComparableHelper<>(DESCRIPTION, N, seed, config));
    }

    /**
     * Partitioner_3Way is a concrete implementation of the Partitioner interface
     * that applies a three-way partitioning strategy, primarily used in Quicksort algorithms.
     * This three-way strategy divides the partition into elements less than, equal to, and greater than the pivot.
     */
    class Partitioner_3Way implements Partitioner<X> {
        /**
         * Method to partition the given partition into smaller partitions.
         *
         * @param partition the partition to divide up.
         * @return a list of partitions, whose length depends on the sorting method being used.
         */
        public List<Partition<X>> partition(Partition<X> partition) {
            X[] xs = partition.xs;
            int lt = partition.from;
            int gt = partition.to - 1;
            helper.swapConditional(xs, lt, gt); // one compare; one or zero swaps, four or six hits.
            X v = xs[lt]; // no hit because we already got this value in the previous statement.
            int i = lt + 1;
            // NOTE: we are trying to avoid checking on instrumented for every time in the inner loop for performance reasons (probably a silly idea).
            // NOTE: if we were using Scala, it would be easy to set up a comparer function and a swapper function. With java, it's possible but much messier.
            if (helper.instrumented()) {
                X xlt = v;
                X xgt = xs[gt]; // no hit because we already got this value in the previous statement.
                while (i <= gt) {
                    X xi = helper.get(xs, i); // one hit
                    if (i == lt) i++;
                    else {
                        int cmp = helper.compare(xi, v); // one compare
                        if (cmp < 0) {
                            helper.swapVW(xlt, xi, xs, lt++, i++); // one swap
                            xlt = helper.get(xs, lt); // one hit
                        } else if (cmp > 0) {
                            helper.swapVW(xi, xgt, xs, i, gt--); // one swap
                            xgt = helper.get(xs, gt); // one hit
                        } else i++; // no statistics affected
                    }
                }
            } else
                while (i <= gt) {
                    // NOTE pureComparison, NOT compareTo. This branch exists only to
                    // skip the counting, not to change the ordering: compareTo ignores
                    // the Helper's comparator altogether, so an uninstrumented sort with
                    // a custom comparator was ordered by the natural ordering instead.
                    // That is how MSDStringSort, which cuts over to this sort with a
                    // SuffixComparator, produced output like "Arab" before "abroad".
                    // pureComparison applies the comparator without counting anything, so
                    // this stays as fast as it was.
                    int cmp = helper.pureComparison(xs[i], v);
                    if (cmp < 0) swap(xs, lt++, i++);
                    else if (cmp > 0) swap(xs, i, gt--);
                    else i++;
                }

            List<Partition<X>> partitions = new ArrayList<>();
            partitions.add(new Partition<>(xs, partition.from, lt));
            partitions.add(new Partition<>(xs, gt + 1, partition.to));
            return partitions;
        }

        /**
         * Constructor for the Partitioner_3Way class using the provided helper.
         *
         * @param helper the helper instance to be used for partitioning logic.
         */
        public Partitioner_3Way(Helper<X> helper) {
            this.helper = helper;
        }

        /**
         * Swaps the elements at the specified positions in the provided array.
         * CONSIDER using helper.swap
         *
         * @param ys the array in which the elements are to be swapped
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

