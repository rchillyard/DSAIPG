"""
Two-sum by calipers, ported from adt/threesum/TwoSumWithCalipers.java.
"""

from __future__ import annotations

from collections.abc import Callable

from src.adt.threesum.pair import Pair
from src.adt.threesum.two_sum import TwoSum


def calipers(a: list[int], function: Callable[[Pair], int]) -> list[Pair]:
    """
    Close a pair of calipers on a sorted list, keeping the pairs which fit.

    One index starts at each end and they move towards each other. The function
    decides which one moves: a positive value means the pair is too big, so the
    upper index comes down; a negative value means it is too small, so the lower
    index goes up; zero means the pair is wanted. Every index is visited at most
    once, so this is O(n).

    Because the function decides what "too big" means, this is not specific to
    summing to zero -- it is the general two-pointer sweep over a sorted list,
    and Pair.sum is only the particular question TwoSumWithCalipers asks.

    NOTE this is a module-level function rather than a static method, as the
    Java has it: a class holding nothing but a static method earns its keep in
    Java and not in Python.

    :param a: a sorted list of values.
    :param function: given a Pair, returns zero to keep it, a positive value to
                     lower the upper index, a negative value to raise the lower.
    :return: the Pairs for which function returned zero, in ascending order.
    """
    # TO BE IMPLEMENTED : implement get_pairs
    raise NotImplementedError("TO BE IMPLEMENTED")


class TwoSumWithCalipers(TwoSum):
    """
    Finds the pairs summing to zero in O(n), given a sorted list.

    This is the pay-off from sorting: TwoSumQuadratic tests every pair, while
    this makes a single pass. The sort costs O(n log n), so it wins as soon as
    the list is more than trivially small -- and if the list arrives sorted, as
    it does from Source, it is free.
    """

    def __init__(self, xs: list[int]) -> None:
        """
        :param xs: a sorted list of values.
        """
        self._xs = xs

    def get_pairs(self) -> list[Pair]:
        """
        NOTE the Java sorts its result list before filling it, which does nothing.
        It gets away with it because calipers already returns the pairs in
        ascending order -- the lower index only ever rises. This sorts afterwards,
        as every sibling class does, which is the same answer arrived at honestly.

        :return: the distinct Pairs summing to zero, in ascending order.
        """
        return sorted(set(calipers(self._xs, Pair.sum)))
