/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The SizedIterable interface provides a contract for iterables with a defined size and additional utility
 * methods for convenience.
 * This interface extends the standard {@code Iterable} interface, ensuring that any
 * implementation is iterable while also providing methods to determine the size of the collection and convert
 * it to a {@code List}.
 *
 * @param <T> the type of elements held by this iterable.
 */
public interface SizedIterable<T> extends Iterable<T> {

    /**
     * Method to yield the size of this iterable.
     *
     * @return the size.
     */
    int size();

    /**
     * Method to yield a List from this Iterable.
     * The result is always a copy of the original (typically of a different shape).
     * The order of the result is the same as the order of the iterator.
     *
     * @return a List&lt;T&gt; formed from the iterator of this Iterable.
     */
    default List<T> toList() {
        final Iterator<T> iterator = iterator();
        final List<T> result = new ArrayList<>();
        while (iterator.hasNext()) result.add(iterator.next());
        return result;
    }
}