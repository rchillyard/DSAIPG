package com.phasmidsoftware.dsaipg.adt.symbolTable.hashtable;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class STMapTest {

    /**
     * Test to verify that the 'put' method adds a new key-value pair to the map.
     */
    @Test
    public void testPutAddsNewKeyValuePair() {
        STMap<String, Integer> stMap = new STMap<>();
        stMap.put("key1", 10);
        assertEquals(Integer.valueOf(10), stMap.get("key1"));
        assertEquals(1, stMap.size());
    }

    /**
     * Test to verify that the 'put' method replaces an existing value for the same key.
     */
    @Test
    public void testPutReplacesValueForExistingKey() {
        STMap<String, Integer> stMap = new STMap<>();
        stMap.put("key1", 10);
        stMap.put("key1", 20);
        assertEquals(Integer.valueOf(20), stMap.get("key1"));
        assertEquals(1, stMap.size());
    }

    /**
     * Test to verify that the 'put' method allows null values to be stored for a valid key.
     */
    @Test
    public void testPutAllowsNullValues() {
        STMap<String, Integer> stMap = new STMap<>();
        stMap.put("key1", null);
        assertNull(stMap.get("key1"));
        assertEquals(1, stMap.size());
    }

    /**
     * Test to verify that the 'put' method handles adding multiple distinct key-value pairs.
     */
    @Test
    public void testPutAddsMultipleKeyValuePairs() {
        STMap<String, Integer> stMap = new STMap<>();
        stMap.put("key1", 10);
        stMap.put("key2", 20);
        stMap.put("key3", 30);
        assertEquals(Integer.valueOf(10), stMap.get("key1"));
        assertEquals(Integer.valueOf(20), stMap.get("key2"));
        assertEquals(Integer.valueOf(30), stMap.get("key3"));
        assertEquals(3, stMap.size());
    }

    /**
     * Test to verify that the 'put' method can populate keys and values in a pre-initialized map.
     */
    @Test
    public void testPutWithPreInitializedMap() {
        HashMap<String, Integer> initialMap = new HashMap<>();
        initialMap.put("key1", 10);
        STMap<String, Integer> stMap = new STMap<>(initialMap);
        stMap.put("key2", 20);
        assertEquals(Integer.valueOf(10), stMap.get("key1"));
        assertEquals(Integer.valueOf(20), stMap.get("key2"));
        assertEquals(2, stMap.size());
    }
}