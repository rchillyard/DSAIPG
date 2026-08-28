"""
Ported from graphs/gis/BaseGeoGraph.java.
"""

from __future__ import annotations

from collections.abc import Iterable
from typing import Generic, TypeVar

from src.graphs.undirected.edge import Edge
from src.graphs.undirected.graph_edges import GraphEdges

from .geo import Geo

V = TypeVar("V")
E = TypeVar("E")


class BaseGeoGraph(Generic[V, E], GraphEdges[V, E], Geo[V, E]):
    """
    A Geo graph built on GraphEdges, leaving only ``length`` to a subclass.
    """

    __slots__ = ()

    def geo_edges(self) -> Iterable[Edge[V, E]]:
        """
        :return: the edges of this graph, as a list.
        """
        return list(self.edges())
