/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.union_find;

import com.phasmidsoftware.dsaipg.util.SizedIterable;

import java.util.HashMap;
import java.util.Map;

/**
 * TypedUF_HWQUPC is a generic implementation of a union-find (UF) data structure
 * based on the Height-weighted Quick Union with Path Compression (HWQUPC) algorithm.
 * This class extends {@code UF_HWQUPC} to allow for operations
 * on generic types {@code T} instead of just integer indices.
 *
 * @param <T> the type of elements over which the union-find operations are performed
 */
public class TypedUF_HWQUPC<T> extends UF_HWQUPC implements TypedUF<T> {

    /**
     * Constructs a new TypedUF_HWQUPC object, a generic union-find data structure,
     * initialized with the elements provided by the given SizedIterable.
     * Each unique element in the iterable will be assigned a unique integer index
     * to be used internally for union-find operations.
     *
     * @param ts the SizedIterable containing the elements of type T to be managed by the union-find structure.
     *           The size of this iterable will determine the initial number of components in the structure,
     *           and each element must be unique to avoid collisions in the internal mapping.
     */
    public TypedUF_HWQUPC(SizedIterable<T> ts) {
        super(ts.size());
        map = new HashMap<>(ts.size());
        int count = 0;
        for (T t : ts) map.put(t, count++);
    }

    /**
     * Determines if two elements of type T are in the same component in the union-find structure.
     * This is achieved by mapping the elements to their corresponding integer indices internally.
     *
     * @param t1 the first element of type T
     * @param t2 the second element of type T
     * @return true if the two elements t1 and t2 are in the same component; false otherwise
     * @throws UFException if either t1 or t2 is not an element managed by the union-find structure
     */
    public boolean connected(T t1, T t2) throws UFException {
        return connected(lookup(t1), lookup(t2));
    }

    /**
     * Merges the components containing the two given elements of type T.
     * This method maps the specified elements to their corresponding internal indices
     * and performs a union operation on those indices.
     *
     * @param t1 the first element of type T
     * @param t2 the second element of type T
     * @throws UFException if either t1 or t2 is not an element managed by the union-find structure
     */
    public void union(T t1, T t2) throws UFException {
        union(lookup(t1), lookup(t2));
    }

    /**
     * Looks up the integer index associated with the given element in the internal mapping of elements.
     * If the element is not found in the mapping, a UFException is thrown.
     *
     * @param t the element of type T whose corresponding index is to be retrieved
     * @return the integer index associated with the given element
     * @throws UFException if the given element is not found in the internal mapping
     */
    private int lookup(T t) throws UFException {
        Integer x = map.get(t);
        if (x != null) return x;
        else throw new UFException("Element " + t + " does not exist");
    }

    final private Map<T, Integer> map;
}