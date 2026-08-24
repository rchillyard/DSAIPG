package com.phasmidsoftware.dsaipg.adt.symbolTable.tree;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class BSTSimpleTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

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
    public void testSizeWithTwoChildren() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();

        // Act
        bst.put(10, "Ten"); // 10 has two direction children: 5 and 15
        bst.put(5, "Five"); // 5 has no children.
        bst.put(15, "Fifteen"); // 15 has two children: 12 and 20.
        bst.put(12, "Twelve"); // 12 has no children.
        bst.put(20, "Twenty"); // 20 has no children.

        // Assert
        assertEquals(5, bst.getNode(bst.root, 10).count);
        assertEquals(1, bst.getNode(bst.root, 5).count);
        assertEquals(3, bst.getNode(bst.root, 15).count);
        assertEquals(1, bst.getNode(bst.root, 12).count);
        assertEquals(1, bst.getNode(bst.root, 20).count);
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

    @Test
    public void testPutSingleEntry() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();

        // Act
        bst.put(10, "Ten");

        // Assert
        assertEquals("Ten", bst.get(10));
        assertEquals(1, bst.size());
    }

    @Test
    public void testPutDuplicateKey() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(10, "OldValue");

        // Act
        String oldValue = bst.put(10, "NewValue");

        // Assert
        assertEquals("OldValue", oldValue);
        assertEquals("NewValue", bst.get(10));
        assertEquals(1, bst.size());
    }

    @Test
    public void testPutWithNegativeKey() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();

        // Act
        bst.put(-5, "Negative");

        // Assert
        assertEquals("Negative", bst.get(-5));
        assertEquals(1, bst.size());
    }

    @Test
    public void testPutWithNullKey() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();

        // Act
        Exception exception = null;
        try {
            bst.put(null, "NullKey");
            fail("Tree should not allow null keys");
        } catch (Exception e) {
            exception = e;
        }

        // Assert
    }

    @Test
    public void testPutWithDuplicateValues() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();

        // Act
        bst.put(1, "DuplicateValue");
        bst.put(2, "DuplicateValue");

        // Assert
        assertEquals("DuplicateValue", bst.get(1));
        assertEquals("DuplicateValue", bst.get(2));
        assertEquals(2, bst.size());
    }

    @Test
    public void testPutNullValue() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();

        // Act
        bst.put(15, null);

        // Assert
        assertNull(bst.get(15));
        assertEquals(1, bst.size());
    }

    @Test
    public void testPutMultipleSequentialEntries() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();

        // Act
        bst.put(1, "One");
        bst.put(2, "Two");
        bst.put(3, "Three");

        // Assert
        assertEquals("One", bst.get(1));
        assertEquals("Two", bst.get(2));
        assertEquals("Three", bst.get(3));
        assertEquals(3, bst.size());
    }

    @Test
    public void testPutSparseRandomEntries() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();

        // Act
        bst.put(50, "Fifty");
        bst.put(20, "Twenty");
        bst.put(70, "Seventy");

        // Assert
        assertEquals("Fifty", bst.get(50));
        assertEquals("Twenty", bst.get(20));
        assertEquals("Seventy", bst.get(70));
        assertEquals(3, bst.size());
    }

    @Test
    public void testContainsKeyInTree() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(1, "One");
        bst.put(2, "Two");
        bst.put(3, "Three");

        // Act & Assert
        assertTrue(bst.contains(1));
        assertTrue(bst.contains(2));
        assertTrue(bst.contains(3));
        assertEquals(true, bst.contains(2));
    }

    @Test
    public void testContainsKeyNotInTree() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(1, "One");
        bst.put(2, "Two");

        // Act & Assert
        assertFalse(bst.contains(3));
        assertFalse(bst.contains(4));
        assertEquals(false, bst.contains(5));
    }

    @Test
    public void testContainsWithNullKey() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(1, "One");

        // Act
        Exception exception = null;
        try {
            bst.contains(null);
            fail("Tree should not allow null keys for contains method");
        } catch (IllegalArgumentException e) {
            exception = e;
        }

        // Assert
        assertNotNull(exception);
        assertEquals("Key cannot be null", exception.getMessage());
    }

    @Test
    public void testInOrderTraverseOnPopulatedTree() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(10, "Ten");
        bst.put(5, "Five");
        bst.put(20, "Twenty");

        List<String> result = new ArrayList<>();

        // Act
        bst.inOrderTraverse((key, value) -> {
            result.add(value);
            return null;
        });

        // Assert
        List<String> expected = Arrays.asList("Five", "Ten", "Twenty");
        assertEquals(expected, result);
    }

    @Test
    public void testInOrderTraverseOnEmptyTree() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();

        List<String> result = new ArrayList<>();

        // Act
        bst.inOrderTraverse((key, value) -> {
            result.add(value);
            return null;
        });

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInOrderTraverseOnSingleNodeTree() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(30, "Thirty");

        List<String> result = new ArrayList<>();

        // Act
        bst.inOrderTraverse((key, value) -> {
            result.add(value);
            return null;
        });

        // Assert
        assertEquals(Collections.singletonList("Thirty"), result);
    }

    @Test
    public void testDeleteLeafNode() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(10, "Ten");
        bst.put(5, "Five");
        bst.put(15, "Fifteen");

        // Act
        bst.delete(5);

        // Assert
        assertNull(bst.get(5));
        assertEquals(2, bst.size());
        assertTrue(bst.contains(10));
        assertTrue(bst.contains(15));
    }

    @Test
    public void testDeleteNodeWithSingleChild() {
        // Arrange
        BST<Integer, String> bst = new BSTSimple<>();
        bst.put(10, "Ten");
        bst.put(5, "Five");
        bst.put(15, "Fifteen");
        bst.put(12, "Twelve"); // 15 has a single child 12.

        // Act
        String s = bst.delete(15);

        // Assert
        assertNull(bst.get(15));
        assertEquals("Fifteen", s);
        assertEquals(3, bst.size());
        assertTrue(bst.contains(10));
        assertTrue(bst.contains(5));
        assertTrue(bst.contains(12));
    }

    @Test
    public void testDeleteNodeWithTwoChildren() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(10, "Ten"); // 10 has two direction children: 5 and 15
        bst.put(5, "Five"); // 5 has no children.
        bst.put(15, "Fifteen"); // 15 has two children: 12 and 20.
        bst.put(12, "Twelve"); // 12 has no children.
        bst.put(20, "Twenty"); // 20 has no children.

        // Act
        bst.delete(15);

        // Assert
        assertNull(bst.get(15));
        assertEquals(4, bst.size());
        assertTrue(bst.contains(10));
        assertTrue(bst.contains(5));
        assertTrue(bst.contains(12));
        assertTrue(bst.contains(20));
    }

    @Test
    public void testDeleteRootNode() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(10, "Ten");
        bst.put(5, "Five");
        bst.put(15, "Fifteen");

        // Act
        bst.delete(10);

        // Assert
        assertNull(bst.get(10));
        assertEquals(2, bst.size());
        assertTrue(bst.contains(5));
        assertTrue(bst.contains(15));
    }

    @Test
    public void testDeleteNonExistentKey() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();
        bst.put(10, "Ten");
        bst.put(5, "Five");

        // Act
        bst.delete(15); // Non-existent key.

        // Assert
        assertEquals(2, bst.size());
        assertTrue(bst.contains(10));
        assertTrue(bst.contains(5));
    }

    @Test
    public void testDeleteOnEmptyTree() {
        // Arrange
        BSTSimple<Integer, String> bst = new BSTSimple<>();

        // Act
        bst.delete(10); // Deletion on an empty tree.

        // Assert
        assertEquals(0, bst.size());
    }
}