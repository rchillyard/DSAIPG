/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterableImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A generic implementation of a queue using a singly linked list.
 * The queue follows the FIFO (First In, First Out) principle, where elements are added at the tail (newest)
 * and removed from the head (oldest).
 *
 * @param <Item> the type of elements held in this queue
 */
public class Queue_Elements<Item> implements SizedIterable<Item>, Queue<Item> {

    /**
     * Adds the specified item to the end of the queue. If the queue is empty, the new item becomes both the
     * oldest and the newest. Otherwise, the current newest item's next reference is updated to point to the newly
     * added item, and the newest reference is updated accordingly.
     *
     * @param item the item to be added to the queue
     */
    public void offer(Item item) {
        // TO BE IMPLEMENTED 
                throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
    }

    /**
     * Retrieves and removes the oldest item from the queue.
     * If the queue is empty, returns null. This method also adjusts
     * the references to maintain the proper state of the queue.
     *
     * @return the oldest item in the queue, or null if the queue is empty
     */
    public Item poll() {
        if (isEmpty()) return null;
        else {
            // TO BE IMPLEMENTED 
                        throw new com.phasmidsoftware.dsaipg.util.general.ImplementationMissing();
        }
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue contains no elements, false otherwise.
     */
    public boolean isEmpty() {
        return oldest == null;
    }

    /**
     * Returns an iterator over the elements in this queue in proper sequence.
     * The iterator will iterate through the elements in FIFO order, starting
     * from the oldest element to the newest.
     *
     * @return an iterator over the elements in this queue
     */
    @NotNull
    public Iterator<Item> iterator() {
        return new QueueIterator();
    }

    /**
     * Returns the number of elements in this queue.
     *
     * @return the number of elements in the queue.
     */
    public int size() {
        return SizedIterableImpl.create(this).size();
    }

    /**
     * Removes all elements from the queue.
     * This method repeatedly calls the {@code poll} method until the queue is empty.
     * After calling this method, the queue will contain no elements and its size will be zero.
     */
    public void clear() {
        while (!isEmpty()) poll();
    }

    @Override
    public String toString() {
        return (oldest != null ? "Queue: next: " + oldest + (oldest.next != null ? " and others..." : "") : "empty");
    }

    /**
     * Construct a new (empty) queue.
     */
    public Queue_Elements() {
        oldest = null;
        newest = null;
    }

    /**
     * The QueueIterator class implements the Iterator interface to provide an iterator
     * for traversing elements in a queue. It iterates through the elements in a
     * first-in, first-out (FIFO) order, starting from the oldest element.
     */
    class QueueIterator implements Iterator<Item> {
        /**
         * Determines if the iteration has more elements.
         *
         * @return true if there are more elements to iterate over, false otherwise.
         */
        public boolean hasNext() {
            return next != null;
        }

        /**
         * Retrieves the next element in the iteration and advances the iterator to the subsequent element.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if no more elements are present
         */
        public Item next() {
            Item result = next.item;
            next = next.next;
            return result;
        }

        Element<Item> next = oldest;

    }

    /**
     * A reference to the {@code Element} containing the oldest item in the queue.
     * This represents the first element inserted into the queue that has not yet been removed.
     * The {@code oldest} field is updated during operations such as removing (polling) items from the queue.
     * If the queue is empty, this field will be {@code null}.
     */
    private Element<Item> oldest;

    /**
     * Represents the most recently added element in the queue.
     * This field is a reference to the last {@link Element} in the singly linked list that forms the underlying structure of the queue.
     * It is updated whenever a new element is appended to the queue.
     * If the queue is empty, this field is null.
     * The {@code newest} element is used in conjunction with the {@code oldest} field to enable efficient
     * FIFO (First-In-First-Out) operations on the queue.
     */
    private Element<Item> newest;
}
