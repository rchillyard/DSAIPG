"""
Ported from graphs/tunnels/Building.java.
"""

from __future__ import annotations

from dataclasses import dataclass, field

from src.graphs.gis.position_spherical import PositionSpherical


@dataclass(frozen=True)
class Building:
    """
    One building on the Northeastern campus, as a place a tunnel could reach.

    NOTE ``get_name`` returns the CODE, not the name -- so a KML placemark is
    labelled "SL" while the description says "Snell Library". That is the Java's
    behaviour and the KML depends on it.
    """

    map: int
    code: str
    zone: str
    lon: float
    lat: float
    is_already_tunneled: bool
    name: str
    position: PositionSpherical = field(init=False, compare=True)

    def __post_init__(self) -> None:
        # NOTE latitude first: the constructor takes lon, lat but a
        # PositionSpherical is (latitude, longitude).
        object.__setattr__(self, "position", PositionSpherical(self.lat, self.lon))

    def get_name(self) -> str:
        """
        :return: the building's code, which is what a KML placemark is named by.
        """
        return self.code

    def get_position(self) -> PositionSpherical:
        """
        :return: where the building is.
        """
        return self.position

    def get_code(self) -> str:
        """
        :return: the building's short code, such as "SL".
        """
        return self.code

    def get_map(self) -> int:
        """
        :return: the building's number on the campus map.
        """
        return self.map

    def __str__(self) -> str:
        return self.name
