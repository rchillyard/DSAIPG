/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.classic;

import com.phasmidsoftware.dsaipg.sort.*;
import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.LazyLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.getRunsConfig;

/**
 * Bucket Sort.
 * <p>
 * CONSIDER re-implementing by doing ClassicSort first (based on buckets) then do insertion sort.
 * </p>
 * <p>
 *     NOTE: there is an alternative implementation of BucketSort in the huskySort package.
 * There are considerable differences so, for now, we'll leave both versions as is.
 * </p>
 *
 * @param <X> the underlying type which must extend Comparable.
 */
public class BucketSort<X extends Comparable<X>> extends ClassificationSorter<X, Void> {

    public static final String DESCRIPTION = "Bucket sort";
    public static final String ALPHABET = " abcdefghijklmnopqrstuvwxyz";
    public static final int ALPHABET_SIZE = ALPHABET.length();

    public static String[] DIGRAPHS;
    public static final int DIGRAPHS_SIZE = ALPHABET_SIZE * ALPHABET_SIZE;

    /**
     * Classifies the initial character of a given string based on its position in the alphabet.
     *
     * @param s the input string whose initial character is to be classified. Must not be null or empty.
     * @return the zero-based index of the initial character in the alphabet, or -1 if the character is not found.
     */
    public static Integer classifyStringInitial(String s) {
        return ALPHABET.indexOf(s.toLowerCase().charAt(0));
    }

    /**
     * Classifies a digraph (two-character string) from the input string `s` based on a predefined list of digraphs.
     * Converts the input string to lowercase and extracts the first two characters to form the digraph.
     * If the predefined list of digraphs is not initialized, it gets constructed using all possible digraphs from the `ALPHABET`.
     * The method then searches for the extracted digraph in the list using binary search.
     *
     * @param s the input string from which the digraph will be classified. It must contain at least one character.
     * @return the index of the digraph in the predefined list of digraphs, or a negative value if the digraph is not found.
     */
    public static Integer classifyStringDigraph(String s) {
        if (DIGRAPHS == null) {
            DIGRAPHS = new String[DIGRAPHS_SIZE];
            int i = 0;
            for (char c1 : ALPHABET.toCharArray())
                for (char c2 : ALPHABET.toCharArray())
                    DIGRAPHS[i++] = String.valueOf(c1) + c2;
        }
        String digraph = (s.toLowerCase() + " ").substring(0, 2);
        return Arrays.binarySearch(DIGRAPHS, digraph);
    }

    /**
     * Sorts the array `xs` within the specified range using a bucket sort approach.
     * If a classifier is not defined and the type of elements in the array is not a number,
     * a {@link SortException} is thrown. The method handles assigning elements to buckets,
     * sorting within buckets, and eventually sorting the entire array.
     *
     * @param xs   the array of elements to be sorted.
     * @param from the starting index of the range to be sorted (inclusive).
     * @param to   the ending index of the range to be sorted (exclusive).
     * @throws SortException if the classifier is undefined and the type of elements is not a number.
     */
    public void sort(X[] xs, int from, int to) {
        if (classifier == null) {
            if (Number.class.isAssignableFrom(xs[0].getClass())) {
                Function<X, Integer> numberClassifier = getNumberClassifier((Number[]) xs, 0, to, buckets.length);
                setClassifier((x, y) -> numberClassifier.apply(x));
            } else
                throw new SortException("BucketSort: classifier undefined AND the type being sorted is not a Number");
        }
        clearBuckets();
        assignToBuckets(xs, from, to);
        checkBuckets(xs);
        unloadBuckets(buckets, xs, helper);
        sort.sort(xs, from, to);
    }

    /**
     * Primary constructor.
     *
     * @param helper     the Helper to use.
     * @param classifier the classifier to yield an integer from an X (may be null on instantiation).
     * @param buckets    an array of Objects which will form the buckets for this BucketSort.
     */
    public BucketSort(Helper<X> helper, Function<X, Integer> classifier, Object[] buckets) {
        super(helper, convertToBiFunction(classifier));
        this.buckets = buckets;
        Helper<X> insertionSortHelper = helper.clone("insertion sort");
        this.sort = new InsertionSort<>(insertionSortHelper);
        for (int i = 0; i < buckets.length; i++) buckets[i] = new ArrayList<>();
        closeHelper = true;
        logger.info(DESCRIPTION + ": " + buckets.length + " buckets of mean size: " + 1.0 * helper.getN() / buckets.length);
    }

    /**
     * Secondary Constructor.
     * <p>
     * TESTME
     *
     * @param classifier the classifier to yield an integer from an X (may be null on instantiation).
     * @param nBuckets   the number of buckets to use.
     * @param N          the number of elements (only affects the InsertionSort sorter).
     * @param config     the configuration.
     */
    public BucketSort(Function<X, Integer> classifier, int nBuckets, int N, Config config) {
        this(HelperFactory.create(DESCRIPTION, N, config), classifier, new Object[nBuckets]);
        closeHelper = true;
    }

    /**
     * Secondary Constructor.
     *
     * @param classifier the classifier to yield an integer from an X (may be null on instantiation).
     * @param nBuckets   the number of buckets to use.
     * @param helper     the Helper to use.
     */
    public BucketSort(Function<X, Integer> classifier, int nBuckets, Helper<X> helper) {
        this(helper, classifier, new Object[nBuckets]);
    }

    /**
     * Secondary constructor.
     * <p>
     * TESTME
     *
     * @param nBuckets the number of buckets to use.
     * @throws IOException a configuration problem.
     */
    BucketSort(int nBuckets) throws IOException {
        this(null, nBuckets, new NonInstrumentingComparableHelper<>(DESCRIPTION, Config.load(BucketSort.class)));
        closeHelper = true;
    }

    /**
     * Creates a case-independent bucket sort instance using the provided classifier, number of buckets, size parameter,
     * and configuration. This method returns a bucket sort implementation designed for strings where comparisons
     * are case-insensitive.
     *
     * @param classifier a function that maps each string to an integer bucket index.
     * @param nBuckets   the number of buckets to use for sorting.
     * @param N          the number of elements to be processed (used primarily for helper initialization).
     * @param config     the configuration settings to customize the sorting process.
     * @return a BucketSort instance configured for case-insensitive string sorting.
     */
    public static BucketSort<String> CaseIndependentBucketSort(Function<String, Integer> classifier, int nBuckets, int N, Config config) {
        return new BucketSort<>(HelperFactory.createGeneric(DESCRIPTION, String.CASE_INSENSITIVE_ORDER, N, getRunsConfig(config), config), classifier, new Object[nBuckets]);
    }

    /**
     * Clears all elements from each bucket in the {@code buckets} array.
     * <p>
     * This method iterates over the {@code buckets} array, which contains lists that hold elements of type {@code X},
     * and invokes the {@code clear} method on each list to remove all its elements.
     * It ensures that the {@code buckets} are emptied before starting a new sorting operation or reuse.
     */
    private void clearBuckets() {
        for (Object b : buckets) //noinspection unchecked
            ((List<X>) b).clear();
    }

    /**
     * Validates that the total number of elements across all buckets matches the expected length of the input array `xs`.
     * Iterates through the buckets, calculates the total count of elements across them, and compares
     * it with the length of the input array. If there is a mismatch, an exception is thrown.
     *
     * @param xs the array of elements to be checked against the total number of elements in all buckets.
     * @throws RuntimeException if the total number of elements in the buckets does not match the length of the input array.
     */
    private void checkBuckets(X[] xs) {
        int count = 0;
        for (Object b : buckets) {
            @SuppressWarnings("unchecked") int size = ((List<X>) b).size();
            count += size;
        }
        if (count != xs.length) throw new RuntimeException("incorrect number of buckets: " + count + ", " + xs.length);
    }

    /**
     * Assigns elements from the specified range of the input array to their respective buckets.
     * This method uses a classifier to determine the bucket index for each element in the array
     * and then adds the element to the corresponding bucket.
     * <p>
     * The helper is used to manage copies and hits, incrementing the respective counters
     * for the operations performed during the bucket assignment process.
     *
     * @param xs   the input array containing the elements to be assigned to buckets.
     * @param from the starting index of the range (inclusive) to process within the input array.
     * @param to   the ending index of the range (exclusive) to process within the input array.
     */
    private void assignToBuckets(X[] xs, int from, int to) {
        helper.incrementCopies(to - from);  // this accounts for copying xs[i] to buckets[index]
        helper.incrementHits(to - from); // this accounts for adding the value to buckets[index]
        // Assign the elements to the buckets
        for (int i = from; i < to; i++) {
            X x = helper.get(xs, i);
            int index = classify(x, null);
            if (index < 0) index = 0;
            if (index >= buckets.length) index = buckets.length - 1;
            //noinspection unchecked
            ((List<X>) buckets[index]).add(x);
        }
    }

    /**
     * Creates a numeric classifier function that maps elements of type T to integer bucket indices based on their
     * numeric values. The classifier determines the minimum and maximum values from the specified portion of the input
     * array and calculates equally sized intervals (buckets) between them. Each element is then classified into one
     * of the buckets based on its numeric value.
     *
     * @param <T>      the type of input elements processed by the classifier function.
     * @param xs       an array of Number objects from which the classifier will derive numeric ranges.
     * @param from     the starting index (inclusive) of the subarray to consider in the input array.
     * @param to       the ending index (exclusive) of the subarray to consider in the input array.
     * @param classes  the number of intervals (buckets) into which the numeric range is divided.
     * @return a Function that maps objects of type T to an integer bucket index based on their numeric value.
     */
    private static <T> Function<T, Integer> getNumberClassifier(final Number[] xs, final int from, final int to, final int classes) {
        // Determine the min, max and gap.
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int i = from; i < to; i++) {
            if (xs[i].doubleValue() < min) min = xs[i].doubleValue();
            if (max < xs[i].doubleValue()) max = xs[i].doubleValue();
        }
        double gap = (max - min) / classes;
        logger.debug("creating numeric classifier with gap size: " + gap);
        return numberClassifier(min, gap, classes);
    }

    /**
     * Creates a numeric classifier function that maps objects of type T to integer bucket indices
     * based on their numeric values in relation to the specified minimum value, gap, and number of buckets.
     *
     * @param <T>     the type of input elements to be classified.
     * @param min     the minimum numeric value used for bucket classification.
     * @param gap     the size of each bucket interval.
     * @param nBuckets the total number of buckets.
     * @return a Function that maps an object of type T to an integer bucket index based on its numeric value.
     */
    private static <T> Function<T, Integer> numberClassifier(final double min, final double gap, int nBuckets) {
        return x -> {
            int index = (int) Math.floor((((Number) x).doubleValue() - min) / gap);
            if (index < 0) index = 0;
            if (index >= nBuckets) index = nBuckets - 1;
            return index;
        };
    }

    /**
     * A static logger instance used for logging messages and diagnostics in the {@code BucketSort} class.
     * The logger leverages the {@code LazyLogger} utility to facilitate efficient logging,
     * ensuring that log messages are only constructed when necessary to minimize overhead.
     */
    private final static LazyLogger logger = new LazyLogger(BucketSort.class);

    /**
     * Method to unload and sort the buckets into the array xs.
     * <p>
     * CONSIDER using from and to as parameters (instead of xs.length).
     *
     * @param buckets an array of Bag of X elements.
     * @param xs      an array of X elements to be filled.
     * @param helper  a helper whose compare method we will use.
     * @param <X>     the underlying type of the array and the Helper.
     */
    @SuppressWarnings("unchecked")
    private static <X extends Comparable<X>> void unloadBuckets(Object[] buckets, X[] xs, final Helper<X> helper) {
        final Index index = new Index(xs.length);
        Arrays.stream(buckets).forEach(xes -> unloadBucket(xs, helper, index, (List<X>) xes));
    }

    /**
     * Unloads elements from the provided list, placing them into the destination array `xs` using the provided index.
     * Increments hit and copy counts in the helper for each operation and handles potential index bounds issues.
     *
     * @param <X>    the type of elements being processed, which must be comparable.
     * @param xs     the destination array where elements will be placed.
     * @param helper the helper instance used for tracking hits and copies during the unloading process.
     * @param index  the index instance used for determining the next insertion point in the destination array.
     * @param xes    the list of elements to be unloaded into the destination array.
     * @throws RuntimeException if the index goes out of bounds while unloading elements.
     */
    @SuppressWarnings("unchecked")
    private static <X extends Comparable<X>> void unloadBucket(X[] xs, Helper<X> helper, Index index, List<X> xes) {
        final Object[] objects = xes.toArray();
        int size = xes.size();
        helper.incrementCopies(size);
        helper.incrementHits(2L * size);
        for (Object x : objects)
            try {
                int next = index.getNext();
                xs[next] = (X) x;
            } catch (IndexException e) {
                throw new RuntimeException("unloadBucket: index out of bounds: " + e.getLocalizedMessage());
            }
    }

    /**
     * The Index class is used to generate a sequence of indices starting from 0
     * and ensures that the indices do not exceed a predefined upper limit.
     * It provides a mechanism to retrieve the next index in the sequence,
     * throwing an exception when the limit is exceeded.
     */
    static class Index {
        public Index(int n) {
            this.n = n;
        }

        int index = 0;

        int getNext() throws IndexException {
            if (index >= 0 && index < n)
                return index++;
            else throw new IndexException(n); // TESTME
        }

        private final int n;
    }

    /**
     * A custom exception that is thrown when an index is either negative or exceeds the allowable range.
     */
    static class IndexException extends Exception {
        /**
         * Constructs a new exception with {@code null} as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         */
        public IndexException(int n) {
            super("Index negative or too large: " + n);
        }
    }

    private final Object[] buckets;
    private final Sort<X> sort;

}