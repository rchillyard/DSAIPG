package com.phasmidsoftware.dsaipg.sort.counting;

/**
 * TODO eliminate this class (and its tests)
 *
 * Radix Sort
 * Radix sort is an integer sorting algorithm that sorts data with integer keys
 * by grouping the keys by individual digits that share the same significant
 * position and value (place value). Radix sort uses counting sort as a
 * subroutine to sort an array of numbers.
 * <p>TESTME
 * <b>Disclaimer:</b> This radix sort can only sort positive integers
 *
 * @version 1.0
 * @since 13th May 2020
 */
public class RadixSort {

    /**
     * findMaxInt method is used to find maximum number in the array
     * within the provided range i.e from and to
     *
     * @param numArr It contains an array of numbers from which maximum value needs to be obtained
     * @param from   This is the starting index from which scanning for maximum number will begin
     * @param to     This is the ending index until which scanning for maximum number will be continued
     * @return int This method returns maximum number between from index and to index
     */
    /**
     * @param numArr the values.
     * @param from   the index of the first value to consider.
     * @param to     one past the index of the last, i.e. EXCLUSIVE.
     * @return the largest value in the range.
     */
    public int findMaxInt(int[] numArr, int from, int to) {
        int maxVal = numArr[from];
        for (int i = from; i < to; i++)
            maxVal = Math.max(maxVal, numArr[i]);
        return maxVal;
    }

    /**
     * countSort method is implementation of basic counting sort algorithm.
     * We provide exponent i.e unit's digit, ten's digit or hundred's digit etc on which counting sort needs to be performed
     *
     * @param numArr It contains an array of numbers on which counting sort needs to be performed
     * @param exp    This is the exponent input on which counting sort would be performed e.g 1, 10, 100, 1000 etc.
     * @param from   This is the starting index from which sorting operation will begin
     * @param to     This is the ending index until which sorting operation will be continued
     */
    /**
     * Sort the range by one digit, stably. Stability is the mechanism: a later
     * pass may only reorder values that differ at that digit, so the order the
     * earlier passes established survives.
     *
     * @param numArr the values, rearranged in place.
     * @param exp    the power of ten selecting the digit: 1, 10, 100, ...
     * @param from   the index of the first value to sort.
     * @param to     one past the index of the last, i.e. EXCLUSIVE.
     */
    public void countSort(int[] numArr, int exp, int from, int to) {
        int arrLength = numArr.length;
        int[] result = new int[arrLength]; //This stores output result
        int[] count = new int[10]; // This maintains digit wise occurrence count

        //This method records occurrence of digits in count[]
        for (int i = from; i < to; i++)
            count[(numArr[i] / exp) % 10]++;

        // Modifying value of count[i] so that it now contains actual position of the digit
        for (int i = 1; i < 10; i++)
            count[i] += count[i - 1];

        // Building result array to contain radix sorted output array on selected exponent
        for (int i = to - 1; i >= from; i--) {
            result[count[(numArr[i] / exp) % 10] - 1 + from] = numArr[i];
            count[(numArr[i] / exp) % 10]--;  // Reducing count[] to adjust the next location of particular digit
        }

        //Copying result array in original array
        if (to - from >= 0) System.arraycopy(result, from, numArr, from, to - from);
    }

    /**
     * sort method is implementation of radix sort algorithm.
     *
     * @param numArr It contains an array of numbers on which radix sort needs to be performed
     * @param from   This is the starting index from which sorting operation will begin
     * @param to     This is the ending index until which sorting operation will be continued
     */
    /**
     * Sort the range, one decimal digit at a time, least significant first.
     * <p>
     * NOTE {@code to} is EXCLUSIVE, as everywhere else in this tree. It used to be
     * inclusive here, alone among the sorts, so a caller following the usual
     * convention silently left the last element of the range unsorted.
     * <p>
     * NOTE only non-negative values work: a negative value yields a negative digit
     * and so a negative bucket index.
     *
     * @param numArr the values, rearranged in place.
     * @param from   the index of the first value to sort.
     * @param to     one past the index of the last, i.e. EXCLUSIVE.
     * @throws Exception if from is greater than to.
     */
    public void sort(int[] numArr, int from, int to) throws Exception {

        // Sort Validations on input

        if (numArr == null || numArr.length == 1 || from == to) return;

        if (from > to) throw new Exception("From value should be less than to");

        if (from < 0 || (from > numArr.length - 1))
            throw new ArrayIndexOutOfBoundsException("From should be between 0 and " + (numArr.length - 1));

        if (to > numArr.length)
            throw new ArrayIndexOutOfBoundsException("To should be between 0 and " + numArr.length);

        // Finding max number
        int maxVal = findMaxInt(numArr, from, to);
        //Performing counting sort on every exponent
        int exp = 1;
        while (maxVal / exp > 0) {
            countSort(numArr, exp, from, to);
            exp *= 10;
        }
    }

}
