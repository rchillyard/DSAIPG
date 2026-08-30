"""
Ported from graphs/gis/MST.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import Iterable, Iterator
from typing import Generic, TypeVar

from src.graphs.undirected.edge import Edge
from src.graphs.undirected.edge_graph import EdgeGraph

V = TypeVar("V")
X = TypeVar("X")


def by_attribute(e1: Edge, e2: Edge) -> int:
    """
    Orders two edges by their attributes, for a priority queue.

    :param e1: the first edge.
    :param e2: the second edge.
    :return: -1, 0 or 1 as the first attribute is less than, equal to or greater
             than the second.
    """
    a, b = e1.attribute, e2.attribute
    if a < b:
        return -1
    if b < a:
        return 1
    return 0


class MST(Generic[V, X], ABC):
    """
    A minimum spanning tree of an edge-weighted graph: the cheapest set of edges
    that leaves every vertex reachable from every other.

    Iterating an MST gives its edges. Where the graph is not connected there is no
    spanning tree at all, and what the algorithms below produce is a spanning
    FOREST -- every component spanned, no edge between components.
    """

    def __init__(self) -> None:
        self._mst: Iterable[Edge[V, X]] = []

    @abstractmethod
    def get_mst(self) -> EdgeGraph[V, X]:
        """
        :return: the MST as a graph, its edges numbered in the order they were
                 chosen.
        """

    def __iter__(self) -> Iterator[Edge[V, X]]:
        return iter(list(self._mst))
