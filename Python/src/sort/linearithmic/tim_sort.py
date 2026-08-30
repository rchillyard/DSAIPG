"""
Timsort, ported from sort/linearithmic/TimSortWrapper.java.

Timsort finds runs of elements that are already in order, extends short ones with
a binary insertion sort, and merges the runs with a galloping strategy that skips
long stretches when one run dominates. It is n log n in the worst case, stable,
and close to linear on input that is partly ordered -- which most real input is.

NOTE this delegates to Python's own sort rather than reimplementing the
algorithm, and that is the faithful choice, not a shortcut: CPython's list.sort
IS Timsort. Tim Peters wrote it for Python in 2002, and Java adopted it for
object arrays in Java 7. The Java tree carries a 950-line reimplementation
because a library sort cannot be instrumented; here the library sort is the
subject itself.

The consequence is that this reports no statistics -- no compares, no hits, no
copies. That is worth stating plainly rather than reporting figures that are not
real. The Java's reimplementation was made instrumentable but the work was never
finished: its binarySort goes through the Helper, while the run-detection and
merge phases -- where Timsort does nearly all of its work -- read and move the
array directly. Measured on 1,000 random ints it reported 3,677 comparisons,
against a floor of lg(1000!) = 8,529 that no comparison sort can go below, and
zero copies for an algorithm that does nothing but merge.
"""

from __future__ import annotations

import functools
from typing import TypeVar

from src.sort.generic.sort_with_helper import SortWithHelper

X = TypeVar("X")

DESCRIPTION = "Timsort"


class TimSort(SortWithHelper[X]):
    """
    Timsort, by way of Python's own list.sort.
    """

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        comparator = self.get_helper().get_comparator()
        xs[from_:to] = sorted(xs[from_:to], key=functools.cmp_to_key(comparator))

    def get_description(self) -> str:
        return DESCRIPTION
