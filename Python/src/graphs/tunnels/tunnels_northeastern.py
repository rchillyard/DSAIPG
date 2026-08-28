"""
The Northeastern campus tunnel problem, ported from
graphs/tunnels/Tunnels_Northeastern.java.
"""

from __future__ import annotations

from collections.abc import Callable, Iterator

from src.graphs.gis.geo import Geo
from src.graphs.gis.geo_graph_spherical import GeoGraphSpherical
from src.graphs.gis.mst import MST
from src.graphs.undirected.edge import Edge
from src.graphs.undirected.edge_graph import EdgeGraph

from .building import Building
from .building_loader import create_buildings
from .tunnel_properties import TunnelProperties

#: The zones of the campus. Their ORDER is significant: the cost of crossing from
#: one zone to another is keyed by position in this list.
ZONES = [
    "Center", "Fenway", "North", "Plaza", "West Village", "Centennial", "Matthews",
    "Columbus", "Strip", "St. Stephens", "Pool", "Theater", "Symphony",
]

#: What it costs, per metre, to tunnel from one zone into another -- a barrier
#: between them being what makes it expensive. Unlisted pairs cost 10000.
CROSS_ZONE_EXPENSE = {
    frozenset({0, 8}): 3000,    # the railroad
    frozenset({0, 2}): 2500,    # Huntington Avenue
    frozenset({10, 11}): 2500,  # Huntington Avenue
    frozenset({10, 12}): 2500,  # Massachusetts Avenue
    frozenset({6, 11}): 2500,   # Massachusetts Avenue
    frozenset({6, 0}): 1100,    # Gainsborough Street
    frozenset({0, 5}): 1500,    # Forsyth Street
    frozenset({3, 0}): 1500,    # Forsyth Street
    frozenset({1, 2}): 1500,    # Hemenway Street
    frozenset({4, 3}): 1200,    # Leon Street
    frozenset({4, 5}): 1200,    # Leon Street
    frozenset({3, 5}): 1200,    # Leon Street
    frozenset({7, 8}): 1750,    # Columbus Avenue
}

#: What it costs, per metre, where no barrier is crossed.
DEFAULT_CROSS_ZONE_EXPENSE = 10000
SAME_ZONE_EXPENSE = 1000
EXISTING_TUNNEL_EXPENSE = 10

#: The tunnels that already exist, as pairs of campus map numbers.
EXISTING_TUNNELS = [
    frozenset({55, 58}), frozenset({55, 54}), frozenset({53, 54}), frozenset({53, 59}),
    frozenset({53, 55}), frozenset({53, 42}), frozenset({53, 41}), frozenset({53, 52}),
    frozenset({52, 50}), frozenset({52, 43}), frozenset({52, 48}),
]

#: A tunnel longer than this is not worth considering.
MAX_LENGTH = 250


def already_connected(b1: Building, b2: Building) -> bool:
    """
    :param b1: one building.
    :param b2: another.
    :return: whether a tunnel between them already exists.
    """
    return frozenset({b1.get_map(), b2.get_map()}) in EXISTING_TUNNELS


def phase(b1: Building, b2: Building) -> int:
    """
    :param b1: one building.
    :param b2: another.
    :return: 0 if the tunnel between them is already built, otherwise 1.
    """
    if b1.is_already_tunneled and b2.is_already_tunneled and already_connected(b1, b2):
        return 0
    return 1


def cost_factor(b1: Building, b2: Building) -> int:
    """
    :param b1: one building.
    :param b2: another.
    :return: what a tunnel between them costs per metre.
    """
    if phase(b1, b2) == 0:
        return EXISTING_TUNNEL_EXPENSE
    if b1.zone == b2.zone:
        return SAME_ZONE_EXPENSE
    return cross_zone_expense(b1.zone, b2.zone)


def cross_zone_expense(zone1: str, zone2: str) -> int:
    """
    :param zone1: one zone.
    :param zone2: another.
    :return: what it costs per metre to tunnel between them.
    """
    return CROSS_ZONE_EXPENSE.get(
        frozenset({ZONES.index(zone1), ZONES.index(zone2)}), DEFAULT_CROSS_ZONE_EXPENSE
    )


def tunnel_properties(b1: Building, b2: Building, length: float) -> TunnelProperties:
    """
    :param b1: one building.
    :param b2: another.
    :param length: how far apart they are, in metres.
    :return: what a tunnel between them would cost, and which phase it is.
    """
    return TunnelProperties(
        round(cost_factor(b1, b2) * length), round(length), phase(b1, b2), 0
    )


def create_graph(
    buildings: list[Building],
    predicate: Callable[[Edge[Building, TunnelProperties]], bool] | None = None,
) -> GeoGraphSpherical[Building, TunnelProperties]:
    """
    Build the graph of every tunnel that could be dug, and what each would cost.

    :param buildings: the buildings to join.
    :param predicate: which candidate tunnels to keep; by default, those no longer
                      than MAX_LENGTH.
    :return: the graph.
    """
    if predicate is None:
        def predicate(e: Edge[Building, TunnelProperties]) -> bool:
            return e.attribute.length <= MAX_LENGTH

    graph: GeoGraphSpherical[Building, TunnelProperties] = GeoGraphSpherical()
    for i, b1 in enumerate(buildings):
        for b2 in buildings[i + 1:]:
            length = graph.get_distance(b1, b2)
            graph.add_edge_vertices(b1, b2, tunnel_properties(b1, b2, length), predicate)
    return graph


class TunnelsNortheastern:
    """
    The cheapest set of tunnels joining the buildings of the Northeastern campus:
    a minimum spanning tree over a graph whose edges are candidate tunnels and
    whose weights are what they would cost to dig.

    A tunnel that already exists is nearly free, one within a zone is cheap, and
    one crossing a road or the railroad is dear -- which is what makes the answer
    interesting rather than simply the shortest tunnels.

    NOTE the Java has four classes here: Tunnels_Northeastern, which takes the MST
    algorithm as a parameter, and Tunnels_Prim, Tunnels_Kruskal and Tunnels_Boruvka,
    which are copies of it with the algorithm fixed -- about 700 lines repeating
    what the parameter already expresses. Only the general one is ported; pass
    GeoPrim, GeoKruskal or GeoBoruvka. Tunnels_Gryphon is not ported either, since
    Gryphon is a Scala library with no Python counterpart.

    NOTE also that the Java fills its zone and existing-tunnel tables from the
    constructor, into static lists, so they are rebuilt on every construction.
    Here they are module constants. Nothing turns on it -- indexOf finds the first
    match, so the duplicated entries are never reached -- but a table of constants
    is what they are.
    """

    def __init__(
        self,
        mst_function: Callable[[EdgeGraph[Building, TunnelProperties]], MST],
        buildings: list[Building] | None = None,
        predicate: Callable[[Edge[Building, TunnelProperties]], bool] | None = None,
    ) -> None:
        """
        :param mst_function: the MST algorithm to use: GeoPrim, GeoKruskal or GeoBoruvka.
        :param buildings: the buildings to join; by default, all of them.
        :param predicate: which candidate tunnels to consider.
        """
        self.buildings = create_buildings() if buildings is None else list(buildings)
        self.graph = create_graph(self.buildings, predicate)
        self.mst = mst_function(self.graph)
        self.geo: Geo[Building, TunnelProperties] = self.mst.get_geo_mst(GeoGraphSpherical())

    def total_cost(self) -> int:
        """
        :return: what digging the whole tunnel network would cost.
        """
        return sum(e.attribute.cost for e in self.geo.geo_edges())

    def total_length(self) -> int:
        """
        :return: how long the whole tunnel network would be, in metres.
        """
        return sum(e.attribute.length for e in self.geo.geo_edges())

    def __iter__(self) -> Iterator[Edge[Building, TunnelProperties]]:
        return iter(self.mst)
