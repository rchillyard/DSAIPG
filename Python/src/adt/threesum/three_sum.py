"""
The ThreeSum abstraction, ported from adt/threesum/ThreeSum.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod

from src.adt.threesum.triple import Triple


class ThreeSum(ABC):
    """
    Something which can find every distinct triple of values summing to zero.

    Implementations differ in how much they assume about the input and in what
    that assumption buys them: the cubic one takes any list at all, while the
    quadratic and quadrithmic ones require it to be sorted and distinct, and are
    wrong -- not merely slow -- if it is not.
    """

    @abstractmethod
    def get_triples(self) -> list[Triple]:
        """
        :return: the distinct Triples summing to zero, in ascending order.
        """
