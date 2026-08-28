"""
Prim's algorithm, ported from graphs/gis/Prim.java.
"""

from __future__ import annotations

from collections.abc import Iterable
from typing import Generic, TypeVar

from src.adt.bqs.queue_elements import QueueElements
from src.adt.pq.priority_queue_binary_heap import PriorityQueueBinaryHeap
from src.graphs.undirected.edge import Edge
from src.graphs.undirected.edge_graph import EdgeGraph
from src.graphs.undirected.graph_edges import GraphEdges

from .mst import MST, by_attribute

V = TypeVar("V")
X = TypeVar("X")


class Prim(Generic[V, X], MST[V, X]):
    """
    The lazy Prim: grow a tree from one vertex, always taking the cheapest edge
    that leaves it, and discard on arrival any edge whose far end has since been
    reached. Translated from Princeton's LazyPrimMST by way of the Java.

    Run from every unreached vertex in turn, so a graph that is not connected
    yields a spanning forest rather than nothing.

    NOTE the Java's priority queue is created with a fixed capacity, and its give()
    DROPS an element rather than growing when full. That mattered: the queue was
    built from an empty iterable, so its capacity was zero and every edge offered
    to the fringe was silently discarded -- the Java Prim returned an empty MST for
    every graph. Python's PriorityQueueBinaryHeap simply appends, so it has no
    capacity to get wrong and this port cannot reproduce that fault. Both are
    right now; the Java takes its capacity from the number of edges.
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
        self._marked = [False] * len(vertices)
        self._pq: PriorityQueueBinaryHeap[Edge[V, X]] = PriorityQueueBinaryHeap(
            max_priority=False, comparator=by_attribute
        )
        self._mst = self._run_prim()

    def get_mst(self) -> EdgeGraph[V, X]:
        """
        :return: the MST as a graph, its edges numbered in the order Prim chose them.
        """
        result: GraphEdges[V, X] = GraphEdges()
        for sequence, edge in enumerate(self._queue):
            edge.attribute.set_sequence(sequence)
            result.add_edge(edge)
        return result

    def _run_prim(self) -> Iterable[Edge[V, X]]:
        """
        Run Prim from every vertex not yet reached, giving a spanning forest.

        :return: the edges chosen.
        """
        for v in list(self._graph.vertices()):
            if not self._marked[self._vertex_to_integer[v]]:
                self._prim(v)
        return list(self._queue)

    def _prim(self, v: V) -> None:
        """
        Grow one tree, starting from one vertex.

        :param v: the vertex to start from.
        """
        self._scan(v)
        while not self._pq.is_empty():
            e = self._pq.take()
            u = e.get()
            w = e.get_other(u)
            ui, wi = self._vertex_to_integer[u], self._vertex_to_integer[w]
            if self._marked[ui] and self._marked[wi]:
                continue  # lazy: both ends are in the tree already
            self._queue.offer(e)
            # scan the ENDPOINTS of e, not the vertex this run started from
            if not self._marked[ui]:
                self._scan(u)
            if not self._marked[wi]:
                self._scan(w)

    def _scan(self, v: V) -> None:
        """
        Bring a vertex into the tree, offering the fringe every edge from it to a
        vertex not yet in the tree.

        :param v: the vertex to bring in.
        """
        self._marked[self._vertex_to_integer[v]] = True
        for e in self._graph.adjacent(v):
            w = e.get_other(v)
            if not self._marked[self._vertex_to_integer[w]]:
                self._pq.give(e)
