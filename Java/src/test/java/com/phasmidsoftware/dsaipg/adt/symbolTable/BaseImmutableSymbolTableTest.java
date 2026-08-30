package com.phasmidsoftware.dsaipg.adt.symbolTable;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class BaseImmutableSymbolTableTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * Mock implementation of BaseImmutableSymbolTable for testing purposes.
     */
    private static class MockBaseImmutableSymbolTable<Key, Value> extends BaseImmutableSymbolTable<Key, Value> {
        public MockBaseImmutableSymbolTable(ST<Key, Value> map, Supplier<Value> defaultSupplier) {
            super(map, defaultSupplier);
        }
    }

    /**
     * Mock implementation of ST interface.
     */
    private static class MockST<Key, Value> implements ST<Key, Value> {
        private final Map<Key, Value> internalMap = new HashMap<>();

        /**
         * Retrieve the value for a given key.
         *
         * @param key the key.
         * @return the value, if key is present, else null.
         */
        @Override
        public Value get(Key key) {
            return internalMap.get(key);
        }

        @Override
        public Value getOrDefault(Key key, Supplier<Value> defaultSupplier) {
            return internalMap.getOrDefault(key, defaultSupplier.get());
        }

        @Override
        public Set<Key> keys() {
            return internalMap.keySet();
        }

        @Override
        public int size() {
            return internalMap.size();
        }

        public Value put(Key key, Value value) {
            return internalMap.put(key, value);
        }

        /**
         * Delete a key.
         *
         * @param key the key.
         * @return the original value, if any, otherwise null.
         */
        @Override
        public Value delete(Key key) {
            return internalMap.remove(key);
        }
    }

    @Test
    public void testGetExistingKey() {
        // Setup
        MockST<String, String> mockST = new MockST<>();
        mockST.put("key1", "value1");
        MockBaseImmutableSymbolTable<String, String> table = new MockBaseImmutableSymbolTable<>(mockST, () -> "default");

        // Test
        String result = table.get("key1");

        // Assert
        assertEquals("value1", result);
    }

    @Test
    public void testGetNonExistentKeyWithDefaultValue() {
        // Setup
        MockST<String, String> mockST = new MockST<>();
        MockBaseImmutableSymbolTable<String, String> table = new MockBaseImmutableSymbolTable<>(mockST, () -> "default");

        // Test
        String result = table.get("nonExistentKey");

        // Assert
        assertEquals("default", result);
    }

    @Test
    public void testGetNullKey() {
        // Setup
        MockST<String, String> mockST = new MockST<>();
        MockBaseImmutableSymbolTable<String, String> table = new MockBaseImmutableSymbolTable<>(mockST, () -> "default");

        // Test & Assert
        try {
            table.get(null);
        } catch (IllegalArgumentException e) {
            assertEquals("ST:get: key is null", e.getMessage());
        }
    }

    @Test
    public void testGetWithEmptyTable() {
        // Setup
        MockST<String, String> mockST = new MockST<>();
        MockBaseImmutableSymbolTable<String, String> table = new MockBaseImmutableSymbolTable<>(mockST, () -> "default");

        // Test
        String result = table.get("key1");

        // Assert
        assertEquals("default", result);
    }

    @Test
    public void testGetWithNullDefaultValue() {
        // Setup
        MockST<String, String> mockST = new MockST<>();
        MockBaseImmutableSymbolTable<String, String> table = new MockBaseImmutableSymbolTable<>(mockST, () -> null);

        // Test
        String result = table.get("nonExistentKey");

        // Assert
        assertNull(result);
    }
}