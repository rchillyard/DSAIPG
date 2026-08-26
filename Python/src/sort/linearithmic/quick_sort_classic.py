"""
Classic quicksort, ported from sort/linearithmic/QuickSort_Classic.java.

The partition is Hoare's: take the first element as the pivot, scan inwards from
both ends, and exchange each out-of-place pair that the scans meet. The pivot
then goes where the scans crossed, and everything below it is smaller and
everything above larger -- so the pivot is in its final place and never moves
again.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.helper.helper import Helper
from src.sort.linearithmic.partition import Partition, Partitioner
from src.sort.linearithmic.quick_sort import QuickSort

X = TypeVar("X")

DESCRIPTION = "QuickSort classic"


class PartitionerBasic(Partitioner[X]):
    """
    Hoare partitioning about the first element.
    """

    def __init__(self, helper: Helper[X]) -> None:
        """
        :param helper: the Helper to compare and exchange through.
        """
        self.helper = helper

    def partition(self, partition: Partition[X]) -> list[Partition[X]]:
        """
        Split the range about its first element.

        :param partition: the range to split.
        :return: the two ranges either side of the pivot, which is now in place.
        """
        helper = self.helper
        ys = partition.xs
        from_ = partition.from_
        hi = partition.to - 1
        v = helper.get(ys, from_)
        i = from_
        j = partition.to
        x = y = None
        while True:
            # scan up for something not less than the pivot
            while i < hi:
                i += 1
                # NOTE one hit and one lookup per step, as in the Java, which
                # goes to the trouble of an inner class to record exactly this.
                x = helper.lookup(helper.get(ys, i))
                if not helper.not_inverted(x, v):
                    break
            # scan down for something not greater than it
            while j > from_:
                j -= 1
                y = helper.lookup(helper.get(ys, j))
                if not helper.not_inverted(v, y):
                    break
            if i >= j:
                break
            # both values are already in hand, so neither is read again
            helper.swap_vw(x, y, ys, i, j)
        # put the pivot where the scans crossed
        if from_ != j:
            helper.swap_v(v, ys, from_, j)
        return [Partition(ys, from_, j), Partition(ys, j + 1, partition.to)]


class QuickSortClassic(QuickSort[X]):
    """
    Quicksort using Hoare partitioning.
    """

    def create_partitioner(self) -> Partitioner[X]:
        return PartitionerBasic(self.get_helper())
