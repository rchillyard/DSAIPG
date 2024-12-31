package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortOpt;
import com.phasmidsoftware.dsaipg.sort.elementary.ShellSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.IntroSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSort;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_3way;
import com.phasmidsoftware.dsaipg.sort.linearithmic.QuickSort_DualPivot;
import com.phasmidsoftware.dsaipg.util.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Unit tests which are in fact benchmarks of the various sort methods.
 * Keep in mind that we are sorting objects here (Integers). not primitives.
 */
public class Benchmarks {

    @BeforeClass
    public static void setupClass() {
        try {
            config = Config.load(Benchmarks.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertionSortOptBenchmark() {
        String description = "Optimized Insertion sort";
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>(description, N, config);
        final Sort<Integer> sort = new InsertionSortOpt<>(helper);
        runBenchmark(description, sort, helper);
    }

    @Test
    public void testIntroSortBenchmark() {
        String description = "Intro sort";
        final Helper<Integer> helper = new NonInstrumentingComparableHelper<>(description, N, config);
        final Sort<Integer> sort = new IntroSort<>(helper);
        runBenchmark(description, sort, helper);
    }

    @Test
    public void testMergeSortBenchmark() {
        String description = "Merge sort";
        final Helper<Integer> helper = new NonInstrumentingComparableHelper<>(description, N, config);
        try (final Sort<Integer> sort = new MergeSort<>(helper)) {
            runBenchmark(description, sort, helper);
        }
    }

    @Test
    public void testQuickSort3WayBenchmark() {
        String description = "3-way Quick sort";
        final Helper<Integer> helper = new NonInstrumentingComparableHelper<>(description, N, config);
        final Sort<Integer> sort = new QuickSort_3way<>(helper);
        runBenchmark(description, sort, helper);
    }

    @Test
    public void testQuickSortDualPivotSortBenchmark() {
        String description = "Dual-pivot Quick sort";
        final Helper<Integer> helper = new NonInstrumentingComparableHelper<>(description, N, config);
        final Sort<Integer> sort = new QuickSort_DualPivot<>(helper);
        runBenchmark(description, sort, helper);
    }

    @Test
    public void testShellSortBenchmark() {
        String description = "3Shell sort";
        final Helper<Integer> helper = new NonInstrumentingComparableHelper<>(description, N, config);
        final Sort<Integer> sort = new ShellSort<>(5, helper);
        runBenchmark(description, sort, helper);
    }

    public void runBenchmark(String description, Sort<Integer> sort, Helper<Integer> helper) {
        sort.init(N);
        Supplier<Integer[]> supplier = () -> helper.random(Integer.class, Random::nextInt);
        final Benchmark<Integer[]> benchmark = new Benchmark_Timer<>(
                description + " for " + N + " Integers",
                (xs) -> Arrays.copyOf(xs, xs.length),
                sort::mutatingSort,
                null
        );
        logger.info(Utilities.formatDecimal3Places(benchmark.runFromSupplier(supplier, 100)) + " ms");
    }

    final static LazyLogger logger = new LazyLogger(Benchmarks.class);

    public static final int N = 2000;

    private static Config config;

}