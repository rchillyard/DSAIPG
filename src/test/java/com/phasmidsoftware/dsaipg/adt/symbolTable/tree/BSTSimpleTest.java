package com.phasmidsoftware.dsaipg.adt.symbolTable.tree;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BSTSimpleTest {

    @Test
    public void testPutAllWithMultipleEntries() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        Map<Integer, String> testMap = new HashMap<>();
        testMap.put(10, "Ten");
        testMap.put(20, "Twenty");
        testMap.put(5, "Five");

        // Act
        bst.putAll(testMap);

        // Assert
        assertEquals("Ten", bst.get(10));
        assertEquals("Twenty", bst.get(20));
        assertEquals("Five", bst.get(5));
        assertEquals(3, bst.size());
    }

    @Test
    public void testPutAllWithEmptyMap() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        Map<Integer, String> testMap = new HashMap<>();

        // Act
        bst.putAll(testMap);

        // Assert
        assertEquals(0, bst.size());
    }

    @Test
    public void testPutAllWithDuplicateKeys() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(10, "OldValue");
        Map<Integer, String> testMap = new HashMap<>();
        testMap.put(10, "NewValue");
        testMap.put(20, "Twenty");

        // Act
        bst.putAll(testMap);

        // Assert
        assertEquals("NewValue", bst.get(10));
        assertEquals("Twenty", bst.get(20));
        assertEquals(2, bst.size());
    }

    @Test
    public void testPutAllMaintainsExistingEntries() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(1, "One");
        Map<Integer, String> testMap = new HashMap<>();
        testMap.put(2, "Two");

        // Act
        bst.putAll(testMap);

        // Assert
        assertEquals("One", bst.get(1));
        assertEquals("Two", bst.get(2));
        assertEquals(2, bst.size());
    }

    @Test
    public void testPutAllWithNullValue() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        Map<Integer, String> testMap = new HashMap<>();
        testMap.put(5, null);

        // Act
        bst.putAll(testMap);

        // Assert
        assertNull(bst.get(5));
        assertEquals(1, bst.size());
    }
}