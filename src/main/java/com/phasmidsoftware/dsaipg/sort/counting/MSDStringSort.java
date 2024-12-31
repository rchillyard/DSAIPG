/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.counting;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.HelperFactory;
import com.phasmidsoftware.dsaipg.sort.Sort;
import com.phasmidsoftware.dsaipg.sort.SortWithHelperAndAdditionalMemory;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_3way;
import com.phasmidsoftware.dsaipg.util.CodePointMapper;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.SuffixComparator;

import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.MSD_CUTOFF_DEFAULT;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.HELPER;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.MSDCUTOFF;

/**
 * Implements the MSD (Most-Significant-Digit) String Sort algorithm
 * for sorting an array of strings. The algorithm recursively partitions
 * strings based on the current character (digit) being evaluated, until the entire
 * array is sorted.
 */
public class MSDStringSort extends SortWithHelperAndAdditionalMemory<String> {

    public static final String DESCRIPTION = "MSD string sort ";

    /**
     * Primary constructor.
     *
     * @param mapper the required CodePointMapper.
     * @param helper the appropriate Helper.
     */
    MSDStringSort(CodePointMapper mapper, Helper<String> helper) {
        super(helper, (s, d) -> mapCodePoint(mapper, s, d));
        this.mapper = mapper; // CONSIDER remove this: all we actually need is the range.
    }

    /**
     * Constructs an MSDStringSort object for sorting strings using Most-Significant-Digit (MSD) radix sort.
     *
     * @param mapper      the CodePointMapper used to interpret and map the characters of the strings.
     * @param description a description of the sorting process or context.
     * @param N           the maximum number of characters in any string to consider during sorting.
     * @param config      the Config object containing configuration settings for the sort.
     * @param nRuns       the number of runs or iterations to be used for any specific task within the sorting process.
     */
    public MSDStringSort(CodePointMapper mapper, String description, int N, Config config, int nRuns) {
        this(mapper, HelperFactory.createGeneric(description, mapper.comparator, N, nRuns, config));
        init(N);
        closeHelper = true;
    }

    /**
     * Constructs an MSDStringSort object for sorting strings using Most-Significant-Digit (MSD) radix sort.
     *
     * @param mapper the CodePointMapper used to interpret and map the characters of the strings.
     * @param N      the maximum number of characters in any string to consider during sorting.
     * @param nRuns  the number of runs or iterations to be used during any specific task in the sorting process.
     * @param config the configuration object containing settings such as cutoff thresholds and other parameters.
     */
    public MSDStringSort(CodePointMapper mapper, int N, int nRuns, Config config) {
        this(mapper, DESCRIPTION + mapper + " with cutoff=" + config.getString(HELPER, MSDCUTOFF, MSD_CUTOFF_DEFAULT + ""), N, config, nRuns);
    }

    /**
     * Generic, mutating sort method which operates on a sub-array.
     *
     * @param xs   sort the array xs from "from" until "to" (exclusive of to).
     * @param from the index of the first element to sort.
     * @param to   the index of the first element not to sort.
     */
    public void sort(String[] xs, int from, int to) {
        doSort(xs, from, to, 0);
    }

    /**
     * Sort from xs[from] to xs[to] (exclusive), ignoring the first d characters of each String.
     * This method is recursive.
     *
     * @param xs   the array to be sorted.
     * @param from the low index.
     * @param to   the high index (one above the highest actually processed).
     * @param d    the number of characters in each String to be skipped.
     */
    private void doSort(String[] xs, int from, int to, int d) {
        int n = to - from;
        if (n <= 1)
            return;
        // NOTE that we never cut over to Quicksort at the top-level.
        if (d > 0 && n <= helper.MSDCutoff()) cutToQuicksort(xs, from, to, d, n);
        else doMSDrecursive(xs, from, to, d);
    }

    /**
     * A private method that transitions to using a 3-way Quicksort algorithm for sorting a subset of strings
     * in the array.
     *
     * @param xs   the array of strings to be sorted.
     * @param from the starting index of the subset of the array to sort (inclusive).
     * @param to   the ending index of the subset of the array to sort (exclusive).
     * @param d    the number of leading characters to be ignored during the comparison.
     * @param n    the maximum number of items considered for the helper and sorting process.
     */
    private void cutToQuicksort(String[] xs, int from, int to, int d, int n) {
        SuffixComparator suffixComparator = new SuffixComparator(helper.getComparator(), d);
        Helper<String> cloned = helper.clone("MSD 3-way quicksort", suffixComparator, n);
        try (Sort<String> sorter = new QuickSort_3way<>(cloned)) {
            sorter.sort(xs, from, to);
        }
    }

    /**
     * Recursively sorts the array of Strings using the Most-Significant-Digit (MSD) radix sort algorithm.
     * This method focuses on sorting the Strings based on their characters starting from the specified depth {@code d}.
     *
     * @param xs   the array of Strings to be sorted.
     * @param from the starting index of the subset of the array to process (inclusive).
     * @param to   the ending index of the subset of the array to process (exclusive).
     * @param d    the depth or character position from which the sorting process continues.
     */
    void doMSDrecursive(String[] xs, int from, int to, int d) {
        int n = to - from;
        String[] aux = new String[n]; // CONSIDER optimizing the usage of aux.
        additionalMemory(n);
        int[] count = new int[mapper.range + 1];
        additionalMemory(mapper.range + 1);

        // Compute frequency counts.
        helper.incrementHits(n); // for the count.
        for (int i = from; i < to; i++) count[classify(xs, i, d) + 1]++;

        // Accumulate counts.
        int countR = count[0];
        for (int r = 1; r < mapper.range; r++) {
            helper.incrementHits(1); // for the count.
            count[r] += countR;
            countR = count[r];
        }

        // Distribute.
        helper.distributeBlock(xs, from, to, aux, x -> count[classify(x, d)]++);

        // Copy back.
        helper.copyBlock(aux, 0, xs, from, n);

        // Recursively sort on the next character position in each String.
        // TO BE IMPLEMENTED 
        // END SOLUTION
        additionalMemory(-(n + mapper.range + 1));
    }

    /**
     * Maps the character of a string at a specific position using a given CodePointMapper.
     *
     * @param mapper the CodePointMapper instance used to map the character at the specified position.
     * @param x      the string whose character at the specified position is to be mapped.
     * @param d      the position in the string to retrieve the character for mapping.
     * @return the integer result of the mapping, as provided by the CodePointMapper.
     */
    private static int mapCodePoint(CodePointMapper mapper, String x, int d) {
        return mapper.map(charAt(x, d));
    }

    /**
     * Returns the character (as an integer) at a specific position in the string.
     * If the position exceeds the length of the string, returns 0.
     *
     * @param s the string from which the character is to be retrieved.
     * @param d the position of the character to be retrieved in the string.
     * @return the character at the specified position as an integer,
     * or 0 if the position exceeds the length of the string.
     */
    private static int charAt(String s, int d) {
        if (d < s.length()) return s.charAt(d);
        else return 0; // CONSIDER creating a value in CodePointMapper to specify this.
    }

    private final CodePointMapper mapper;

    /**
     * QuickSortThreeWayByFunction is a specialized implementation of a three-way QuickSort algorithm
     * for sorting arrays of Strings, extending the QuickSort_3way class.
     * <p>
     * This class inherits the functionality of the QuickSort_3way class and allows customization
     * through the provided Helper object. It uses the three-way partitioning strategy, which divides
     * the array into three segments during partitioning: less than, equal to, and greater than the pivot.
     * <p>
     * This implementation is particularly effective when sorting datasets containing many duplicate keys,
     * as it reduces unnecessary comparisons and swaps by grouping equal elements together.
     * <p>
     * Constructor Details:
     * - The constructor takes a Helper<String> object as an input parameter, which manages
     * auxiliary operations such as comparisons, instrumentation, and possibly additional features
     * like benchmarking or instrumentation for performance analysis.
     */
    static class QuickSortThreeWayByFunction extends QuickSort_3way<String> {

        /**
         * Constructs a QuickSortThreeWayByFunction object, which is a specialized implementation
         * of a three-way QuickSort algorithm for sorting arrays of Strings.
         * <p>
         * This constructor accepts a Helper object to manage auxiliary operations
         * like comparisons, instrumentation, and performance analysis during sorting.
         *
         * @param helper a Helper object that provides support for operations such as comparisons
         *               and instrumentation during the sorting process.
         */
        public QuickSortThreeWayByFunction(Helper<String> helper) {
            super(helper);
        }
    }

    /**
     * Closes the MSDStringSort instance, ensuring that any associated resources are properly released.
     * <p>
     * If the helper is available (as indicated by the closeHelper field), its {@code close} method is called to release
     * any resources held by the helper. Then, the superclass's {@code close} method is invoked to perform
     * additional cleanup, which may include releasing memory or logging relevant information about the object.
     * <p>
     * This method is designed to ensure all resources used by the object, including helper instances and other resources
     * managed by the parent class, are safely and effectively released.
     */
    @Override
    public void close() {
        if (closeHelper) helper.close();
        super.close();
    }
}