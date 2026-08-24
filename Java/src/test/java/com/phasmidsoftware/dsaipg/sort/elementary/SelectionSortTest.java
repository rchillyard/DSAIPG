/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.generic.Sort;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.HelperFactory;
import com.phasmidsoftware.dsaipg.sort.helper.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import com.phasmidsoftware.dsaipg.util.benchmark.StatPack;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.phasmidsoftware.dsaipg.sort.helper.BaseComparatorHelper.INVERSIONS;
import static com.phasmidsoftware.dsaipg.sort.helper.Instrument.*;
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

@SuppressWarnings("ALL")
public class SelectionSortTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void sort0() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        Integer[] xs = list.toArray(new Integer[0]);
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        int n = list.size();
        Helper<Integer> helper = HelperFactory.create("SelectionSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        SortWithHelper<Integer> sorter = new SelectionSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        sorter.postProcess(ys);
        assertTrue(helper.isSorted(ys));
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        assertEquals(n * (n - 1) / 2, compares);
        final int swaps = (int) statPack.getStatistics(SWAPS).mean();
        assertEquals(0, swaps);
        final int hits = (int) statPack.getStatistics(HITS).mean();
        assertEquals(n * (n - 1) / 2 + n - 1 + swaps * 4, hits);
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        assertEquals(0L, inversions);
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        assertEquals(inversions, fixes);
    }

    @Test
    public void sort0a() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(2);
        list.add(4);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        int n = list.size();
        Helper<Integer> helper = HelperFactory.create("SelectionSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        SortWithHelper<Integer> sorter = new SelectionSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        sorter.postProcess(ys);
        assertTrue(helper.isSorted(ys));
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        assertEquals(n * (n - 1) / 2, compares);
        final int swaps = (int) statPack.getStatistics(SWAPS).mean();
        assertTrue(swaps < n);
        final int hits = (int) statPack.getStatistics(HITS).mean();
        assertEquals(n * (n - 1) / 2 + n - 1 + swaps * 4, hits);
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        assertEquals(4L, inversions);
    }

    @Test
    public void sort1() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("SelectionSort", xs.length, Config.load(SelectionSortTest.class));
        Sort<Integer> sorter = new SelectionSort<Integer>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
    }

    @Test
    public void sort2() throws Exception {
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        int n = 100;
        Helper<Integer> helper = HelperFactory.create("SelectionSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000));
        SortWithHelper<Integer> sorter = new SelectionSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        sorter.postProcess(ys);
        assertTrue(helper.isSorted(ys));
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        assertEquals(n * (n - 1) / 2, compares);
        final int swaps = (int) statPack.getStatistics(SWAPS).mean();
        assertTrue(swaps < n);
        final int hits = (int) statPack.getStatistics(HITS).mean();
        assertEquals(n * (n - 1) / 2 + n - 1 + swaps * 4, hits);
        final int lookups = (int) statPack.getStatistics(LOOKUPS).mean();
        assertEquals(n, lookups);
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        System.out.println(statPack);
        assertEquals(inversions, fixes);
    }

    final static LazyLogger logger = new LazyLogger(SelectionSort.class);

}