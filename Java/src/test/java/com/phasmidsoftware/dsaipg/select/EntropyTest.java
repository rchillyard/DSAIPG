package com.phasmidsoftware.dsaipg.select;

import org.junit.Test;

import java.util.Random;

import static com.phasmidsoftware.dsaipg.select.Entropy.log2;
import static com.phasmidsoftware.dsaipg.select.Shuffle.calculateNBits;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class EntropyTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testGetRandomWithSeed() {
        Entropy.seed = new Random(1L).nextLong() & 0x7fffffffffffffffL;
        Entropy entropy = new Entropy(40);
        long expected = 867L;
        long actual = entropy.getRandom(1024L);
        assertEquals(expected, actual);
        Entropy.seed = 0;
    }

//    @Test
    public void testGetEntropy0() {
        Entropy entropy = new Entropy(25);
        assertEquals(32, entropy.getBits());
        System.out.println(entropy);
        entropy.getEntropy(8);
        entropy.getEntropy(8);
        entropy.getEntropy(8);
        long actual = entropy.getEntropy(8);
        // The following assertion must be adjusted every few days as it is only good for about 66 2/3 hours.
        long expected = 0;
        assertEquals(expected, actual >> 4);
    }

//    @Test
    public void testGetEntropy1() {
        Entropy entropy = new Entropy(25);
        assertEquals(32, entropy.getBits());
        System.out.println(entropy);
        long six = entropy.getEntropy(6);
        System.out.println(six);
        assertEquals(26, entropy.getBits());
        entropy.getEntropy(8);
        entropy.getEntropy(8);
        entropy.getEntropy(8);
        // The following assertion must be adjusted every few days as it is only good for about 66 2/3 hours.
        long expected = 0;
        long actual = entropy.getEntropy(2);
        assertEquals(expected, actual);
        assertEquals(0, entropy.getBits());
    }

    @Test
    public void testGetEntropy2() {
        byte[] rawBits = {(byte) 0xF0, (byte) 0xB5, (byte) 0xA1, 0x51, 0x78, 0x3B, (byte) 0xEB, (byte) 0xCB, 0x07, 0x39, 0x3D, (byte) 0xF4, (byte) 0xF4, (byte) 0x9B, 0x5E, 0x6B, (byte) 0xB1, (byte) 0xBE, (byte) 0x94, (byte) 0xAA, 0x5B, 0x18, 0x12, (byte) 0xFD, (byte) 0xCF, 0x50, 0x7F, 0x19, (byte) 0xB4, (byte) 0xBF, 0x09, (byte) 0x9F};
        Entropy entropy = new Entropy(rawBits);
        assertEquals(32, entropy.getEntropy().length);
        assertEquals(256, entropy.getBits());
        assertEquals(1L, entropy.getEntropy(1));
        assertEquals(3L, entropy.getEntropy(2));
        assertEquals(3L, entropy.getEntropy(2));
        assertEquals(9L, entropy.getEntropy(4));
        System.out.println(entropy);
    }

    @Test
    public void testGetEntropy3() {
        Entropy entropy = new Entropy(calculateNBits(12));
        assertEquals(5, entropy.getEntropy().length);
        assertEquals(40, entropy.getBits());
        System.out.println(entropy);
    }

    @Test
    public void testGetEntropyValidInputs() {
        Entropy.seed = 0xffffffffffffffffL;
        Entropy entropy = new Entropy(32);
        assertEquals(32, entropy.getBits());
        long result1 = entropy.getEntropy(8);
        long result2 = entropy.getEntropy(16);
        long result3 = entropy.getEntropy(8);
        assertEquals(8, Long.bitCount(result1));  // Validating 8-bit output
        assertEquals(16, Long.bitCount(result2)); // Validating 16-bit output
        assertEquals(8, Long.bitCount(result3));  // Validating 8-bit output
        Entropy.seed = 0L;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEntropyInvalidInputs() {
        Entropy entropy = new Entropy(32);
        entropy.getEntropy(-1); // Invalid negative bits
        entropy.getEntropy(0);  // Invalid zero bits
        entropy.getEntropy(65); // Invalid bits over 64
        entropy.getEntropy(100); // Invalid bits exceeding the available bits
    }

    @Test
    public void testGetEntropyBoundaryValues1() {
        Entropy.seed = 0xffffffffffffffffL;
        Entropy entropy = new Entropy(64);
        long singleBit = entropy.getEntropy(1);
        assertEquals(1, Long.bitCount(singleBit)); // Validates 1 bit is returned
        Entropy.seed = 0;
    }

    @Test
    public void testGetEntropyBoundaryValues2() {
        Entropy.seed = 0xffffffffffffffffL;
        Entropy entropy = new Entropy(64);
        long sixtyFourBits = entropy.getEntropy(64);
        assertEquals(64, Long.bitCount(sixtyFourBits)); // Validates 64 bits are returned
        Entropy.seed = 0;
    }

    @Test
    public void testLog2() {
        assertEquals(0, log2(1));
        assertEquals(1, log2(2));
        assertEquals(2, log2(3));
        assertEquals(2, log2(4));
        assertEquals(3, log2(5));
        assertEquals(3, log2(8));
    }
}