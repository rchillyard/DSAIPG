package com.phasmidsoftware.dsaipg.misc;

import com.phasmidsoftware.dsaipg.adt.bqs.Queue;
import com.phasmidsoftware.dsaipg.adt.bqs.Queue_Elements;
import com.phasmidsoftware.dsaipg.adt.bqs.Stack;
import com.phasmidsoftware.dsaipg.adt.bqs.Stack_LinkedList;

import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * The RecursionAndIteration class demonstrates recursive and iterative methods
 * for traversing tree-like and linked-list structures. It provides implementations
 * for depth-first traversal of binary trees and traversal of singly linked lists,
 * utilizing Java's Consumer functional interface for handling elements during traversal.
 */
public class RecursionAndIteration {

    /**
     * A Node for a binary structure that encapsulates references to its left and right child nodes.
     * This class is static and immutable, meaning its left and right nodes cannot be altered once the
     * object is created.
     */
    static class Node {

        /**
         * Performs a depth-first traversal of a binary tree starting from a given node.
         * Allows customizable operations at three stages of traversal: pre-order, in-order, and post-order,
         * by applying the provided consumers on the node at the respective traversal stage.
         *
         * @param node         the starting node for the depth-first traversal; if null, the method will return immediately
         * @param preConsumer  a Consumer applied to each node before traversing its left subtree (pre-order stage)
         * @param inConsumer   a Consumer applied to each node after traversing its left subtree but before its right subtree (in-order stage)
         * @param postConsumer a Consumer applied to each node after traversing both its subtrees (post-order stage)
         */
        void dfs(Node node, Consumer<Node> preConsumer, Consumer<Node> inConsumer, Consumer<Node> postConsumer) {
            if (node == null) return;
            preConsumer.accept(node);
            dfs(node.left, preConsumer, inConsumer, postConsumer);
            inConsumer.accept(node);
            dfs(node.right, preConsumer, inConsumer, postConsumer);
            postConsumer.accept(node);
        }

        /**
         * Constructs a new Node with references to the specified left and right child nodes.
         *
         * @param left  the left child node; may be null if there is no left child
         * @param right the right child node; may be null if there is no right child
         */
        Node(Node left, Node right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Represents the left child node of the current node in a binary tree structure.
         * This is an immutable reference, meaning once assigned during the construction
         * of the parent node, it cannot be modified.
         * <p>
         * If the node has no left child, this reference will be null.
         */
        private final Node left;
        /**
         * Represents the right child node in a binary tree structure.
         * This field references another Node object or is null if there is no right child.
         * It is immutable, meaning its value is fixed once assigned during the Node's construction.
         */
        private final Node right;
    }

    /**
     * This class represents a generic element in a linked list structure.
     *
     * @param <T> the type of the value contained in this element
     */
    static class Element<T> {

        /**
         * Traverses the elements of the linked list, applying the specified actions before and after visiting each element.
         *
         * @param preConsumer  the action to be performed on each element before traversing its next element
         * @param postConsumer the action to be performed on each element after traversing its next element
         */
        void traverse(Consumer<T> preConsumer, Consumer<T> postConsumer) {
            preConsumer.accept(t);
            if (next != null) next.traverse(preConsumer, postConsumer);
            postConsumer.accept(t);
        }

        /**
         * Iterates over elements in a linked list, applying the provided pre-processing and post-processing consumers
         * to each element in the list.
         *
         * @param preConsumer a {@code Consumer<T>} that is applied to each element before any additional logic
         * @param postConsumer a {@code Consumer<T>} that is applied to each element after the preConsumer logic
         */
        private void iterate(Consumer<T> preConsumer, Consumer<T> postConsumer) {
            Element<T> element = this;
            while (element != null) {
                preConsumer.accept(element.t);
                postConsumer.accept(element.t);
                element = element.next;
            }
        }

        /**
         * Constructs an Element with a value and a reference to the next element in the list.
         *
         * @param t    the value of this element
         * @param next the next element in the linked list, or null if this is the last element
         */
        Element(T t, Element<T> next) {
            this.t = t;
            this.next = next;
        }

        /**
         * Represents the value associated with this element of the linked list.
         * It is of type T, which is the generic type parameter of the containing class.
         * This field is immutable and is initialized at the time of the element's creation.
         */
        private final T t;
        /**
         * A reference to the next element in the linked list.
         * This represents the link connecting the current Element<T> instance
         * to the subsequent Element<T> in the list.
         * If this is the last element in the list, the value of this field will be null.
         */
        private final Element<T> next;
    }

    public static void main(String[] args) {
//				Element<Integer> list = getList123();
        doMain(getListFromInputStream(System.in));
    }

    private static Element<Integer> getListFromInputStream(InputStream inputStream) {
        Stack<Integer> inputStack = new Stack_LinkedList<>();
        Scanner scanner = new Scanner(inputStream);
        int n = scanner.nextInt();
        for (int i = n; i > 0; i--) inputStack.push(scanner.nextInt());
        Element<Integer> list = null;
        for (Integer x : inputStack) list = new Element<>(x, list);
        return list;
    }

    private static Element<Integer> getList123() {
        return new Element<>(1, new Element<>(2, new Element<>(3, null)));
    }

    static void doMain(Element<Integer> list) {
        // Recursive solution using traverse
        System.out.println("traverse:");
        Queue_Elements<Integer> preQueue = new Queue_Elements<>();
        Queue<Integer> postQueue = new Queue_Elements<>();
        list.traverse(preQueue::offer, postQueue::offer);
        System.out.println("preQueue:");
        for (Integer x : preQueue) System.out.println(x);
        System.out.println("postQueue:");
        for (Integer x : postQueue) System.out.println(x);
        preQueue.clear();

        // Iterative solution using iterate
        System.out.println("iterate:");
        java.util.Stack<Integer> postStack = new java.util.Stack<>();
        list.iterate(preQueue::offer, postStack::push);
        System.out.println("preQueue:");
        for (Integer x : preQueue) System.out.println(x);
        System.out.println("postStack:");
        for (Integer x : postStack) System.out.println(x);
    }
}
