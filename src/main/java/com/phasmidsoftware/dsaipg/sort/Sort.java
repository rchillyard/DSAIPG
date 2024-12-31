/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.util.Utilities;

import java.util.Arrays;
import java.util.Collection;

/**
 * Interface Sort which defines the various sort methods for sorting elements of type X.
 * NOTE this definition does not assume that X extends Comparable of X.
 *
 * @param <X> the type of the elements to be sorted.
 */
public interface Sort<X> extends AutoCloseable {

    String getDescription();

    /**
     * Generic, non-mutating sort method which allows for explicit determination of the makeCopy option.
     *
     * @param xs       sort the array xs, returning the sorted result, leaving xs unchanged.
     * @param makeCopy if set to true, we make a copy first and sort that.
     */
    default X[] sort(X[] xs, boolean makeCopy) {
        init(xs.length);
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        sort(result, 0, result.length);
        return result;
    }

    /**
     * Generic, non-mutating sort method.
     *
     * @param xs sort the array xs, returning the sorted result, leaving xs unchanged.
     */
    default X[] sort(X[] xs) {
        return sort(xs, true);
    }

    /**
     * Generic, mutating sort method.
     * Note that there is no return value.
     *
     * @param xs the array to be sorted.
     */
    default void mutatingSort(X[] xs) {
        sort(xs, false);
    }

    /**
     * Generic, mutating sort method which operates on a sub-array.
     *
     * @param xs   sort the array xs from "from" until "to" (exclusive of to).
     * @param from the index of the first element to sort.
     * @param to   the index of the first element not to sort.
     */
    void sort(X[] xs, int from, int to);

    /**
     * Method to take a Collection of X and return an Iterable of X in order.
     *
     * @param xs the collection of X elements.
     * @return a sorted iterable of X.
     */
    default Iterable<X> sort(Collection<X> xs) {
        if (xs.isEmpty()) return xs;
        final X[] array = Utilities.asArray(xs);
        mutatingSort(array);
        return Arrays.asList(array);
    }

    /**
     * Perform initializing step for this Sort.
     * <p>
     * CONSIDER merging this with preProcess logic.
     *
     * @param n the number of elements to be sorted.
     */
    void init(int n);

    /**
     * We redefine this method so that it does not throw an Exception.
     */
    void close();
}