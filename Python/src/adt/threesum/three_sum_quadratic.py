"""
Quadratic three-sum, ported from adt/threesum/ThreeSumQuadratic.java.
"""

from __future__ import annotations

from src.adt.threesum.three_sum import ThreeSum
from src.adt.threesum.triple import Triple


class ThreeSumQuadratic(ThreeSum):
    """
    Divides the solution space into n sub-spaces, one per middle index, and solves
    each in linear time by working outwards from it. Overall O(n^2).

    NOTE the list given to the constructor MUST be sorted and distinct. Nothing
    checks this, and an unsorted list produces a wrong answer rather than an
    error.
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
            triples.extend(self.get_triples_with_middle(i))
        return sorted(set(triples))

    def get_triples_with_middle(self, j: int) -> list[Triple]:
        """
        Get the Triples whose middle element is at index j.

        Two indices walk outwards from j, one down and one up. At each step the
        value needed at the top is determined by the one at the bottom, so a
        single comparison says which index to move -- which is what makes this
        sub-problem linear rather than quadratic.

        NOTE named get_triples_with_middle rather than get_triples, because Python
        cannot overload: the Java distinguishes getTriples(int) from getTriples()
        by arity.

        :param j: the index of the middle value.
        :return: the Triples with a[j] in the middle.
        """
        # TO BE IMPLEMENTED : for each candidate, test if a[i] + a[j] + a[k] = 0.
        raise NotImplementedError("TO BE IMPLEMENTED")
