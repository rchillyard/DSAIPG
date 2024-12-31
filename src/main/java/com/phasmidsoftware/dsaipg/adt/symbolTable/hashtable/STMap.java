/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable.hashtable;

import com.phasmidsoftware.dsaipg.adt.symbolTable.ST;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides an implementation of the {@code ST} interface, which represents a symbol table using a generic map backend.
 * This class can store key-value pairs and supports operations such as insertion, retrieval, and querying of key sets.
 * It is a wrapper over any {@code Map} implementation.
 *
 * @param <Key>   the type of keys maintained by this symbol table.
 * @param <Value> the type of mapped values.
 */
public class STMap<Key, Value> implements ST<Key, Value> {
    /**
     * Returns the number of key-value pairs currently stored in the symbol table.
     *
     * @return the number of key-value pairs in the map.
     */
    public int size() {
        return map.size();
    }

    /**
     * Retrieve the value for a given key.
     *
     * @param key the key.
     * @return the value, if key is present, else null.
     */
    public Value get(Key key) {
        return map.get(key);
    }

    /**
     * Get the set of keys in this symbol table.
     *
     * @return the Set of keys.
     */
    public Set<Key> keys() {
        return map.keySet();
    }

    /**
     * Insert a key/value pair.
     * If the key already exists, then its value will simply be overwritten.
     *
     * @param key the key.
     * @param val the value.
     * @return the original value associated with <code>key</code>, if any, otherwise null.
     */
    public Value put(Key key, Value val) {
        return map.put(key, val);
    }

    /**
     * Constructs an STMap instance with the provided map.
     * This constructor initializes the symbol table using the given map as its backend storage.
     *
     * @param map a Map instance that serves as the underlying storage for key-value pairs.
     */
    public STMap(Map<Key, Value> map) {
        this.map = map;
    }

    /**
     * Default constructor for the STMap class.
     * This constructor initializes the underlying symbol table implementation with an empty {@code HashMap}.
     */
    public STMap() {
        this(new HashMap<>());
    }

    @Override
    public String toString() {
        return map.toString();
    }

    private final Map<Key, Value> map;
}