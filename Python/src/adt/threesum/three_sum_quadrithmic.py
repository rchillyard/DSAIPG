"""
Quadrithmic three-sum, ported from adt/threesum/ThreeSumQuadrithmic.java.
"""

from __future__ import annotations

from src.adt.threesum.three_sum import ThreeSum
from src.adt.threesum.triple import Triple


class ThreeSumQuadrithmic(ThreeSum):
    """
    Takes every pair and looks for the value which completes it, by binary search.
    That is O(n^2) pairs each costing O(log n), so O(n^2 log n).

    The interesting comparison is with ThreeSumQuadratic, which solves the same
    problem in O(n^2). Both require a sorted list; the quadratic one exploits the
    ordering to walk two indices in step, while this one only uses it to search.
    Having the right data structure is not the same as using it well.

    NOTE the list given to the constructor MUST be sorted and distinct.
    """

    def __init__(self, a: list[int]) -> None:
        """
        :param a: a sorted list of distinct values.
        """
        self._a = a
        self._length = len(a)

    def get_triples(self) -> list[Triple]:
        """
        :return: the distinct Triples summing to zero, in ascending order.
        """
        triples: list[Triple] = []
        for i in range(self._length):
            for j in range(i + 1, self._length):
                triple = self.get_triple(i, j)
                if triple is not None:
                    triples.append(triple)
        return sorted(set(triples))

    def get_triple(self, i: int, j: int) -> Triple | None:
        """
        Find the Triple completing the pair at indices i and j, if there is one.

        The third element must lie beyond j, or the same triple would be found
        again from a different pair.

        :param i: the index of the first element.
        :param j: the index of the second element, greater than i.
        :return: the Triple, or None if no such value is present.
        """
        # TO BE IMPLEMENTED : use binary search to find the third element
        raise NotImplementedError("TO BE IMPLEMENTED")
