"""
An undirected graph over arbitrary vertices, ported from
graphs/generic_BFS_and_prims/Graph.java.
"""

from __future__ import annotations

from collections.abc import Iterator
from typing import Generic, TypeVar

T = TypeVar("T")


class Graph(Generic[T]):
    """
    An unweighted undirected graph whose vertices may be anything hashable.

    NOTE the Java keeps a vertex count V passed to the constructor and returns it
    from V(), but the adjacency structure is a HashMap and add_vertex does not
    touch the count -- so V() reports what the caller once said rather than how
    many vertices there are. That is faithfully NOT reproduced: order_declared
    keeps the constructor's number under a name that says what it is, and
    __len__ answers the question V() looked like it was answering.
    """

    def __init__(self, order: int = 0) -> None:
        """
        :param order: the number of vertices the caller expects; advisory only.
        """
        self.order_declared = order
        self._adj: dict[T, list[T]] = {}
        self._edges = 0

    def __len__(self) -> int:
        """
        :return: the number of vertices actually present.
        """
        return len(self._adj)

    def e(self) -> int:
        """
        :return: the number of edges.
        """
        return self._edges

    def add_vertex(self, v: T) -> None:
        """
        Add a vertex with no edges. Re-adding an existing vertex clears its edges,
        as the Java's map put does.

        :param v: the vertex.
        """
        self._adj[v] = []

    def add_edge(self, v: T, w: T) -> None:
        """
        Add an undirected edge, recording it at both ends.

        NOTE the Java requires both vertices to have been added first and throws
        NullPointerException otherwise. This creates them on demand, which is what
        every other graph in this tree does.

        :param v: one vertex.
        :param w: the other.
        """
        self._adj.setdefault(v, []).append(w)
        self._adj.setdefault(w, []).append(v)
        self._edges += 1

    def add_directed_edge(self, v: T, w: T) -> None:
        """
        Add an edge in one direction only.

        NOTE the Java has BOTH conventions and does not say so. Graph.addEdge is
        undirected, recording the edge at each end; BFS.addEdge is directed,
        recording it only at v -- which is why BFSTest adds 0->2 and 2->0 as
        separate calls, and why its expected traversals come out as they do.
        Collapsing the two BFS classes into one meant keeping both.

        :param v: the vertex the edge leaves.
        :param w: the vertex it arrives at.
        """
        self._adj.setdefault(v, []).append(w)
        self._adj.setdefault(w, [])
        self._edges += 1

    def adj(self, v: T) -> Iterator[T]:
        """
        :param v: the vertex.
        :return: its neighbours; empty for a vertex not in the graph.
        """
        return iter(self._adj.get(v, []))

    def vertices(self) -> list[T]:
        """
        :return: every vertex.
        """
        return list(self._adj)
