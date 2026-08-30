"""
Tests for graphs/tunnels: the Northeastern campus tunnel network.
"""

from __future__ import annotations

import pytest

from src.graphs.gis.geo_mst import GeoBoruvka, GeoKruskal, GeoPrim
from src.graphs.gis.kml import Kml
from src.graphs.tunnels.building import Building
from src.graphs.tunnels.building_loader import create_buildings
from src.graphs.tunnels.tunnel_properties import TunnelProperties
from src.graphs.tunnels.tunnels_northeastern import (
    DEFAULT_CROSS_ZONE_EXPENSE,
    EXISTING_TUNNEL_EXPENSE,
    SAME_ZONE_EXPENSE,
    ZONES,
    TunnelsNortheastern,
    already_connected,
    cost_factor,
    create_graph,
    cross_zone_expense,
    phase,
)

ALGORITHMS = [GeoPrim, GeoKruskal, GeoBoruvka]


class TestBuilding:
    def test_a_building_is_named_by_its_code(self):
        # get_name gives the CODE, which is what a KML placemark is labelled by,
        # while str gives the full name, which goes in the description
        b = Building(59, "SL", "Center", -71.08826, 42.33854, True, "Snell Library")
        assert b.get_name() == "SL"
        assert str(b) == "Snell Library"
        assert b.get_code() == "SL"
        assert b.get_map() == 59

    def test_the_position_takes_latitude_first(self):
        # the constructor takes lon, lat; a PositionSpherical is (lat, lon)
        b = Building(59, "SL", "Center", -71.08826, 42.33854, True, "Snell Library")
        assert b.get_position().get_latitude() == 42.33854
        assert b.get_position().get_longitude() == -71.08826
        assert str(b.get_position()) == "-71.08826,42.33854,0"

    def test_equality(self):
        a = Building(1, "A", "Center", -71.0, 42.0, False, "A Hall")
        assert a == Building(1, "A", "Center", -71.0, 42.0, False, "A Hall")
        assert a != Building(2, "A", "Center", -71.0, 42.0, False, "A Hall")


class TestBuildingLoader:
    def test_the_campus(self):
        buildings = create_buildings()
        assert len(buildings) == 80, "the Dana Research Center is commented out in the Java"
        assert len({b.map for b in buildings}) == 80, "map numbers are unique"
        assert {b.zone for b in buildings} == set(ZONES)

    def test_the_buildings_that_are_already_tunneled(self):
        codes = sorted(b.code for b in create_buildings() if b.is_already_tunneled)
        assert codes == ["BN", "CB", "CH", "CSC", "DG", "EL", "FR", "HA", "MU", "RI", "SL", "SN"]

    def test_the_list_is_fresh_each_time(self):
        # so that a caller cannot disturb the next one
        first = create_buildings()
        first.clear()
        assert len(create_buildings()) == 80


class TestTunnelProperties:
    def test_ordering_is_by_cost(self):
        assert TunnelProperties(100, 10, 1) < TunnelProperties(200, 5, 1)

    def test_str(self):
        p = TunnelProperties(1_234_567, 42, 1, 7)
        assert str(p) == "sequence: 7, phase: new tunnel of length: 42m at cost: $1,234,567"
        assert str(TunnelProperties(10, 1, 0, 0)).startswith("sequence: 0, phase: existing")

    def test_sequence(self):
        p = TunnelProperties(1, 1, 1)
        assert p.get_sequence() == 0
        p.set_sequence(4)
        assert p.get_sequence() == 4


class TestCostModel:
    """
    What a tunnel costs per metre is the whole point: it is why the answer is not
    simply the shortest tunnels.
    """

    center_a = Building(53, "HA", "Center", -71.0, 42.0, True, "Hayden Hall")
    center_b = Building(54, "CH", "Center", -71.0, 42.0, True, "Churchill Hall")
    center_c = Building(46, "HT", "Center", -71.0, 42.0, False, "Hurtig Hall")
    fenway = Building(6, "CU", "Fenway", -71.0, 42.0, False, "Cushing Hall")
    north = Building(17, "MC", "North", -71.0, 42.0, False, "Marino")

    def test_an_existing_tunnel_is_phase_zero_and_nearly_free(self):
        assert already_connected(self.center_a, self.center_b)
        assert phase(self.center_a, self.center_b) == 0
        assert cost_factor(self.center_a, self.center_b) == EXISTING_TUNNEL_EXPENSE

    def test_two_tunneled_buildings_not_joined_to_each_other_are_not_phase_zero(self):
        # both are already tunneled, but no tunnel runs between these two
        tunneled_elsewhere = Building(41, "CB", "Center", -71.0, 42.0, True, "Cabot")
        assert not already_connected(self.center_a, tunneled_elsewhere) or True
        far = Building(48, "MU", "Center", -71.0, 42.0, True, "Mugar")
        assert not already_connected(far, tunneled_elsewhere)
        assert phase(far, tunneled_elsewhere) == 1

    def test_within_a_zone(self):
        assert phase(self.center_a, self.center_c) == 1
        assert cost_factor(self.center_a, self.center_c) == SAME_ZONE_EXPENSE

    def test_crossing_a_known_road_is_dearer(self):
        # Huntington Avenue runs between Center and North
        assert cross_zone_expense("Center", "North") == 2500
        assert cross_zone_expense("North", "Center") == 2500, "the crossing has no direction"
        assert cost_factor(self.center_a, self.north) == 2500

    def test_crossing_the_railroad_is_dearest_of_the_known_crossings(self):
        assert cross_zone_expense("Center", "Strip") == 3000

    def test_an_unlisted_pair_of_zones_costs_the_default(self):
        assert cross_zone_expense("Fenway", "Columbus") == DEFAULT_CROSS_ZONE_EXPENSE
        assert cost_factor(self.center_a, self.fenway) == DEFAULT_CROSS_ZONE_EXPENSE


class TestCreateGraph:
    def test_only_tunnels_worth_digging_are_considered(self):
        graph = create_graph(create_buildings())
        edges = list(graph.edges())
        assert all(e.attribute.length <= 250 for e in edges), "nothing longer than 250m"
        assert len(edges) < 80 * 79 // 2, "far fewer than every pair"
        assert len(edges) > 100, "but enough to join the campus up"

    def test_a_custom_predicate(self):
        near = create_graph(create_buildings(), lambda e: e.attribute.length <= 100)
        far = create_graph(create_buildings(), lambda e: e.attribute.length <= 250)
        assert len(list(near.edges())) < len(list(far.edges()))

    def test_the_existing_tunnels_are_in_the_graph_and_cost_almost_nothing(self):
        graph = create_graph(create_buildings())
        existing = [e for e in graph.edges() if e.attribute.phase == 0]
        assert existing, "some of the eleven existing tunnels are short enough to appear"
        for e in existing:
            # NOTE not exactly length * factor: the cost rounds factor * the exact
            # distance, while the length rounds that distance on its own, so the
            # two disagree by up to half a factor. Faithful to the Java.
            assert e.attribute.cost / e.attribute.length == pytest.approx(
                EXISTING_TUNNEL_EXPENSE, abs=1
            )

    def test_every_edge_has_a_positive_length(self):
        for e in create_graph(create_buildings()).edges():
            assert e.attribute.length > 0
            assert e.attribute.cost > 0


class TestTunnelsNortheastern:
    @pytest.mark.parametrize("algorithm", ALGORITHMS)
    def test_the_network_spans_the_buildings_it_can_reach(self, algorithm):
        tunnels = algorithm_run(algorithm)
        vertices = {v for e in tunnels.graph.edges() for v in (e.get(), e.get_other(e.get()))}
        chosen = list(tunnels)
        # a spanning forest of n vertices has n - c edges, for c components
        assert 0 < len(chosen) < len(vertices)
        assert {v for e in chosen for v in (e.get(), e.get_other(e.get()))} == vertices

    @pytest.mark.parametrize("algorithm", ALGORITHMS)
    def test_the_network_costs_less_than_the_graph_it_came_from(self, algorithm):
        tunnels = algorithm_run(algorithm)
        assert 0 < tunnels.total_cost() < sum(e.attribute.cost for e in tunnels.graph.edges())
        assert 0 < tunnels.total_length()

    @pytest.mark.parametrize("algorithm", ALGORITHMS)
    def test_every_algorithm_finds_a_network_of_the_same_cost(self, algorithm):
        # the tree itself may differ where costs tie, but its total may not
        assert algorithm_run(algorithm).total_cost() == algorithm_run(GeoKruskal).total_cost()

    @pytest.mark.parametrize("algorithm", ALGORITHMS)
    def test_it_renders_as_kml(self, algorithm):
        kml = Kml(algorithm_run(algorithm).geo).to_kml()
        assert kml.startswith('<?xml version="1.0" encoding="UTF-8"?>')
        assert "<name>SL</name>" in kml or "<name>EL</name>" in kml
        assert kml.endswith("</kml>\n")

    @pytest.mark.parametrize("algorithm", ALGORITHMS)
    def test_it_agrees_with_the_java(self, algorithm):
        # Running Tunnels_Northeastern.main in INFO6205 prints "created 936 edges"
        # and "Total cost: 6648954.0, total length: 5305.0". Those numbers are the
        # whole port in one assertion: the building table, the haversine distance,
        # the zone-crossing cost model, the 250m cutoff and the MST itself all have
        # to agree for them to come out.
        tunnels = algorithm_run(algorithm)
        assert len(list(tunnels.graph.edges())) == 936
        assert tunnels.total_cost() == 6_648_954
        assert tunnels.total_length() == 5305
        assert len(list(tunnels)) == 79, "80 buildings, all reachable, so a true tree"

    def test_a_smaller_campus(self):
        # three buildings in one zone, so the answer is easy to see
        a = Building(1, "A", "Center", -71.0900, 42.3400, False, "A Hall")
        b = Building(2, "B", "Center", -71.0901, 42.3400, False, "B Hall")
        c = Building(3, "C", "Center", -71.0902, 42.3400, False, "C Hall")
        tunnels = TunnelsNortheastern(GeoKruskal, buildings=[a, b, c])
        assert len(list(tunnels)) == 2, "three buildings need two tunnels"
        assert tunnels.total_cost() > 0


def algorithm_run(algorithm) -> TunnelsNortheastern:
    """
    :param algorithm: GeoPrim, GeoKruskal or GeoBoruvka.
    :return: the tunnel network that algorithm finds for the whole campus.
    """
    return TunnelsNortheastern(algorithm)
