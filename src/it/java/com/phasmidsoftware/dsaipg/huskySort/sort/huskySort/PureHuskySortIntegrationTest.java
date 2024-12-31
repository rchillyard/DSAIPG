package com.phasmidsoftware.dsaipg.huskySort.sort.huskySort;

import com.phasmidsoftware.dsaipg.huskySort.sort.ComparableSortHelper;
import com.phasmidsoftware.dsaipg.huskySort.sort.huskySortUtils.HuskyCoderFactory;
import com.phasmidsoftware.dsaipg.huskySort.util.Benchmark;
import com.phasmidsoftware.dsaipg.huskySort.util.Config;
import com.phasmidsoftware.dsaipg.huskySort.util.TimeLogger;
import com.phasmidsoftware.dsaipg.util.Utilities;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static com.phasmidsoftware.dsaipg.huskySort.sort.huskySort.HuskySortBenchmark.timeLoggersLinearithmic;
import static com.phasmidsoftware.dsaipg.huskySort.util.ProcessorDependentTimeout.getFactoredTimeout;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.junit.Assert.assertEquals;

public class PureHuskySortIntegrationTest {

    @BeforeClass
    public static void doBeforeClass() throws IOException {
        config = Config.load();
    }

    @Test
    public void testSortString4() {
        final int N = 1000;
        final String[] words = HuskySortBenchmarkHelper.getWords("3000-common-words.txt", HuskySortBenchmark::lineAsList);
        final Random random = new Random();
        final PureHuskySort<String> pureHuskySort = new PureHuskySort<>(HuskyCoderFactory.asciiCoder, false, false);
        final Benchmark<String[]> benchmark = new Benchmark<>("PureHuskySort", null, pureHuskySort::sort, null);
        final double time = benchmark.run(() -> Utilities.fillRandomArray(String.class, random, N, r -> words[r.nextInt(words.length)]), 200);
        final double expected = getFactoredTimeout(475, MICROSECONDS, config, MICROSECONDS);
        assertEquals(expected, time * 1000, 400);
        for (final TimeLogger timeLogger : timeLoggersLinearithmic) timeLogger.log(time, N);
    }

    @Test
    public void testSortString5() {
        final int N = 1000;
        final String[] words = HuskySortBenchmarkHelper.getWords("3000-common-words.txt", HuskySortBenchmark::lineAsList);
        final Random random = new Random();
        final PureHuskySort<String> pureHuskySort = new PureHuskySort<>(HuskyCoderFactory.englishCoder, false, false);
        final Benchmark<String[]> benchmark = new Benchmark<>("PureHuskySort", null, pureHuskySort::sort, null);
        final double time = benchmark.run(() -> Utilities.fillRandomArray(String.class, random, N, r -> words[r.nextInt(words.length)]), 200);
        final double expected = getFactoredTimeout(325, MICROSECONDS, config, MICROSECONDS);
        assertEquals(expected, time * 1000, 300);
        for (final TimeLogger timeLogger : timeLoggersLinearithmic) timeLogger.log(time, N);
    }

    private static Config config;

    private final ComparableSortHelper<String> helper = new ComparableSortHelper<>("dummy helper");

}