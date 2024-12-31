package com.phasmidsoftware.dsaipg.adt.bqs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ElementTest {

    /**
     * Test case to validate equality when comparing the same object.
     */
    @Test
    public void testEquals_SameObject() {
        Element<Integer> element = new Element<>(1);
        assertEquals(element, element);
    }

    /**
     * Test case to validate equality when comparing null.
     */
    @Test
    public void testEquals_NullObject() {
        Element<Integer> element = new Element<>(1);
        assertNotEquals(null, element);
    }

    /**
     * Test case to validate equality when comparing an object of a different class.
     */
    @Test
    public void testEquals_DifferentClass() {
        Element<Integer> element = new Element<>(1);
        String other = "Test";
        assertNotEquals(element, other);
    }

    /**
     * Test case to validate equality of two elements with the same value and no next element.
     */
    @Test
    public void testEquals_SameValue_NoNext() {
        Element<Integer> element1 = new Element<>(1);
        Element<Integer> element2 = new Element<>(1);
        assertEquals(element1, element2);
    }

    /**
     * Test case to validate inequality of two elements with different values and no next element.
     */
    @Test
    public void testEquals_DifferentValue_NoNext() {
        Element<Integer> element1 = new Element<>(1);
        Element<Integer> element2 = new Element<>(2);
        assertNotEquals(element1, element2);
    }

    /**
     * Test case to validate equality of two elements with the same value and identical next elements.
     */
    @Test
    public void testEquals_SameValue_WithNext() {
        Element<Integer> next1 = new Element<>(2);
        Element<Integer> element1 = new Element<>(1, next1);
        Element<Integer> next2 = new Element<>(2);
        Element<Integer> element2 = new Element<>(1, next2);
        assertEquals(element1, element2);
    }

    /**
     * Test case to validate inequality of two elements with the same value but different next elements.
     */
    @Test
    public void testEquals_SameValue_DifferentNext() {
        Element<Integer> next1 = new Element<>(2);
        Element<Integer> element1 = new Element<>(1, next1);
        Element<Integer> next2 = new Element<>(3);
        Element<Integer> element2 = new Element<>(1, next2);
        assertNotEquals(element1, element2);
    }

    /**
     * Test case to validate equality of two elements where one element has no next, and the other has a null next.
     */
    @Test
    public void testEquals_NullNext() {
        Element<Integer> element1 = new Element<>(1, null);
        Element<Integer> element2 = new Element<>(1);
        assertEquals(element1, element2);
    }
}