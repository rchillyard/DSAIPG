package com.phasmidsoftware.dsaipg.select;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * The Entropy class facilitates the generation and management of entropy data.
 * It provides functionality to extract a specific number of bits from an internal entropy source
 * and supports secure and system-clock-based entropy generation.
 * This class ensures the controlled use and extraction of entropy to avoid over-consumption.
 * The (singleton) `SecureRandom` instance is seeded from `/dev/urandom`.
 * NOTE the purpose of this class is pedagogical (i.e., for teaching).
 * In particular, it shows how you might use the raw bits from `SecureRandom` to satisfy requests for a random integer within a range.
 * In practice, you would probably the `nextInt(n)` method from `SecureRandom` directly.
 */
public class Entropy {

    /**
     * Retrieves the number of available bits of entropy.
     *
     * @return the number of bits currently represented by this instance.
     */
    public int getBits() {
        return bits;
    }

    /**
     * Generates a long value by extracting a specified number of bits from an internal entropy source.
     * This method ensures that the requested number of bits is less than or equal to 64, positive,
     * and within the bounds of the available entropy.
     *
     * @param bits the number of bits to extract. It must be a positive integer, no more than 64, and
     *             less than or equal to the available entropy bits.
     * @return a long value containing the extracted bits in its least significant positions.
     * @throws IllegalArgumentException if the specified number of bits is less than or equal to zero,
     *                                  exceeds the maximum allowed bits (64) or is greater than the
     *                                  available entropy.
     */
    public long getEntropy(final int bits) {
        if (bits <= 0 || bits > this.bits || bits > 64)
            throw new IllegalArgumentException("Invalid number of bits: " + bits + " (should be positive, no more than 64, and less than or equal to " + this.bits + " bits)");
        long result = 0;
        int requiredBits = bits;
        while (requiredBits >= 8) {
            result = (result << 8) | getAByte();
            requiredBits -= 8;
        }
        if (requiredBits > 0)
            result = (result << requiredBits) | getOddBits(requiredBits);
        long bitmask = createBitmask(bits);
        return result & bitmask;
    }

    /**
     * Generates a random long value between 0 and `n` (exclusive).
     * The number of bits used to generate the random value is determined
     * by calculating the base-2 logarithm of `n`.
     *
     * @param n defines the range of possible results: `0 ... n-1`.
     * @return a random long value generated using the entropy source, in range `0 ... n-1`.
     */
    public long getRandom(final long n) {
        int bits = log2(n);
        return (getEntropy(bits) & createBitmask(bits)) % n;
    }

    /**
     * Package-private method that retrieves the raw entropy data represented as a byte array.
     * NOTE this is used solely for testing.
     * CONSIDER making this private.
     *
     * @return a byte array containing the raw entropy data. The byte array represents the underlying entropy source.
     */
    byte[] getEntropy() {
        return entropy;
    }

    /**
     * Constructs an Entropy object based on the given byte array.
     * This byte array serves as the entropy source, with its length determining
     * the number of bits available in this instance. The constructor enforces
     * that at least one bit of entropy must be contained in the array.
     *
     * @param entropy the byte array representing the entropy source. It must
     *                contain at least one byte, and its length is used to determine
     *                the bit count for this entropy instance.
     * @throws IllegalArgumentException if the byte array represents less than 1 bit of entropy.
     */
    public Entropy(final byte[] entropy) {
        this.entropy = entropy;
        this.bits = entropy.length * 8;
        this.lastElementBits = 8;
        this.lastIndex = entropy.length - 1;
    }

    /**
     * Constructs an Entropy object with a specified number of bits.
     * The constructor internally calculates the required entropy bytes using the specified bits
     * and initializes the Entropy instance.
     *
     * @param bits the number of entropy bits to be generated. It must be a non-negative integer.
     *             The provided bits determine the length of the entropy source.
     * @throws IllegalArgumentException if the specified number of bits is negative.
     */
    public Entropy(final int bits) {
        this(getBytes(bits));
    }

    @Override
    public String toString() {
        String[] hex = new String[entropy.length];
        for (int i = 0; i < entropy.length; i++) {
            hex[i] = String.format("%02X", entropy[i]);
        }
        return "Entropy{" +
                "bits=" + bits +
                ", entropy=" + Arrays.toString(hex) +
                '}';
    }

    /**
     * Extracts the specified number of bits from an internal entropy source, starting from the odd-bit positions.
     * This method processes a specified number of bits and ensures consistent state updates for subsequent access.
     *
     * @param bits the number of odd bits to extract. This must be a non-negative integer.
     * @return a long value containing the extracted bits in the least significant bit positions.
     * Throws an assertion error if `bits` is negative.
     */
    private long getOddBits(final int bits) {
        assert bits >= 0;
        if (bits <= lastElementBits) {
            byte lastByte = entropy[lastIndex];
            long result = lastByte & createBitmask(bits);
            entropy[lastIndex] = (byte) (lastByte >> bits);
            lastElementBits -= bits;
            if (lastElementBits == 0) {
                lastIndex--;
                lastElementBits = 8;
            }
            this.bits -= bits;
            return result;
        } else {
            long result = 0;
            int bitCount = lastElementBits;
            result |= getOddBits(bitCount);
            int requiredBits = bits - bitCount;
            result = result << (requiredBits);
            result |= getOddBits(requiredBits);
            return result;
        }
    }

    /**
     * Creates a bitmask with a specified length of consecutive 1-bits, starting from the least significant bit.
     *
     * @param length the number of consecutive 1-bits in the bitmask. Must be a non-negative integer.
     * @return a long value representing the constructed bitmask.
     *         If the length is zero, returns 0.
     */
    private static long createBitmask(final int length) {
        if (length == 64)
            return 0xFFFFFFFFFFFFFFFFL;
        return (0x1L << length) - 1;
    }

    /**
     * Extracts a single byte of entropy from the internal entropy array.
     * The method ensures that there are enough bits before extraction.
     * It shifts the remaining bytes in the entropy array after the extracted byte, updates the state of the entropy,
     * and decrements the available entropy bits by 8.
     *
     * @return the extracted byte value from the entropy array.
     * @throws AssertionError if the precondition of sufficient bits or valid 0 is violated.
     */
    private byte getAByte() {
        assert 0 < entropy.length;
        assert bits >= 8;
        byte result = entropy[0];
        for (int i = 0; i < lastIndex; i++) {
            entropy[i] = entropy[i + 1];
        }
        bits -= 8;
        lastIndex--;
        // NOTE -1 is a legitimate terminal state, not a bug: it means every byte
        // has now been consumed. `bits` reaches 0 at the same moment, and
        // getEntropy rejects any request larger than `bits`, so lastIndex can
        // never be read again. Asserting >= 0 here forbade drawing exactly the
        // entropy available -- e.g. 32 bits from a 32-bit Entropy.
        assert lastIndex >= -1;
        return result;
    }
    /**
     * Currently package-private.
     */
    private int bits;
    private int lastIndex;
    private int lastElementBits;
    private final byte[] entropy;

    /**
     * Generates a byte array representation of the specified number of bits.
     * The method determines the entropy source based on the number of bits:
     * if bits == 0, then we simply use 0L as the seed;
     * if bits are less than 32, it derives the bytes from the system clock;
     * for a larger number of bits, it utilizes a cryptographically secure random number generator.
     *
     * @param bits the number of bits to represent as a byte array. Must be non-negative.
     * @return a byte array containing the entropy-derived data.
     * The array size is calculated as the ceiling of bits divided by 8.
     * @throws IllegalArgumentException if the specified number of bits is negative.
     */
    private static byte[] getBytes(int bits) {
        if (bits < 0)
            throw new IllegalArgumentException("Invalid number of bits: " + bits);
        byte[] result = new byte[ceiling(bits, 8)];
        if (seed != 0L)
            packIntoBytes(result, seed);
        else if (bits < 32)
            useClockTime(result);
        else
            secureRandom.nextBytes(result);
        return result;
    }

    /**
     * Computes the ceiling of the division of two integers.
     * The result is the smallest integer greater than or equal to the division of n by d.
     *
     * @param n the dividend, which can be any integer.
     * @param d the divisor, which must be a positive integer.
     * @return the ceiling of the division (n / d) as an integer.
     */
    private static int ceiling(int n, final int d) {
        return (n + d - 1) / d;
    }

    /**
     * Populates the given byte array with entropy data derived from the current
     * system clock time in milliseconds. Each byte of the array is filled by
     * extracting 8-bit segments from the lower 32 bits of the current timestamp.
     * We ignore the upper 32 bits since they aren't remotely random.
     *
     * @param result the byte array to be populated with the entropy data,
     *               where each element is assigned a part of the timestamp value.
     */
    static private void useClockTime(byte[] result) {
        packIntoBytes(result, System.currentTimeMillis());
    }

    private static void packIntoBytes(byte[] result, long seed) {
        for (int i = 0; i < result.length; i++)
            result[i] = (byte) (seed >> (i * 8));
    }

    /**
     * Computes the base-2 logarithm of a given positive long integer.
     * This method iteratively calculates how many times the input number
     * can be divided by 2 until it becomes 1, effectively determining the
     * exponent in the power-of-two representation of the number.
     *
     * @param x the input positive long number for which the base-2 logarithm
     *          is to be computed. Must be greater than 0.
     * @return the base-2 logarithm of the input number as an integer.
     *         Returns 0 if the input number is 1.
     * @throws IllegalArgumentException if the input number is less than or equal to 0.
     */
    static int log2(final long x) {
        if (x <= 0)
            throw new IllegalArgumentException("x must be positive");
        if (x == 1)
            return 0;
        return 64 - Long.numberOfLeadingZeros(x - 1);
    }

    static long seed = 0;
    private static final SecureRandom secureRandom;

    static {
        try {
            // XXX specify `NativePRNG` which uses `/dev/urandom` for its seed.
            secureRandom = SecureRandom.getInstance("NativePRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
