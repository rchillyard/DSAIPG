/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable.hashtable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class which implements ST (symbol table) by using Linear Probing (Open Addressing).
 * <p>
 * NOTE given capacity for a million keys up front, this is as quick as {@code java.util.HashMap}
 * -- 0.056s against 0.059s for a million puts and gets. Written out in Python the same algorithm
 * takes 1.73s, while Python's own {@code dict}, whose inner loop is in C, takes 0.29s. So the cost
 * of writing a hash table rather than calling one is about 6x in Python and nothing at all in Java.
 * That is the reason idiomatic Python leans so hard on its libraries. See
 * {@code docs/Java vs Python.md}.
 *
 * @param <Key>   the key type.
 * @param <Value> the value type.
 */
public class HashTable_LP<Key, Value> extends HashTable<Key, Value> {

    /**
     * Retrieve the value for a given key.
     *
     * @param key the key.
     * @return the value, if key is present, else null.
     */
    public Value get(Key key) {
        validateKey(key);
        int index = getIndex(key);
        Optional<Object> maybeElement = findNodeByKey(key, index);
        //noinspection unchecked
        return maybeElement.map(o -> ((KeyValuePair<Key, Value>) o).value).orElseGet(null);
    }

    /**
     * Insert a key-value pair into the hash table.
     * If the key already exists in the hash table, updates its value.
     * If the hash table is full, throws a HashTableException.
     *
     * @param key   the key to insert or update.
     * @param value the value associated with the specified key.
     * @throws HashTableException if the hash table does not have sufficient capacity to insert the key-value pair.
     */
    public Value put(Key key, Value value) {
        if (size >= m - 1)
            throw new HashTableException("table is full");
        validateKey(key);
        int index = findMatchingIndex(key, getIndex(key));
        //noinspection unchecked
        KeyValuePair<Key, Value> element = (KeyValuePair<Key, Value>) elements[index];
        if (element != null) {
            assert (getKey(index).equals(key));
            Value oldValue = element.value;
            element.value = value;
            return oldValue;
        } else {
            elements[index] = new KeyValuePair<>(key, value);
            size++;
            return null;
        }
    }

    /**
     * Delete a key.
     *
     * @param key the key.
     * @return the original value, if any, otherwise null.
     */
    public Value delete(Key key) {
        return null;
    }

    protected void insertKeyValuePair(KeyValuePair<Key, Value> kv, int index) {

    }

    protected Optional<KeyValuePair<Key, Value>> findKeyValuePair(Key key, int index) {
        return Optional.empty();
    }

    /**
     * Tests if this HashTable_LP maps no keys to value. The general contract
     * for the {@code isEmpty} method is that the result is true if and only
     * if this dictionary contains no entries.
     *
     * @return {@code true} if this HashTable_LP maps no keys to values;
     * {@code false} otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Get the size of this HashTable_LP.
     *
     * @return the current size.
     */
    public int size() {
        return this.size;
    }

    /**
     * Get the set of keys in this symbol table.
     *
     * @return the Set of keys.
     */
    public Set<Key> keys() {
        //noinspection unchecked
        return Arrays.stream(elements).filter(Objects::nonNull).map(elem -> ((KeyValuePair<Key, Value>) elem).key).sorted().collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * What is this? Why is it different from get?
     *
     * @param key the key.
     * @return the value.
     */
    public Value getValueMaybe(Key key) {
        int index = findMatchingIndex(key, getIndex(key));
        return getValue(index);
    }

    /**
     * Constructs a HashTable_LP instance with the specified initial capacity.
     * This constructor initializes necessary data structures and calculates the
     * required properties for internal storage.
     *
     * @param capacity the initial capacity of the hash table. It determines
     *                 the number of slots in the underlying storage, rounded
     *                 to the nearest power of two.
     */
    public HashTable_LP(int capacity) {
        super(capacity);
        this.elements = new Object[capacity];
        this.size = 0;
    }

    /**
     * Retrieves the key of the element at the specified index in the hash table.
     *
     * @param index the index of the element whose key is to be retrieved.
     * @return the key of the element at the specified index.
     * @throws NullPointerException if the element at the specified index is null.
     */
    private Key getKey(int index) {
        //noinspection unchecked
        KeyValuePair<Key, Value> element = (KeyValuePair<Key, Value>) elements[index];
        assert (element != null);
        return element.key;
    }

    /**
     * Retrieves the value associated with an element at a specified index within the hash table.
     * If the index does not contain an element, this method returns null.
     *
     * @param index the index of the element in the hash table.
     * @return the value of the element at the specified index, or null if no element exists at that index.
     */
    private Value getValue(int index) {
        //noinspection unchecked
        KeyValuePair<Key, Value> element = (KeyValuePair<Key, Value>) elements[index];
        if (element != null)
            return element.value;
        else
            return null;
    }

    /**
     * Finds and retrieves an element from the hash table corresponding to the specified key and starting index.
     * The method utilizes {@code findMatchingIndex} to perform the linear probing.
     *
     * @param <Element> the type of the element stored in the hash table.
     * @param key       the key used to locate the element.
     * @param index     the starting index for the search.
     * @return an Optional containing the element if found, or an empty Optional if the element is not present.
     */
    protected <Element> Optional<Element> findNodeByKey(Key key, int index) {
        int i = findMatchingIndex(key, index);
        @SuppressWarnings("unchecked") Element element = (Element) elements[i];
        return Optional.ofNullable(element);
    }

    /**
     * Finds the index of the element in the hash table that matches the given key by performing a linear
     * probing search.
     * If no match is found, returns the index where the next element with the given key could be inserted.
     *
     * @param key   the key to search for.
     * @param index the initial index to start the search from.
     * @return the index of the matching key if found; otherwise, the index where the key can be inserted.
     */
    private int findMatchingIndex(Key key, int index) {
        int result = index;
        while (elements[result] != null) {
            if (getKey(result) == key)
                return result;
            else {
                result++;
                if (result == m) result = 0;
            }
        }
        return result;
    }

    public void show() {
        for (int i = 0; i < m; i++)
            if (elements[i] != null)
                System.out.println("i: " + i + ", key: " + getKey(i) + ", value: " + getValue(i));
    }

    private final Object[] elements;
    int size;

    // TODO should be private
    static int getIndex(Object key, int bits) {
        int mask = 0xFFFFFFFF;
        return key.hashCode() & (mask << bits ^ mask);
    }

    static class HashTableException extends RuntimeException {
        public HashTableException(String s) {
            super(s);
        }
    }
}
