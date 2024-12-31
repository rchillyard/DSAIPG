package com.phasmidsoftware.dsaipg.adt.bqs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DictionaryTest {

    /**
     * Test case: Adding a single key-value pair to the dictionary.
     * This checks if the dictionary correctly stores the value for a given key.
     */
    @Test
    public void testPutSingleEntry() {
        Dictionary_Hash<String, String> dictionary = new Dictionary_Hash<>();
        dictionary.put("key1", "value1");

        assertEquals("value1", dictionary.get("key1"));
        assertEquals(1, dictionary.size());
    }

    /**
     * Test case: Updating an existing key with a new value.
     * This ensures that the value associated with a key can be updated.
     */
    @Test
    public void testPutUpdateExistingKey() {
        Dictionary_Hash<String, String> dictionary = new Dictionary_Hash<>();
        dictionary.put("key1", "value1");
        dictionary.put("key1", "value2");

        assertEquals("value2", dictionary.get("key1"));
        assertEquals(1, dictionary.size());
    }

    /**
     * Test case: Adding multiple unique key-value pairs.
     * This verifies if the dictionary supports multiple entries with distinct keys.
     */
    @Test
    public void testPutMultipleEntries() {
        Dictionary_Hash<String, String> dictionary = new Dictionary_Hash<>();
        dictionary.put("key1", "value1");
        dictionary.put("key2", "value2");

        assertEquals("value1", dictionary.get("key1"));
        assertEquals("value2", dictionary.get("key2"));
        assertEquals(2, dictionary.size());
    }

    /**
     * Test case: Adding a null key to the dictionary.
     * This verifies if the dictionary can successfully store and retrieve with a null key.
     */
    @Test
    public void testPutNullKey() {
        Dictionary_Hash<String, String> dictionary = new Dictionary_Hash<>();
        dictionary.put(null, "value1");

        assertEquals("value1", dictionary.get(null));
        assertEquals(1, dictionary.size());
    }

    /**
     * Test case: Adding a null value to the dictionary.
     * This ensures that the dictionary can store null values properly.
     */
    @Test
    public void testPutNullValue() {
        Dictionary_Hash<String, String> dictionary = new Dictionary_Hash<>();
        dictionary.put("key1", null);

        assertNull(dictionary.get("key1"));
        assertEquals(1, dictionary.size());
    }

    /**
     * Test case: Adding a null key and null value to the dictionary.
     * This verifies if null keys and null values can coexist in the dictionary.
     */
    @Test
    public void testPutNullKeyAndValue() {
        Dictionary_Hash<String, String> dictionary = new Dictionary_Hash<>();
        dictionary.put(null, null);

        assertNull(dictionary.get(null));
        assertEquals(1, dictionary.size());
    }

    /**
     * Test case: Adding and replacing multiple entries.
     * This ensures that after updating entries with the same key, only the latest entry persists.
     */
    @Test
    public void testPutReplaceMultipleEntries() {
        Dictionary_Hash<String, String> dictionary = new Dictionary_Hash<>();
        dictionary.put("key1", "value1");
        dictionary.put("key2", "value2");
        dictionary.put("key1", "newValue1");

        assertEquals("newValue1", dictionary.get("key1"));
        assertEquals("value2", dictionary.get("key2"));
        assertEquals(2, dictionary.size());
    }
}