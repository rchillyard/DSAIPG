"""
Three-way quicksort, ported from sort/linearithmic/QuickSort_3way.java.

Ordinary quicksort puts one element in its final place per partition. This puts
the whole run of elements equal to the pivot there, and recurses only on what is
strictly less and strictly greater. On input with few distinct values that turns
quadratic behaviour into linear -- which is the case ordinary quicksort handles
worst.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.helper.helper import Helper
from src.sort.linearithmic.partition import Partition, Partitioner
from src.sort.linearithmic.quick_sort import QuickSort

X = TypeVar("X")

DESCRIPTION = "QuickSort 3 way"


class Partitioner3Way(Partitioner[X]):
    """
    Dijkstra's three-way partition: less than, equal to, greater than the pivot.
    """

    def __init__(self, helper: Helper[X]) -> None:
        """
        :param helper: the Helper to compare and exchange through.
        """
        self.helper = helper

    def partition(self, partition: Partition[X]) -> list[Partition[X]]:
        """
        Split the range into the elements below the pivot and those above it.
        Those equal to it are left between the two, already in place.

        :param partition: the range to split.
        :return: the two ranges either side of the run of equal elements.
        """
        helper = self.helper
        xs = partition.xs
        from_ = partition.from_
        to = partition.to
        lt = from_
        gt = to - 1
        # NOTE the conditional swap comes FIRST: it may move the pivot, so
        # reading it beforehand would leave a stale value.
        helper.swap_conditional(xs, lt, gt)
        # NOTE read directly, not through helper.get: swap_conditional has
        # already fetched both of these, and counting them again would overstate
        # the hits.
        v = xs[lt]
        i = lt + 1
        x_lt = v
        x_gt = xs[gt]
        while i <= gt:
            xi = helper.get(xs, i)
            if i == lt:
                i += 1
                continue
            cmp = helper.compare(xi, v)
            if cmp < 0:
                helper.swap_vw(x_lt, xi, xs, lt, i)
                lt += 1
                i += 1
                x_lt = helper.get(xs, lt)
            elif cmp > 0:
                helper.swap_vw(xi, x_gt, xs, i, gt)
                gt -= 1
                x_gt = helper.get(xs, gt)
            else:
                i += 1
        return [Partition(xs, partition.from_, lt), Partition(xs, gt + 1, partition.to)]


class QuickSort3Way(QuickSort[X]):
    """
    Quicksort with three-way partitioning.
    """

    def create_partitioner(self) -> Partitioner[X]:
        return Partitioner3Way(self.get_helper())
