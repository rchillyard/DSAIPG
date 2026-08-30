package com.phasmidsoftware.dsaipg.adt.bqs;

import java.util.Arrays;
import java.util.List;
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
        // The cast is safe here for the same reason as in Bag_Array.iterator: Item
        // is erased inside this class, so it compiles to nothing.
        @SuppressWarnings("unchecked") List<Item> list = (List<Item>) Arrays.asList(asArray());
        return list.iterator();
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
     * NOTE: Object[], not Item[]. See Bag_Array.asArray for why: Item is erased,
     * so the array really is an Object[] however it is declared, and declaring it
     * Item[] promises a type no caller can actually use.
     *
     * @return this Queue as an array.
     */
    Object[] asArray() {
        int count = size();
        Object[] items = new Object[count];
        if (straddle() && !isEmpty()) {
            int l = n - i;
            System.arraycopy(this.items, i, items, 0, l);
            System.arraycopy(this.items, 0, items, l, j);
        } else
            System.arraycopy(this.items, i, items, 0, count);
        return items;
    }

    /**
     * Displays the current state of the Queue_Array instance by printing a formatted string
     * that includes the values of the fields `i`, `j`, and the contents of the `items` array.
     * This method is useful for debugging purposes to observe the internal state of the queue.
     */
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

    /**
     * Internal storage array for the items in the queue.
     * This array holds the elements of the queue in a circular structure, where the logical start of the queue
     * may not correspond to the first index of the array. The positions are managed through the indices `i` and `j`.
     * The capacity of this array may dynamically increase as new elements are added, maintaining the queue's functionality.
     */
    private Item[] items;

    /**
     * Represents the head index of the queue in the internal array representation.
     * This variable points to the position of the first element in the queue.
     * <p>
     * In the context of a circular queue implementation:
     * - The value of {@code i} is updated when elements are removed (dequeued) from the queue.
     * - It will wrap around to the beginning of the array when exceeding the array's capacity to maintain the circular behavior.
     * - Plays a critical role in computing the current size of the queue as well as determining if the queue is empty or full.
     */
    private int i;

    /**
     * Represents the index of the tail of the queue in the internal array.
     * This variable is used to track the position where the next item will be added.
     * It is incremented (with wrap-around logic using modulo operation) whenever an item is added to the queue.
     * The value of this variable, in conjunction with the head index (`i`), determines the state and size of the queue.
     */
    private int j;

    /**
     * Represents the capacity of the queue in the `Queue_Array` class.
     *
     * This field holds the maximum number of elements that can be stored in the queue's
     * underlying array before requiring a resize operation. The value of `n` is used
     * in conjunction with other fields and methods (e.g., `i`, `j`, and `growArray`)
     * to manage the circular indexing of the queue and to determine its current size.
     */
    private int n;
}
