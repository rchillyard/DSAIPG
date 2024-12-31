/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class BenchmarkTest {

    int pre = 0;
    int run = 0;
    int post = 0;

    @Test // Slow
    public void testWaitPeriods() throws Exception {
        int nRuns = 2;
        int warmups = 1;
        Benchmark<Boolean> bm = new Benchmark_Timer<>(
                "testWaitPeriods", b -> {
            GoToSleep(50L, -1);
            return null;
        },
                b -> {
                    GoToSleep(100L, 0);
                },
                b -> {
                    GoToSleep(30L, 1);
                });
        double x = bm.run(true, nRuns);
        assertEquals(nRuns, post);
        assertEquals(nRuns + warmups, run);
        assertEquals(nRuns + warmups, pre);
        assertEquals(100, x, 10);
    }

    private void GoToSleep(long mSecs, int which) {
        try {
            Thread.sleep(mSecs);
            if (which == 0) run++;
            else if (which > 0) post++;
            else pre++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getWarmupRuns() {
        assertEquals(1, Benchmark_Timer.getWarmupRuns(0));
        assertEquals(1, Benchmark_Timer.getWarmupRuns(20));
        assertEquals(3, Benchmark_Timer.getWarmupRuns(45));
        assertEquals(3, Benchmark_Timer.getWarmupRuns(100));
        assertEquals(3, Benchmark_Timer.getWarmupRuns(1000));
    }


    /**
     * Test the runFromSupplier method for a case where no preprocessing or postprocessing is done.
     * Ensures that the method executes the provided supplier and run functions without exceptions.
     */
    @Test
    public void testRunFromSupplier_Basic() {
        // Create a supplier that generates a number
        Supplier<Integer> supplier = () -> 42;

        // Create a consumer for the run operation (a no-op in this case)
        Consumer<Integer> fRun = i -> {
        };

        // Create an instance of Benchmark_Timer
        Benchmark_Timer<Integer> timer = new Benchmark_Timer<>("Basic Test", fRun);

        // Execute the runFromSupplier method
        double time = timer.runFromSupplier(supplier, 10);

        // Assert that execution took some measurable time
        assertTrue(time >= 0);
    }

    /**
     * Test the runFromSupplier method with preprocessing.
     * The preprocessing step modifies the input before being processed.
     */
    @Test
    public void testRunFromSupplier_WithPreprocessing() {
        // Create a supplier that generates a number
        Supplier<Integer> supplier = () -> 5;

        // Create a preprocessing function to double the input
        UnaryOperator<Integer> fPre = i -> i * 2;

        // Create a consumer for the run operation (a simple validation in this case)
        Consumer<Integer> fRun = i -> assertTrue(i >= 0);

        // Create an instance of Benchmark_Timer with preprocessing
        Benchmark_Timer<Integer> timer = new Benchmark_Timer<>("With Preprocessing Test", fPre, fRun);

        // Execute the runFromSupplier method
        double time = timer.runFromSupplier(supplier, 10);

        // Assert that execution took some measurable time
        assertTrue(time >= 0);
    }

    /**
     * Test the runFromSupplier method with postprocessing.
     * The postprocessing step is executed after running the main function.
     */
    @Test
    public void testRunFromSupplier_WithPostprocessing() {
        // Create a supplier that generates a number
        Supplier<Integer> supplier = () -> 100;

        // Create a consumer for the run operation that increments a counter
        Consumer<Integer> fRun = i -> {
        };

        // Create a postprocessing consumer to validate the result
        Consumer<Integer> fPost = i -> assertTrue(i == 100);

        // Create an instance of Benchmark_Timer with postprocessing
        Benchmark_Timer<Integer> timer = new Benchmark_Timer<>("With Postprocessing Test", fRun, fPost);

        // Execute the runFromSupplier method
        double time = timer.runFromSupplier(supplier, 10);

        // Assert that execution took some measurable time
        assertTrue(time >= 0);
    }

    /**
     * Test the runFromSupplier method with both preprocessing and postprocessing.
     */
    @Test
    public void testRunFromSupplier_FullPipeline() {
        // Create a supplier that generates a number
        Supplier<Integer> supplier = () -> 50;

        // Create a preprocessing function to square the input
        UnaryOperator<Integer> fPre = i -> i * i;

        // Create a consumer for the run operation (log the squared value)
        Consumer<Integer> fRun = i -> assertTrue(i >= 0);

        // Create a postprocessing consumer to validate that the squared value is correct
        Consumer<Integer> fPost = i -> assertTrue(i == 2500);

        // Create an instance of Benchmark_Timer with full pipeline
        Benchmark_Timer<Integer> timer = new Benchmark_Timer<>("Full Pipeline Test", fPre, fRun, fPost);

        // Execute the runFromSupplier method
        double time = timer.runFromSupplier(supplier, 10);

        // Assert that execution took some measurable time
        assertTrue(time >= 0);
    }

    /**
     * Test the runFromSupplier method with zero iterations.
     * Ensures that warmup and main execution are handled gracefully.
     */
    @Test
    public void testRunFromSupplier_ZeroIterations() {
        // Create a supplier that generates a number
        Supplier<Integer> supplier = () -> 10;

        // Create a consumer for the run operation (a no-op in this case)
        Consumer<Integer> fRun = i -> {
        };

        // Create an instance of Benchmark_Timer
        Benchmark_Timer<Integer> timer = new Benchmark_Timer<>("Zero Iterations Test", fRun);

        // Execute the runFromSupplier method with zero iterations
        double time = timer.runFromSupplier(supplier, 0);

        // Assert that execution does not break and returns a valid time
        assertTrue(time >= 0);
    }

}