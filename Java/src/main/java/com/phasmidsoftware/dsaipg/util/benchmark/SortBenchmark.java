/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package com.phasmidsoftware.dsaipg.util.benchmark;

import com.phasmidsoftware.dsaipg.sort.classic.BucketSort;
import com.phasmidsoftware.dsaipg.sort.counting.LSDStringSort;
import com.phasmidsoftware.dsaipg.sort.counting.MSDStringSort;
import com.phasmidsoftware.dsaipg.sort.elementary.*;
import com.phasmidsoftware.dsaipg.sort.generic.Sort;
import com.phasmidsoftware.dsaipg.sort.generic.SortException;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.sort.linearithmic.TimSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.*;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.general.CodePointMapper;
import com.phasmidsoftware.dsaipg.util.general.Utilities;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparatorHelper.AT;
import static com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort.MERGESORT;
import static com.phasmidsoftware.dsaipg.util.benchmark.SortBenchmarkHelper.*;
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.isInstrumented;
import static com.phasmidsoftware.dsaipg.util.general.Utilities.formatWhole;

/**
 * <p>This class runs a suite of sorting benchmarks.
 * </p>
 * In order to make it work you need to do two things:
 * <ol><li>Edit config.ini</li>
 * <li>Provide command line arguments to specify the problem sizes that you want</li></ol>
 * <p>Note that each benchmark smaller than 512,000 is designed to run in approximately 10 seconds.
 * When the size equals or exceeds 512,000, that period of time will be roughly proportional to the size.</p>
 */
public class SortBenchmark {

    /**
     * The main method serves as the entry point for the SortBenchmark application.
     * It loads the configuration for the benchmark, logs the starting details, and
     * invokes the {@code doMain} method to execute the benchmarks.
     *
     * @param args The command-line arguments, representing word counts
     *             to be processed.
     *             If no word counts are provided, a warning is logged.
     * @throws IOException If an IO error occurs during loading configuration or execution.
     */
    public static void main(String[] args) throws IOException {
        Config config = Config.load(SortBenchmark.class);
        logger.info("!!!!!!!!!!!!!!!!!!!! SortBenchmark Start !!!!!!!!!!!!!!!!!!!!\n");
        logger.info("SortBenchmark.main: version " + config.get("sortbenchmark", "version") + " with word counts: " + Arrays.toString(args));
        if (args.length == 0) logger.warn("No word counts specified on the command line");
        new SortBenchmark(config).doMain(args);
    }

    /**
     * Extracts words from the given input line based on the Leipzig regex pattern: {@link SortBenchmarkHelper#regexLeipzig}.
     *
     * @param line the input string from which words are to be extracted.
     * @return a collection of strings containing the extracted words.
     */
    public static Collection<String> getLeipzigWords(String line) {
        return getWords(regexLeipzig, line);
    }

    /**
     * Method to run a sorting benchmark, using an explicit preProcessor.
     *
     * @param words        an array of available words (to be chosen randomly).
     * @param nWords       the number of words to be sorted.
     * @param nRuns        the number of runs of the sort to be performed.
     * @param sorter       the sorter to use--NOTE that this sorter will be closed at the end of this method.
     * @param preProcessor the pre-processor function, if any.
     * @param timeLoggers  a set of timeLoggers to be used.
     */
    static void runStringSortBenchmark(String[] words, int nWords, int nRuns, SortWithHelper<String> sorter, UnaryOperator<String[]> preProcessor, TimeLogger[] timeLoggers) {
        logger.info("****************************** String sort: " + nRuns + " runs of " + nWords + " " + sorter.getDescription() + " ******************************");
        new SorterBenchmark<>(String.class, preProcessor, sorter, words, nRuns, timeLoggers).run(getDescription(nWords, sorter), nWords);
        sorter.close();
    }

    /**
     * Method to apply integer sorting operations on a stream of integer sizes.
     * For each size in the stream, the sorting operations are executed.
     *
     * @param wordCounts a stream of integer values representing the sizes of the integer datasets to be sorted
     */
    void sortIntegers(Stream<Long> wordCounts) {
        wordCounts.forEach(this::runIntegerSorts);
    }

    /**
     * Executes sorting operations on integer datasets using multiple sorting algorithms
     * (Shell Sort, Bucket Sort, Quick Sort) based on the configuration settings.
     * This method includes validations and performs the sort processes with the
     * appropriate number of runs and work estimations.
     *
     * @param N the size of the dataset to be sorted, must not exceed {@link Integer#MAX_VALUE}.
     *          Throws a {@link SortException} if the size exceeds this limit.
     */
    void runIntegerSorts(long N) {
        if (N > Integer.MAX_VALUE) throw new SortException("number of elements is too large");
        double totalWork = getTotalWork(N, config, "benchmarkintegersorters");
        if (isConfigBenchmarkIntegerSorter("shellsort"))
            sortIntegersByShellSort((int) N, 12 * estimateRuns(totalWork, Math.pow(N, 4.0 / 3)));
        if (isConfigBenchmarkIntegerSorter("bucketsort"))
            runIntegerBucketSort((int) N, estimateRuns(totalWork * 2, N));
        if (isConfigBenchmarkIntegerSorter("quicksort"))
            runIntegerQuickSort((int) N, 10 * estimateRuns(totalWork, Math.log(N) * N));
    }

    /**
     * Method to benchmark local date time sorts.
     * <p>
     * TESTME
     *
     * @param n      the number of dateTimes.
     * @param config the configuration.
     * @throws IOException if there's an exception.
     */
    void sortLocalDateTimes(final int n, Config config) throws IOException {
        logger.info("Beginning LocalDateTime sorts");
        // CONSIDER why do we have localDateTimeSupplier IN ADDITION TO localDateTimes?
        Supplier<LocalDateTime[]> localDateTimeSupplier = () -> generateRandomLocalDateTimeArray(n);
        Helper<ChronoLocalDateTime<?>> helper = new NonInstrumentingComparableHelper<>("DateTimeHelper", config);
        final LocalDateTime[] localDateTimes = generateRandomLocalDateTimeArray(n);

        // CONSIDER finding the common ground amongst these sorts and get them all working together.

        // NOTE Test on date using pure tim sort.
        if (isConfigBenchmarkDateSorter("timsort"))
            logger.info(benchmarkFactory("ProcessingSort LocalDateTimes using Arrays::sort (TimSort)", Arrays::sort, null).runFromSupplier(localDateTimeSupplier, 100) + "ms");

        // NOTE this is supposed to match the previous benchmark run exactly. I don't understand why it takes rather less time.
        if (isConfigBenchmarkDateSorter("timsort")) {
            logger.info(benchmarkFactory("Repeat ProcessingSort LocalDateTimes using timSort::mutatingSort", new TimSort<>(helper)::mutatingSort, null).runFromSupplier(localDateTimeSupplier, 100) + "ms");
            // NOTE this is intended to replace the run two lines previous. It should take the exact same amount of time.
            runDateTimeSortBenchmark(LocalDateTime.class, localDateTimes, n, 100);
        }
    }

    /**
     * Method to run string sorter benchmarks.
     * <p>
     * NOTE: this is package-private because it is used by unit tests.
     *  @param words     the word source.
     *
     * @param nWords the number of words to be sorted.
     */
    void benchmarkStringSorters(String[] words, int nWords) {
        double totalWork = getTotalWork(nWords, config, BENCHMARKSTRINGSORTERS);
        logger.info("benchmarkStringSorters: sorting " + formatWhole(nWords) + " words" + (isInstrumented(config) ? " and instrumented" : "") + " with total work (for estimating runs): " + totalWork);
        if (isInstrumented(config))
            logger.info("    normalization of statistics is based on n ln n");
        Random random = new Random();
        int nRunsLinearithmic = estimateRuns(totalWork, minComparisons(nWords));
        int nRunsLinear = estimateRuns(totalWork, 15.0 * nWords);
        int nRunsBucket = estimateRuns(totalWork, 2.0 * nWords + 0.5 * nWords * nWords / BucketSort.DIGRAPHS_SIZE);

        // System sort
        if (isConfigBenchmarkStringSorter("puresystemsort") && nRunsLinearithmic > 0)
            runPureSystemSortBenchmark(words, nWords, nRunsLinearithmic, random);

        // Linear sorts
        if (isConfigBenchmarkStringSorter("bucketsort") && nRunsBucket > 0)
            try (SortWithHelper<String> sorter = BucketSort.CaseIndependentBucketSort(BucketSort::classifyStringDigraph, BucketSort.DIGRAPHS_SIZE, nWords, config)) {
                runStringSortBenchmark(words, nWords, nRunsBucket, sorter, timeLoggersLinear);
            }

        if (isConfigBenchmarkStringSorter("LSD") && nRunsLinear > 0) {
            int nRuns = nRunsLinear * 5;
            try (SortWithHelper<String> sorter = new LSDStringSort(nWords, 20, String::compareTo, nRuns, config)) {
                runStringSortBenchmark(words, nWords, nRuns, sorter, timeLoggersLinear);
            }
        }

        if (isConfigBenchmarkStringSorter("MSD") && nRunsLinear > 0) {
            int nRuns = nRunsLinear * 5;
            try (SortWithHelper<String> sorter = new MSDStringSort(CodePointMapper.ASCIIExt, nWords, nRuns, config)) {
                runStringSortBenchmark(words, nWords, nRuns, sorter, timeLoggersLinear);
            }
//            try (SortWithHelper<String> sorter = new MSDStringSort(CodePointMapper.English, nWords, config)) {
//                runStringSortBenchmark(words, nWords, nRunsLinear * 10, sorter, timeLoggersLinear);
//            }
        }

        // Linearithmic sorts
        if (isConfigBenchmarkStringSorter("timsort") && nRunsLinearithmic > 0)
            try (SortWithHelper<String> sorter = TimSort.CaseInsensitiveSort(nWords, config)) {
                runStringSortBenchmark(words, nWords, nRunsLinearithmic * 2, sorter, timeLoggersLinearithmic);
            }

        if (isConfigBenchmarkStringSorter(MERGESORT))
            runMergeSortBenchmark(words, nWords, nRunsLinearithmic * 4, config);

        if (isConfigBenchmarkStringSorter("quicksort3way") && nRunsLinearithmic > 0)
            try (SortWithHelper<String> sorter = new QuickSort_3way<>(nWords, nRunsLinearithmic, config)) {
                runStringSortBenchmark(words, nWords, nRunsLinearithmic * 3, sorter, timeLoggersLinearithmic);
            }

        if (isConfigBenchmarkStringSorter("quicksortDualPivot") && nRunsLinearithmic > 0)
            try (SortWithHelper<String> sorter = new QuickSort_DualPivot<>(nWords, nRunsLinearithmic, config)) {
                runStringSortBenchmark(words, nWords, nRunsLinearithmic * 4, sorter, timeLoggersLinearithmic);
            }

        if (isConfigBenchmarkStringSorter("quicksort") && nRunsLinearithmic > 0)
            try (SortWithHelper<String> sorter = new QuickSort_Basic<>(nWords, nRunsLinearithmic, config)) {
                runStringSortBenchmark(words, nWords, nRunsLinearithmic * 3, sorter, timeLoggersLinearithmic);
            }

        if (isConfigBenchmarkStringSorter("heapsort") && nRunsLinearithmic > 0) {
            try (SortWithHelper<String> sorter = new HeapSort<>(nWords, nRunsLinearithmic, config)) {
                runStringSortBenchmark(words, nWords, nRunsLinearithmic * 3, sorter, timeLoggersLinearithmic);
            }
        }

        if (isConfigBenchmarkStringSorter("introsort") && nRunsLinearithmic > 0)
            try (SortWithHelper<String> sorter = new IntroSort<>(nWords, nRunsLinearithmic, config)) {
                runStringSortBenchmark(words, nWords, nRunsLinearithmic * 3, sorter, timeLoggersLinearithmic);
            }

        if (isConfigBenchmarkStringSorter("randomsort") && nRunsLinearithmic > 0)
            try (SortWithHelper<String> sorter = new RandomSort<>(nWords, config)) {
                runStringSortBenchmark(words, nWords, nRunsLinearithmic, sorter, timeLoggersLinearithmic);
            }

        if (isConfigBenchmarkStringSorter("shellsort")) {
            int nRunsSubQuadratic = estimateRuns(totalWork, Math.pow(nWords, 4.0 / 3) / 2);
            if (nRunsSubQuadratic > 0)
                try (SortWithHelper<String> sorter = new ShellSort<>(4, nWords, nRunsSubQuadratic, config)) {
                    runStringSortBenchmark(words, nWords, nRunsSubQuadratic, sorter, timeLoggersSubQuadratic);
                }
        }

        // Quadratic sorts.
        if (isConfigBenchmarkStringSorter("insertionsort") || isConfigBenchmarkStringSorter("insertionsortopt") || isConfigBenchmarkStringSorter("bubblesort")) {
            double inversions = meanInversions(nWords);
            int nRunsQuadraticQuick = estimateRuns(totalWork * 15, inversions);
            int nRunsQuadraticSlow = estimateRuns(totalWork * 4, inversions);

            if (isConfigBenchmarkStringSorter("insertionsortopt") && nRunsQuadraticQuick > 0)
                try (SortWithHelper<String> sorter = new InsertionSortOpt<>(InsertionSortOpt.DESCRIPTION, nWords, nRunsQuadraticQuick, config)) {
                    runStringSortBenchmark(words, nWords, nRunsQuadraticQuick, sorter, timeLoggersQuadratic);
                }

            if (isConfigBenchmarkStringSorter("insertionsort") && nRunsQuadraticQuick > 0)
                try (SortWithHelper<String> sorter = new InsertionSort<>(InsertionSort.DESCRIPTION, nWords, nRunsQuadraticQuick, config)) {
                    runStringSortBenchmark(words, nWords, nRunsQuadraticQuick, sorter, timeLoggersQuadratic);
                }

            if (isConfigBenchmarkStringSorter("bubblesort") && nRunsQuadraticSlow > 0)
                try (SortWithHelper<String> sorter = new BubbleSort<>(nWords, nRunsQuadraticSlow, config)) {
                    runStringSortBenchmark(words, nWords, nRunsQuadraticSlow, sorter, timeLoggersQuadratic);
                }
        }
    }

    /**
     * Method to allow unit testing of the main program.
     *
     * @param args the command-line arguments.
     */
    void doMain(String[] args) {
        sortStrings(getWordCounts(args));
        sortIntegers(getWordCounts(args));
    }

    /**
     * Constructor for the SortBenchmark class.
     * Initializes the SortBenchmark with the given configuration.
     *
     * @param config the configuration object to set up the benchmark environment
     */
    public SortBenchmark(Config config) {
        this.config = config;
    }

    /**
     * Estimate an appropriate amount of total work for the given problem size.
     *
     * @param n             problem size.
     * @param config        the configuration.
     * @param configSection the appropriate config section.
     * @return same as configured totalComparisons, unless n >= 32,000.
     */
    private static double getTotalWork(long n, Config config, final String configSection) {
        long z = config.getLong(configSection, "totalwork", 100_000_000L);
        long x = n / 512_000 + 1; // NOTE this is integer division
        return (double) z * x;
    }

    /**
     * Estimates the number of runs needed, based on the total work and work per run.
     * The method calculates the approximate number of runs required to complete
     * a given workload, ensuring it is within a valid range.
     * If the calculated number of runs is either non-positive or exceeds the permissible limit,
     * an exception is thrown.
     *
     * @param totalWork  the total amount of work to be done.
     * @param workPerRun the amount of work that can be completed in a single run.
     * @return the estimated number of runs as an integer.
     * @throws SortException    if the estimated number of runs exceeds 10 million.
     * @throws RuntimeException if the estimated number of runs is not a positive integer.
     */
    private int estimateRuns(double totalWork, double workPerRun) {
        long result = Utilities.round(totalWork / workPerRun);
        if (result >= 0 && result < Integer.MAX_VALUE)
            if (result < 10_000_000)
                return (int) result;
            else
                throw new SortException("estimated number of runs is too large (max is 10 million): " + result + ". Reduce the value of totalwork accordingly");
        else
            throw new RuntimeException("estimated number of runs is not a positive Integer: " + result);
    }

    /**
     * Executes a benchmarking routine for sorting an integer dataset using the BucketSort algorithm.
     * This method initializes a {@link BucketSort} sorter,
     * prepares the dataset, and benchmarks the sorting for the specified number of runs.
     *
     * @param N    the size of the dataset to be sorted; must be a positive integer.
     * @param runs the number of times the sorting operation will be executed for benchmarking purposes.
     */
    private void runIntegerBucketSort(int N, final int runs) {
        int bucketSize = config.getInt(BENCHMARKINTEGERSORTERS, "bucketsize", 16);
        int buckets = (N + bucketSize - 1) / bucketSize;
        BucketSort<Integer> sorter = new BucketSort<>(null, buckets, N, config);
        Helper<Integer> helper = sorter.getHelper();
        helper.init(N);
        Integer[] xs = helper.random(N, Integer.class, r -> r.nextInt(1000));
        runIntegerSortBenchmark(xs, N, runs, sorter, null, timeLoggersLinearithmic);
        helper.close();
    }

    /**
     * Runs a benchmark for the pure System sort method (using {@code Arrays.sort}).
     * This method measures the sorting performance of an array of strings over multiple runs.
     *
     * @param words  The initial array of words to be used for sorting.
     * @param nWords The number of words to be sorted from the given array.
     * @param nRuns  The number of times the benchmark should be executed.
     * @param random A {@code Random} instance used for shuffling words or other random operations before sorting.
     */
    private static void runPureSystemSortBenchmark(String[] words, int nWords, int nRuns, Random random) {
        Benchmark<String[]> benchmark = new Benchmark_Timer<>("SystemSort", null, Arrays::sort, null);
        doPureBenchmark(words, nWords, nRuns, random, benchmark);
    }

    /**
     * Sorts an array of integers using the Shell Sort algorithm based on the specified configuration.
     *
     * @param N    the size of the integer dataset to be sorted.
     * @param runs the number of times the sorting operation is executed for benchmarking.
     */
    private void sortIntegersByShellSort(int N, int runs) {
        int m = config.getInt(BENCHMARKINTEGERSORTERS, "mode", 4);
        SortWithHelper<Integer> sorter = new ShellSort<>(m, N, runs, config);
        Integer[] numbers = sorter.getHelper().random(Integer.class, Random::nextInt);
        runIntegerSortBenchmark(numbers, N, runs, sorter, sorter::preProcess, timeLoggersSubQuadratic);
    }

    /**
     * Executes a benchmark for sorting an array of integers using QuickSort with dual pivot.
     * The method creates a sorter instance, generates a random integer array, and then performs
     * the sorting benchmarks while logging performance metrics.
     *
     * @param N    the size of the dataset to be sorted, representing the number of integers in the array.
     * @param runs the number of sorting operations to be performed for benchmarking purposes.
     */
    private void runIntegerQuickSort(int N, final int runs) {
        SortWithHelper<Integer> sorter = new QuickSort_DualPivot<>(N, runs, config);
        Integer[] numbers = sorter.getHelper().random(Integer.class, Random::nextInt);
        runIntegerSortBenchmark(numbers, N, runs, sorter, sorter::preProcess, timeLoggersLinearithmic);
    }

    /**
     * Sorts strings based on various benchmark configurations and performs
     * Leipzig benchmarks for English text.
     * This method processes a stream
     * of word counts, where each count is used to dictate specific benchmark
     * operations on string sorting.
     *
     * @param wordCounts a stream of Long values representing the sizes
     *                   of word datasets to be processed and benchmarked.
     */
    private void sortStrings(Stream<Long> wordCounts) {
        logger.info("Beginning String sorts");

        // NOTE: common words benchmark
//        benchmarkStringSorters(getWords("3000-common-words.txt", SortBenchmark::lineAsList), config.getInt("benchmarkstringsorters", "words", 1000), config.getInt("benchmarkstringsorters", "runs", 1000));

        // NOTE: Leipzig English words benchmarks (according to command-line arguments)
        wordCounts.forEach(this::doLeipzigBenchmarkEnglish);

        // NOTE: Leipzig Chinese words benchmarks (according to command-line arguments)
//        doLeipzigBenchmark("zho-simp-tw_web_2014_10K-sentences.txt", 5000, 1000);
    }

    /**
     * Performs a benchmark test to evaluate string sorting algorithms using the Leipzig English dataset.
     * The size of the dataset is determined by the input parameter.
     *
     * @param N the number of elements to be used in the benchmark.
     *          Must not exceed {@code Integer.MAX_VALUE}.
     *          If the value is too large, a {@code SortException} will be thrown.
     */
    private void doLeipzigBenchmarkEnglish(long N) {
        if (N > Integer.MAX_VALUE) throw new SortException("number of elements is too large");
        int x = (int) N;
        logger.info("############################### " + x + " words ###############################");
//        String resource = "eng-uk_web_2002_" + (x < 50000 ? "10K" : x < 200000 ? "100K" : "1M") + "-sentences.txt";
        String resource = "eng-uk_web_2002_" + (x < 50000 ? "10K" : "100K") + "-sentences.txt";
        try {
            benchmarkStringSorters(getWords(resource, SortBenchmark::getLeipzigWords), x);
        } catch (FileNotFoundException e) {
            logger.warn("Unable to find resource: " + resource + "because:", e); // TESTME
        } catch (Exception e) {
            logger.warn("Unable to run benchmark with N: " + N + "because:", e); // TESTME
        }
    }

    /**
     * Method to run a sorting benchmark using the standard preProcess method of the sorter.
     *
     * @param words       an array of available words (to be chosen randomly).
     * @param nWords      the number of words to be sorted.
     * @param nRuns       the number of runs of the sort to be performed.
     * @param sorter      the sorter to use--NOTE that this sorter will be closed at the end of this method.
     * @param timeLoggers a set of timeLoggers to be used.
     *                    <p>
     *                                                                             NOTE: this method is public because it is referenced in a unit test of a different package
     */
    public static void runStringSortBenchmark(String[] words, int nWords, int nRuns, SortWithHelper<String> sorter, TimeLogger[] timeLoggers) {
        sorter.getHelper().init(nWords, nRuns);
        try (Stopwatch stopwatch = new Stopwatch()) {
            runStringSortBenchmark(words, nWords, nRuns, sorter, sorter::preProcess, timeLoggers);
            logger.info("************************************************************ (" + stopwatch.lap() / 1000.0 + " sec.)");
        }
    }

    /**
     * Method to run a sorting benchmark, using an explicit preProcessor.
     *
     * @param numbers      an array of available integers (to be chosen randomly).
     * @param n            the number of integers to be sorted.
     * @param nRuns        the number of runs of the sort to be performed.
     * @param sorter       the sorter to use--NOTE that this sorter will be closed at the end of this method.
     * @param preProcessor the pre-processor function, if any.
     * @param timeLoggers  a set of timeLoggers to be used.
     */
    static void runIntegerSortBenchmark(Integer[] numbers, int n, int nRuns, SortWithHelper<Integer> sorter, UnaryOperator<Integer[]> preProcessor, TimeLogger[] timeLoggers) {
        logger.info("****************************** Integer sort: " + n + " " + sorter.getDescription() + " ******************************");
        try (Stopwatch stopwatch = new Stopwatch()) {
            new SorterBenchmark<>(Integer.class, preProcessor, sorter, numbers, nRuns, timeLoggers).run(getDescription(n, sorter), n);
            sorter.close();
            logger.info("************************************************************ (" + stopwatch.lap() / 1000.0 + " sec.)");
        }
    }

    public static final String BENCHMARKSTRINGSORTERS = "benchmarkstringsorters";
    public static final TimeLogger TIME_LOGGER_RAW = new TimeLogger("Raw time per run {mSec}: ", null);

    /**
     * For mergesort, the number of array accesses is actually four times the number of comparisons (six when nocopy is false).
     * That's because, in addition to each comparison, there will be approximately two copy operations.
     * Thus, in the case where comparisons are based on primitives,
     * the normalized time per run should approximate the time for one array access.
     */
    public final static TimeLogger[] timeLoggersLinearithmic = {
            TIME_LOGGER_RAW,
            new TimeLogger("Normalized time per run {n log n}: ", SortBenchmark::minComparisons)
    };

    /**
     * Linear time loggers.
     */
    public final static TimeLogger[] timeLoggersLinear = {
            TIME_LOGGER_RAW,
            new TimeLogger("Normalized time per run {n}: ", n -> n * 1.0)
    };

    /**
     * Quadratic time loggers.
     */
    final static TimeLogger[] timeLoggersQuadratic = {
            TIME_LOGGER_RAW,
            new TimeLogger("Normalized time per run {n^2}: ", SortBenchmark::meanInversions)
    };

    /**
     * For shellsort.
     */
    final static TimeLogger[] timeLoggersSubQuadratic = {
            TIME_LOGGER_RAW,
            new TimeLogger("Normalized time per run {n^(4/3)}: ", n -> Math.pow(n, 5.0 / 4))
    };


    final static LazyLogger logger = new LazyLogger(SortBenchmark.class);

    /**
     * This is based on log2(n!)
     *
     * @param n the number of elements.
     * @return the minimum number of comparisons possible to sort n randomly ordered elements.
     */
    static double minComparisons(int n) {
        double lgN = Utilities.lg(n);
        return n * (lgN - LgE) + lgN / 2 + 1.33;
    }

    /**
     * This is the mean number of inversions in a randomly ordered set of n elements.
     * For insertion sort, each (low-level) swap fixes one inversion, so on average, this number of swaps is required.
     * The minimum number of comparisons is slightly higher.
     *
     * @param n the number of elements
     * @return one quarter n-squared more or less.
     */
    static double meanInversions(int n) {
        return 0.25 * n * (n - 1);
    }

    /**
     * Used by integration tests (for HuskySort).
     * Really??
     * NOTE: this doesn't seem to do anything useful.
     *
     * @param line a String
     * @return an ArrayList with just the one <code>line</code> in it.
     */
    private static Collection<String> lineAsList(String line) {
        List<String> words = new ArrayList<>();
        words.add(line);
        return words;
    }

    /**
     * Creates a Benchmark instance configured with a description,
     * a sorter, and a checker for an array of LocalDateTime elements.
     * The benchmark will make a copy of the input array before passing it to the provided sorter and checker.
     *
     * @param description A string describing the benchmark task being performed.
     * @param sorter      A Consumer that performs sorting on an array of LocalDateTime elements.
     * @param checker     A Consumer that validates or performs any additional operations on the sorted array.
     * @return A Benchmark instance configured with the provided description, sorter, and checker.
     */
    // CONSIDER: to be eliminated soon.
    private static Benchmark<LocalDateTime[]> benchmarkFactory(String description, Consumer<LocalDateTime[]> sorter, Consumer<LocalDateTime[]> checker) {
        return new Benchmark_Timer<>(
                description,
                (xs) -> Arrays.copyOf(xs, xs.length),
                sorter,
                checker
        );
    }

    /**
     * Executes a pure benchmark test with given parameters.
     *
     * @param words an array of words to randomly use during the benchmark
     * @param nWords the number of words to include in the test
     * @param nRuns the number of iterations to run the benchmark
     * @param random a Random instance to help in generating random data
     * @param benchmark the Benchmark object used to execute the performance test
     */
    private static void doPureBenchmark(String[] words, int nWords, int nRuns, Random random, Benchmark<String[]> benchmark) {
        // CONSIDER we should manage the space returned by fillRandomArray and deallocate it after use.
        final double time = benchmark.runFromSupplier(() -> Utilities.fillRandomArray(String.class, random, nWords, r -> words[r.nextInt(words.length)]), nRuns);
        for (TimeLogger timeLogger : timeLoggersLinearithmic) timeLogger.log("pure benchmark", time, nWords);
    }

    // TODO arrange for this to be resurrected.
//    private void dateSortBenchmark(Supplier<LocalDateTime[]> localDateTimeSupplier, LocalDateTime[] localDateTimes, Sort<ChronoLocalDateTime<?>> dateHuskySortSystemSort, String s, int i) {
//        logger.info(benchmarkFactory(s, dateHuskySortSystemSort::sort, dateHuskySortSystemSort::postProcess).runFromSupplier(localDateTimeSupplier, 100) + "ms");
//        // NOTE: this is intended to replace the run in the previous line. It should take the exact same amount of time.
//        runDateTimeSortBenchmark(LocalDateTime.class, localDateTimes, 100000, 100, i);
//    }

    /**
     * Processes an array of strings and converts them into a stream of long values.
     * Each string is parsed and transformed using the parseInt method of the SortBenchmark class.
     *
     * @param args an array of strings that need to be parsed into long values
     * @return a stream of long values obtained by parsing the input array
     */
    private static Stream<Long> getWordCounts(String[] args) {
        return Arrays.stream(args).map(SortBenchmark::parseInt);
    }

    /**
     * Parses a given string representation of a memory size and converts it into its long value.
     * The method interprets specific letter patterns and replaces them with their numeric multipliers:
     * 'g' or 'G' is replaced with "mk",
     * 'm' or 'M' is replaced with "kk",
     * and 'k' or 'K' is replaced with "*1024".
     * The resulting expression is evaluated based on these replacements.
     *
     * @param w the string representation of the memory size to parse, containing letters like 'g', 'm', or 'k'
     *          to indicate gigabytes, megabytes, or kilobytes respectively
     * @return the calculated long value representing the equivalent memory size
     */
    static long parseInt(String w) {
        long result = 1L;
        String expression = w.replaceAll("[gG]", "mk").replaceAll("[mM]", "kk").replaceAll("[kK]", "*1024");
        for (String split : expression.split("\\*")) result *= Integer.parseInt(split);
        return result;
    }

    /**
     * Runs a benchmark for the MergeSort algorithm using the given parameters.
     *
     * @param words   An array of strings to be sorted.
     * @param nWords  The number of words to be sorted, extracted from the input array.
     * @param nRuns   The number of times the sorting operation should be executed.
     * @param config  The configuration object providing additional settings for the sort and benchmark.
     */
    private void runMergeSortBenchmark(String[] words, int nWords, int nRuns, Config config) {
        try (SortWithComparableHelper<String> sorter = new MergeSort<>(nWords, nRuns, config)) {
            runStringSortBenchmark(words, nWords, nRuns, sorter, timeLoggersLinearithmic);
        }
    }

    /**
     * Constructs a description string by combining the integer value and the description
     * provided by the sorter instance.
     *
     * @param n the integer value to be included in the description
     * @param sorter the Sort instance whose description will be appended
     * @return a string that combines the integer value and the sorter's description
     */
    private static <X> String getDescription(int n, Sort<X> sorter) {
        return n + AT + sorter.getDescription();
    }

    /**
     * TESTME
     */
    @SuppressWarnings("SameParameterValue")
    private void runDateTimeSortBenchmark(Class<?> tClass, ChronoLocalDateTime<?>[] dateTimes, int N, int m) throws IOException {
        final SortWithHelper<ChronoLocalDateTime<?>> sorter = new TimSort<>();
        logger.info("****************************** DateTime sort: " + N + " " + sorter.getDescription() + " ******************************");
        @SuppressWarnings("unchecked") final SorterBenchmark<ChronoLocalDateTime<?>> sorterBenchmark = new SorterBenchmark<>((Class<ChronoLocalDateTime<?>>) tClass, (xs) -> Arrays.copyOf(xs, xs.length), sorter, dateTimes, m, timeLoggersLinearithmic);
        sorterBenchmark.run(getDescription(N, sorter), N);
        sorter.close();
        logger.info("************************************************************");
    }

    /**
     * The precomputed logarithm (base 10) of Euler's number (e),
     * a mathematical constant approximately equal to 2.71828.
     * This value is statically initialized using a utility method for logarithmic calculations,
     * and remains constant throughout the application runtime.
     */
    private static final double LgE = Utilities.lg(Math.E);

    /**
     * Checks if the given option is configured as a benchmark string sorter.
     *
     * @param option the configuration option to be checked
     * @return true if the option is configured as a benchmark string sorter, false otherwise
     */
    private boolean isConfigBenchmarkStringSorter(String option) {
        return isConfigBoolean(BENCHMARKSTRINGSORTERS, option);
    }

    /**
     * Checks if the provided configuration option enables or disables the
     * benchmark date sorter functionality.
     *
     * @param option the configuration option to be checked
     * @return true if the configuration option corresponds to enabling
     *         the benchmark date sorter functionality, false otherwise
     */
    private boolean isConfigBenchmarkDateSorter(String option) {
        return isConfigBoolean("benchmarkdatesorters", option);
    }

    /**
     * Determines if the given option corresponds to a configuration setting
     * for benchmarking integer sorters.
     *
     * @param option the configuration option to check
     * @return true if the option matches the benchmark integer sorter configuration, false otherwise
     */
    private boolean isConfigBenchmarkIntegerSorter(String option) {
        return isConfigBoolean(BENCHMARKINTEGERSORTERS, option);
    }

    /**
     * Determines if the specified configuration option in a given section is a boolean value.
     *
     * @param section the section of the configuration to check
     * @param option the specific option within the section to verify
     * @return true if the configuration option is a boolean, false otherwise
     */
    private boolean isConfigBoolean(String section, String option) {
        return config.getBoolean(section, option);
    }

    public static final String BENCHMARKINTEGERSORTERS = "benchmarkintegersorters";

    private final Config config;
}