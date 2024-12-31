package com.phasmidsoftware.dsaipg.adt.bqs;

import org.junit.Test;

import static org.junit.Assert.*;

public class LinkedListElementsTest {

    /**
     * Test for adding a single element to the LinkedList_Elements.
     * Ensures the element is added as the head of the list and the list is not empty.
     */
    @Test
    public void testAddSingleElement() {
        LinkedList_Elements<String> list = new LinkedList_Elements<>();
        list.add("Test1");

        assertFalse(list.isEmpty());
        assertEquals("Test1", list.getHead());
    }

    /**
     * Test for adding multiple elements to the LinkedList_Elements.
     * Ensures the elements are added correctly and the head contains the last added element.
     */
    @Test
    public void testAddMultipleElements() {
        LinkedList_Elements<Integer> list = new LinkedList_Elements<>();
        list.add(1);
        list.add(2);
        list.add(3);

        assertFalse(list.isEmpty());
        assertEquals(Integer.valueOf(3), list.getHead());
    }

    /**
     * Test for adding a null element to the LinkedList_Elements.
     * Ensures null can safely be added to the list and becomes the head.
     */
    @Test
    public void testAddNullElement() {
        LinkedList_Elements<String> list = new LinkedList_Elements<>();
        list.add(null);

        assertFalse(list.isEmpty());
        assertNull(list.getHead());
    }

    /**
     * Test to verify the behavior of adding elements to an initially empty list.
     * Ensures the head remains consistent with the last added element.
     */
    @Test
    public void testAddToEmptyList() {
        LinkedList_Elements<String> list = new LinkedList_Elements<>();
        list.add("First");
        list.add("Second");

        assertEquals("Second", list.getHead());
        list.add("Third");
        assertEquals("Third", list.getHead());
    }

    /**
     * Test to verify that the list maintains correct order after multiple additions.
     * Checks elements are added with head pointing to the last added.
     */
    @Test
    public void testOrderPreservationAfterAdd() throws BQSException {
        LinkedList_Elements<Integer> list = new LinkedList_Elements<>();
        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(Integer.valueOf(30), list.getHead());
        list.remove();
        assertEquals(Integer.valueOf(20), list.getHead());
        list.remove();
        assertEquals(Integer.valueOf(10), list.getHead());
    }

    /**
     * Test to verify rapid consecutive additions update the head properly.
     */
    @Test
    public void testRapidAdditionsUpdateHead() {
        LinkedList_Elements<String> list = new LinkedList_Elements<>();
        list.add("Head1");
        list.add("Head2");
        list.add("Head3");

        assertEquals("Head3", list.getHead());
    }

    /**
     * Test to verify that the list maintains correct order after multiple additions.
     * Checks elements are added with head pointing to the last added.
     */
    @Test
    public void testEqualsAndHashCode() throws BQSException {
        LinkedList_Elements<Integer> list1 = new LinkedList_Elements<>();
        list1.add(10);
        list1.add(20);
        LinkedList_Elements<Integer> list2 = new LinkedList_Elements<>();
        list2.add(10);

        assertEquals(2883, list1.hashCode());
        assertEquals(1302, list2.hashCode());
        assertNotEquals(list1, list2);
        list1.remove();
        assertEquals(1302, list1.hashCode());
        list1.remove();
        assertEquals(31, list1.hashCode());
    }
}