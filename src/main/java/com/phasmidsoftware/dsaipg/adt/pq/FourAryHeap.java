package com.phasmidsoftware.dsaipg.adt.pq;
 import java.util.*;
 import java.util.function.BiPredicate;
 import java.util.function.Consumer;
 
 
 public class FourAryHeap<K> implements Iterable<K> {

     public FourAryHeap(boolean max, Object[] heap, int first, int last, Comparator<K> comparator, boolean floyd) {
         this.max = max;
         this.first = first;
         this.comparator = comparator;
         this.last = last;
         //noinspection unchecked
         this.heap = (K[]) heap;
         this.floyd = floyd;
     }

     public FourAryHeap(int n, int first, boolean max, Comparator<K> comparator, boolean floyd) {
         // Reserve 'first' positions; array length is n + first.
         this(max, new Object[n + first], first, 0, comparator, floyd);
     }

     public FourAryHeap(int n, boolean max, Comparator<K> comparator, boolean floyd) {
         this(n, 1, max, comparator, floyd);
     }

     public FourAryHeap(int n, boolean max, Comparator<K> comparator) {
         this(n, max, comparator, false);
     }
 
     public boolean isEmpty() {
         return last == 0;
     }
 
     public int size() {
         return last;
     }
 
     public void give(K key) {
         if (last == heap.length - first)
             last--; // if at capacity, arbitrarily drop the least eligible element.
         heap[++last + first - 1] = key;
         swimUp(last + first - 1);
     }

     public K take() throws PQException {
         if (isEmpty()) throw new PQException("Heap is empty");
         if (floyd) return doTake(this::snake);
         else return doTake(this::sink);
     }
 
     K doTake(Consumer<Integer> f) {
         K result = heap[first];
         swap(first, last-- + first - 1);
         f.accept(first);
         heap[last + first] = null; // prevent loitering
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
         return (comparator.compare(heap[i], heap[j]) > 0) ^ max;
     }

     private int doHeapify(int k, BiPredicate<Integer, Integer> p) {
         int i = k;
         while (firstChild(i) <= last + first - 1) {
             int fc = firstChild(i);
             int bestChild = fc;
             // Check up to 3 additional children (total 4 children)
             for (int j = 1; j < 4; j++) {
                 int child = fc + j;
                 if (child <= last + first - 1 && unordered(bestChild, child)) {
                     bestChild = child;
                 }
             }
             if (p.test(i, bestChild)) break;
             swap(i, bestChild);
             i = bestChild;
         }
         return i;
     }
 
     private void swap(int i, int j) {
         K temp = heap[i];
         heap[i] = heap[j];
         heap[j] = temp;
     }

     private int parent(int k) {
         return (k - first - 1) / 4 + first;
     }
 
     private int firstChild(int k) {
         return 4 * (k - first) + first + 1;
     }
 

     public Iterator<K> iterator() {
         Collection<K> copy = new ArrayList<>(Arrays.asList(Arrays.copyOf(heap, last + first)));
         Iterator<K> it = copy.iterator();
         if (first > 0) it.next(); // Skip the reserved slot.
         return it;
     }
 
     // Fields
     private final boolean max;
     private final int first;
     private final Comparator<K> comparator;
     private final K[] heap;
     private int last; // number of elements in the heap
     private final boolean floyd;
 }
