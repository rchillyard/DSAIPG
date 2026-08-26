"""
Heap sort, ported from sort/elementary/HeapSort.java.

Heap sort builds a max-heap and then repeatedly moves the largest remaining
element to the end. It sorts in place with no additional memory and is n log n
in the worst case, which quicksort is not -- but it touches memory in a scattered
way, which is why it is usually slower in practice than a sort with worse
worst-case bounds.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.generic.sort_with_helper import SortWithHelper

X = TypeVar("X")

DESCRIPTION = "Heap Sort"


class HeapSort(SortWithHelper[X]):
    """
    Heap sort.
    """

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs.

        NOTE from_ and to are ignored: this sorts the whole list, as the Java
        does. Passing a sub-range silently sorts everything, so HeapSort does not
        honour the Sort contract the way the other sorts here do. Recorded rather
        than fixed, because making it work over a range means offsetting every
        index in the heap arithmetic, which is a change worth making
        deliberately.

        :param xs: the list to sort.
        :param from_: ignored.
        :param to: ignored.
        """
        if xs is None or len(xs) <= 1:
            return
        # construction phase
        self._build_max_heap(xs)
        # sort-down phase
        helper = self.get_helper()
        for i in range(len(xs) - 1, 0, -1):
            helper.swap(xs, 0, i)
            self._heapify(xs, i, 0)

    def _build_max_heap(self, xs: list[X]) -> None:
        """
        Rearrange xs so that every parent is at least as large as its children.

        :param xs: the list.
        """
        for i in range(len(xs) // 2, -1, -1):
            self._heapify(xs, len(xs), i)

    def _heapify(self, xs: list[X], heap_size: int, index: int) -> None:
        """
        Restore the heap property at index, assuming it holds below.

        :param xs: the list.
        :param heap_size: the number of elements still in the heap.
        :param index: the index to sift down from.
        """
        helper = self.get_helper()
        left = index * 2 + 1
        right = index * 2 + 2
        largest = index
        if left < heap_size and helper.compare_at(xs, largest, left) < 0:
            largest = left
        if right < heap_size and helper.compare_at(xs, largest, right) < 0:
            largest = right
        if index != largest:
            helper.swap(xs, index, largest)
            self._heapify(xs, heap_size, largest)
