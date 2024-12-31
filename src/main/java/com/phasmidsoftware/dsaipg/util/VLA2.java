/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import java.util.Arrays;
import java.util.Comparator;

/**
 * The VLA2 class demonstrates an example implementation of sorting and binary searching
 * an array of objects based on a custom property using a comparator.
 * <p>
 * This class includes a main method that sorts an array of `VLA2` objects based on their
 * `dishSize` field and performs binary searches to locate specific values.
 * <p>
 * The `dishSize` field is a final property initialized through the constructor
 * to represent the size of a dish associated with a VLA2 instance.
 */
public class VLA2 {
    public VLA2(int d) {
        dishSize = d;
    }

    public static void main(String[] args) {
        Comparator<VLA2> cf = Comparator.comparingInt(o -> o.dishSize);
        VLA2[] va = {new VLA2(40), new VLA2(200), new VLA2(60)};
        Arrays.sort(va, cf);
        int index = Arrays.binarySearch(va, new VLA2(40), cf);
        System.out.print(index + " ");
        index = Arrays.binarySearch(va, new VLA2(80), cf);
        System.out.print(index >= 0);
    }

    private final int dishSize;
}