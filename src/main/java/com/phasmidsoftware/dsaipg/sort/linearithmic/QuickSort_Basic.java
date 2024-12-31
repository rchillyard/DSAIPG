/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.util.Config;

import java.util.ArrayList;
import java.util.List;

import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.getRunsConfig;

/**
 * Implementation of the QuickSort algorithm with a basic partitioning strategy.
 * This class extends a generic QuickSort class, utilizing a simple partitioner
 * to divide the array into smaller partitions for sorting.
 *
 * @param <X> the type of elements to be sorted, which must implement Comparable.
 */
public class QuickSort_Basic<X extends Comparable<X>> extends QuickSort<X> {

    public static final String DESCRIPTION = "QuickSort basic";

    public QuickSort_Basic(String description, int N, final int nRuns, Config config) {
        super(description, N, nRuns, config);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_Basic
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public QuickSort_Basic(Helper<X> helper) {
        super(helper);
        setPartitioner(createPartitioner());
    }

    /**
     * Constructor for QuickSort_Basic
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs.
     * @param config the configuration.
     */
    public QuickSort_Basic(int N, final int nRuns, Config config) {
        this(DESCRIPTION, N, nRuns, config);
    }

    /**
     * Constructor for QuickSort_Basic
     *
     * @param config the configuration.
     */
    public QuickSort_Basic(Config config) {
        this(0, getRunsConfig(config), config);
    }

    /**
     * Constructor for QuickSort_Basic class.
     *
     * @param n      the number of elements to be sorted.
     * @param config the configuration settings for the sorting algorithm.
     */
    public QuickSort_Basic(int n, Config config) {
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
         * Constructor for creating a basic partitioner with a specified helper.
         *
         * @param helper a Helper instance of type Y used to perform various partitioning-related tasks.
         */
        public Partitioner_Basic(Helper<Y> helper) {
            this.helper = helper;
        }

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
                while (true) {
                    XValue x = new XValue();
                    while (i < hi && x.update(ys, ++i) && helper.less(x.x, v)) {
                    }
                    XValue y = new XValue();
                    while (j > from && y.update(ys, --j) && helper.less(v, y.x)) {
                    }
                    if (i >= j) break;
                    helper.swap(ys, x.x, i, j, y.x);
                }
                helper.swap(ys, v, from, j);
            } else {
                while (true) {
                    while (i < hi && ys[++i].compareTo(v) < 0) {
                    }
                    while (j > from && ys[--j].compareTo(v) > 0) {
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
         * Auxiliary class to help with the instrumenting case.
         * In particular, we minimize the number of hits.
         */
        class XValue {
            public boolean update(Y[] xs, int i) {
                helper.incrementHits(1);
                this.x = xs[i];
                return true;
            }

            Y x;

            public XValue(Y x) {
                this.x = x;
            }

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
