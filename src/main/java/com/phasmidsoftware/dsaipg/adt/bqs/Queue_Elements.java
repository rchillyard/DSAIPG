/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import com.phasmidsoftware.dsaipg.util.SizedIterable;
import com.phasmidsoftware.dsaipg.util.SizedIterableImpl;
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
     * Construct a new (empty) queue.
     */
    public Queue_Elements() {
        oldest = null;
        newest = null;
    }

    /**
     * Adds the specified item to the end of the queue. If the queue is empty, the new item becomes both the
     * oldest and the newest. Otherwise, the current newest item's next reference is updated to point to the newly
     * added item, and the newest reference is updated accordingly.
     *
     * @param item the item to be added to the queue
     */
    public void offer(Item item) {
        // TO BE IMPLEMENTED 

        // END SOLUTION
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
             return null;
            // END SOLUTION
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

    // This Element essentially begins a LinkedList of Elements which correspond
    // to the elements that can be taken from the queue (head points to the oldest element).
    // However, it is built in manner that requires a pointer to the newest element.
    private Element<Item> oldest;

    // This Element always points to the newest (tail-most) element in the LinkedList referenced by oldest.
    private Element<Item> newest;

    @Override
    public String toString() {
        return (oldest != null ? "Queue: next: " + oldest + (oldest.next != null ? " and others..." : "") : "empty");
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
}