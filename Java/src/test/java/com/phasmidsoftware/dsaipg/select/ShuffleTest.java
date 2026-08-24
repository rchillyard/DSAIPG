package com.phasmidsoftware.dsaipg.select;

import org.junit.Test;

import java.util.Arrays;

import static com.phasmidsoftware.dsaipg.select.Shuffle.calculateNBits;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class ShuffleTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testShuffle0() {
        Integer[] a = {};
        Integer[] expected = {};
        Entropy entropy = new Entropy(calculateNBits(0));
        Integer[] result = new Shuffle<>(a, entropy).shuffle();
        System.out.println("Result: " + Arrays.toString(result));
        assertArrayEquals(expected, result);
    }

    @Test
    public void testShuffle1() {
        Integer[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] expected = {10, 4, 6, 3, 8, 9, 1, 2, 7, 5};
        Entropy.seed = 0xAAAAAAAAL;
        Entropy entropy = new Entropy(calculateNBits(10));
        Integer[] result = new Shuffle<>(a, entropy).shuffle();
        System.out.println("Result: " + Arrays.toString(result));
        assertArrayEquals(expected, result);
        Entropy.seed = 2025L;
    }

    @Test
    public void testShuffle2() {
        Integer[] a = new Integer[52];
        Integer[] expected = new Integer[]{15, 50, 36, 43, 22, 2, 19, 44, 47, 21, 26, 12, 13, 46, 38, 40, 6, 31, 29, 35, 33, 37, 39, 32, 8, 11, 5, 20, 41, 18, 34, 48, 28, 9, 3, 1, 27, 10, 17, 30, 49, 7, 24, 42, 52, 51, 45, 4, 16, 14, 25, 23};
        for (int i = 0; i < a.length; i++) a[i] = i + 1;
        byte[] rawBits = {(byte) 0xF0, (byte) 0xB5, (byte) 0xA1, 0x51, 0x78, 0x3B, (byte) 0xEB, (byte) 0xCB, 0x07, 0x39, 0x3D, (byte) 0xF4, (byte) 0xF4, (byte) 0x9B, 0x5E, 0x6B, (byte) 0xB1, (byte) 0xBE, (byte) 0x94, (byte) 0xAA, 0x5B, 0x18, 0x12, (byte) 0xFD, (byte) 0xCF, 0x50, 0x7F, 0x19, (byte) 0xB4, (byte) 0xBF, 0x09, (byte) 0x9F};
        Entropy entropy = new Entropy(rawBits);
        Integer[] result = new Shuffle<>(a, entropy).shuffle();
        System.out.println("Result: " + Arrays.toString(result));
        assertArrayEquals(expected, result);
    }

    @Test
    public void testShuffle3() {
        Integer[] a = new Integer[52];
        for (int i = 0; i < a.length; i++) a[i] = i + 1;
        Integer[] result = new Shuffle<>(a).shuffle();
        System.out.println("Result: " + Arrays.toString(result));
    }

    @Test
    public void testPowersOfTwo() {
        assertEquals(1, Shuffle.powersOf2(0));
        assertEquals(2, Shuffle.powersOf2(1));
        assertEquals(4, Shuffle.powersOf2(2));
        assertEquals(8, Shuffle.powersOf2(3));
        assertEquals(16, Shuffle.powersOf2(4));
    }

    @Test
    public void testCalculateNBits() {
        assertEquals(0, calculateNBits(0));
        assertEquals(0, calculateNBits(1));
        assertEquals(1, calculateNBits(2));
        assertEquals(3, calculateNBits(3));
        assertEquals(5, calculateNBits(4));
        assertEquals(8, calculateNBits(5));
        assertEquals(11, calculateNBits(6));
        assertEquals(14, calculateNBits(7));
        assertEquals(17, calculateNBits(8));
        assertEquals(25, calculateNBits(10));
        assertEquals(29, calculateNBits(11));
        assertEquals(33, calculateNBits(12));
        assertEquals(49, calculateNBits(16));
        assertEquals(129, calculateNBits(32));
        assertEquals(249, calculateNBits(52));
        assertEquals(321, calculateNBits(64));
    }
}