"""
A range of a list, and the thing that splits one, ported from
sort/linearithmic/Partition.java and Partitioner.java.

Quicksort is entirely defined by how it partitions. Separating that out is what
lets the same recursion drive classic, three-way, dual-pivot and exponential
quicksort without knowing which it has.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Generic, TypeVar

X = TypeVar("X")


class Partition(Generic[X]):
    """
    The range xs[from_:to] of a list.
    """

    def __init__(self, xs: list[X], from_: int, to: int) -> None:
        """
        :param xs: the list.
        :param from_: the index of the first element.
        :param to: the index one past the last element.
        """
        self.xs = xs
        self.from_ = from_
        self.to = to

    def is_sorted(self, helper) -> bool:
        """
        :param helper: the Helper, which supplies the ordering.
        :return: true if this range is sorted.
        """
        return helper.is_sorted(self.xs, self.from_, self.to)

    def __str__(self) -> str:
        if self.from_ >= self.to:
            return f"Empty Partition at {{{self.to - 1}}}"
        return (f"Partition{{from={self.from_}, to={self.to} "
                f"elements: {self.xs[self.from_:self.to]}}}")


class Partitioner(ABC, Generic[X]):
    """
    Something which splits a Partition into smaller ones.
    """

    @abstractmethod
    def partition(self, partition: Partition[X]) -> list[Partition[X]]:
        """
        Rearrange the elements of the given range and say how it has been split.

        :param partition: the range to split.
        :return: the ranges still needing to be sorted. Anything not covered by
                 one of them is already in its final place.
        """
