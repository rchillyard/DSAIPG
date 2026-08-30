package com.phasmidsoftware.dsaipg.misc.lab_1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Objects;

/**
 * This class represents an immutable tree with immutable nodes.
 * Yes, it does work!
 *
 * @param <X> the underlying type of the nodes and tree.
 */
public class MyTree<X> {

    /**
     * Represents a node in a tree structure with a value of type X and a list of child nodes.
     *
     * @param <X> the type of value held by the node.
     */
    public static class Node<X> {
        /**
         * Constructor to create a new Node from an X value and a set of child nodes.
         *
         * @param x        a value of X.
         * @param children the set of children for the new Node.
         */
        Node(X x, ImmutableList<Node<X>> children) {
            this.x = x;
            this.children = children;
        }

        /**
         * Constructor to create a new Node from an X value and no children.
         *
         * @param x a value of X.
         */
        Node(X x) {
            this(x, ImmutableList.of());
        }

        /**
         * Method to add a child of value X to this tree.
         *
         * @param y a Node.
         * @return a copy of this Node but with an additional child.
         */
        public Node<X> addChild(Node<X> y) {
            ImmutableList.Builder<Node<X>> builder = ImmutableList.builder();
            // TO BE IMPLEMENTED 
                        throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
        }

        /**
         * Method to add a child of value X to this tree.
         *
         * @param xx the value of X.
         * @return a copy of this Node but with an additional child.
         */
        public Node<X> addChild(X xx) {
            return addChild(new Node<>(xx));
        }

        /**
         * Method to replace child y by z in this Node.
         *
         * @param y the Node which is to replace y.
         * @return the new Node which is a copy of this Node but with y replaced by z.
         */
        public Node<X> replace(Node<X> y, Node<X> z) {
            Collection<Node<X>> ns = Lists.newArrayList(children.iterator());
            boolean ok = Iterables.removeIf(ns, xNode -> Objects.equals(xNode, y));
            ImmutableList.Builder<Node<X>> builder = ImmutableList.builder();
            // TO BE IMPLEMENTED 
                        throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
        }

        /**
         * Method to replace child y by z in this Node.
         *
         * @param y the Node which is to replace y.
         * @return the new Node which is a copy of this Node but with y replaced by z.
         */
        public Node<X> replace(Node<X> y, X z) {
            return replace(y, new Node<>(z));
        }

        final X x;
        final ImmutableList<Node<X>> children;
    }

    /**
     * Public constructor for MyTree from an explicit root node.
     *
     * @param root the root node.
     */
    public MyTree(Node<X> root) {
        this.root = root;
    }

    /**
     * Public constructor for MyTree from an explicit value for the root.
     *
     * @param x an X value.
     */
    public MyTree(X x) {
        this(new Node<>(x));
    }

    /**
     * Retrieves the root node of this tree.
     *
     * @return the root Node of type X.
     */
    public Node<X> getRoot() {
        return root;
    }

    /**
     * Method to add a child of value X to this tree.
     *
     * @param y a Node.
     * @return a copy of this Node but with an additional child.
     */
    public MyTree<X> addChild(Node<X> y) {
        return new MyTree<>(root.addChild(y));
    }

    /**
     * The root node of the tree, represented as a final instance of {@link Node}.
     * It defines the top-most element of the tree structure.
     * <p>
     * This variable is immutable once initialized, and it serves as the entry point
     * for accessing the entire tree hierarchy.
     *
     * @param <X> the type of value stored in the nodes of the tree.
     */
    final Node<X> root;
}
