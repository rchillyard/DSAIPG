package com.phasmidsoftware.dsaipg.graphs.traversal;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The {@code Bag} class represents a bag (or multiset) of
 * generic items.
 * It supports insertion and iterating over the
 * items in arbitrary order.
 * <p>
 * This implementation uses a singly linked list with a static nested class Node.
 * Whereas the textbook that uses a non-static nested class.
 * The <em>add</em>, <em>isEmpty</em>, and <em>size</em> operations
 * take constant time. Iteration takes time proportional to the number of items.
 * <p>
 * For additional documentation, see <a href="https://algs4.cs.princeton.edu/13stacks">Section 1.3</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @param <Item> the generic type of an item in this bag
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class Bag<Item> implements Iterable<Item> {

    /**
     * Returns true if this bag is empty.
     *
     * @return {@code true} if this bag is empty;
     * {@code false} otherwise
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Returns the number of items in this bag.
     *
     * @return the number of items in this bag
     */
    public int size() {
        return n;
    }

    /**
     * Adds the item to this bag.
     *
     * @param item the item to add to this bag
     */
    public void add(Item item) {
        Node<Item> oldfirst = first;
        first = new Node<>();
        first.item = item;
        first.next = oldfirst;
        n++;
    }

    /**
     * Returns an iterator that iterates over the items in this bag in arbitrary order.
     *
     * @return an iterator that iterates over the items in this bag in arbitrary order
     */
    public Iterator<Item> iterator() {
        return new LinkedIterator(first);
    }

    /**
     * Initializes an empty bag.
     */
    public Bag() {
        first = null;
        n = 0;
    }

    private Node<Item> first;    // beginning of bag
    private int n;               // number of elements in bag

    // helper linked list class
    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
    }

    /**
     * The {@code LinkedIterator} class represents an iterator for traversing
     * elements in a singly linked list. It is implemented as a static nested class
     * within the {@code Bag} class. The iteration proceeds in the natural order
     * in which the elements are linked.
     * <p>
     * This class does not implement the {@code remove} operation and will throw
     * an {@code UnsupportedOperationException} if attempted.
     */
    private class LinkedIterator implements Iterator<Item> {
        /**
         * Constructs a {@code LinkedIterator} using the provided starting node of a singly linked list.
         *
         * @param first the first node of the linked list to initiate iteration
         */
        public LinkedIterator(Node<Item> first) {
            current = first;
        }

        /**
         * Determines if there are more elements to iterate over in the collection.
         *
         * @return true if there is at least one more element to iterate over, false otherwise
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Removes the current element from the underlying collection.
         * This method is not supported by this iterator and will always throw
         * an {@code UnsupportedOperationException}.
         *
         * @throws UnsupportedOperationException always thrown to indicate that the
         *                                       remove operation is not supported
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the next item in the iteration and advances the iterator.
         *
         * @return the next item in the iteration
         * @throws NoSuchElementException if there are no more items to iterate
         */
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }

        private Node<Item> current;
    }
}

/*Copyright © 2000–2019, Robert Sedgewick and Kevin Wayne.*/

