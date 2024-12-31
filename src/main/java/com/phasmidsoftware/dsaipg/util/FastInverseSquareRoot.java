/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

/**
 * A utility class for computing the fast inverse square root of a floating-point number.
 * This implementation uses a combination of bit manipulation and Newton's method
 * for calculating an approximate inverse square root efficiently.
 * The algorithm originates from a method popularized by the Quake III Arena engine.
 * More detail here: <a href="https://en.wikipedia.org/wiki/Fast_inverse_square_root">...</a>
 */
public class FastInverseSquareRoot {

    /**
     * Computes the fast inverse square root of a given floating-point number using
     * a combination of bit manipulation and Newton's method. The level of optimization
     * depends on the number of iterations specified.
     *
     * @param x        the input number for which the inverse square root is to be calculated.
     *                 Must be a positive floating-point value.
     * @param optimize the number of Newton's method iterations to refine the result.
     *                 Higher values may improve accuracy at the cost of performance.
     * @return the approximate inverse square root of the input number.
     */
    static float invSqrt(final float x, int optimize) {
        final int MAGIC_NUMBER = 0x5f3759df;
        final float x2 = x * 0.5f;
        int number = Float.floatToIntBits(x);
        number = MAGIC_NUMBER - (number >> 1);
        float result = Float.intBitsToFloat(number);
        while (optimize-- > 0) result *= (1.5f - (x2 * result * result));
        return result;
    }

    public static void main(final String[] args) {
        final float result = invSqrt(3.1415927f, 0);
        System.out.println(result);
        System.out.println(1.0f / (result * result));
    }

}