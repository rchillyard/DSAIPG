package com.phasmidsoftware.dsaipg.sort.elementary;

/**
 * This is a basic implementation of insertion sort for MSD Radix Sort.
 * It does not extend Sort, nor does it employ any optimizations.
 */
public class InsertionSortMSD {

    /**
     * Sorts a specified portion of the array using insertion sort.
     * The sorting considers the characters starting from index `d` of the strings.
     *
     * @param a  the array of strings to be sorted
     * @param from the starting index of the portion to sort (inclusive)
     * @param to the ending index of the portion to sort (exclusive)
     * @param d  the character index to start comparing strings
     */
    public static void sort(String[] a, int from, int to, int d) {
        for (int i = from; i < to; i++)
            for (int j = i; j > from && less(a[j], a[j - 1], d); j--)
                swap(a, j, j - 1);
    }

    /**
     * Compares two strings starting from the specified character index.
     * Determines if the substring of the first string starting at the given index
     * is lexicographically less than the corresponding substring of the second string.
     *
     * @param v the first string
     * @param w the second string
     * @param d the starting character index for the comparison
     * @return {@code true} if the substring of the first string is lexicographically less
     * than the substring of the second string, {@code false} otherwise
     */
    private static boolean less(String v, String w, int d) {
        return v.substring(d).compareTo(w.substring(d)) < 0;
    }

    /**
     * Swaps two elements in an array of objects.
     *
     * @param a the array in which the elements will be swapped
     * @param j the index of the first element to be swapped
     * @param i the index of the second element to be swapped
     */
    private static void swap(Object[] a, int j, int i) {
        Object temp = a[j];
        a[j] = a[i];
        a[i] = temp;
    }
}
