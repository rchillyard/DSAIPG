/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.util;

import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

import static com.phasmidsoftware.dsaipg.util.SortBenchmarkHelper.getWords;

/**
 * The {@code PQBenchmark} class is designed to benchmark operations performed with priority queues.
 * It includes methods to evaluate insertion and deletion performance using different configurations
 * and algorithms. This class uses external configuration for its settings and employs benchmarking
 * utilities to measure execution times of the operations.
 */
public class PQBenchmark {

    /**
     * Constructs a new instance of PQBenchmark with the specified configuration.
     *
     * @param config the configuration object used to set up the benchmark
     */
    public PQBenchmark(Config config) {
        this.config = config;
    }

    /**
     * The main method serves as the entry point for the PQBenchmark application. It initializes
     * the configuration, logs application information, performs benchmarking for insertion and
     * deletion operations with and without Floyd's method, and outputs the results.
     *
     * @param args command-line arguments, expected to specify word counts for benchmarking;
     *             may be empty if no counts are provided.
     * @throws IOException if an error occurs during configuration loading.
     */
    public static void main(String[] args) throws IOException {
        Config config = Config.load(PQBenchmark.class);
        // XXX this does not look at all correct. Why huskysort?
        logger.info("SortBenchmark.main: " + config.get("huskysort", "version") + " with word counts: " + Arrays.toString(args));
        if (args.length == 0) logger.warn("No word counts specified on the command line");
        PQBenchmark benchmark = new PQBenchmark(config);
        System.out.println("with floyd: " + benchmark.insertDeleteN(10000, 1000, true));
        System.out.println("no floyd: " + benchmark.insertDeleteN(10000, 1000, false));
    }

    /**
     * Inserts and conditionally deletes elements from a priority queue using Floyd insertion or standard insertion.
     * This method processes an integer array by inserting elements into a priority queue and, based on a random condition,
     * attempts to remove an element from the queue.
     *
     * @param a     the array of integers to be inserted into the priority queue
     * @param floyd a flag that determines whether to use Floyd insertion method for the priority queue
     */
    // Insert and delete random integer array with floyd methods according to parameter
    private void insertArray(int[] a, final boolean floyd) {
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>(a.length, true, Comparator.naturalOrder(), floyd);
        final Random random = new Random();
        for (int j : a) {
            pq.give(j);
            if (random.nextBoolean()) {
                try {
                    pq.take();
                } catch (PQException e) {
                    e.printStackTrace(); // TODO use logging
                }
            }
        }
    }

    /**
     * Performs a benchmark test by inserting and deleting elements, measuring the operation's execution time.
     * This method uses the Benchmark_Timer to calculate the average runtime for the given operation.
     *
     * @param n      the number of random integers to be generated and processed.
     * @param m      the number of times the benchmark test is repeated.
     * @param floyd  a flag indicating whether the Floyd's heap construction method should be used during the insertion.
     * @return the average execution time for the benchmark process, in milliseconds.
     */
    private double insertDeleteN(final int n, int m, final boolean floyd) {
        final Random ran = new Random();
        int[] random = new int[n];
        for (int i = 0; i < n; i++) {
            random[i] = ran.nextInt(n);
        }
        Benchmark<Boolean> bm = new Benchmark_Timer<>(
                "testPQwithFloydoff",
                null,
                b -> insertArray(random, floyd),
                null
        );
        return bm.run(true, m);

    }

    /**
     * For mergesort, the number of array accesses is actually six times the number of comparisons.
     * That's because, in addition to each comparison, there will be approximately two copy operations.
     * Thus, in the case where comparisons are based on primitives,
     * the normalized time per run should approximate the time for one array access.
     */
    public final static TimeLogger[] timeLoggersLinearithmic = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n log n): ", SortBenchmark::minComparisons)
    };

    final static LazyLogger logger = new LazyLogger(PQBenchmark.class);

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

    private static Collection<String> lineAsList(String line) {
        List<String> words = new ArrayList<>();
        words.add(line);
        return words;
    }

    private static Collection<String> getLeipzigWords(String line) {
        return getWords(SortBenchmarkHelper.regexLeipzig, line);
    }

    // CONSIDER: to be eliminated soon.
    private static Benchmark<LocalDateTime[]> benchmarkFactory(String description, Consumer<LocalDateTime[]> sorter, Consumer<LocalDateTime[]> checker) {
        return new Benchmark_Timer<>(
                description,
                (xs) -> Arrays.copyOf(xs, xs.length),
                sorter,
                checker
        );
    }

    private static final double LgE = Utilities.lg(Math.E);

    boolean isConfigBoolean(String section, String option) {
        return config.getBoolean(section, option);
    }

    private final Config config;
}