"""
Ported from graphs/gis/ShortestPaths.java.
"""

from __future__ import annotations

import heapq
import math
from typing import Generic, TypeVar

from src.graphs.dag.di_graph import DiGraph
from src.graphs.dag.edge import Edge

V = TypeVar("V")
E = TypeVar("E")


class Vertex(Generic[V, E]):
    """
    What is known so far about one vertex: how cheaply it can be reached, and by
    which edge.
    """

    def __init__(self, vertex: V, cost: float = math.inf, edge_to: Edge[V, E] | None = None) -> None:
        """
        :param vertex: the vertex itself.
        :param cost: the cheapest known cost of reaching it; infinite if unreached.
        :param edge_to: the last edge of the cheapest known path to it.
        """
        self.vertex = vertex
        self.cost = cost
        self.edge_to = edge_to

    def relax(self, cost: float, edge_to: Edge[V, E]) -> None:
        """
        :param cost: a cheaper cost of reaching this vertex.
        :param edge_to: the last edge of that cheaper path.
        """
        self.cost = cost
        self.edge_to = edge_to

    def __str__(self) -> str:
        return f"Vertex {{{self.vertex}: cost={self.cost}, edgeTo={self.edge_to}}}"


class ShortestPaths(Generic[V, E]):
    """
    The cheapest route from one vertex to every other, over a directed graph whose
    edge attributes are numbers.

    NOTE the Java's queue is a ``PriorityQueue<V>``, so it orders VERTICES by their
    own natural ordering rather than by cost. That is not Dijkstra's rule, and it
    means the Java also requires the vertex type to be Comparable, which its
    signature never says. It still arrives at the right answer, because a vertex is
    re-queued whenever a cheaper route to it is found and the loop runs until
    nothing improves -- but it does more work than Dijkstra, and it fails outright
    on a vertex type that cannot be ordered. This port queues by cost, which is
    both the algorithm as taught and free of that constraint.
    """

    def __init__(self, graph: DiGraph[V, E], start: V) -> None:
        """
        :param graph: the directed graph to search.
        :param start: the vertex to start from.
        """
        self.graph = graph
        self.start = start
        self.table: dict[V, Vertex[V, E]] = self._dijkstra()

    def cost(self, v: V) -> float:
        """
        :param v: the vertex asked about.
        :return: the cheapest cost of reaching it, or infinity if it cannot be reached.
        """
        return self.table[v].cost if v in self.table else math.inf

    def has_path_to(self, v: V) -> bool:
        """
        :param v: the vertex asked about.
        :return: whether it can be reached from the start.
        """
        return v in self.table

    def path_to(self, target: V) -> list[Edge[V, E]]:
        """
        :param target: the vertex to reach.
        :return: the edges of the cheapest route to it, in order; empty if it
                 cannot be reached.
        """
        edges: list[Edge[V, E]] = []
        if self.has_path_to(target):
            v = target
            vertex = self.table[v]
            while vertex.edge_to is not None:
                edge_to = vertex.edge_to
                if edge_to.get_to() != v:
                    raise RuntimeError("assertion error")
                edges.append(edge_to)
                v = edge_to.get_from()
                vertex = self.table[v]
        edges.reverse()
        return edges

    def _dijkstra(self) -> dict[V, Vertex[V, E]]:
        """
        :return: the table of what is known about every vertex reached.
        """
        result: dict[V, Vertex[V, E]] = {self.start: Vertex(self.start, 0, None)}
        # (cost, tie-break, vertex): the counter keeps heapq from ever having to
        # compare two vertices, which it would otherwise do on equal costs
        counter = 0
        pq: list[tuple[float, int, V]] = [(0.0, counter, self.start)]
        settled: set[V] = set()
        while pq:
            _, _, vertex = heapq.heappop(pq)
            if vertex in settled:
                continue
            settled.add(vertex)
            for e in self.graph.adjacent(vertex):
                w = e.get_to()
                vertex_w = result.setdefault(w, Vertex(w))
                relaxed = result[e.get_from()].cost + float(e.get_attributes())
                if vertex_w.cost > relaxed:
                    vertex_w.relax(relaxed, e)
                    counter += 1
                    heapq.heappush(pq, (relaxed, counter, w))
        return result

    def __str__(self) -> str:
        return f"ShortestPaths{{table={ {k: str(v) for k, v in self.table.items()} }}}"
