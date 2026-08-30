"""
A directed acyclic graph, ported from graphs/dag/DAG_Impl.java.
"""

from __future__ import annotations

from collections.abc import Callable, Iterable
from typing import Generic, TypeVar

from src.graphs.dag.dag import DAG
from src.graphs.dag.di_graph import DiGraph

V = TypeVar("V")
E = TypeVar("E")


class DAGImpl(DiGraph[V, E], DAG[V, E], Generic[V, E]):
    """
    A DiGraph which additionally offers depth-first search and a topological sort.

    Everything structural is inherited; what this adds is the two traversals.
    """

    __slots__ = ()

    def dfs(self, vertex: V, pre: Callable[[V], None] | None,
            post: Callable[[V], None] | None) -> None:
        """
        Depth-first search from one vertex.

        NOTE this reaches only what is reachable from that vertex. sorted() uses
        the whole-graph traversal instead, which is why an isolated vertex appears
        in a topological order but not in a dfs from elsewhere.

        :param vertex: where to start.
        :param pre: called on each vertex before its descendants, or None.
        :param post: called on each vertex after its descendants, or None.
        """
        self.DepthFirstSearch(self, set(), pre, post).inner_dfs(vertex)

    def sorted(self) -> Iterable[V]:
        """
        The vertices in topological order: every edge points forwards.

        NOTE nothing here checks that the graph is acyclic, and on a graph with a
        cycle this still returns an order -- just not a topological one, since no
        such order exists. The Java has a testSortedWithCycle which is commented
        out for exactly that reason: its author noted the result "is not really
        predictable".

        :return: the vertices, in an order where each comes before everything it
                 points to.
        """
        return self.reverse_post_order_dfs()
