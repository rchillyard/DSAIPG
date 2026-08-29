package com.phasmidsoftware.dsaipg.sort.counting;

import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests for RadixSort.
 * <p>
 * NOTE there is Cucumber glue for this class in the RadixSortStepDefinition
 * sub-package, but nothing executes it: RadixSortTestRunner is commented out in
 * its entirety, and the glue class has no {@code @Test} methods. These are the
 * tests that run.
 * <p>
 * {@code to} is EXCLUSIVE here, as everywhere else in this tree.
 */
public class RadixSortTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    private final RadixSort sorter = new RadixSort();

    @Test
    public void testSort() throws Exception {
        int[] xs = {170, 45, 75, 90, 802, 24, 2, 66};
        sorter.sort(xs, 0, xs.length);
        assertArrayEquals(new int[]{2, 24, 45, 66, 75, 90, 170, 802}, xs);
    }

    @Test
    public void testSortRandom() throws Exception {
        Random random = new Random(42);
        int[] xs = new int[500];
        for (int i = 0; i < xs.length; i++) xs[i] = random.nextInt(99999);
        int[] expected = xs.clone();
        Arrays.sort(expected);
        sorter.sort(xs, 0, xs.length);
        assertArrayEquals(expected, xs);
    }

    @Test
    public void testSortDuplicates() throws Exception {
        int[] xs = {5, 3, 5, 3, 1, 1, 5};
        sorter.sort(xs, 0, xs.length);
        assertArrayEquals(new int[]{1, 1, 3, 3, 5, 5, 5}, xs);
    }

    @Test
    public void testSortAlreadySorted() throws Exception {
        int[] xs = {1, 2, 3, 4, 5};
        sorter.sort(xs, 0, xs.length);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, xs);
    }

    @Test
    public void testSortZeros() throws Exception {
        int[] xs = {0, 0, 0};
        sorter.sort(xs, 0, xs.length);
        assertArrayEquals(new int[]{0, 0, 0}, xs);
    }

    /**
     * The whole point of the change: the last element of the range must be
     * included. With an inclusive "to", sort(xs, 1, 5) left xs[5] where it was.
     */
    @Test
    public void testSortSubRange() throws Exception {
        int[] xs = {99, 5, 3, 4, 1, 2, 99};
        sorter.sort(xs, 1, 6);
        assertArrayEquals(new int[]{99, 1, 2, 3, 4, 5, 99}, xs);
    }

    @Test
    public void testSortWholeArrayIsNowLegal() throws Exception {
        // to == length is in range, since to is exclusive.
        int[] xs = {3, 1, 2};
        sorter.sort(xs, 0, 3);
        assertArrayEquals(new int[]{1, 2, 3}, xs);
    }

    @Test
    public void testSortEmptyRange() throws Exception {
        int[] xs = {3, 1, 2};
        sorter.sort(xs, 1, 1);
        assertArrayEquals("an empty range changes nothing", new int[]{3, 1, 2}, xs);
    }

    @Test
    public void testSortSingleton() throws Exception {
        int[] xs = {7};
        sorter.sort(xs, 0, 1);
        assertArrayEquals(new int[]{7}, xs);
    }

    @Test
    public void testSortFromAfterTo() {
        assertThrows(Exception.class, () -> sorter.sort(new int[]{3, 1, 2}, 2, 1));
    }

    @Test
    public void testSortIndexOutOfBounds() {
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> sorter.sort(new int[]{3, 1, 2}, 0, 9));
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> sorter.sort(new int[]{3, 1, 2}, -1, 2));
    }

    @Test
    public void testFindMaxInt() {
        assertEquals(9, sorter.findMaxInt(new int[]{3, 9, 2, 7}, 0, 4));
        assertEquals(7, sorter.findMaxInt(new int[]{3, 9, 2, 7}, 2, 4));
    }

    /**
     * Stability is what makes the successive passes work. These all have the same
     * units digit, so a pass by units must leave them exactly as they are --
     * otherwise the order an earlier pass established would be lost.
     */
    @Test
    public void testCountSortIsStable() {
        int[] xs = {21, 11, 31};
        sorter.countSort(xs, 1, 0, 3);
        assertArrayEquals(new int[]{21, 11, 31}, xs);
    }

    @Test
    public void testCountSortOrdersBySelectedDigit() {
        int[] xs = {21, 11, 31};
        sorter.countSort(xs, 10, 0, 3);
        assertArrayEquals(new int[]{11, 21, 31}, xs);
    }
}
