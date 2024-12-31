/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.elementary;

import java.util.Comparator;

/**
 * A generic implementation of the insertion sort algorithm.
 * Allows sorting of arrays using either the natural order of elements
 * (when the elements implement Comparable) or a custom Comparator provided during instantiation.
 *
 * @param <S> the type of elements to be sorted
 */
public class InsertionSortBasic<S> {

    /**
     * Factory method to create an instance of InsertionSortBasic configured to compare elements using the natural order.
     *
     * @param <X> the type of elements to be sorted, which must extend Comparable.
     * @return a new instance of InsertionSortBasic configured for natural ordering of elements.
     */
    public static <X extends Comparable<X>> InsertionSortBasic<X> create() {
        return new InsertionSortBasic<>(Comparable::compareTo);
    }

    /**
     * Sorts the entire array using an insertion sort algorithm.
     * Delegates to a partitioned sort method for implementation.
     *
     * @param a the array to be sorted
     */
    public void sort(S[] a) {
        sort(a, 0, a.length);
    }

    /**
     * Sort the array a starting with index from and end just before index to.
     *
     * @param a    the array of which we must sort a partition.
     * @param from the lowest index of the partition to be sorted.
     * @param to   one more than the highest index of the partition to be sorted.
     */
    public void sort(S[] a, int from, int to) {
        for (int i = from + 1; i < to; i++) insert(a, from, i);
    }

    /**
     * Constructs an instance of InsertionSortBasic with a specified comparator for sorting elements.
     *
     * @param comparator the comparator used to determine the order of elements. It is applied
     *                   to compare elements during the sorting process.
     */
    public InsertionSortBasic(Comparator<S> comparator) {
        this.comparator = comparator;
    }

    /**
     * Move (insert) the element a[i] into its proper place amongst the (sorted) part of the array
     * (starting with from and ending at i-1).
     *
     * @param a    the (sorted) array into which the transitional element should be moved.
     * @param from the first (left-most) element of the partition being sorted.
     * @param i    the index of the transitional element.
     */
    void insert(S[] a, int from, int i) {
        // TO BE IMPLEMENTED  : implement inner loop of insertion sort using comparator
        // END SOLUTION
    }

    private void swap(Object[] a, int j, int i) {
        Object temp = a[j];
        a[j] = a[i];
        a[i] = temp;
    }

    private final Comparator<S> comparator;
}