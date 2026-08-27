"""
The simplest undirected graph, ported from graphs/undirected/Graph_Simple.java.
"""

from __future__ import annotations

from src.graphs.undirected.abstract_graph import AbstractGraph


class GraphSimple(AbstractGraph[int, int]):
    """
    An undirected graph whose vertices are ints and whose edges carry nothing.

    The adjacency type is the vertex type: what is adjacent to a vertex is
    another vertex, not an edge. That is the whole difference between this and
    GraphEdges, and it is why AbstractGraph is generic in both -- the vertex type
    and the adjacency type vary independently.

    NOTE the Java overrides adjacent(int) to call super. That is only there to
    accept a primitive int without boxing at the call site; Python has no such
    distinction, so AbstractGraph.adjacent serves unchanged.
    """

    __slots__ = ()

    def add_edge(self, v1: int, v2: int) -> None:
        """
        Add an undirected edge between two vertices.

        Undirected means recorded twice, once at each end. Either vertex may be
        new: get_adjacency_bag creates a bag on demand.

        :param v1: one vertex.
        :param v2: the other.
        """
        self.get_adjacency_bag(v1).add(v2)
        self.get_adjacency_bag(v2).add(v1)

    def __str__(self) -> str:
        return str(self._adjacent_edges)
