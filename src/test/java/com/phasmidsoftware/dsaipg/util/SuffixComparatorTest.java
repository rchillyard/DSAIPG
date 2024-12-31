package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class SuffixComparatorTest {

    /**
     * Test 1: Verify compare method where suffixes are identical and prefix length is ignored (prefixLength = 0).
     */
    @Test
    public void testCompareWithIdenticalSuffixes() {
        Comparator<String> baseComparator = String::compareTo;
        SuffixComparator suffixComparator = new SuffixComparator(baseComparator, 7);

        String s1 = "prefix1_ABC";
        String s2 = "Prefix2_ABC";

        assertEquals(0, suffixComparator.compare(s1, s2));
    }

    /**
     * Test 2: Verify compare method where suffixes differ and prefix length is ignored (prefixLength = 0).
     */
    @Test
    public void testCompareWithDifferentSuffixes() {
        Comparator<String> baseComparator = String::compareTo;
        SuffixComparator suffixComparator = new SuffixComparator(baseComparator, 7);

        String s1 = "prefix_ABC";
        String s2 = "prefix_DEF";

        int result = suffixComparator.compare(s1, s2);

        assertEquals(-3, result);
    }

    /**
     * Test 3: Verify compare method when using a custom prefix length (e.g., prefixLength = 7).
     */
    @Test
    public void testCompareWithCustomPrefixLength() {
        Comparator<String> baseComparator = String::compareTo;
        SuffixComparator suffixComparator = new SuffixComparator(baseComparator, 6);

        String s1 = "prefix123_ABC";
        String s2 = "prefix456_DEF";

        int result = suffixComparator.compare(s1, s2);

        assertEquals(-3, result);
    }

    /**
     * Test 4: Verify compare method when comparing suffixes that are equal but prefixes differ (prefixLength > 0).
     */
    @Test
    public void testCompareWithEqualSuffixesAndDifferentPrefixes() {
        Comparator<String> baseComparator = String::compareTo;
        SuffixComparator suffixComparator = new SuffixComparator(baseComparator, 9);

        String s1 = "prefix123_same";
        String s2 = "prefix456_same";

        int result = suffixComparator.compare(s1, s2);

        assertEquals(0, result);
    }

    /**
     * Test 5: Verify compare method with an empty string and custom prefix length.
     */
    @Test
    public void testCompareWithEmptyStrings() {
        Comparator<String> baseComparator = String::compareTo;
        SuffixComparator suffixComparator = new SuffixComparator(baseComparator, 5);

        String s1 = "";
        String s2 = "";

        int result = suffixComparator.compare(s1, s2);

        assertEquals(0, result);
    }

    /**
     * Test 6: Verify compare method when one string is shorter than the prefix length.
     */
    @Test
    public void testCompareWithShortStrings() {
        Comparator<String> baseComparator = String::compareTo;
        SuffixComparator suffixComparator = new SuffixComparator(baseComparator, 4);

        String s1 = "short";
        String s2 = "longsuffix";

        int result = suffixComparator.compare(s1, s2);

        assertEquals(1, result);
    }
}