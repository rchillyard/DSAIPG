/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.graphs.union_find;

/**
 * The WQUPC class provides a weighted quick-union implementation with path compression
 * for the union–find (disjoint-set) data structure. This implementation can be used
 * to model and manipulate equivalence classes efficiently.
 * The class uses an integer-array-based representation where each site is
 * associated with an integer from 0 to n-1. It provides methods to:
 * - Find the connected component containing a specific site.
 * - Union two sets into one.
 * - Determine whether two sites are in the same connected component.
 * - Count the number of independent sets (or components).
 * The "weighted" approach ensures that smaller trees are always
 * merged under larger trees to maintain a more balanced structure.
 * The "path compression" ensures that trees remain nearly flat
 * for faster future operations.
 * This implementation ensures efficient union-find operations. The find,
 * union, and connected operations take time proportional to the logarithm
 * of the number of sites (amortized time complexity), while the count
 * operation takes constant time.
 */
public class WQUPC {

    /**
     * Initializes an empty union–find data structure with {@code n} sites
     * {@code 0} through {@code n-1}. Each site is initially in its own
     * component.
     *
     * @param n the number of sites
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public WQUPC(int n) {
        count = n;
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    /**
     * Displays the current state of the union-find structure.
     * <p>
     * This method prints the index, parent array value, and size array value
     * for each site in the union-find structure. The output can be used to debug
     * or inspect the internal state of the data structure.
     */
    public void show() {
        for (int i = 0; i < parent.length; i++) {
            System.out.printf("%d: %d, %d\n", i, parent[i], size[i]);
        }
    }

    /**
     * Returns the number of components.
     *
     * @return the number of components (between {@code 1} and {@code n})
     */
    public int count() {
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
        while (root != parent[root]) {
            root = parent[root];
        }
        while (p != root) {
            int newp = parent[p];
            parent[p] = root;
            p = newp;
        }
        return root;
    }

    /**
     * Validates that the given index is within the permissible range of the parent array.
     *
     * @param p the index to be validated
     * @throws IllegalArgumentException if the index {@code p} is less than 0 or greater than or equal to the length of the parent array
     */
    void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n - 1));
        }
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
     * component containing site {@code q}.
     *
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @throws IllegalArgumentException unless
     *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
     */
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;
        // make smaller root point to larger one
        if (size[rootP] < size[rootQ]) {
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];
        } else {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];
        }
        count--;
    }

    private final int[] parent;   // parent[i] = parent of i
    private final int[] size;   // size[i] = size of subtree rooted at i
    private int count;  // number of components
}