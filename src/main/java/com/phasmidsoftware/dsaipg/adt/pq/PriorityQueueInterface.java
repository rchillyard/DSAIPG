package com.phasmidsoftware.dsaipg.adt.pq;

public interface PriorityQueueInterface<K> {
    boolean isEmpty();
    int size();
    void give(K key);
    K take() throws PQException;
}