"""
The DAG abstraction, ported from graphs/dag/DAG.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import Callable, Collection, Iterable
from typing import Generic, TypeVar

from src.graphs.dag.edge import Edge

V = TypeVar("V")
E = TypeVar("E")


class DAG(Generic[V, E], ABC):
    """
    A directed acyclic graph.

    Acyclic is a promise the interface makes and cannot keep: nothing here checks
    it, and ``sorted`` on a graph with a cycle returns an order rather than an
    error. See DAGImpl.sorted.
    """

    @abstractmethod
    def vertices(self) -> Collection[V]:
        """
        :return: every vertex.
        """

    @abstractmethod
    def edges(self) -> Collection[Edge[V, E]]:
        """
        :return: every edge.
        """

    @abstractmethod
    def adjacent(self, vertex: V) -> Iterable[Edge[V, E]]:
        """
        :param vertex: the vertex to look from.
        :return: the edges leading away from it.
        """

    @abstractmethod
    def dfs(self, vertex: V, pre: Callable[[V], None] | None,
            post: Callable[[V], None] | None) -> None:
        """
        Depth-first search from the given vertex.

        :param vertex: where to start.
        :param pre: called on each vertex before its descendants, or None.
        :param post: called on each vertex after its descendants, or None.
        """

    @abstractmethod
    def sorted(self) -> Iterable[V]:
        """
        :return: the vertices in topological order.
        """
