package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.helper.Helper;

import java.util.Arrays;

/**
 * Class representing a partition of an array for sorting or manipulation purposes.
 * The partition is defined by a range of indices within the array, specified by
 * the starting index {@code from} (inclusive) and the ending index {@code to} (exclusive).
 * This class is typically used in the context of divide-and-conquer sorting algorithms
 * such as Quicksort, where subsets of an array are recursively partitioned and sorted.
 *
 * @param <X> the type of elements in the array, which must extend {@code Comparable<X>}.
 */
public class Partition<X extends Comparable<X>> {
    /**
     * @param xs   the array to be sorted.
     * @param from the index of the first element to be sorted.
     * @param to   the index of the first element NOT to be sorted.
     */
    public Partition(X[] xs, int from, int to) {
        this.xs = xs;
        this.from = from;
        this.to = to;
    }

    /**
     * Checks if the elements within the specified range of the array are sorted.
     *
     * @param helper a {@code Helper<X>} instance providing utility methods for array sorting
     *               and validation, including the {@code isSorted} method.
     * @return {@code true} if the elements in the range [{@code from}, {@code to}) are sorted
     *         in non-decreasing order, {@code false} otherwise.
     */
    public boolean isSorted(Helper<X> helper) {
        return helper.isSorted(xs, from, to);
    }

    /**
     * Returns a string representation of the Partition object.
     * <p>
     * The representation includes the number of elements in the array,
     * the starting index, and the end index of the range to be sorted.
     *
     * @return a string in the format "Partition{xs: <number of elements> elements, from=<start index>, to=<end index>}".
     */
    public String toString() {
        if (from >= to)
            return "Empty Partition at {" + (to-1)+ "}";
        X[] elements = Arrays.copyOfRange(xs, from, to);
        return "Partition{" +
                "from=" + from +
                ", to=" + to + " elements: " +
                Arrays.toString(elements) +
                '}';
    }

    /**
     * The array of elements to be partitioned. This array is of a generic type {@code X},
     * which extends {@code Comparable<X>}, ensuring that the elements can be compared.
     * The array represents the data to be sorted or manipulated during the partition process.
     */
    public final X[] xs;
    /**
     * The index of the first element in the array to be sorted.
     * Defines the starting position of the partition range.
     */
    public final int from;
    /**
     * Represents the index of the first element NOT to be sorted in a specific range.
     * This variable is part of the Partition class, which defines a partition of an array
     * for Quicksort. The value of this variable marks the end boundary (exclusive)
     * of the subarray to be considered for sorting.
     */
    public final int to;
}
