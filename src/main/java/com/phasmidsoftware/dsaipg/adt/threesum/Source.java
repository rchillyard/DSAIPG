/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.threesum;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

/**
 * The Source class provides a source of entropy and a set of utilities
 * for generating arrays of integers based on specific constraints,
 * and designed for use in scenarios like
 * testing algorithms or data structures that require randomly generated inputs.
 */
class Source {
    /**
     * Primary constructor of a Source instance with the specified values for N, M, and a Random instance.
     *
     * @param N      the number of integers to generate.
     * @param M      the range of integers, where each integer is in the range -M through M-1.
     * @param random the Random instance used for generating random numbers.
     */
    public Source(int N, int M, Random random) {
        n = N;
        m = M;
        this.random = random;
    }

    /**
     * Secondary constructor of a Source instance with the specified number of integers, the range of integers,
     * and a seed value for the random number generator.
     *
     * @param N    the number of integers to generate.
     * @param M    the range of integers, where each integer is in the range -M through M-1.
     * @param seed the seed value to initialize the random number generator.
     */
    public Source(int N, int M, long seed) {
        this(N, M, new Random(seed));
    }

    /**
     * Secondary constructor of a Source instance with the specified number of integers and the range of integers.
     * A default Random instance will be used for generating random numbers.
     *
     * @param N the number of integers to generate.
     * @param M the range of integers, where each integer is in the range -M through M-1.
     */
    public Source(int N, int M) {
        this(N, M, new Random());
    }

    /**
     * Generates a {@code Supplier} that provides an array of {@code n} integers,
     * generated based on the specified safety factor and ensuring specific properties:
     * - The array is derived from a larger array of random integers scaled by the safetyFactor.
     * - Each value in the resulting array is distinct.
     * - The output array is ordered and uniformly samples the distinct values.
     *
     * @param safetyFactor the safety factor which determines the size of the initial integer pool
     *                     and the range of random integers generated. The factor increases the distribution
     *                     of values in the initial pool compared to the final array.
     * @return a {@code Supplier} of an integer array, containing {@code n} distinct, ordered integers
     *         uniformly sampled from a sorted set of random integers.
     */
    public Supplier<int[]> intsSupplier(int safetyFactor) {
        return () -> {
            int[] ints = (int[]) Array.newInstance(int.class, safetyFactor * n);
            for (int i = 0; i < ints.length; i++) ints[i] = random.nextInt(safetyFactor * m) - safetyFactor * m / 2;
            Arrays.sort(ints);
            int[] distinct = Arrays.stream(ints).distinct().toArray();
            int[] result = (int[]) Array.newInstance(int.class, n);
            for (int i = 0; i < n; i++)
                result[i] = distinct[i * (distinct.length / n)];
            return result;
        };
    }

    private final int n;
    private final int m;
    private final Random random;
}