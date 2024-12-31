/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable.tree;

import com.phasmidsoftware.dsaipg.util.*;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.phasmidsoftware.dsaipg.adt.symbolTable.tree.BSTOptimisedDeletion.random;
import static com.phasmidsoftware.dsaipg.util.SortBenchmarkHelper.getWords;
import static com.phasmidsoftware.dsaipg.util.Utilities.formatDecimal3Places;


/**
 * A class that benchmarks Binary Search Tree (BST) operations using various configurations,
 * including experiments with different tree structures, input sizes, and operational modes.
 * This class extends the Benchmark_Timer to provide timing-based benchmarking functionality.
 *
 * @param <K> The type of keys in the BST, which must be comparable.
 * @param <V> The type of values stored in the BST.
 */
public class BSTBenchmark<K extends Comparable<K>, V> extends Benchmark_Timer<K[]> {

    /**
     * The main method serves as the entry point to the program. It logs the benchmarking process
     * and takes command-line arguments that represent integers. Each integer is passed to the
     * {@code doBenchmark} method for executing a binary search tree (BST) benchmarking test.
     *
     * @param args an array of strings representing command-line arguments. Each argument is
     *             expected to be an integer, which determines the number of words used in
     *             benchmarking and the associated resources to be loaded.
     */
    public static void main(final String[] args) {
        logger.info("BSTBenchmark: with args: " + Arrays.toString(args));
        final Stream<Integer> stream = Arrays.stream(args).map(Integer::parseInt);
        stream.forEach(BSTBenchmark::doBenchmark);
    }

    /**
     * Executes a benchmarking process using the provided supplier and the number of defined runs.
     * This method utilizes the supplier to generate input data, and benchmarks the performance
     * by invoking the {@code runFromSupplier} method.
     *
     * @param supplier a supplier that provides an array of elements of type {@code K}.
     *                 These elements serve as the input data for the benchmark.
     * @return the average time in milliseconds for each run during the benchmarking process.
     */
    public double runBenchmark(final Supplier<K[]> supplier) {
        return runFromSupplier(supplier, nRuns);
    }

    /**
     * Constructor for a SorterBenchmark where we provide the following parameters:
     *
     * @param tClass      the class of K.
     * @param bst         the bst.
     * @param ks          the array of Ts.
     * @param nRuns       the number of runs to perform in this benchmark.
     * @param timeLoggers the time-loggers.
     * @param stats       the statistics to be returned.
     */
    public BSTBenchmark(final Class<K> tClass, final BstDetail<K, V> bst, final K[] ks, final int nRuns, final TimeLogger[] timeLoggers, final Stats stats) {
        super("BST benchmark", createPreProcessor(), createExperiment(bst), createPostProcessor(bst, stats));
        this.nRuns = nRuns;
    }

    /**
     * Executes a benchmark process using a binary search tree (BST) with a set of words as input data.
     * This method determines the word resource file based on the input parameter, initializes the BST,
     * and runs a benchmarking test for performance evaluation.
     *
     * @param x the number of words to use in the benchmark. Determines the resource file to load
     *          based on thresholds: "10K" for < 50,000; "100K" for >= 50,000 and < 200,000; "1M" for >= 200,000.
     */
    private static void doBenchmark(final int x) {
        String resource = "eng-uk_web_2002_" + (x < 50000 ? "10K" : x < 200000 ? "100K" : "1M") + "-words.txt";
        try {
            final int mode = 2;
            final double initialSampleFraction = 0.8;
            final double runSampleFraction = 0.2;
            final int nRuns = 1000;
            final String[] words = getWords(resource, SortBenchmark::getLeipzigWords);
            logger.info("creating benchmark with " + x + " words from " + resource);
            final int runSampleSize = (int) (words.length * runSampleFraction);
            logger.info("creating BST with mode " + mode + " and " + words.length + " words");
            final BstDetail<String, Integer> bst = createBST(mode, words, initialSampleFraction);
            logger.info("BST has " + bst.size() + " nodes initially");
            final Stats stats = new Stats(bst.size());
            final BSTBenchmark<String, Integer> benchmark = new BSTBenchmark<>(String.class, bst, words, nRuns, SortBenchmark.timeLoggersLinearithmic, stats);
            final Supplier<String[]> supplier = () -> Utilities.fillRandomArray(String.class, random, runSampleSize, r -> words[r.nextInt(words.length)]);
            final double result = benchmark.runBenchmark(supplier);
            logger.info("Stats: " + stats + "; average milliseconds: " + formatDecimal3Places(result));
        } catch (FileNotFoundException e) {
            logger.error("BSTBenchmark: cannot find word file: " + e.getLocalizedMessage());
        }
    }

    /**
     * Creates a binary search tree (BST) with pre-populated data based on the provided parameters.
     * This method initializes the BST using a mode value, selects a random subset of input words,
     * and maps them to their corresponding lengths before inserting them into the tree.
     *
     * @param mode       the mode used to configure the BST initialization process.
     * @param words      an array of input strings, which are used to populate the BST.
     * @param sampleRate the rate at which a sample of the input array is selected to populate the BST.
     *                   A value between 0.0 and 1.0, where 1.0 means all elements are used.
     * @return a BstDetail object containing the initialized BST populated with the sample words and their lengths.
     */
    private static BstDetail<String, Integer> createBST(final int mode, final String[] words, final double sampleRate) {
        final BSTOptimisedDeletion<String, Integer> bst = new BSTOptimisedDeletion<>(mode);
        final int sampleSize = (int) (words.length * sampleRate);
        final String[] initialStrings = Utilities.fillRandomArray(String.class, random, sampleSize, r -> words[r.nextInt(words.length)]);
        final Map<String, Integer> map = new HashMap<>();
        for (String initialString : initialStrings) map.put(initialString, initialString.length());
        bst.putAll(map);
        return bst;
    }

    /**
     * Creates a pre-processor UnaryOperator for an array of generic type X.
     * This method logs the size of the array and its first element.
     *
     * @param <X> the generic type of the array elements
     * @return a UnaryOperator that processes an array of type X
     */
    private static <X> UnaryOperator<X[]> createPreProcessor() {
        return xs -> {
            logger.debug("pre-processor: size of array is: " + xs.length + " with first element: " + xs[0]);
            return xs;
        };
    }

    /**
     * Creates an experiment as a {@code Consumer} that operates on an array of elements of type {@code X}.
     * The experiment performs two operations on the provided binary search tree (BST):
     * 1. Deletes all elements of the array from the BST.
     * 2. Reinserts all elements of the array back into the BST with {@code null} values.
     * Both actions log the size of the BST before and after execution.
     *
     * @param <X> the type of the elements in the array, which must extend {@code Comparable<X>}.
     * @param bst the binary search tree (BST) on which the experiment operates.
     * @return a {@code Consumer} that defines the experiment to perform on the BST and the array of elements.
     */
    private static <X extends Comparable<X>> Consumer<X[]> createExperiment(final BST<X, ?> bst) {
        return xs -> {
            logger.debug("experiment: before: bst has " + bst.size());
            for (X x : xs) bst.delete(x);
            for (X x : xs) bst.put(x, null);
            logger.debug("experiment: after: bst has " + bst.size());
        };
    }

    /**
     * Creates a post-processor {@code Consumer} for an array of elements of type {@code X}.
     * The post-processor logs specific binary search tree (BST) statistics, such as mean depth
     * and the square root of the tree's size. Additionally, it updates the provided {@code Stats}
     * object with the computed mean depth and size of the BST.
     *
     * @param <X> the type of the elements in the array, which must extend {@code Comparable<X>}.
     * @param bst the detailed binary search tree (BST) on which calculations are based.
     * @param stats the {@code Stats} object that will be updated with the BST's mean depth and size.
     * @return a {@code Consumer} that processes an array of {@code X} elements and logs and updates BST statistics.
     */
    private static <X extends Comparable<X>> Consumer<X[]> createPostProcessor(final BstDetail<X, ?> bst, final Stats stats) {
        return xs -> {
            final double meanDepth = bst.meanDepth();
            final double sqrt = Math.sqrt(bst.size());
            stats.setMeanDepth(bst.size(), meanDepth);
            logger.debug("BST nodes: " + bst.size() + " mean depth: " + formatDecimal3Places(meanDepth) + " sqrt(n): " + formatDecimal3Places(sqrt));
        };
    }

    /**
     * Represents statistical data associated with a benchmarking process involving binary search trees (BST).
     * Instances of this class are used to track and record key metrics such as the number of nodes,
     * the mean depth of the tree, and the initial mean depth.
     */
    public static class Stats {
        public Stats(final int nodes) {
            this.initialNodes = nodes;
        }

        @Override
        public String toString() {
            return "initialNodes: " +
                    initialNodes +
                    ", nodes: " +
                    nodes +
                    ", initialMeanDepth: " +
                    formatDecimal3Places(initialMeanDepth) +
                    ", meanDepth: " +
                    formatDecimal3Places(meanDepth);
        }

        /**
         * Updates the number of nodes and the mean depth of the tree. If the initial mean depth
         * has not been set, it is initialized with the provided mean depth value.
         *
         * @param nodes     the total number of nodes in the binary search tree.
         * @param meanDepth the average depth of the nodes in the binary search tree.
         */
        void setMeanDepth(final int nodes, final double meanDepth) {
            if (initialMeanDepth == 0) initialMeanDepth = meanDepth;
            this.meanDepth = meanDepth;
            this.nodes = nodes;
        }

        final int initialNodes;
        double meanDepth = 0;
        double initialMeanDepth = 0;
        int nodes;
    }

    final static LazyLogger logger = new LazyLogger(BSTBenchmark.class);

    private final int nRuns;
}