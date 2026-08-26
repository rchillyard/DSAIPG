"""
Bubble sort, ported from sort/elementary/BubbleSort.java.

Bubble sort makes repeated passes, exchanging adjacent elements that are out of
order, and stops as soon as a pass makes no exchange. That early exit is what
makes it linear on input that is already sorted -- the one thing it is good at.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.generic.sort_with_helper import SortWithHelper
from src.sort.helper.helper import Helper

X = TypeVar("X")

DESCRIPTION = "Bubble sort"


class BubbleSort(SortWithHelper[X]):
    """
    Bubble sort, with an early exit.
    """

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        helper = self.get_helper()
        for j in range(to, from_, -1):
            if self._optimized_inner_loop_success(xs, helper, from_, j):
                break

    def _optimized_inner_loop_success(self, xs: list[X], helper: Helper[X],
                                      from_: int, j: int) -> bool:
        """
        Make one pass, exchanging adjacent elements that are out of order.

        The straightforward way to write this is::

            for i in range(from_ + 1, j):
                swapped |= helper.swap_stable_conditional(xs, i)

        but that re-reads an element the pass has already seen. Carrying v and w
        along means each element is read once, which is what makes the hit count
        honest.

        :param xs: the list.
        :param helper: the Helper.
        :param from_: the index of the first element of the pass.
        :param j: one past the index of the last element of the pass.
        :return: true if the pass made no exchange, so the list is sorted and
                 the caller can stop.
        """
        if from_ >= j - 1:
            return False
        swapped = False
        i = from_
        v = helper.get(xs, i)
        w = helper.get(xs, i + 1)
        while True:
            b = helper.swap_conditional_vw(xs, v, i, i + 1, w)
            swapped = swapped or b
            i += 1
            if i == j - 1:
                break
            if not b:
                v = w
            w = helper.get(xs, i + 1)
        return not swapped
