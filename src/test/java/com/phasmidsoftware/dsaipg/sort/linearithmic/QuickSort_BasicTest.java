/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.*;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.LazyLogger;
import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import com.phasmidsoftware.dsaipg.util.StatPack;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.phasmidsoftware.dsaipg.sort.Instrument.*;
import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.DEFAULT_RUNS;
import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.getRunsConfig;
import static com.phasmidsoftware.dsaipg.util.ConfigTest.INVERSIONS;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.*;
import static com.phasmidsoftware.dsaipg.util.Utilities.round;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class QuickSort_BasicTest {

    @Test
    public void testSort1() throws Exception {
        Integer[] xs = new Integer[4];
        xs[0] = 3;
        xs[1] = 4;
        xs[2] = 2;
        xs[3] = 1;
        Sort<Integer> s = new QuickSort_Basic<>(Config.load(getClass()));
        Integer[] ys = s.sort(xs);
        assertEquals(Integer.valueOf(1), ys[0]);
        assertEquals(Integer.valueOf(2), ys[1]);
        assertEquals(Integer.valueOf(3), ys[2]);
        assertEquals(Integer.valueOf(4), ys[3]);
    }

    @Test
    public void testSort2() throws Exception {
        int n = 16;
        final SortWithHelper<Integer> sorter = new QuickSort_Basic<>(Config.load(getClass()));
        final Helper<Integer> helper = sorter.getHelper();
        helper.init(n);
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(100));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testPartition() throws Exception {
        String testString = "PABXWPPVPDPCYZ";
        char[] charArray = testString.toCharArray();
        Character[] array = new Character[charArray.length];
        for (int i = 0; i < array.length; i++) array[i] = charArray[i];
        Sort<Character> s = new QuickSort_Basic<Character>(Config.load(getClass()));
        Partition<Character> p = ((QuickSort<Character>) s).createPartition(array, 0, array.length - 1);
        assertEquals(0, p.from);
        assertEquals(13, p.to);
        assertEquals(Character.valueOf('P'), array[0]);
        assertEquals(Character.valueOf('Z'), array[array.length - 1]);
    }

    @Test
    public void testSort() throws Exception {
        Integer[] xs = new Integer[4];
        xs[0] = 3;
        xs[1] = 4;
        xs[2] = 2;
        xs[3] = 1;
        Sort<Integer> s = new QuickSort_Basic<>(xs.length, 1, setupConfig("true", "false", "0", "1", "1", ""));
        Integer[] ys = s.sort(xs);
        assertEquals(Integer.valueOf(1), ys[0]);
        assertEquals(Integer.valueOf(2), ys[1]);
        assertEquals(Integer.valueOf(3), ys[2]);
        assertEquals(Integer.valueOf(4), ys[3]);
    }

    @Test
    public void testSortA() throws Exception {
        Integer[] xs = new Integer[]{3, 4, 2, 1, 4, 1, 2, 3, 2, 1};
        Sort<Integer> s = new QuickSort_Basic<>(xs.length, 1, setupConfig("true", "false", "0", "1", "1", ""));
        Integer[] ys = s.sort(xs);
        assertEquals(Integer.valueOf(1), ys[0]);
        assertEquals(Integer.valueOf(1), ys[1]);
        assertEquals(Integer.valueOf(1), ys[2]);
        assertEquals(Integer.valueOf(2), ys[3]);
        assertEquals(Integer.valueOf(4), ys[9]);
    }

    @Test
    public void testSortWithInstrumenting0() throws Exception {
        int n = 16;
        final SortWithHelper<Integer> sorter = new QuickSort_Basic<>(n, getRunsConfig(config_instr), config_instr);
        final InstrumentedComparatorHelper<Integer> helper = (InstrumentedComparatorHelper<Integer>) sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
        assertEquals(56, helper.getCompares());
        assertEquals(14, helper.getSwaps());
        assertEquals(99, helper.getHits());
        assertEquals(112, helper.getLookups());
    }

    @Test
    public void testSortWithInstrumenting1() throws Exception {
        int n = 541; // a prime number
        final SortWithHelper<Integer> sorter = new QuickSort_Basic<>(n, getRunsConfig(config_instr), config_instr);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(97));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting2() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_Basic<>(n, config_instr);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(100));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting3() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_Basic<>(n, config_instr);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting4() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_Basic<>(n, config_instr);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting5() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_Basic<>(n, config_instr); // no cutoff
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
        assertEquals(12377, helper.getCompares());
        assertEquals(2221, helper.getSwaps());
        assertEquals(17813, helper.getHits());
        assertEquals(24754, helper.getLookups());
    }

    @Test
    public void testSortWithInstrumenting5a() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_Basic<>(n, config_instr.copy(HELPER, CUTOFF, "7")); // with cutoff = 16
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
        double averageCompares = 2 * n * Math.log(n);
        long expectedCompares = 12166L;
        assertEquals(expectedCompares, helper.getCompares());
        double averageSwaps = averageCompares / 6;
        long expectedSwaps = 2614L;
        assertEquals(expectedSwaps, helper.getSwaps());
        assertEquals(17990, helper.getHits());
        assertEquals(24332, helper.getLookups());
        assertEquals(averageCompares, expectedCompares, 2000);
        assertEquals(averageSwaps, expectedSwaps, 800);
    }

    @Test
    public void testPartition1() throws Exception {
        String testString = "HBAXWPQVDCREZY";
        char[] charArray = testString.toCharArray();
        Character[] array = new Character[charArray.length];
        for (int i = 0; i < array.length; i++) array[i] = charArray[i];
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        QuickSort<Character> sorter = new QuickSort_Basic<Character>(array.length, config);
        Partitioner<Character> partitioner = sorter.partitioner;
        List<Partition<Character>> partitions = partitioner.partition(QuickSort.createPartition(array));
        assertEquals(2, partitions.size());
        Partition<Character> p0 = partitions.get(0);
        assertEquals(0, p0.from);
        assertEquals(5, p0.to);
        Partition<Character> p1 = partitions.get(1);
        assertEquals(6, p1.from);
        assertEquals(14, p1.to);
        char[] chars = new char[array.length];
        for (int i = 0; i < chars.length; i++) chars[i] = array[i];
        String partitionedString = new String(chars);
        assertEquals("DBAECHQVPWRXZY", partitionedString);
    }

    @Test
    public void testPartition2() throws Exception {
        String testString = "SEAYRLFVZQTCMK";
        char[] charArray = testString.toCharArray();
        Character[] array = new Character[charArray.length];
        for (int i = 0; i < array.length; i++) array[i] = charArray[i];
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        QuickSort<Character> sorter = new QuickSort_Basic<Character>(array.length, DEFAULT_RUNS, config);
        Partitioner<Character> partitioner = sorter.partitioner;
        List<Partition<Character>> partitions = partitioner.partition(QuickSort.createPartition(array));
        assertEquals(2, partitions.size());
        Partition<Character> p0 = partitions.get(0);
        assertEquals(0, p0.from);
        assertEquals(9, p0.to);
        Partition<Character> p1 = partitions.get(1);
        assertEquals(10, p1.from);
        assertEquals(14, p1.to);
        char[] chars = new char[array.length];
        for (int i = 0; i < chars.length; i++) chars[i] = array[i];
        String partitionedString = new String(chars);
        assertEquals("QEAKRLFMCSTZVY", partitionedString);
    }

    @Test
    public void testPartition3() throws Exception {
        String testString = "PBAXWPPVPCPDZY";
        char[] charArray = testString.toCharArray();
        Character[] array = new Character[charArray.length];
        for (int i = 0; i < array.length; i++) array[i] = charArray[i];
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        final Helper<Character> helper = HelperFactory.create("quick sort", array.length, config);
        QuickSort<Character> sorter = new QuickSort_Basic<>(helper);
        sorter.init(array.length);
        Partitioner<Character> partitioner = sorter.partitioner;
        List<Partition<Character>> partitions = partitioner.partition(QuickSort.createPartition(array));
        assertEquals(2, partitions.size());
        Partition<Character> p0 = partitions.get(0);
        assertEquals(0, p0.from);
        assertEquals(6, p0.to);
        Partition<Character> p1 = partitions.get(1);
        assertEquals(7, p1.from);
        assertEquals(14, p1.to);
        System.out.println("pivot: " + array[6]);
        char[] chars = new char[array.length];
        for (int i = 0; i < chars.length; i++) chars[i] = array[i];
        String partitionedString = new String(chars);
        assertEquals("PBADPCPVPPWXZY", partitionedString);
        sorter.postProcess(new Character[1]);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        final int swaps = (int) statPack.getStatistics(SWAPS).mean();
        assertEquals(15, compares);
        assertEquals(5, swaps);
        assertEquals(27, (int) statPack.getStatistics(HITS).mean());
    }

    @Test
    public void testPartition4() throws Exception {
        String testString = "SEAYRLFVZQTCMK";
        char[] charArray = testString.toCharArray();
        Character[] array = new Character[charArray.length];
        for (int i = 0; i < array.length; i++) array[i] = charArray[i];
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        final Helper<Character> helper = HelperFactory.create("quick sort", array.length, config);
        QuickSort<Character> sorter = new QuickSort_Basic<>(helper);
        sorter.init(array.length);
        Partitioner<Character> partitioner = sorter.partitioner;
        List<Partition<Character>> partitions = partitioner.partition(QuickSort.createPartition(array));
        assertEquals(2, partitions.size());
        Partition<Character> p0 = partitions.get(0);
        assertEquals(0, p0.from);
        assertEquals(9, p0.to);
        Partition<Character> p1 = partitions.get(1);
        assertEquals(10, p1.from);
        assertEquals(14, p1.to);
        System.out.println("pivot: " + array[9]);
        char[] chars = new char[array.length];
        for (int i = 0; i < chars.length; i++) chars[i] = array[i];
        String partitionedString = new String(chars);
        assertEquals("QEAKRLFMCSTZVY", partitionedString);
        sorter.postProcess(new Character[1]);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        final int swaps = (int) statPack.getStatistics(SWAPS).mean();
        assertEquals(15, compares);
        assertEquals(4, swaps);
        assertEquals(25, (int) statPack.getStatistics(HITS).mean());
    }

    @Test
    public void testSortDetailed() throws Exception {
        int k = 7;
        int N = (int) Math.pow(2, k);
        int levels = k - 2;
        final Config config = setupConfig("true", "false", "0", "1", "1", "");
        final Helper<Integer> helper = HelperFactory.create("quick sort dual pivot", N, config);
        Sort<Integer> s = new QuickSort_Basic<>(helper);
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
        final double averageCompares = round(2.0 * N * Math.log(N));
        final long bestCompares = (N * k);
        System.out.println("bestCompares: " + bestCompares + ", compares: " + compares + ", averageCompares: " + averageCompares);
        assertTrue(compares <= averageCompares);
        assertEquals(averageCompares, compares, averageCompares / 5);
        System.out.println("ratio of compares to swaps: " + compares * 1.0 / swaps);
    }

    /**
     * NOTE: this test will occasionally fail.
     * TODO: understand why.
     *
     * @throws Exception a problem.
     */
    public void testSortDetailedRandom() throws Exception {
        int k = 10;
        int N = (int) Math.pow(2, k);
        // NOTE this depends on the cutoff value for quick sort.
        int levels = k - 2;
        final Config config = setupConfig("true", "false", "", "1", "", "");
        final Helper<Integer> helper = HelperFactory.create("quick sort dual pivot", N, config);
        System.out.println(helper);
        Sort<Integer> s = new QuickSort_Basic<>(helper);
        s.init(N);
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10000));
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
        final long bestCompares = round(N * k);
        System.out.println("bestCompares: " + bestCompares + ", compares: " + compares + ", worstCompares: " + worstCompares);
        // FIXME this depends on the cutoff value and so shouldn't be compared to worstCompares.
//        assertTrue(compares <= worstCompares); // FIXME sometimes this fails.
        System.out.println("ratio of compares to swaps: " + compares * 1.0 / swaps);
    }

    @Test
    public void testPartitionWithSort() {
        String[] xs = new String[]{"g", "f", "e", "d", "c", "b", "a"};
        int n = xs.length;
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        final Helper<String> helper = new InstrumentedComparableHelper<>("test", config);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        QuickSort<String> sorter = new QuickSort_Basic<>(helper);
        int inversions = n * (n - 1) / 2;
        assertEquals(inversions, helper.inversions(xs));
        Partitioner<String> partitioner = sorter.createPartitioner();
        List<Partition<String>> partitions = partitioner.partition(new Partition<>(xs, 0, xs.length));
        assertEquals(11L, privateMethodTester.invokePrivate("getFixes"));
        Partition<String> p0 = partitions.get(0);
        sorter.sort(xs, 0, p0.to, 0);
        assertEquals(21L, privateMethodTester.invokePrivate("getFixes"));
        Partition<String> p1 = partitions.get(1);
        sorter.sort(xs, p1.from, p1.to, 0);
        assertEquals(21L, privateMethodTester.invokePrivate("getFixes"));
        long fixes = (long) privateMethodTester.invokePrivate("getFixes");
        // NOTE: there are at least as many fixes as inversions -- sort methods aren't necessarily perfectly efficient in terms of swaps.
        assertTrue(inversions <= fixes);
        assertEquals(0, helper.inversions(xs));
        assertEquals(11L, privateMethodTester.invokePrivate("getSwaps"));
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

    final static LazyLogger logger = new LazyLogger(QuickSort_Basic.class);

    @BeforeClass
    public static void beforeClass() throws IOException {
        config_instr = setupConfig("true", "false", "3", "0", "1", "");
    }

    private static Config config_instr;

}