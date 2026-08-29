from __future__ import annotations

from collections.abc import Callable, Iterable
from typing import Generic, TypeVar

from .abstract_graph import AbstractGraph
from .edge import Edge
from .edge_graph import EdgeGraph

V = TypeVar("V")
E = TypeVar("E")


class GraphEdges(Generic[V, E], AbstractGraph[V, Edge[V, E]], EdgeGraph[V, E]):
    """
    A graph whose adjacency type is the Edge itself, so edges can carry attributes.

    NOTE the EdgeGraph base was missing until now, though the Java has
    ``implements EdgeGraph<V, E>``. Two things followed from that: EdgeGraph was
    an interface with no implementation anywhere in the Python tree, and
    ``add_edge_vertices`` -- the Java's ``addEdge(from, to, attribute)`` -- could
    not be called on the only class that should have had it.
    """

    __slots__ = ()

    def edges(self) -> Iterable[Edge[V, E]]:
        """
        NOTE an edge sits in the adjacency bag of BOTH its endpoints, so gathering
        every bag would report each edge twice. An edge is therefore collected only
        from the bag of the vertex ``Edge.get`` returns, which is one endpoint and
        not the other. A self-loop occupies one bag once, and is reported once.

        :return: every edge, each appearing exactly once.
        """
        result: list[Edge[V, E]] = []
        for vertex, bag in self._adjacent_edges.items():
            for e in bag:
                if e.get() == vertex:
                    result.append(e)
        return result

    def add_edge(
        self, edge: Edge[V, E], predicate: Callable[[Edge[V, E]], bool] | None = None
    ) -> None:
        """
        Add an edge to both of its vertices, because this graph is undirected and
        an edge is incident on each of its endpoints alike.

        Both bags, so that ``adjacent(v)`` reports the edges AT v rather than the
        edges that happen to have been written with v first -- which is what an
        algorithm walking the graph by adjacency needs. ``edges`` still reports
        each edge once; see there for how.

        :param edge: the edge to add.
        :param predicate: if given, the edge is added only when this accepts it.
        """
        if predicate is None or predicate(edge):
            v = edge.get()
            w = edge.get_other(v)
            self.get_adjacency_bag(v).add(edge)
            # A self-loop is incident on one vertex, so it belongs in one bag once.
            if v == w:
                return
            self.get_adjacency_bag(w).add(edge)

    def __str__(self) -> str:
        return str(self._adjacent_edges)
