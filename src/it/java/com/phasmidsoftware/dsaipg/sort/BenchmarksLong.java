package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.sort.elementary.BubbleSort;
import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.sort.elementary.SelectionSort;
import com.phasmidsoftware.dsaipg.util.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Unit tests which are in fact benchmarks of the various sort methods.
 * Keep in mind that we are sorting objects here (Integers). not primitives.
 */
public class BenchmarksLong {

    @BeforeClass
    public static void setupClass() {
        try {
            config = Config.load(BenchmarksLong.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Ignore // Slow
    public void testBubbleSortBenchmark() {
        String description = "BubbleSort";
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>(description, N, config);
        final Sort<Integer> sort = new BubbleSort<>(helper);
        runBenchmark(description, sort, helper);
    }

    @Test
    public void testInsertionSortBenchmark() {
        String description = "Insertion sort";
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>(description, N, config);
        final Sort<Integer> sort = new InsertionSort<>(helper);
        runBenchmark(description, sort, helper);
    }

    @Test
    public void testSelectionSortBenchmark() {
        String description = "Selection sort";
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>(description, N, config);
        final Sort<Integer> sort = new SelectionSort<>(helper);
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

    final static LazyLogger logger = new LazyLogger(BenchmarksLong.class);

    public static final int N = 2000;

    private static Config config;

}