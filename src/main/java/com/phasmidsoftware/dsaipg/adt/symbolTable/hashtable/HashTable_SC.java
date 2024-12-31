/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable.hashtable;

import com.phasmidsoftware.dsaipg.adt.symbolTable.ST;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HashTable_SC is a hash table implementation using separate chaining (SC) for collision resolution.
 * This class supports basic operations such as insertion, retrieval, and key set generation. It also
 * implements the ST interface for working with key-value pairs.
 *
 * @param <Key>   the type of keys maintained by this hash table.
 * @param <Value> the type of mapped values.
 */
public class HashTable_SC<Key, Value> implements ST<Key, Value> {
    /**
     * Get the size of this ImmutableSymbolTable.
     *
     * @return the current size.
     */
    public int size() {
        int result = 0;
        for (Object bucket : buckets) //noinspection unchecked
            result += (int) nodesAsStream((Node) bucket).count();
        return result;
    }

    /**
     * Insert a key/value pair.
     * If the key already exists, then its value will simply be overwritten.
     * If the value provided is null, we should CONSIDER performing a deletion.
     *
     * @param key   the key.
     * @param value the value.
     */
    public Value put(Key key, Value value) {
        int index = getIndex(key);
        @SuppressWarnings("unchecked") Node bucket = (Node) buckets[index];
        List<Node> matches = nodesAsStream(bucket).takeWhile(Objects::nonNull).filter(node -> node.key.equals(key)).toList();
        if (matches.size() == 1)
            matches.get(0).value = value;
        else if (matches.isEmpty())
            buckets[index] = new Node(key, value, bucket);
        else
            throw new HashTable_LP.HashTableException("HashTable_SC:put: logic error");
        return null; // TODO return the original value
    }

    /**
     * Retrieve the value for a given key.
     *
     * @param key the key.
     * @return the value, if key is present, else null.
     */
    public Value get(Key key) {
        Object bucket = buckets[getIndex(key)];
        if (bucket == null) return null;
        //noinspection unchecked
        return nodesAsStream((Node) bucket).filter(n -> n != null && n.key == key).findFirst().map(node -> node.value).orElse(null);
    }

    /**
     * Get the set of keys in this symbol table.
     *
     * @return the Set of keys.
     */
    public Set<Key> keys() {
        Set<Key> result = new TreeSet<>();
        for (Object bucket : buckets)
            //noinspection unchecked
            result.addAll(nodesAsStream((Node) bucket).map(node -> node.key).toList());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < buckets.length; i++) {
            //noinspection unchecked
            Node bucket = (Node) buckets[i];
            if (bucket != null) {
                List<Node> nodes = nodesAsStream(bucket).collect(Collectors.toList());
                result.append(i).append(": ");
                result.append(nodes);
                result.append("\n");
            }
        }
        return result.toString();
    }

    /**
     * Construct a new HashTable_SC with m buckets.
     *
     * @param m the required number of buckets.
     */
    public HashTable_SC(int m) {
        this.m = m;
        this.buckets = new Object[m];
    }

    /**
     * Construct a new HashTable_SC with 16 buckets.
     */
    public HashTable_SC() {
        this(16);
    }

    /**
     * Computes the index for the given key based on its hash code.
     *
     * @param key the key for which the index is to be computed.
     * @return the computed index within the range of available buckets.
     */
    private int getIndex(Key key) {
        return (key.hashCode() & 0x7FFFFFFF) % m;
    }

    /**
     * Converts the linked list of {@code Node} objects starting from the given {@code bucket} into a {@code Stream} of nodes.
     *
     * @param bucket the starting node of the linked list, can be null.
     * @return a {@code Stream} of {@code Node} objects, or an empty stream if the {@code bucket} is null.
     */
    private Stream<Node> nodesAsStream(Node bucket) {
        if (bucket == null)
            return Stream.empty();
        else
            return Stream.iterate(bucket, Objects::nonNull, node -> node.next);
    }

    private final int m;
    private final Object[] buckets;

    /**
     * A class representing a Node in a singly linked list structure used in the HashTable_SC class.
     * Each Node object stores a key-value pair and a reference to the next Node in the chain.
     * This is intended to be a private inner class within the enclosing HashTable_SC class.
     */
    private class Node {
        /**
         * Constructs a Node object for use in a singly linked list. Each Node
         * stores a key-value pair and a reference to the next Node in the chain.
         *
         * @param key   the key associated with the Node.
         * @param value the value associated with the Node.
         * @param next  the reference to the next Node in the linked list.
         */
        public Node(Key key, Value value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public String toString() {
            return key + ":" + value;
        }

        private final Key key;
        private Value value;
        // NOTE: this will need to be mutable when we implement delete.
        private final Node next;
    }
}