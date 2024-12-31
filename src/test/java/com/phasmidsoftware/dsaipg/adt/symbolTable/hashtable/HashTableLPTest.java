/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable.hashtable;

import com.phasmidsoftware.dsaipg.adt.symbolTable.ImmutableSymbolTable;
import com.phasmidsoftware.dsaipg.adt.symbolTable.ST;
import org.junit.Test;

import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class HashTableLPTest {
    @Test
    public void testGetIndex0() {
        assertEquals(2, HashTable_LP.getIndex("Hello0", 2));
        assertEquals(3, HashTable_LP.getIndex("Hello1", 2));
        assertEquals(0, HashTable_LP.getIndex("Hello2", 2));
        assertEquals(1, HashTable_LP.getIndex("Hello3", 2));
        assertEquals(6, HashTable_LP.getIndex("Hello0", 3));
        assertEquals(7, HashTable_LP.getIndex("Hello1", 3));
        assertEquals(0, HashTable_LP.getIndex("Hello2", 3));
        assertEquals(1, HashTable_LP.getIndex("Hello3", 3));
        assertEquals(2, HashTable_LP.getIndex("Hello4", 3));
        assertEquals(3, HashTable_LP.getIndex("Hello5", 3));
        assertEquals(4, HashTable_LP.getIndex("Hello6", 3));
        assertEquals(5, HashTable_LP.getIndex("Hello7", 3));
    }

    @Test
    public void testIsEmpty() {
        final ST<Object, Object> hashTable = new HashTable_LP<>(10);
        assertTrue(hashTable.isEmpty());
        hashTable.put("Hello0", "World!0");
        assertFalse(hashTable.isEmpty());
    }

    @Test
    public void testHashTable0() {
        final ImmutableSymbolTable<Object, Object> hashTable = new HashTable_LP<>(2);
        assertEquals(0, hashTable.size());
        assertTrue(((HashTable_LP<?, ?>) hashTable).check(1, 2));
    }

    @Test
    public void testHashTable1() {
        final HashTable_LP<String, Object> hashTable = new HashTable_LP<>(3);
        assertTrue(hashTable.check(2, 4));
        assertNull(hashTable.getValueMaybe("Hello"));
    }

    @Test
    public void testHashTable2() {
        final ST<String, String> hashTable = new HashTable_LP<>(4);
        assertTrue(((HashTable_LP<?, ?>) hashTable).check(2, 4));
        hashTable.put("Hello", "World!");
        assertEquals(1, hashTable.size());
        assertNotNull(hashTable.get("Hello"));
        assertEquals("World!", hashTable.get("Hello"));
    }

    @Test
    public void testHashTable3() {
        final HashTable_LP<String, String> hashTable = new HashTable_LP<>(4);
        assertTrue(hashTable.check(2, 4));
        hashTable.put("Hello0", "World!0");
        hashTable.show();
        hashTable.put("Hello1", "World!1");
        hashTable.show();
        assertEquals(2, hashTable.size);
        hashTable.put("Hello2", "World!2");
        hashTable.show();
        assertEquals(3, hashTable.size);
        assertNotNull(hashTable.get("Hello0"));
        assertNotNull(hashTable.getValueMaybe("Hello1"));
        assertEquals("World!0", hashTable.get("Hello0"));
        assertEquals("World!1", hashTable.get("Hello1"));
        assertEquals("World!2", hashTable.get("Hello2"));
    }

    @Test(expected = HashTable_LP.HashTableException.class)
    public void testHashTable3a() {
        final ST<String, String> hashTable = new HashTable_LP<>(4);
        hashTable.put("Hello0", "World!0");
        hashTable.put("Hello1", "World!1");
        hashTable.put("Hello2", "World!2");
        hashTable.put("Hello3", "World!3");
        Set<String> keys = hashTable.keys();
        assertEquals(4, keys.size());
    }

    @Test
    public void testHashTable4() {
        Random random = new Random(0L);
        int capacity = 32;
        final ST<String, String> hashTable = new HashTable_LP<>(capacity);
        for (int i = 0; i < capacity - 1; i++)
            hashTable.put(String.valueOf(random.nextInt(100)), String.valueOf(random.nextFloat()));
        assertEquals(28, hashTable.size());
        Set<String> keys = hashTable.keys();
        assertEquals(28, keys.size());
    }

    @Test
    public void testHashTable4a() {
        Random random = new Random(0L);
        int capacity = 32768;
        long freeMemory0 = Runtime.getRuntime().freeMemory();
        System.out.println("free memory: " + freeMemory0);
        final ImmutableSymbolTable<String, String> ht0 = new HashTable_LP<>(capacity);
        assertTrue(ht0.isEmpty());
        long freeMemory1 = Runtime.getRuntime().freeMemory();
        System.out.println("used memory (empty hash table with capacity " + capacity + "): " + (freeMemory0 - freeMemory1));
        final ST<String, String> ht1 = new HashTable_LP<>(capacity);
        assertTrue(ht1.isEmpty());
        long freeMemory2 = Runtime.getRuntime().freeMemory();
        System.out.println("used memory (empty hash table with capacity " + capacity + "): " + (freeMemory1 - freeMemory2));
        for (int i = 0; i < capacity / 2 - 1; i++)
            ht1.put(String.valueOf(random.nextInt(100000)), String.valueOf(random.nextFloat()));
        assertEquals(15148, ht1.size()); // for 32k
//        assertEquals(28060, ht1.size()); // for 64k
        long freeMemory3 = Runtime.getRuntime().freeMemory();
        System.out.println("used memory (half full hash table with capacity " + capacity + "): " + (freeMemory2 - freeMemory3));
        Set<String> keys = ht1.keys();
        System.out.println(keys.size());
    }

    //  TODO  @Test(expected = java.lang.AssertionError.class)
    public void testHashTable5() {
        final HashTable_LP<String, Object> hashTable = new HashTable_LP<>(3);
        hashTable.get("Hello");
    }

    /**
     * Tests the put method of HashTable_LP:
     * Ensures that entries can be added successfully to the hash table.
     */
    @Test
    public void testPutAddsEntry() {
        HashTable_LP<String, Integer> hashTable = new HashTable_LP<>(8);
        hashTable.put("key1", 1);

        assertEquals(1, (int) hashTable.get("key1"));
        assertEquals(1, hashTable.size());
    }

    /**
     * Tests the put method of HashTable_LP:
     * Ensures that an existing entry can be updated with a new value.
     */
    @Test
    public void testPutUpdatesEntry() {
        HashTable_LP<String, Integer> hashTable = new HashTable_LP<>(8);
        hashTable.put("key1", 1);
        hashTable.put("key1", 2);

        assertEquals(2, (int) hashTable.get("key1"));
        assertEquals(1, hashTable.size());
    }

    /**
     * Tests the put method of HashTable_LP:
     * Verifies behavior when multiple keys with distinct hash codes are added.
     */
    @Test
    public void testPutMultipleKeysDistinctHash() {
        HashTable_LP<String, Integer> hashTable = new HashTable_LP<>(8);
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);

        assertEquals(1, (int) hashTable.get("key1"));
        assertEquals(2, (int) hashTable.get("key2"));
        assertEquals(2, hashTable.size());
    }

    /**
     * Tests the put method of HashTable_LP:
     * Ensures that the table handles keys with hash collisions correctly.
     */
    @Test
    public void testPutHandlesHashCollisions() {
        HashTable_LP<Integer, String> hashTable = new HashTable_LP<>(8);
        hashTable.put(1, "value1");
        hashTable.put(9, "value9"); // Assuming hash collision with key 1.

        assertEquals("value1", hashTable.get(1));
        assertEquals("value9", hashTable.get(9));
        assertEquals(2, hashTable.size());
    }

    /**
     * Tests the put method of HashTable_LP:
     * Verifies that an exception is thrown when attempting to insert an entry into a full hash table.
     */
    @Test(expected = HashTable_LP.HashTableException.class)
    public void testPutThrowsExceptionWhenTableFull() {
        HashTable_LP<String, Integer> hashTable = new HashTable_LP<>(2);
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);
        hashTable.put("key3", 3); // Should trigger exception
    }

    /**
     * Tests the put method of HashTable_LP:
     * Checks that adding a null key is handled appropriately.
     */
    @Test(expected = NullPointerException.class)
    public void testPutNullKey() {
        HashTable_LP<String, Integer> hashTable = new HashTable_LP<>(8);
        hashTable.put(null, 1); // Should trigger NullPointerException
    }

    /**
     * Tests the put method of HashTable_LP:
     * Verifies that the correct set of keys is returned after insertion.
     */
    @Test
    public void testPutKeysSet() {
        HashTable_LP<String, Integer> hashTable = new HashTable_LP<>(8);
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);

        Set<String> keys = hashTable.keys();
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
        assertEquals(2, keys.size());
    }

    /**
     * Tests the put method of HashTable_LP:
     * Verifies that the table size remains accurate after multiple insertions and updates.
     */
    @Test
    public void testPutSizeAccuratelyUpdated() {
        HashTable_LP<String, Integer> hashTable = new HashTable_LP<>(8);
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);
        hashTable.put("key1", 3); // Update

        assertEquals(2, hashTable.size());
    }
}