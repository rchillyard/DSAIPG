/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import java.util.Comparator;
import java.util.function.UnaryOperator;

/**
 * Class to manage code point mapping.
 * This implementation deals with three components: the mapper, the range, and the comparator.
 * See the constructor for explanations.
 * <p>
 * What is a codepoint? It's an integer (could be 8, 16, or more bits) that maps to a (Unicode) character.
 * See <a href="https://en.wikipedia.org/wiki/Code_point">Wikipedia: Codepoint</a>.
 */
public class CodePointMapper implements UnaryOperator<Integer>, Comparator<String> {

    @Override
    public String toString() {
        return name;
    }

    /**
     * A UnaryOperator that maps an Integer Unicode code point to a specific English character representation.
     * <p>
     * If the input value is within the range of ASCII characters (less than 256)
     * and corresponds to a valid letter (either uppercase or lowercase),
     * the mapped value is calculated by applying a bitwise AND operation with 0x1F,
     * effectively providing a normalized value for English letters.
     * <p>
     * If the input does not meet the criteria, the mapper returns 0.
     */
    static final UnaryOperator<Integer> EnglishMapper = x -> {
        if (x < 256 && Character.isLetter(x)) return x & 0x1F;
        else return 0;
    };

    /**
     * A Comparator implementation for comparing two strings by applying a custom mapping function,
     * defined through the EnglishMapper, to their individual characters. This comparator determines
     * the ordering of strings based on the transformed values of their characters.
     * <p>
     * - Characters are compared one by one, starting from the beginning of the string.
     * - If the end of either string is reached, the comparison continues with a default value of 0
     *   for any missing characters.
     * - The comparison of corresponding characters is carried out by applying the EnglishMapper to each character and
     *   comparing the resulting mapped values.
     * - The first non-equal mapped value determines the result of the comparison.
     * - If all characters are equal (based on the mapping), the comparator returns 0.
     *
     * The ordering behavior is dependent on the specific implementation of the EnglishMapper function.
     */
    static final Comparator<String> EnglishComparator = (o1, o2) -> {
        for (int i = 0; i <= o1.length() && i <= o2.length(); i++) {
            int char1 = (i < o1.length()) ? o1.charAt(i) : 0;
            int char2 = (i < o2.length()) ? o2.charAt(i) : 0;
            int cf = EnglishMapper.apply(char1) - EnglishMapper.apply(char2);
            if (cf != 0) return cf;
        }
        return 0;
    };

    /**
     * CodePointMapper to yield a value in the range 0 -> 31 which is good for English characters.
     */
    public final static CodePointMapper English = new CodePointMapper("English", EnglishMapper, 32, EnglishComparator);

    /**
     * A UnaryOperator implementation that maps an integer value to its corresponding ASCII value within the 8-bit range.
     * This ensures that the resulting value is within 0-255 by applying a bitwise AND operation with 0xFF.
     */
    static final UnaryOperator<Integer> ASCIIMapperExt = x -> x & 0xFF;
    /**
     * A comparator that compares two strings lexicographically based on their mapped ASCII values.
     * Each character in the strings is transformed using the {@code ASCIIMapperExt} function before comparison.
     * The comparison proceeds character by character, considering the length of the strings and defaulting to 0
     * for unmatched characters if strings are of unequal length.
     * The comparison result is determined by the difference of transformed values at the first differing character.
     * If all characters are equivalent, the strings are considered equal.
     */
    public static final Comparator<String> ASCIIComparatorExt = (o1, o2) -> {
        int l1 = o1.length();
        int l2 = o2.length();
        for (int i = 0; i <= l1 && i <= l2; i++) {
            int char1 = (i < l1) ? o1.charAt(i) : 0;
            int char2 = (i < l2) ? o2.charAt(i) : 0;
            int cf = ASCIIMapperExt.apply(char1) - ASCIIMapperExt.apply(char2);
            if (cf != 0) return cf;
        }
        return 0;
    };

    /**
     * CodePointMapper to yield a value in the range 0 -> 255 which is good for (8-bit) ASCII characters.
     */
    public final static CodePointMapper ASCIIExt = new CodePointMapper("ASCII (Ext)", ASCIIMapperExt, 256, ASCIIComparatorExt);

    /**
     * A UnaryOperator that maps an input integer to its 7-bit ASCII representation by applying a bitwise AND operation with 0x7F.
     * This effectively ensures that the resultant value is within the valid ASCII range (0–127).
     */
    static final UnaryOperator<Integer> ASCIIMapper = x -> x & 0x7F;
    /**
     * A comparator for comparing two strings based on the ASCII mapping of their characters.
     * This comparator compares strings character by character using an external ASCII mapping function.
     * If both strings have equal character mappings up to their length, comparison falls back to comparing
     * string lengths to determine the order.
     * <p>
     * This utility is implemented as a lambda function and is immutable and stateless.
     */
    public static final Comparator<String> ASCIIComparator = (o1, o2) -> {
        int l1 = o1.length();
        int l2 = o2.length();
        for (int i = 0; i <= l1 && i <= l2; i++) {
            int char1 = (i < l1) ? o1.charAt(i) : 0;
            int char2 = (i < l2) ? o2.charAt(i) : 0;
            int cf = ASCIIMapper.apply(char1) - ASCIIMapper.apply(char2);
            if (cf != 0) return cf;
        }
        return 0;
    };

    /**
     * CodePointMapper to yield a value in the range 0 -> 255 which is good for (8-bit) ASCII characters.
     */
    public final static CodePointMapper ASCII = new CodePointMapper("ASCII", ASCIIMapper, 128, ASCIIComparator);

    /**
     * Constructor.
     *
     * @param name       the name of this mapper.
     * @param mapper     a function which takes a codePoint and returns a valid character within the defined <code>range</code>.
     * @param range      an int which specifies the number of legal values that can be output by the <code>mapper</code>.
     * @param comparator a String comparator.
     */
    public CodePointMapper(String name, UnaryOperator<Integer> mapper, int range, Comparator<String> comparator) {
        this.name = name;
        this.mapper = mapper;
        this.range = range;
        this.comparator = comparator;
    }

    /**
     * Method to take a (Unicode) code point and yield an int in the appropriate range.
     *
     * @param codePoint a Unicode codePoint.
     * @return an int which is non-negative and less than the value of <code>range</code>.
     */
    public int map(int codePoint) {
        int result = mapper.apply(codePoint);
        if (inRange(result)) return result;
        else throw new RuntimeException("CodePointMapper " + this + ": " + "result out of range: " + result);
    }

    /**
     * Converts a string by mapping each character using a defined mapping function.
     *
     * @param s the input string to be processed.
     * @return a new string where each character from the input has been replaced by the result of the mapping function.
     */
    public String map(String s) {
        StringBuilder sb = new StringBuilder();
        for (char x : s.toCharArray()) sb.append((char) map(x));
        return sb.toString();
    }

    /**
     * Method to determine if the value <code>x</code> is within legal range, i.e., according to <code>range</code>.
     *
     * @param x the value.
     * @return true if it is in range.
     */
    boolean inRange(int x) {
        return x >= 0 && x < range;
    }

    public final UnaryOperator<Integer> mapper;
    public final int range;
    public final Comparator<String> comparator;

    /**
     * Applies this function to the given argument.
     *
     * @param x the function argument
     * @return the function result
     */
    public Integer apply(Integer x) {
        return map(x);
    }

    /**
     * Compares two strings lexicographically.
     *
     * @param o1 the first string to be compared.
     * @param o2 the second string to be compared.
     * @return a negative integer, zero, or a positive integer as the first string
     * is lexicographically less than, equal to, or greater than the second string.
     */
    public int compare(String o1, String o2) {
        return 0;
    }

    private final String name;
}