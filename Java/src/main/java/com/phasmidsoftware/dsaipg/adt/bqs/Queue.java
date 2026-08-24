/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

/**
 * A Queue represents a First-In-First-Out (FIFO) collection of elements.
 * Elements are added at the newest end and removed from the oldest end.
 * This interface allows basic queue operations such as adding, removing,
 * and checking if the queue is empty. It also provides iteration over elements
 * in FIFO order.
 *
 * @param <Item> the type of elements held in this queue.
 */
public interface Queue<Item> extends Iterable<Item> {

    /**
     * Update this Queue by adding an item on the "newest" end.
     *
     * @param item the item to add
     */
    void offer(Item item);

    /**
     * Update this Queue by taking the oldest item off the queue.
     *
     * @return the item or null if there is no such item.
     */
    Item poll();

    /**
     * @return true if this stack is empty
     */
    boolean isEmpty();
}
