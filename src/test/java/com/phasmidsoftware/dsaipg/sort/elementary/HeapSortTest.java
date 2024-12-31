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
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class HeapSortTest {

    @Test
    public void sortWithRange() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(3);
        list.add(8);
        list.add(6);
        list.add(2);
        Integer[] xs = list.toArray(new Integer[0]);
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("HeapSort", xs.length, Config.load(HeapSortTest.class));
        HeapSort<Integer> sorter = new HeapSort<>(helper);
        sorter.sort(xs, 0, list.size());
        assertTrue(helper.isSorted(xs));
    }

    @Test
    public void sortWithEmptyArray() throws Exception {
        Integer[] xs = new Integer[0];
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("HeapSort", xs.length, Config.load(HeapSortTest.class));
        HeapSort<Integer> sorter = new HeapSort<>(helper);
        sorter.sort(xs, 0, 0);
        assertTrue(helper.isSorted(xs));
    }

    @Test
    public void sortWithNullArray() throws IOException {
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("HeapSort", 0, Config.load(HeapSortTest.class));
        HeapSort<Integer> sorter = new HeapSort<>(helper);
        sorter.sort(null, 0, 0);
        // No exception should occur, and behavior should be a no-op.
    }

    @Test
    public void sortUnsortedArray() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(1);
        list.add(4);
        list.add(2);
        list.add(3);
        Integer[] xs = list.toArray(new Integer[0]);
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("HeapSort", xs.length, Config.load(HeapSortTest.class));
        HeapSort<Integer> sorter = new HeapSort<>(helper);
        sorter.sort(xs, 0, xs.length);
        assertTrue(helper.isSorted(xs));
    }

    @Test
    public void sort0() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        Integer[] xs = list.toArray(new Integer[0]);
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        Helper<Integer> helper = HelperFactory.create("HeapSort", list.size(), config);
        helper.init(list.size());
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        SortWithHelper<Integer> sorter = new HeapSort<Integer>(helper);
        sorter.preProcess(xs);
//        System.out.println(Arrays.toString(xs));
        Integer[] ys = sorter.sort(xs);
//        System.out.println(Arrays.toString(ys));
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        assertEquals(7, (int) statPack.getStatistics(COMPARES).mean());
        assertEquals(8, (int) statPack.getStatistics(SWAPS).mean());
        assertEquals(46, (int) statPack.getStatistics(HITS).mean());
    }

    @Test
    public void sort1() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("HeapSort", xs.length, Config.load(HeapSortTest.class));
        Sort<Integer> sorter = new HeapSort<Integer>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        System.out.println(sorter.toString());
    }

    @Test
    public void testMutatingHeapSort() throws IOException {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("HeapSort", xs.length, Config.load(HeapSortTest.class));
        Sort<Integer> sorter = new HeapSort<Integer>(helper);
        sorter.mutatingSort(xs);
        assertTrue(helper.isSorted(xs));
    }

    @Test
    public void sort2() throws Exception {
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        int n = 100;
        Helper<Integer> helper = HelperFactory.create("HeapSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000));
        SortWithHelper<Integer> sorter = new HeapSort<Integer>(helper);
        sorter.preProcess(xs);
//        System.out.println(Arrays.toString(xs));
        Integer[] ys = sorter.sort(xs);
//        System.out.println(Arrays.toString(ys));
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        // Since we set a specific seed, this should always succeed.
        assertEquals(1026, compares);
        final int swaps = (int) statPack.getStatistics(SWAPS).mean();
        assertEquals(581, swaps);
        final int hits = (int) statPack.getStatistics(HITS).mean();
        assertEquals(2 * compares + 2 * swaps + 1162, hits); // Why this difference?
    }

    @Test
    public void sort3() throws Exception {
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        int n = 100;
        Helper<Integer> helper = HelperFactory.create("HeapSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        Integer[] xs = new Integer[n];
        for (int i = 0; i < n; i++) xs[i] = n - i;
        SortWithHelper<Integer> sorter = new HeapSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        // Since we set a specific seed, this should always succeed.
        assertEquals(944, compares); // TODO check this.
    }

    final static LazyLogger logger = new LazyLogger(HeapSort.class);

}