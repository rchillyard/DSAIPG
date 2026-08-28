"""
Ported from graphs/gis/GeoEdge.java.
"""

from __future__ import annotations

from typing import TypeVar

from src.graphs.undirected.edge import Edge

V = TypeVar("V")
E = TypeVar("E")


class GeoEdge(Edge[V, E]):
    """
    An edge between two places.

    NOTE it adds no behaviour to Edge whatsoever -- it exists so that an edge of a
    Geo graph is recognisably of that graph. The Java is the same.
    """

    @staticmethod
    def create(edge: Edge[V, E]) -> Edge[V, E]:
        """
        :param edge: an ordinary edge.
        :return: the same edge, as a GeoEdge.
        """
        v = edge.get()
        return GeoEdge(v, edge.get_other(v), edge.attribute)
