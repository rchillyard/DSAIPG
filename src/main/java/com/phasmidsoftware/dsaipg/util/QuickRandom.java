/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

/**
 * Fast, simple pseudo-random number generator providing 31 bits of entropy.
 */
public class QuickRandom {

    /**
     * Get the next (positive) random number which is at least 0 and less than N.
     *
     * @return the value of get(0).
     */
    public int get() {
        return get(0);
    }

    /**
     * Get the next random number which is at least m and less than N.
     *
     * @return a pseudo-random number between m (inclusive) and N (exclusive).
     */
    public int get(int m) {
        r ^= r << 13;
        r ^= r >> 17;
        r ^= r << 5;
        r &= 0x7FFFFFFF;
        return r % (N - m) + m;
    }

    /**
     * Constructs a QuickRandom instance with a specified upper limit and a seed for random number generation.
     *
     * @param N    the upper bound (exclusive) for the pseudo-random numbers generated.
     * @param seed the initial seed for the random number generator.
     */
    public QuickRandom(int N, long seed) {
        this.N = N;
        r = (int) seed;
    }

    /**
     * Constructs a QuickRandom object with numbers ranging from 0 to N and initializes the random number generator
     * with the current system time as a seed.
     *
     * @param N the upper bound of the random number range (exclusive) for generated values.
     */
    public QuickRandom(int N) {
        this(N, System.currentTimeMillis());
    }

    /**
     * Constructs a QuickRandom instance with a default upper bound of {@link Integer#MAX_VALUE}.
     *
     * @param seed the initial seed value for the pseudo-random number generator.
     */
    public QuickRandom(long seed) {
        this(Integer.MAX_VALUE, seed);
    }

    /**
     * Default constructor for QuickRandom.
     * Initializes a QuickRandom instance with the maximum possible integer value
     * as the upper bound for random number generation, using the current system
     * time as the seed.
     */
    public QuickRandom() {
        this(Integer.MAX_VALUE);
    }


    private final int N;
    private int r;
}