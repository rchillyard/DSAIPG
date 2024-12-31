/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is an implementation of Dictionary which delegates to HashMap.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class Dictionary_Hash<K, V> implements Dictionary<K, V> {

    /**
     * Inserts the specified key-value pair into the map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     *
     * @param k the key with which the specified value is to be associated
     * @param v the value to be associated with the specified key
     */
    public void put(K k, V v) {
        map.put(k, v);
    }

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param k the key whose associated value is to be returned
     * @return the value associated with the specified key, or null if the map contains no mapping for the key
     */
    public V get(K k) {
        return map.get(k);
    }

    /**
     * Returns the number of key-value mappings in the map.
     *
     * @return the size of the map
     */
    public int size() {
        return map.size();
    }

    /**
     * Checks if the map is empty.
     *
     * @return true if the map contains no key-value mappings, false otherwise
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Determines if the map contains a mapping for the specified key.
     *
     * @param key the key whose presence in the map is to be tested
     * @return true if the map contains a mapping for the specified key, false otherwise
     */
    public boolean containsKey(Object key) {
        //noinspection SuspiciousMethodCalls
        return map.containsKey(key);
    }

    /**
     * Removes all mappings from the map, leaving it empty.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns a Set view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are reflected in the set, and vice versa.
     * If the map is modified while an iteration over the set is in progress, the results of the iteration are undefined.
     *
     * @return a Set view of the keys contained in this map
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    private final Map<K, V> map = new HashMap<>();
}