"""
Shell sort, ported from sort/elementary/ShellSort.java.

Shell sort is insertion sort applied repeatedly over decreasing gaps. Sorting
elements h apart first moves them a long way cheaply, so that by the time the gap
reaches 1 -- an ordinary insertion sort -- very few inversions remain. How well
it does depends entirely on the sequence of gaps, which is why the sequence is a
parameter rather than a constant.
"""

from __future__ import annotations

from typing import TypeVar

from src.sort.generic.sort_with_helper import SortWithHelper
from src.sort.helper.helper import Helper

X = TypeVar("X")

DESCRIPTION = "Shell sort in mode "

#: The gap sequence used when none is chosen. 4 is Sedgewick's.
DEFAULT_MODE = 4


class H:
    """
    A sequence of gaps, counting down to 1 and then stopping.

    The modes are:

    1. no gaps at all, so shell sort degenerates to insertion sort;
    2. 2(h+1) - 1, that is 1, 3, 7, 15, ...;
    3. 3h + 1, Knuth's sequence: 1, 4, 13, 40, ...;
    4. Sedgewick's sequence, which is the best known in practice;
    5. products of powers of 2 and 3 -- Pratt's sequence, which gives the best
       known asymptotic bound but uses many more passes.
    """

    def __init__(self, n: int, m: int) -> None:
        """
        :param n: the number of elements to be sorted.
        :param m: which sequence to use, 1 to 5.
        :raises ValueError: if m is not one of the five.
        """
        self.m = m
        self.h = 1
        self.i = 0
        self.started = False
        self.data: list[int] = []
        if m == 1:
            pass
        elif m == 2:
            while self.h <= n // 2:
                self.h = 2 * (self.h + 1) - 1
        elif m == 3:
            while self.h <= n // 3:
                self.h = self.h * 3 + 1
        elif m == 4:
            self.i = 0
            while sedgewick(self.i) < n:
                self.i += 1
            self.i -= 1
            self.h = sedgewick(self.i)
        elif m == 5:
            # every 2^i * 3^j not greater than n, in order
            j = 1
            while j <= n:
                i = j
                while i <= n:
                    self.data.append(i)
                    i *= 2
                j *= 3
            self.data.sort()
            self.i = len(self.data) - 1
            self.h = self.data[self.i]
        else:
            raise ValueError(f"invalid m value: {m}")

    def first(self) -> int:
        """
        :return: the largest gap.
        :raises RuntimeError: if called more than once.
        """
        if self.started:
            raise RuntimeError("cannot call first more than once")
        self.started = True
        return self.h

    def next(self) -> int:
        """
        :return: the next gap down, or 0 when there are no more.
        """
        if not self.started:
            self.started = True
            return self.h
        if self.m == 1:
            return 0
        if self.m == 2:
            self.h = (self.h + 1) // 2 - 1
            return self.h
        if self.m == 3:
            self.h = self.h // 3
            return self.h
        if self.m == 4:
            self.i -= 1
            return sedgewick(self.i)
        if self.m == 5:
            self.i -= 1
            return 0 if self.i < 0 else self.data[self.i]
        raise ValueError(f"invalid m value: {self.m}")


def sedgewick(k: int) -> int:
    """
    :param k: which term is wanted.
    :return: the kth term of Sedgewick's gap sequence, or 0 for k below zero.
    """
    if k < 0:
        return 0
    if k % 2 == 0:
        return 9 * (2 ** k - 2 ** (k // 2)) + 1
    return 8 * 2 ** k - 6 * 2 ** ((k + 1) // 2) + 1


class ShellSort(SortWithHelper[X]):
    """
    Shell sort.
    """

    def __init__(self, helper: Helper[X], m: int = DEFAULT_MODE) -> None:
        """
        :param helper: the Helper to sort through.
        :param m: which gap sequence to use, 1 to 5.
        """
        super().__init__(helper)
        self.m = m

    def get_description(self) -> str:
        return DESCRIPTION + str(self.m)

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to, with an insertion sort at each gap in turn.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        h = H(to - from_, self.m)
        gap = h.first()
        while gap > 0:
            self._h_sort(gap, xs, from_, to)
            gap = h.next()

    def _h_sort(self, h: int, xs: list[X], from_: int, to: int) -> None:
        """
        Insertion-sort the elements that are h apart.

        NOTE the Java has two versions of this, choosing between them on whether
        the Helper is instrumented; they differ only in that this one carries
        values along instead of re-reading them, so that the hit count is honest.
        Since carrying them along is also slightly less work, there is one
        version here. Two versions of the same loop is how QuickSort_3way came to
        compare by the natural ordering on one path and by the comparator on the
        other.

        :param h: the gap.
        :param xs: the list.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        """
        helper = self.get_helper()
        for k in range(h):
            if from_ + k >= to:
                break
            a = helper.get(xs, from_ + k)
            for i in range(from_ + h + k, to, h):
                a = _do_insert(xs, h, from_, i, a, helper.get(xs, i), helper)


def _do_insert(xs: list[X], h: int, from_: int, i: int, a: X, b: X, helper: Helper[X]) -> X:
    """
    Move xs[i] down through the elements h below it until it is in place.

    :param xs: the list.
    :param h: the gap.
    :param from_: the index of the first element of the range.
    :param i: the index of the element to insert.
    :param a: the value of xs[i-h], which the caller already holds.
    :param b: the value of xs[i], which the caller already holds.
    :param helper: the Helper.
    :return: the value to pass in as a on the next iteration.
    """
    a_next = b
    j = i
    while True:
        if not helper.swap_conditional_vw(xs, a, j - h, j, b):
            break
        if a_next is b:
            a_next = a
        j -= h
        if j - h < from_:
            break
        a = helper.get(xs, j - h)
    return a_next
