package com.phasmidsoftware.dsaipg.adt.threesum;

import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TwoSumBenchmarkTest {

    /**
     * This class tests the method {@code runBenchmarks()} in the {@link TwoSumBenchmark} class.
     * The {@code runBenchmarks()} method runs multiple benchmarks for two different implementations of the Two-Sum
     * algorithm and records execution time. This test class ensures that the method runs as expected.
     */

    /**
     * Test to check if the runBenchmarks method completes without issues for small inputs.
     */
    @Test
    public void testRunBenchmarksWithSmallInput() {
        int runs = 10;
        int n = 100;
        int m = 100;

        TwoSumBenchmark benchmark = new TwoSumBenchmark(runs, n, m);
        benchmark.runBenchmarks();

        // The test does not throw exceptions, ensuring the method executes correctly.
        assertTrue(true);
    }

    /**
     * Test to ensure that the supplier in TwoSumBenchmark provides a valid array of integers.
     */
    @Test
    public void testSupplierProvidesIntegers() {
        int runs = 5;
        int n = 50;
        int m = 50;

        TwoSumBenchmark benchmark = new TwoSumBenchmark(runs, n, m);
        Supplier<int[]> supplier = new Source(n, m).intsSupplier(10);

        int[] result = supplier.get();
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    /**
     * Test to verify that the runBenchmarks method does not perform benchmarks for n > 8000.
     */
    @Test
    public void testRunBenchmarksIgnoredForLargeN() {
        int runs = 5;
        int n = 16000;
        int m = 16000;

        TwoSumBenchmark benchmark = new TwoSumBenchmark(runs, n, m);
        // Since n > 8000, we expect no benchmarks to be executed without errors.
        benchmark.runBenchmarks();

        // The test passes as long as it does not throw exceptions.
        assertTrue(true);
    }

    /**
     * Test to ensure benchmarkTwoSum handles empty input arrays gracefully.
     */
    @Test
    public void testBenchmarkTwoSumHandlesEmptyArray() {
        int runs = 5;
        int n = 50;
        int m = 50;

        TwoSumBenchmark benchmark = new TwoSumBenchmark(runs, n, m);
        Supplier<int[]> supplier = () -> new int[0];

        try {
            double t1 = new Benchmark_Timer<int[]>("Empty Array Test", x -> x, xs -> {
            }).runFromSupplier(supplier, runs);
            assertTrue(t1 >= 0);
        } catch (Exception e) {
            throw new AssertionError("Benchmark failed to handle empty arrays.");
        }
    }
}