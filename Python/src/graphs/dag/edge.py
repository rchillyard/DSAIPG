"""
A directed edge, ported from graphs/dag/Edge.java.
"""

from __future__ import annotations

from dataclasses import dataclass
from typing import Generic, TypeVar

V = TypeVar("V")
E = TypeVar("E")


@dataclass(frozen=True)
class Edge(Generic[V, E]):
    """
    An edge which points from one vertex to another, carrying an attribute.

    Distinct from graphs.undirected.Edge, which is symmetric: there,
    ``Edge(1, 2, x) == Edge(2, 1, x)``. Here direction is the whole point, so
    they are different edges.

    NOTE the Java has neither equals nor hashCode, so its edges compare by
    identity: two separately built edges with the same endpoints and attribute
    are not equal. The dataclass gives value equality instead, which matches the
    undirected Edge and is what makes ``reverse().reverse() == original``
    assertable. Nothing depends on the identity behaviour -- Kernel, which does
    rely on identity, is a separate class and keeps it.

    NOTE ``from_`` rather than ``from``, which is a Python keyword.
    """

    from_: V
    to: V
    attributes: E

    def get_from(self) -> V:
        """
        :return: the vertex this edge points away from.
        """
        return self.from_

    def get_to(self) -> V:
        """
        :return: the vertex this edge points to.
        """
        return self.to

    def get_attributes(self) -> E:
        """
        :return: the attribute carried by this edge.
        """
        return self.attributes

    def reverse(self) -> Edge[V, E]:
        """
        :return: an edge pointing the other way, with the same attribute.
        """
        return Edge(self.to, self.from_, self.attributes)

    def __str__(self) -> str:
        return f"{self.attributes}: {self.from_}->{self.to}"

    def __repr__(self) -> str:
        """
        The same form as __str__: an Edge lives inside a BagArray inside a dict,
        and both build their string with repr. See graphs.undirected.Edge.
        """
        return str(self)
