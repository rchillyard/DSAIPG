package com.phasmidsoftware.dsaipg.adt.symbolTable.hashtable;

import com.phasmidsoftware.dsaipg.adt.symbolTable.ST;

import java.util.Optional;

/**
 * HashTable is an abstract hash table implementation using buckets for collision resolution.
 * This class supports basic operations such as insertion, retrieval, and key set generation. It also
 * implements the ST interface for working with key-value pairs.
 *
 * @param <Key>   the type of keys maintained by this hash table.
 * @param <Value> the type of mapped values.
 */
abstract class HashTable<Key, Value> implements ST<Key, Value> {

    /**
     * Insert a key/value pair.
     * If the key already exists, then its value will simply be overwritten.
     * If the value provided is null, we should CONSIDER performing a deletion.
     *
     * @param key   the key.
     * @param value the value.
     */
    public Value put(Key key, Value value) {
        validateKey(key);
        int index = getIndex(key);
        Optional<KeyValuePair<Key, Value>> optionalNode = findKeyValuePair(key, index);
        if (optionalNode.isPresent()) {
            KeyValuePair<Key, Value> keyValuePair = optionalNode.get();
            Value oldValue = keyValuePair.value;
            keyValuePair.value = value;
            return oldValue;
        } else {
            insertKeyValuePair(new KeyValuePair<>(key, value), index);
            return null;
        }
    }

    /**
     * Inserts a key-value pair into the hash table at the specified index.
     *
     * @param kv    the key-value pair to insert into the hash table.
     * @param index the index in the underlying structure where the pair should be placed.
     */
    protected abstract void insertKeyValuePair(KeyValuePair<Key, Value> kv, int index);

    /**
     * Finds a key-value pair in the hash table based on the specified key and index.
     *
     * @param key   the key to search for in the hash table.
     * @param index the index within the underlying structure where the search should be conducted.
     * @return an Optional containing the found key-value pair if it exists, otherwise an empty Optional.
     */
    protected abstract Optional<KeyValuePair<Key, Value>> findKeyValuePair(Key key, int index);

    /**
     * Finds a node in the hash table based on the given key and index.
     *
     * @param key       the key to search for in the hash table.
     * @param index     the index within the underlying structure to search for the key.
     * @param <Element> the type of the element to be returned.
     * @return an Optional containing the found node, if present, otherwise an empty Optional.
     */
    protected abstract <Element> Optional<Element> findNodeByKey(Key key, int index);

    /**
     * Retrieve the value for a given key.
     *
     * @param key the key.
     * @return the value, if key is present, else null.
     */
    public Value get(Key key) {
        validateKey(key);
        return findKeyValuePair(key, getIndex(key)).map(kv -> kv.value).orElse(null);
    }

    /**
     * Computes the index for the given key based on its hash code.
     *
     * @param key the key for which the index is to be computed.
     * @return the computed index within the range of available buckets.
     */
    protected int getIndex(Key key) {
        return (key.hashCode() & 0x7FFFFFFF) % m;
    }

    /**
     * The size of the array in this hash table.
     * This determines the capacity of the hash table and plays a key role in distributing the keys
     * across the buckets to minimize collisions.
     * The value of {@code m} is constant and initialized at the time of constructing the
     * {@code HashTable} instance.
     */
    protected final int m;

    /**
     * Constructs a HashTable with a specified number of buckets.
     *
     * @param m the initial number of buckets for the hash table.
     */
    public HashTable(int m) {
        this.m = m;
    }

    /**
     * Represents a key-value pair used internally within the containing data structure.
     * This class encapsulates a key and its associated value.
     * The value is mutable but the key is not.
     */
    protected static class KeyValuePair<K, V> {

        /**
         * Constructs a KeyValuePair instance with the specified key and value.
         *
         * @param key   the key of the key-value pair; must not be null. This component is immutable.
         * @param value the value associated with the key. This component is mutable.
         */
        public KeyValuePair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "->" + value;
        }

        /**
         * The key component of the KeyValuePair class.
         * This field holds the immutable key for the key-value pair.
         * It serves as the unique identifier for the associated value
         * and cannot be mutated after the pair is created.
         */
        protected final K key;
        /**
         * Represents the mutable value component of a key-value pair in the KeyValuePair class.
         * This field holds the data associated with the immutable key for the key-value pair.
         * It can be updated to reflect changes in the underlying data structure.
         */
        protected V value;
    }

    /**
     * Represents an interface to handle elements that are linked in a sequential structure.
     * Classes implementing this interface should provide functionality to retrieve the next element
     * in the sequence.
     *
     * @param <Element> the type of elements handled by this interface.
     */
    interface HasNext<Element> {
         abstract Element next();
    }

}
