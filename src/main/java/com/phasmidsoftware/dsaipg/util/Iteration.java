/*
 * Copyright (c) 2018-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

/**
 * A utility class that provides methods to perform operations involving arrays using iteration.
 * CONSIDER removing this class--it doesn't seem to do anything useful.
 */
public class Iteration {

    public static int sum(int[] array) {
        int result = 0;
        for (int i : array) result += i;
        return result;
    }

    public static void main(String[] args) {
        int[] array = new int[10];
        for (int i = 0; i < 10; i++) array[i] = i + 1;
        System.out.println(sum(array));
    }
}