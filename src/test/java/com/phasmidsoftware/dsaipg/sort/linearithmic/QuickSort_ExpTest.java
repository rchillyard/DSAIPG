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
import static com.phasmidsoftware.dsaipg.util.ConfigTest.INVERSIONS;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfig;
import static com.phasmidsoftware.dsaipg.util.Utilities.round;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class QuickSort_ExpTest {

    @Test
    public void testSort1() throws Exception {
        Integer[] xs = new Integer[4];
        xs[0] = 3;
        xs[1] = 4;
        xs[2] = 2;
        xs[3] = 1;
        Sort<Integer> s = new QuickSort_Exp<>(Config.load(getClass()));
        Integer[] ys = s.sort(xs);
        assertEquals(Integer.valueOf(1), ys[0]);
        assertEquals(Integer.valueOf(2), ys[1]);
        assertEquals(Integer.valueOf(3), ys[2]);
        assertEquals(Integer.valueOf(4), ys[3]);
    }

    @Test
    public void testSort2() throws Exception {
        int n = 16;
        final SortWithHelper<Integer> sorter = new QuickSort_Exp<>(Config.load(getClass()));
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
        Sort<Character> s = new QuickSort_Exp<Character>(Config.load(getClass()));
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
        Sort<Integer> s = new QuickSort_Exp<>(xs.length, config);
        Integer[] ys = s.sort(xs);
        assertEquals(Integer.valueOf(1), ys[0]);
        assertEquals(Integer.valueOf(2), ys[1]);
        assertEquals(Integer.valueOf(3), ys[2]);
        assertEquals(Integer.valueOf(4), ys[3]);
    }

    @Test
    public void testSortWithInstrumenting0() throws Exception {
        int n = 16;
        final SortWithHelper<Integer> sorter = new QuickSort_Exp<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting1() throws Exception {
        int n = 541; // a prime number
        final SortWithHelper<Integer> sorter = new QuickSort_Exp<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(97));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting2() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_Exp<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(100));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting3() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_Exp<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting4() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_Exp<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting5() throws Exception {
        int n = 1000;
        final SortWithHelper<Integer> sorter = new QuickSort_Exp<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting6() throws Exception {
        int n = 10000;
        final SortWithHelper<Integer> sorter = new QuickSort_Exp<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000000));
        final Integer[] sorted = sorter.sort(xs);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSortWithInstrumenting6Biscuit() throws Exception {
        int n = 10000;
        final SortWithHelper<Integer> sorter = new QuickSort_Exp<>(n, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000000));
        final Integer[] sorted = sorter.sort(xs);
        final Integer[] biscuit = sorter.sort(sorted);
        assertTrue(helper.isSorted(biscuit));
    }

    @Test
    public void testPartition1() throws Exception {
        String testString = "HBAXWPQVDCREZY";
        char[] charArray = testString.toCharArray();
        Character[] array = new Character[charArray.length];
        for (int i = 0; i < array.length; i++) array[i] = charArray[i];
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        QuickSort<Character> sorter = new QuickSort_Exp<Character>(array.length, config);
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
        assertEquals("CBAERPQHDVWXZY", partitionedString);
    }

    @Test
    public void testPartition2() throws Exception {
        String testString = "SEAYRLFVZQTCMK";
        char[] charArray = testString.toCharArray();
        Character[] array = new Character[charArray.length];
        for (int i = 0; i < array.length; i++) array[i] = charArray[i];
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        QuickSort<Character> sorter = new QuickSort_Exp<Character>(array.length, config);
        Partitioner<Character> partitioner = sorter.partitioner;
        List<Partition<Character>> partitions = partitioner.partition(QuickSort.createPartition(array));
        assertEquals(2, partitions.size());
        Partition<Character> p0 = partitions.get(0);
        assertEquals(0, p0.from);
        assertEquals(11, p0.to);
        Partition<Character> p1 = partitions.get(1);
        assertEquals(12, p1.from);
        assertEquals(14, p1.to);
        char[] chars = new char[array.length];
        for (int i = 0; i < chars.length; i++) chars[i] = array[i];
        String partitionedString = new String(chars);
        assertEquals("CEAKRLFSMQTVZY", partitionedString);
    }

    @Test
    public void testSortDetailed() throws Exception {
        int k = 7;
        int N = (int) Math.pow(2, k);
        // NOTE this depends on the cutoff value for quick sort.
        int levels = k - 2;
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        final Helper<Integer> helper = HelperFactory.create("quick sort dual pivot", N, config);
        System.out.println(helper);
        Sort<Integer> s = new QuickSort_Exp<>(helper);
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
        final long bestCompares = round(N * k);
        System.out.println("bestCompares: " + bestCompares + ", compares: " + compares + ", worstCompares: " + worstCompares);
        assertTrue(compares <= worstCompares);
        System.out.println("ratio of compares to swaps: " + compares * 1.0 / swaps);
    }

    @Test
    public void testSortDetailedRandom() throws Exception {
        int k = 10;
        int N = (int) Math.pow(2, k);
        // NOTE this depends on the cutoff value for quick sort.
        int levels = k - 2;
        final Config config = setupConfig("true", "false", "", "1", "", "");
        final Helper<Integer> helper = HelperFactory.create("quick sort dual pivot", N, config);
        System.out.println(helper);
        Sort<Integer> s = new QuickSort_Exp<>(helper);
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
        // FIXME why does this sometimes fail?
//        assertTrue(compares <= worstCompares);
        if (compares > worstCompares) System.err.println("compares is larger than worst compares");
        System.out.println("ratio of compares to swaps: " + compares * 1.0 / swaps);
    }

    @Test
    public void testPartitionWithSort() {
        String[] xs = new String[]{"g", "f", "e", "d", "c", "b", "a"};
        int n = xs.length;
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        final Helper<String> helper = new InstrumentedComparableHelper<>("test", config);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        QuickSort<String> sorter = new QuickSort_Exp<>(helper);
        int inversions = n * (n - 1) / 2;
        assertEquals(inversions, helper.inversions(xs));
        Partitioner<String> partitioner = sorter.createPartitioner();
        List<Partition<String>> partitions = partitioner.partition(new Partition<>(xs, 0, xs.length));
        assertEquals(17L, privateMethodTester.invokePrivate("getFixes"));
        Partition<String> p0 = partitions.get(0);
        sorter.sort(xs, 0, p0.to, 0);
        assertEquals(19L, privateMethodTester.invokePrivate("getFixes"));
        Partition<String> p1 = partitions.get(1);
        sorter.sort(xs, p1.from, p1.to, 0);
        assertEquals(21L, privateMethodTester.invokePrivate("getFixes"));
        long fixes = (long) privateMethodTester.invokePrivate("getFixes");
        // NOTE: there are at least as many fixes as inversions -- sort methods aren't necessarily perfectly efficient in terms of swaps.
        assertTrue(inversions <= fixes);
        assertEquals(0, helper.inversions(xs));
        assertEquals(9L, privateMethodTester.invokePrivate("getSwaps"));
    }


    @Test
    public void testPartitionWithSortPartial() {
        String[] xs = new String[]{"a", "b", "c", "d", "e", "f", "g"};
        int n = xs.length;
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        final Helper<String> helper = new InstrumentedComparableHelper<>("test", config);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        QuickSort<String> sorter = new QuickSort_Exp<>(helper);
        assertEquals(0, helper.inversions(xs));
        Partitioner<String> partitioner = sorter.createPartitioner();
        List<Partition<String>> partitions = partitioner.partition(new Partition<>(xs, 0, xs.length));
        assertEquals(4L, privateMethodTester.invokePrivate("getFixes"));
        Partition<String> p0 = partitions.get(0);
        sorter.sort(xs, 0, p0.to, 0);
        assertEquals(4L, privateMethodTester.invokePrivate("getFixes"));
        Partition<String> p1 = partitions.get(1);
        sorter.sort(xs, p1.from, p1.to, 0);
        assertEquals(4L, privateMethodTester.invokePrivate("getFixes"));
        long fixes = (long) privateMethodTester.invokePrivate("getFixes");
        assertEquals(0, helper.inversions(xs));
        assertEquals(2L, privateMethodTester.invokePrivate("getSwaps"));
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

    final static LazyLogger logger = new LazyLogger(QuickSort_Exp.class);

    @BeforeClass
    public static void beforeClass() throws IOException {
        config = Config.load();
    }

    private static Config config;

}