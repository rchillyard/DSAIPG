/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.BaseComparableHelperTest;
import com.phasmidsoftware.dsaipg.sort.InstrumentedComparableHelper;
import com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper;
import com.phasmidsoftware.dsaipg.sort.Sort;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.LazyLogger;
import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import com.phasmidsoftware.dsaipg.util.StatPack;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.phasmidsoftware.dsaipg.sort.Instrument.*;
import static com.phasmidsoftware.dsaipg.util.ConfigTest.INVERSIONS;
import static org.junit.Assert.*;

@SuppressWarnings("ALL")
public class ShellSortTest {

    @BeforeClass
    public static void setupClass() {
        try {
            config = Config.load(BaseComparableHelperTest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSedgewick0() throws IOException {
        ShellSort.H h = new ShellSort.H(0, 4);
        assertEquals(1, h.sedgewick(0));
        assertEquals(5, h.sedgewick(1));
        assertEquals(19, h.sedgewick(2));
        assertEquals(41, h.sedgewick(3));
    }

    @Test
    public void testSedgewick50() throws IOException {
        ShellSort.H h = new ShellSort.H(50, 4);
        assertEquals(41, h.first());
        assertEquals(19, h.next());
        assertEquals(5, h.next());
        assertEquals(1, h.next());
        assertEquals(0, h.next());
    }

    @Test
    public void hSortKnuth3() {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {4, 1, -1, 2, 8, 3, 0, 5, 15, 6, 1, 9, 17, 7, 11};
        Sort<Integer> sorter = new ShellSort<>(3, xs.length, 1, config);
        PrivateMethodTester t = new PrivateMethodTester(sorter);
        Class[] classes = {int.class, Comparable[].class, int.class, int.class};
        t.invokePrivateExplicit("hSort", classes, 4, xs, 0, xs.length);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sortKnuth1() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        assertArrayEquals(zs, new ShellSort<Integer>(3, xs.length, 1, config).sort(xs));
    }

    @Test
    public void sortPratt1a() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        assertArrayEquals(zs, new ShellSort<Integer>(5, xs.length, 1, config).sort(xs));
    }

    @Test
    public void sortKnuth2() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        Sort ss = new ShellSort<Integer>(3, xs.length, 1, config);
        ss.sort(xs, 0, xs.length);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sortPratt2a() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        Sort ss = new ShellSort<Integer>(5, xs.length, 1, config);
        ss.sort(xs, 0, xs.length);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sortKnuth3() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {15, -1, 3, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Sort ss = new ShellSort<Integer>(3, xs.length, 1, config);
        ss.sort(xs, 1, 3);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sortKnuth3a() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {15, -1, 3, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Sort ss = new ShellSort<Integer>(5, xs.length, 1, config);
        ss.sort(xs, 1, 3);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sortKnuth4() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 7, 9, 11, 17};
        Sort ss = new ShellSort<Integer>(3, xs.length, 1, config);
        ss.sort(xs, 11, xs.length);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sortPratt4a() throws Exception {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 7, 9, 11, 17};
        Sort ss = new ShellSort<Integer>(5, xs.length, 1, config);
        ss.sort(xs, 11, xs.length);
        assertArrayEquals(zs, xs);
    }

    @Test
    public void sortKnuth7() throws Exception {
        doShellSortTest(1000, 3);
    }

    @Test
    public void sortPratt7a() throws Exception {
        doShellSortTest(1000, 5);
    }

    @Test
    public void sortKnuth5a() throws Exception {
        doShellSortTest(10, 3);
    }

    @Test
    public void sortPratt5b() throws Exception {
        doShellSortTest(10, 5);
    }

    @Test
    public void sortKnuth6a() throws Exception {
        doShellSortTest(100, 3);
    }

    @Test
    public void sortPratt6b() throws Exception {
        doShellSortTest(100, 5);
    }

    @Test
    public void sortInsertionSortH1() {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        assertArrayEquals(zs, new ShellSort<Integer>(1, xs.length, 1, config).sort(xs));
    }

    @Test
    public void sortShellH2() {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        assertArrayEquals(zs, new ShellSort<Integer>(2, xs.length, 1, config).sort(xs));
    }

    @Test
    public void sortPrattH3() {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Integer[] zs = {-1, 0, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 15, 17};
        assertArrayEquals(zs, new ShellSort<Integer>(5, xs.length, 1, config).sort(xs));
    }

    @Test
    public void doShellSortKnuth() {
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        InstrumentedComparatorHelper<Integer> helper = new InstrumentedComparableHelper<>("ShellSort with instrumentation", xs.length, config);
        ShellSort.doShellSort(3, helper, xs);
    }

    @Test
    public void doInstrumentedRandomDoubleShellSortKnuth() throws IOException {
        ShellSort.doRandomDoubleShellSort(3, 1000, 10, Config.load());
    }

    private void doShellSortTest(int N, final int gapSequence) throws IOException {
        final Config config = Config.load(getClass());
        InstrumentedComparatorHelper<Integer> helper = new InstrumentedComparableHelper<>("ShellSort", N, config);
        Integer[] xs = (Integer[]) helper.random(Integer.class, random -> random.nextInt(N * 2));
        helper.init(N);
        helper.preProcess(xs);
        ShellSort<Integer> sorter = new ShellSort<>(gapSequence, helper);
        sorter.setShellFunction((h) -> showInversions(h));
        sorter.mutatingSort(xs);
        helper.postProcess(xs);
        assertTrue(helper.isSorted(xs));
        showStatistics(helper);
        helper.close();
    }

    private void showStatistics(AutoCloseable helper) {
        if (InstrumentedComparableHelper.class.isAssignableFrom(helper.getClass())) {
            StatPack statPack = ((InstrumentedComparatorHelper<Integer>) helper).getStatPack();
            double inversions = statPack.getStatistics(INVERSIONS).mean();
            double compares = statPack.getStatistics(COMPARES).mean();
            double swaps = statPack.getStatistics(SWAPS).mean();
            double fixes = statPack.getStatistics(FIXES).mean();
            System.out.println(inversions + ", " + compares + ", " + swaps + " " + fixes);
        }
    }

    private void showInversions(AutoCloseable helper) {
        if (InstrumentedComparableHelper.class.isAssignableFrom(helper.getClass())) {
            InstrumentedComparatorHelper<Integer> instrumentedHelper = (InstrumentedComparatorHelper<Integer>) helper;
            // TODO this doesn't really make sense.
            System.out.println("inversions: " + instrumentedHelper.inversions(instrumentedHelper.getRandomArray()));
            System.out.println("compares: " + instrumentedHelper.getCompares());
            System.out.println("swaps: " + instrumentedHelper.getSwaps());
        }
    }

    final static LazyLogger logger = new LazyLogger(ShellSort.class);

    private static Config config;
}