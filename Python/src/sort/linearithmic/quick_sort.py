"""
The shared part of every quicksort, ported from
sort/linearithmic/QuickSort.java.

The recursion here knows nothing about how the list is split: it asks its
Partitioner for the ranges still needing work and recurses into them. Everything
that distinguishes classic from three-way from dual-pivot lives in the
Partitioner.

NOTE the Java writes each partitioning loop twice -- once through the Helper and
once with raw comparisons, choosing between them on whether the Helper is
instrumented -- to save the cost of the indirection. There is one of each here.
In Python there is no JIT to inline anything, so the second version would buy
nothing, and counting is already free when the Helper holds an InstrumenterDummy.
It would buy only the bug that the second version had in all five of the Java
sorts: comparing by the natural ordering instead of the Helper's comparator.
"""

from __future__ import annotations

from abc import abstractmethod
from typing import TypeVar

from src.sort.elementary.insertion_sort import InsertionSort
from src.sort.generic.sort_with_helper import SortWithHelper
from src.sort.linearithmic.partition import Partition, Partitioner

X = TypeVar("X")


class QuickSort(SortWithHelper[X]):
    """
    Quicksort, less the partitioning.
    """

    def __init__(self, helper, partitioner: Partitioner[X] | None = None) -> None:
        """
        :param helper: the Helper to sort through.
        :param partitioner: how to split a range; None means ask
                            ``create_partitioner``.
        """
        super().__init__(helper)
        self.insertion_sort = InsertionSort(
            helper.clone("Quicksort: insertion sort", share_instrumenter=True))
        self.partitioner = partitioner if partitioner is not None else self.create_partitioner()

    @abstractmethod
    def create_partitioner(self) -> Partitioner[X]:
        """
        :return: the Partitioner this quicksort splits with.
        """

    def set_partitioner(self, partitioner: Partitioner[X]) -> None:
        """
        :param partitioner: the Partitioner to use instead of the default.
        """
        self.partitioner = partitioner

    def sort_range(self, xs: list[X], from_: int, to: int, depth: int = 0) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        :param depth: the recursion depth, recorded so that the deepest can be
                      reported.
        """
        if self.terminator(xs, from_, to, depth):
            return
        self.get_helper().register_depth(depth)
        for p in self.partitioner.partition(Partition(xs, from_, to)):
            self.sort_range(p.xs, p.from_, p.to, depth + 1)

    def terminator(self, xs: list[X], from_: int, to: int, depth: int) -> bool:
        """
        Deal with a range too small to be worth partitioning.

        :param xs: the list.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        :param depth: the recursion depth.
        :return: true if the range has been dealt with and must not be
                 partitioned.
        """
        helper = self.get_helper()
        n = to - from_
        if n <= 1:
            return True
        if n == 2:
            helper.sort_pair(xs, from_, to)
            return True
        if n == 3:
            helper.sort_trio(xs, from_, to)
            return True
        # NOTE the cutoff is reduced by one so that a configured value of 1
        # switches the mechanism off. Partitioning fewer than three elements
        # makes no sense whatever the cutoff says.
        cutoff = max(helper.cutoff() - 1, 3)
        if n > cutoff:
            return False
        self.insertion_sort.sort_range(xs, from_, to)
        return True

    def get_insertion_sort(self) -> InsertionSort[X]:
        """
        :return: the insertion sort used below the cutoff.
        """
        return self.insertion_sort
