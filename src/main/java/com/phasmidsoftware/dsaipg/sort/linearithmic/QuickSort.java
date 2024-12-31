/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.LazyLogger;

import java.util.Arrays;
import java.util.Collection;

public abstract class QuickSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    public QuickSort(String description, int N, int nRuns, Config config) {
        super(description, N, nRuns, config);
        insertionSort = setupInsertionSort(getHelper());
    }

    public QuickSort(Helper<X> helper) {
        super(helper);
        insertionSort = setupInsertionSort(helper);
    }

    private InsertionSort<X> setupInsertionSort(final Helper<X> helper) {
        return new InsertionSort<>(helper.clone("Quicksort: insertion sort"));
    }

    /**
     * Method to create a Partitioner.
     *
     * @return a Partitioner of X which is suitable for the quicksort method being used.
     */
    public abstract Partitioner<X> createPartitioner();

    /**
     * Method to set the partitioner.
     * <p>
     * NOTE: it would be much nicer if we could do this immutably but this isn't Scala, it's Java.
     *
     * @param partitioner the partitioner to be used.
     */
    public void setPartitioner(Partitioner<X> partitioner) {
        this.partitioner = partitioner;
    }

    /**
     * Method to sort.
     *
     * @param xs       sort the array xs, returning the sorted result, leaving xs unchanged.
     * @param makeCopy if set to true, we make a copy first and sort that.
     * @return the result (sorted version of xs).
     */
    public X[] sort(X[] xs, boolean makeCopy) {
        // CONSIDER merge with MergeSortBasic and maybe others.
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        sort(result, 0, result.length, 0);
        return result;
    }

    /**
     * Sort the sub-array xs[from] ... xs[to-1]
     *
     * @param xs    the complete array from which this sub-array derives.
     * @param from  the index of the first element to sort.
     * @param to    the index of the first element not to sort.
     * @param depth the depth of the recursion.
     */
    public void sort(X[] xs, int from, int to, int depth) {
        if (terminator(xs, from, to, depth)) return;
        getHelper().registerDepth(depth);
        Partition<X> partition = createPartition(xs, from, to);
        if (partitioner == null) throw new RuntimeException("partitioner not set");
        Collection<Partition<X>> partitions = partitioner.partition(partition);
        partitions.forEach(p -> sort(p.xs, p.from, p.to, depth + 1));
    }

    /**
     * Sort the sub-array xs[from] ... xs[to-1]
     *
     * @param xs   the complete array from which this sub-array derives.
     * @param from the index of the first element to sort.
     * @param to   the index of the first element not to sort.
     */
    public void sort(X[] xs, int from, int to) {
        sort(xs, from, to, 0);
    }

    /**
     * Protected method to determine to terminate the recursion of this quick sort.
     * NOTE that in this implementation, the depth is ignored.
     *
     * @param xs    the complete array from which this sub-array derives.
     * @param from  the index of the first element to sort.
     * @param to    the index of the first element not to sort.
     * @param depth the current depth of the recursion.
     * @return true if there is no further work to be done.
     */
    protected boolean terminator(X[] xs, int from, int to, int depth) {
        int n = to - from;
        if (n <= 1) return true;
        if (n == 2) {
            helper.sortPair(xs, from, to);
            return true;
        }
        if (n == 3) {
            helper.sortTrio(xs, from, to);
            return true;
        }
        // NOTE: we reduce the cutoff by 1 so that we can use 1 to disable cutoff (because 0 gives the default of 7).
        int cutoff = Math.max(getHelper().cutoff() - 1, 3); // NOTE it makes no sense to partition an array smaller than 3 elements, regardless of cutoff.
        if (n > getHelper().cutoff()) return false;
        insertionSort.sort(xs, from, to);
        return true;
    }

    public InsertionSort<X> getInsertionSort() {
        return insertionSort;
    }

    /**
     * Create a partition on ys from "from" to "to".
     *
     * @param ys   the array to partition
     * @param from the index of the first element to partition.
     * @param to   the index of the first element NOT to partition.
     * @param <Y>  the underlying type of ys.
     * @return a Partition of Y.
     */
    public static <Y extends Comparable<Y>> Partition<Y> createPartition(Y[] ys, int from, int to) {
        return new Partition<>(ys, from, to);
    }

    public static <Y extends Comparable<Y>> Partition<Y> createPartition(Y[] ys) {
        return createPartition(ys, 0, ys.length);
    }

    private final InsertionSort<X> insertionSort;

    protected Partitioner<X> partitioner;

    final static LazyLogger logger = new LazyLogger(QuickSort.class);
}