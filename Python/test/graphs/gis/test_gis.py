"""
Tests for graphs/gis: the MST algorithms, the spherical graph, and KML.
"""

from __future__ import annotations

import pytest

from src.graphs.dag.di_graph import DiGraph
from src.graphs.dag.edge import Edge as DiEdge
from src.graphs.gis.boruvka import Boruvka
from src.graphs.gis.geo_edge import GeoEdge
from src.graphs.gis.geo_graph_spherical import GeoGraphSpherical
from src.graphs.gis.geo_mst import GeoBoruvka, GeoKruskal, GeoPrim
from src.graphs.gis.kml import Kml
from src.graphs.gis.kruskal import Kruskal
from src.graphs.gis.position_spherical import PositionSpherical
from src.graphs.gis.prim import Prim
from src.graphs.gis.shortest_paths import ShortestPaths
from src.graphs.undirected.edge import Edge
from src.graphs.undirected.graph_edges import GraphEdges

ALGORITHMS = [Prim, Kruskal, Boruvka]
GEO_ALGORITHMS = [GeoPrim, GeoKruskal, GeoBoruvka]


class Route:
    """
    A route between two places, weighted by what it costs. The test counterpart of
    the Java's MSTFixture.Route.
    """

    def __init__(self, cost: float) -> None:
        self.cost = cost
        self.sequence = 0

    def get_sequence(self) -> int:
        return self.sequence

    def set_sequence(self, sequence: int) -> None:
        self.sequence = sequence

    def __lt__(self, other: Route) -> bool:
        return self.cost < other.cost

    def __eq__(self, other: object) -> bool:
        return isinstance(other, Route) and self.cost == other.cost

    def __hash__(self) -> int:
        return hash(self.cost)

    def __str__(self) -> str:
        return str(int(self.cost))


class Place:
    """
    A named place, for the Geo tests.
    """

    def __init__(self, name: str, latitude: float, longitude: float) -> None:
        self.name = name
        self.position = PositionSpherical(latitude, longitude)

    def get_name(self) -> str:
        return self.name

    def get_position(self) -> PositionSpherical:
        return self.position

    def __eq__(self, other: object) -> bool:
        return isinstance(other, Place) and self.name == other.name

    def __hash__(self) -> int:
        return hash(self.name)

    def __str__(self) -> str:
        return self.name


def kalimantan() -> GraphEdges[str, Route]:
    """
    Six places in Kalimantan, with the cost of a route between each pair. The same
    graph the Java tests use.

    :return: a complete graph on six vertices, with fifteen edges.
    """
    g: GraphEdges[str, Route] = GraphEdges()
    for v, w, cost in [
        ("Po", "Pa", 80), ("Po", "Ban", 101), ("Po", "Bal", 123), ("Po", "S", 237),
        ("Po", "T", 417), ("Pa", "Ban", 56), ("Pa", "Bal", 64), ("Pa", "S", 83),
        ("Pa", "T", 187), ("Ban", "Bal", 73), ("Ban", "S", 95), ("Ban", "T", 203),
        ("Bal", "S", 23), ("Bal", "T", 89), ("S", "T", 63),
    ]:
        g.add_edge(Edge(v, w, Route(cost)))
    return g


#: The one minimum spanning tree of kalimantan, costing 286. Every weight in the
#: graph is distinct, so there is exactly one -- which is why the same answer can
#: be asserted of all three algorithms.
KALIMANTAN_MST = sorted(["Bal-S(23)", "Ban-Pa(56)", "Bal-Pa(64)", "S-T(63)", "Pa-Po(80)"])


def describe(edge: Edge[str, Route]) -> str:
    """
    :param edge: the edge to describe.
    :return: the edge with its endpoints in alphabetical order, so that it reads
             the same whichever way round an algorithm reports it.
    """
    v = edge.get()
    w = edge.get_other(v)
    first, second = (v, w) if v <= w else (w, v)
    return f"{first}-{second}({int(edge.attribute.cost)})"


def assert_mst(expected: list[str], cost: float, mst) -> None:
    """
    :param expected: the edges the MST should consist of.
    :param cost: what those edges should come to.
    :param mst: the tree produced by one of the algorithms.
    """
    edges = list(mst)
    assert sorted(describe(e) for e in edges) == expected
    assert sum(e.attribute.cost for e in edges) == pytest.approx(cost)


class TestMSTAlgorithms:
    """
    Prim, Kruskal and Boruvka must all find the same tree, since there is only one.
    """

    @pytest.mark.parametrize("algorithm", ALGORITHMS)
    def test_kalimantan(self, algorithm):
        assert_mst(KALIMANTAN_MST, 286, algorithm(kalimantan()))

    @pytest.mark.parametrize("algorithm", ALGORITHMS)
    def test_get_mst(self, algorithm):
        mst = algorithm(kalimantan()).get_mst()
        assert len(list(mst.edges())) == 5
        assert len(list(mst.vertices())) == 6
        assert_mst(KALIMANTAN_MST, 286, mst.edges())

    @pytest.mark.parametrize("algorithm", ALGORITHMS)
    def test_get_mst_sequences_the_edges(self, algorithm):
        # the order the algorithm chose them in, which is what Kml draws by
        mst = algorithm(kalimantan()).get_mst()
        assert sorted(e.attribute.get_sequence() for e in mst.edges()) == [0, 1, 2, 3, 4]

    @pytest.mark.parametrize("algorithm", ALGORITHMS)
    def test_spans_each_component_of_a_disconnected_graph(self, algorithm):
        # no spanning tree exists, so what comes out is a spanning FOREST
        g: GraphEdges[str, Route] = GraphEdges()
        g.add_edge(Edge("A", "B", Route(1)))
        g.add_edge(Edge("C", "D", Route(2)))
        g.add_edge(Edge("C", "E", Route(3)))
        assert_mst(["A-B(1)", "C-D(2)", "C-E(3)"], 6, algorithm(g))

    @pytest.mark.parametrize("algorithm", ALGORITHMS)
    def test_degenerate_graphs(self, algorithm):
        assert list(algorithm(GraphEdges())) == []
        one: GraphEdges[str, Route] = GraphEdges()
        one.add_vertex("A")
        assert list(algorithm(one)) == []


class TestGeoMST:
    """
    The Geo variants render the tree they found as a graph of places.
    """

    @staticmethod
    def geo_graph() -> GeoGraphSpherical:
        boston = Place("Boston", 42.3601, -71.0589)
        new_york = Place("New York", 40.7128, -74.0060)
        philly = Place("Philadelphia", 39.9526, -75.1652)
        g: GeoGraphSpherical = GeoGraphSpherical()
        g.add_edge(Edge(boston, new_york, Route(306)))
        g.add_edge(Edge(new_york, philly, Route(130)))
        g.add_edge(Edge(boston, philly, Route(436)))
        return g

    @pytest.mark.parametrize("algorithm", GEO_ALGORITHMS)
    def test_get_geo_mst(self, algorithm):
        result = algorithm(self.geo_graph()).get_geo_mst(GeoGraphSpherical())
        edges = list(result.edges())
        assert len(edges) == 2
        assert sorted(int(e.attribute.cost) for e in edges) == [130, 306]
        assert all(isinstance(e, GeoEdge) for e in edges), "the edges are GeoEdges"

    @pytest.mark.parametrize("algorithm", GEO_ALGORITHMS)
    def test_geo_mst_has_a_length(self, algorithm):
        result = algorithm(self.geo_graph()).get_geo_mst(GeoGraphSpherical())
        for edge in result.geo_edges():
            assert result.length(edge) > 0


class TestGeoGraphSpherical:
    def test_distance_between_two_degrees_of_latitude(self):
        # a degree of latitude is about 111 km anywhere on the globe
        g: GeoGraphSpherical = GeoGraphSpherical()
        assert g.get_distance(Place("a", 0, 0), Place("b", 1, 0)) == pytest.approx(111_320, rel=1e-3)

    def test_distance_from_a_place_to_itself_is_zero(self):
        g: GeoGraphSpherical = GeoGraphSpherical()
        assert g.get_distance(Place("a", 42.3, -71.1), Place("a", 42.3, -71.1)) == pytest.approx(0)

    def test_boston_to_new_york(self):
        # about 306 km as the crow flies
        g: GeoGraphSpherical = GeoGraphSpherical()
        boston, new_york = Place("Boston", 42.3601, -71.0589), Place("NY", 40.7128, -74.0060)
        assert g.get_distance(boston, new_york) == pytest.approx(306_000, rel=0.02)

    def test_length_of_an_edge(self):
        g: GeoGraphSpherical = GeoGraphSpherical()
        boston, new_york = Place("Boston", 42.3601, -71.0589), Place("NY", 40.7128, -74.0060)
        edge = Edge(boston, new_york, Route(1))
        assert g.length(edge) == pytest.approx(g.get_distance(boston, new_york))


class TestPositionSpherical:
    def test_getters(self):
        p = PositionSpherical(42.0, -71.0)
        assert p.get_latitude() == 42.0
        assert p.get_longitude() == -71.0
        assert (p.x, p.y) == (42.0, -71.0)

    def test_str_is_kml_order(self):
        # KML wants longitude, latitude, altitude -- not the order of the fields
        assert str(PositionSpherical(42.0, -71.0)) == "-71.0,42.0,0"

    def test_equality(self):
        assert PositionSpherical(1.0, 2.0) == PositionSpherical(1.0, 2.0)
        assert PositionSpherical(1.0, 2.0) != PositionSpherical(2.0, 1.0)


class TestGeoEdge:
    def test_create_keeps_the_vertices_and_the_attribute(self):
        route = Route(1)
        edge = GeoEdge.create(Edge("A", "B", route))
        assert isinstance(edge, GeoEdge)
        assert (edge.get(), edge.get_other("A"), edge.attribute) == ("A", "B", route)


class TestKml:
    def test_renders_placemarks_and_lines(self):
        boston, new_york = Place("Boston", 42.3601, -71.0589), Place("NY", 40.7128, -74.0060)
        g: GeoGraphSpherical = GeoGraphSpherical()
        g.add_edge(Edge(boston, new_york, Route(306)))
        kml = Kml(g).to_kml()
        assert kml.startswith('<?xml version="1.0" encoding="UTF-8"?>')
        assert kml.endswith("</kml>\n")
        assert "<name>Boston</name>" in kml
        assert "<name>Boston--NY</name>" in kml
        assert "-71.0589,42.3601,0" in kml
        assert kml.count("<Placemark>") == 3, "two places and one line"

    def test_lines_are_drawn_in_sequence(self):
        a, b, c = Place("A", 0, 0), Place("B", 1, 1), Place("C", 2, 2)
        g: GeoGraphSpherical = GeoGraphSpherical()
        later, earlier = Route(1), Route(2)
        later.set_sequence(1)
        earlier.set_sequence(0)
        g.add_edge(Edge(a, b, later))
        g.add_edge(Edge(b, c, earlier))
        kml = Kml(g).to_kml()
        assert kml.index("<name>B--C</name>") < kml.index("<name>A--B</name>")

    def test_create_kml_writes_the_file(self, tmp_path):
        g: GeoGraphSpherical = GeoGraphSpherical()
        g.add_edge(Edge(Place("A", 0, 0), Place("B", 1, 1), Route(1)))
        path = tmp_path / "tunnels.kml"
        Kml(g).create_kml(path)
        assert path.read_text(encoding="utf-8") == Kml(g).to_kml()


class TestShortestPaths:
    @staticmethod
    def graph() -> DiGraph:
        g: DiGraph = DiGraph()
        for v, w, cost in [
            ("A", "B", 1.0), ("B", "C", 2.0), ("C", "D", 1.0), ("A", "E", 4.0),
            ("A", "F", 8.0), ("B", "F", 6.0), ("B", "G", 6.0), ("C", "G", 2.0),
            ("D", "G", 1.0), ("D", "H", 4.0), ("E", "F", 5.0), ("G", "F", 1.0),
            ("G", "H", 1.0),
        ]:
            g.add_edge(DiEdge(v, w, cost))
        return g

    def test_cost(self):
        # the same graph and answer as the Java's testShortestPaths2
        paths = ShortestPaths(self.graph(), "A")
        assert paths.has_path_to("H")
        assert paths.cost("H") == pytest.approx(6.0)

    def test_an_unreachable_vertex(self):
        g: DiGraph = DiGraph()
        g.add_edge(DiEdge("A", "B", 1.0))
        g.add_vertex("Z")
        paths = ShortestPaths(g, "A")
        assert not paths.has_path_to("Z")
        assert paths.cost("Z") == float("inf")
        assert paths.path_to("Z") == []

    def test_path_to_reads_from_the_start(self):
        # The Java's pathTo threw "assertion error" for any path of more than one
        # edge, and would have returned it backwards once that was fixed. No Java
        # test had ever called it. Both are fixed there now.
        g: DiGraph = DiGraph()
        g.add_edge(DiEdge("A", "B", 1.0))
        g.add_edge(DiEdge("B", "C", 2.0))
        g.add_edge(DiEdge("A", "C", 9.0))
        paths = ShortestPaths(g, "A")
        assert [(e.get_from(), e.get_to()) for e in paths.path_to("C")] == [("A", "B"), ("B", "C")]

    def test_path_to_the_start_is_empty(self):
        assert ShortestPaths(self.graph(), "A").path_to("A") == []

    def test_the_whole_path_of_the_cheapest_route(self):
        paths = ShortestPaths(self.graph(), "A")
        assert [e.get_to() for e in paths.path_to("H")] == ["B", "C", "G", "H"]
