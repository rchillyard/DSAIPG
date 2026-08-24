package com.phasmidsoftware.dsaipg.adt.pq;

/**
 * An interface that represents a priority queue structure, allowing efficient
 * addition and extraction of elements based on their priority.
 * The priority queue may support maximum or minimum priority orders depending
 * on its implementation.
 */
public interface PriorityQueue<K> {
    /**
     * Checks if the priority queue is empty.
     *
     * @return true if the priority queue contains no elements, false otherwise.
     */
    boolean isEmpty();

    /**
     * Retrieves the number of elements currently stored in the priority queue.
     *
     * @return the number of elements in the priority queue.
     */
    int size();

    /**
     * Attempts to add the specified element to the priority queue.
     * If the key has low priority, it will be spilled.
     *
     * @param key the element to be added to the priority queue
     */
    void give(K key);

    /**
     * Removes and returns the highest-priority element from the priority queue.
     * If max is false (it's a minimum PQ), then this will result in the smallest item.
     * If the queue is empty, throws a PQException.
     *
     * @return the highest-priority element in the priority queue (assuming getMax() is true).
     * @throws PQException if the priority queue is empty.
     */
    K take() throws PQException;

    /**
     * Constructs or initializes the underlying data structure for the Priority Queue
     * in order to prepare it for efficient insertion and extraction operations.
     * Typically used to build the heap representation of the priority queue based
     * on its current elements.
     * Useful for the heap construction phase of HeapSort.
     */
    void heapConstructor();

    /**
     * Retrieves the element at the specified position in the priority queue without removing it.
     * WARNING: this is primarily for testing -- not recommended for general use.
     *
     * @param k the position index of the element to peek at, where 0 represents the highest priority element.
     * @return the element at the specified position in the priority queue.
     */
    K peek(int k);

    /**
     * Determines if this is a max-Priority Queue.
     * WARNING: this is primarily for testing -- not recommended for general use.
     *
     * @return true if the maximum value is present, false otherwise.
     */
    @SuppressWarnings("unused")
    boolean getMax();
}
