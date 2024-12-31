package com.phasmidsoftware.dsaipg.sort.par;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

/**
 * ParSortTest is a test suite for the ParSort class.
 * It contains unit tests to verify the correct functionality of the sort method.
 */
public class ParSortTest {

    @Test
    public void testSortSmallArray() {
        int[] array = {5, 2, 8, 1, 9};
        int[] expected = {1, 2, 5, 8, 9};
        ParSort.cutoff = 10;  // Ensure sequential sorting is used
        ParSort.sort(array, 0, array.length);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testSortLargeArray() {
        int[] array = {20, 35, -15, 7, 55, 1, -22, 90, 3, 47};
        int[] expected = {-22, -15, 1, 3, 7, 20, 35, 47, 55, 90};
        ParSort.cutoff = 5;  // Ensure parallel sorting is used
        ParSort.sort(array, 0, array.length);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testSortWithNegativeNumbers() {
        int[] array = {-5, -1, -10, 0, -3};
        int[] expected = {-10, -5, -3, -1, 0};
        ParSort.cutoff = 10;
        ParSort.sort(array, 0, array.length);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testSortAlreadySortedArray() {
        int[] array = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};
        ParSort.cutoff = 10;
        ParSort.sort(array, 0, array.length);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testSortEmptyArray() {
        int[] array = {};
        int[] expected = {};
        ParSort.cutoff = 10;
        ParSort.sort(array, 0, array.length);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testSortSingleElementArray() {
        int[] array = {42};
        int[] expected = {42};
        ParSort.cutoff = 10;
        ParSort.sort(array, 0, array.length);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testSortPartialArray() {
        int[] array = {4, 3, 2, 10, 1, 20, 30};
        int[] expected = {2, 3, 4, 10, 1, 20, 30};
        ParSort.cutoff = 10;
        ParSort.sort(array, 0, 4);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testSortRandomLargeArray() {
        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (Math.random() * 100000);
        }
        int[] expected = Arrays.copyOf(array, array.length);
        Arrays.sort(expected);
        ParSort.cutoff = 1000;  // Ensure parallel sorting is used for this large array
        ParSort.sort(array, 0, array.length);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testSortWithDuplicateValues() {
        int[] array = {7, 3, 5, 3, 7, 9};
        int[] expected = {3, 3, 5, 7, 7, 9};
        ParSort.cutoff = 10;
        ParSort.sort(array, 0, array.length);
        assertArrayEquals(expected, array);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSortNegativeRange() {
        int[] array = {2, 4, 6, 8, 10};
        ParSort.cutoff = 10;
        ParSort.sort(array, -1, 3);
    }

    @Test
    public void testSortOverlappingRange() {
        int[] array = {12, 4, 6, 15, 2, 10};
        int[] expected = {4, 6, 12, 15, 2, 10};
        ParSort.cutoff = 10;
        ParSort.sort(array, 0, 3);
        assertArrayEquals(expected, array);
    }
}