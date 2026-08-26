"""
Introsort, ported from sort/linearithmic/IntroSort.java.

Quicksort is fast on average but quadratic in the worst case; heap sort is n log n
always but slower in practice. Introsort runs dual-pivot quicksort and watches how
deep the recursion goes: if it passes 2 lg n -- which only happens when the pivots
have been going badly -- it abandons quicksort for that sub-range and heap-sorts
it instead. So it keeps quicksort's speed and heap sort's guarantee.

It is what a standard library reaches for: this is essentially what C++'s
std::sort does.
"""

from __future__ import annotations

import math
from typing import TypeVar

from src.sort.helper.helper import Helper
from src.sort.linearithmic.quick_sort_dual_pivot import QuickSortDualPivot

X = TypeVar("X")

DESCRIPTION = "Intro sort"

#: Below this many elements, insertion sort wins outright.
SIZE_THRESHOLD = 16


def floor_lg(a: int) -> int:
    """
    :param a: a positive number.
    :return: the floor of its base-2 logarithm, or 0 for anything below 1.
    """
    return int(math.floor(math.log2(a))) if a > 0 else 0


class IntroSort(QuickSortDualPivot[X]):
    """
    Dual-pivot quicksort which falls back to heap sort when it recurses too deep.
    """

    def __init__(self, helper, partitioner=None) -> None:
        """
        :param helper: the Helper to sort through.
        :param partitioner: how to split a range; None means dual-pivot.
        """
        super().__init__(helper, partitioner)
        self.depth_threshold = None

    def sort(self, xs: list[X], make_copy: bool = True) -> list[X]:
        """
        Sort a list, setting the depth at which to give up on quicksort.

        :param xs: the list to sort.
        :param make_copy: if true, sort a copy and return it.
        :return: the sorted list.
        """
        self.get_helper().init(len(xs))
        self.depth_threshold = 2 * floor_lg(len(xs))
        result = list(xs) if make_copy else xs
        self.sort_range(result, 0, len(result), 0)
        return result

    def sort_range(self, xs: list[X], from_: int, to: int, depth: int | None = None) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        :param depth: the recursion depth; None means this is a fresh top-level
                      call, so the threshold is set from the size of the range.
        """
        if depth is None:
            self.depth_threshold = 2 * floor_lg(to - from_)
            depth = 0
        super().sort_range(xs, from_, to, depth)

    def terminator(self, xs: list[X], from_: int, to: int, depth: int) -> bool:
        """
        Deal with a range that is small enough, or that has recursed too deep.

        :param xs: the list.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        :param depth: the recursion depth.
        :return: true if the range has been dealt with.
        """
        if to - from_ <= SIZE_THRESHOLD:
            if to > from_ + 1:
                self.get_insertion_sort().sort_range(xs, from_, to)
            return True
        if self.depth_threshold is not None and depth >= self.depth_threshold:
            self._heap_sort(xs, from_, to)
            return True
        return False

    def _heap_sort(self, a: list[X], from_: int, to: int) -> None:
        """
        Heap-sort the range, which guarantees n log n however bad the pivots were.

        :param a: the list.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        """
        helper = self.get_helper()
        n = to - from_
        for i in range(n // 2, 0, -1):
            self._down_heap(a, i, n, from_, helper)
        for i in range(n, 1, -1):
            helper.swap(a, from_, from_ + i - 1)
            self._down_heap(a, 1, i - 1, from_, helper)

    def _down_heap(self, a: list[X], i: int, n: int, from_: int, helper: Helper[X]) -> None:
        """
        Sift the element at heap position i down to where it belongs.

        NOTE heap positions here are one-based, so heap position i is a[from_ +
        i - 1].

        :param a: the list.
        :param i: the heap position to sift down from.
        :param n: the number of elements in the heap.
        :param from_: the index at which the heap begins.
        :param helper: the Helper.
        """
        d = a[from_ + i - 1]
        while i <= n // 2:
            child = 2 * i
            if child < n and helper.compare_at(a, from_ + child - 1, from_ + child) < 0:
                child += 1
            if helper.compare(d, a[from_ + child - 1]) >= 0:
                break
            helper.increment_fixes(1)
            a[from_ + i - 1] = a[from_ + child - 1]
            i = child
        a[from_ + i - 1] = d
