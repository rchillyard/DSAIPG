"""
Boruvka's algorithm, ported from graphs/gis/Boruvka.java.
"""

from __future__ import annotations

from collections.abc import Iterable
from typing import Generic, TypeVar

from src.adt.bqs.queue_elements import QueueElements
from src.graphs.undirected.edge import Edge
from src.graphs.undirected.edge_graph import EdgeGraph
from src.graphs.undirected.graph_edges import GraphEdges
from src.graphs.union_find.typed_uf_hwqupc import TypedUF_HWQUPC

from .mst import MST

V = TypeVar("V")
X = TypeVar("X")


class Boruvka(Generic[V, X], MST[V, X]):
    """
    Boruvka's algorithm: every component finds the cheapest edge leaving it, and
    all of those edges are added at once. Each round at least halves the number of
    components, so log V rounds suffice -- which is what the doubling loop counts.

    NOTE like Kruskal, and unlike Prim, this reads the edges rather than asking
    what is adjacent to a vertex.
    """

    def __init__(self, graph: EdgeGraph[V, X]) -> None:
        """
        :param graph: the edge-weighted graph to span.
        """
        super().__init__()
        self._graph = graph
        self._queue: QueueElements[Edge[V, X]] = QueueElements()
        vertices = list(graph.vertices())
        self._vertex_to_integer = {v: i for i, v in enumerate(vertices)}
        self._uf = TypedUF_HWQUPC(vertices)
        self._size = len(vertices)
        self._mst = self._run_boruvka()

    def get_mst(self) -> EdgeGraph[V, X]:
        """
        :return: the MST as a graph, its edges numbered in the order Boruvka chose them.
        """
        result: GraphEdges[V, X] = GraphEdges()
        for sequence, edge in enumerate(self._queue):
            edge.attribute.set_sequence(sequence)
            result.add_edge(edge)
        return result

    def _run_boruvka(self) -> Iterable[Edge[V, X]]:
        """
        :return: the edges chosen, a round at a time.
        """
        t = 1
        while t < self._size and len(self._queue) < self._size - 1:
            closest: list[Edge[V, X] | None] = [None] * self._size
            # for each component, the cheapest edge leaving it; ties go to the
            # edge that comes first in graph.edges()
            for e in self._graph.edges():
                v = e.get()
                w = e.get_other(v)
                i = self._uf.find(self._vertex_to_integer[v])
                j = self._uf.find(self._vertex_to_integer[w])
                if i == j:
                    continue  # same tree
                if closest[i] is None or e.attribute < closest[i].attribute:
                    closest[i] = e
                if closest[j] is None or e.attribute < closest[j].attribute:
                    closest[j] = e
            for e in closest:
                if e is None:
                    continue
                v = e.get()
                w = e.get_other(v)
                # do not add the same edge twice: both its components may have
                # chosen it, and an earlier pair may have joined them since
                if self._uf.find(self._vertex_to_integer[v]) != self._uf.find(
                    self._vertex_to_integer[w]
                ):
                    self._queue.offer(e)
                    self._uf.union(v, w)
            t += t
        return list(self._queue)
