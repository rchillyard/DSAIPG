package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;


public class FourAryPriorityQueue<K> implements Iterable<K> {

    public FourAryPriorityQueue(boolean max, Object[] fourAryHeap, int first, int last, Comparator<K> comparator, boolean floyd) {
        this.max = max;
        this.first = first;
        this.comparator = comparator;
        this.last = last;
        this.fourAryHeap = (K[]) fourAryHeap;
        this.floyd = floyd;
    }

    public FourAryPriorityQueue(int n, int first, boolean max, Comparator<K> comparator, boolean floyd) {
        this(max, new Object[4 * n + first], first, 0, comparator, floyd);
    }

    public FourAryPriorityQueue(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        this(n, 1, max, comparator, floyd);
    }

    public FourAryPriorityQueue(int n, boolean max, Comparator<K> comparator) {
        this(n, 1, max, comparator, false);
    }

    public FourAryPriorityQueue(int n, Comparator<K> comparator) {
        this(n, 1, true, comparator, true);
    }

    public boolean isEmpty() {
        return last == 0;
    }

    public int size() {
        return last;
    }

    public void give(K key) {
        if (last >= fourAryHeap.length - first) last--;
        fourAryHeap[++last + first - 1] = key;
        swimUp(last + first - 1);
    }

    public K take() throws PQException {
        if (isEmpty()) throw new PQException("Priority queue is empty");
        if (floyd) return doTake(this::snake);
        else return doTake(this::sink);
    }

    K doTake(Consumer<Integer> f) {
        K result = fourAryHeap[first];
        swap(first, last-- + first - 1);
        f.accept(first);
        fourAryHeap[last + first] = null;
        return result;
    }

    void sink(int k) {
        doHeapify(k, (a, b) -> !unordered(a, b));
    }

    void snake(int k) {
        swimUp(doHeapify(k, (a, b) -> !unordered(a, b)));
    }

    void swimUp(int k) {
        int i = k;
        while (i > first && unordered(parent(i), i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    boolean unordered(int i, int j) {
        return (comparator.compare(fourAryHeap[i], fourAryHeap[j]) > 0) ^ max;
    }

    public Iterator<K> iterator() {
        Collection<K> copy = new ArrayList<>(Arrays.asList(Arrays.copyOf(fourAryHeap, last + first)));
        Iterator<K> result = copy.iterator();
        if (first > 0) result.next();
        return result;
    }

    private int doHeapify(int k, BiPredicate<Integer, Integer> p) {
        int i = k;
        while (true) {
            int firstChild = firstChild(i);
            if (firstChild > last + first - 1) break;

            int extremeChild = firstChild;
            for (int j = 1; j < 4; j++) {
                int currentChild = firstChild + j;
                if (currentChild > last + first - 1) break;
                if (unordered(extremeChild, currentChild)) extremeChild = currentChild;
            }

            if (p.test(i, extremeChild)) break;
            swap(i, extremeChild);
            i = extremeChild;
        }
        return i;
    }

    private void swap(int i, int j) {
        K tmp = fourAryHeap[i];
        fourAryHeap[i] = fourAryHeap[j];
        fourAryHeap[j] = tmp;
    }

    private int parent(int k) {
        return (k - first - 1) / 4 + first;
    }

    private int firstChild(int k) {
        return 4 * (k - first) + first + 1;
    }

    public static void main(String[] args) throws PQException {
        Comparator<Integer> comp = Comparator.naturalOrder();
        FourAryPriorityQueue<Integer> pq = new FourAryPriorityQueue<>(10, true, comp);

        pq.give(3);
        pq.give(1);
        pq.give(4);
        pq.give(5);
        pq.give(2);

        while (!pq.isEmpty()) System.out.println(pq.take());
    }

    private final boolean max;
    private final int first;
    private final Comparator<K> comparator;
    private final K[] fourAryHeap;
    private int last;
    private final boolean floyd;
}

