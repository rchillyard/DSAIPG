"""
Ported from graphs/gis/Position_Spherical.java.
"""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class PositionSpherical:
    """
    A position on the earth, as a latitude and a longitude in degrees.

    NOTE ``x`` is the latitude and ``y`` the longitude, so this satisfies
    ``Position`` -- but the string form puts longitude FIRST, because that is the
    order KML wants: longitude, latitude, altitude.
    """

    latitude: float
    longitude: float

    @property
    def x(self) -> float:
        """
        :return: the latitude, standing in for x.
        """
        return self.latitude

    @property
    def y(self) -> float:
        """
        :return: the longitude, standing in for y.
        """
        return self.longitude

    def get_latitude(self) -> float:
        """
        :return: the latitude in degrees.
        """
        return self.latitude

    def get_longitude(self) -> float:
        """
        :return: the longitude in degrees.
        """
        return self.longitude

    def __str__(self) -> str:
        return f"{self.longitude},{self.latitude},0"
