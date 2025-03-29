package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.generic.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.helper.Helper;
import com.phasmidsoftware.dsaipg.util.config.Config;

/**
 * Implementation of the Heap Sort algorithm for sorting an array of elements that implement the Comparable interface.
 * Heap Sort is an in-place, comparison-based sorting algorithm that uses a binary heap data structure.
 * <p>
 * The algorithm consists of two main phases:
 * 1. Construction phase: The input array is transformed into a max-heap.
 * 2. Sort-down phase: The largest element is repeatedly removed from the heap and placed at the end of the array,
 * while the remaining heap is restored as a max-heap.
 * <p>
 * This class extends SortWithComparableHelper and provides specific sorting logic using the heap structure.
 * <p>
 *     CONSIDER using the heapConstructor() method of PriorityQueue.
 * </p>
 *
 * @param <X> The type of the elements to be sorted, which must extend Comparable.
 */
public class HeapSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    /**
     * Constructor for the HeapSort class.
     * Initializes the HeapSort algorithm with a helper instance.
     *
     * @param helper The helper instance to assist with operations such as comparisons and swaps
     *               during the sorting process. The helper is typically a utility class that
     *               provides additional methods to facilitate sorting logic.
     */
    public HeapSort(Helper<X> helper) {
        super(helper);
    }

    /**
     * Constructs a HeapSort instance with the specified number of words and configuration settings.
     * This constructor allows configuring the HeapSort with specific parameters such as the number
     * of words and other configuration options provided in the Config object.
     *
     * @param nWords The number of words to be used for the sorting algorithm configuration.
     * @param config The configuration object that specifies settings and parameters for the HeapSort algorithm.
     */
    public HeapSort(int nWords, Config config) {
        super(DESCRIPTION, nWords, 1, config);
    }

    /**
     * Constructs a HeapSort instance with the specified number of words, number of runs, and configuration settings.
     * This constructor is used to configure and initialize the HeapSort algorithm with detailed parameters.
     *
     * @param nWords The number of words to be used for the sorting algorithm configuration.
     * @param nRuns  The number of sorting runs or iterations to be managed by the algorithm.
     * @param config The configuration object that specifies settings and parameters for the HeapSort algorithm.
     */
    public HeapSort(int nWords, int nRuns, Config config) {
        super(DESCRIPTION, nWords, nRuns, config);
    }

    /**
     * Sorts the specified portion of the array using the heap sort algorithm.
     * The method first constructs a max heap and then sorts the elements by repeatedly
     * extracting the maximum element.
     *
     * @param array the array to be sorted. The array is modified in place.
     *              If the array is null or has a length of 1 or less, no operation is performed.
     * @param from  the index of the first element in the portion to be sorted (inclusive).
     *              This parameter is currently ignored as the method sorts the entire array.
     * @param to    the index of the last element in the portion to be sorted (exclusive).
     *              This parameter is currently ignored as the method sorts the entire array.
     */
    public void sort(X[] array, int from, int to) {
        if (array == null || array.length <= 1) return;

        // XXX construction phase
        buildMaxHeap(array);

        // XXX sort-down phase
        Helper<X> helper = getHelper();
        // TODO we over-count hits in the swap operation -- fix it.
        for (int i = array.length - 1; i >= 1; i--) {
            helper.swap(array, 0, i);
            heapify(array, i, 0);
        }
    }

    /**
     * Builds a max heap from the given array. The method adjusts the input array
     * such that it satisfies the properties of a max heap, where each parent node
     * is greater than or equal to its child nodes.
     *
     * @param array the array to be transformed into a max heap. It is modified
     *              in place. The array should not be null, and its elements
     *              should support comparison.
     */
    private void buildMaxHeap(X[] array) {
        int half = array.length / 2;
        for (int i = half; i >= 0; i--) heapify(array, array.length, i);
    }

    /**
     * Maintains the max-heap property for the given array. This method assumes that the binary trees rooted at the left
     * and right children of the index satisfy the max-heap property, but the node at the given index may violate this
     * property. The method restores the max-heap property by ensuring that the subtree rooted at the index satisfies it.
     *
     * @param array    the array representing the heap. This should already satisfy the max-heap property except
     *                 potentially at the specified index.
     * @param heapSize the number of valid elements in the heap within the array. Elements beyond this size are not
     *                 considered part of the heap.
     * @param index    the index of the node potentially violating the max-heap property. The method ensures that the
     *                 subtree rooted at this index satisfies the max-heap property upon completion.
     */
    private void heapify(X[] array, int heapSize, int index) {
        // TODO we over-count hits in the swap operation -- fix it.
        Helper<X> helper = getHelper();
        final int left = index * 2 + 1;
        final int right = index * 2 + 2;
        int largest = index;
        if (left < heapSize && helper.compare(array, largest, left) < 0) largest = left;
        if (right < heapSize && helper.compare(array, largest, right) < 0) largest = right;
        if (index != largest) {
            helper.swap(array, index, largest);
            heapify(array, heapSize, largest);
        }
    }

    public static final String DESCRIPTION = "Heap Sort";

}