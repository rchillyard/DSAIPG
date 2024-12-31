/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.counting;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.HelperFactory;
import com.phasmidsoftware.dsaipg.sort.SortException;
import com.phasmidsoftware.dsaipg.sort.classic.ClassificationSorter;
import com.phasmidsoftware.dsaipg.util.CodePointMapper;
import com.phasmidsoftware.dsaipg.util.Config;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * LSDStringSort implements the Least Significant Digit (LSD) string sort algorithm.
 * This sorting algorithm processes strings by sorting them character-by-character
 * starting from the least significant character (rightmost) to the most significant character (leftmost).
 * It operates in a stable manner and is most efficient for strings of fixed or bounded lengths.
 * This implementation assumes ASCII characters in the strings being sorted.
 * TODO The mechanism for mixed-case sorting is not ideal.
 */
public class LSDStringSort extends ClassificationSorter<String, Integer> {

    public final static String DESCRIPTION = "LSD String Sort";

    /**
     * This Comparator is required to be consistent with the logic of LSDStringSort (and is used by the helper to check sorted, etc.)
     */
    public final static Comparator<String> comparatorASCII = CodePointMapper.ASCIIComparator;

    /**
     * sort method is implementation of LSD String sort algorithm.
     *
     * @param xs   It contains an array of String on which LSD sort needs to be performed
     * @param from This is the starting index from which sorting operation will begin
     * @param to   This is the first index to be ignored.
     */
    public void sort(String[] xs, int from, int to) {
        // XXX first, we try to ensure that all elements of xs have only ASCII characters.
        // NOTE for now, at least, we do not increment hits for this operation.
        Stream<String> stream = Arrays.stream(xs, from, to).map(CodePointMapper.ASCII::map);
        String[] ys = stream.toArray(String[]::new);
        int n = ys.length;
        if (n != xs.length) System.err.println("LSD String Sort: lost strings");
        // CONSIDER using a CodeMapper here.
        int i = from;
        for (String y : ys) xs[i++] = y;
        for (; i < to; i++)
            xs[i] = "";

        // XXX now, we find the longest string
        // NOTE for now, at least, we do not increment hits for this operation.
        int maxLength = w > 0 ? w : findMaxLength(xs);

        // XXX finally, run charSort.
        for (int d = maxLength; d > 0; d--)
            charSort(xs, d - 1, 0, n);
    }

    /**
     * Perform pre-processing step for this Sort.
     * Forces all strings to be in lower case.
     * <p>
     * CONSIDER moving this to a sub-class.
     *
     * @param xs the elements to be pre-processed.
     */
    @Override
    public String[] preProcess(String[] xs) {
        String[] strings = super.preProcess(xs);
        for (int i = 0; i < strings.length; i++) strings[i] = strings[i].toLowerCase();
        return strings;
    }

    /**
     * Primary constructor.
     *
     * @param helper a String helper.
     * @param w      the max length of each string (if 0, then we will calculate it).
     */
    public LSDStringSort(Helper<String> helper, int w) {
        super(helper, LSDStringSort::charAsciiVal);
        this.w = w;
    }

    /**
     * Secondary constructor.
     *
     * @param N          the expected number of elements.
     * @param w          the max length of each string (if 0, then we will calculate it).
     * @param comparator the comparator for Strings.
     * @param nRuns      the expected number of runs to be made.
     * @param config     the configuration.
     */
    public LSDStringSort(int N, int w, Comparator<String> comparator, int nRuns, Config config) {
        this(HelperFactory.createGeneric(DESCRIPTION + (w > 0 ? " " + w : ""), comparator, N, nRuns, config), w);
        if (w > 0) getLogger().info("LSD string sort with fixed length: " + w);
        closeHelper = true;
    }

    /**
     * Performs a single iteration of the LSD string sort algorithm on an array of strings.
     * The elements in the specified range of the array are sorted based on the character
     * at the given position. Utilizes the helper methods to classify, count, accumulate,
     * and distribute elements, and copies the results back to the original array.
     *
     * @param xs           An array of strings to be sorted.
     * @param charPosition The position of the character in the strings used for sorting.
     *                     If a string is shorter than this position, it is treated as having
     *                     a character value of 0 (null character).
     * @param from         The starting index (inclusive) of the range in the array to sort.
     * @param to           The ending index (exclusive) of the range in the array to sort.
     */
    private void charSort(String[] xs, int charPosition, int from, int to) {
        int ASCII_RANGE = 128;
        int[] count = new int[ASCII_RANGE + 1];

        classifyAndCountElements(xs, charPosition, from, to, count);

        accumulateCounts(count, ASCII_RANGE);

        String[] auxiliaryArray = distributeCounts(xs, charPosition, from, to, count);

        // copy back
        helper.copyBlock(auxiliaryArray, 0, xs, from, to - from);
    }

    /**
     * Distributes elements of the input array into the result array based on counts
     * accumulated for classifying elements.
     * This method works within a specified range of the input array and uses a classification function to determine
     * the appropriate position for each element in the result array.
     *
     * @param xs           An array of strings from which elements are distributed into the result array.
     * @param charPosition The position of the character in the string used for classification.
     *                     This determines the classification criteria for distribution.
     * @param from         The start index of the range (inclusive) in the input array to process.
     * @param to           The end index of the range (exclusive) in the input array to process.
     * @param count        An array to store the counts after classification, which is updated during the operation.
     *                     Used to determine the target indices for distribution.
     * @return String[] A new array containing the strings distributed into their appropriate positions.
     */
    private String[] distributeCounts(String[] xs, int charPosition, int from, int to, int[] count) {
        // XXX It's rather surprising that this function works (since it involves updating count) but it does!
        String[] result = new String[xs.length];
        helper.distributeBlock(xs, from, to, result, x -> count[classify(x, charPosition)]++);
        return result;
    }

    /**
     * Accumulates the counts in the provided count array for use in the LSD string sort algorithm.
     * Takes the current count values and computes prefix sums in the count array, which helps
     * in determining the positions of elements during sorting.
     *
     * @param count       An integer array used for counting occurrences of ASCII values.
     *                    This array must have at least (ASCII_RANGE + 1) elements.
     * @param ASCII_RANGE The maximum ASCII value (inclusive) for the characters being processed.
     *                    Determines the range of the input count array that will be updated.
     */
    private void accumulateCounts(int[] count, int ASCII_RANGE) {
        int countR = count[0];
        for (int r = 1; r < ASCII_RANGE + 1; r++) {
            helper.incrementHits(1); // for the count.
            count[r] += countR;
            countR = count[r];
        }
    }

    /**
     * This method classifies and counts elements in a specified range of a string array based on a character position.
     * It uses a helper method to retrieve elements and classify them according to the specified character,
     * then updates the count array accordingly.
     *
     * @param xs           An array of strings to be classified and counted.
     * @param charPosition The position of the character in the string to use for classification.
     * @param from         The start index of the range within the array to process.
     * @param to           The end index (exclusive) of the range within the array to process.
     * @param count        An array to store the counts based on the classification results.
     */
    private void classifyAndCountElements(String[] xs, int charPosition, int from, int to, int[] count) {
        for (int i = from; i < to; i++) {
            String x = helper.get(xs, i);
            int c = classify(x, charPosition);
            helper.incrementHits(1); // for the count.
            checkAndCount(count, c);
        }
    }

    /**
     * findMaxLength method returns the maximum length of all available strings in an array (not just from->to).
     *
     * @param xs It contains an array of String from which maximum length needs to be found
     * @return int Returns maximum length value
     */
    private int findMaxLength(String[] xs) {
        int maxLength = Integer.MIN_VALUE;
        helper.incrementHits(xs.length);
        for (String str : xs)
            maxLength = Math.max(maxLength, str.length());
        return maxLength;
    }

    /**
     * Updates the count array if the input index is within range; otherwise, throws a SortException.
     * This method is used to ensure that a given index falls within the valid range for the count array and
     * increments the value at the corresponding position. If the index is out of range, it raises an exception.
     *
     * @param count An integer array used for counting occurrences.
     *              This array must be large enough to accommodate the increment operation.
     * @param c     The index to be checked and incremented in the count array.
     *              Must lie within the valid range (0 to count.length - 1).
     * @throws SortException If the index c is out of the valid range (0 to count.length - 1).
     */
    private static void checkAndCount(int[] count, int c) {
        if (c >= 0 && c <= count.length) count[c + 1]++;
        else throw new SortException(DESCRIPTION + ": count index " + c +
                " is out of range: 0 thru " + count.length);
    }

    /**
     * charAsciiVal method returns ASCII value of particular character in a String.
     *
     * @param str          String input for which ASCII Value need to be found
     * @param charPosition Character position (zero-based) of which ASCII value needs to be found. If character
     *                     doesn't exist then ASCII value of null i.e. 0 is returned
     * @return int Returns ASCII value
     */
    private static int charAsciiVal(String str, int charPosition) {
        if (charPosition < 0 || charPosition >= str.length()) return 0;
        char x = str.charAt(charPosition);
        return x & 0x7F; // CONSIDER using MSDStringSort mapper.
    }

    private final int w;
}