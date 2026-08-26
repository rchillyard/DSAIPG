"""
Brute-force three-sum, ported from adt/threesum/ThreeSumCubic.java.
"""

from __future__ import annotations

from src.adt.threesum.three_sum import ThreeSum
from src.adt.threesum.triple import Triple


class ThreeSumCubic(ThreeSum):
    """
    Tests every candidate in the solution space, so O(n^3).

    The one implementation which assumes nothing: the list need be neither
    ordered nor distinct. That is what it buys with the extra factor of n.

    NOTE "assumes nothing" is about which solutions are found, not about how they
    are written down. A Triple records its elements in the order they were met, so
    an unsorted list yields Triple(-40, 40, 0) where a sorted one yields
    Triple(-40, 0, 40) -- the same solution, but not equal, and deduplication will
    not collapse the two. Give it a sorted list if the answer is to be compared
    with anything.
    """

    def __init__(self, a: list[int]) -> None:
        """
        :param a: the values, in any order.
        """
        self._a = a
        self._length = len(a)

    def get_triples(self) -> list[Triple]:
        """
        :return: the distinct Triples summing to zero, in ascending order.
        """
        a, length = self._a, self._length
        triples = [Triple(a[i], a[j], a[k])
                   for i in range(length)
                   for j in range(i + 1, length)
                   for k in range(j + 1, length)
                   if a[i] + a[j] + a[k] == 0]
        return sorted(set(triples))
