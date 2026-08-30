package com.phasmidsoftware.dsaipg.adt.symbolTable.tree;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;

/**
 * A simple implementation of a Binary Search Tree (BST) that maps keys to values.
 * The class allows basic BST operations such as insertion, retrieval, deletion,
 * in-order traversal, and calculation of depths.
 * <p>
 * The BST implicitly assumes that all keys are unique and comparison of keys
 * will determine their order in the tree structure.
 *
 * @param <Key>   the type of keys maintained by this BST. Must be comparable.
 * @param <Value> the type of mapped values.
 */
public class BSTSimple<Key extends Comparable<Key>, Value> implements BstDetail<Key, Value> {

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
     * Returns the number of elements in the Binary Search Tree (BST).
     * If the tree is empty, the size is 0.
     *
     * @return the total number of elements in this BST.
     */
    public int size() {
        return root != null ? root.count : 0;
    }

    /**
     * Performs an in-order traversal of the Binary Search Tree (BST) and applies the specified function to each node.
     * During in-order traversal, the nodes are visited in ascending order of keys.
     *
     * @param f a BiFunction that takes a key and its associated value as input, and performs operations defined in the function body.
     */
    public void inOrderTraverse(BiFunction<Key, Value, Void> f) {
        doTraverse(0, root, f);
    }

    /**
     * Retrieves the value associated with the specified key in the Binary Search Tree (BST).
     *
     * @param key the key whose associated value is to be returned; cannot be null.
     * @return the value associated with the specified key, or null if the key is not found.
     */
    public Value get(Key key) {
        validateKey(key);
        return get(root, key);
    }

    /**
     * Inserts the specified key-value pair into the Binary Search Tree (BST).
     * If the key is already present, updates the value associated with the key.
     * If the key is not present, adds a new node to the tree.
     *
     * @param key   the key to be associated with the value; cannot be null.
     * @param value the value to associate with the key; can be null.
     * @return the previous value associated with the key, or null if the key was not already present in the tree.
     */
    public Value put(Key key, Value value) {
        validateKey(key);
        NodeValue nodeValue = put(root, key, value);
        if (root == null) root = nodeValue.node;
        return nodeValue.value;
    }

    /**
     * Removes the specified key and its associated value from the Binary Search Tree (BST).
     * After the key is deleted, the structure of the BST is adjusted to maintain its properties.
     *
     * @param key the key to be deleted; cannot be null.
     * @return the original value, if any.
     */
    public Value delete(Key key) {
        validateKey(key);
        Node result = getNode(root, key);
        if (result == null) return null;
        root = delete(root, key);
        return result.value;
    }

    /**
     * Returns a set containing all the keys present in the Binary Search Tree (BST).
     * The keys in the set are unique and represent all the nodes currently in the BST.
     *
     * @return a Set of all keys present in the BST.
     */
    public Set<Key> keySet() {
        Set<Key> set = new TreeSet<>();
        inOrderTraverse((k, v) -> {
            set.add(k);
            return null;
        });
        return set;
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
     * Computes the mean depth of all nodes in the Binary Search Tree (BST).
     * The mean depth is calculated as the total depth of all nodes
     * divided by the number of nodes in the tree.
     * If the tree is empty, the mean depth is 0.
     *
     * @return the mean depth of the BST as a double value.
     */
    public double meanDepth() {
        // TODO implement me.
        return 0;
    }

    /**
     * Default constructor for the BSTSimple class.
     * Initializes an empty Binary Search Tree (BST) with no elements.
     */
    public BSTSimple() {
    }

    /**
     * Constructs a Binary Search Tree (BST) by initializing it as empty and then populating it with the key-value pairs from the provided map.
     * The keys from the map are inserted in a randomized order.
     *
     * @param map a map containing key-value pairs to be inserted into the BST; keys must be unique, and the map cannot be null.
     */
    public BSTSimple(Map<Key, Value> map) {
        this();
        putAll(map);
    }

    Node root = null;

    /**
     * Retrieves the value associated with the specified key in the subtree rooted at the given node.
     *
     * @param node the root of the subtree to search; can be null.
     * @param key the key whose associated value is to be retrieved; cannot be null.
     * @return the value associated with the specified key, or null if the key is not found within the subtree.
     */
    private Value get(Node node, Key key) {
        Node result = getNode(node, key);
        return result != null ? result.value : null;
    }

    /**
     * Retrieves the node associated with the specified key in the subtree rooted at the given node.
     * If the key is found in the subtree, the corresponding node is returned. Otherwise, returns null.
     * Package-private so that we can easily unit-test it.
     *
     * @param node the root of the subtree to search; can be null.
     * @param key the key to search for in the subtree; cannot be null.
     * @return the node associated with the specified key, or null if the key is not found.
     */
    Node getNode(Node node, Key key) {
        if (node == null) return null;
        int cf = key.compareTo(node.key);
        if (cf < 0) return getNode(node.smaller, key);
        else if (cf > 0) return getNode(node.larger, key);
        else return node;
    }

    /**
     * Method to put the key/value pair into the subtree whose root is node.
     *
     * @param node  the root of a subtree
     * @param key   the key to insert
     * @param value the value to associate with the key
     * @return a tuple of Node and Value: Node is the
     */
    private NodeValue put(Node node, Key key, Value value) {
        // If node is null, then we return the newly constructed Node, and value=null
        if (node == null) return new NodeValue(new Node(key, value, 1), null);
        int cf = key.compareTo(node.key);
        if (cf == 0) {
            // If keys match, then we return the node and its value
            NodeValue result = new NodeValue(node, node.value);
            node.value = value;
            return result;
        } else if (cf < 0) {
            // if key is less than node's key, we recursively invoke put in the smaller subtree
            NodeValue result = put(node.smaller, key, value);
            if (node.smaller == null)
                node.smaller = result.node;
            evaluateCount(node);
            return result;
        } else {
            // if key is greater than node's key, we recursively invoke put in the larger subtree
            NodeValue result = put(node.larger, key, value);
            if (node.larger == null)
                node.larger = result.node;
            evaluateCount(node);
            return result;
        }
    }

    /**
     * Removes a node with the specified key from the subtree rooted at the provided node.
     * Adjusts the Binary Search Tree structure to preserve its properties after removal.
     * CONSIDER this should be an instance method of Node.
     *
     * @param x the root of the subtree from which the node will be removed; can be null.
     * @param key the key of the node to be deleted; cannot be null.
     * @return the root of the subtree after the node has been removed; may return null if the subtree becomes empty.
     */
    private Node delete(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.smaller = delete(x.smaller, key);
        }
        else if (cmp > 0) x.larger = delete(x.larger, key);
        else {
            if (x.larger == null) x = x.smaller;
            else if (x.smaller == null) x = x.larger;
            else x = hibbardDeletion(x);
        }
        evaluateCount(x);
        return x;
    }

    private void evaluateCount(Node x) {
        if (x == null) return;
        int result = 1;
        if (x.smaller != null) result += x.smaller.count;
        if (x.larger != null) result += x.larger.count;
        x.count = result;
    }

    /**
     * Performs Hibbard deletion on the given node.
     * This method modifies the tree to delete a node with two children by
     * replacing it with its in-order successor.
     * NOTE: violation of the ASP.
     *
     * @param x the node to be deleted. Must not be null.
     * @return the modified node after performing Hibbard deletion.
     */
    @NotNull
    private Node hibbardDeletion(Node x) {
        // TO BE IMPLEMENTED 
                throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
    }

    /**
     * Removes the smallest node in the subtree rooted at the given node and returns the modified subtree.
     * The smallest node is identified as the leftmost node in the tree, and removing it involves adjusting
     * the tree structure to maintain the Binary Search Tree (BST) properties.
     *
     * @param x the root of the subtree from which the smallest node will be removed; can be null.
     * @return the root of the subtree after removing the smallest node; may return null if the subtree becomes empty.
     */
    private Node deleteMin(Node x) {
        if (x.smaller == null) return x.larger;
        x.smaller = deleteMin(x.smaller);
        evaluateCount(x);
        return x;
    }

    /**
     * Computes the size of the subtree rooted at the given node.
     * The size is defined as the number of nodes in the subtree.
     *
     * @param x the root of the subtree; can be null.
     * @return the size of the subtree rooted at the given node, or 0 if the node is null.
     */
    private int size(Node x) {
        return x == null ? 0 : x.count;
    }

    /**
     * Do a generic traverse of the binary tree starting with node
     *
     * @param q    determines when the function visitor is invoked ( lt 0: pre, ==0: in, gt 0: post)
     * @param node the node
     * @param visitor    the function to be invoked
     */
    private void doTraverse(int q, Node node, BiFunction<Key, Value, Void> visitor) {
        if (node == null) return;
        if (q < 0) visitor.apply(node.key, node.value);
        doTraverse(q, node.smaller, visitor);
        if (q == 0) visitor.apply(node.key, node.value);
        doTraverse(q, node.larger, visitor);
        if (q > 0) visitor.apply(node.key, node.value);
    }

    /**
     * Yield the total depth of this BST. If root is null, then depth will be 0.
     *
     * @return the total number of levels in this BST.
     */
    public int depth() {
        return root != null ? root.depth() : 0;
    }

    /**
     * A helper class to encapsulate a pair consisting of a Node and its associated Value.
     * This class is used to facilitate operations where a tuple of Node and Value needs
     * to be passed around or returned, such as during key-value insertion into the Binary
     * Search Tree (BST).
     */
    private class NodeValue {
        NodeValue(Node node, Value value) {
            this.node = node;
            this.value = value;
        }

        @Override
        public String toString() {
            return node + "<->" + value;
        }

        private final Node node;
        private final Value value;
    }

    /**
     * Represents a node in a Binary Search Tree (BST). Each node contains a key-value pair,
     * a depth indicating its level within the tree, and references to its smaller (left)
     * and larger (right) child nodes. Additional information, such as the count of nodes
     * in its subtree, may also be stored.
     */
    class Node {
        Node(Key key, Value value, int count) {
            this.key = key;
            this.value = value;
            this.count = count;
        }

        /**
         * Finds and returns the node with the smallest key in the subtree rooted at this node.
         * Traverses the left subtree recursively until a node without a left child is reached,
         * which is the node with the smallest key.
         *
         * @return the node with the smallest key in the subtree.
         */
        Node min() {
            return smaller != null ? smaller.min() : this;
        }

        /**
         * Calculates the depth of the subtree rooted at this node in a Binary Search Tree (BST).
         * The depth is defined as the number of edges on the longest path from this node to a leaf node.
         * If the tree is empty, the depth is considered to be 0.
         *
         * @return the depth of the subtree rooted at this node, with the root node contributing a depth of 1.
         */
        int depth() {
            int depthS = smaller != null ? smaller.depth() : 0;
            int depthL = larger != null ? larger.depth() : 0;
            return 1 + Math.max(depthL, depthS);
        }

        /**
         * The unique identifier (key) associated with this node in the Binary Search Tree (BST).
         * This key is used to determine the ordering and position of the node within the tree,
         * adhering to the BST property where keys in the left subtree are less than the node's key,
         * and keys in the right subtree are greater than the node's key. It is immutable.
         */
        final Key key;
        /**
         * The value associated with a given key in the node of a Binary Search Tree (BST).
         * This value represents the data or payload stored in a node, which can be retrieved,
         * updated, or used during various operations within the BST.
         */
        Value value;
        /**
         * The depth of the node in the binary search tree, representing its level from the root node.
         * The root node has a depth of 0, its direct children have a depth of 1, and so on.
         * This value remains constant for a node and helps in calculating tree-related metrics
         * such as the depth of a subtree or the mean depth of the entire tree.
         */
        int depth = 0;
        /**
         * A reference to the smaller (left) child node in a Binary Search Tree (BST).
         * It represents the subtree containing all nodes with keys smaller than the key
         * of the current node. If this node does not have a left child, the value is null.
         */
        Node smaller = null;
        /**
         * Represents a reference to the larger (right) child node in a Binary Search Tree (BST).
         * The larger field points to the node whose key is greater than the key of the current node.
         * If there is no such node, it is set to null, indicating the absence of a right child.
         */
        Node larger = null;
        /**
         * The count variable represents the number of nodes in the subtree rooted at this specific node
         * within the Binary Search Tree (BST). Its value is updated to reflect the total number of nodes
         * in the left and right subtrees, plus the current node itself.
         */
        int count;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Node: " + key + ":" + value);
            if (smaller != null) sb.append(", smaller: ").append(smaller.key);
            if (larger != null) sb.append(", larger: ").append(larger.key);
            return sb.toString();
        }

    }

    /**
     * Creates and returns a new Node object with the specified key, value, and depth.
     * NOTE: this is used by unit tests.
     *
     * @param key   the key associated with the Node
     * @param value the value associated with the Node
     * @param count the depth level of the Node
     * @return a new Node object initialized with the given key, value, and depth
     */
    private Node makeNode(Key key, Value value, int count) {
        return new Node(key, value, count);
    }

    /**
     * Retrieves the root node of the structure.
     * NOTE: this is used by unit tests.
     *
     * @return the root node
     */
    private Node getRoot() {
        return root;
    }

    /**
     * Sets the root node of the structure. If the root is not already set,
     * it increments the count of the root node. Otherwise, it replaces the
     * existing root with the provided node.
     * NOTE: this is used by unit tests.
     *
     * @param node the new node to set as the root of the structure
     */
    private void setRoot(Node node) {
            root = node;
    }

    /**
     * Recursively appends the structure and content of the given node and its subtrees
     * (if they exist) to the provided StringBuffer. The method visually represents the
     * Binary Search Tree (BST) structure including key, value, and hierarchical indentation.
     *
     * @param node   the current node to be displayed; can be null. If null, the method returns immediately.
     * @param sb     a StringBuffer to which the tree representation will be appended.
     * @param indent the current level of indentation used for formatting the output representation.
     */
    private void show(Node node, StringBuffer sb, int indent) {
        if (node == null) return;
        sb.append("  ".repeat(Math.max(0, indent)));
        sb.append(node.key);
        sb.append(": ");
        sb.append(node.value);
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

    public String toString() {
        StringBuffer sb = new StringBuffer();
        show(root, sb, 0);
        return sb.toString();
    }

    /**
     * Computes the depth of the specified key in the subtree rooted at the given node.
     * The depth is the distance from the given node to the node containing the specified key.
     * If the key is not found in the subtree, a DepthException is thrown.
     *
     * @param node the root of the subtree to search; cannot be null.
     * @param key the key whose depth is to be computed; cannot be null.
     * @return the depth of the key relative to the given node, where the depth of the root is 0.
     * @throws DepthException if the key is not found in the subtree.
     */
    private int depth(Node node, Key key) throws DepthException {
        if (node == null) throw new DepthException();
        int cf = key.compareTo(node.key);
        if (cf < 0) return 1 + depth(node.smaller, key);
        else if (cf > 0) return 1 + depth(node.larger, key);
        else return 0;
    }

    /**
     * DepthException is a custom exception class used to signal
     * errors related to depth constraints or issues in a specific context.
     * This exception can be thrown and caught to indicate and handle
     * depth-related problems effectively during program execution.
     */
    private static class DepthException extends Exception {
        public DepthException() {
        }
    }
}
