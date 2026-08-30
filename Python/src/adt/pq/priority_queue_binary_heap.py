from collections.abc import Callable, Iterable, Iterator
from typing import TypeVar

from .pq_exception import PQException
from .priority_queue import PriorityQueue

K = TypeVar('K')

class PriorityQueueBinaryHeap(PriorityQueue[K]):
    """
    Priority Queue Data Structure which uses a binary heap.
    
    It is unlimited in capacity.
    It can serve as a minPQ or a maxPQ (define "max" as either False or True, respectively).
    It can support the root at index 1 or the root at index 0 variants.
    It operates on arbitrary Object types which implies that it requires a Comparator to be passed in,
    or it relies on the natural ordering of elements.
    """

    def __init__(self, 
                 max_priority: bool = True, 
                 comparator: Callable[[K, K], int] | None = None, 
                 floyd: bool = False, 
                 first: int = 0, 
                 initial_data: Iterable[K] | None = None):
        """
        Constructs a PriorityQueueBinaryHeap.

        Args:
            max_priority: whether or not this is a Maximum Priority Queue (default True).
            comparator: a comparator function that takes two arguments and returns 
                        >0 if first > second, <0 if first < second, 0 if equal.
                        If None, uses natural ordering (operator.lt/gt).
            floyd: True if we use Floyd's trick (aka snake) (default False).
            first: the index of the root element (default 0).
            initial_data: optional initial data to populate the queue.
        """
        self._max = max_priority
        self._first = first
        self._floyd = floyd
        self._comparator = comparator
        self._heap: list[K | None] = [None] * first  # Initialize with 'first' number of None placeholders
        
        if initial_data:
            for item in initial_data:
                self._heap.append(item)
            self.heap_constructor()

    def is_empty(self) -> bool:
        return self.size() == 0

    def size(self) -> int:
        return len(self._heap) - self._first

    def give(self, key: K) -> None:
        self._heap.append(key)
        self._swim_up(len(self._heap) - 1)

    def take(self) -> K:
        if self.is_empty():
            raise PQException("Priority queue is empty")
        
        return self._do_take(self._snake if self._floyd else self._sink)

    def _do_take(self, f: Callable[[int], None]) -> K:
        result = self._heap[self._first]
        last_index = len(self._heap) - 1
        self._swap(self._first, last_index)
        self._heap.pop()  # Remove the last element (which was the root)
        
        if not self.is_empty():
            f(self._first)
            
        return result

    def heap_constructor(self) -> None:
        m = self.size()
        for k in range(self._parent(m + self._first - 1), self._first - 1, -1):
            self._sink(k)

    def peek(self, k: int) -> K | None:
        if 0 <= k < len(self._heap):
            return self._heap[k]
        return None

    def _sink(self, k: int) -> None:
        self._do_heapify_standard(k)

    def _snake(self, k: int) -> None:
        self._swim_up(self._do_heapify(k, lambda a, b: False))

    def _swim_up(self, k: int) -> None:
        i = k
        while i > self._first and self._inverted(self._parent(i), i):
            self._swap(i, self._parent(i))
            i = self._parent(i)

    def _do_heapify(self, k: int, p: Callable[[int, int], bool]) -> int:
        i = k
        while True:
            first_child = self._first_child(i)
            if not (first_child < len(self._heap)):
                break
            j = first_child
            if j < len(self._heap) - 1 and self._inverted(j, j + 1):
                j += 1
            if p(i, j):
                break
            self._swap(i, j)
            i = j
        return i

    def _do_heapify_standard(self, k: int) -> int:
        return self._do_heapify(k, lambda a, b: not self._inverted(a, b))

    def _swap(self, i: int, j: int) -> None:
        self._heap[i], self._heap[j] = self._heap[j], self._heap[i]

    def _parent(self, k: int) -> int:
        return (k + 1 - self._first) // 2 + self._first - 1

    def _first_child(self, k: int) -> int:
        return (k + 1 - self._first) * 2 + self._first - 1

    def _inverted(self, i: int, j: int) -> bool:
        if self._comparator:
            cmp = self._comparator(self._heap[i], self._heap[j])
            return (cmp > 0) ^ self._max
        else:
            # Natural ordering
            # If max is True, we want heap[i] > heap[j] (normal heap property for max heap is parent > child)
            # Wait, 'inverted' means OUT OF ORDER.
            # For Max Heap: parent should be > child. If parent < child, it is inverted.
            # For Min Heap: parent should be < child. If parent > child, it is inverted.
            
            # Let's look at Java: (comparator.compare(binHeap[i], binHeap[j]) > 0) ^ max
            # If max=true: (cmp > 0) ^ true  => (cmp > 0) == false => cmp <= 0.
            # So if parent <= child (cmp <= 0), inverted is true? 
            # Wait. Java: return (cmp > 0) ^ max;
            # If max=true (MaxPQ), we want parent > child.
            # If parent > child (cmp > 0), then true ^ true = false. Not inverted. Correct.
            # If parent < child (cmp < 0), then false ^ true = true. Inverted. Correct.
            
            # Python natural ordering:
            # If max=True:
            #   if heap[i] > heap[j]: False (Correct order)
            #   if heap[i] < heap[j]: True (Inverted)
            #   So: heap[i] < heap[j]
            
            # If max=False (MinPQ):
            #   if heap[i] < heap[j]: False (Correct order)
            #   if heap[i] > heap[j]: True (Inverted)
            #   So: heap[i] > heap[j]
            
            if self._max:
                return self._heap[i] < self._heap[j]
            else:
                return self._heap[i] > self._heap[j]

    def __iter__(self) -> Iterator[K]:
        # Return an iterator over a copy of the valid elements
        # Note: The order is not guaranteed to be sorted, just the heap array order
        return iter(self._heap[self._first:])

    def __len__(self) -> int:
        return self.size()
