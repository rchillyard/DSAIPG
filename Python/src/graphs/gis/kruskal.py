"""
Kruskal's algorithm, ported from graphs/gis/Kruskal.java.
"""

from __future__ import annotations

from collections.abc import Iterable
from typing import Generic, TypeVar

from src.adt.bqs.queue_elements import QueueElements
from src.adt.pq.priority_queue_binary_heap import PriorityQueueBinaryHeap
from src.graphs.undirected.edge import Edge
from src.graphs.undirected.edge_graph import EdgeGraph
from src.graphs.undirected.graph_edges import GraphEdges
from src.graphs.union_find.typed_uf_hwqupc import TypedUF_HWQUPC

from .mst import MST, by_attribute

V = TypeVar("V")
X = TypeVar("X")


class Kruskal(Generic[V, X], MST[V, X]):
    """
    Kruskal's algorithm: consider every edge in order of cost, and take it unless
    its endpoints are already joined. A union-find structure answers "already
    joined" in very nearly constant time.

    NOTE unlike Prim this never asks the graph what is adjacent to a vertex -- it
    reads the edges and nothing else. That is why the one-sided storage in
    GraphEdges left Kruskal correct while Prim was not.
    """

    def __init__(self, graph: EdgeGraph[V, X]) -> None:
        """
        :param graph: the edge-weighted graph to span.
        """
        super().__init__()
        self._queue: QueueElements[Edge[V, X]] = QueueElements()
        self._pq: PriorityQueueBinaryHeap[Edge[V, X]] = PriorityQueueBinaryHeap(
            max_priority=False, comparator=by_attribute, initial_data=list(graph.edges())
        )
        vertices = list(graph.vertices())
        self._uf = TypedUF_HWQUPC(vertices)
        self._size = len(vertices)
        self._mst = self._run_kruskal()

    def get_mst(self) -> EdgeGraph[V, X]:
        """
        :return: the MST as a graph, its edges numbered in the order Kruskal chose them.
        """
        result: GraphEdges[V, X] = GraphEdges()
        for sequence, edge in enumerate(self._queue):
            edge.attribute.set_sequence(sequence)
            result.add_edge(edge)
        return result

    def _run_kruskal(self) -> Iterable[Edge[V, X]]:
        """
        :return: the edges chosen, cheapest first.
        """
        while not self._pq.is_empty() and len(self._queue) < self._size - 1:
            edge = self._pq.take()
            s1 = edge.get()
            s2 = edge.get_other(s1)
            if not self._uf.connected(s1, s2):
                self._uf.union(s1, s2)
                self._queue.offer(edge)
        return list(self._queue)
