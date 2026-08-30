package com.phasmidsoftware.dsaipg.select;

import java.util.Arrays;

/**
 * The `Shuffle` class provides utility methods for shuffling an array of objects.
 * It implements the Fisher-Yates (Knuth) shuffle algorithm to randomize the order of elements in the array.
 * NOTE that number of bits `B(n)` of entropy required for the shuffle is based on the length `n` of any given array.
 * Ideally, the number of bits is `lg n!` (approximately `n lg n - 1.44 n`)
 * In practice, however, because of wastage in the Knuth shuffle mechanism, the algorithm actually requires more.
 * The formula for n is derived as follows (where `k` represents a power of 2):
 * <ul>
 *     <li>`n = 2^k`</li>
 *     <li>`B(k) = (k-1)*n + 1`</li>
 * </ul>
 * Where `n` is not a power of 2, then an appropriate adjustment is made by counting down,
 * `k` at a time, from the value for the next higher power of k.
 * Here are some examples:
 * <dl>
 *     <dt>4</dt><dd>5 instead of 4</dd>
 *     <dt>6</dt><dd>11, instead of 6.87</dd>
 *     <dt>12</dt><dd>33, instead of 25.75</dd>
 *     <dt>52</dt><dd>249, instead of 221.5</dd>
 *     <dt>64</dt><dd>321, instead of 291.8</dd>
 * </dl>
 * For more cases, see ShuffleTest.java
 */
public class Shuffle<X> {

    /**
     * Shuffles the elements of the initial array in random order using the Knuth shuffle algorithm.
     *
     * @return a new array containing the elements of the initial array, randomized in order.
     */
    public X[] shuffle() {
        X[] result = Arrays.copyOf(a, a.length);
        for (int i = 1; i < a.length; i++) {
            int random = (int) entropy.getRandom(i + 1L);
            swap(result, i, random);
        }
        return result;
    }

    /**
     * Constructs a new instance of the Shuffle class with the specified array and entropy source.
     *
     * @param a       the array of objects to be shuffled. This array is not modified during the shuffle operation.
     * @param entropy an entropy source used to generate random numbers for shuffling.
     *                It ensures randomization is performed with a defined level of entropy.
     */
    public Shuffle(X[] a, Entropy entropy) {
        this.a = a;
        this.entropy = entropy;
    }

    /**
     * Constructs a new instance of the Shuffle class with the specified array.
     * The entropy source is generated automatically based on the size of the array.
     *
     * @param a the array of objects to be shuffled. This array is not modified during the shuffle operation.
     */
    public Shuffle(X[] a) {
        this(a, getEntropy(a.length));
    }

    private final Entropy entropy;
    private final X[] a;

    /**
     * Calculates the entropy for a given input size and returns an Entropy object.
     * The entropy is calculated based on the number of bits derived from the input size.
     *
     * @param n the input size for which entropy is to be calculated. It must be a non-negative integer.
     * @return an Entropy object that encapsulates the calculated entropy bits.
     */
    static Entropy getEntropy(int n) {
        return new Entropy(calculateNBits(n));
    }

    /**
     * Calculates the total number of bits required to represent entropy for a given input size.
     * This method determines the bits based on powers of 2 and their contributions to the total entropy.
     *
     * @param n the input size for which the number of bits is to be calculated. Must be a positive integer.
     * @return the number of bits required to represent the entropy for the given input size.
     */
    static int calculateNBits(int n) {
        int bits = 0;
        int k = 0;
        int m = 1;
        while (m < n) {
            int j = powersOf2(k);
            m += j;
            k++;
            bits += k * j;
        }
        return bits - (m - n) * k;
    }

    /**
     * Knuth shuffle
     * In iteration i, pick integer r between zero and i uniformly at random
     * Swap a[i] and a[r].
     * TODO eliminate this method: it is only for QuickSelect
     *
     * @param a the array
     */
    static void shuffle(Object[] a) {
        int length = a.length;
        if (length < 2) return;
        Shuffle<Object> shuffle = new Shuffle<>(a);
        System.arraycopy(shuffle.shuffle(), 0, a, 0, length);
    }

    /**
     * Computes the power of 2 raised to the given exponent.
     *
     * @param k the exponent to raise 2 to. Must be a non-negative integer.
     * @return the result of 2 raised to the power k.
     */
    static int powersOf2(int k) {
        return 1 << k;
    }

    /**
     * Swaps the elements at the specified positions in the given array.
     * If the specified positions are the same, no operation is performed.
     *
     * @param a the array in which the elements are to be swapped. Must not be null.
     * @param i the index of the first element to swap. Must be within the array bounds.
     * @param j the index of the second element to swap. Must be within the array bounds.
     */
    private static void swap(Object[] a, int i, int j) {
        if (i == j) return;
        Object temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
