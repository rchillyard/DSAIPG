"""
Dual-pivot quicksort, ported from
sort/linearithmic/QuickSort_DualPivot.java.

Two pivots split the range into three parts rather than two, so the recursion is
shallower and each pass does more. This is the algorithm Java's own
Arrays.sort uses for primitives.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.generic.sort_exception import SortException
from src.sort.helper.helper import Helper
from src.sort.linearithmic.partition import Partition, Partitioner
from src.sort.linearithmic.quick_sort import QuickSort

X = TypeVar("X")

DESCRIPTION = "QuickSort dual pivot"


class PartitionerDualPivot(Partitioner[X]):
    """
    Partition about two pivots, into the elements below the first, those between
    the two, and those above the second.
    """

    def __init__(self, helper: Helper[X]) -> None:
        """
        :param helper: the Helper to compare and exchange through.
        """
        self.helper = helper

    def partition(self, partition: Partition[X]) -> list[Partition[X]]:
        """
        Split the range about two pivots.

        :param partition: the range to split.
        :return: the three ranges between and either side of the pivots.
        :raises SortException: if the range holds fewer than three elements.
        """
        helper = self.helper
        xs = partition.xs
        n = partition.to - partition.from_
        if n < 3:
            raise SortException("cannot use DualPivot partitioning when size is less than 3")
        p1 = partition.from_
        p2 = partition.to - 1
        # ensure the smaller pivot is at p1
        helper.swap_conditional(xs, p1, p2)
        lt = p1 + 1
        gt = p2 - 1
        i = lt
        v1 = xs[p1]
        v2 = xs[p2]
        x_lt = helper.get(xs, lt)
        x_gt = helper.get(xs, gt)
        xi = x_lt
        while i <= gt:
            if helper.compare(helper.lookup(xi), v1) < 0:
                helper.swap_vw(x_lt, xi, xs, lt, i)
                lt += 1
                i += 1
                xi = helper.get(xs, i)
                x_lt = helper.get(xs, lt) if lt != i else xi
            elif helper.compare(xi, v2) > 0:
                helper.swap_vw(xi, x_gt, xs, i, gt)
                gt -= 1
                if i == lt:
                    x_lt = x_gt
                xi = x_gt
                x_gt = helper.get(xs, gt)
            else:
                i += 1
                xi = helper.get(xs, i)
        # NOTE these restore the two pivots to their final places, and either can
        # be a no-op: lt is still p1 + 1 if nothing was less than v1, and gt is
        # still p2 - 1 if nothing was greater than v2, which happens readily when
        # the input has many duplicates. helper.swap asserts i != j, because
        # counting a self-swap would corrupt the instrumentation, so skip the
        # swap rather than ask for one.
        lt -= 1
        if lt != p1:
            helper.swap(xs, p1, lt)
        gt += 1
        if gt != p2:
            helper.swap(xs, p2, gt)
        return [
            Partition(xs, partition.from_, lt),
            Partition(xs, lt + 1, gt),
            Partition(xs, gt + 1, partition.to),
        ]


class QuickSortDualPivot(QuickSort[X]):
    """
    Quicksort with two pivots.
    """

    def create_partitioner(self) -> Partitioner[X]:
        return PartitionerDualPivot(self.get_helper())
