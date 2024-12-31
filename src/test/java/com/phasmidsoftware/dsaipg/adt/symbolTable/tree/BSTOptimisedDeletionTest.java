package com.phasmidsoftware.dsaipg.adt.symbolTable.tree;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BSTOptimisedDeletionTest {

    /**
     * Test the `put` method for inserting a single key-value pair.
     * Verifies that the key-value pair is added correctly and can be retrieved.
     */
    @Test
    public void testPutSingleElement() {
        BSTOptimisedDeletion<Integer, String> bst = new BSTOptimisedDeletion<>();
        String value = bst.put(10, "A");
        assertEquals("A", value);
        assertTrue(bst.contains(10));
        assertEquals("A", bst.get(10));
        assertEquals(1, bst.size());
    }

    /**
     * Test the `put` method for updating an existing key's value.
     * Ensures that the value is updated properly without impacting the structure.
     */
    @Test
    public void testPutUpdateExistingKey() {
        BSTOptimisedDeletion<Integer, String> bst = new BSTOptimisedDeletion<>();
        bst.put(10, "A");
        String updatedValue = bst.put(10, "B");
        assertEquals("B", updatedValue);
        assertTrue(bst.contains(10));
        assertEquals("B", bst.get(10));
        assertEquals(1, bst.size());
    }

    /**
     * Test the `put` method for adding multiple unique key-value pairs.
     * Verifies that the structure updates correctly and all elements can be retrieved.
     */
    @Test
    public void testPutMultipleElements() {
        BSTOptimisedDeletion<Integer, String> bst = new BSTOptimisedDeletion<>();
        bst.put(10, "A");
        bst.put(5, "B");
        bst.put(15, "C");

        assertTrue(bst.contains(10));
        assertTrue(bst.contains(5));
        assertTrue(bst.contains(15));
        assertEquals("A", bst.get(10));
        assertEquals("B", bst.get(5));
        assertEquals("C", bst.get(15));
        assertEquals(3, bst.size());
    }

    /**
     * Test the `put` method for handling keys in sorted order.
     * Ensures the correct insertion and structure when adding keys sequentially in ascending order.
     */
    @Test
    public void testPutSortedOrder() {
        BSTOptimisedDeletion<Integer, String> bst = new BSTOptimisedDeletion<>();
        for (int i = 1; i <= 5; i++) {
            bst.put(i, "Value" + i);
        }

        assertEquals(5, bst.size());
        for (int i = 1; i <= 5; i++) {
            assertTrue(bst.contains(i));
            assertEquals("Value" + i, bst.get(i));
        }
    }

    /**
     * Test the `put` method for inserting duplicate keys.
     * Verifies that inserting a duplicate key simply updates the value.
     */
    @Test
    public void testPutDuplicateKeys() {
        BSTOptimisedDeletion<Integer, String> bst = new BSTOptimisedDeletion<>();
        bst.put(20, "FirstValue");
        bst.put(20, "UpdatedValue");

        assertEquals(1, bst.size());
        assertEquals("UpdatedValue", bst.get(20));
    }

    /**
     * Test the `put` method for proper behavior when inserting into an empty tree.
     */
    @Test
    public void testPutIntoEmptyTree() {
        BSTOptimisedDeletion<Integer, String> bst = new BSTOptimisedDeletion<>();
        String value = bst.put(50, "RootValue");

        assertEquals("RootValue", value);
        assertTrue(bst.contains(50));
        assertEquals("RootValue", bst.get(50));
        assertEquals(1, bst.size());
    }

    /**
     * Test the `put` method for inserting keys with larger and smaller subtrees.
     * Verifies correct structure and values.
     */
    @Test
    public void testPutComplexTree() {
        BSTOptimisedDeletion<Integer, String> bst = new BSTOptimisedDeletion<>();
        bst.put(30, "Root");
        bst.put(20, "Left");
        bst.put(40, "Right");
        bst.put(10, "LeftLeft");
        bst.put(25, "LeftRight");
        bst.put(35, "RightLeft");
        bst.put(50, "RightRight");

        assertEquals(7, bst.size());
        assertEquals("Root", bst.get(30));
        assertEquals("Left", bst.get(20));
        assertEquals("Right", bst.get(40));
        assertEquals("LeftLeft", bst.get(10));
        assertEquals("LeftRight", bst.get(25));
        assertEquals("RightLeft", bst.get(35));
        assertEquals("RightRight", bst.get(50));
    }
}