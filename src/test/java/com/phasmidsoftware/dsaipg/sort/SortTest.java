package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.sort.linearithmic.MergeSortTest;
import com.phasmidsoftware.dsaipg.util.Config;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;

public class SortTest {

    static class TestSorter extends SortWithComparableHelper<Integer> {
        public TestSorter(String description, int N, Config config) {
            super(description, N, 1, config);
        }

        /**
         * Generic, mutating sort method which operates on a sub-array
         *
         * @param xs   sort the array xs from "from" to "to".
         * @param from the index of the first element to sort
         * @param to   the index of the first element not to sort
         */
        public void sort(Integer[] xs, int from, int to) {
            Arrays.sort(xs, from, to);
        }
    }

    @Test
    public void testSort1() {
        final TestSorter sorter = new TestSorter("test", 100, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000000));
        final Integer[] ys = sorter.sort(xs);
        assertTrue(ys[0] < ys[1]);
        helper.postProcess(ys); // test that ys is properly sorted.
    }

    @Test
    public void testSort2() {
        final int N = 100;
        final TestSorter sorter = new TestSorter("test", N, config);
        final Helper<Integer> helper = sorter.getHelper();
        helper.init(N);
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000000));
        sorter.sort(xs, 0, xs.length);
        assertTrue(xs[0] < xs[1]);
        helper.postProcess(xs); // test that xs is properly sorted.
    }

    @Test
    public void testSort3() throws IOException {
        final Config config = Config.load(getClass());
        final SortWithHelper<Integer> sorter = new SortWithComparableHelper<>("test", 100, 1, config) {
            public void sort(Integer[] xs, int from, int to) {
                // Do nothing.
            }

            public void postProcess(Integer[] xs) {
                if (!getHelper().isSorted(xs)) throw new HelperException("Array is not sorted");
            }
        };
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000000));
        sorter.sort(xs, 0, xs.length);
        assertFalse("array should not be sorted - except under extremely rare circumstances", helper.isSorted(xs));
    }

    @Test
    public void mutatingSort() {
        final TestSorter sorter = new TestSorter("test", 100, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000000));
        sorter.mutatingSort(xs);
        assertTrue(xs[0] < xs[1]);
        helper.postProcess(xs); // test that xs is properly sorted.
    }

    @Test
    public void testSort4() {
        final TestSorter sorter = new TestSorter("test", 100, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000000));
        final Integer[] ys = sorter.sort(xs, false);
        assertArrayEquals(xs, ys);
        assertTrue(ys[0] < ys[1]);
        helper.postProcess(ys); // test that xs is properly sorted.
    }

    @Test
    public void testSort5() {
        final TestSorter sorter = new TestSorter("test", 100, config);
        final Helper<Integer> helper = sorter.getHelper();
        final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000000));
        final Collection<Integer> list = Arrays.asList(xs);
        final Iterable<Integer> ys = sorter.sort(list);
        final Iterator<Integer> iterator = ys.iterator();
        int first = iterator.next();
        int second = iterator.next();
        assertTrue(first < second);
    }

    @Test
    // TEST
    public void getHelper() {
    }

    @Test
    // TEST
    public void init() {
    }

    @Test
    // TEST
    public void preProcess() {
    }

    @Test
    // TEST
    public void close() {
    }

    private static Config config;

    @BeforeClass
    public static void beforeClass() throws IOException {
        config = Config.load(MergeSortTest.class);
    }

}