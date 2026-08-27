"""
A directed graph, ported from graphs/dag/DiGraph.java.
"""

from __future__ import annotations

from collections.abc import Callable, Collection
from random import Random
from typing import Generic, TypeVar

from src.adt.bqs.bag import Bag
from src.adt.bqs.bag_array import BagArray
from src.adt.bqs.stack import Stack
from src.adt.bqs.stack_linked_list import StackLinkedList
from src.graphs.dag.edge import Edge
from src.graphs.undirected.abstract_graph import AbstractGraph

V = TypeVar("V")
E = TypeVar("E")
T = TypeVar("T")


class Kernel(Generic[T]):
    """
    A set of vertices treated as one: a strongly connected component.

    NOTE deliberately NOT a dataclass, and deliberately without __eq__ or
    __hash__. Two kernels holding the same vertices are still different kernels,
    and DiGraph.kernel_dag relies on that -- it asks whether an edge's two ends
    landed in the same kernel by identity. It also has to be hashable, since a
    Kernel is used as a vertex of the resulting DAG and therefore as a dictionary
    key. Identity gives both for free; value equality would break the first and
    (for a mutable collection) the second.
    """

    def __init__(self, vertices: Collection[T] | None = None) -> None:
        """
        :param vertices: the vertices to start with. None means empty.
        """
        self.vertices: list[T] = list(vertices) if vertices is not None else []

    def add(self, t: T) -> None:
        """
        :param t: a vertex to add to this kernel.
        """
        self.vertices.append(t)

    def __str__(self) -> str:
        return str(self.vertices)

    def __repr__(self) -> str:
        return str(self)


class DiGraph(AbstractGraph[V, "Edge[V, E]"], Generic[V, E]):
    """
    A graph whose edges point one way.

    Note how little separates this from graphs.undirected.GraphEdges: both store
    an edge in the bag of one endpoint only. The difference is entirely in the
    Edge -- an undirected Edge treats its two vertices symmetrically, this one
    does not -- and in what the algorithms then make of it.
    """

    __slots__ = ("_random",)

    def __init__(self, random: Random | None = None) -> None:
        """
        :param random: the entropy for the adjacency bags, which iterate in random
                       order. Passing a seeded Random makes a traversal repeatable.
        """
        super().__init__()
        self._random = random

    def _new_bag(self) -> Bag[Edge[V, E]]:
        """
        :return: an empty bag using this graph's random source.
        """
        return BagArray(self._random)

    def get_adjacency_bag(self, vertex: V) -> Bag[Edge[V, E]]:
        """
        NOTE overridden so the bag gets this graph's random source, which
        AbstractGraph knows nothing about.

        :param vertex: the vertex whose bag is wanted.
        :return: its bag, created if it did not exist.
        """
        bag = self._adjacent_edges.get(vertex)
        if bag is None:
            bag = self._new_bag()
            self._adjacent_edges[vertex] = bag
        return bag

    def add_edge(self, edge: Edge[V, E]) -> None:
        """
        Add a directed edge.

        Unlike an undirected graph this records the edge once, at the vertex it
        leaves. The destination still gets a bag, so that it counts as a vertex,
        but that bag stays empty unless something leads away from it.

        :param edge: the edge to add.
        """
        self.get_adjacency_bag(edge.get_from()).add(edge)
        self.get_adjacency_bag(edge.get_to())

    def add_edge_vertices(self, from_: V, to: V, attributes: E) -> None:
        """
        NOTE named separately because Python cannot overload; the Java has this
        as addEdge(V, V, E).

        :param from_: the vertex the edge leaves.
        :param to: the vertex it arrives at.
        :param attributes: what the edge carries.
        """
        self.add_edge(Edge(from_, to, attributes))

    def edges(self) -> Collection[Edge[V, E]]:
        """
        :return: every edge, gathered from every vertex's bag.
        """
        result: list[Edge[V, E]] = []
        for bag in self._adjacent_edges.values():
            result.extend(bag)
        return result

    def reverse(self) -> DiGraph[V, E]:
        """
        :return: a graph with every edge pointing the other way.
        """
        result: DiGraph[V, E] = DiGraph(self._random)
        for e in self.edges():
            result.add_edge(e.reverse())
        return result

    def reverse_post_order_dfs(self) -> Stack[V]:
        """
        Visit every vertex depth-first, pushing each one as it is finished.

        Popping the result therefore yields reverse post-order, which for an
        acyclic graph is a topological order: a vertex is finished only after
        everything reachable from it, so it is pushed later and pops sooner.

        :return: a Stack of the vertices, in post-order from the bottom.
        """
        post_order_stack: Stack[V] = StackLinkedList()
        self.DepthFirstSearch(self, set(), None, post_order_stack.push).inner_dfs_all()
        return post_order_stack

    def kernel_dag(self) -> DiGraph[Kernel[V], E]:
        """
        Collapse each strongly connected component to a single vertex.

        This is Kosaraju-Sharir: take the reverse graph's reverse post-order, then
        depth-first search the original in that order. Each search that finds
        anything new has found exactly one strongly connected component.

        :return: a DAG whose vertices are the kernels of this graph. It really is
                 acyclic -- collapsing every cycle is what the method does.
        """
        from src.graphs.dag.dag_impl import DAGImpl
        result: DAGImpl[Kernel[V], E] = DAGImpl(Random(0))
        marked: set[V] = set()
        for vertex in self.reverse().reverse_post_order_dfs():
            kernel: Kernel[V] = Kernel()
            self.DepthFirstSearch(self, marked, kernel.add, None).inner_dfs(vertex)
            if kernel.vertices:
                result.add_vertex(kernel)
        kernels = list(result.vertices())
        for edge in self.edges():
            from_kernel = _find_kernel(kernels, edge.get_from())
            to_kernel = _find_kernel(kernels, edge.get_to())
            # NOTE identity, not equality: two kernels holding equal vertices
            # would still be different components.
            if from_kernel is not None and to_kernel is not None \
                    and from_kernel is not to_kernel:
                result.add_edge(Edge(from_kernel, to_kernel, edge.get_attributes()))
        return result

    def __str__(self) -> str:
        return str(self._adjacent_edges)

    class DepthFirstSearch(Generic[V, E]):
        """
        A depth-first traversal, with optional work before and after each vertex.

        NOTE recursive, as the Java is. Python's default recursion limit is around
        a thousand, so a path longer than that would need an explicit stack; the
        graphs in this chapter are far smaller.
        """

        def __init__(self, graph: DiGraph[V, E], marked: set[V],
                     pre: Callable[[V], None] | None,
                     post: Callable[[V], None] | None) -> None:
            """
            :param graph: the graph to walk.
            :param marked: the vertices already visited. Shared deliberately --
                           kernel_dag passes the same set to successive searches,
                           which is what stops a component being found twice.
            :param pre: called on a vertex before its descendants, or None.
            :param post: called on a vertex after its descendants, or None.
            :raises ValueError: if both pre and post are None, which would walk
                                the graph and do nothing.
            """
            if pre is None and post is None:
                raise ValueError("DepthFirstSearch: pre and post cannot both be None")
            self._graph = graph
            self._marked = marked
            self._pre = pre
            self._post = post

        def inner_dfs_all(self) -> None:
            """
            Search from every vertex, so that nothing is missed for being
            unreachable from wherever the walk began.
            """
            for v in list(self._graph.vertices()):
                self.inner_dfs(v)

        def inner_dfs(self, v: V) -> None:
            """
            Search from one vertex, skipping anything already visited.

            NOTE the Java reads adjacentEdges.get(v) directly, so an unknown vertex
            gives a NullPointerException. This goes through adjacent, which returns
            an empty bag -- consistent with the rest of the Python tree.

            :param v: where to start.
            """
            if v in self._marked:
                return
            self._marked.add(v)
            if self._pre is not None:
                self._pre(v)
            for e in list(self._graph.adjacent(v)):
                if e.get_to() not in self._marked:
                    self.inner_dfs(e.get_to())
            if self._post is not None:
                self._post(v)


def _find_kernel(kernels: list[Kernel[V]], vertex: V) -> Kernel[V] | None:
    """
    :param kernels: the kernels to search.
    :param vertex: the vertex to look for.
    :return: the kernel containing it, or None.
    """
    for kernel in kernels:
        if vertex in kernel.vertices:
            return kernel
    return None
