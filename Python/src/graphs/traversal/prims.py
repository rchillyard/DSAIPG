"""
Prim's minimum spanning tree, ported from graphs/traversal/Prims.java.
"""

from __future__ import annotations

import heapq

from src.graphs.traversal.edge import Edge
from src.graphs.traversal.edge_weighted_graph import EdgeWeightedGraph


class Prims:
    """
    Prim's algorithm: grow a tree from vertex 0, always taking the lightest edge
    that leaves it.

    NOTE the Java uses its own MinPQ, a binary heap it ships in both
    graphs/traversal and graphs/generic_BFS_and_prims. This uses heapq, which is
    the same data structure from the standard library. The tree's own
    PriorityQueueBinaryHeap would also serve; heapq is chosen because nothing here
    is teaching how a heap works -- PriorityQueueBinaryHeap is where that lesson
    lives, and it has its own tests.

    NOTE the Java assumes the graph is connected, and says so. On a disconnected
    graph it returns a spanning tree of vertex 0's component only. Reproduced, and
    asserted in the tests, rather than quietly fixed.
    """

    def __init__(self, graph: EdgeWeightedGraph) -> None:
        """
        :param graph: the graph, assumed connected.
        """
        self._mst: list[Edge] = []
        self._marked = [False] * graph.v()
        # heapq orders by the tuple, so the counter breaks ties on weight without
        # ever comparing two Edges -- which would be fine here but is fragile in
        # general, since Edge orders on weight alone and equal weights are common.
        heap: list[tuple[float, int, Edge]] = []
        counter = 0

        def visit(v: int) -> None:
            nonlocal counter
            self._marked[v] = True
            for e in graph.adj(v):
                if not self._marked[e.other(v)]:
                    heapq.heappush(heap, (e.weight, counter, e))
                    counter += 1

        if graph.v() > 0:
            visit(0)
            while heap:
                _, _, edge = heapq.heappop(heap)
                v = edge.either()
                w = edge.other(v)
                if self._marked[v] and self._marked[w]:
                    continue
                self._mst.append(edge)
                if not self._marked[v]:
                    visit(v)
                if not self._marked[w]:
                    visit(w)

    def edges(self) -> list[Edge]:
        """
        :return: the edges of the minimum spanning tree.
        """
        return list(self._mst)

    def weight(self) -> float:
        """
        :return: the total weight of the tree.
        """
        return sum(e.weight for e in self._mst)
