package com.phasmidsoftware.dsaipg.misc.equable;

import com.phasmidsoftware.dsaipg.misc.equable.ComparableEquable.ComparableEquableException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class ComparableEquableTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * Test the compareTo method with two ComparableEquable objects having equal elements.
     */
    @Test
    public void testCompareToEqualElements() {
        ComparableEquable equable1 = new ComparableEquable(Arrays.asList(1, 2, 3));
        ComparableEquable equable2 = new ComparableEquable(Arrays.asList(1, 2, 3));

        int result = equable1.compareTo(equable2);

        assertEquals(0, result);
    }

    /**
     * Test the compareTo method where the first object is lexicographically smaller.
     */
    @Test
    public void testCompareToSmallerElements() {
        ComparableEquable equable1 = new ComparableEquable(Arrays.asList(1, 2, 2));
        ComparableEquable equable2 = new ComparableEquable(Arrays.asList(1, 2, 3));

        int result = equable1.compareTo(equable2);

        assertEquals(-1, result);
    }

    /**
     * Test the compareTo method where the first object is lexicographically larger.
     */
    @Test
    public void testCompareToLargerElements() {
        ComparableEquable equable1 = new ComparableEquable(Arrays.asList(1, 2, 4));
        ComparableEquable equable2 = new ComparableEquable(Arrays.asList(1, 2, 3));

        int result = equable1.compareTo(equable2);

        assertEquals(1, result);
    }

    /**
     * Test the compareTo method with objects of different lengths, expecting an exception.
     */
    @Test
    public void testCompareToDifferentLengths() {
        ComparableEquable equable1 = new ComparableEquable(Arrays.asList(1, 2, 3));
        ComparableEquable equable2 = new ComparableEquable(Arrays.asList(1, 2));

        assertThrows(ComparableEquableException.class, () -> equable1.compareTo(equable2));
    }

    /**
     * Test the compareTo method when elements are not comparable, expecting an exception.
     */
    @Test
    public void testCompareToNonComparableElements() {
        ComparableEquable equable1 = new ComparableEquable(Collections.singletonList(new Object()));
        ComparableEquable equable2 = new ComparableEquable(Collections.singletonList(new Object()));

        assertThrows(ComparableEquableException.class, () -> equable1.compareTo(equable2));
    }

    /**
     * The "same length" rule must hold whichever way round the two are given, or
     * the relation disagrees with itself: walking only this one's elements would
     * let the shorter compared with the longer run out and report 0, equal.
     */
    @Test
    public void testCompareToDifferentLengthsEitherWayRound() {
        ComparableEquable shorter = new ComparableEquable(Arrays.asList(1, 2));
        ComparableEquable longer = new ComparableEquable(Arrays.asList(1, 2, 3));
        assertThrows(ComparableEquableException.class, () -> longer.compareTo(shorter));
        assertThrows("and this is the direction that would otherwise answer 0",
                ComparableEquableException.class, () -> shorter.compareTo(longer));
    }

    /**
     * The same fault in Equable.equals, where it broke the Object contract:
     * equals must be symmetric, and every hash-based collection relies on it.
     */
    @Test
    public void testEqualsIsSymmetric() {
        Equable shorter = new Equable(Arrays.asList(1, 2));
        Equable longer = new Equable(Arrays.asList(1, 2, 3));
        assertNotEquals("a prefix is not the whole", shorter, longer);
        assertNotEquals(longer, shorter);
        assertEquals(new Equable(Arrays.asList(1, 2)), shorter);
    }
}
