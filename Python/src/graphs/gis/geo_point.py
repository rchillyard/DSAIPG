"""
Ported from graphs/gis/GeoPoint.java.
"""

from __future__ import annotations

from typing import Protocol, runtime_checkable

from src.graphs.undirected.position import Position


@runtime_checkable
class GeoPoint(Protocol):
    """
    A named place on the earth.
    """

    def get_name(self) -> str:
        """
        :return: what the place is called.
        """
        ...

    def get_position(self) -> Position:
        """
        :return: where the place is.
        """
        ...
