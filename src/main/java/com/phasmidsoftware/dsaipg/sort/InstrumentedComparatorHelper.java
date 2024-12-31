/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparator;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.LazyLogger;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.*;
import static com.phasmidsoftware.dsaipg.util.Utilities.formatWhole;

/**
 * Helper class for sorting methods with instrumentation of compares and swaps, and in addition, bounds checks.
 * This Helper class may be used for analyzing sort methods but will run at slightly slower speeds than the super-class.
 * TODO merge this class with InstrumentedHelper.
 *
 * @param <X> the underlying type (must be Comparable).
 */
public class InstrumentedComparatorHelper<X> extends BaseComparatorHelper<X> {

    final static LazyLogger logger = new LazyLogger(InstrumentedComparatorHelper.class);

    public boolean instrumented() {
        return true;
    }

    /**
     * Get the element at xs[i].
     *
     * @param xs the source array.
     * @param i  the target index.
     * @return the value of xs[i].
     */
    @Override
    public X get(X[] xs, int i) {
        instrumenter.incrementHits(1);
        return xs[i];
    }

    /**
     * Compare values v and xs[j] and return true if v is less than xs[j].
     *
     * @param xs the array.
     * @param v  the first value.
     * @param j  the index of the second value.
     * @return true if v is less than xs[j].
     */
    public boolean less(X[] xs, X v, int j) {
        return less(v, get(xs, j));
    }

    /**
     * Compare values xs[i] and w and return true if xs[i] is less than w.
     *
     * @param xs the array.
     * @param i  the index of the first value.
     * @param w  the second value.
     * @return true if v is less than w.
     */
    public boolean less(X[] xs, int i, X w) {
        return less(get(xs, i), w);
    }

    /**
     * Compare values xs[i] and xs[j] and return true if xs[i] is less than xs[j].
     *
     * @param xs the array.
     * @param i  the index of the first value.
     * @param j  the index of the second value.
     * @return true if xs[i] is less than xs[j].
     */
    public boolean less(X[] xs, int i, int j) {
        return less(xs, get(xs, i), j);
    }

    /**
     * Swap the elements of array xs at indices i and j.
     *
     * @param xs the array.
     * @param i  one of the indices.
     * @param j  the other index.
     */
    public void swap(X[] xs, int i, int j) {
        swap(xs, get(xs, i), i, j);
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j]
     *
     * @param xs the array of X elements.
     * @param v  the value of xs[i].
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     */
    public void swap(X[] xs, X v, int i, int j) {
        swap(xs, v, i, j, get(xs, j));
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j]
     *
     * @param xs the array of X elements.
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     * @param w  the value of xs[j].
     */
    public void swap(X[] xs, int i, int j, X w) {
        swap(xs, get(xs, i), i, j, w);
    }

    /**
     * Method to perform a general swap, i.e., between xs[i] and xs[j]
     *
     * @param xs the array of X elements.
     * @param v  the value of xs[i].
     * @param i  the index of the lower of the elements to be swapped.
     * @param j  the index of the higher of the elements to be swapped.
     * @param w  the value of xs[j].
     */
    public void swap(X[] xs, X v, int i, int j, X w) {
        if (i == j) return;
        instrumenter.incrementSwaps(1);
        if (instrumenter.countFixes()) enumerateFixes(xs, i, j, Integer.signum(pureComparison(v, w)));
        if (logger.isDebugEnabled()) {
            if (xs[i] != v)
                logger.warn("swap: WARNING: v=" + v + " is not equal to xs[" + i + "]: " + xs[i]);
            if (xs[j] != w)
                logger.warn("swap: WARNING: w=" + w + " is not equal to xs[" + j + "]: " + xs[j]);
        }
        instrumenter.incrementHits(2);
        super.swap(xs, v, i, j, w);
    }

    /**
     * Method to perform a stable swap using half-exchanges,
     * i.e. between xs[i] and xs[j] such that xs[j] is moved to index i,
     * and xs[i] through xs[j-1] are all moved up one.
     * This type of swap is used by insertion sort.
     *
     * @param xs the array of Xs.
     * @param i  the index of the destination of xs[j].
     * @param j  the index of the right-most element to be involved in the swap.
     */
    public void swapInto(X[] xs, int i, int j) {
        instrumenter.incrementSwaps(j - i);
        instrumenter.incrementFixes(j - i);
        super.swapInto(xs, i, j);
    }

    /**
     * Method to perform a stable swap using half-exchanges, and binary search.
     * i.e. x[i] is moved leftwards to its proper place, and all elements from
     * the destination of x[i] through x[i-1] are moved up one place.
     * This type of swap is used by insertion sort.
     * <p>
     * NOTE: this needs more precision.
     *
     * @param xs the array of X elements, whose elements 0 through i-1 MUST be sorted.
     * @param i  the index of the element to be swapped into the ordered array xs[0...i-1].
     */
    public void swapIntoSorted(X[] xs, int i) {
        int j = binarySearch(xs, 0, i, xs[i]);
        if (j < 0) j = -j - 1;
        if (j < i) swapInto(xs, j, i);
    }

    /**
     * Count the number of inversions of this array.
     *
     * @param xs an array of Xs.
     * @return the number of inversions.
     */
    public long inversions(X[] xs) {
        return InsertionSortComparator.countInversions(xs, getComparator());
    }

    /**
     * Method to enumerate the number of inversions fixed by the swap of i and j elements in the array xs.
     * NOTE: this may not be accurate when there are duplicates.
     *
     * @param xs    the array.
     * @param i     the lower index.
     * @param j     the upper index.
     * @param sense the sense of "fix."
     */
    private void enumerateFixes(X[] xs, int i, int j, int sense) {
        instrumenter.incrementFixes(sense);
        X v = xs[i];
        X w = xs[j];
        for (int k = i + 1; k < j; k++) {
            X x = xs[k];
            if (pureComparison(w, x) < 0 && pureComparison(x, v) < 0) instrumenter.incrementFixes(2 * sense);
        }
    }

    /**
     * This version of binarySearch is copied from Arrays, but is generalized to operate on X.
     * Furthermore, it does not check its indexes like the Arrays public method.
     *
     * @param xs   the array of X elements.
     * @param from the 'from' index.
     * @param to   the to index.
     * @param key  the key.
     * @return the index of the element where <code>key</code> was found, otherwise the index where it would have been found.
     */
    private int binarySearch(X[] xs, int from, int to, X key) {
        int low = from;
        int high = to - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            incrementHits(1);
            int cmp = compare(xs[mid], key);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    public boolean swapConditional(X[] xs, int i, int j) {
        return swapConditional(xs, get(xs, i), i, j);
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @param w  the value of xs[j].
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    public boolean swapConditional(X[] xs, int i, int j, X w) {
        return swapConditional(xs, get(xs, i), i, j, w);
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param v  the value of xs[i].
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    public boolean swapConditional(X[] xs, X v, int i, int j) {
        return swapConditional(xs, v, i, j, get(xs, j));
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[j], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param v  the value of xs[i].
     * @param i  the index of the lower element.
     * @param j  the index of the upper element.
     * @param w  the value of xs[j]
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    public boolean swapConditional(X[] xs, X v, int i, int j, X w) {
        if (i == j) return false;
        if (i > j) return swapConditional(xs, w, j, i, v);
        boolean exchange = compare(v, w) > 0;
        if (exchange) swap(xs, v, i, j, w);
        return exchange;
    }

    /**
     * Method to perform a stable swap, but only if xs[i] is less than xs[i-1], i.e. out of order.
     *
     * @param xs the array of elements under consideration
     * @param i  the index of the upper element.
     * @return true if there was an inversion (i.e., the order was wrong and had to be fixed).
     */
    public boolean swapStableConditional(X[] xs, int i) {
        return swapConditional(xs, i - 1, i);
    }

    /**
     * Copy the element at source[j] into target[i]
     *
     * @param source the source array.
     * @param i      the target index.
     * @param target the target array.
     * @param j      the source index.
     */
    public void copy(X[] source, int i, X[] target, int j) {
        copy(get(source, i), target, j);
    }

    public void copy(X x, X[] target, int j) {
        instrumenter.incrementCopies(1);
        instrumenter.incrementHits(1);
        target[j] = x;
    }

    public void copyBlock(X[] source, int i, X[] target, int j, int n) {
        super.copyBlock(source, i, target, j, n);
        instrumenter.incrementCopies(n);
        instrumenter.incrementHits(2L * n);
    }

    public void distributeBlock(X[] source, int from, int to, X[] target, Function<X, Integer> f) {
        super.distributeBlock(source, from, to, target, f);
        instrumenter.incrementCopies(to - from);
        instrumenter.incrementHits((to - from) * 2L);
    }

    public X[] copyArray(X[] a) {
        instrumenter.incrementCopies(a.length);
        return super.copyArray(a);
    }

    /**
     * Compare v and w
     *
     * @param v the first X.
     * @param w the second X.
     * @return the result of comparing v and w.
     */
    public int compare(X v, X w) {
        instrumenter.incrementCompares();
        // NOTE in the following lines, we depend on the fact that X is an object type (and not a primitive).
        instrumenter.incrementLookups();
        instrumenter.incrementLookups();
        return pureComparison(v, w);
    }

    /**
     * Compare v with element j.
     *
     * @param xs the array.
     * @param v  the first comparand.
     * @param j  the index of the second comparand.
     * @return the result of comparing xs[i] to w.
     */
    public int compare(X[] xs, X v, int j) {
        return compare(v, get(xs, j));
    }

    /**
     * Compare element i of xs with w.
     *
     * @param xs the array.
     * @param i  the index of the first comparand.
     * @param w  the other comparand.
     * @return the result of comparing xs[i] to w.
     */
    public int compare(X[] xs, int i, X w) {
        return compare(get(xs, i), w);
    }

    /**
     * Compare elements of an array.
     *
     * @param xs the array.
     * @param i  one of the indices.
     * @param j  the other index.
     * @return the result of compare(xs[i], xs[j]).
     */
    public int compare(X[] xs, int i, int j) {
        if (i == j) return 0;
        return compare(xs, i, get(xs, j));
    }

    public int MSDCutoff() {
        return MSDcutoff;
    }

    @Override
    public String toString() {
        return "Instrumenting helper for " + description + " with " + formatWhole(n) + " elements";
    }

    /**
     * Initialize this Helper.
     *
     * @param n the size to be managed.
     */
    public void init(int n) {
        instrumenter.init(n, nRuns);
        if (n == this.n) return;
        super.init(n);
    }

    /**
     * Method to do any required preProcessing.
     *
     * @param xs the array to be sorted.
     * @return the array after any pre-processing.
     */
    public X[] preProcess(X[] xs) {
        final X[] result = super.preProcess(xs);
        // NOTE: because counting inversions is so slow, we only do if for a (configured) number of samples.
        if (countInversions-- > 0) {
            if (instrumenter.getStatPack() != null)
                instrumenter.getStatPack().add(Instrumenter.INVERSIONS, inversions(result));
            else throw new RuntimeException("InstrumentedComparableHelper.postProcess: no StatPack");
        }
        return result;
    }

    /**
     * Method to post-process the array xs after sorting.
     * By default, this method checks that an array is sorted.
     *
     * @param xs the array to be tested.
     *                               TODO log the message
     *                               TODO show the number of inversions
     */
    public void postProcess(X[] xs) {
        super.postProcess(xs);
        int index = findInversion(xs);
        if (index != -1)
            throw new HelperException(this + ": Array is not sorted (Comparator) at index: " + index + ": " + xs[index - 1] + ", " + xs[index]);
        instrumenter.gatherStatistic();
    }

    public void registerDepth(int depth) {
        if (depth > maxDepth) maxDepth = depth;
    }

    public int maxDepth() {
        return maxDepth;
    }

    public void close() {
        if (instrumenter.isShowStats() && instrumenter.getStatPack() != null)
            logger.info(n + AT + description + ": " + instrumenter.getStatPack());
        super.close();
    }

    /**
     * Method that retrieves the current value of randomArray.
     *
     * @return an X[] unless random() was never invoked in which case we return null.
     */
    public X[] getRandomArray() {
        return randomArray;
    }

    /**
     * Constructor for explicit random number generator.
     *
     * @param description  the description of this Helper (for humans).
     * @param comparator   the Comparator of X to be used.
     * @param n            the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param random       a random number generator.
     * @param nRuns        an (explicit) number of runs (for statistics).
     * @param instrumenter an implementer of Instrument.
     * @param config       the configuration (note that the seed value is ignored).
     */
    public InstrumentedComparatorHelper(String description, Comparator<X> comparator, int n, Random random, int nRuns, final Instrument instrumenter, Config config) {
        // CONSIDER using config.toString here somewhere.
        super(description, comparator, n, random, instrumenter, config);
        this.countInversions = config.getInt(Instrumenter.INSTRUMENTING, Instrumenter.INVERSIONS, 0);
        this.MSDcutoff = config.getInt(HELPER, MSDCUTOFF, MSD_CUTOFF_DEFAULT);
        this.nRuns = nRuns;
    }

    public InstrumentedComparatorHelper(String description, Comparator<X> comparator, int n, Config config) {
        this(description, comparator, n, getRunsConfig(config), config);
    }

    /**
     * Constructor to create a Helper
     *
     * @param description the description of this Helper (for humans).
     * @param comparator  the Comparator of X to be used.
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param nRuns       an (explicit) number of runs (for statistics).
     * @param config      The configuration.
     */
    public InstrumentedComparatorHelper(String description, Comparator<X> comparator, int n, int nRuns, Config config) {
        this(description, comparator, n, getSeed(config), nRuns, config);
    }

    /**
     * Constructor to create a Helper
     *
     * @param description the description of this Helper (for humans).
     * @param comparator  the Comparator of X to be used.
     * @param n           the number of elements expected to be sorted. The field n is mutable so can be set after the constructor.
     * @param seed        the seed for the random number generator.
     * @param nRuns       the explicit number of runs expected.
     * @param config      the configuration.
     */
    public InstrumentedComparatorHelper(String description, Comparator<X> comparator, int n, long seed, int nRuns, Config config) {
        this(description, comparator, n, new Random(seed), nRuns, new Instrumenter(config), config);
    }

    public InstrumentedComparatorHelper(String description, Comparator<X> comparator, int nElements, long seed, Config config) {
        this(description, comparator, nElements, new Random(seed), getRunsConfig(config), new Instrumenter(config), config);
    }

    /**
     * Constructor to create a Helper with a random seed and an n value of 0.
     * <p>
     * NOTE: this constructor is used only by unit tests
     *
     * @param description the description of this Helper (for humans).
     * @param comparator  the Comparator of X to be used.
     */
    public InstrumentedComparatorHelper(String description, Comparator<X> comparator, Config config) {
        this(description, comparator, 0, config);
    }

    /**
     * If instrumenting, increment the number of copies by n.
     *
     * @param n the number of copies made.
     */
    public void incrementCopies(int n) {
        instrumenter.incrementCopies(n);
    }

    /**
     * Method to keep track of hits (array accesses that MAY not be in cache)...
     * but only if instrumenting.
     *
     * @param n the number of hits.
     */
    public void incrementHits(long n) {
        instrumenter.incrementHits(n);
    }

    public String showStats() {
        return description + ": " + instrumenter.getStatPack().toString();
    }

    public Helper<X> clone(String description, int N) {
        return new InstrumentedComparatorHelper<>(description, getComparator(), N, random, nRuns, instrumenter, config);
    }

    public Helper<X> clone(String description, Comparator<X> comparator, int N) {
        return new InstrumentedComparatorHelper<>(description, comparator, N, random, nRuns, instrumenter, config);
    }

    public String showFixes(X[] xs) {
        checkFixes(xs);
        return "fixes+inversions: " + (instrumenter.getFixes() + inversions(xs));
    }

    // NOTE: the following private methods are only for testing (using reflection).

    private void checkFixes(X[] xs) {
        if (instrumenter.getStatPack() != null) {
            final double initial = instrumenter.getStatPack().total(Instrumenter.INVERSIONS);
            final long inversions = inversions(xs);
            if (instrumenter.getFixes() + inversions != initial) {
                System.err.println("inversions and fixes don't match");
            }
        }
    }

    protected final int MSDcutoff;
    protected final int nRuns;
    protected long countInversions;
    protected int maxDepth = 0;

    public static final String AT = "@";
    /**
     * Default value of runs when not specified in config.init (Help section).
     * Its only significance actually is that it's the initial number of samples in the statistics array.
     * It shouldn't matter too much because those arrays will grow as needed.
     */
    public static final int DEFAULT_RUNS = 1;
    public static final int MSD_CUTOFF_DEFAULT = 256;

    public static int getRunsConfig(Config config) {
        return config.getInt(HELPER, "runs", DEFAULT_RUNS);
    }

}