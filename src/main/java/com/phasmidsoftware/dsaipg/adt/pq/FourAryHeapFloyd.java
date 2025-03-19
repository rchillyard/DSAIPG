package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.Comparator;

public class FourAryHeapFloyd<K> extends FourAryHeap<K> {


     // Constructor for 4-ary heap with Floyd's trick

    public FourAryHeapFloyd(int n, boolean max, Comparator<K> comparator) {
        super(n, max, comparator, true);  // Always use floyd's trick
    }


      //Override to ensure Floyd's trick is always used

    @Override
    public K take() throws PQException {
        if (isEmpty()) throw new PQException("Priority queue is empty");
        return doTake(this::snake);
    }
}