"""
Ported from graphs/gis/GeoMST.java, GeoPrim.java, GeoKruskal.java and GeoBoruvka.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Generic, TypeVar

from src.graphs.undirected.edge import Edge
from src.graphs.undirected.edge_graph import EdgeGraph

from .boruvka import Boruvka
from .geo import Geo
from .geo_edge import GeoEdge
from .kruskal import Kruskal
from .prim import Prim

V = TypeVar("V")
X = TypeVar("X")


class GeoMST(Generic[V, X], ABC):
    """
    An MST algorithm that can render its answer as a Geo graph.

    NOTE the Java gives GeoPrim, GeoKruskal and GeoBoruvka a copy each of
    get_geo_mst and create_edge -- the same eight lines three times. Here they live
    once, on this class, and the three subclasses below add nothing at all. The
    Java cannot do that: get_geo_mst needs getMST(), which is package-private on
    MST, and an interface method must be public.
    """

    @abstractmethod
    def get_mst(self) -> EdgeGraph[V, X]:
        """
        :return: the MST as a graph.
        """

    def get_geo_mst(self, geo_graph: Geo[V, X]) -> Geo[V, X]:
        """
        :param geo_graph: an empty Geo graph, to be filled with the MST's edges.
        :return: that same graph, now holding the MST.
        """
        for e in self.get_mst().edges():
            geo_graph.add_edge(self.create_edge(e))
        return geo_graph

    @staticmethod
    def create_edge(edge: Edge[V, X]) -> Edge[V, X]:
        """
        :param edge: an ordinary edge.
        :return: the same edge, as a GeoEdge.
        """
        v = edge.get()
        return GeoEdge(v, edge.get_other(v), edge.attribute)


class GeoPrim(Generic[V, X], Prim[V, X], GeoMST[V, X]):
    """
    Prim's algorithm over a graph of places.
    """


class GeoKruskal(Generic[V, X], Kruskal[V, X], GeoMST[V, X]):
    """
    Kruskal's algorithm over a graph of places.
    """


class GeoBoruvka(Generic[V, X], Boruvka[V, X], GeoMST[V, X]):
    """
    Boruvka's algorithm over a graph of places.
    """
