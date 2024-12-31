/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.graphs.union_find;

import java.util.Arrays;

/**
 * Height-weighted Quick Union with Path Compression
 */
public class UF_HWQUPC implements UF {
    /**
     * Ensure that site @{Code p} is connected to site @{Code q},
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     */
    public void connect(int p, int q) {
        if (!isConnected(p, q)) union(p, q);
    }

    /**
     * Initializes an empty union–find data structure with {@code n} sites
     * {@code 0} through {@code n-1}. Each site is initially in its own
     * component.
     *
     * @param n               the number of sites
     * @param pathCompression whether to use path compression
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public UF_HWQUPC(int n, boolean pathCompression) {
        count = n;
        parent = new int[n];
        height = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            height[i] = 1;
        }
        this.pathCompression = pathCompression;
    }

    /**
     * Initializes an empty union–find data structure with {@code n} sites
     * {@code 0} through {@code n-1}. Each site is initially in its own
     * component.
     * This data structure uses path compression
     *
     * @param n the number of sites
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public UF_HWQUPC(int n) {
        this(n, true);
    }

    /**
     * Displays the current state of the union-find data structure.
     * Specifically, it iterates over the parent and height arrays and prints
     * the values associated with each index. For each index, it shows:
     * - The index
     * - The parent of the corresponding element
     * - The height of the corresponding element
     * This method is useful for visualizing and debugging the internal state
     * of the union-find implementation.
     */
    public void show() {
        for (int i = 0; i < parent.length; i++) {
            System.out.printf("%d: %d, %d\n", i, parent[i], height[i]);
        }
    }

    /**
     * Returns the number of components.
     *
     * @return the number of components (between {@code 1} and {@code n})
     */
    public int components() {
        return count;
    }

    /**
     * Returns the component identifier for the component containing site {@code p}.
     *
     * @param p the integer representing one site
     * @return the component identifier for the component containing site {@code p}
     * @throws IllegalArgumentException unless {@code 0 <= p < n}
     */
    public int find(int p) {
        validate(p);
        int root = p;
        // TO BE IMPLEMENTED 
throw new RuntimeException("implementation missing");
    }

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
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    /**
     * Merges the component containing site {@code p} with the
     *  component containing site {@code q}.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public void union(int p, int q) {
        // CONSIDER can we avoid doing find again?
        mergeComponents(find(p), find(q));
        count--;
    }

    /**
     * Returns the number of elements in the Union–Find data structure.
     *
     * @return the number of sites in the union–find data structure.
     */
    public int size() {
        return parent.length;
    }

    /**
     * Used only by testing code
     *
     * @param pathCompression true if you want path compression
     */
    public void setPathCompression(boolean pathCompression) {
        this.pathCompression = pathCompression;
    }

    @Override
    public String toString() {
        return "UF_HWQUPC:" + "\n  count: " + count +
                "\n  path compression? " + pathCompression +
                "\n  parents: " + Arrays.toString(parent) +
                "\n  heights: " + Arrays.toString(height);
    }

    /**
     * Validates that the given index is within the valid range for the union–find data structure.
     *
     * @param p the index to validate
     * @throws IllegalArgumentException if {@code p} is not between 0 (inclusive) and {@code n-1} (inclusive),
     *                                  where {@code n} is the size of the parent array.
     */
    private void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n - 1));
        }
    }

    /**
     * Updates the parent of a specified site to a new value.
     *
     * @param p the index of the site whose parent is to be updated
     * @param x the new value to be set as the parent of the site
     */
    private void updateParent(int p, int x) {
        parent[p] = x;
    }

    /**
     * Updates the height of the component associated with site p by adding the height of the component
     * associated with site x. This is used during the union operation to maintain accurate heights
     * of components.
     *
     * @param p the index of the site whose component's height is to be updated
     * @param x the index of the site whose component's height will be added to site p
     */
    private void updateHeight(int p, int x) {
        height[p] += height[x];
    }

    /**
     * Used only by testing code
     *
     * @param i the component
     * @return the parent of the component
     */
    private int getParent(int i) {
        return parent[i];
    }

    /**
     * Merges the components containing sites i and j. The smaller tree
     * (determined by height) is made a subtree of the larger tree to
     * keep the structure balanced and minimize depth.
     *
     * @param i the index of the first component to be merged
     * @param j the index of the second component to be merged
     */
    void mergeComponents(int i, int j) {
        // TO BE IMPLEMENTED  make shorter root point to taller one
        // END SOLUTION
    }

    /**
     * This implements the single-pass path-halving mechanism of path compression
     */
    void doPathCompression(int i) {
        // TO BE IMPLEMENTED  update parent to value of grandparent
        // END SOLUTION
    }

    private final int[] parent;   // parent[i] = parent of i
    private final int[] height;   // height[i] = height of subtree rooted at i
    private int count;  // number of components
    private boolean pathCompression;
}