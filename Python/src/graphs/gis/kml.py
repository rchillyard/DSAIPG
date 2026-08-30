"""
Ported from graphs/gis/Kml.java.
"""

from __future__ import annotations

from pathlib import Path
from typing import Generic, TypeVar

from src.graphs.undirected.edge import Edge
from src.graphs.undirected.edge_graph import EdgeGraph

V = TypeVar("V")
E = TypeVar("E")

PREAMBLE = (
    '<?xml version="1.0" encoding="UTF-8"?>\n'
    '<kml xmlns="http://www.opengis.net/kml/2.2">\n'
    "  <Document>\n"
    "    <name>NEU Tunnel System</name>\n"
    "    <description>A possible design for a future tunnel system for the "
    "Northeastern University Campus in Boston, MA.</description>\n"
)

COLOPHON = "  </Document>\n</kml>\n"


class Kml(Generic[V, E]):
    """
    Renders a graph of places as KML, which Google Earth and Google Maps can draw.

    The vertices become placemarks and the edges become lines, the lines in the
    order the MST algorithm chose them -- which is what the Sequenced attribute is
    for.
    """

    def __init__(self, graph: EdgeGraph[V, E]) -> None:
        """
        :param graph: the graph to render; its vertices must be GeoPoints and its
                      edge attributes Sequenced.
        """
        self.graph = graph

    def create_kml(self, file: str | Path) -> None:
        """
        :param file: where to write the KML.
        """
        Path(file).write_text(self.to_kml(), encoding="utf-8")

    def to_kml(self) -> str:
        """
        NOTE the Java has only createKML, which writes straight to a file. Pulling
        the rendering out makes it testable without a temporary directory, and
        createKML is then one line.

        :return: the KML document as a string.
        """
        parts = [PREAMBLE]
        parts.extend(self._as_point(vertex) for vertex in self.graph.vertices())
        edges = sorted(self.graph.edges(), key=lambda e: e.attribute.get_sequence())
        parts.extend(self._as_line(edge) for edge in edges)
        parts.append(COLOPHON)
        return "".join(parts)

    @staticmethod
    def _as_point(vertex: V) -> str:
        """
        :param vertex: a place.
        :return: that place as a KML placemark.
        """
        return (
            "      <Placemark>\n"
            f"      <name>{vertex.get_name()}</name>\n"
            f"      <description>{vertex}</description>\n"
            "      <Point>\n"
            "        <coordinates>\n"
            f"{vertex.get_position()}\n"
            "        </coordinates>\n"
            "      </Point>\n"
            "      </Placemark>\n"
        )

    @staticmethod
    def _as_line(edge: Edge[V, E]) -> str:
        """
        :param edge: a route between two places.
        :return: that route as a KML line.
        """
        v1 = edge.get()
        v2 = edge.get_other(v1)
        return (
            "      <Placemark>\n"
            f"      <name>{v1.get_name()}--{v2.get_name()}</name>\n"
            f"      <description>{edge}</description>\n"
            "      <LineString>\n"
            "        <tessellate>1</tessellate>\n"
            "        <coordinates>\n"
            f"{v1.get_position()}\n"
            f"{v2.get_position()}\n"
            "        </coordinates>\n"
            "      </LineString>\n"
            "      </Placemark>\n"
        )
