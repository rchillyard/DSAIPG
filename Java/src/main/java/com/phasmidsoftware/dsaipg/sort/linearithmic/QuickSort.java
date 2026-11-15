package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;

import java.util.Arrays;
import java.util.Collection;
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.shouldRecurse;

/**
 * Abstract class representing the QuickSort algorithm,
 * which is a recursive sorting algorithm using partitioning.
 *
 * @param <X> the type of elements to be sorted, which must implement Comparable.
 */
public abstract class QuickSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

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
        assert !getHelper().getConfig().getBoolean("quicksort", "verifysortedpartitions") || verifySortedPartitions(partitions);
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
        // NOTE the condition below is "n <= cutoff", i.e. "to <= from + cutoff", which is the
        // convention throughout this project and HuskySort: a cutoff of k means ranges of up to
        // k elements go to insertion sort. Reviewed 2026-09-30 and correct.
        // NOTE the "- 1", however, makes this sort's effective cutoff one less than MergeSort's
        // for the same configuration -- 19 against 20 at the default. It is safe, but it is not
        // needed for the reason given: Math.max(..., 3) already lets a cutoff of 1 disable the
        // mechanism, because n of 3 or less is handled above, so insertion sort is never reached
        // either way. (The old parenthetical also said that 0 gives a default of 7; that has not
        // been so since CUTOFF_DEFAULT became 20 -- 0 gives 20.) Dropping the "- 1" would align
        // the two sorts but changes one instrumented expectation:
        // QuickSort_ClassicTest.testSortWithInstrumenting5a counts 12,168 compares instead of
        // 12,189. Left alone pending a decision, since that figure is course material.
        int cutoff = Math.max(getHelper().cutoff(), 3); // NOTE it makes no sense to partition an array smaller than 3 elements, regardless of cutoff.
        if (shouldRecurse(n, cutoff)) return false;
        insertionSort.sort(xs, from, to);
        return true;
    }

    /**
     * Retrieves the instance of InsertionSort used in this class.
     *
     * @return an InsertionSort instance configured for sorting arrays of type X.
     */
    public InsertionSort<X> getInsertionSort() {
        return insertionSort;
    }

    /**
     * Constructor for the QuickSort class.
     *
     * @param description a String describing the QuickSort instance.
     * @param N           the size of the dataset to be sorted.
     * @param nRuns       the number of times the sort process will be run.
     * @param config      a configuration object containing parameters for the sorting algorithm.
     */
    public QuickSort(String description, int N, int nRuns, Config config) {
        super(description, N, nRuns, config);
        insertionSort = setupInsertionSort(getHelper());
    }

    /**
     * Constructor for QuickSort, which initializes the sorting algorithm with the given helper.
     *
     * @param helper the helper instance to be used by this QuickSort implementation for sorting-related operations.
     */
    public QuickSort(Helper<X> helper) {
        super(helper);
        insertionSort = setupInsertionSort(helper);
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

    /**
     * Creates a partition on the given array.
     *
     * @param ys the array to be partitioned
     * @param <Y> the type of the elements in the array, which must be comparable
     * @return a Partition object representing the partitioned range of the array
     */
    public static <Y extends Comparable<Y>> Partition<Y> createPartition(Y[] ys) {
        return createPartition(ys, 0, ys.length);
    }

    protected Partitioner<X> partitioner;

    private final InsertionSort<X> insertionSort;

    /**
     * Sets up and returns an instance of the InsertionSort class configured with a cloned helper.
     *
     * @param helper the Helper instance, which provides necessary utilities and configurations for sorting.
     * @return an InsertionSort instance initialized with a cloned helper specific for insertion sort.
     */
    private InsertionSort<X> setupInsertionSort(final Helper<X> helper) {
        return new InsertionSort<>(helper.clone("Quicksort: insertion sort", true));
    }

    private boolean verifySortedPartitions(Collection<Partition<X>> partitions) {
        return partitions.stream().allMatch(p -> p.isSorted(getHelper()));
    }

    final static LazyLogger logger = new LazyLogger(QuickSort.class);
}
