/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

/**
 * Implementation of the SizedIterable interface.
 * This class provides an implementation for a sized iterable,
 * which wraps a given {@code Iterable} or {@code Collection}, allowing efficient size retrieval
 * and iteration over the contained elements.
 *
 * @param <T> the type of elements held by this iterable.
 */
public class SizedIterableImpl<T> implements SizedIterable<T> {
    /**
     * Constructs a new instance of {@code SizedIterableImpl} using the given collection.
     * The size of the collection is precomputed and stored for efficient retrieval.
     *
     * @param collection the collection to be wrapped, providing iteration and size computation capabilities
     */
    public SizedIterableImpl(Collection<T> collection) {
        this.iterable = collection;
        size = collection.size();
    }

    /**
     * Constructs a SizedIterableImpl instance from the specified iterable.
     * Calculates the size of the iterable by iterating through its elements.
     *
     * @param iterable the iterable to wrap, which allows iteration and size retrieval
     */
    public SizedIterableImpl(Iterable<T> iterable) {
        this.iterable = iterable;
        size = getSize(iterable);
    }

    /**
     * Returns the size of the elements wrapped by this {@code SizedIterable} instance.
     *
     * @return the number of elements in the sized iterable.
     */
    public int size() {
        return size;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an {@code Iterator} instance that provides access to the elements
     * contained in the underlying {@code Iterable}.
     */
    @NotNull
    public Iterator<T> iterator() {
        return iterable.iterator();
    }

    /**
     * Factory method to create a SizedIterable instance from a given collection.
     *
     * @param collection the input collection to be wrapped in a SizedIterable.
     *                   It provides efficient size retrieval and iteration capabilities.
     * @param <T>        the type of elements in the collection.
     * @return a SizedIterable instance backed by the input collection.
     */
    public static <T> SizedIterable<T> create(Collection<T> collection) {
        return new SizedIterableImpl<>(collection);
    }

    /**
     * Creates a new instance of {@code SizedIterable} from the given {@code Iterable}.
     *
     * @param iterable the {@code Iterable} to be wrapped as a {@code SizedIterable}.
     *                 This allows efficient size retrieval and iteration over its elements.
     * @param <T>      the type of elements contained in the {@code Iterable}.
     * @return a {@code SizedIterable} that wraps the provided {@code Iterable}.
     */
    public static <T> SizedIterable<T> create(Iterable<T> iterable) {
        return new SizedIterableImpl<>(iterable);
    }

    /**
     * Computes the size of the given iterator by iterating through all its elements.
     *
     * @param <T>      the type of elements returned by the iterator
     * @param iterator the iterator whose size is to be determined
     * @return the number of elements in the iterator
     */
    public static <T> int getSize(Iterator<T> iterator) {
        int size = 0;
        while (iterator.hasNext()) {
            size++;
            iterator.next();
        }
        return size;
    }

    private final Iterable<T> iterable;
    private final int size;

    /**
     * Computes the size of the given iterable by iterating through its elements.
     *
     * @param <T>      the type of elements in the iterable
     * @param iterable the input iterable whose size is to be determined
     * @return the number of elements in the iterable
     */
    private static <T> int getSize(Iterable<T> iterable) {
        int size = 0;
        for (T t : iterable) size++;
        return size;
    }
}