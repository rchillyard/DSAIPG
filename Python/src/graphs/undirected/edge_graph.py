from __future__ import annotations
from abc import ABC, abstractmethod
from typing import Generic, TypeVar, Callable, Iterable
from .edge import Edge
from .graph import Graph

V = TypeVar("V")
E = TypeVar("E")


class EdgeGraph(Generic[V, E], Graph[V, Edge[V, E]], ABC):
    @abstractmethod
    def edges(self) -> Iterable[Edge[V, E]]:
        pass

    @abstractmethod
    def add_edge(
        self, edge: Edge[V, E], predicate: Callable[[Edge[V, E]], bool] | None = None
    ) -> None:
        pass

    def add_edge_vertices(
        self,
        from_v: V,
        to_v: V,
        attribute: E,
        predicate: Callable[[Edge[V, E]], bool] | None = None,
    ) -> None:
        self.add_edge(Edge(from_v, to_v, attribute), predicate)
