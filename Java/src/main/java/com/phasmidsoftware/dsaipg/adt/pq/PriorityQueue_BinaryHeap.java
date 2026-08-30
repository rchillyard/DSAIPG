package com.phasmidsoftware.dsaipg.adt.pq;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Priority Queue Data Structure which uses a binary heap.
 * <p/>
 * It is unlimited in capacity, although there is no code to grow it after it has been constructed.
 * It can serve as a minPQ or a maxPQ (define "max" as either false or true, respectively).
 * <p/>
 * It can support the root at index 1 or the root at index 0 variants.
 * <p/>
 * It operates on arbitrary Object types which implies that it requires a Comparator to be passed in.
 * <p/>
 * For all details on usage, please see PriorityQueueTest.java
 * CONSIDER why do we need to implement Iterable?
 *
 * @param <K>
 */
public class PriorityQueue_BinaryHeap<K> implements PriorityQueue<K>, Iterable<K> {

    /**
     * @return true if the current size is zero.
     */
    public boolean isEmpty() {
        return m == 0;
    }

    /**
     * @return the number of elements actually stored in this Priority Queue
     */
    public int size() {
        return m;
    }

    /**
     * Adds a key to the priority queue. If the priority queue is at its capacity,
     * the last element is removed to make space for the new key.
     * CONSIDER we can prevent the new element displacing a higher-priority element but at the cost of an extra comparison.
     * Is that worth it?
     *
     * @param key the element to be inserted into the priority queue
     */
    public void give(K key) {
        if (m == binHeap.length - first) m--;
        binHeap[++m + first - 1] = key;
        swimUp(m + first - 1);
    }

    /**
     * Remove the root element from this Priority Queue and adjust the binary heap accordingly.
     * If max is true, then the result will be the maximum element, else the minimum element.
     * NOTE that this method is sometimes called DelMax (or DelMin).
     *
     * @return If max is true, then the maximum element, otherwise the minimum element.
     * @throws PQException if this priority queue is empty
     */
    public K take() throws PQException {
        if (isEmpty()) throw new PQException("Priority queue is empty");
        return doTake(floyd ? this::snake : this::sink);
    }

    /**
     * Package-private method to remove the root element from the priority queue,
     * reorganizes the heap to maintain the priority queue properties,
     * and applies the provided function to the root index.
     *
     * @param f a consumer function that manipulates the root index to maintain the heap order.
     * @return the root element of the priority queue before reorganization.
     */
    K doTake(Consumer<Integer> f) {
        K result = binHeap[first]; // get the root element (the largest or smallest, according to field max)
        swap(first, m-- + first - 1); // swap the root element with the last element
        f.accept(first); // invoke the function f so that it is ordered again
        binHeap[m + first] = null; // prevent loitering
        return result;
    }

    /**
     * Sink the element at index k down
     */
    void sink(@SuppressWarnings("SameParameterValue") int k) {
        doHeapifyStandard(k);
    }

    /**
     * Special sink method that sinks the element and then swim the element back
     *
     * @param k the starting index of the element in the heap to be adjusted.
     */
    void snake(@SuppressWarnings("SameParameterValue") int k) {
        swimUp(doHeapify(k, (a, b) -> false));
    }

    /**
     * Swim the element at index k up
     */
    void swimUp(int k) {
        int i = k;
        while (i > first && inverted(parent(i), i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    /**
     * Initializes or reconstructs the binary heap to satisfy the heap property for all elements.
     * This method assumes the binary heap is stored in an array-like structure.
     * Starting from the last non-leaf node, it applies the sink operation iteratively
     * to ensure the heap invariant is maintained across all levels.
     * The heap property ensures that each parent node is greater than or less than its children
     * depending on whether the heap is configured as a max-heap or min-heap.
     * The Floyd's heap construction approach might be invoked if it matches the configuration.
     */
    public void heapConstructor() {
        for (int k = parent(m + first); k >= first; k--) sink(k);
    }

    /**
     * Compare the elements at indices i and j.
     * We expect the first index (the smaller one) to be greater than the second, assuming that max is true.
     * In this case, we return false.
     *
     * @param i the lower index, numerically
     * @param j the higher index, numerically
     * @return true if the values are out of order.
     */
    boolean inverted(int i, int j) {
        return (comparator.compare(binHeap[i], binHeap[j]) > 0) ^ max;
    }

    /**
     * Non-mutating iterator over all values of this PriorityQueue_BinaryHeap.
     * NOTE: after the first element, there is no definite ordering of the remaining elements.
     * NOTE: this method is really not truly a method of the PriorityQueue_BinaryHeap API.
     * It is here only for convenience.
     *
     * @return an iterator based on a copy of the underlying array.
     */
    @NotNull
    public Iterator<K> iterator() {
        Collection<K> copy = new ArrayList<>(Arrays.asList(Arrays.copyOf(binHeap, m + first)));
        Iterator<K> result = copy.iterator();
        if (first > 0) result.next(); // strip off the leading null value.
        return result;
    }

    /**
     * Primary constructor that takes the max value, an actual array of elements, and a comparator.
     *
     * @param max        whether or not this is a Maximum Priority Queue as opposed to a Minimum PQ.
     * @param binHeap    a pre-formed array with length sufficient to accommodate all required elements plus any unused array slots.
     * @param first      the index of the root element.
     * @param m          the number of elements in binHeap
     * @param comparator a comparator for the type K
     * @param floyd      true if we use Floyd's trick (aka snake).
     */
    public PriorityQueue_BinaryHeap(boolean max, Object[] binHeap, int first, int m, Comparator<K> comparator, boolean floyd) {
        this.max = max;
        this.first = first;
        this.comparator = comparator;
        this.m = m;
        //noinspection unchecked
        this.binHeap = (K[]) binHeap;
        this.floyd = floyd;
    }

    /**
     * Secondary constructor which takes only the priority queue's maximum capacity and a comparator
     *
     * @param n          the desired maximum capacity.
     * @param first      the index to use for the first (root) element.
     * @param max        whether or not this is a Maximum Priority Queue as opposed to a Minimum PQ.
     * @param comparator a comparator for the type K
     * @param floyd      true if we use Floyd's trick (aka snake).
     */
    public PriorityQueue_BinaryHeap(int n, int first, boolean max, Comparator<K> comparator, boolean floyd) {
        // NOTE that we reserve the first element of the binary heap, so the length must be n+1, not n
        this(max, new Object[n + first], first, 0, comparator, floyd);
    }

    /**
     * Constructs a priority queue with specified capacity, type (max or min), a comparator,
     * and an option to use Floyd's heap construction algorithm.
     *
     * @param n          the desired maximum capacity of the priority queue.
     * @param max        if true, this is a Maximum Priority Queue; if false, it is a Minimum Priority Queue.
     * @param comparator a comparator for the type K to define the priority order.
     * @param floyd      if true, Floyd's heap construction algorithm will be used.
     */
    public PriorityQueue_BinaryHeap(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        // NOTE that we reserve the first element of the binary heap, so the length must be n+1, not n
        this(n, 1, max, comparator, floyd);
    }

    /**
     * Secondary constructor which takes only the priority queue's maximum capacity and a comparator.
     * Floyd is false and first is always 1.
     *
     * @param n          the desired maximum capacity.
     * @param max        whether or not this is a Maximum Priority Queue as opposed to a Minimum PQ.
     * @param comparator a comparator for the type K
     */
    public PriorityQueue_BinaryHeap(int n, boolean max, Comparator<K> comparator) {
        // NOTE that we reserve the first element of the binary heap, so the length must be n+1, not n
        this(n, max, comparator, false);
    }

    /**
     * Secondary constructor which takes only the priority queue's maximum capacity and a comparator.
     * Other parameter values: max = true; first = 0; floyd = true.
     *
     * @param n          the desired maximum capacity.
     * @param comparator a comparator for the type K
     */
    public PriorityQueue_BinaryHeap(int n, Comparator<K> comparator) {
        this(n, 0, true, comparator, true);
    }

    /**
     * Secondary constructor which takes a Collection to be added immediately and a comparator.
     * Other parameter values: n = ks.size; max = true; first = 0; floyd = true.
     * This constructor uses the O(n) heap construction method (sometimes also known as "Floyd's Trick.").
     * This constructor is suitable for use by HeapSort.
     *
     * @param ks         a Collection of K elements.
     * @param comparator a comparator for the type K
     */
    public PriorityQueue_BinaryHeap(Collection<K> ks, Comparator<K> comparator) {
        this(ks.size(), comparator);
        int i = 0;
        for (K k : ks) binHeap[i++] = k;
        m = ks.size();
        heapConstructor();
    }

    /**
     * Adjusts a subtree rooted at index k to ensure it satisfies the heap property.
     * The method reorganizes the binary heap by comparing parent and child nodes,
     * swapping their positions if necessary, until the correct heap order is maintained.
     *
     * @param k the starting index of the element in the heap that needs to be adjusted.
     *          That's to say, the root of the sub-heap.
     * @param p a predicate that determines the heap condition to be satisfied.
     *          It takes two indices (parent and child) and returns true if the parent satisfies the heap property relative to the child.
     *          When the predicate is satisfied, we break out of the loop.
     * @return the final position of the element originally at index k after reorganization.
     */
    private int doHeapify(int k, BiPredicate<Integer, Integer> p) {
        int i = k;
        while (true) {
            int firstChild = firstChild(i);
            if (!(firstChild <= m + first - 1)) break;
            int j = firstChild;
            if (j < m + first - 1 && inverted(j, j + 1)) j++;
            if (p.test(i, j)) break;
            swap(i, j);
            i = j;
        }
        return i;
    }

    /**
     * Adjusts a subtree rooted at index k to ensure it satisfies the heap property.
     * The method reorganizes the binary heap by comparing parent and child nodes,
     * swapping their positions if necessary, until the correct heap order is maintained.
     *
     * @param k the starting index of the element in the heap that needs to be adjusted.
     *          That's to say, the root of the sub-heap.
     * @return the final position of the element originally at index k after reorganization.
     */
    private int doHeapifyStandard(int k) {
        return doHeapify(k, (a, b) -> !inverted(a, b));
    }

    /**
     * Exchange the values at indices i and j
     */
    private void swap(int i, int j) {
        K tmp = binHeap[i];
        binHeap[i] = binHeap[j];
        binHeap[j] = tmp;
    }

    /**
     * Get the index of the parent of the element at index k
     */
    private int parent(int k) {
        return (k + 1 - first) / 2 + first - 1;
    }

    /**
     * Get the index of the first child of the element at index k.
     * The index of the second child will be one greater than the result.
     */
    private int firstChild(int k) {
        return (k + 1 - first) * 2 + first - 1;
    }

    /**
     * Retrieves the element at the specified position in the binary heap without removing it.
     * WARNING: this is primarily for testing -- not recommended for general use.
     *
     * @param k the index of the element to peek at in the binary heap. The index must be within the valid range of elements.
     * @return the element of type K at the specified index in the binary heap.
     */
    public K peek(int k) {
        return binHeap[k];
    }

    /**
     * Gets the max property of this priority queue.
     * WARNING: this is primarily for testing -- not recommended for general use.
     *
     * @return true if this priority queue is configured as a max-heap, false if it is configured as a min-heap.
     */
    public boolean getMax() {
        return max;
    }

    /**
     * Indicates whether this Priority Queue is configured as a Maximum Priority Queue.
     * If true, the Priority Queue will prioritize higher values, making the maximum element
     * the first to be removed. If false, the Priority Queue will act as a Minimum Priority Queue,
     * prioritizing lower values instead.
     */
    private final boolean max;

    /**
     * The index of the root element of the priority queue.
     * This field indicates the position of the root element in the binary heap array.
     * Its value is determined during the construction of the priority queue and is used
     * throughout to maintain the priority queue's structural and logical properties.
     * NOTE that only values 0 and 1 are tested in PriorityQueueTest.java
     */
    private final int first;

    /**
     * A comparator used to define the order of elements in the PriorityQueue_BinaryHeap.
     * It determines the relative priority of two elements of type K.
     * This comparator is passed during the construction of the PriorityQueue_BinaryHeap
     * and is used throughout its operations to maintain the desired heap order.
     */
    private final Comparator<K> comparator;

    /**
     * The binary heap array used to represent the internal structure of the priority queue.
     * This array is structured to maintain the properties of a binary heap,
     * either as a max-heap or a min-heap, depending on the configuration of the priority queue.
     * The first index may not always contain an element, as it depends on the initialization parameters.
     * The array has a capacity determined at the creation of the PriorityQueue_BinaryHeap and
     * may include a single extra space for reorganization purposes.
     */
    private final K[] binHeap;

    /**
     * The current number of elements in the binary heap used by this priority queue.
     * This variable represents the dynamic size of the priority queue, and
     * is incremented or decremented as elements are added or removed.
     */
    private int m;

    /**
     * A boolean flag that indicates whether Floyd's optimization method, known as "Floyd's Trick" or
     * "Floyd's snake method", is enabled or disabled during the execution of the take method.
     * When enabled, this optimization adjusts the binary heap to enhance performance in specific scenarios.
     */
    private final boolean floyd;

}
