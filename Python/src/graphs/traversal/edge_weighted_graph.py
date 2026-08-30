"""
An edge-weighted undirected graph, ported from
graphs/traversal/EdgeWeightedGraph.java.
"""

from __future__ import annotations

from collections.abc import Iterator
from random import Random

from src.adt.bqs.bag import Bag
from src.adt.bqs.bag_array import BagArray
from src.graphs.traversal.edge import Edge


class EdgeWeightedGraph:
    """
    A fixed-size undirected graph whose vertices are 0..V-1 and whose edges carry
    weights.

    NOTE the vertex count is fixed at construction, unlike graphs.undirected and
    graphs.dag where a vertex springs into being when an edge mentions it. That is
    the Sedgewick-and-Wayne design this class comes from, and the reason
    validate_vertex exists.
    """

    def __init__(self, v: int, e: int = 0, random: Random | None = None) -> None:
        """
        :param v: the number of vertices.
        :param e: how many random edges to add; 0 for an empty graph.
        :param random: the source of randomness for those edges, so a generated
                       graph can be repeated.
        :raises ValueError: if v or e is negative.
        """
        if v < 0:
            raise ValueError("Number of vertices must be non-negative")
        if e < 0:
            raise ValueError("Number of edges must be non-negative")
        self._v = v
        self._e = 0
        self._adj: list[Bag[Edge]] = [BagArray() for _ in range(v)]
        if e:
            rng = random if random is not None else Random()
            for _ in range(e):
                self.add_edge(Edge(rng.randrange(v), rng.randrange(v),
                                   round(100 * rng.random()) / 100.0))

    @classmethod
    def copy_of(cls, graph: EdgeWeightedGraph) -> EdgeWeightedGraph:
        """
        NOTE the Java's copy constructor pushes each adjacency onto a Stack and
        then re-adds it, which reverses the list -- a deliberate trick to preserve
        insertion order given that its Bag prepends. Ours does not need it: the
        adjacency bags here are BagArrays, which append, so copying in order gives
        the same order.

        :param graph: the graph to copy.
        :return: an independent copy.
        """
        result = cls(graph.v())
        result._e = graph.e()
        for vertex in range(graph.v()):
            for edge in graph._adj[vertex].as_array():
                result._adj[vertex].add(edge)
        return result

    def v(self) -> int:
        """
        :return: the number of vertices.
        """
        return self._v

    def e(self) -> int:
        """
        :return: the number of edges.
        """
        return self._e

    def _validate_vertex(self, vertex: int) -> None:
        if vertex < 0 or vertex >= self._v:
            raise ValueError(f"vertex {vertex} is not between 0 and {self._v - 1}")

    def add_edge(self, edge: Edge) -> None:
        """
        Add an edge, recording it at both ends because the graph is undirected.

        :param edge: the edge to add.
        """
        v = edge.either()
        w = edge.other(v)
        self._validate_vertex(v)
        self._validate_vertex(w)
        self._adj[v].add(edge)
        self._adj[w].add(edge)
        self._e += 1

    def adj(self, vertex: int) -> Iterator[Edge]:
        """
        :param vertex: the vertex.
        :return: the edges touching it.
        """
        self._validate_vertex(vertex)
        return iter(self._adj[vertex])

    def degree(self, vertex: int) -> int:
        """
        :param vertex: the vertex.
        :return: how many edges touch it.
        """
        self._validate_vertex(vertex)
        return len(self._adj[vertex])

    def edges(self) -> list[Edge]:
        """
        Every edge, once each.

        NOTE a self-loop is recorded twice at its single vertex, so the Java takes
        care to yield it once. That is reproduced here rather than tidied away.

        :return: the edges.
        """
        result: list[Edge] = []
        for vertex in range(self._v):
            self_loops = 0
            for edge in self._adj[vertex].as_array():
                if edge.other(vertex) > vertex:
                    result.append(edge)
                elif edge.other(vertex) == vertex:
                    # a self-loop appears twice in the bag: take every other one
                    if self_loops % 2 == 0:
                        result.append(edge)
                    self_loops += 1
        return result

    def __str__(self) -> str:
        lines = [f"{self._v} {self._e}"]
        for vertex in range(self._v):
            lines.append(f"{vertex}: " + "  ".join(str(e) for e in self._adj[vertex].as_array()))
        return "\n".join(lines) + "\n"
