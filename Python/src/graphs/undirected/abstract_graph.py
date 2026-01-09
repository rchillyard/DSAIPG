# Python/src/graphs/undirected/abstract_graph.py
from __future__ import annotations
from typing import Generic, TypeVar, Dict
from collections.abc import Iterable, Collection
from adt.bqs.bag import Bag
from adt.bqs.bag_array import BagArray
from .graph import Graph

V = TypeVar("V")
Adj = TypeVar("Adj")


class AbstractGraph(Generic[V, Adj], Graph[V, Adj]):
    __slots__ = ("_adjacent_edges",)

    def __init__(self) -> None:
        self._adjacent_edges: Dict[V, Bag[Adj]] = {}

    def add_vertex(self, vertex: V) -> None:
        self._adjacent_edges[vertex] = BagArray()

    def vertices(self) -> Collection[V]:
        return self._adjacent_edges.keys()

    def adjacent(self, vertex: V) -> Iterable[Adj]:
        bag = self._adjacent_edges.get(vertex)
        return bag if bag is not None else BagArray()

    def get_adjacency_bag(self, vertex: V) -> Bag[Adj]:
        bag = self._adjacent_edges.get(vertex)
        if bag is None:
            bag = BagArray()
            self._adjacent_edges[vertex] = bag
        return bag
