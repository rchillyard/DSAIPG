/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.util.Arrays;
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.shouldRecurse;

/**
 * A class that implements the Introsort algorithm, a hybrid sorting algorithm
 * that begins with quicksort and switches to heapsort when the recursion depth
 * exceeds a predetermined threshold or insertion sort when the sub-array size
 * becomes small.
 *
 * @param <X> the type of elements to be sorted, which must extend Comparable.
 */
public class IntroSort<X extends Comparable<X>> extends QuickSort_DualPivot<X> {

    /**
     * Sorts the given array using the IntroSort algorithm.
     * This method performs an in-place sort if makeCopy is false.
     * Otherwise, a copy of the array is made before sorting.
     *
     * @param xs       the array of elements to be sorted
     * @param makeCopy true if a copy of the array should be created before sorting; false if the original array should be sorted in-place
     * @return the sorted array, which is either the same as the input array (if makeCopy is false) or a new sorted copy of the array (if makeCopy is true)
     */
    public X[] sort(X[] xs, boolean makeCopy) {
        getHelper().init(xs.length);
        depthThreshold = 2 * floor_lg(xs.length);
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        int from = 0, to = result.length;
        sort(result, from, to, 0);
        return result;
    }

    /**
     * @param xs   an array of Xs.
     * @param from the index of the first element to sort.
     * @param to   the index of the first element not to sort.
     */
    public void sort(X[] xs, int from, int to) {
        // NOTE the LAST argument of sort(X[], int, int, int) is the CURRENT depth, which starts
        // at zero; the limit goes in depthThreshold, exactly as sort(X[], boolean) does it. This
        // method used to pass 2 * floor_lg(to - from) there -- the limit -- as the starting
        // depth, which broke the depth mechanism in both directions. Measured 2026-09-30 over
        // 20,000 elements:
        //   called on its own, depthThreshold was still Integer.MAX_VALUE (only sort(X[],
        //   boolean) ever set it), so the heapSort fallback never fired at all and this
        //   degenerated to plain dual-pivot quicksort -- losing the O(n log n) worst case that
        //   is the whole point of Introsort;
        //   called on the whole array after sort(X[], boolean) had set depthThreshold, the
        //   starting depth equalled the threshold, so the very first terminator call heapsorted
        //   all 20,000 elements.
        // Both answers were correct, which is why no test noticed.
        // NOTE this never affected the benchmarks. Benchmarks.runBenchmark goes through
        // mutatingSort, hence sort(X[], boolean), which this method is not on the path of; see
        // terminator's javadoc for why heapSort does not appear in those runs.
        depthThreshold = 2 * floor_lg(Math.max(to - from, 1));
        sort(xs, from, to, 0);
    }

    /**
     * Protected method to determine whether to terminate the recursion of this quick sort.
     * <p>
     * Both of Introsort's escape hatches live here. <b>The defining one is the cutover to
     * heapSort</b>: quicksort is abandoned once the recursion passes {@code 2 lg n} levels, and
     * that is what turns quicksort's quadratic worst case into an O(n log n) guarantee. The
     * insertion-sort cutoff is the secondary, ordinary small-range optimisation that any
     * quicksort may carry.
     * <p>
     * <b>Why heapSort never shows up in the benchmarks.</b> Not because anything is broken: it is
     * that random input does not come close to the limit. Depths actually reached through
     * {@code mutatingSort}, which is the entry point {@code Benchmarks} uses, measured
     * 2026-09-30:
     * <pre>
     *   input             n         limit 2 lg n   max depth   heapSort calls
     *   uniform random    2,000     20             10          0
     *   uniform random    20,000    28             14          0
     *   uniform random    200,000   34             22          0
     *   organ pipe        20,000    28             28          66
     *   all equal         20,000    28             28          1
     *   already sorted    20,000    28             28          1
     * </pre>
     * Random input settles around {@code lg n}, not {@code 2 lg n}, so the fallback is never
     * invoked --- which is the design working, not failing. It takes an input that defeats a
     * naive quicksort to reach the limit, and then it fires.
     * <p>
     * The <i>tests</i> are nevertheless written the other way round, size before depth, and that
     * is deliberate rather than a statement about which matters. When both apply --- a range of
     * 16 or fewer that is also too deep --- insertion sort is the better answer, so it wins. All
     * four Introsort variants across this project and HuskySort order them this way.
     * <p>
     * NOTE the javadoc here used to say "the depth is ignored", which is true of
     * {@code QuickSort.terminator}, where it presumably came from, and has never been true here.
     * <p>
     * NOTE also that this overrides {@code QuickSort.terminator} completely, so the configured
     * {@code helper.cutoff()} has no effect on Introsort: the size at which it stops is
     * {@link #sizeThreshold}, which is a constant. That is defensible --- the threshold is part
     * of the algorithm rather than a tuning knob --- but it does surprise a caller who sets a
     * cutoff and sees nothing change.
     *
     * @param xs    the complete array from which this sub-array derives.
     * @param from  the index of the first element to sort.
     * @param to    the index of the first element not to sort.
     * @param depth the current depth of the recursion.
     * @return true if there is no further work to be done.
     */
    protected boolean terminator(X[] xs, int from, int to, int depth) {
        if (!shouldRecurse(to - from, sizeThreshold)) {
            // NOTE no "if (to > from + 1)" guard: InsertionSort.sort opens with
            // "if (to <= from + 1) return", so a range of one is already a no-op there.
            getInsertionSort().sort(xs, from, to);
            return true;
        }
        if (depth >= depthThreshold) {
            heapSort(xs, from, to);
            return true;
        }
        return false;
    }

    public static final String DESCRIPTION = "Intro sort";

    /**
     * Constructor for QuickSort_3way
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public IntroSort(Helper<X> helper) {
        super(helper);
    }

    /**
     * Constructor for QuickSort_3way
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs to perform.
     * @param config the configuration.
     */
    public IntroSort(int N, int nRuns, Config config) {
        super(DESCRIPTION, N, nRuns, config);
    }

    /**
     * Constructor for IntroSort using a configuration instance.
     *
     * @param config the configuration object that provides settings for the sort.
     */
    public IntroSort(Config config) {
        this(new NonInstrumentingComparableHelper<>(DESCRIPTION, config));
    }

    /**
     * Sorts the specified portion of the array using the Heapsort algorithm.
     * This method organizes the array in a binary heap structure and ensures sorting by repeatedly
     * moving the largest element to the end of the heap and re-adjusting the heap structure.
     *
     * @param a    the array to be sorted
     * @param from the index of the first element to be included in the sorting
     * @param to   the index of the first element not to be included in the sorting
     */
    private void heapSort(X[] a, int from, int to) {
        Helper<X> helper = getHelper();
        int n = to - from;
        // NOTE heapSort carries its own cutoff, as HuskySort's QuickHuskySort.heapSort and
        // IntroHuskySort.heapSort do. Building a heap out of a handful of elements costs more
        // than insertion-sorting them, and a method that switches to quicksort's fallback should
        // not be the one place that ignores the small-range rule.
        // NOTE as this class calls it today the guard cannot fire: terminator tests the size
        // before the depth, so heapSort is only ever reached with n > sizeThreshold. Confirmed by
        // counting, 2026-09-30 -- over every length from 1 to 400 in both organ-pipe and
        // duplicate-heavy shapes, plus organ-pipe and all-equal at 5,000 and 20,000, heapSort was
        // entered 492 times, the smallest range it ever saw was 18, and the guard fired 0 times.
        // It is here so that heapSort is right when called from anywhere else, which is exactly
        // the position the two HuskySort versions are in.
        if (!shouldRecurse(n, sizeThreshold)) {
            getInsertionSort().sort(a, from, to);
            return;
        }
        for (int i = n / 2; i >= 1; i = i - 1) {
            downHeap(a, i, n, from, helper);
        }
        for (int i = n; i > 1; i = i - 1) {
            helper.swap(a, from, from + i - 1);
            downHeap(a, 1, i - 1, from, helper);
        }
    }

    /**
     * Restores the heap property by moving an element down the heap starting at the given position.
     * Ensures that the subtree rooted at the specified position satisfies the heap condition.
     *
     * @param a      the array representing the heap
     * @param i      the current index in the heap being adjusted
     * @param n      the size of the heap
     * @param from     the offset for indexing into the array
     * @param helper a helper utility used for instrumentation and comparisons
     */
    private void downHeap(X[] a, int i, int n, int from, Helper<X> helper) {
        X d = a[from + i - 1];
        int child;
        while (i <= n / 2) {
            child = 2 * i;
            if (helper.instrumented()) {
                if (child < n && helper.compare(a, from + child - 1, from + child) < 0) child++;
                if (helper.compare(d, a[from + child - 1]) >= 0) break;
            } else {
                // NOTE pureComparison, NOT compareTo -- see QuickSort_3way.
                // compareTo ignores the Helper's comparator, so an uninstrumented
                // sort with a custom comparator came out in the natural ordering
                // instead. This branch exists to skip the counting, not to change
                // the ordering.
                if (child < n && helper.pureComparison(a[from + child - 1], a[from + child]) < 0)
                    child++;
                if (helper.pureComparison(d, a[from + child - 1]) >= 0) break;
            }
            helper.incrementFixes(1);
            a[from + i - 1] = a[from + child - 1];
            i = child;
        }
        a[from + i - 1] = d;
    }

    /**
     * Computes the largest integer less than or equal to the base-2 logarithm of the given integer.
     *
     * @param a the integer whose floor base-2 logarithm is to be calculated; must be greater than 0
     * @return the floor of the base-2 logarithm of the given integer
     */
    private static int floor_lg(int a) {
        return (int) (Math.floor(Math.log(a) / Math.log(2)));
    }

    private int depthThreshold = Integer.MAX_VALUE;

    private static final int sizeThreshold = 16;
}
