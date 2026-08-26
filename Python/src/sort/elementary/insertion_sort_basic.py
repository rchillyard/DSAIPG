"""
Insertion sort at its simplest, ported from
sort/elementary/InsertionSortBasic.java.

This one uses no Helper and counts nothing: it exists to show the algorithm by
itself, with the inner loop left as an exercise.
"""

from __future__ import annotations

from collections.abc import Callable
from typing import Generic, TypeVar

from src.sort.helper.helper import natural_comparison

S = TypeVar("S")


class InsertionSortBasic(Generic[S]):
    """
    Insertion sort, ordering by a comparison function.
    """

    def __init__(self, comparator: Callable[[S, S], int]) -> None:
        """
        :param comparator: the comparison function.
        """
        self.comparator = comparator

    @staticmethod
    def create() -> InsertionSortBasic:
        """
        :return: an InsertionSortBasic using the natural ordering of its
                 elements.
        """
        return InsertionSortBasic(natural_comparison)

    def sort(self, a: list[S], from_: int = 0, to: int | None = None) -> None:
        """
        Sort a in place, between from_ and to.

        :param a: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort, defaulting to
                   the end of the list.
        """
        if to is None:
            to = len(a)
        for i in range(from_ + 1, to):
            self.insert(a, from_, i)

    def insert(self, a: list[S], from_: int, i: int) -> None:
        """
        Move the element at index i down until it is in its place, given that
        a[from_:i] is already sorted.

        :param a: the list.
        :param from_: the index of the first element of the sorted part.
        :param i: the index of the element to insert.
        """
        # TO BE IMPLEMENTED : implement inner loop of insertion sort using comparator
        raise NotImplementedError("TO BE IMPLEMENTED")

    def _swap(self, a: list[S], j: int, i: int) -> None:
        """
        Exchange a[i] and a[j].

        :param a: the list.
        :param j: one index.
        :param i: the other index.
        """
        a[j], a[i] = a[i], a[j]
