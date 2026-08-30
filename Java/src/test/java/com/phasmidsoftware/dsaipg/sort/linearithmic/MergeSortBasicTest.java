package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.generic.Sort;
import com.phasmidsoftware.dsaipg.util.config.Config;
import org.junit.Test;

import static com.phasmidsoftware.dsaipg.util.config.Config_Benchmark.setupConfig;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class MergeSortBasicTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void sort() {
        Integer[] xs = new Integer[4];
        xs[0] = 3;
        xs[1] = 4;
        xs[2] = 2;
        xs[3] = 1;
        // NOTE: first we ensure that there is no cutoff to insertion sort going on.
        final Config config = setupConfig("true", "false", "", "0", "1", "");
        Sort<Integer> s = new MergeSortBasic<>(xs.length, config);
        Integer[] ys = s.sort(xs);
        assertEquals(Integer.valueOf(1), ys[0]);
        assertEquals(Integer.valueOf(2), ys[1]);
        assertEquals(Integer.valueOf(3), ys[2]);
        assertEquals(Integer.valueOf(4), ys[3]);
    }

    /**
     * sort(xs, makeCopy) leaves the original alone when asked to copy, and sorts it
     * in place when not. Both return the sorted array.
     */
    @Test
    public void testSort() {
        Integer[] xs = {3, 4, 2, 1};
        Sort<Integer> s = new MergeSortBasic<>(xs.length, noCutoff());
        Integer[] copied = s.sort(xs, true);
        assertArrayEquals(new Integer[]{1, 2, 3, 4}, copied);
        assertArrayEquals("the original is untouched", new Integer[]{3, 4, 2, 1}, xs);

        Integer[] ys = {3, 4, 2, 1};
        Integer[] inPlace = new MergeSortBasic<Integer>(ys.length, noCutoff()).sort(ys, false);
        assertArrayEquals(new Integer[]{1, 2, 3, 4}, ys);
        assertSame("without a copy, the array returned IS the one given", ys, inPlace);
    }

    @Test
    public void mutatingSort() {
        Integer[] xs = {5, 3, 9, 1, 7};
        new MergeSortBasic<Integer>(xs.length, noCutoff()).mutatingSort(xs);
        assertArrayEquals(new Integer[]{1, 3, 5, 7, 9}, xs);
    }

    /**
     * sort(a, from, to) touches only that range. to is EXCLUSIVE, as everywhere in
     * this repository.
     */
    @Test
    public void testSort1() {
        Integer[] xs = {9, 4, 3, 2, 1, 9};
        new MergeSortBasic<Integer>(xs.length, noCutoff()).sort(xs, 1, 5);
        assertArrayEquals(new Integer[]{9, 1, 2, 3, 4, 9}, xs);
    }

    /**
     * The range method may be reached directly, without sort(X[], boolean) having
     * allocated the auxiliary array first. It must allocate one for itself.
     */
    @Test
    public void testSort2() {
        Integer[] xs = {4, 3, 2, 1};
        MergeSortBasic<Integer> s = new MergeSortBasic<>(xs.length, noCutoff());
        s.sort(xs, 0, xs.length);
        assertArrayEquals(new Integer[]{1, 2, 3, 4}, xs);
    }

    /**
     * Below the cutoff the work is handed to insertion sort, and the answer must be
     * the same. With a cutoff of 32 nothing here is merged at all.
     */
    @Test
    public void testSort3() {
        Integer[] xs = {5, 3, 9, 1, 7, 2, 8, 6, 4};
        final Config cutoff32 = setupConfig("true", "false", "", "0", "32", "");
        Integer[] ys = new MergeSortBasic<Integer>(xs.length, cutoff32).sort(xs);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, ys);
    }

    /**
     * An empty array and a singleton are both already sorted, and must not throw.
     */
    @Test
    public void testSortDegenerate() {
        Integer[] empty = {};
        assertArrayEquals(new Integer[]{}, new MergeSortBasic<Integer>(0, noCutoff()).sort(empty));
        Integer[] one = {42};
        assertArrayEquals(new Integer[]{42}, new MergeSortBasic<Integer>(1, noCutoff()).sort(one));
    }

    /**
     * A merge sort is stable, and a sort of an already-sorted array leaves it alone.
     */
    @Test
    public void testSortIdempotent() {
        Integer[] xs = {1, 2, 3, 4, 5};
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5},
                new MergeSortBasic<Integer>(xs.length, noCutoff()).sort(xs));
    }

    /**
     * @return a Config with the cutoff set to 1, so that nothing is handed to
     * insertion sort and the merge itself is what is being tested.
     */
    private static Config noCutoff() {
        return setupConfig("true", "false", "", "0", "1", "");
    }
}