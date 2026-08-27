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
        result: list[Edge[V, E]] = []
        for bag in self._adjacent_edges.values():
            for e in bag:
                result.append(e)
        return result

    def add_edge(
        self, edge: Edge[V, E], predicate: Callable[[Edge[V, E]], bool] | None = None
    ) -> None:
        if predicate is None or predicate(edge):
            v = edge.get()
            self.get_adjacency_bag(v).add(edge)
            self.get_adjacency_bag(edge.get_other(v))

    def __str__(self) -> str:
        return str(self._adjacent_edges)
