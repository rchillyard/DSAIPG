package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.Comparator;

/**
 * FourAryHeap extends the PriorityQueue to implement a 4-ary heap.
 * For a 4-ary heap (with root at index 1):
 * - Parent of node at index k: (k - 2) / 4 + 1.
 * - First child of node at index k: 4*(k - 1) + 2.
 *
 * Can be used with floyd = false (basic) or floyd = true (with Floyd's trick).
 *
 * @param <K> the type of elements held in this heap
 */
public class FourAryHeap<K> extends PriorityQueue<K> {

    public FourAryHeap(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        super(n, 1, max, comparator, floyd);
    }

    @Override
    protected int parent(int k) {
        return (k - 2) / 4 + 1;
    }

    @Override
    protected int firstChild(int k) {
        return 4 * (k - 1) + 2;
    }
}