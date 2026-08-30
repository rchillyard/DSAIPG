/*
 * Copyright (c) 2018. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.misc;

public class CallByValue {

    /**
     * Increments the given integer by 1 and returns the result.
     *
     * @param number the integer to be incremented
     * @return the incremented value of the input integer
     */
    // Hide "number" field
    public int incrementNumber1(int number) {
        number++;
        return number;
    }

    /**
     * Increments the field "number" by 1 and returns the updated value.
     *
     * @return the incremented value of the field "number".
     */
    // Access "number" field
    public int incrementNumber2() {
        number++;
        return number;
    }

    /**
     * This method attempts to increment each element of the input array by one using a foreach loop.
     * However, the increment operation does not affect the original array due to how Java handles primitive types
     * inside a foreach loop.
     *
     * @param array an array of integers which should be incremented element-wise
     * @return the original array without any modification
     */
    // Also compare foreach loop and for loop
    public int[] incrementArray1(int[] array) {
        for (int a : array) {
            //noinspection UnusedAssignment
            a++; // CONSIDER what is going on here?
        }
        return array;
    }

    /**
     * Increments each element of the given array by 1.
     *
     * @param array the array of integers to be incremented
     * @return the modified array with each element incremented by 1
     */
    public int[] incrementArray2(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i]++;
        }
        return array;
    }

    /**
     * Attempts to increment each element of the array field by 1 using a foreach loop.
     * However, the foreach loop operates on a copy of each array element and does not modify the original array.
     *
     * @return the original array without any modification
     */
    public int[] incrementArray3() {
        for (int a : array) {
            //noinspection UnusedAssignment
            a++; // CONSIDER what is going on here?
        }
        return array;
    }

    /**
     * Increments each element of the array field "array" by 1.
     *
     * @return the modified array with each element incremented by 1.
     */
    public int[] incrementArray4() {
        for (int i = 0; i < array.length; i++) {
            array[i]++;
        }
        return array;
    }

    /**
     * This field represents an integer variable used to hold a numeric value.
     * It is initialized to 0 by default and can be incremented or used in various operations
     * depending on the methods manipulating it within the containing class.
     */
    private int number = 0;
    /**
     * A private final integer array field initialized with a single element, 0.
     * This field represents an array that can be used in various operations within the class,
     * such as incrementing its elements or passing it as a parameter to methods.
     * Since this array is declared final, its reference cannot be reassigned,
     * but the contents of the array can still be modified.
     */
    private final int[] array = {0};

    /**
     * The main method demonstrates the behavior of various methods in the CallByValue class.
     *
     * @param args command-line arguments passed to the program
     */
    public static void main(String[] args) {
        CallByValue cbv = new CallByValue();

        cbv.incrementNumber1(cbv.number);
        System.out.println(cbv.number);

        cbv.incrementNumber2();
        System.out.println(cbv.number);

        cbv.incrementArray1(cbv.array);
        System.out.println(cbv.array[0]);

        cbv.incrementArray2(cbv.array);
        System.out.println(cbv.array[0]);

        cbv.incrementArray3();
        System.out.println(cbv.array[0]);

        cbv.incrementArray4();
        System.out.println(cbv.array[0]);
    }
}

