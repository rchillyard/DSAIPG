package com.phasmidsoftware.dsaipg.adt.symbolTable.hashtable;

import org.jetbrains.annotations.NotNull;

import java.util.*;
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
public class HashTable_SC<Key, Value> extends HashTable<Key, Value> {
    /**
     * Get the size of this ImmutableSymbolTable.
     *
     * @return the current size.
     */
    public int size() {
        int result = 0;
        for (Object bucket : buckets) //noinspection unchecked
            result += (int) nodesAsStream((Node<Key, Value>) bucket).count();
        return result;
    }

    /**
     * Inserts a key-value pair at the head of the bucket indicated by the specified index.
     * This method constructs a new {@code Node} object using the provided key-value pair.
     *
     * @param kv    the {@code KeyValuePair} to be inserted, consisting of a key and its associated value.
     * @param index the index of the bucket where the key-value pair will be inserted.
     */
    protected void insertKeyValuePair(KeyValuePair<Key, Value> kv, int index) {
        @SuppressWarnings("unchecked") Node<Key, Value> bucket = (Node<Key, Value>) buckets[index];
        Node<Key, Value> node = new Node<>(kv, bucket);
        buckets[index] = node;
    }

    /**
     * Finds and retrieves the key-value pair associated with the specified key by searching
     * in the bucket at the given index.
     * It first finds the appropriate {@code Node} using {@code findNodeByKey}.
     * If a matching key exists, the corresponding key-value pair is returned wrapped in an
     * {@code Optional}.
     *
     * @param key   the key whose associated key-value pair needs to be found.
     * @param index the index of the bucket in which to search for the key.
     * @return an {@code Optional} containing the key-value pair if found, or an empty {@code Optional} if no match exists.
     */
    protected Optional<KeyValuePair<Key, Value>> findKeyValuePair(Key key, int index) {
        Optional<Node<Key, Value>> optionalNode = findNodeByKey(key, index);
        return optionalNode.map(node -> node.keyValuePair);
    }

    /**
     * Finds and retrieves the first node matching the specified key within the bucket at the given index.
     * The method searches the bucket for nodes whose key matches the provided key and ensures there is at most one match.
     * If multiple matches are found, an exception is thrown. If no match is found, an empty {@code Optional} is returned.
     *
     * @param key       the key to search for in the specified bucket.
     * @param index     the index of the bucket within the hash table where the search will be performed.
     * @param <Element> the type of the element to be returned, inferred from the caller's context.
     * @return an {@code Optional} containing the matching node if found, or an empty {@code Optional} if no match exists.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    protected <Element> Optional<Element> findNodeByKey(Key key, int index) {
        Node<Key, Value> bucket = (Node<Key, Value>) buckets[index];
        List<Node<Key, Value>> matches = nodesAsStream(bucket).takeWhile(Objects::nonNull).filter(node -> node.keyValuePair.key.equals(key)).toList();
        if (matches.size() > 1)
            throw new HashTable_LP.HashTableException("HashTable:findNode: logic error: more than one matching key: " + key + " at index: " + index);
        Optional<Node<Key, Value>> first = matches.stream().findFirst();
        return (Optional<Element>) first;
    }

    /**
     * Two-pass implementation to delete a key-value pair.
     *
     * @param key the key.
     * @return the original value, if any, otherwise null.
     */
    public Value delete(Key key) {
        validateKey(key);
        int index = getIndex(key);
        Optional<Node<Key, Value>> optionalNode = findNodeByKey(key, index);
        if (optionalNode.isPresent()) {
            Node<Key, Value> node = optionalNode.get();
            @SuppressWarnings("unchecked") Node<Key, Value> bucket = (Node<Key, Value>) buckets[index];
            Stream<Node<Key, Value>> nodeStream = nodesAsStream(bucket).takeWhile(n -> !n.equals(node));
            List<Node<Key, Value>> list = nodeStream.toList();
            Node<Key, Value> node1 = list.get(list.size() - 1);
            node1.next = node.next;
            return node.keyValuePair.value;
        } else return null;
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
            result.addAll(nodesAsStream((Node<Key, Value>) bucket).map(node -> node.keyValuePair.key).toList());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < buckets.length; i++) {
            //noinspection unchecked
            Node<Key, Value> bucket = (Node<Key, Value>) buckets[i];
            if (bucket != null) {
                List<Node<Key, Value>> nodes = nodesAsStream(bucket).collect(Collectors.toList());
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
        super(m);
        this.buckets = new Object[m];
    }

    /**
     * Construct a new HashTable_SC with 16 buckets.
     */
    public HashTable_SC() {
        this(16);
    }

    /**
     * Converts the linked list of {@code Node} objects starting from the given {@code bucket} into a {@code Stream} of nodes.
     *
     * @param bucket the starting node of the linked list, can be null.
     * @return a {@code Stream} of {@code Node} objects, or an empty stream if the {@code bucket} is null.
     */
    private <Element extends HasNext<Element>> Stream<Element> nodesAsStream(Element bucket) {
        if (bucket == null)
            return Stream.empty();
        else
            return Stream.iterate(bucket, Objects::nonNull, HasNext::next);
    }

    /**
     * An array representing the set of buckets used to store key-value pairs in the hash table.
     * Each bucket typically contains a linked list or similar structure to handle collisions via chaining.
     * <p>
     * This field is initialized during the construction of the hash table and its size does not change.
     * The number of buckets determines the hash table's capacity and affects its performance by reducing collisions.
     */
    private final Object[] buckets;

    /**
     * A class representing a Node in a singly linked list structure used in the HashTable_SC class.
     * Each Node object stores a key-value pair and a reference to the next Node in the chain.
     * This is intended to be a private inner class within the enclosing HashTable_SC class.
     */
    static class Node<K, V> implements HasNext<Node<K, V>> {
        /**
         * Constructs a Node object for use in a singly linked list. Each Node
         * stores a key-value pair and a reference to the next Node in the chain.
         *
         * @param keyValuePair a KeyValuePair.
         * @param next         the reference to the next Node in the linked list.
         */
        public Node(KeyValuePair<K, V> keyValuePair, Node<K, V> next) {
            this.keyValuePair = keyValuePair;
            this.next = next;
        }

        @Override
        public String toString() {
            return keyValuePair.toString();
        }

        /**
         * Represents the key-value pair encapsulated within the Node class.
         * This variable is a reference to the KeyValuePair instance associated
         * with the current Node in the singly linked list structure.
         * It holds the key and its corresponding value as a single entity
         * and is immutable due to its final declaration.
         */
        private final KeyValuePair<K, V> keyValuePair;

        /**
         * A reference to the next Node in the singly linked list.
         * This variable is used to link Node objects together,
         * forming the chain in a hash table's separate chaining implementation.
         * This must be mutable when Node deletion is supported.
         */
        private Node<K, V> next;

        /**
         * Returns the next node in the singly linked list structure.
         *
         * @return the next Node in the chain, or null if there is no next node.
         */
        public Node<K, V> next() {
            return next;
        }
    }
}
