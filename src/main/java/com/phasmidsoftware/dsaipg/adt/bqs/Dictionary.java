/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import java.util.Set;

/**
 * The purpose of defining this interface is really just to illustrate the use of an interface for encapsulation purposes.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public interface Dictionary<K, V> {

    /**
     * Inserts a key-value pair into the dictionary. If the key already exists, the associated value is updated.
     *
     * @param k the key to insert or update
     * @param v the value to associate with the key
     */
    void put(K k, V v);

    /**
     * Retrieves the value associated with the given key in the dictionary.
     *
     * @param k the key for which the associated value needs to be retrieved
     * @return the value associated with the specified key, or null if the key is not found
     */
    V get(K k);

    /**
     * Returns the number of elements present.
     *
     * @return the total count of elements.
     */
    int size();

    /**
     * Determines if the collection or data structure is empty.
     *
     * @return true if the collection or data structure contains no elements, false otherwise.
     */
    boolean isEmpty();

    /**
     * Checks if the dictionary contains a mapping for the specified key.
     *
     * @param key the key to check for existence in the dictionary
     * @return true if the dictionary contains a mapping for the specified key, false otherwise
     */
    boolean containsKey(Object key);

    /**
     * Removes all elements from the structure, leaving it empty.
     */
    void clear();

    /**
     * Returns a Set view of the keys contained in the dictionary.
     *
     * @return a Set of keys present in the dictionary.
     */
    Set<K> keySet();
}