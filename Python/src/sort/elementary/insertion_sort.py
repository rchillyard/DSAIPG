"""
Insertion sort, ported from sort/elementary/InsertionSort.java and
InsertionSortOpt.java.

Insertion sort takes each element in turn and moves it down until it is in
place. Its cost is one swap per inversion, no more and no less, which is what
makes it the natural way to count inversions and what makes it fast on input
that is nearly sorted already.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.generic.sort_with_helper import SortWithHelper
from src.sort.helper.helper import Helper

X = TypeVar("X")

DESCRIPTION = "Insertion sort"
DESCRIPTION_OPT = "Insertion sort optimized"


class InsertionSort(SortWithHelper[X]):
    """
    Insertion sort, exchanging adjacent elements.
    """

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        helper = self.get_helper()
        if to <= from_ + 1:
            return
        a = helper.lookup(helper.get(xs, from_))
        for i in range(from_ + 1, to):
            a = _do_insert(xs, from_, i, a, helper.get(xs, i), helper)


class InsertionSortOpt(InsertionSort[X]):
    """
    Insertion sort using binary search and half-exchanges.

    Instead of exchanging an element with each of its neighbours in turn, this
    finds where the element belongs and moves the block above it up one place.
    The number of comparisons falls to log n per element; the number of elements
    moved does not change.
    """

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        helper = self.get_helper()
        # NOTE optimistic: it assumes one lookup per element.
        helper.increment_lookups(to - from_)
        for i in range(from_ + 1, to):
            helper.swap_into_sorted(xs, from_, i)


def _do_insert(xs: list[X], from_: int, i: int, a: X, b: X, helper: Helper[X]) -> X:
    """
    Move xs[i] down into its place within the sorted run below it.

    :param xs: the list.
    :param from_: the first index of the sorted run.
    :param i: the index of the element to insert.
    :param a: the value of xs[i-1], which the caller already holds.
    :param b: the value of xs[i], which the caller already holds.
    :param helper: the Helper.
    :return: the value now at index i, ready to be passed in as a next time.
    """
    a_next = helper.lookup(b)  # NOTE this represents an optimistic view of the lookups.
    j = i
    while True:
        if not helper.swap_conditional_vw(xs, a, j - 1, j, b):
            break
        # NOTE identity, not equality, as in the Java. Only the lookup count
        # depends on it, and both languages cache small integers, so both are
        # imprecise here in the same way.
        if a_next is b:
            a_next = a
        j -= 1
        if j == from_:
            break
        a = helper.get(xs, j - 1)
    return a_next
