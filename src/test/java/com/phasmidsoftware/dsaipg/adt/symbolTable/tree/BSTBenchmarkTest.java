package com.phasmidsoftware.dsaipg.adt.symbolTable.tree;

import com.phasmidsoftware.dsaipg.util.SortBenchmark;
import com.phasmidsoftware.dsaipg.util.Utilities;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A test class for benchmarking the performance of Binary Search Trees (BST) using the
 * `BSTBenchmark` class and its `runBenchmark` method. The tests evaluate the behavior
 * of the BSTBenchmark for different scenarios such as valid input, empty input, single-element
 * input, and large input.
 * TODO re-implement the commented-out tests.
 */
public class BSTBenchmarkTest {

    /**
     * Test class for the BSTBenchmark class and, specifically, its runBenchmark method.
     */
//    @Test
    public void testRunBenchmarkWithValidSupplier() {
        // Create a simple BstDetail instance
        BstDetail<String, Integer> bst = new BSTOptimisedDeletion<>(2);
        bst.put("a", 1);
        bst.put("b", 2);

        String[] data = {"a", "b", "c"};

        Supplier<String[]> supplier = () -> data;

        // Create a Stats instance
        BSTBenchmark.Stats stats = new BSTBenchmark.Stats(3);

        // Create an instance of BSTBenchmark
        BSTBenchmark<String, Integer> benchmark = new BSTBenchmark<>(
                String.class,
                bst,
                data,
                10, // Number of runs
                SortBenchmark.timeLoggersLinearithmic,
                stats
        );

        // Run the benchmark
        double result = benchmark.runBenchmark(supplier);

        // Verify result (result should be greater than 0 because it measures execution time)
        assertTrue("Expected runBenchmark() to return a positive value", result > 0);
    }

    //    @Test
    public void testRunBenchmarkWithEmptyArray() {
        // Create a simple BstDetail instance
        BstDetail<String, Integer> bst = new BSTOptimisedDeletion<>(2);

        String[] data = {};

        Supplier<String[]> supplier = () -> data;

        // Create a Stats instance
        BSTBenchmark.Stats stats = new BSTBenchmark.Stats(0);

        // Create an instance of BSTBenchmark
        BSTBenchmark<String, Integer> benchmark = new BSTBenchmark<>(
                String.class,
                bst,
                data,
                10, // Number of runs
                SortBenchmark.timeLoggersLinearithmic,
                stats
        );

        // Run the benchmark
        double result = benchmark.runBenchmark(supplier);

        // Verify result (result should be 0 for an empty array since there's no work to do)
        assertEquals("Expected runBenchmark() to return 0 for an empty array", 0, result, 0.0);
    }

    @Test
    public void testRunBenchmarkWithSingleElementArray() {
        // Create a simple BstDetail instance
        BstDetail<String, Integer> bst = new BSTOptimisedDeletion<>(2);
        bst.put("a", 1);

        String[] data = {"a"};

        Supplier<String[]> supplier = () -> data;

        // Create a Stats instance
        BSTBenchmark.Stats stats = new BSTBenchmark.Stats(1);

        // Create an instance of BSTBenchmark
        BSTBenchmark<String, Integer> benchmark = new BSTBenchmark<>(
                String.class,
                bst,
                data,
                10, // Number of runs
                SortBenchmark.timeLoggersLinearithmic,
                stats
        );

        // Run the benchmark
        double result = benchmark.runBenchmark(supplier);

        // Verify result (result should be greater than 0 because it measures execution time)
        assertTrue("Expected runBenchmark() to return a positive value", result > 0);
    }

    //    @Test
    public void testRunBenchmarkWithLargeInput() {
        // Create a simple BstDetail instance
        BstDetail<String, Integer> bst = new BSTOptimisedDeletion<>(2);
        String[] data = Utilities.fillRandomArray(String.class, BSTOptimisedDeletion.random, 1000, random -> "word" + random.nextInt(1000));

        Supplier<String[]> supplier = () -> data;

        // Create a Stats instance
        BSTBenchmark.Stats stats = new BSTBenchmark.Stats(1000);

        // Create an instance of BSTBenchmark
        BSTBenchmark<String, Integer> benchmark = new BSTBenchmark<>(
                String.class,
                bst,
                data,
                10, // Number of runs
                SortBenchmark.timeLoggersLinearithmic,
                stats
        );

        // Run the benchmark
        double result = benchmark.runBenchmark(supplier);

        // Verify result (result should be greater than 0 because it measures execution time)
        assertTrue("Expected runBenchmark() to return a positive value for large input", result > 0);
    }
}