/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.NonInstrumentingComparableHelper;

import java.io.IOException;
import java.util.Random;

/**
 * The OperationsBenchmark class is responsible for benchmarking operations on arrays of integers
 * such as finding the largest integer, comparing adjacent elements, and their optimized variant.
 * It uses a configuration object to drive the parameters of the benchmarks.
 */
public class OperationsBenchmark {
    /**
     * Executes a series of benchmarking tests to measure the computation costs associated with different operations.
     * The benchmarks include:
     * - Running a test to find the largest integer in an array (`runLargestInteger`).
     * - Running a test to compare adjacent elements in an array (`runCompareAdjacentElements`).
     * - Running an optimized version of the adjacent comparison test (`runCompareAdjacentElementsOptimized`).
     * Each test internally measures performance metrics such as time taken to complete the operation for a set of configuration parameters.
     */
    public void runBenchmarks() {
        runLargestInteger();
        runCompareAdjacentElements();
        runCompareAdjacentElementsOptimized();
    }

    /**
     * Constructs an OperationsBenchmark with the provided configuration.
     *
     * @param config the configuration object that defines the parameters
     *               for the operations benchmark.
     */
    public OperationsBenchmark(Config config) {
        this.config = config;
    }

    /**
     * The main method serves as the entry point for the OperationsBenchmark program.
     * It initializes the configuration, creates an instance of OperationsBenchmark,
     * and executes the benchmarks.
     *
     * @param args Command-line arguments. Not used in this application.
     * @throws IOException If an I/O error occurs while loading the configuration.
     */
    public static void main(String[] args) throws IOException {
        Config config = Config.load(OperationsBenchmark.class);
        final OperationsBenchmark benchmark = new OperationsBenchmark(config);
        benchmark.runBenchmarks();
    }

    /**
     * This benchmark is designed to determine the cost of visiting every element of an array of Integers.
     * It's awkward to do the same thing with primitive ints, so I'll leave that until later.
     */
    private void runLargestInteger() {
        final int nLargest = config.getInt("operationsbenchmark", "nlargest", DEFAULT_ARRAY_SIZE);
        final int repetitions = config.getInt("operationsbenchmark", "repetitions", 1000);
        logger.info("OperationsBenchmark.runBenchmarks: largest " + nLargest + " integers with " + repetitions + " repetitions");

        try (
                final Helper<Integer> helper = new NonInstrumentingComparableHelper<>("largest", nLargest, 0L, config)) {
            final Timer timer = new Timer();
            final double time = timer.repeat(repetitions,
                    () -> getRandomIntegers(helper),
                    OperationsBenchmark::findLargest
            );
            for (TimeLogger timeLogger : timeLoggersLinear) timeLogger.log("Operations", time, nLargest);
        }
    }

    /**
     * This benchmark is designed to determine the cost of visiting every element of an array of Integers twice.
     */
    private void runCompareAdjacentElements() {
        final int nCompareAdjacent = config.getInt("operationsbenchmark", "ncompareadjacent", DEFAULT_ARRAY_SIZE);
        final int repetitions = config.getInt("operationsbenchmark", "repetitions", 1000);
        logger.info("OperationsBenchmark.runBenchmarks: compareAdjacent " + nCompareAdjacent + " integers with " + repetitions + " repetitions");
        try (final Helper<Integer> helper = new NonInstrumentingComparableHelper<>("compareAdjacent", nCompareAdjacent, 0L, config)) {
            final Timer timer = new Timer();
            final double time = timer.repeat(repetitions,
                    () -> getRandomIntegers(helper),
                    OperationsBenchmark::compareAdjacent
            );
            for (TimeLogger timeLogger : timeLoggersLinear) timeLogger.log("Operations", time, nCompareAdjacent);
        }
    }

    /**
     * This benchmark is designed to determine the cost of visiting every element of an array of Integers twice.
     */
    private void runCompareAdjacentElementsOptimized() {
        final int nCompareAdjacent = config.getInt("operationsbenchmark", "ncompareadjacent", DEFAULT_ARRAY_SIZE);
        final int repetitions = config.getInt("operationsbenchmark", "repetitions", 1000);
        logger.info("OperationsBenchmark.runBenchmarks: compareAdjacentOptimized " + nCompareAdjacent + " integers with " + repetitions + " repetitions");
        try (final Helper<Integer> helper = new NonInstrumentingComparableHelper<>("compareAdjacent", nCompareAdjacent, 0L, config)) {
            final Timer timer = new Timer();
            final double time = timer.repeat(repetitions,
                    () -> getRandomIntegers(helper),
                    OperationsBenchmark::compareAdjacentOptimized
            );
            for (TimeLogger timeLogger : timeLoggersLinear) timeLogger.log("Operations", time, nCompareAdjacent);
        }
    }

    /**
     * Generates an array of random integers using the given helper instance.
     *
     * @param helper the Helper instance used to generate random integers; it provides
     *               the logic for generating random values of the specified type.
     * @return an array of randomly generated integers.
     */
    private Integer[] getRandomIntegers(Helper<Integer> helper) {
        return helper.random(Integer.class, Random::nextInt);
    }

    /**
     * Holds the configuration settings for the OperationsBenchmark.
     * This configuration defines parameters that control how the benchmarking
     * operations are executed, including but not limited to input size, optimization
     * preferences, and performance thresholds.
     */
    private final Config config;

    /**
     * Finds the largest integer in the given array of integers.
     *
     * @param xs an array of integers where the largest value is to be determined
     * @return the largest integer in the array, or null if the array is null or empty
     */
    private static Integer findLargest(Integer[] xs) {
        int largest = Integer.MIN_VALUE;
        for (Integer x : xs) if (x > largest) largest = x;
        return largest;
    }

    /**
     * Compares adjacent elements in the given array of integers to determine if there is any inversion.
     * An inversion occurs when a preceding element is greater than its following element.
     *
     * @param xs the array of integers to be checked for adjacent inversions
     * @return true if at least one inversion is found; false otherwise
     */
    private static Boolean compareAdjacent(Integer[] xs) {
        boolean inversions = false;
        for (int i = 1; i < xs.length; i++)
            inversions = inversions | xs[i - 1] > xs[i];
        return inversions;
    }

    /**
     * Compares adjacent elements in the provided integer array to detect if an inversion exists.
     * An inversion is defined as a pair of adjacent elements where the preceding element is greater than the succeeding one.
     *
     * @param xs the array of integers to be checked for adjacent inversions.
     *           It is assumed to have at least one element, and its validity is not explicitly checked.
     * @return true if there exists at least one inversion in the array, false otherwise.
     */
    private static Boolean compareAdjacentOptimized(Integer[] xs) {
        boolean inversions = false;
        int x = xs[0];
        for (int i = 1; i < xs.length; i++) {
            final int y = xs[i];
            inversions = inversions | x > y;
            //noinspection SuspiciousNameCombination
            x = y;
        }
        return inversions;
    }

    /**
     * An array of TimeLogger instances, used for logging the timing measurements
     * of various computational operations in the benchmarking process. Each instance
     * logs time data in a specific format and normalization (if applicable).
     * <p>
     * - The first TimeLogger logs raw time per operation in milliseconds.
     * - The second TimeLogger logs normalized time per operation, where the time
     * is normalized by a factor of the problem size.
     * <p>
     * This configuration enables both raw and normalized performance analysis for
     * benchmarking tasks in the OperationsBenchmark class.
     */
    private final static TimeLogger[] timeLoggersLinear = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n): ", n -> n * 1.0)
    };

    /**
     * A static logger instance used for logging messages and events within the OperationsBenchmark class.
     * This logger is initialized with a reference to the OperationsBenchmark class and provides
     * capabilities for lazy logging to ensure minimal performance impact when log messages are not needed.
     */
    final static LazyLogger logger = new LazyLogger(OperationsBenchmark.class);

    public static final int DEFAULT_ARRAY_SIZE = 100000;

}