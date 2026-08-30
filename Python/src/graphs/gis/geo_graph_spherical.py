"""
Ported from graphs/gis/GeoGraphSpherical.java.
"""

from __future__ import annotations

import math
from typing import Generic, TypeVar

from src.graphs.undirected.edge import Edge
from src.graphs.undirected.position import Position

from .base_geo_graph import BaseGeoGraph
from .geo_point import GeoPoint

V = TypeVar("V")
E = TypeVar("E")

#: The radius of the earth, in metres.
R = 6378100


class GeoGraphSpherical(Generic[V, E], BaseGeoGraph[V, E]):
    """
    A Geo graph on the surface of a sphere, where the length of an edge is the
    great-circle distance between its endpoints.
    """

    __slots__ = ()

    def length(self, edge: Edge[V, E]) -> float:
        """
        :param edge: the edge to measure.
        :return: the distance between its endpoints, in metres.
        """
        v1 = edge.get()
        return self.get_distance(v1, edge.get_other(v1))

    def get_distance(self, p1: GeoPoint, p2: GeoPoint) -> float:
        """
        :param p1: the first place.
        :param p2: the second place.
        :return: the distance between them, in metres.
        """
        return distance(p1.get_position(), p2.get_position())


def distance(p1: Position, p2: Position) -> float:
    """
    The haversine formula: the great-circle distance between two positions.

    :param p1: the first position, whose x is a latitude and y a longitude.
    :param p2: the second position.
    :return: the distance between them, in metres.
    """
    lat_arc = math.radians(p2.x - p1.x)
    lon_arc = math.radians(p2.y - p1.y)
    a = (
        math.sin(lat_arc / 2) ** 2
        + math.cos(math.radians(p1.x)) * math.cos(math.radians(p2.x))
        * math.sin(lon_arc / 2) ** 2
    )
    return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
