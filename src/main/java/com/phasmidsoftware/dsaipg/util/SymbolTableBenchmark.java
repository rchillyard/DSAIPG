/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static com.phasmidsoftware.dsaipg.util.SortBenchmarkHelper.getWords;
import static com.phasmidsoftware.dsaipg.util.SortBenchmarkHelper.regexLeipzig;
import static com.phasmidsoftware.dsaipg.util.Utilities.formatWhole;

/**
 * Class to test the comparative efficiency of:
 * (1) build a hash table and get the values in key order;
 * (2) build a Red-black tree and the values in key order.
 * <p>
 * Spoiler alert! RB tree is a little faster (as it should be).
 */
public class SymbolTableBenchmark {

    /**
     * Creates an instance of the SymbolTableBenchmark class with the specified configuration.
     *
     * @param config the configuration object used to initialize the benchmark.
     */
    public SymbolTableBenchmark(Config config) {
        this.config = config;
    }

    /**
     * The main method serves as the entry point for the SymbolTableBenchmark application.
     * It initializes the configuration, logs version information, and executes the benchmarking process.
     *
     * @param args an array of strings specifying the word counts for the benchmark;
     *             if no arguments are provided, a warning is logged.
     * @throws IOException if an I/O error occurs while loading the configuration or processing input.
     */
    public static void main(String[] args) throws IOException {
        Config config = Config.load(SymbolTableBenchmark.class);
        logger.info("SortBenchmark.main: " + config.get("SortBenchmark", "version") + " with word counts: " + Arrays.toString(args));
        if (args.length == 0) logger.warn("No word counts specified on the command line");
        new SymbolTableBenchmark(config).doMain(args);
    }

    /**
     * Executes the benchmarking process by processing the provided arguments.
     * The method extracts word counts from the arguments and performs benchmarking tasks on them.
     *
     * @param args an array of strings representing numerical word counts to be used for benchmarks.
     *             Each element is expected to be a parsable integer.
     */
    void doMain(String[] args) {
        doBenchmarks(getWordCounts(args));
    }

    /**
     * Method to run pure (non-instrumented) string sorter benchmarks.
     * <p>
     * NOTE: this is package-private because it is used by unit tests.
     *
     * @param words  the word source.
     * @param nWords the number of words to be sorted.
     * @param nRuns  the number of runs.
     */
    void benchmarkStringSorters(String[] words, int nWords, int nRuns) {
        logger.info("Testing pure sorts with " + formatWhole(nRuns) + " runs of sorting " + formatWhole(nWords) + " words");
        Random random = new Random();
        runHashTableBenchmark(words, nWords, nRuns, random);
        runRBTreeBenchmark(words, nWords, nRuns, random);
    }

    /**
     * Executes a benchmark test for building and rendering a hash table with the given parameters.
     *
     * @param words  an array of words to be used as input for the benchmark.
     * @param nWords the number of words to be processed during the benchmark.
     * @param nRuns  the number of times the benchmark process will be repeated.
     * @param random an instance of {@code Random} used to introduce randomness during the benchmarking process.
     */
    private static void runHashTableBenchmark(String[] words, int nWords, int nRuns, Random random) {
        Benchmark<String[]> benchmark = new Benchmark_Timer<>("hashTable", null, SymbolTableBenchmark::buildAndRenderHashTable, null);
        doPureBenchmark(words, nWords, nRuns, random, benchmark);
    }

    /**
     * Runs the benchmark for a Red-Black Tree implementation.
     *
     * @param words  the array of words to be used for the benchmark.
     * @param nWords the number of words to be processed in each benchmark run.
     * @param nRuns  the number of benchmark iterations to execute.
     * @param random the Random instance used for any randomness needed during benchmarking.
     */
    private static void runRBTreeBenchmark(String[] words, int nWords, int nRuns, Random random) {
        Benchmark<String[]> benchmark = new Benchmark_Timer<>("RBTree", null, SymbolTableBenchmark::buildRBTree, null);
        doPureBenchmark(words, nWords, nRuns, random, benchmark);
    }

    /**
     * Builds a hash table from the input array and prepares its keys for sorted rendering.
     * The method constructs a {@code HashMap} where each string in the input array is a key
     * and its index in the array is the associated value.
     * The keys are then sorted for rendering.
     *
     * @param xs an array of strings to be used as keys in the hash table.
     */
    private static void buildAndRenderHashTable(String[] xs) {
        Map<String, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < xs.length; i++)
            hashMap.put(xs[i], i);
        // TODO sort this out.
        List<String> keys = new ArrayList<>(new TreeMap<>(hashMap).keySet());
        Collections.sort(keys);
//        List<Integer> values = new ArrayList<>();
//        for (String key : keys) values.add(hashMap.get(key));
        // CONSIDER returning values.
    }

    /**
     * Constructs a Red-Black Tree using the input array of strings.
     * Each string from the array is added to a {@code TreeMap} with its index in the array as the value.
     * This method is currently pending further implementation and may be subject to modifications.
     *
     * @param xs an array of strings to be used as keys in the Red-Black Tree.
     */
    private static void buildRBTree(String[] xs) {
        Map<String, Integer> treeMap = new TreeMap<>();
        // TODO sort this out.
        for (int i = 0; i < xs.length; i++)
            treeMap.put(xs[i], i);
//        List<Integer> values = new ArrayList<>();
//        for (String key : treeMap.keySet()) values.add(treeMap.get(key));
        // CONSIDER returning values.
    }

    /**
     * Executes benchmarking processes for a stream of word counts.
     * For each word count within the stream, it performs a symbol table benchmark.
     *
     * @param wordCounts a stream of integers representing the word counts to be used in the benchmarks
     */
    private void doBenchmarks(Stream<Integer> wordCounts) {
        logger.info("Beginning String sorts");
        wordCounts.forEach(this::doSymbolTableBenchmark);
    }

    /**
     * Executes a symbol table benchmark test using a specified word count threshold to determine the resource file.
     * The method fetches words from the selected resource,
     * processes them using the `SymbolTableBenchmark::getLeipzigWords`
     * function, and then performs string sorter benchmarking. If the resource file cannot be located, a warning is logged.
     *
     * @param x the word count threshold that determines which resource file to use for the benchmark;
     *          if less than 50,000, a smaller resource file is selected, otherwise a larger one is chosen
     */
    private void doSymbolTableBenchmark(int x) {
        String resource = "eng-uk_web_2002_" + (x < 50000 ? "10K" : "100K") + "-sentences.txt";
        try {
            String[] words = getWords(resource, SymbolTableBenchmark::getLeipzigWords);
            benchmarkStringSorters(words, x, 1000);
        } catch (FileNotFoundException e) {
            logger.warn("Unable to find resource: " + resource, e);
        }
    }

    /**
     * For mergesort, the number of array accesses is actually four times the number of comparisons (six when noCopy is false).
     * That's because, in addition to each comparison, there will be approximately two copy operations.
     * Thus, in the case where comparisons are based on primitives,
     * the normalized time per run should approximate the time for one array access.
     */
    public final static TimeLogger[] timeLoggersLinearithmic = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n log n): ", SymbolTableBenchmark::minComparisons)
    };

    final static LazyLogger logger = new LazyLogger(SymbolTableBenchmark.class);

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
     * Extracts a collection of words from the provided input string based on a predefined regular expression.
     *
     * @param line the input string from which to extract words.
     * @return a collection of strings containing the words extracted from the input string.
     */
    public static Collection<String> getLeipzigWords(String line) {
        return getWords(regexLeipzig, line);
    }

    /**
     * Executes a pure benchmark test by generating randomized arrays of words,
     * running the benchmark multiple times, and logging the results.
     *
     * @param words     an array of strings used as a source for random word generation.
     * @param nWords    the number of words to include in the generated array during each run.
     * @param nRuns     the number of times the benchmark should be executed.
     * @param random    an instance of {@code Random} for generating random indices for word selection.
     * @param benchmark the benchmark instance used to measure the runtime of operations on the generated arrays.
     */
    private static void doPureBenchmark(String[] words, int nWords, int nRuns, Random random, Benchmark<String[]> benchmark) {
        // CONSIDER we should manage the space returned by fillRandomArray and deallocate it after use.
        final double time = benchmark.runFromSupplier(() -> Utilities.fillRandomArray(String.class, random, nWords, r -> words[r.nextInt(words.length)]), nRuns);
        for (TimeLogger timeLogger : timeLoggersLinearithmic) timeLogger.log("", time, nWords);
    }

    /**
     * Processes an array of strings, converting each string into an integer, and returns a stream of integers.
     *
     * @param args an array of strings, each representing a valid integer value
     * @return a stream containing the integer values parsed from the input array
     */
    private static Stream<Integer> getWordCounts(String[] args) {
        return Arrays.stream(args).map(Integer::parseInt);
    }

    /**
     * A constant representing the base-2 logarithm of Euler's number (e).
     * This value is computed using the {@code Utilities.lg} method,
     * which calculates the logarithm base 2 of a given number.
     * <p>
     * Useful in contexts requiring the base-2 logarithm of mathematical constants.
     */
    private static final double LgE = Utilities.lg(Math.E);

    /**
     * NOTE currently unused.
     *
     * @return the current configuration
     */
    private Config getConfig() {
        return config;
    }

    private final Config config;
}