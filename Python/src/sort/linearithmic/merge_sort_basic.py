"""
Merge sort without the optimizations, ported from
sort/linearithmic/MergeSortBasic.java.

This is merge sort as it is first taught: sort each half, copy the range into an
auxiliary list, and merge back. MergeSort adds the no-copy and insurance options
on top; this is what they are measured against.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.elementary.insertion_sort import InsertionSort
from src.sort.generic.has_additional_memory import HasAdditionalMemory
from src.sort.generic.sort_exception import SortException
from src.sort.generic.sort_with_helper import SortWithHelper

X = TypeVar("X")

DESCRIPTION = "MergeSort"


class MergeSortBasic(SortWithHelper[X], HasAdditionalMemory):
    """
    Merge sort, copying into an auxiliary list at every level.
    """

    def __init__(self, helper) -> None:
        """
        :param helper: the Helper to sort through.
        """
        super().__init__(helper)
        self.insertion_sort = InsertionSort(
            helper.clone("MergeSortBasic: insertion sort", share_instrumenter=True))
        self.aux: list[X] | None = None
        self.array_memory = -1
        self.additional = 0
        self.max_memory = 0

    def sort(self, xs: list[X], make_copy: bool = True) -> list[X]:
        """
        Sort a list, accounting for the auxiliary memory used.

        :param xs: the list to sort.
        :param make_copy: if true, sort a copy and return it.
        :return: the sorted list.
        """
        self.get_helper().init(len(xs))
        self.additional_memory(len(xs))
        result = list(xs) if make_copy else xs
        self.aux = list(xs)
        self.sort_range(result, 0, len(result))
        self.additional_memory(-len(xs))
        return result

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        helper = self.get_helper()
        if to <= from_ + helper.cutoff():
            self.insertion_sort.sort_range(xs, from_, to)
            return
        # NOTE aux is normally allocated by sort, but this is the method the Sort
        # interface requires, so a caller may reach it directly -- and used to get
        # a NullPointerException from the copy_block below when it did.
        if self.aux is None or len(self.aux) < len(xs):
            self.aux = list(xs)
        n = to - from_
        mid = from_ + n // 2
        self.sort_range(xs, from_, mid)
        self.sort_range(xs, mid, to)
        helper.copy_block(xs, from_, self.aux, from_, n)
        self._merge(self.aux, xs, from_, mid, to)

    def _merge(self, aux: list[X], a: list[X], from_: int, mid: int, to: int) -> None:
        """
        Merge the two sorted halves of aux back into a.

        :param aux: the list holding the two sorted halves.
        :param a: the list to merge into.
        :param from_: the index of the first element.
        :param mid: the index at which the second half begins.
        :param to: one past the index of the last element.
        """
        helper = self.get_helper()
        i = from_
        j = mid
        for k in range(from_, to):
            if i >= mid:
                helper.copy_at(aux, j, a, k)
                j += 1
            elif j >= to:
                helper.copy_at(aux, i, a, k)
                i += 1
            elif helper.inverted_at(aux, i, j):
                helper.copy_at(aux, j, a, k)
                j += 1
            else:
                helper.copy_at(aux, i, a, k)
                i += 1

    # ---- HasAdditionalMemory ---------------------------------------------

    def set_array_memory(self, n: int) -> None:
        if self.array_memory == -1:
            self.array_memory = n
            self.additional_memory(n)

    def additional_memory(self, n: int) -> None:
        self.additional += n
        if self.max_memory < self.additional:
            self.max_memory = self.additional

    def get_memory_factor(self) -> float:
        """
        :return: the peak additional memory as a multiple of the size of the list.
        :raises SortException: if the size was never recorded.
        """
        if self.array_memory == -1:
            raise SortException("Array memory has not been set")
        return 1.0 * self.max_memory / self.array_memory
