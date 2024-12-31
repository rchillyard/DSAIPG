/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable.tree;

import com.phasmidsoftware.dsaipg.adt.symbolTable.ST;

import java.util.Set;

/**
 * Interface defining the core methods for a Binary Search Tree (BST).
 * A BST is a data structure that stores key-value pairs in a sorted manner
 * such that for every node, keys in the left subtree are less than the node's key,
 * and keys in the right subtree are greater.
 *
 * @param <Key>   the type of keys, which must be Comparable.
 * @param <Value> the type of values associated with the keys.
 */
public interface BST<Key extends Comparable<Key>, Value> extends ST<Key, Value> {

    /**
     * @return The set of all keys.
     */
    Set<Key> keySet();

    /**
     * Get the set of keys in this symbol table.
     * CONSIDER why do we have keys AND keySet()?
     *
     * @return the Set of keys.
     */
    @Override
    default Set<Key> keys() {
        return keySet();
    }

    /**
     * Delete the given key.
     * CONSIDER returning the original value.
     *
     * @param key the key to be deleted.
     */
    void delete(Key key);
}