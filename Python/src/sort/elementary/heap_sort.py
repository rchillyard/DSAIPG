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
        Sort xs between from_ and to.

        NOTE the heap occupies xs[from_:to], so heap index k is xs[from_ + k].
        Only that range is touched; the rest of the list is left alone.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        n = to - from_
        if xs is None or n <= 1:
            return
        # construction phase
        self._build_max_heap(xs, from_, n)
        # sort-down phase
        helper = self.get_helper()
        for i in range(n - 1, 0, -1):
            helper.swap(xs, from_, from_ + i)
            self._heapify(xs, from_, i, 0)

    def _build_max_heap(self, xs: list[X], from_: int, n: int) -> None:
        """
        Rearrange the heap so that every parent is at least as large as its
        children.

        :param xs: the list.
        :param from_: the index at which the heap begins.
        :param n: the number of elements in the heap.
        """
        for i in range(n // 2, -1, -1):
            self._heapify(xs, from_, n, i)

    def _heapify(self, xs: list[X], from_: int, heap_size: int, index: int) -> None:
        """
        Restore the heap property at index, assuming it holds below.

        :param xs: the list.
        :param from_: the index at which the heap begins.
        :param heap_size: the number of elements still in the heap.
        :param index: the heap index to sift down from.
        """
        helper = self.get_helper()
        left = index * 2 + 1
        right = index * 2 + 2
        largest = index
        if left < heap_size and helper.compare_at(xs, from_ + largest, from_ + left) < 0:
            largest = left
        if right < heap_size and helper.compare_at(xs, from_ + largest, from_ + right) < 0:
            largest = right
        if index != largest:
            helper.swap(xs, from_ + index, from_ + largest)
            self._heapify(xs, from_, heap_size, largest)
