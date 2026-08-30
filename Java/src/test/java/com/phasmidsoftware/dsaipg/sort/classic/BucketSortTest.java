/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.sort.classic;

import com.google.common.collect.ImmutableList;
import com.phasmidsoftware.dsaipg.sort.generic.Sort;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.sort.helper.HelperException;
import com.phasmidsoftware.dsaipg.sort.helper.InstrumentedComparableHelper;
import com.phasmidsoftware.dsaipg.sort.helper.NonInstrumentingComparableHelper;
import com.phasmidsoftware.dsaipg.util.config.Config;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

@SuppressWarnings("ALL")
public class BucketSortTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testSort0() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravo", "Campion", "Able", "Aardvark", "Beetle");
        Sort<String> sorter = new BucketSort<String>(s -> classifyString(s), 3, 5, Config.load(BucketSortTest.class));
        String[] xs = list.toArray(new String[]{});
        sorter.mutatingSort(xs);
        System.out.println(Arrays.toString(xs));
        assertArrayEquals(new String[]{"Aardvark", "Able", "Beetle", "Bravo", "Campion"}, xs);
    }

    @Test
    public void testSort1() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravo", "Campion", "Able", "Aardvark", "Beetle");
        Sort<String> sorter = new BucketSort<String>(BucketSort::classifyStringInitial, BucketSort.ALPHABET_SIZE, 5, Config.load(BucketSortTest.class));
        String[] xs = list.toArray(new String[]{});
        sorter.mutatingSort(xs);
        System.out.println(Arrays.toString(xs));
        assertArrayEquals(new String[]{"Aardvark", "Able", "Beetle", "Bravo", "Campion"}, xs);
    }

    @Test
    public void testSort2() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravo", "Campion", "Able", "Aardvark", "Beetle", "C");
        Sort<String> sorter = new BucketSort<String>(BucketSort::classifyStringDigraph, BucketSort.DIGRAPHS_SIZE, 6, Config.load(BucketSortTest.class));
        String[] xs = list.toArray(new String[]{});
        sorter.mutatingSort(xs);
        System.out.println(Arrays.toString(xs));
        assertArrayEquals(new String[]{"Aardvark", "Able", "Beetle", "Bravo", "C", "Campion"}, xs);
    }

    @Test
    public void testSort3() throws IOException {
        ImmutableList<String> list = ImmutableList.of("bravo", "Campion", "able", "aArdvark", "beetle");
        BucketSort<String> sorter = BucketSort.CaseIndependentBucketSort(BucketSort::classifyStringInitial, BucketSort.ALPHABET_SIZE, 5, Config.load(BucketSortTest.class));
        String[] xs = list.toArray(new String[]{});
        sorter.mutatingSort(xs);
        System.out.println(Arrays.toString(xs));
        assertArrayEquals(new String[]{"aArdvark", "able", "beetle", "bravo", "Campion"}, xs);
    }

    @Test
    public void testSort4() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravo", "Campion", "Able", "Aardvark", "Beetle", "c");
        BucketSort<String> sorter = BucketSort.CaseIndependentBucketSort(BucketSort::classifyStringDigraph, BucketSort.DIGRAPHS_SIZE, 6, Config.load(BucketSortTest.class));
        String[] xs = list.toArray(new String[]{});
        sorter.mutatingSort(xs);
        System.out.println(Arrays.toString(xs));
        assertArrayEquals(new String[]{"Aardvark", "Able", "Beetle", "Bravo", "c", "Campion"}, xs);
    }

    @Test
    public void testSortN() throws Exception {
        int N = 10000;
        Integer[] xs = new Integer[N];
        Random random = new Random();
        for (int i = 0; i < N; i++) xs[i] = random.nextInt(10000);
        Helper<Integer> helper = new NonInstrumentingComparableHelper<>("BucketSort", xs.length, Config.load(BucketSortTest.class));
        Sort<Integer> sorter = new BucketSort<>(null, 100, helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        System.out.println(sorter.toString());
    }

    @Test
    public void testSortInstrumented() throws Exception {
        int N = 10_000;
        final int bound = 20_000;
        int nBuckets = 100;
        final Config config = setupConfig("true", "true", "0", "1", "", "");
        Helper<Integer> helper = new InstrumentedComparableHelper<>("BucketSort", N, config);
        Integer[] xs = helper.random(N, Integer.class, r -> r.nextInt(bound));
        Sort<Integer> sorter = new BucketSort<>(null, nBuckets, helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        System.out.println(sorter.toString());
        assertEquals(2L * N, helper.getCopies());
        assertEquals(261_328L, helper.getCompares());
        assertEquals(803_991L, helper.getHits());
        assertEquals(20_000L, helper.getLookups());
        long inversions = helper.getFixes();
        assertEquals((long) N * N / 4 / nBuckets, inversions, (long) N);
        assertEquals(inversions, helper.getFixes());
    }

    /**
     * init(n) passes the length through to the Helper, which sizes its statistics by
     * it. Re-initialising to the same n is fine; changing it is not.
     */
    @Test
    public void init() throws IOException {
        Config config = Config.load(BucketSortTest.class);
        BucketSort<String> sorter = new BucketSort<>(BucketSortTest::classifyString, 3, 5, config);
        assertEquals("N is passed to the Helper by the constructor", 5, sorter.getHelper().getN());
        sorter.init(5);
        assertEquals("the same n again is harmless", 5, sorter.getHelper().getN());
        assertThrows("a different n is not", HelperException.class, () -> sorter.init(3));
    }

    /**
     * postProcess rejects an array which is not sorted, since checksorted is true in
     * test/resources/config.ini -- a test must not pass on a sort which did not sort.
     */
    @Test
    public void postProcess() throws IOException {
        Config config = Config.load(BucketSortTest.class);
        BucketSort<String> sorter = new BucketSort<>(BucketSortTest::classifyString, 3, 5, config);
        // NOTE N -- the constructor's third argument -- has already initialised the
        // Helper to 5, and re-initialising to a different value is rejected.
        sorter.postProcess(new String[]{"Able", "Bravo", "Campion"});
        assertThrows(HelperException.class,
                () -> sorter.postProcess(new String[]{"Campion", "Able", "Bravo"}));
    }

    /**
     * close() is idempotent: a second call does nothing rather than closing the
     * Helper twice.
     */
    @Test
    public void close() throws IOException {
        Config config = Config.load(BucketSortTest.class);
        BucketSort<String> sorter = new BucketSort<>(BucketSortTest::classifyString, 3, 5, config);
        sorter.close();
        sorter.close();
    }

    private static Integer classifyString(String s) {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(s.toUpperCase().charAt(0));
    }

    @Test
    public void classifyStringInitial() {
        String input = "Alpha";
        int classification = BucketSort.classifyStringInitial(input);
        assertEquals(1, classification); // 'A' is the 0th letter but the alphabet starts with a space.
    }

    @Test
    public void classifyStringDigraph() {
        String input = "Bravo";
        int classification = BucketSort.classifyStringDigraph(input);
        assertTrue(classification >= 0); // Ensure classification is valid and within the expected range
    }

    @Test
    public void sort() throws IOException {
        String[] input = {"Delta", "Charlie", "Bravo", "Alpha"};
        Sort<String> sorter = new BucketSort<>(BucketSort::classifyStringInitial, BucketSort.ALPHABET_SIZE, 4, Config.load(BucketSortTest.class));
        sorter.mutatingSort(input);
        assertArrayEquals(new String[]{"Alpha", "Bravo", "Charlie", "Delta"}, input);
    }

    @Test
    public void convertToBiFunction() throws IOException {
        Function<String, Integer> classifyStringInitial = BucketSort::classifyStringInitial;
        assertNotNull(ClassificationSorter.convertToBiFunction(classifyStringInitial));
    }

    @Test
    public void caseIndependentBucketSort() throws IOException {
        String[] input = {"delta", "Charlie", "bravo", "Alpha"};
        BucketSort<String> sorter = BucketSort.CaseIndependentBucketSort(BucketSort::classifyStringInitial, BucketSort.ALPHABET_SIZE, 4, Config.load(BucketSortTest.class));
        sorter.mutatingSort(input);
        assertArrayEquals(new String[]{"Alpha", "bravo", "Charlie", "delta"}, input);
    }

    /**
     * A sub-range must sort, leaving the rest alone. Three things have to respect
     * {@code from} for that: checkBuckets counts against the range's length rather
     * than the whole array's, unloadBuckets writes from {@code from}, and the
     * numeric classifier reads from {@code from}.
     */
    @Test
    public void testSortSubRange() throws IOException {
        String[] xs = {"zulu", "bravo", "charlie", "alpha", "delta"};
        Sort<String> sorter = new BucketSort<String>(BucketSort::classifyStringInitial,
                BucketSort.ALPHABET_SIZE, xs.length, Config.load(BucketSortTest.class));
        sorter.sort(xs, 1, 4);
        assertArrayEquals(new String[]{"zulu", "alpha", "bravo", "charlie", "delta"}, xs);
    }

    /**
     * The numeric classifier is chosen from the range being sorted, not from the
     * whole array.
     */
    @Test
    public void testSortSubRangeOfNumbers() throws IOException {
        Integer[] xs = {900, 5, 3, 4, 1, 2, 900};
        Sort<Integer> sorter = new BucketSort<Integer>(null, 4, xs.length, Config.load(BucketSortTest.class));
        sorter.sort(xs, 1, 6);
        assertArrayEquals(new Integer[]{900, 1, 2, 3, 4, 5, 900}, xs);
    }

    /**
     * When every value is the same the gap is zero. This used to give the right
     * answer only because 0.0/0.0 is NaN, Math.floor(NaN) is NaN, and (int) NaN
     * is 0; it is now tested for outright.
     */
    @Test
    public void testSortAllEqual() throws IOException {
        Integer[] xs = {5, 5, 5, 5};
        Sort<Integer> sorter = new BucketSort<Integer>(null, 16, xs.length, Config.load(BucketSortTest.class));
        sorter.mutatingSort(xs);
        assertArrayEquals(new Integer[]{5, 5, 5, 5}, xs);
    }

    @Test
    public void testSortSingleton() throws IOException {
        Integer[] xs = {7};
        Sort<Integer> sorter = new BucketSort<Integer>(null, 16, xs.length, Config.load(BucketSortTest.class));
        sorter.mutatingSort(xs);
        assertArrayEquals(new Integer[]{7}, xs);
    }
}