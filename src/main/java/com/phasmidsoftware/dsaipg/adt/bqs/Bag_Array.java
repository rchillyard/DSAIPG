/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * An implementation of the Bag interface using an array as the underlying storage.
 * This class provides basic functionality such as adding elements, checking for containment,
 * and retrieving the size of the bag. The bag does not maintain any specific order of elements.
 * Internal capacity automatically grows when required to accommodate more items.
 *
 * @param <Item> the type of elements contained in the bag
 */
public class Bag_Array<Item> implements Bag<Item> {

    /**
     * Primary constructor that takes an explicit Random source (which will be passed to any UnorderedIterator).
     * NOTE: random is mutable and therefore unpredictable.
     *
     * @param random a random source.
     */
    public Bag_Array(Random random) {
        //noinspection unchecked
        grow((Item[]) new Object[0], 32);
        this.random = random;
    }

    /**
     * Default no-argument constructor for Bag_Array.
     * Constructs a Bag_Array instance using a new Random instance as the source of entropy.
     * The constructor initializes the bag's internal storage with an initial capacity.
     */
    public Bag_Array() {
        this(new Random());
    }

    /**
     * Adds the specified item to the bag.
     * If the internal storage is full, it will be expanded to accommodate the new item.
     *
     * @param item the item to be added to the bag
     */
    public void add(Item item) {
        assert items != null;
        if (full())
            grow(items, 2 * capacity());
        items[count++] = item;
    }

    /**
     * Determines if the bag currently contains no items.
     *
     * @return true if the bag is empty (i.e., the count of items is zero), otherwise false.
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Retrieves the number of items currently in the bag.
     *
     * @return the total count of items in the bag.
     */
    public int size() {
        return count;
    }

    /**
     * Clears all items from the bag.
     * Resets the count of items contained in the bag to zero, effectively emptying it.
     */
    public void clear() {
        count = 0;
    }

    /**
     * Checks if the specified item is present in the items array.
     *
     * @param item the item to check for presence in the bag
     * @return true if the item is found in the bag, otherwise false
     */
    public boolean contains(Item item) {
        for (Item i : items) {
            if (i != null && i.equals(item))
                return true;
        }
        return false;
    }

    /**
     * Method to calculate the multiplicity of a given item in the bag.
     *
     * @param item the item for which the multiplicity is to be determined
     * @return the number of instances of the specified item in the bag
     */
    public int multiplicity(Item item) {
        int result = 0;
        if (isEmpty()) return 0;
        for (Item i : items) {
            if (i != null && i.equals(item))
                result++;
        }
        return result;
    }

    /**
     * Method to generate a randomly ordered iterator on this Bag.
     *
     * @return an Iterator on Item.
     */
    public Iterator<Item> iterator() {
        assert items != null; // Should be not-null any time after construction.
        return new UnorderedIterator<>(asArray(), random);
    }

    /**
     * Method to get this Bag as an array.
     * <p>
     * Internally, Object[] can be cast as an Item[] but it is not valid externally.
     * Hence, the arraycopy.
     * This is a quirk of Java Generics.
     *
     * @return this Bag as an array.
     */
    public Item[] asArray() {
        @SuppressWarnings("unchecked") Item[] items = (Item[]) new Object[count];
        System.arraycopy(this.items, 0, items, 0, count);
        return items;
    }

    @Override
    public String toString() {
        return "Bag_Array{" +
                "items=" + Arrays.toString(asArray()) +
                ", count=" + count +
                '}';
    }

    /**
     * Expands the Bag_Array's internal storage to accommodate a new size,
     * replacing the current items with the contents of the newly grown array.
     *
     * @param source the source array whose elements are to be copied into the new array
     * @param size   the size of the new expanded array
     */
    private void grow(Item[] source, int size) {
        items = growFrom(source, size);
    }

    /**
     * Retrieves the current capacity of the internal storage for the bag.
     * The capacity is defined as the size of the array used to store items.
     * It represents the maximum number of items the bag can hold without resizing.
     *
     * @return the capacity of the internal storage.
     */
    private int capacity() {
        assert items != null; // Should be not-null any time after construction.
        return items.length;
    }

    /**
     * Determines if the bag is full by checking if the number of items in the bag equals its capacity.
     *
     * @return true if the bag is full, otherwise false.
     */
    private boolean full() {
        return size() == capacity();
    }

    /**
     * This fairly primitive grow method takes a T array called "from",
     * instantiates a new array of the given size,
     * copies all the elements of from into the start of the resulting array,
     * then returns the result.
     *
     * @param from the source array
     * @param size the size of the new array
     */
    private static <T> T[] growFrom(T[] from, int size) {
        // TO BE IMPLEMENTED  grow array and copy
         return null;
        // END SOLUTION
    }

    private final Random random;

    private Item[] items = null;
    private int count = 0;
}