"""
Quicksort partitioning about the middle element, ported from
sort/linearithmic/QuickSort_Exp.java.

Classic quicksort takes the first element as its pivot, which is the worst
possible choice for input that is already sorted -- every partition then peels
off one element and the sort is quadratic. Taking the middle element instead
costs one exchange and removes that case.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.helper.helper import Helper
from src.sort.linearithmic.partition import Partition, Partitioner
from src.sort.linearithmic.quick_sort import QuickSort

X = TypeVar("X")

DESCRIPTION = "QuickSort exp"


class PartitionerExp(Partitioner[X]):
    """
    Hoare partitioning about the middle element.
    """

    def __init__(self, helper: Helper[X]) -> None:
        """
        :param helper: the Helper to compare and exchange through.
        """
        self.helper = helper

    def partition(self, partition: Partition[X]) -> list[Partition[X]]:
        """
        Split the range about its middle element.

        NOTE the scans read through the Helper, so every element they touch is
        counted. The Java read the list directly and charged a single hit for the
        whole partition, which made this sort report 72% of the accesses it made;
        corrected in both trees.

        :param partition: the range to split.
        :return: the two ranges either side of the pivot.
        """
        helper = self.helper
        xs = partition.xs
        from_ = partition.from_
        to = partition.to
        hi = to - 1
        mid = from_ + (to - from_) // 2
        helper.swap(xs, from_, mid)
        v = xs[from_]
        i = from_
        j = to
        helper.increment_hits(1)  # for the pivot, read above
        while True:
            while i < hi:
                i += 1
                if not helper.not_inverted(helper.get(xs, i), v):
                    break
            while j > from_:
                j -= 1
                if not helper.not_inverted(v, helper.get(xs, j)):
                    break
            if i >= j:
                break
            helper.swap(xs, i, j)
        if from_ != j:
            helper.swap(xs, from_, j)
        return [Partition(xs, from_, j), Partition(xs, j + 1, to)]


class QuickSortExp(QuickSort[X]):
    """
    Quicksort partitioning about the middle element.
    """

    def create_partitioner(self) -> Partitioner[X]:
        return PartitionerExp(self.get_helper())
