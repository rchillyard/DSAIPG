package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A simple Fibonacci Heap implementation functioning as a min-heap.
 * Supports basic operations: give (insert) and take (remove min).
 *
 * @param <K> the type of elements held in this heap
 */
public class FibonacciHeap<K> implements PriorityQueueInterface<K>, Iterable<K> {

    private Comparator<K> comparator;
    private Node<K> min;
    private int n;

    private static class Node<K> {
        K key;
        int degree;
        Node<K> parent;
        Node<K> child;
        Node<K> left;
        Node<K> right;
        boolean mark;

        Node(K key) {
            this.key = key;
            degree = 0;
            parent = null;
            child = null;
            left = this;
            right = this;
            mark = false;
        }
    }

    public FibonacciHeap(int capacity, boolean max, Comparator<K> comparator) {
        // Capacity and max are ignored; we implement a min-heap.
        this.comparator = comparator;
        min = null;
        n = 0;
    }

    @Override
    public boolean isEmpty() {
        return min == null;
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public void give(K key) {
        Node<K> node = new Node<>(key);
        if (min == null) {
            min = node;
        } else {
            // Insert node into the root list.
            node.left = min;
            node.right = min.right;
            min.right.left = node;
            min.right = node;
            if (comparator.compare(key, min.key) < 0) {
                min = node;
            }
        }
        n++;
    }

    @Override
    public K take() throws PQException {
        if (isEmpty())
            throw new PQException("Fibonacci Heap is empty");
        Node<K> z = min;
        if (z != null) {
            if (z.child != null) {
                Node<K> x = z.child;
                do {
                    x.parent = null;
                    x = x.right;
                } while (x != z.child);
                // Merge z's child list into the root list.
                Node<K> zLeft = z.left;
                Node<K> childLeft = z.child.left;
                z.left = childLeft;
                childLeft.right = z;
                z.child.left = zLeft;
                zLeft.right = z.child;
            }
            // Remove z from the root list.
            z.left.right = z.right;
            z.right.left = z.left;
            if (z == z.right) {
                min = null;
            } else {
                min = z.right;
                consolidate();
            }
            n--;
        }
        return z.key;
    }

    private void consolidate() {
        int D = ((int) Math.floor(Math.log(n) / Math.log(2))) + 1;
        Node<K>[] A = new Node[D];
        for (int i = 0; i < D; i++) {
            A[i] = null;
        }
        int rootCount = 0;
        if (min != null) {
            Node<K> x = min;
            do {
                rootCount++;
                x = x.right;
            } while (x != min);
        }
        Node<K>[] rootNodes = new Node[rootCount];
        Node<K> x = min;
        for (int i = 0; i < rootCount; i++) {
            rootNodes[i] = x;
            x = x.right;
        }
        for (Node<K> w : rootNodes) {
            x = w;
            int d = x.degree;
            while (A[d] != null) {
                Node<K> y = A[d];
                if (comparator.compare(x.key, y.key) > 0) {
                    Node<K> temp = x;
                    x = y;
                    y = temp;
                }
                link(y, x);
                A[d] = null;
                d++;
            }
            A[d] = x;
        }
        min = null;
        for (int i = 0; i < D; i++) {
            if (A[i] != null) {
                if (min == null) {
                    min = A[i];
                    min.left = min;
                    min.right = min;
                } else {
                    A[i].left = min;
                    A[i].right = min.right;
                    min.right.left = A[i];
                    min.right = A[i];
                    if (comparator.compare(A[i].key, min.key) < 0) {
                        min = A[i];
                    }
                }
            }
        }
    }

    private void link(Node<K> y, Node<K> x) {
        y.left.right = y.right;
        y.right.left = y.left;
        y.parent = x;
        if (x.child == null) {
            x.child = y;
            y.left = y;
            y.right = y;
        } else {
            y.left = x.child;
            y.right = x.child.right;
            x.child.right.left = y;
            x.child.right = y;
        }
        x.degree++;
        y.mark = false;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("Iterator not implemented for FibonacciHeap");
    }
}