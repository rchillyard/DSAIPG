/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.select;

/**
 * A functional interface for selecting the k-th smallest element from an array.
 *
 * @param <X> the type of elements that must extend Comparable.
 */
public interface Select<X extends Comparable<X>> {
    /**
     * Selects the k-th smallest element from the given array.
     *
     * @param a the input array of elements to search within. All elements must implement the Comparable interface.
     * @param k the index (0-based) of the smallest element to find; must be between 0 and a.length - 1.
     * @return the k-th smallest element in the input array.
     * @throws IllegalArgumentException if the value of k is out of the valid range (0 to a.length - 1).
     */
    X select(X[] a, int k);
}