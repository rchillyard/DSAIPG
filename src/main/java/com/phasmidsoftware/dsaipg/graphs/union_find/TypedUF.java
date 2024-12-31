/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.union_find;

/**
 * TypedUF is an extension of the UF interface where elements are not limited to integers,
 * but can be represented by objects of a generic type {@code T}.
 * This interface allows union-find operations to be performed on generic types.
 *
 * @param <T> the type of elements over which the union-find operations are performed
 */
public interface TypedUF<T> extends UF {
    /**
     * Determines if two elements, represented by objects of type {@code T}, are in the same component.
     *
     * @param t1 the first element
     * @param t2 the second element
     * @return {@code true} if the two elements {@code t1} and {@code t2} are in the same component,
     * {@code false} otherwise
     * @throws UFException if an error occurs within the union-find operation
     */
    boolean connected(T t1, T t2) throws UFException;

    /**
     * Merges the components containing the two specified elements.
     * This operation connects two elements such that they belong to the same set.
     *
     * @param t1 the first element to be merged
     * @param t2 the second element to be merged
     * @throws UFException if an error occurs during the union operation,
     * such as invalid or unrecognized elements
     */
    void union(T t1, T t2) throws UFException;
}