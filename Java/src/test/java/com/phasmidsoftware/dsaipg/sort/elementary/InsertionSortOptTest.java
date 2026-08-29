/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.generic.Sort;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.HelperFactory;
import com.phasmidsoftware.dsaipg.sort.helper.Instrument;
import com.phasmidsoftware.dsaipg.sort.helper.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import com.phasmidsoftware.dsaipg.util.benchmark.StatPack;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.config.ConfigTest;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.phasmidsoftware.dsaipg.sort.helper.BaseComparatorHelper.INVERSIONS;
import static com.phasmidsoftware.dsaipg.sort.helper.Instrument.*;
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

@SuppressWarnings("ALL")
public class InsertionSortOptTest {
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
        int n = xs.length;
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
        // NOTE 7, not 2n. One hit per element read, plus one per probe of the
        // binary search. The probe count changed when swapIntoSorted became
        // stable: it now searches for the upper bound rather than stopping at
        // the first equal element. 2n was a coincidence of the old search, not
        // a property of the algorithm.
        assertEquals(7, hits);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        // NOTE 4, not 5. One comparison per probe of the binary search, over
        // ranges of size 1, 2 and 3: 1 + 1 + 2. The stable search probes
        // differently from the old one -- see the note on hits above.
        assertEquals(4, compares);
        final int lookups = (int) statPack.getStatistics(LOOKUPS).mean();
        assertEquals(n, lookups); // CONSIDER this might be too optimistic in real life.
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
        int n = xs.length;
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
        // Sorting {3, 4, 2, 1}, the accesses are:
        //   i=1: one read, one probe                              =  2
        //   i=2: one read, two probes, then a move of 2 (1 + 2*2) =  8
        //   i=3: one read, two probes, then a move of 3 (1 + 2*3) = 10
        // NOTE copyBlock charges 2n for a same-array block move, not n + 1,
        // because 2n is what such a move costs: the two moves above are 4 and 6.
        assertEquals(20, hits);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        // NOTE n + 1, not n, since swapIntoSorted became stable -- see sort0.
        assertEquals(n + 1, compares);
        final int lookups = (int) statPack.getStatistics(LOOKUPS).mean();
        assertEquals(n, lookups);
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

    @Test
    public void testSort100() {
        final Config config = setupConfig("true", "false", "3", "1", "", "").copy(Instrument.INSTRUMENTING, FIXES, "true");
        int n = 257;
        double logNminus1 = 8.0;
        Helper<Integer> helper = HelperFactory.create("InsertionSortOpt", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000));
        SortWithHelper<Integer> sorter = new InsertionSortOpt<>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(COMPARES).mean();
        double expectedCompares = logNminus1 * (n - 1) - 1.44 * n + 0.5 * logNminus1 + 1.33; // lg (n-1)!
        // lg (n-1)! is the information-theoretic minimum: no comparison sort can
        // beat it, so it is a lower bound.
        // A STABLE binary insertion sort sits a little above it. It searches for
        // the upper bound of the run of equal elements, so it cannot stop early
        // when it happens to land on one -- and that early exit is exactly what
        // made the old, unstable version match the bound so closely. The price
        // of stability here is under 1.5% more comparisons.
        // Asserted as a band rather than a point, because the exact figure
        // depends on how many duplicates the seed happens to produce.
        assertTrue("must not beat the information-theoretic minimum: " + compares,
                compares >= expectedCompares);
        assertTrue("a stable binary insertion sort should stay close to it: " + compares,
                compares <= expectedCompares * 1.015);
        final int inversions = (int) statPack.getStatistics(ConfigTest.INVERSIONS).mean();
        final int fixes = (int) statPack.getStatistics(FIXES).mean();
        System.out.println(statPack);
        // The fixes are exactly the inversions, with no fudge factor -- which is
        // the check that swapIntoSorted is stable. An unstable one moves each
        // element past the ones equal to it, and every such pointless move is
        // counted as an inversion fixed, so the two stop agreeing.
        assertEquals(inversions, fixes);

    }

    final static LazyLogger logger = new LazyLogger(InsertionSortOpt.class);


    /**
     * A value whose ordering ignores its tag, so that two elements can compare
     * equal while remaining distinguishable.
     */
    record Tagged(int key, String tag) implements Comparable<Tagged> {
        public int compareTo(Tagged o) {
            return Integer.compare(key, o.key);
        }
    }

    /**
     * Insertion sort is stable, and the optimised version must be too.
     * <p>
     * It was not: swapIntoSorted placed each element BEFORE the run of elements
     * equal to it rather than after, so equal elements came out in reverse order
     * -- and each was moved past for no reason. Helper describes the method as
     * "a stable swap using half-exchanges", so this was a defect, not a trade.
     */
    @Test
    public void testStability() {
        Tagged[] xs = {new Tagged(1, "a"), new Tagged(0, "b"), new Tagged(1, "c"),
                new Tagged(0, "d"), new Tagged(1, "e")};
        Config config = setupConfig("false", "", "0", "0", "", "");
        Helper<Tagged> helper = HelperFactory.create("stability", xs.length, config);
        try (InsertionSortOpt<Tagged> sorter = new InsertionSortOpt<>(helper)) {
            sorter.sort(xs, 0, xs.length);
        }
        StringBuilder tags = new StringBuilder();
        for (Tagged x : xs) tags.append(x.tag());
        assertEquals("equal keys must keep the order they came in", "bdace", tags.toString());
    }

    /**
     * Every element moved is one inversion fixed, and no element is moved that
     * need not be -- so the number of copies is exactly the number of inversions,
     * whether or not the input has duplicates.
     * <p>
     * Before swapIntoSorted was made stable this failed badly when duplicates were
     * common: 10116 copies against 8911 inversions, because each element was moved
     * past the ones equal to it.
     */
    @Test
    public void testCopiesEqualInversions() {
        for (int distinct : new int[]{10000, 10}) {
            Random random = new Random(11);
            Integer[] xs = new Integer[200];
            for (int i = 0; i < xs.length; i++) xs[i] = random.nextInt(distinct);
            long inversions = InsertionSortComparator.countInversions(xs.clone(), Integer::compare);
            Config config = setupConfig("true", "false", "0", "0", "", "");
            Helper<Integer> helper = HelperFactory.create("copies", xs.length, config);
            try (InsertionSortOpt<Integer> sorter = new InsertionSortOpt<>(helper)) {
                sorter.sort(xs.clone(), 0, xs.length);
            }
            assertEquals("with values drawn from 0.." + (distinct - 1),
                    inversions, helper.getCopies());
        }
    }
}