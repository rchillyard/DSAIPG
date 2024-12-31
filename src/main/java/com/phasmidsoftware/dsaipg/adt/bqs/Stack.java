/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

/**
 * Interface representing a generic Stack (LIFO - Last In, First Out) data structure.
 * This Stack supports basic operations such as adding an item (push), removing the top item (pop),
 * peeking at the top item without removing it, and checking if the stack is empty.
 *
 * @param <Item> the type of elements stored in this Stack
 */
public interface Stack<Item> extends Iterable<Item> {

    /**
     * Update this Stack by adding an item on the top.
     *
     * @param item the item to add
     */
    void push(Item item);

    /**
     * Update this Stack by taking the top item of this Stack.
     *
     * @return the item.
     * @throws BQSException if this Stack is empty.
     */
    Item pop() throws BQSException;

    /**
     * Take a peek at the item on top of this Stack.
     *
     * @return the item.
     */
    Item peek();

    /**
     * @return true if this stack is empty
     */
    boolean isEmpty();
}