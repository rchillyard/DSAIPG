"""
The TwoSum abstraction, ported from adt/threesum/TwoSum.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod

from src.adt.threesum.pair import Pair


class TwoSum(ABC):
    """
    Something which can find every distinct pair of values summing to zero.

    As with ThreeSum, the faster implementation is the one which assumes more:
    the calipers need a sorted list, the quadratic one needs nothing.
    """

    @abstractmethod
    def get_pairs(self) -> list[Pair]:
        """
        :return: the distinct Pairs summing to zero, in ascending order.
        """
