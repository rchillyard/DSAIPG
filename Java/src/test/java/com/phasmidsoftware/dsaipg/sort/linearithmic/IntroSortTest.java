/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.generic.Sort;
import com.phasmidsoftware.dsaipg.sort.generic.SortWithHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.HelperFactory;
import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import com.phasmidsoftware.dsaipg.util.benchmark.StatPack;
import com.phasmidsoftware.dsaipg.util.config.Config;
import com.phasmidsoftware.dsaipg.util.logging.LazyLogger;
import org.junit.Test;

import java.util.List;

import static com.phasmidsoftware.dsaipg.sort.helper.Instrument.*;
import static com.phasmidsoftware.dsaipg.util.config.ConfigTest.INVERSIONS;
import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static com.phasmidsoftware.dsaipg.util.general.Utilities.round;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

@SuppressWarnings("ALL")
public class IntroSortTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * The sub-array entry point must do the same <b>work</b> as the whole-array one. Added
     * 2026-09-30, when {@code sort(xs, from, to)} was found to pass the depth <i>limit</i> where
     * the <i>current depth</i> belongs, so that it either never heapsorted (called on its own,
     * with depthThreshold still Integer.MAX_VALUE) or heapsorted everything on the first call
     * (after sort(X[], boolean) had set depthThreshold to the value it was also passing as the
     * depth).
     * <p>
     * <b>Both produced correct output</b>, which is why nothing caught it -- and why this test
     * counts compares rather than checking the answer. An Introsort whose depth fallback never
     * fires is a plain quicksort; one that fires immediately is a plain heapsort. The input is
     * organ-pipe, which defeats a naive quicksort and so drives the recursion past the limit.
     */
    @Test
    public void subArrayEntryPointDoesTheSameWorkAsTheWholeArrayEntryPoint() throws Exception {
        int n = 20000;
        Integer[] organPipe = new Integer[n];
        for (int i = 0; i < n; i++) organPipe[i] = i < n / 2 ? i : n - i;
        Integer[] expected = java.util.Arrays.copyOf(organPipe, n);
        java.util.Arrays.sort(expected);

        long viaWhole = comparesFor(organPipe, expected, false, false);
        long viaSub = comparesFor(organPipe, expected, true, false);
        long viaSubReused = comparesFor(organPipe, expected, true, true);

        assertEquals("the sub-array entry point must do the same work as the whole-array one",
                viaWhole, viaSub);
        assertEquals("and must not be poisoned by an earlier whole-array sort on the same instance",
                viaWhole, viaSubReused);
    }

    /**
     * @param subArray true to go through sort(xs, from, to) rather than sort(xs, makeCopy).
     * @param reuse    true to run a whole-array sort on the same instance first, which is what
     *                 used to set depthThreshold and poison the sub-array path.
     * @return the number of compares, having first checked the answer is right.
     */
    private long comparesFor(Integer[] input, Integer[] expected, boolean subArray, boolean reuse) throws Exception {
        Config config = setupConfig("true", "false", "0", "1", "", "");
        Helper<Integer> helper = HelperFactory.create("IntroSort", input.length, config);
        IntroSort<Integer> sorter = new IntroSort<>(helper);
        if (reuse) sorter.sort(java.util.Arrays.copyOf(input, input.length), false);
        helper.init(input.length);
        Integer[] xs = java.util.Arrays.copyOf(input, input.length);
        if (subArray) sorter.sort(xs, 0, xs.length);
        else sorter.sort(xs, false);
        assertArrayEquals("output must be sorted whichever entry point is used", expected, xs);
        return helper.getCompares();
    }

    @Test
    public void testSort1() throws Exception {
        Integer[] xs = new Integer[4];
        xs[0] = 3;
        xs[1] = 4;
        xs[2] = 2;
        xs[3] = 1;
        Sort<Integer> s = new IntroSort<>(Config.load(getClass()));
        Integer[] ys = s.sort(xs);
        assertEquals(Integer.valueOf(1), ys[0]);
        assertEquals(Integer.valueOf(2), ys[1]);
        assertEquals(Integer.valueOf(3), ys[2]);
        assertEquals(Integer.valueOf(4), ys[3]);
    }

    @Test
    public void testSortDetailed1() throws Exception {
        int k = 7;
        int N = (int) Math.pow(2, k);
        // NOTE this depends on the cutoff value for quick sort.
        int levels = k - 2;
        final Config config = setupConfig("true", "false", "0", "1", "", "");
        final Helper<Integer> helper = HelperFactory.create("intro sort", N, config);
        System.out.println(helper);
        Sort<Integer> s = new IntroSort<>(helper);
        s.init(N);
        final Integer[] xs = new Integer[N];
        for (int i = 0; i < N; i++) xs[i] = i;
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
        assertEquals(13, helper.maxDepth());
    }

    @Test
    public void testSortDetailed2() throws Exception {
        int k = 7;
        int N = (int) Math.pow(2, k);
        // NOTE this depends on the cutoff value for quick sort.
        int levels = k - 2;
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        final Helper<Integer> helper = HelperFactory.create("intro sort", N, config);
        System.out.println(helper);
        Sort<Integer> s = new IntroSort<>(helper);
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
        assertEquals(4, helper.maxDepth());
        final long worstCompares = round(2.0 * N * Math.log(N));
        System.out.println("compares: " + compares + ", worstCompares: " + worstCompares);
        assertTrue(compares <= worstCompares);
        assertTrue(inversions <= fixes);
    }

    @Test
    public void testHeapSort() throws Exception {
        SortWithHelper<Integer> sorter = new IntroSort<>(Config.load(getClass()));
        PrivateMethodTester t = new PrivateMethodTester(sorter);
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Class[] classes = {Comparable[].class, int.class, int.class};
        t.invokePrivateExplicit("heapSort", classes, xs, 0, xs.length);
        assertTrue(sorter.getHelper().isSorted(xs));
    }

    @Test
    public void testInsertionSort() throws Exception {
        SortWithHelper<Integer> sorter = new IntroSort<>(Config.load(getClass()));
        PrivateMethodTester t = new PrivateMethodTester(sorter);
        Integer[] xs = {15, 3, -1, 2, 4, 1, 0, 5, 8, 6, 1, 9, 17, 7, 11};
        Sort<Integer> insertionSort = (Sort<Integer>) t.invokePrivate("getInsertionSort");
        insertionSort.sort(xs, 0, xs.length);
        assertTrue(sorter.getHelper().isSorted(xs));
    }

    @Test
    public void testPartition() throws Exception {
        String testString = "PABXWPPVPDPCYZ";
        char[] charArray = testString.toCharArray();
        Character[] array = new Character[charArray.length];
        for (int i = 0; i < array.length; i++) array[i] = charArray[i];
        Sort<Character> s = new IntroSort<>(Config.load(getClass()));
        Partition<Character> partition = new Partition<>(array, 0, array.length);
        List<Partition<Character>> partitions = ((QuickSort<Character>) s).partitioner.partition(partition);
        assertEquals(0, partitions.get(0).from);
        assertEquals(4, partitions.get(0).to);
        assertEquals(5, partitions.get(1).from);
        assertEquals(13, partitions.get(1).to);
        assertEquals(14, partitions.get(2).from);
        assertEquals(array.length, partitions.get(2).to);
        assertEquals(Character.valueOf('C'), array[0]);
        assertEquals(Character.valueOf('Z'), array[array.length - 1]);
    }

    final static LazyLogger logger = new LazyLogger(IntroSort.class);


}