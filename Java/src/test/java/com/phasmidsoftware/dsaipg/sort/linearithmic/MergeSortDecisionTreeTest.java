package com.phasmidsoftware.dsaipg.sort.linearithmic;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class MergeSortDecisionTreeTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * This class tests the sort method of the MergeSortDecisionTree class.
     * The sort method performs a merge sort algorithm to sort an array
     * between given left and right indices.
     */
    @Test
    public void testSortWithUnsortedArray() {
        // Arrange
        MergeSortDecisionTree sorter = new MergeSortDecisionTree();
        int[] input = {12, 11, 13, 5, 6, 7};
        int[] expected = {5, 6, 7, 11, 12, 13};

        // Act
        sorter.sort(input, 0, input.length - 1);

        // Assert
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortWithSortedArray() {
        // Arrange
        MergeSortDecisionTree sorter = new MergeSortDecisionTree();
        int[] input = {1, 2, 3, 4, 5, 6};
        int[] expected = {1, 2, 3, 4, 5, 6};

        // Act
        sorter.sort(input, 0, input.length - 1);

        // Assert
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortWithReverseSortedArray() {
        // Arrange
        MergeSortDecisionTree sorter = new MergeSortDecisionTree();
        int[] input = {9, 8, 7, 6, 5};
        int[] expected = {5, 6, 7, 8, 9};

        // Act
        sorter.sort(input, 0, input.length - 1);

        // Assert
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortWithAllElementsEqual() {
        // Arrange
        MergeSortDecisionTree sorter = new MergeSortDecisionTree();
        int[] input = {7, 7, 7, 7, 7};
        int[] expected = {7, 7, 7, 7, 7};

        // Act
        sorter.sort(input, 0, input.length - 1);

        // Assert
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortWithSingleElementArray() {
        // Arrange
        MergeSortDecisionTree sorter = new MergeSortDecisionTree();
        int[] input = {42};
        int[] expected = {42};

        // Act
        sorter.sort(input, 0, input.length - 1);

        // Assert
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortWithEmptyArray() {
        // Arrange
        MergeSortDecisionTree sorter = new MergeSortDecisionTree();
        int[] input = {};
        int[] expected = {};

        // Act
        sorter.sort(input, 0, input.length - 1);

        // Assert
        assertArrayEquals(expected, input);
    }

    @Test
    public void testSortWithNegativeNumbers() {
        // Arrange
        MergeSortDecisionTree sorter = new MergeSortDecisionTree();
        int[] input = {3, -1, 4, -5, 2};
        int[] expected = {-5, -1, 2, 3, 4};

        // Act
        sorter.sort(input, 0, input.length - 1);

        // Assert
        assertArrayEquals(expected, input);
    }
}