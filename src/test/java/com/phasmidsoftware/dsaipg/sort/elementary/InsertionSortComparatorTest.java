package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.HelperFactory;
import org.junit.Test;

import java.util.Comparator;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfigFixes;
import static org.junit.Assert.assertArrayEquals;

/**
 * Test class for InsertionSortComparator.
 * This class tests the `sort` method in the `InsertionSortComparator` class which performs an insertion sort on an array.
 */
public class InsertionSortComparatorTest {

    @Test
    public void testSortWithIntegers() {
        Integer[] input = {5, 3, 8, 6, 2};
        Integer[] expected = {2, 3, 5, 6, 8};

        Comparator<Integer> comparator = Integer::compareTo;
        Helper<Integer> helper = HelperFactory.createGeneric("Test", comparator, input.length, 1, setupConfigFixes());
        InsertionSortComparator<Integer> sorter = new InsertionSortComparator<>(helper);

        sorter.sort(input, 0, input.length);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortWithStrings() {
        String[] input = {"banana", "apple", "cherry", "date"};
        String[] expected = {"apple", "banana", "cherry", "date"};

        Comparator<String> comparator = String::compareTo;
        Helper<String> helper = HelperFactory.createGeneric("Test", comparator, input.length, 1, setupConfigFixes());
        InsertionSortComparator<String> sorter = new InsertionSortComparator<>(helper);

        sorter.sort(input, 0, input.length);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortEmptyArray() {
        Integer[] input = {};
        Integer[] expected = {};

        Comparator<Integer> comparator = Integer::compareTo;
        Helper<Integer> helper = HelperFactory.createGeneric("Test", comparator, input.length, 1, setupConfigFixes());
        InsertionSortComparator<Integer> sorter = new InsertionSortComparator<>(helper);

        sorter.sort(input, 0, input.length);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortSingleElementArray() {
        Integer[] input = {1};
        Integer[] expected = {1};

        Comparator<Integer> comparator = Integer::compareTo;
        Helper<Integer> helper = HelperFactory.createGeneric("Test", comparator, input.length, 1, setupConfigFixes());
        InsertionSortComparator<Integer> sorter = new InsertionSortComparator<>(helper);

        sorter.sort(input, 0, input.length);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortWithSubArray() {
        Integer[] input = {10, 5, 2, 6, 8};
        Integer[] expected = {10, 2, 5, 6, 8};

        Comparator<Integer> comparator = Integer::compareTo;
        Helper<Integer> helper = HelperFactory.createGeneric("Test", comparator, input.length, 1, setupConfigFixes());
        InsertionSortComparator<Integer> sorter = new InsertionSortComparator<>(helper);

        sorter.sort(input, 1, input.length);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortReversedArray() {
        Integer[] input = {9, 7, 5, 3, 1};
        Integer[] expected = {1, 3, 5, 7, 9};

        Comparator<Integer> comparator = Integer::compareTo;
        Helper<Integer> helper = HelperFactory.createGeneric("Test", comparator, input.length, 1, setupConfigFixes());
        InsertionSortComparator<Integer> sorter = new InsertionSortComparator<>(helper);

        sorter.sort(input, 0, input.length);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortAlreadySortedArray() {
        Integer[] input = {1, 2, 3, 4, 5};
        Integer[] expected = {1, 2, 3, 4, 5};

        Comparator<Integer> comparator = Integer::compareTo;
        Helper<Integer> helper = HelperFactory.createGeneric("Test", comparator, input.length, 1, setupConfigFixes());
        InsertionSortComparator<Integer> sorter = new InsertionSortComparator<>(helper);

        sorter.sort(input, 0, input.length);
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortWithDuplicateValues() {
        Integer[] input = {3, 7, 3, 1, 7};
        Integer[] expected = {1, 3, 3, 7, 7};

        Comparator<Integer> comparator = Integer::compareTo;
        Helper<Integer> helper = HelperFactory.createGeneric("Test", comparator, input.length, 1, setupConfigFixes());
        InsertionSortComparator<Integer> sorter = new InsertionSortComparator<>(helper);

        sorter.sort(input, 0, input.length);
        assertArrayEquals(expected, input);
    }
}