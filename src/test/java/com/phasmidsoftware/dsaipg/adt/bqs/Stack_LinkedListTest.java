package com.phasmidsoftware.dsaipg.adt.bqs;

import org.junit.Test;

import static org.junit.Assert.*;

public class Stack_LinkedListTest {

    /**
     * Test the push method by pushing a single item to an empty stack,
     * and verifying that the item is present at the top of the stack.
     */
    @Test
    public void testPushSingleItem() {
        Stack_LinkedList<Integer> stack = new Stack_LinkedList<>();
        stack.push(1);
        assertFalse(stack.isEmpty());
        assertEquals(Integer.valueOf(1), stack.peek());
    }

    /**
     * Test the push method by pushing multiple items and checking
     * if the stack order is maintained (LIFO).
     */
    @Test
    public void testPushMultipleItems() {
        Stack_LinkedList<String> stack = new Stack_LinkedList<>();
        stack.push("First");
        stack.push("Second");
        stack.push("Third");
        assertFalse(stack.isEmpty());
        assertEquals("Third", stack.peek());
    }

    /**
     * Test pushing a null item to the stack and check if it is handled correctly.
     */
    @Test
    public void testPushNullItem() {
        Stack_LinkedList<Object> stack = new Stack_LinkedList<>();
        stack.push(null);
        assertFalse(stack.isEmpty());
        assertNull(stack.peek());
    }

    /**
     * Test the push behavior alongside pop to ensure the stack behaves
     * correctly when items are added and then removed.
     */
    @Test
    public void testPushAndPop() throws BQSException {
        Stack_LinkedList<Double> stack = new Stack_LinkedList<>();
        stack.push(1.5);
        stack.push(2.5);
        assertFalse(stack.isEmpty());
        assertEquals(Double.valueOf(2.5), stack.pop());
        assertEquals(Double.valueOf(1.5), stack.pop());
        assertTrue(stack.isEmpty());
    }

    /**
     * Test push with boundary conditions by adding a large number of elements.
     * This test ensures that the stack can handle a reasonably large number of pushes.
     */
    @Test
    public void testPushLargeNumberOfElements() {
        Stack_LinkedList<Integer> stack = new Stack_LinkedList<>();
        int size = 1000;
        for (int i = 1; i <= size; i++) {
            stack.push(i);
        }
        assertFalse(stack.isEmpty());
        assertEquals(Integer.valueOf(size), stack.peek());
    }
}