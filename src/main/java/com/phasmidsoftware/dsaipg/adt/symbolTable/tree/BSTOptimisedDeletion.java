/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable.tree;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Binary Search Tree that is not simple and which has optimized deletion mechanism.
 *
 * @param <Key>   the key type (which must be comparable).
 * @param <Value> the value type.
 * @author Robin Hillyard
 * @author Abhishek Ravindra Satbhai;
 */
public class BSTOptimisedDeletion<Key extends Comparable<Key>, Value> implements BstDetail<Key, Value> {

    public Value get(Key key) {
        BiFunction<Node, Node, Node> doGet = (node1, node2) -> node2;
        Node result = root.navigate(key, doGet, doGet);
        return result != null ? result.value : null;
    }

    /**
     * Inserts the specified key-value pair into the binary search tree (BST).
     * If the key already exists in the BST, the value is updated.
     * If the tree is empty, initializes the tree with the given key-value pair.
     *
     * @param key   the key to be added or updated in the BST.
     * @param value the value associated with the key.
     * @return the previous value associated with the key if it existed,
     * or the newly inserted value if the key did not exist.
     */
    public Value put(final Key key, final Value value) {
        if (root != null) {
            Node result = root.navigate(key, doPut(key, value), Node::updateCount);
            assert result != null;
            return result.value;
        } else {
            root = makeNode(key, value, 0);
            return value;
        }
    }

    /**
     * Deletes the specified key from the binary search tree (BST).
     * If the key exists, it removes the associated node and restructures the BST to maintain its properties.
     *
     * @param key the key to be deleted from the BST.
     */
    public void delete(Key key) {
        // CONSIDER using navigate
        root = root.delete(key);
    }

    /**
     * Computes the size of the subtree rooted at the given node.
     * The size is defined as the total number of nodes present in the subtree,
     * including the given node and all its descendants.
     * TODO fix this
     *
     * @param node the root node of the subtree for which the size is to be calculated.
     *             If null, the size is considered to be 0.
     * @return the total number of nodes in the subtree rooted at the given node.
     */
    int size(Node node) {
        if (node == null)
            return 0;
        else
            return (size(node.smaller) + 1 + size(node.larger));
    }

    /**
     * Checks if the binary search tree contains the specified key.
     *
     * @param key the key to check for existence in the binary search tree.
     * @return true if the key is present in the binary search tree, false otherwise.
     */
    public Boolean contains(Key key) {
        return get(key) != null;
    }

    /**
     * This implementation of putAll ensures that the keys are inserted into this BST in random order.
     *
     * @param map a map of key value pairs
     */
    public void putAll(Map<Key, Value> map) {
        List<Key> ks = new ArrayList<>(map.keySet());
        Collections.shuffle(ks);
        for (Key k : ks) put(k, map.get(k));
    }

    /**
     * Returns the size of the binary search tree (BST).
     * The size is defined as the total number of nodes in the tree.
     *
     * @return the size of the BST, or 0 if the tree is empty.
     */
    public int size() {
        return root != null ? root.count : 0;
    }

    /**
     * Performs an in-order traversal of the binary search tree (BST).
     * The provided function is applied to each key-value pair in ascending key order.
     *
     * @param f a {@link BiFunction} that takes a key and value as its parameters and returns {@code Void}.
     *          The function is invoked for each key-value pair during the traversal.
     */
    public void inOrderTraverse(BiFunction<Key, Value, Void> f) {
        // CONSIDER using navigate
        doTraverse(0, root, f);
    }

    /**
     * Deletes the smallest key from the binary search tree (BST).
     * This operation removes the node with the minimum key in the tree
     * and restructures the BST to maintain its properties.
     * If the tree is empty, this method has no effect.
     */
    public void deleteMin() {
        // CONSIDER removing this method.
        root = deleteMin(root);
    }

    /**
     * TODO implement this method properly.
     *
     * @return a set of keys.
     */
    public Set<Key> keySet() {
        return null;
    }

    /**
     * Yield the total depth of this BST. If root is null, then depth will be 0.
     *
     * @return the total number of levels in this BST.
     */
    public int depth() {
        return depth(root);
    }

    /**
     * Method to yield the depth of a key, relative to the root.
     *
     * @param key the key whose depth we are interested in.
     * @return the depth of the key (root: 0) otherwise -1 if key is not found.
     */
    public int depth(Key key) {
        try {
            return depth(root, key);
        } catch (DepthException e) {
            return -1;
        }
    }

    /**
     * The Depth class is designed to accumulate and analyze depth-related statistics
     * during operations on a binary search tree (BST) or similar data structure.
     * It maintains a count of nodes and the total depth of those nodes.
     */
    static class Depth {
        long nodes = 0;
        long totalDepth = 0;

        /**
         * Updates the depth statistics by incrementing the count of nodes
         * and adding the provided depth value to the total depth.
         *
         * @param depth the depth to be added to the total depth.
         */
        void increment(int depth) {
            nodes++;
            totalDepth += depth;
        }

        /**
         * Computes the mean depth of all nodes based on the total accumulated depth and node count.
         * The mean depth is calculated as the total depth divided by the number of nodes.
         *
         * @return the mean depth as a double. If there are no nodes, the result may be undefined.
         */
        double getMeanDepth() {
            return totalDepth * 1.0 / nodes;
        }
    }

    /**
     * Computes the mean depth of all nodes in the binary search tree (BST).
     * The mean depth is calculated by traversing the tree and accumulating
     * the depth of each node, then dividing the total depth by the number of nodes.
     *
     * @return the mean depth of the nodes in the BST as a double. If the tree is empty, the result may be undefined.
     */
    public double meanDepth() {
        final Depth depth = new Depth();
        root.navigate(null, (node1, node2) -> {
            depth.increment(node1.depth);
            return null;
        }, (node1, node2) -> null);
        return depth.getMeanDepth();
    }

    /**
     * Validates the binary search tree (BST) structure by enforcing the correctness of the tree's properties.
     * This method ensures the following:
     * - The depth of each node is consistent with its expected depth in the tree.
     * - The keys in the left subtree of every node are less than the node's key.
     * - The keys in the right subtree of every node are greater than the node's key.
     * <p>
     * This method delegates the validation process to the root node of the tree, starting the validation
     * at depth zero. If the BST violates its structural constraints or properties, assertions are thrown.
     */
    void validate() {
        root.validate(0);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        show(root, sb, 0);
        return sb.toString();
    }

    /**
     * Primary constructor for BSTOptimisedDeletion.
     *
     * @param map  an existing collection of key-value pairs.
     * @param mode the deletion mode:
     *             0: regular Hibbard deletion;
     *             1: random Hibbard deletion;
     *             2: weight-dependent Hibbard deletion.
     */
    public BSTOptimisedDeletion(Map<Key, Value> map, int mode) {
        this.mode = mode;
        if (map != null) putAll(map);
    }

    /**
     * Secondary constructor which yields an empty BST.
     *
     * @param mode the deletion mode: see constructor BSTOptimisedDeletion(Map&lt;Key, Value&gt; map, int mode).
     */
    public BSTOptimisedDeletion(int mode) {
        this(null, mode);
    }

    /**
     * Secondary constructor which yields an empty BST and unoptimized for deletion.
     */
    public BSTOptimisedDeletion() {
        this(0);
    }

    /**
     * Class to represent a node in the BST.
     */
    class Node {

        /**
         * Primary Constructor.
         *
         * @param key   the key.
         * @param value the value.
         * @param depth the depth.
         */
        Node(Key key, Value value, int depth) {
            this.key = key;
            this.value = value;
            this.depth = depth;
            this.count = 1;
        }


        final Key key;
        Value value;
        int depth;
        Node smaller = null;
        Node larger = null;
        int count;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Node: " + key + ":" + value + "@" + depth + " with count=" + count);
            if (smaller != null) sb.append(", smaller: ").append(smaller.key);
            if (larger != null) sb.append(", larger: ").append(larger.key);
            return sb.toString();
        }

        /**
         * Navigates through the binary tree to find and perform operations using the specified key and functions.
         * Depending on the key comparison, it delegates to the appropriate subtree or applies the provided functions.
         *
         * @param k         the key used for navigation and comparison; may be null.
         * @param function1 a BiFunction to apply when key matches or when a subtree is null.
         * @param function2 a BiFunction to combine results from parent and child nodes.
         * @return a Node resulting from the navigation logic, or null if no node matches or key is null.
         */
        private Node navigate(Key k, BiFunction<Node, Node, Node> function1, BiFunction<Node, Node, Node> function2) {
            if (k != null) {
                int cf = k.compareTo(this.key);
                return cf == 0 ? function1.apply(this, this) : navigateSubtree(cf < 0 ? smaller : larger, k, function1, function2);
            } else {
                navigateSubtree(smaller, null, function1, function2);
                navigateSubtree(larger, null, function1, function2);
                return null;
            }
        }

        /**
         * Navigates a specific subtree of the binary tree based on the provided key and applies the given functions.
         * Depending on whether the subtree exists, it either navigates the subtree using the provided key, or it applies
         * the first function if the subtree is null. The result is then processed using the second function.
         *
         * @param subtree   the subtree node to navigate; may be null.
         * @param k         the key used for navigation and comparison; may be null.
         * @param function1 a BiFunction that is applied when a matching node is found or the subtree is null.
         * @param function2 a BiFunction that combines the current node with the result from navigating the subtree.
         * @return a Node resulting from the navigation and the applied functions, or null if the operations result in no valid node.
         */
        private Node navigateSubtree(Node subtree, Key k, BiFunction<Node, Node, Node> function1, BiFunction<Node, Node, Node> function2) {
            Node node = subtree != null ? subtree.navigate(k, function1, function2) : function1.apply(this, null);
            return function2.apply(this, node);
        }

        /**
         * Update the count of this Node by doing a shallow update.
         * We assume that the counts of the children, if any, are accurate.
         *
         * @param other another Node.
         * @return other.
         */
        private Node updateCount(Node other) {
            count = 1 + (smaller != null ? smaller.count : 0) + (larger != null ? larger.count : 0);
            return other;
        }

        /**
         * Deletes the node associated with the given key from the binary tree.
         * If the key matches the current node, it utilizes the Hibbard deletion strategy to find a replacement node
         * or rebalances the tree as necessary. If the key does not match the current node, it recursively delegates
         * the deletion to the appropriate subtree.
         *
         * @param k the key of the node to be deleted.
         * @return the updated node after the deletion process. If the key matches the current node and no subtree exists,
         * returns null. If the key does not exist in the tree, returns the current node unaffected.
         */
        private Node delete(Key k) {
            // CONSIDER using navigate
            // TO BE IMPLEMENTED 
             return null;
            // END SOLUTION
        }

        /**
         * Performs the Hibbard deletion algorithm for binary search trees.
         * Depending on the discriminator's evaluation, it selects either the minimum node
         * from the right subtree or the maximum node from the left subtree as the replacement.
         * It adjusts the subtree depths, links, and counts accordingly to maintain the structure
         * and properties of the tree.
         *
         * @return the resulting Node after applying the Hibbard deletion logic, with
         * updated subtree links, sizes, and depth values.
         */
        private Node getHibbardResult() {
            Node result;
            if (getDiscriminator()) {
                result = min(larger);
                result.larger = deleteMin(larger);
                result.smaller = smaller;
            } else {
                result = max(smaller);
                result.smaller = deleteMax(smaller);
                result.larger = larger;
            }
            result.depth--;
            result.count = size(result.smaller) + size(result.larger) + 1;
            return result;
        }

        /**
         * Determines a boolean discriminator based on the current mode of operation and tree properties.
         * The logic varies as follows:
         * - If the mode is 1, returns a randomly generated boolean value.
         * - If the mode is 2, compares the sizes of the larger and smaller subtrees and returns true if the larger subtree's size
         *   is greater than or equal to the smaller subtree's size.
         * - For any other mode, defaults to returning true.
         *
         * @return a boolean value representing the discriminator, determined by the current mode and subtree relationships.
         */
        private boolean getDiscriminator() {
            switch (mode) {
                case 1:
                    return random.nextBoolean();
                case 2:
                    return size(larger) >= size(smaller);
                default:
                    return true;
            }
        }

        /**
         * Reduces the depth of the current node and all its child nodes in the binary tree.
         * This operation decrements the depth value for the current node and recursively
         * applies the same to the left (smaller) and right (larger) children, if they exist.
         *
         * @return the current node after updating the depth values.
         */
        private Node reduceDepth() {
            // CONSIDER using navigate
            this.depth--;
            if (smaller != null) smaller.reduceDepth();
            if (larger != null) larger.reduceDepth();
            return this;
        }

        /**
         * Validates the structure and symmetry of a binary tree at the current node and recursively for its subtrees.
         * Ensures that the depth of the current node matches the expected depth, and verifies
         * symmetric order properties for the left (smaller) and right (larger) subtrees.
         *
         * @param d the expected depth of the current node.
         */
        private void validate(int d) {
            // CONSIDER using navigate
            assert (this.depth == d) : "At node " + key + ": incorrect depth value: " + depth + " but should be " + d;
            if (smaller != null) {
                assert smaller.key.compareTo(key) < 0 : "Symmetric order violation";
                smaller.validate(d + 1);
            }
            if (larger != null) {
                assert larger.key.compareTo(key) > 0 : "Symmetric order violation";
                larger.validate(d + 1);
            }
        }
    }

    private Node root = null;
    private final int mode;

    /**
     * Produces a BiFunction to handle the insertion of a key-value pair into the binary search tree (BST).
     * The BiFunction operates on two nodes and returns the result of the insertion or update operation.
     *
     * @param key   the key to be inserted or updated in the BST.
     * @param value the value associated with the key to be inserted or updated.
     * @return a BiFunction that takes two Node arguments (representing the current and the adjacent nodes),
     *         and returns the resulting Node after performing the insert or update operation.
     */
    private BiFunction<Node, Node, Node> doPut(final Key key, final Value value) {
        return (node1, node2) -> {
            if (node2 != null) {
                node2.value = value;
                return node2;
            } else {
                if (node1 != null) {
                    node1.count++;
                    int cf = key.compareTo(node1.key);
                    Node node = makeNode(key, value, node1.depth + 1);
                    if (cf < 0) node1.smaller = node;
                    else if (cf > 0) node1.larger = node;
                    else throw new RuntimeException("put: Logic error");
                    return node;
                } else {
                    assert false : "this is impossible";
                    return null;
                }
            }
        };
    }

    /**
     * Deletes the smallest node in the subtree rooted at the given node.
     * If the node to remove is found, its replacement is determined and the subtree is restructured accordingly.
     *
     * @param x the root node of the subtree where the minimum node is to be deleted.
     *          If null, the method has no effect.
     * @return the new root of the subtree after the smallest node has been deleted.
     */
    private Node deleteMin(Node x) {
        // CONSIDER using navigate
        if (x.smaller == null) {
            if (x.larger != null) x.larger.reduceDepth();
            return x.larger;
        } else {
            x.smaller = deleteMin(x.smaller);
            x.count = 1 + size(x.smaller) + size(x.larger);
            return x;
        }
    }

    /**
     * Deletes the largest node in the subtree rooted at the given node.
     * If the largest node has a left child, the subtree is restructured accordingly.
     *
     * @param x the root node of the subtree where the largest node is to be deleted.
     *          If x is null, the method has no effect.
     * @return the new root of the subtree after the largest node has been deleted.
     */
    private Node deleteMax(Node x) {
        // CONSIDER using navigate
        if (x.larger == null) {
            if (x.smaller != null) x.smaller.reduceDepth();
            return x.smaller;
        } else {
            x.larger = deleteMax(x.larger);
            x.count = 1 + size(x.smaller) + size(x.larger);
            return x;
        }
    }

    /**
     * Finds the node with the minimum key in the subtree rooted at the given node.
     * This method recursively navigates the left children of the subtree
     * until the smallest node is found.
     *
     * @param x the root node of the subtree being searched for the minimum key.
     *          If x is null, a runtime exception is thrown.
     * @return the node with the smallest key in the subtree rooted at x.
     * @throws RuntimeException if the provided node x is null.
     */
    private Node min(Node x) {
        // CONSIDER using navigate
        if (x == null) throw new RuntimeException("min not implemented for null");
        else if (x.smaller == null) return x;
        else return min(x.smaller);
    }

    /**
     * Finds the maximum node in a binary search tree starting from the given node.
     *
     * @param x the starting node to search for the maximum node
     * @return the node with the maximum value in the subtree
     *         rooted at the given node
     * @throws RuntimeException if the input node is null
     */
    private Node max(Node x) {
        // CONSIDER using navigate
        if (x == null) throw new RuntimeException("max not implemented for null");
        else if (x.larger == null) return x;
        else return max(x.larger);
    }

    /**
     * Do a generic traverse of the binary tree starting with node
     *
     * @param q    determines when the function f is invoked ( lt 0: pre, ==0: in, gt 0: post)
     * @param node the node
     * @param f    the function to be invoked
     */
    private void doTraverse(int q, Node node, BiFunction<Key, Value, Void> f) {
        if (node == null) return;
        if (q < 0) f.apply(node.key, node.value);
        doTraverse(q, node.smaller, f);
        if (q == 0) f.apply(node.key, node.value);
        doTraverse(q, node.larger, f);
        if (q > 0) f.apply(node.key, node.value);
    }

    /**
     * Computes the depth of a binary tree starting from the given node.
     * Depth is defined as the number of edges on the longest path from the node to a leaf.
     *
     * @param node the root node of the binary tree or subtree for which the depth is to be computed
     * @return the depth of the binary tree or subtree rooted at the given node
     */
    private int depth(Node node) {
        // CONSIDER making this an instance method of Node
        if (node == null) return 0;
        int depthS = depth(node.smaller);
        int depthL = depth(node.larger);
        return 1 + Math.max(depthL, depthS);
    }

    /**
     * Create a new Node from its key, value and depth.
     *
     * @param key   the key.
     * @param value the value.
     * @param depth the depth.
     * @return a new Node.
     */
    private Node makeNode(Key key, Value value, int depth) {
        return new Node(key, value, depth);
    }

    /**
     * NOTE: This method is called through reflection in unit tests.
     *
     * @return the root of this BST.
     */
    private Node getRoot() {
        return root;
    }

    /**
     * NOTE: This method is called through reflection in unit tests.
     *
     * @param node the new root of this BST.
     */
    private void setRoot(Node node) {
        if (root == null) {
            root = node;
            root.count++;
        } else
            root = node;
    }

    /**
     * Displays the structure and content of the given tree node and its children,
     * appending the results to the provided StringBuffer with appropriate formatting.
     *
     * @param node   The starting node of the tree to display. If null, the method does nothing.
     * @param sb     The StringBuffer that collects the formatted output of the tree structure.
     * @param indent The current level of indentation, used for formatting the output.
     */
    private void show(Node node, StringBuffer sb, int indent) {
        // CONSIDER using navigate
        if (node == null) return;
        sb.append("  ".repeat(Math.max(0, indent)));
//        sb.append(node.toString());
        sb.append(node.key);
        sb.append(": ");
        sb.append(node.value);
        sb.append(" @ depth ").append(node.depth);
        sb.append(" with count ").append(node.count);
        sb.append("\n");
        if (node.smaller != null) {
            sb.append("  ".repeat(Math.max(0, indent + 1)));
            sb.append("smaller: ");
            show(node.smaller, sb, indent + 1);
        }
        if (node.larger != null) {
            sb.append("  ".repeat(Math.max(0, indent + 1)));
            sb.append("larger: ");
            show(node.larger, sb, indent + 1);
        }
    }

    /**
     * Computes the depth of a given key within a binary search tree, starting from a specific node.
     *
     * @param node the current node in the binary search tree to start the search from
     * @param key the key whose depth is to be determined
     * @return the depth of the specified key relative to the given node in the tree
     * @throws DepthException if the node is null or the key is not found
     */
    private int depth(Node node, Key key) throws DepthException {
        if (node == null) throw new DepthException();
        int cf = key.compareTo(node.key);
        if (cf < 0) return 1 + depth(node.smaller, key);
        else if (cf > 0) return 1 + depth(node.larger, key);
        else return 0;
    }

    static final Random random = new Random();

    /**
     * DepthException is a custom exception that is thrown to indicate an error
     * related to depth constraints or violations in a specific context.
     * <p>
     * This exception extends the base {@code Exception} class and can be used
     * to signal and handle depth-related issues in an application.
     */
    private static class DepthException extends Exception {
        public DepthException() {
        }
    }
}

