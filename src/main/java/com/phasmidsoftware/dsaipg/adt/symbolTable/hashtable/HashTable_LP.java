/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable.hashtable;

import com.phasmidsoftware.dsaipg.adt.symbolTable.ST;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Class which implements ST (symbol table) by using Linear Probing (Open Addressing).
 *
 * @param <Key>   the key type.
 * @param <Value> the value type.
 */
public class HashTable_LP<Key, Value> implements ST<Key, Value> {

    /**
     * Retrieve the value for a given key.
     *
     * @param key the key.
     * @return the value, if key is present, else null.
     */
    public Value get(Key key) {
        int index = findMatchingIndex(key, getIndex(key, bits));
        return getValue(index);
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
        if (size >= length - 1)
            throw new HashTableException("table is full");
        int index = findMatchingIndex(key, getIndex(key, bits));
        //noinspection unchecked
        Element element = (Element) elements[index];
        if (element != null) {
            assert (getKey(index).equals(key));
            element.value = value;
        } else {
            elements[index] = new Element(key, value);
            size++;
        }
        return null; // TODO return original value
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
        return Arrays.stream(elements).filter(Objects::nonNull).map(elem -> ((Element) elem).key).sorted().collect(Collectors.toCollection(TreeSet::new));
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
        this.bits = (int) Math.ceil(Math.log(capacity) / Math.log(2));
        this.length = (int) Math.pow(2, bits);
        this.elements = new Object[length];
        this.size = 0;
    }

    /**
     * What is this? Why is it different from get?
     *
     * @param key the key.
     * @return the value.
     */
    public Value getValueMaybe(Key key) {
        int index = findMatchingIndex(key, getIndex(key, bits));
        return getValue(index);
    }

    /**
     * Represents an individual key-value pair within a hash table.
     * This class is a simple container for holding a key and its associated value.
     * The key is final and immutable, while the value can be updated based on usage.
     */
    class Element {
        public Element(Key key, Value value) {
            this.value = value;
            this.key = key;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "value=" + value +
                    ", key=" + key +
                    '}';
        }

        Value value;
        final Key key;
    }

    private boolean checkKey(Key key, int index) {
        return key == getKey(index);
    }

    private Key getKey(int index) {
        //noinspection unchecked
        Element element = (Element) elements[index];
        assert (element != null);
        return element.key;
    }

    private Value getValue(int index) {
        //noinspection unchecked
        Element element = (Element) elements[index];
        if (element != null)
            return element.value;
        else
            return null;
    }

    private int findMatchingIndex(Key key, int index) {
        int result = index;
        int hash = key.hashCode();
        while (elements[result] != null) {
            if (getKey(result).hashCode() == hash)
                return result;
            else {
                result++;
                if (result == length) result = 0;
            }
        }
        return result;
    }

    public void show() {
        for (int i = 0; i < length; i++)
            if (elements[i] != null)
                System.out.println("i: " + i + ", key: " + getKey(i) + ", value: " + getValue(i));
    }

    // This is only for testing and should be made private
    boolean check(int bits, int length) {
        return (this.bits == bits && this.length == length);
    }

    private final int length;
    private final int bits;
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