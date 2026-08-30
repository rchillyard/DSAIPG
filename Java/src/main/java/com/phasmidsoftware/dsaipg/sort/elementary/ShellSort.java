/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparableHelper;
import com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparatorHelper;
import com.phasmidsoftware.dsaipg.sort.helper.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.benchmark.Benchmark;
import com.phasmidsoftware.dsaipg.util.benchmark.Benchmark_Timer;
import com.phasmidsoftware.dsaipg.util.benchmark.Stopwatch;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.config.Config_Benchmark;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.phasmidsoftware.dsaipg.util.benchmark.SortBenchmark.BENCHMARKSTRINGSORTERS;

/**
 * Implementation of ShellSort, a generalization of insertion sort which allows
 * the exchange of items that are far apart, defined by "gap" sequences.
 * This implementation of ShellSort uses various gap sequences, selectable by
 * specified modes. It provides versatility and performance improvements for
 * diverse data distributions.
 *
 * NOTE that when invoking ShellSort with mode 1 (should be identical to InsertionSort),
 * we actually do fractionally more hits, but slightly fewer comparisons, than InsertionSort.
 * See the method doInstrumentedHSort for a slightly better explanation.
 *
 * @param <X> the type parameter which extends Comparable, allowing comparison
 *            of elements.
 */
public class ShellSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    /**
     * Method to sort a sub-array of an array of Xs.
     * <p>
     * XXX check that the treatment of from and to is correct. It seems to be according to the unit tests.
     *
     * @param xs an array of Xs to be sorted in place.
     */
    public void sort(X[] xs, int from, int to) {
        H h = new H(to - from, m);
        int gap = h.first();
//        try (Stopwatch stopwatch = new Stopwatch("microseconds")) {
        while (gap > 0) {
            hSort(gap, xs, from, to);
//            logger.debug("hSort time: "+stopwatch.lap()+" microseconds");
            if (shellFunction != null)
                shellFunction.apply(getHelper(), xs);
            gap = h.next();
        }
//        }
    }

    /**
     * Set the "shell" function which is invoked on the helper after each shell (i.e., each value of h).
     * Yes, I do realize that shell was the name of the inventor, Donald Shell.
     * But it's also a convenient name of a (set of) h-sorts which one particular h-value.
     *
     * @param shellFunction a consumer of Helper of X.
     */
    public void setShellFunction(BiFunction<Helper<X>, X[], Void> shellFunction) {
        this.shellFunction = shellFunction;
    }

    /**
     * Primary constructor for ShellSort with mode, size, runs, and configuration.
     *
     * @param m      the mode, that is to say the "gap" (h) sequence to follow:
     *               1: ordinary insertion sort;
     *               2: use powers of two less one;
     *               3: use the sequence based on 3 (the one in the book): 1, 4, 13, etc.
     *               4: Sedgewick's sequence.
     *               5: Pratt Sequence 2^i*3^j with i, j >= 0.
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs to be expected.
     * @param config the configuration.
     */
    public ShellSort(int m, int N, int nRuns, Config config) {
        super(DESCRIPTION + m, N, nRuns, config);
        this.m = m;
        trackInversions = config.getBoolean(BENCHMARKSTRINGSORTERS, "trackinversions");
    }

    /**
     * Secondary constructor for ShellSort using the Sedgewick sequence.
     */
    public ShellSort() throws IOException {
        this(4);
    }

    /**
     * Secondary constructor for ShellSort using the Pratt sequence and the standard configuration-based helper.
     *
     * @param m the mode, which is to say the "gap" (h) sequence to follow (see other constructors)
     */
    public ShellSort(int m) throws IOException {
        this(m, new InstrumentedComparableHelper<>(DESCRIPTION + m, Config.load(ShellSort.class)));
    }

    /**
     * Primary constructor for ShellSort with explicit mode and helper.
     *
     * @param m      the "gap" (h) sequence to follow:
     *               1: ordinary insertion sort;
     *               2: use powers of two less one;
     *               3: use the sequence based on 3 (the one in the book): 1, 4, 13, etc.
     *               4: Sedgewick's sequence (not implemented).
     *               5: Pratt Sequence 2^i*3^j with i, j >= 0.
     * @param helper an explicit instance of Helper to be used.
     */
    public ShellSort(int m, Helper<X> helper) {
        super(helper);
        this.m = m;
        trackInversions = false;
    }

    /**
     * Description string that identifies the mode of the Shell Sort operation being performed.
     * This constant is used as a prefix in logging or message output to indicate
     * the current mode (or gap sequence) used by the algorithm.
     */
    public static final String DESCRIPTION = "Shell sort in mode ";

    /**
     * Private inner class to provide h (gap) values.
     */
    static class H {
        private final int m;
        @SuppressWarnings("CanBeFinal")
        private int h = 1;
        private int i;
        private boolean started = false;
        final List<Integer> data = new ArrayList<>();

        H(int N, int m) {
            this.m = m;
            switch (m) {
                case 1:
                    break;
                case 2:
                    while (h <= N / 2) h = 2 * (h + 1) - 1;
                    break;
                case 3:
                    while (h <= N / 3) h = h * 3 + 1;
                    break;
                case 4:
                    i = 0;
                    while (sedgewick(i) < N) i++;
                    i--;
                    h = (int) sedgewick(i); // Note there will be loss of precision for large i
                    break;
                case 5:
                    //2^i*3^j with i, j >= 0
                    int i;
                    int j = 1;
                    while (j <= N) {
                        i = j;
                        while (i <= N) {
                            data.add(i);
                            i = i * 2;
                        }
                        j = j * 3;
                    }
                    // TODO this doesn't calculate hits, etc. correctly.
                    Collections.sort(data);
                    this.i = data.size() - 1;
                    h = data.get(this.i);
                    break;

                default:
                    throw new RuntimeException("invalid m value: " + m);
            }
        }

        /**
         * Method to yield the first h value.
         * NOTE: this may only be called once.
         *
         * @return the first (largest) value of h, given the size of the problem (N)
         */
        int first() {
            if (started) throw new RuntimeException("cannot call first more than once");
            started = true;
            return h;
        }

        /**
         * Method to yield the next h value in the "gap" series.
         * NOTE: first must be called before next.
         *
         * @return the next value of h in the gap series.
         */
        int next() {
            if (started) {
                switch (m) {
                    case 1 -> {
                        return 0;
                    }
                    case 2 -> {
                        h = (h + 1) / 2 - 1;
                        return h;
                    }
                    case 3 -> {
                        h = h / 3;
                        return h;
                    }
                    case 4 -> {
                        i--;
                        return (int) sedgewick(i);
                    }
                    case 5 -> {
                        i--;
                        if (i < 0) return 0;
                        return data.get(i);
                    }
                    default -> throw new RuntimeException("invalid m value: " + m);
                }
            } else {
                started = true;
                return h;
            }
        }

        /**
         * Computes the k-th value in the Sedgewick gap sequence for shell sort.
         * The Sedgewick sequence is defined as:
         * - When k is even: 9 * (2^k - 2^(k/2)) + 1
         * - When k is odd:  8 * 2^k - 6 * 2^((k+1)/2) + 1
         *
         * @param k the index of the desired gap value. It must be a non-negative integer.
         * @return the k-th Sedgewick sequence value. For k < 0, the method returns 0.
         */
        long sedgewick(int k) {
            if (k < 0) return 0;
            if (k % 2 == 0) return 9L * (powerOf2(k) - powerOf2(k / 2)) + 1;
            else return 8L * powerOf2(k) - 6 * powerOf2((k + 1) / 2) + 1;
        }

        /**
         * Computes the value of 2 raised to the power of the given exponent.
         *
         * @param k the exponent. It must be a non-negative integer.
         * @return the computed value of 2^k as a long.
         */
        private long powerOf2(int k) {
            long value = 1;
            for (int i = 0; i < k; i++) value *= 2;
            return value;
        }
    }

    /**
     * The main method demonstrates the performance evaluation of a Shell Sort algorithm
     * for various array sizes using an instrumented helper and benchmark timer.
     * It iteratively doubles the array size starting from 64000 up to a maximum of 100000.
     * The method collects and displays statistics including the number of comparisons,
     * swaps, hits, and the execution time for sorting the arrays.
     *
     * @param args command-line arguments passed to the program, not used in this implementation.
     */
    public static void main(String[] args) {
        int N = 64000;

        while (N <= 100000) {
            int nRuns = 20;
            Config config = Config_Benchmark.setupConfig("true", "true", "0", "0", "", "true");
            InstrumentedComparatorHelper<Integer> instrumentedHelper = new InstrumentedComparableHelper<>("ShellSort", N, nRuns, config);
            ShellSort<Integer> s = new ShellSort<>(4, instrumentedHelper);
            int j = N;
            s.init(j);
            Integer[] xs = instrumentedHelper.random(Integer.class, r -> r.nextInt(j));
            Benchmark<Boolean> benchmark = new Benchmark_Timer<>("Sorting", config, b -> s.sort(xs, 0, j));
            double nTime = benchmark.run(true, nRuns);
            long nCompares = instrumentedHelper.getCompares();
            long nSwaps = instrumentedHelper.getSwaps();
            long nHits = instrumentedHelper.getHits();

            System.out.println("When array size is: " + j);
            System.out.println("Compares: " + nCompares);
            System.out.println("Swaps: " + nSwaps);
            System.out.println("Hits: " + nHits);
            System.out.println("Time: " + nTime);

            N = N * 2;
        }
    }

    /**
     * Executes a shell sort algorithm on the given array using the specified gap sequence mode and helper.
     *
     * @param m      the mode defining the gap sequence to follow for sorting:
     *               1: ordinary insertion sort;
     *               2: use powers of two less one;
     *               3: use the sequence based on 3 (e.g., 1, 4, 13, etc.);
     *               4: Sedgewick's sequence;
     *               5: Pratt's sequence (2^i * 3^j, where i, j >= 0).
     * @param helper a helper instance to assist with sorting actions and operations.
     * @param xs     the array of elements to be sorted in place.
     * @return true if the operation was successful.
     */
    static <T extends Comparable<T>> boolean doShellSort(int m, Helper<T> helper, final T[] xs) {
        SortWithComparableHelper<T> shellSort = new ShellSort<>(m, helper);
        shellSort.mutatingSort(xs);
//        return helper.findInversion(xs) == -1;
        return true;
    }

    /**
     * Method to perform an (instrumented) shell sort on random data.
     *
     * @param m      the mode (gap sequence).
     * @param n      the size of the array to be sorted.
     * @param r      the number of repetitions.
     * @param config the configuration.
     * @return true if everything went according to plan.
     */
    static boolean doRandomDoubleShellSort(int m, int n, int r, final Config config) {
        boolean instrumented = config.getBoolean(Config_Benchmark.HELPER, "instrument");
        Helper<Double> helper = instrumented ? new InstrumentedComparableHelper<>("ShellSort mode: " + m + " with instrumentation", n, r, config) : new NonInstrumentingComparableHelper<>("ShellSort mode: " + m, n, config);
        boolean result = true;
        for (int i = 0; i < r; i++) {
            Double[] xs = helper.random(Double.class, Random::nextDouble);
            result = result && doShellSort(m, helper, xs);
            helper.postProcess(xs);
        }
        if (helper.instrumented()) logger.info(helper.showStats());
        return result;
    }

    /**
     * Private method to h-sort an array.
     *
     * @param h    the stride (gap) of the h-sort.
     * @param xs   the array to be sorted.
     * @param from the first index to be considered in array xs.
     * @param to   one plus the last index to be considered in array xs.
     */
    private void hSort(int h, X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        if (helper.instrumented())
            doInstrumentedHSort(h, xs, from, to, helper);
        else
            // XXX Straightforward hSort logic for when not instrumented
            for (int i = h + from; i < to; i++) {
                int j = i;
                while (j >= h + from && helper.swapConditional(xs, j - h, j)) j -= h;
            }
    }

    /**
     * The mode defining the "gap" (h) sequence used for Shell Sort.
     * <p>
     * This variable determines the strategy used to define the gap sequence for sorting:
     * - 1: Ordinary insertion sort.
     * - 2: Gap sequence as powers of two minus one.
     * - 3: Gap sequence based on the series {1, 4, 13, ...}.
     * - 4: Sedgewick's sequence.
     * - 5: Pratt's sequence, defined as 2^i * 3^j, where i, j >= 0.
     * <p>
     * This variable is final and is set during the initialization of the ShellSort class.
     */
    private final int m;
    /**
     * A boolean flag indicating whether inversions should be tracked during the sorting process.
     * When enabled, the algorithm will monitor and potentially log the number and positions
     * of inversions in the data being sorted. This can be useful for debugging or analyzing
     * the efficiency of the sorting algorithm.
     */
    private final boolean trackInversions;
    /**
     * A consumer function used in the context of the Shell Sort implementation.
     * This function can be set or modified to perform specific actions
     * during the execution of the sorting algorithm, particularly after each "shell"
     * (i.e., each h-step) is processed. It allows the execution of custom operations,
     * such as resource management or instrumentation tasks, by consuming an
     * instance of {@link AutoCloseable}.
     * <p>
     * Initially, this variable is null. Its purpose can be defined or overridden
     * using the setShellFunction method provided in the ShellSort class.
     */
    private BiFunction<Helper<X>, X[], Void> shellFunction = null;

    /**
     * Performs an instrumented h-sort on a segment of an array using the specified helper.
     * The method tracks inversions if enabled and provides debug logging for before and after state changes.
     * <p>
     * NOTE that when mode is 1, this results in slightly fewer comparison but slightly more hits
     * than Insertion sort.
     * The exact causes remain a mystery as of now.
     *
     * @param h      the stride (gap) used for h-sorting.
     * @param xs     the array of elements to be sorted.
     * @param from   the starting index (inclusive) of the sub-array to be sorted.
     * @param to     the ending index (exclusive) of the sub-array to be sorted.
     * @param helper an instance of the Helper interface, providing utilities such as comparisons and inversion tracking.
     */
    private void doInstrumentedHSort(int h, X[] xs, int from, int to, Helper<X> helper) {
        long inversionsStart = 0;
        if (trackInversions) {
            inversionsStart = helper.inversions(xs);
            logger.debug("hSort (begin) with h=" + h + ", current inversionsStart=" + inversionsStart);
        }
        // XXX hSort logic that does not over-count hits, etc.
        for (int k = 0; k < h; k++) {
            X a = helper.get(xs, from + k);
            for (int i = from + h + k; i < to; i += h)
                a = doInsert(xs, h, from, i, a, helper.get(xs, i), helper);
        }
        if (trackInversions) {
            long inversionsEnd = helper.inversions(xs);
            long fixed = inversionsStart - inversionsEnd;
            int proportionFixed = (int) (100.0 * fixed / inversionsStart);
            logger.debug("hSort (end) with h=" + h + ", inversions fixed=" + fixed + " (" + proportionFixed + "%)");
        }
    }

    /**
     * Performs a modified insertion process within a h-sorted sub-array during the Shell Sort algorithm.
     *
     * @param xs      the array of elements to be sorted.
     * @param h       the current gap (stride) used in the Shell Sort process.
     * @param from    the starting index of the sub-array being processed.
     * @param i       the current index in the sub-array where the insertion is performed.
     * @param a       an element of type X currently being compared and/or swapped.
     * @param b       another element of type X being swapped into position.
     * @param helper  an instance of the Helper<X> interface used to perform comparisons and swaps.
     * @param <X>     the type parameter extending Comparable, representing the elements in the array.
     * @return the element of type X that will replace `a` after this process is complete.
     */
    private static <X extends Comparable<X>> X doInsert(X[] xs, int h, int from, int i, X a, X b, Helper<X> helper) {
        X aNext = b;
        int j = i;
        while (true) {
            boolean swapped = helper.swapConditional(xs, a, j - h, j, b);
            if (!swapped) break;
            if (aNext == b) aNext = a;
            j -= h;
            if (j - h < from) break;
            a = helper.get(xs, j - h);
        }
        return aNext;
    }

    /**
     * Displays the results of performing a Shell Sort on random double arrays.
     * The Shell Sort mode and array size are specified as parameters, and the method calculates
     * the elapsed time for the sorting operation over multiple repetitions.
     *
     * @param m      the mode defining the gap sequence of the Shell Sort:
     *               1: ordinary insertion sort;
     *               2: powers of two less one;
     *               3: the sequence based on 3 (e.g., 1, 4, 13, etc.);
     *               4: Sedgewick's sequence;
     *               5: Pratt's sequence (2^i * 3^j, where i, j >= 0).
     * @param n      the size of the array to be generated and sorted.
     * @param config the configuration object used to manage resources and settings for sorting.
     */
    private static void showRandomDoubleShellSortResult(int m, int n, final Config config) {
        try (Stopwatch stopwatch = new Stopwatch()) {
            int repetitions = 100;
            boolean sorted = doRandomDoubleShellSort(m, n, repetitions, config);
            long millis = stopwatch.lap();
            if (sorted)
                logger.info("Shell Sorted with mode " + m + " and n = " + n + " (millisecs elapsed: " + millis * 1.0 / repetitions + ")");
            else throw new SortException("not sorted");
        }
    }

    final private static LazyLogger logger = new LazyLogger(ShellSort.class);
}