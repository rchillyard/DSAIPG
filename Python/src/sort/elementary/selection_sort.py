"""
Selection sort, ported from sort/elementary/SelectionSort.java.

Selection sort finds the smallest remaining element and puts it in place. It
makes the same number of comparisons whatever the input -- n(n-1)/2 -- but at
most n-1 exchanges, which is the fewest of any of the elementary sorts. That is
the trade it exists to illustrate: comparisons are not the only cost.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.generic.sort_with_helper import SortWithHelper
from src.sort.helper.helper import Helper

X = TypeVar("X")

DESCRIPTION = "Selection sort"


class SelectionSort(SortWithHelper[X]):
    """
    Selection sort.
    """

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        helper = self.get_helper()
        # one lookup per element; maybe a little optimistic in real life.
        helper.increment_lookups(to - from_)
        for i in range(from_, to - 1):
            minimum = self.locate_minimum(xs, i, to, helper)
            # NOTE no point exchanging an element with itself.
            if i != minimum:
                helper.swap(xs, i, minimum)

    def locate_minimum(self, xs: list[X], from_: int, to: int, helper: Helper[X]) -> int:
        """
        Find the smallest element of xs between from_ and to.

        :param xs: the list.
        :param from_: the index of the first element to consider.
        :param to: one past the index of the last.
        :param helper: the Helper.
        :return: the index of the smallest element.
        """
        k = from_
        minimum = helper.get(xs, k)
        for j in range(from_ + 1, to):
            x = helper.get(xs, j)
            if helper.inverted(minimum, x):
                k = j
                minimum = x
        return k
