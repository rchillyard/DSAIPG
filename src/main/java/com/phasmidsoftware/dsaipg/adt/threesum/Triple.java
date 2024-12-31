/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.threesum;

import java.util.Objects;

/**
 * The Triple class represents an immutable triplet of integers and provides functionality
 * for summing its components, comparing instances for equality and ordering, and generating
 * hash codes and string representations.
 * This class implements the {@code Comparable<Triple>} interface to support natural ordering
 * based on its components.
 */
public class Triple implements Comparable<Triple> {
    /**
     * Computes the sum of the three components (x, y, and z) of this instance.
     *
     * @return the sum of x, y, and z as an integer.
     */
    public int sum() {
        return x + y + z;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    /**
     * Compares this Triple object with the specified object for equality.
     * Two Triple objects are considered equal if and only if their x, y, and z components
     * are all equal.
     *
     * @param o the object to be compared with this Triple for equality.
     * @return true if the specified object is equal to this Triple; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple triple)) return false;
        return x == triple.x && y == triple.y && z == triple.z;
    }

    /**
     * Computes the hash code for this object based on its components.
     * The hash code is generated using the {@code hash} method from the {@code Objects} utility class,
     * ensuring a consistent and efficient distribution for hashing purposes.
     *
     * @return the hash code value for this object as an integer derived from x, y, and z components.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    /**
     * Compares this Triple instance with the specified Triple for order.
     * The comparison is primarily based on the x-component.
     * If the x-components
     * are equal, the y-components are compared. If the y-components are also
     * equal, the z-components are compared.
     *
     * @param o the Triple object to be compared with this Triple.
     * @return a negative integer, zero, or a positive integer as this Triple
     * is less than, equal to, or greater than the specified Triple, respectively.
     */
    public int compareTo(Triple o) {
        int cf1 = this.x - o.x;
        if (cf1 != 0) return cf1;
        int cf2 = this.y - o.y;
        if (cf2 != 0) return cf2;
        return this.z - o.z;
    }

    /**
     * Constructs a Triple with the specified x, y, and z integer values.
     *
     * @param x the x-coordinate or first component of the triple.
     * @param y the y-coordinate or second component of the triple.
     * @param z the z-coordinate or third component of the triple.
     */
    public Triple(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    final int x;
    final int y;
    final int z;
}