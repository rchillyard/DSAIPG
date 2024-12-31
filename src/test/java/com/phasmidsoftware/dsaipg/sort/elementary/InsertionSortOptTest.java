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

import static com.phasmidsoftware.dsaipg.sort.BaseComparatorHelper.INVERSIONS;
import static com.phasmidsoftware.dsaipg.sort.Instrument.*;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class InsertionSortOptTest {

    @Test
    public void sort0() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        Integer[] xs = list.toArray(new Integer[0]);
        final int inversions = 0;
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        Helper<Integer> helper = HelperFactory.create("InsertionSortOpt", list.size(), config);
        helper.init(list.size());
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        SortWithHelper<Integer> sorter = new InsertionSortOpt<>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int hits = (int) statPack.getStatistics(HITS).mean();
        assertEquals(5, hits);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        assertEquals(5, compares);
        final int inversionsFound = (int) statPack.getStatistics(INVERSIONS).mean();
        assertEquals(0L, inversionsFound);
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
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        Helper<Integer> helper = HelperFactory.create("InsertionSortOpt", list.size(), config);
        long inversions = helper.inversions(xs);
        SortWithHelper<Integer> sorter = new InsertionSortOpt<>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        final int hits = (int) statPack.getStatistics(HITS).mean();
        assertEquals(14, hits);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        assertEquals(4, compares);
        final int inversionsFound = (int) statPack.getStatistics(INVERSIONS).mean();
        assertEquals(0L, inversionsFound);
    }

    @Test
    public void testMutatingInsertionSort() throws IOException {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("InsertionSortOpt", xs.length, Config.load(InsertionSortOptTest.class));
        Sort<Integer> sorter = new InsertionSortOpt<>(helper);
        sorter.mutatingSort(xs);
        assertTrue(helper.isSorted(xs));
    }

    final static LazyLogger logger = new LazyLogger(InsertionSortOpt.class);

}