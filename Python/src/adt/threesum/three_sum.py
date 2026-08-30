"""
The ThreeSum abstraction, ported from adt/threesum/ThreeSum.java.

NOTE these three implementations are the clearest place in the repository to see that a
growth rate belongs to the algorithm and not to the language. Timing each at a series of
sizes recovers the exponents 3, 2 and 2 in Python just as in Java -- and rather more
cleanly, because the interpreter's uniform per-operation cost drowns out the JIT warm-up
and cache effects that still show in Java's numbers at these sizes. Python takes some
forty times longer, which buys Java about 500 elements: past that, Python's quadratic
beats Java's cubic. See docs/Java vs Python.md.
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
