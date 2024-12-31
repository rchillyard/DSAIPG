/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Class to represent a circular queue.
 *
 * @param <Item> the underlying type.
 */
public class Queue_Array<Item> implements Queue<Item> {

    /**
     * Adds the specified item to the queue.
     * The method places the item at the end of the queue, adjusts the internal array index,
     * and ensures there is enough room for future items by possibly resizing the internal array.
     *
     * @param item the item to add to the queue
     */
    public void offer(Item item) {
        items[j++] = item;
        j = j % n;
        ensureRoom();
    }

    /**
     * Retrieves and removes the item at the front of the queue.
     * If the queue is empty, returns {@code null}.
     *
     * @return the item at the front of the queue, or {@code null} if the queue is empty
     */
    public Item poll() {
        if (isEmpty()) return null;
        Item result = items[i++];
        i = i % n;
        return result;
    }

    /**
     * @return true if this stack is empty
     */
    public boolean isEmpty() {
        return i == j;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    public Iterator<Item> iterator() {
        assert items != null; // Should be not-null any time after construction.
        // NOTE: there is no Java-defined array iterator.
        return Arrays.asList(asArray()).iterator();
    }

    /**
     * Returns the number of elements currently stored in the queue.
     * The size is calculated based on the difference between the head (i) and tail (j) indices
     * modulo the capacity (n) of the queue.
     *
     * @return the number of elements in the queue
     */
    public int size() {
        return (n + j - i) % n;
    }

    /**
     * Package-scope method to get the contents of this Queue_Array as an array.
     * <p>
     * NOTE: Internally, Object[] can be cast as an Item[] but it is not valid externally.
     * Hence, the arraycopy.
     * This is a quirk of Java Generics.
     *
     * @return this Queue as an array.
     */
    Item[] asArray() {
        int count = size();
        @SuppressWarnings("unchecked") Item[] items = (Item[]) new Object[count];
        if (straddle() && !isEmpty()) {
            int l = n - i;
            System.arraycopy(this.items, i, items, 0, l);
            System.arraycopy(this.items, 0, items, l, j);
        } else
            System.arraycopy(this.items, i, items, 0, count);
        return items;
    }

    void show() {
        String sb = "Queue_Array: " + "i: " + i + ", " +
                "j: " + j + ", " +
                Arrays.toString(items);
        System.out.println(sb);
    }

    /**
     * Primary constructor of a queue using an array representation.
     *
     * @param items the initial array of items for the queue
     * @param i     the starting index of the queue
     * @param j     the ending index representing the next available position in the queue
     */
    public Queue_Array(Item[] items, int i, int j) {
        this.items = items;
        this.i = i;
        this.j = j;
        this.n = items.length;
    }

    /**
     * Secondary constructor of a new Queue_Array with a specified initial capacity.
     *
     * @param n the initial capacity of the queue
     *          (must be a non-negative integer, the number of elements the queue can initially store)
     */
    public Queue_Array(int n) {
        //noinspection unchecked
        this((Item[]) new Object[n], 0, 0);
    }

    /**
     * Resizes the given array to the specified new size and returns the new array.
     * The contents of the original array are copied into the new array up to the size of the original array.
     *
     * @param ts  the original array to be resized
     * @param n   the new size for the array, must be greater than or equal to the length of the original array
     * @param <T> the type of elements in the array
     * @return a new array of type {@code T} with the specified size, containing the elements from the original array
     */
    private static <T> T[] growArray(T[] ts, int n) {
        assert (n >= ts.length);
        @SuppressWarnings("unchecked") T[] result = (T[]) new Object[n];
        System.arraycopy(ts, 0, result, 0, ts.length);
        return result;
    }

    /**
     * Ensures there is sufficient capacity in the queue's internal array for additional elements.
     * <p>
     * If the internal array is full (when the head and tail indices coincide),
     * this method doubles the size of the array, copying existing elements to the expanded array,
     * and adjusts internal indices to maintain the proper queue structure.
     * <p>
     * The method relies on the invariant that it is called only after an "offer" operation,
     * ensuring that the queue is not empty when invoked.
     */
    private void ensureRoom() {
        // When this method is called, the queue cannot be empty because we just completed an "offer."
        if (i == j) {
            items = growArray(items, n * 2);
            System.arraycopy(items, 0, items, n, i);
            j += n;
            n = n * 2;
        }
    }

    /**
     * Determines if the indices of the queue "straddle" the end of the underlying array.
     * Specifically, this method returns true if the tail index (j) is less than or equal to the head index (i),
     * which indicates that the queue elements are wrapped around the end of the array.
     *
     * @return true if the queue elements straddle the array boundaries, false otherwise.
     */
    private boolean straddle() {
        return j <= i;
    }

    private Item[] items;
    private int i;
    private int j;
    private int n;
}