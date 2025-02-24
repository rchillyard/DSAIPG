package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.Comparator;

public class FourAryHeap<K> extends PriorityQueue<K> {

    // Constructor for 4-ary heap

    public FourAryHeap(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        super(n, max, comparator, floyd);  // Remove the '1' parameter since we'll use default
    }


    //Get the index of the parent in a 4-ary heap
    @Override
    protected int parent(int k) {
        return (k - 1) / 4 + 1;
    }


      //Get the index of the first child in a 4-ary heap

    @Override
    protected int firstChild(int k) {
        return 4 * k + 1;
    }

    @Override
    protected void sink(int k) {
        int n = size();
        while (firstChild(k) <= n) {
            // Find the largest/smallest (depending on max/min heap) among the 4 children
            int j = firstChild(k);

            // Compare with second child if it exists
            if (j + 1 <= n && unordered(j, j + 1)) j++;

            // Compare with third child if it exists
            if (j + 1 <= n && unordered(j, j + 1)) j++;

            // Compare with fourth child if it exists
            if (j + 1 <= n && unordered(j, j + 1)) j++;

            // If k is not out of order with largest/smallest child, we're done
            if (!unordered(k, j)) break;

            // Otherwise, swap and continue
            swap(k, j);
            k = j;
        }
    }


    @Override
    protected void snake(int k) {
        // First sink the element
        int finalPosition = k;
        int n = size();

        while (firstChild(finalPosition) <= n) {
            int j = firstChild(finalPosition);
            int largest = j;

            // Find the largest/smallest among all existing children
            for (int i = 1; i < 4 && j + i <= n; i++) {
                if (unordered(largest, j + i)) {
                    largest = j + i;
                }
            }

            if (!unordered(finalPosition, largest)) break;

            swap(finalPosition, largest);
            finalPosition = largest;
        }

        // Then swim it up if necessary
        swimUp(finalPosition);
    }


    @Override
    protected void swimUp(int k) {
        while (k > 1 && unordered(parent(k), k)) {
            int parent = parent(k);
            swap(k, parent);
            k = parent;
        }
    }
}