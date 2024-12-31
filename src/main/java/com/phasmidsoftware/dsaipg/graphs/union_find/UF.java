/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.union_find;

/**
 * The UF interface models the Union-Find or Disjoint-Set data structure.
 * It provides methods for tracking and merging disjoint sets, as well as for determining
 * if two elements are in the same set.
 * This interface extends the Connections interface.
 */
public interface UF extends Connections {
    /**
     * Returns the number of components.
     *
     * @return the number of components (between {@code 1} and {@code n})
     */
    int components();

    /**
     * Returns the component identifier for the component containing site {@code p}.
     *
     * @param p the integer representing one site
     * @return the component identifier for the component containing site {@code p}
     * @throws IllegalArgumentException unless {@code 0 <= p < n}
     */
    int find(int p);

    /**
     * Merges the component containing site {@code p} with
     * the component containing site {@code q}.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    void union(int p, int q);

    /**
     * Returns true if the two sites are in the same component.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @return {@code true} if the two sites {@code p} and {@code q} are in the same component;
     * {@code false} otherwise
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    default boolean isConnected(int p, int q) {
        return find(p) == find(q);
    }

    /**
     * Returns the number of sites (objects) in this UF object.
     *
     * @return the number of sites.
     */
    int size();
}