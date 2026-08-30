"""
Breadth-first search, ported from graphs/traversal/BFS.java and
graphs/generic_BFS_and_prims/GBFS.java.
"""

from __future__ import annotations

from collections import deque
from typing import Generic, TypeVar

from src.graphs.traversal.graph import Graph

T = TypeVar("T")


class BFS(Generic[T]):
    """
    Breadth-first traversal of a Graph.

    NOTE the Java has this twice: BFS, over int vertices with its own adjacency
    lists, and GBFS<T>, over a generic Graph. In Python the two collapse, because
    a dict-backed Graph takes any hashable vertex and there is no int/generic
    distinction to make. This is the single version.

    NOTE also that GBFS keeps `marked` as a field set up in the constructor, so
    calling its bfs twice on the same instance returns nothing the second time --
    every vertex is still marked from the first run. Its constructor is dead
    besides: it builds an edgeTo map, discards it, and its one call to bfs is
    commented out. Here the visited set is local to the traversal, so a BFS object
    can be reused.
    """

    def __init__(self, graph: Graph[T]) -> None:
        """
        :param graph: the graph to search.
        """
        self._graph = graph

    def traverse(self, source: T) -> list[T]:
        """
        Visit every vertex reachable from source, nearest first.

        :param source: where to start.
        :return: the vertices in the order they were reached.
        """
        visited = {source}
        queue: deque[T] = deque([source])
        output: list[T] = []
        while queue:
            v = queue.popleft()
            output.append(v)
            for w in self._graph.adj(v):
                if w not in visited:
                    visited.add(w)
                    queue.append(w)
        return output
