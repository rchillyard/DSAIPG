/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.generic.Sort;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.helper.BaseHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.HelperFactory;
import com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparableHelper;
import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import com.phasmidsoftware.dsaipg.util.benchmark.StatPack;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.sort.helper.Instrument.*;
import static com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparatorHelper.getRunsConfig;
import static com.phasmidsoftware.dsaipg.util.config.ConfigTest.INVERSIONS;
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static com.phasmidsoftware.dsaipg.util.general.Utilities.round;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

@SuppressWarnings("ALL")
public class QuickSortDualPivotTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testSort() throws Exception {
        Integer[] xs = new Integer[4];
        xs[0] = 3;
        xs[1] = 4;
        xs[2] = 2;
        xs[3] = 1;
        Sort<Integer> s = new QuickSort_DualPivot<>(xs.length, config);
        Integer[] ys = s.sort(xs);
        assertEquals(Integer.valueOf(1), ys[0]);
        assertEquals(Integer.valueOf(2), ys[1]);
        assertEquals(Integer.valueOf(3), ys[2]);
        assertEquals(Integer.valueOf(4), ys[3]);
    }

    @Test
    public void testSortWithInstrumenting0() throws Exception {
        int n = 16;
        final SortWithHelper<Integer> sorter = new QuickSort_DualPivot<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting1() throws Exception {
        int n = 541; // a prime number
        final SortWithHelper<Integer> sorter = new QuickSort_DualPivot<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(97));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting2() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_DualPivot<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(100));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting3() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_DualPivot<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting4() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_DualPivot<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting5() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_DualPivot<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting6a() throws Exception {
        Integer[] xs = new Integer[2];
        xs[0] = 3;
        xs[1] = 4;
        final Config config = setupConfig("true", "false", "0", "1", "1", "");
        final Helper<Integer> helper = new InstrumentedComparableHelper<>("test", config);
        helper.init(xs.length, getRunsConfig(config));
        SortWithComparableHelper<Integer> sorter = new QuickSort_DualPivot<>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        helper.postProcess(ys);
        assertEquals(Integer.valueOf(3), ys[0]);
        assertEquals(Integer.valueOf(4), ys[1]);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        assertEquals(1, (int) statPack.getStatistics(COMPARES).mean());
        assertEquals(0, (int) statPack.getStatistics(SWAPS).mean()); // XXX check this
        assertEquals(0, (int) statPack.getStatistics(FIXES).mean());
        assertEquals(2, (int) statPack.getStatistics(HITS).mean()); // XXX Is this correct? Why not just 2?
    }

    @Test
    public void testSortWithInstrumenting6b() throws Exception {
        Integer[] xs = new Integer[2];
        xs[0] = 4;
        xs[1] = 3;
        final Config config = setupConfig("true", "true", "0", "1", "1", "");
        final Helper<Integer> helper = new InstrumentedComparableHelper<>("test", config);
        helper.init(xs.length, getRunsConfig(config));
        SortWithComparableHelper<Integer> sorter = new QuickSort_DualPivot<>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        helper.postProcess(ys);
        assertEquals(Integer.valueOf(3), ys[0]);
        assertEquals(Integer.valueOf(4), ys[1]);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        assertEquals(1, (int) statPack.getStatistics(COMPARES).mean());
        assertEquals(1, (int) statPack.getStatistics(SWAPS).mean()); // XXX check this
        assertEquals(1, (int) statPack.getStatistics(FIXES).mean());
        assertEquals(4, (int) statPack.getStatistics(HITS).mean()); // XXX Is this correct? Why not just 4?
    }

    @Test
    public void testSortWithInstrumenting6c() throws Exception {
        Integer[] xs = new Integer[3];
        xs[0] = 4;
        xs[1] = 3;
        xs[2] = 5;
        final Config config = setupConfig("true", "true", "0", "1", "1", "");
        final Helper<Integer> helper = new InstrumentedComparableHelper<>("test", config);
        helper.init(xs.length, getRunsConfig(config));
        SortWithComparableHelper<Integer> sorter = new QuickSort_DualPivot<>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        helper.postProcess(ys);
        assertEquals(Integer.valueOf(3), ys[0]);
        assertEquals(Integer.valueOf(4), ys[1]);
        assertEquals(Integer.valueOf(5), ys[2]);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        assertEquals(3, (int) statPack.getStatistics(COMPARES).mean());
        assertEquals(1, (int) statPack.getStatistics(SWAPS).mean());
        assertEquals(1, (int) statPack.getStatistics(FIXES).mean());
        assertEquals(5, (int) statPack.getStatistics(HITS).mean());
    }

    @Test
    public void testSortWithInstrumenting7() throws Exception {
        Integer[] xs = new Integer[4];
        // Inversions = 5
        xs[0] = 3;
        xs[1] = 4;
        xs[2] = 2;
        xs[3] = 1;
        final Config config = setupConfig("true", "true", "0", "1", "1", "");
        final Helper<Integer> helper = new InstrumentedComparableHelper<>("test", config);
        helper.init(xs.length, getRunsConfig(config));
        SortWithComparableHelper<Integer> sorter = new QuickSort_DualPivot<>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        helper.postProcess(ys);
        assertEquals(Integer.valueOf(1), ys[0]);
        assertEquals(Integer.valueOf(2), ys[1]);
        assertEquals(Integer.valueOf(3), ys[2]);
        assertEquals(Integer.valueOf(4), ys[3]);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        assertEquals(5, (int) statPack.getStatistics(COMPARES).mean());
        assertEquals(3, (int) statPack.getStatistics(SWAPS).mean());
        assertEquals(3, (int) statPack.getStatistics(FIXES).mean());
        // NOTE was 16 before Partitioner_DualPivot.partition stopped asking
        // helper.swap for a self-swap when a pivot is already in place. Those two
        // array accesses are no longer performed, so 14 is what the code now
        // costs; the old figure counted work that achieved nothing.
        assertEquals(14, (int) statPack.getStatistics(HITS).mean());
    }

    @Test
    public void testPartition1() throws Exception {
        String testString = "PBAXWPPVPCPDZY";
        char[] charArray = testString.toCharArray();
        Character[] array = new Character[charArray.length];
        for (int i = 0; i < array.length; i++) array[i] = charArray[i];
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        QuickSort<Character> sorter = new QuickSort_DualPivot<Character>(array.length, 1, config);
        Partitioner<Character> partitioner = sorter.partitioner;
        List<Partition<Character>> partitions = partitioner.partition(QuickSort.createPartition(array));
        assertEquals(3, partitions.size());
        Partition<Character> p0 = partitions.get(0);
        assertEquals(0, p0.from);
        assertEquals(4, p0.to);
        Partition<Character> p1 = partitions.get(1);
        assertEquals(5, p1.from);
        assertEquals(12, p1.to);
        Partition<Character> p2 = partitions.get(2);
        assertEquals(13, p2.from);
        assertEquals(14, p2.to);
        char[] chars = new char[array.length];
        for (int i = 0; i < chars.length; i++) chars[i] = array[i];
        String partitionedString = new String(chars);
        assertEquals("DBACPPPVPXPWYZ", partitionedString);
    }

    @Test
    public void testPartition2() throws Exception {
        String testString = "SEAYRLFVZQTCMK";
        char[] charArray = testString.toCharArray();
        Character[] array = new Character[charArray.length];
        for (int i = 0; i < array.length; i++) array[i] = charArray[i];
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        QuickSort<Character> sorter = new QuickSort_DualPivot<Character>(array.length, 1, config);
        Partitioner<Character> partitioner = sorter.partitioner;
        List<Partition<Character>> partitions = partitioner.partition(QuickSort.createPartition(array));
        assertEquals(3, partitions.size());
        Partition<Character> p0 = partitions.get(0);
        assertEquals(0, p0.from);
        assertEquals(4, p0.to);
        Partition<Character> p1 = partitions.get(1);
        assertEquals(5, p1.from);
        assertEquals(9, p1.to);
        Partition<Character> p2 = partitions.get(2);
        assertEquals(10, p2.from);
        assertEquals(14, p2.to);
        char[] chars = new char[array.length];
        for (int i = 0; i < chars.length; i++) chars[i] = array[i];
        String partitionedString = new String(chars);
        assertEquals("CEAFKLMRQSZVYT", partitionedString);
    }

    @Test
    public void testSortDetailed() throws Exception {
        int k = 7;
        int N = (int) Math.pow(2, k);
        // NOTE this depends on the cutoff value for quick sort.
        int levels = k - 2;
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        final Helper<Integer> helper = HelperFactory.create("quick sort dual pivot", N, config);
        System.out.println(helper);
        Sort<Integer> s = new QuickSort_DualPivot<>(helper);
        s.init(N);
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10000));
        assertEquals(Integer.valueOf(1360), xs[0]);
        helper.preProcess(xs);
        Integer[] ys = s.sort(xs);
        assertTrue(helper.isSorted(ys));
        helper.postProcess(ys);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        System.out.println(statPack);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        final int swaps = (int) statPack.getStatistics(SWAPS).mean();
        final int copies = (int) statPack.getStatistics(COPIES).mean();
        final long worstCompares = round(2.0 * N * Math.log(N));
        System.out.println("compares: " + compares + ", worstCompares: " + worstCompares);
        assertTrue(compares <= worstCompares);
        assertTrue(inversions <= fixes);
    }

    @Test
    public void testPartitionWithSort() {
        String[] xs = new String[]{"g", "f", "e", "d", "c", "b", "a"};
        int n = xs.length;
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        final Helper<String> helper = new InstrumentedComparableHelper<>("test", config);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        QuickSort<String> sorter = new QuickSort_DualPivot<>(helper);
        int inversions = n * (n - 1) / 2;
        assertEquals(inversions, helper.inversions(xs));
        Partitioner<String> partitioner = sorter.createPartitioner();
        List<Partition<String>> partitions = partitioner.partition(new Partition<>(xs, 0, xs.length));
        assertEquals(11L, privateMethodTester.invokePrivate("getFixes"));
        Partition<String> p0 = partitions.get(0);
        sorter.sort(xs, 0, p0.to, 0);
        assertEquals(11L, privateMethodTester.invokePrivate("getFixes"));
        Partition<String> p1 = partitions.get(1);
        sorter.sort(xs, p1.from, p1.to, 0);
        assertEquals(21L, privateMethodTester.invokePrivate("getFixes"));
        Partition<String> p2 = partitions.get(2);
        sorter.sort(xs, p2.from, n, 0);
        long fixes = (long) privateMethodTester.invokePrivate("getFixes");
        // NOTE: there are at least as many fixes as inversions -- sort methods aren't necessarily perfectly efficient in terms of swaps.
        // assertTrue(inversions <= fixes); // TODO restore
        assertEquals(0, helper.inversions(xs));
        assertEquals(11L, privateMethodTester.invokePrivate("getSwaps"));
    }

    @Test
    public void testSortWithInstrumentingK3() throws Exception {
        doQuickSortDualPivotTest(3, "7", 100, true);
    }

    @Test
    public void testSortWithInstrumentingK4() throws Exception {
        doQuickSortDualPivotTest(4, "1", 250, true);
    }

    @Test
    public void testSortWithInstrumentingK5() throws Exception {
        doQuickSortDualPivotTest(5, "6", 1_000, true);
    }
    @Test
    public void testSortWithInstrumentingK6() throws Exception {
        doQuickSortDualPivotTest(6, "13", 2_500, true);
    }

    @Test
    public void testSortWithInstrumentingK7() throws Exception {
        doQuickSortDualPivotTest(7, "12", 50_000, true);
    }
    @Test
    public void testSortWithInstrumentingK8() throws Exception {
        doQuickSortDualPivotTest(8, "2", 50_000, true);
    }
    @Test
    public void testSortWithInstrumentingK9() throws Exception {
        doQuickSortDualPivotTest(9, "2", 50_000, true);
    }

    private static void doQuickSortDualPivotTest(int k, String seed, int bound, boolean instrumenting) {
        int n = (int) (Math.pow(2, k));
        System.out.println("n: " + n + ", k: " + k);
        // expectedMeanDepth (gross value) =  (13/18) × (1/ln(2)) × log₃(n)
        // expectedLoops =  expectedMeanDepth × n
        // expectedCompares = expectedLoops × (24/13)
        // expectedSwaps = expectedCompares / 6
        // expectedHits = 8 × expectedSwaps
        final Config config_instr = setupConfig(instrumenting + "", "false", seed, "0", "1", "");
//        System.out.println(config_instr);
        final SortWithHelper<Integer> sorter = new QuickSort_DualPivot<>(n, getRunsConfig(config_instr), config_instr);
        final BaseHelper<Integer> helper = (BaseHelper<Integer>) sorter.getHelper();
        Function<Random,Integer> random = r -> r.nextInt(bound);
        final Integer[] xs = helper.random(n, Integer.class, random);
        helper.init(n);
        final Integer[] sorted = sorter.sort(xs);
        assertTrue("is not sorted", helper.isSorted(sorted));
        double expectedMeanDepth = 13.0 * k / 18 / Math.log(2);
        System.out.println("expected mean depth: " + (expectedMeanDepth));
        int expectedLoops = (int) (expectedMeanDepth * n);
        long expectedCompares = (long) (expectedLoops * 5.0 / 3);
        long expectedSwaps = (long) (expectedLoops * 2.0 / 3);  // equivalently: expectedCompares * 2/5
        long expectedHits = (long) (expectedLoops * 8.0 / 3);  // equivalently: expectedCompares * 8/5
        //        long loops = helper.getLookups();
//        System.out.println("expected lookups: " + (expectedLoops));
//        System.out.println("Compares: " + loops+", expected: " + expectedLoops);
        long compares = helper.getCompares();
        System.out.println("Compares: " + compares + ", expected: " + expectedCompares);
        long swaps = helper.getSwaps();
        System.out.println("Swaps: " + swaps + ", expected: " + expectedSwaps);
        long hits = helper.getHits();
        System.out.println("Hits: " + hits + ", expected: " + expectedHits);
        System.out.println("Max depth: " + helper.maxDepth());
    }

    private static String[] setupWords(final int n) {
        if (n > 36) throw new RuntimeException("cannot have n > 36");
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        String[] words = new String[n * n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                words[i * n + j] = alphabet.substring(i, i + 1) + alphabet.substring(j, j + 1);
        return words;
    }

    final static LazyLogger logger = new LazyLogger(QuickSort_DualPivot.class);

    @BeforeClass
    public static void beforeClass() throws IOException {
        config = Config.load();
    }

    private static Config config;
}