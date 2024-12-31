/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import com.phasmidsoftware.dsaipg.misc.equable.BaseEquable;
import com.phasmidsoftware.dsaipg.misc.equable.Equable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class serves as a test harness for Equable.
 */
public class Tuple extends BaseEquable {

    /**
     * Constructs a Tuple with the specified integer and double values.
     *
     * @param x the integer value for this tuple.
     * @param y the double value for this tuple.
     */
    public Tuple(int x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the value of the x-coordinate.
     *
     * @return the integer value of x.
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the value of the y-coordinate.
     *
     * @return the y-coordinate as a double.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns a string representation of the {@code Tuple} object.
     * The string representation includes the values of the x and y components
     * formatted as "Tuple(x, y)".
     *
     * @return a string representation of the {@code Tuple} object.
     */
    @Override
    public String toString() {
        return "Tuple(" + x + ", " + y + ")";
    }

    /**
     * Creates and returns a new instance of the Equable class representing the elements of this Tuple.
     * The Equable instance is constructed with the `x` and `y` values of this Tuple.
     *
     * @return an Equable instance initialized with a collection containing this Tuple's `x` and `y` values.
     */
    public Equable getEquable() {
        Collection<Object> elements = new ArrayList<>();
        elements.add(x);
        elements.add(y);
        return new Equable(elements);
    }

    /**
     * Calculates an index based on the given hash value by performing bitwise operations.
     * The index is computed by XOR'ing the higher and lower 16 bits of the hash.
     *
     * @param hash the hash value for which the index is to be computed.
     * @return the computed index based on the hash value.
     */
    public static int index(int hash) {
        System.out.printf("hash: %x\n", hash);
        int x = (hash & 0xFFFF0000) >>> 16 ^ hash & 0x0000FFFF;
        System.out.printf("index: %x\n", x);
        return x;
    }

    private final int x;
    private final double y;
}
