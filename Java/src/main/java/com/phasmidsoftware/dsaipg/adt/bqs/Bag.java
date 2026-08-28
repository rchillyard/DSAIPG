/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;

/**
 * A Bag is a collection that allows multiple occurrences of items.
 * Also known as a multi-set.
 * It extends the SizedIterable interface, enabling iteration over its elements and providing size-related functionalities.
 * The items in the Bag have no guaranteed order when iterated over.
 *
 * @param <Item> the type of elements stored in this Bag.
 */
public interface Bag<Item> extends SizedIterable<Item> {

    /**
     * Update this Bag by adding item.
     * No guarantee is made regarding the ordering of Items in the iterator
     *
     * @param item the item to add
     */
    void add(Item item);

    /**
     * @return true if this bag is empty
     */
    boolean isEmpty();

    /**
     * @param item an item which we want to find in this Bag.
     * @return true if the item has at least one instance in this Bag.
     */
    boolean contains(Item item);

    /**
     * @param item an item for whose multiplicity we desire.
     * @return the multiplicity of item, that's to say the number of instances of item there are in this Bag.
     */
    int multiplicity(Item item);

    /**
     * Method to get this Bag as an array.
     *
     * @return this Bag as an array of Objects.
     */
    Object[] asArray();

    /**
     * Method to get this Bag as an array of the same component type as the one
     * supplied.
     * <p>
     * The array carries the type information that {@link #asArray()} cannot: Item
     * is erased, so there is nothing at runtime to say what kind of array to
     * allocate, and the caller supplying one settles it. This is exactly
     * {@link java.util.Collection#toArray(Object[])}, and for exactly the same
     * reason — call it with {@code new Item[0]} to get a genuinely typed array
     * back.
     *
     * @param a   an array of the desired component type. It is filled and returned
     *            if it is big enough; otherwise a new one of the same type is
     *            allocated.
     * @param <T> the component type.
     * @return this Bag as an array of T.
     */
    <T> T[] asArray(T[] a);

    /**
     * Empty out this Bag
     */
    void clear();
}
