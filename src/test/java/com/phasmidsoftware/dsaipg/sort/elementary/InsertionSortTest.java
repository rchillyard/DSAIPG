/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.*;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.LazyLogger;
import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import com.phasmidsoftware.dsaipg.util.StatPack;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.phasmidsoftware.dsaipg.sort.Instrument.*;
import static com.phasmidsoftware.dsaipg.util.ConfigTest.INVERSIONS;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfig;
import static org.junit.Assert.*;

@SuppressWarnings("ALL")
public class InsertionSortTest {

    @Test
    public void sort0() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        Integer[] xs = list.toArray(new Integer[0]);
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        Helper<Integer> helper = HelperFactory.create("InsertionSort", list.size(), config);
        helper.init(list.size());
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        SortWithHelper<Integer> sorter = new InsertionSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        assertEquals(list.size() - 1, compares);
        final int hits = (int) statPack.getStatistics(HITS).mean();
        assertEquals(4, hits);
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        assertEquals(0, inversions);
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        assertEquals(inversions, fixes);
    }

    @Test
    public void sort0a() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(4);
        list.add(3);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        int n = list.size();
        Helper<Integer> helper = HelperFactory.create("InsertionSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        SortWithHelper<Integer> sorter = new InsertionSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        assertEquals(n * (n - 1) / 2, compares);
        final int hits = (int) statPack.getStatistics(HITS).mean();
        assertEquals(19, hits); // TODO check this
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        assertEquals(n * (n - 1) / 2, inversions);
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        assertEquals(inversions, fixes);
    }

    @Test
    public void sort0b() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        int n = list.size();
        Helper<Integer> helper = HelperFactory.create("InsertionSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        SortWithHelper<Integer> sorter = new InsertionSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        assertEquals(n * (n - 1) / 2, compares);
        final int hits = (int) statPack.getStatistics(HITS).mean();
        assertEquals(10, hits);
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        assertEquals(n * (n - 1) / 2, inversions);
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        assertEquals(inversions, fixes);
    }

    @Test
    public void sort0c() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        int n = list.size();
        Helper<Integer> helper = HelperFactory.create("InsertionSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        SortWithHelper<Integer> sorter = new InsertionSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        assertEquals(n * (n - 1) / 2, compares);
        final int hits = (int) statPack.getStatistics(HITS).mean();
        assertEquals(4, hits); // xs[1], xs[0],
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        assertEquals(n * (n - 1) / 2, inversions);
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        assertEquals(inversions, fixes);
    }

    @Test
    public void sort1() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("InsertionSort", xs.length, Config.load(InsertionSortTest.class));
        Sort<Integer> sorter = new InsertionSort<Integer>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        System.out.println(sorter.toString());
    }

    @Test
    public void testMutatingInsertionSort() throws IOException {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("InsertionSort", xs.length, Config.load(InsertionSortTest.class));
        Sort<Integer> sorter = new InsertionSort<Integer>(helper);
        sorter.mutatingSort(xs);
        assertTrue(helper.isSorted(xs));
    }

    @Test
    public void testStaticInsertionSort() throws IOException {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        InsertionSort.sort(xs);
        assertTrue(xs[0] < xs[1] && xs[1] < xs[2] && xs[2] < xs[3]);
    }

    @Test
    public void sort2() throws Exception {
        final Config config = setupConfig("true", "false", "0", "1", "", "").copy(Instrument.INSTRUMENTING, FIXES, "true");
        int n = 100;
        Helper<Integer> helper = HelperFactory.create("InsertionSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000));
        SortWithHelper<Integer> sorter = new InsertionSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        // NOTE: these are suppoed to match within about 12%.
        // Since we set a specific seed, this should always succeed.
        // If we use true random seed and this test fails, just increase the delta a little.
        assertEquals(1.0, 4.0 * compares / n / (n - 1), 0.12);
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        System.out.println(statPack);
        assertEquals(inversions, fixes);
    }

    @Test
    public void sort3() throws Exception {
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        int n = 100;
        Helper<Integer> helper = HelperFactory.create("InsertionSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        Integer[] xs = new Integer[n];
        for (int i = 0; i < n; i++) xs[i] = n - i;
        SortWithHelper<Integer> sorter = new InsertionSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        // NOTE: these are suppoed to match within about 12%.
        // Since we set a specific seed, this should always succeed.
        // If we use true random seed and this test fails, just increase the delta a little.
        assertEquals(4950, compares);
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        System.out.println(statPack);
        assertEquals(inversions, fixes);
    }

    @Test
    public void sort4a() throws Exception {
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        Integer[] xs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int n = xs.length;
        InstrumentedComparatorHelper<Integer> helper = new InstrumentedComparableHelper<>("test insertion sort", n, config);
        helper.init(n);
        Sort<Integer> sorter = new InsertionSort<Integer>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        Instrument instrumenter = helper.instrumenter;
        assertEquals(n - 1, instrumenter.getCompares());
        assertEquals(0L, instrumenter.getFixes());
        assertEquals(0L, instrumenter.getSwaps());
        assertEquals(n, instrumenter.getHits());
        assertEquals(2 * (n - 1), instrumenter.getLookups());
    }

    @Test
    public void sort4b() throws Exception {
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        Integer[] ordered = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Integer[] xs = {7, 0, 8, 2, 4, 5, 6, 9, 3, 1};
        int n = xs.length;
        InstrumentedComparatorHelper<Integer> helper = new InstrumentedComparableHelper<>("test insertion sort", n, config);
        helper.init(n);
        Sort<Integer> sorter = new InsertionSort<Integer>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        assertArrayEquals(ordered, ys);
        Instrument instrumenter = helper.instrumenter;
        assertEquals(31, instrumenter.getCompares());
        assertEquals(23, instrumenter.getFixes());
        assertEquals(23, instrumenter.getSwaps());
        assertEquals(78, instrumenter.getHits());
        assertEquals(62, instrumenter.getLookups());
    }

    final static LazyLogger logger = new LazyLogger(InsertionSort.class);

}