"""
Brute-force two-sum, ported from adt/threesum/TwoSumQuadratic.java.
"""

from __future__ import annotations

from src.adt.threesum.pair import Pair
from src.adt.threesum.two_sum import TwoSum


class TwoSumQuadratic(TwoSum):
    """
    Tests every pair, so O(n^2). Assumes nothing about the list.

    NOTE as with ThreeSumCubic, a Pair records its elements in the order they were
    met: an unsorted list gives Pair(3, -3) where a sorted one gives Pair(-3, 3).
    Both are the same solution, but they are not equal.
    """

    def __init__(self, a: list[int]) -> None:
        """
        :param a: the values, in any order.
        """
        self._a = a
        self._length = len(a)

    def get_pairs(self) -> list[Pair]:
        """
        :return: the distinct Pairs summing to zero, in ascending order.
        """
        a, length = self._a, self._length
        pairs = [Pair(a[i], a[j])
                 for i in range(length)
                 for j in range(i + 1, length)
                 if a[i] + a[j] == 0]
        return sorted(set(pairs))
