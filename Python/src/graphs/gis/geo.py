"""
Ported from graphs/gis/Geo.java.
"""

from __future__ import annotations

from abc import abstractmethod
from collections.abc import Iterable
from typing import Generic, TypeVar

from src.graphs.undirected.edge import Edge
from src.graphs.undirected.edge_graph import EdgeGraph

V = TypeVar("V")
E = TypeVar("E")


class Geo(Generic[V, E], EdgeGraph[V, E]):
    """
    An edge graph whose vertices are places, so that an edge has a length.
    """

    @abstractmethod
    def geo_edges(self) -> Iterable[Edge[V, E]]:
        """
        :return: the edges of this graph.
        """

    @abstractmethod
    def length(self, edge: Edge[V, E]) -> float:
        """
        :param edge: the edge to measure.
        :return: how far it is from one end of the edge to the other, in metres.
        """
