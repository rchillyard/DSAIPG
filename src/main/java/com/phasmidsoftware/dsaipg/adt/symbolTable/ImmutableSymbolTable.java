/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Interface to model an immutable symbol table.
 *
 * @param <Key>   key type.
 * @param <Value> value type.
 */
public interface ImmutableSymbolTable<Key, Value> {

    /**
     * Tests if this ImmutableSymbolTable maps no keys to value. In other words, its size is 0.
     *
     * @return {@code true} if this ImmutableSymbolTable maps no keys to values;
     * {@code false} otherwise.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Retrieve the value for a given key.
     *
     * @param key the key.
     * @return the value, if key is present, else null.
     */
    Value get(Key key);

    /**
     * Retrieves the value to which the specified key is mapped, or returns the default value if the key is not found.
     *
     * @param key                  the key whose associated value is to be returned.
     * @param defaultValueFunction the (call-by-name) default value to return if the specified key is not present in the symbol table.
     * @return the value associated with the specified key, or the provided default value if the key is not present.
     */
    default Value getOrDefault(Key key, Supplier<Value> defaultValueFunction) {
        Value val = get(key);
        if (val != null) return val;
        else return defaultValueFunction.get();
    }

    /**
     * Get the set of keys in this symbol table.
     *
     * @return the Set of keys.
     */
    Set<Key> keys();

    /**
     * Get the size of this ImmutableSymbolTable.
     *
     * @return the current size.
     */
    int size();
}