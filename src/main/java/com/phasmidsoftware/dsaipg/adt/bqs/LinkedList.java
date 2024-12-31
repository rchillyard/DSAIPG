/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

/**
 * Interface to define the behavior of a linked list.
 *
 * @param <Item> the underlying type of the list.
 */
public interface LinkedList<Item> extends Iterable<Item>, ListLike<Item> {

    /**
     * Method to get the head element of this list.
     *
     * @return the head of this list.
     */
    Item getHead();

}