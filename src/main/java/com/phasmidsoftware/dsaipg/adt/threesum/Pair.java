/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.threesum;

import java.util.Objects;

/**
 * The Pair class represents an immutable pair of integers and provides utility methods for comparison,
 * hashing, and summation. This class implements the {@code Comparable<Pair>} interface to support natural ordering.
 */
public class Pair implements Comparable<Pair> {
    public int sum() {
        return x + y;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair triple)) return false;
        return x == triple.x && y == triple.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Compares this Pair object with the specified Pair object for order.
     * The comparison is primarily based on the x-coordinate of the pairs.
     * If the x-coordinates are equal, the y-coordinates are compared.
     *
     * @param o the Pair object to be compared with this Pair.
     * @return a negative integer, zero, or a positive integer as this Pair
     * is less than, equal to, or greater than the specified Pair, respectively.
     */
    public int compareTo(Pair o) {
        int cf1 = this.x - o.x;
        if (cf1 != 0) return cf1;
        return this.y - o.y;
    }

    /**
     * Constructs a Pair with the given x and y integer values.
     *
     * @param x the x-coordinate of the pair.
     * @param y the y-coordinate of the pair.
     */
    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The x-coordinate of the pair. This field is immutable and represents the first component
     * of the Pair instance.
     */
    final int x;
    /**
     * The y-coordinate of the pair. This field is immutable and represents the second component
     * of the Pair instance.
     */
    final int y;
}