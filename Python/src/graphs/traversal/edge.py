"""
A weighted undirected edge, ported from graphs/traversal/Edge.java.
"""

from __future__ import annotations

import math
from dataclasses import dataclass, field
from functools import total_ordering


@total_ordering
@dataclass(frozen=True)
class Edge:
    """
    An undirected edge between two integer vertices, carrying a weight.

    Ordered by weight alone, which is what Prim's algorithm needs of it: the
    priority queue must hand back the lightest crossing edge, and nothing else
    about the edge matters to that decision.

    NOTE distinct from graphs.undirected.Edge, which carries an arbitrary
    attribute and orders on nothing, and from graphs.dag.Edge, which is directed.
    Three Edge classes is the Java's arrangement and the port keeps it, because
    they really are three different things.
    """

    v: int
    w: int
    weight: float = field(compare=True)

    def __post_init__(self) -> None:
        if self.v < 0 or self.w < 0:
            raise ValueError("vertex index must be a non-negative integer")
        if math.isnan(self.weight):
            raise ValueError("Weight is NaN")

    def either(self) -> int:
        """
        :return: one of the two vertices, arbitrarily but consistently the first.
        """
        return self.v

    def other(self, vertex: int) -> int:
        """
        :param vertex: one endpoint.
        :return: the other endpoint.
        :raises ValueError: if vertex is neither endpoint.
        """
        if vertex == self.v:
            return self.w
        if vertex == self.w:
            return self.v
        raise ValueError("Illegal endpoint")

    def __lt__(self, other: Edge) -> bool:
        return self.weight < other.weight

    def __str__(self) -> str:
        return f"{self.v}-{self.w} {self.weight:.5f}"

    def __repr__(self) -> str:
        return str(self)
