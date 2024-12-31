/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import com.phasmidsoftware.dsaipg.sort.Helper;

import java.util.Comparator;

/**
 * Comparator of Strings designed to compare the suffixes of two Strings.
 */
public class SuffixComparator implements Comparator<String> {

    /**
     * Compares two strings based on their suffixes, excluding the specified prefix length.
     * The comparison is delegated to a provided string comparator after the prefix is removed.
     *
     * @param o1 the first string to compare.
     * @param o2 the second string to compare.
     * @return a negative integer, zero, or a positive integer as the suffix of the first string
     * is less than, equal to, or greater than the suffix of the second string.
     */
    public int compare(String o1, String o2) {
        return stringComparator.compare(Helper.discriminateString(o1, prefixLength), Helper.discriminateString(o2, prefixLength));
    }

    /**
     * Primary constructor.
     *
     * @param stringComparator a Comparator of String, which could be a Collator.
     * @param prefixLength     the number of characters to be ignored in a String comparand.
     */
    public SuffixComparator(Comparator<String> stringComparator, int prefixLength) {
        this.stringComparator = stringComparator;
        this.prefixLength = prefixLength;
    }

    /**
     * Constructor for a Comparator that works exactly like the Comparator passed in as the parameter.
     *
     * @param comparator a String-Comparator.
     */
    public SuffixComparator(Comparator<String> comparator) {
        this(comparator, 0);
    }

    private final Comparator<String> stringComparator;
    private final int prefixLength;
}