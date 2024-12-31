package com.phasmidsoftware.dsaipg.misc.equable;

import com.phasmidsoftware.dsaipg.misc.equable.ComparableEquable.ComparableEquableException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ComparableEquableTest {

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
}