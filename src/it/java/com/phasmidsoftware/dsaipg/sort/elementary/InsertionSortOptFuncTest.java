/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.HelperFactory;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;
import com.phasmidsoftware.dsaipg.util.*;
import org.junit.Test;

import static com.phasmidsoftware.dsaipg.sort.BaseComparatorHelper.INVERSIONS;
import static com.phasmidsoftware.dsaipg.sort.Instrument.COMPARES;
import static com.phasmidsoftware.dsaipg.sort.Instrument.FIXES;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class InsertionSortOptFuncTest {
    @Test
    public void sort2() throws Exception {
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        int n = 10000;
        Helper<Integer> helper = HelperFactory.create("InsertionSortopt", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        Integer[] xs = helper.random(Integer.class, r -> r.nextInt(n * 100));
        SortWithHelper<Integer> sorter = new InsertionSortOpt<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        // NOTE: these are suppoed to match within about 12%.
        // Since we set a specific seed, this should always succeed.
        // If we use true random seed and this test fails, just increase the delta a little.
        double expectedCompares = n * Utilities.lg(n / 2);
        assertEquals(1.0, compares / expectedCompares, 0.12);
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        System.out.println(statPack);
        // NOTE: it is possible that there might be slightly more fixes than inversions.
        // This happens only with the optimized version and then only when the xs values are non-distinct.
        // It's because the binary search mechanism can overshoot slightly when there are duplicates.
        assertTrue(fixes - inversions <= n / 100);
    }

    final static LazyLogger logger = new LazyLogger(InsertionSortOpt.class);

}