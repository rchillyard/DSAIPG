/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable.tree;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * The BstDetail interface defines the operations available for a Binary Search Tree (BST).
 * It extends the generic BST interface, associating specific methods to enhance BST functionality.
 *
 * @param <Key>   the type of keys maintained by this BST; must be comparable.
 * @param <Value> the type of values maintained by this BST.
 */
public interface BstDetail<Key extends Comparable<Key>, Value> extends BST<Key, Value> {

    /**
     * Determine if this BST contains key.
     *
     * @param key the key to find.
     * @return true if this contains key.
     */
    Boolean contains(Key key);

    /**
     * Method to input a Map of key-value pairs.
     *
     * @param map the given map.
     */
    void putAll(Map<Key, Value> map);

    /**
     * Method to visit all keys based on the inorder form of traverse.
     * CONSIDER returning a Map of key-value pairs.
     *
     * @param f the function to invoke for each node.
     */
    void inOrderTraverse(BiFunction<Key, Value, Void> f);

    /**
     * Delete the minimum element of the BST.
     */
    void deleteMin();

    /**
     * CONSIDER this seems to be incompatible with depth(Key)
     *
     * @return the depth of the whole tree.
     */
    int depth();

    /**
     * Method to yield the depth of a key, relative to the root.
     *
     * @param key the key whose depth we are interested in.
     * @return the depth of the key (root: 0) otherwise -1 if key is not found.
     */
    int depth(Key key);

    /**
     * Computes the mean depth of all nodes in the Binary Search Tree (BST).
     * The mean depth is the average of the depths of all nodes relative to the root.
     *
     * @return the mean depth as a double value. If the tree is empty, return 0.0.
     */
    double meanDepth();
}