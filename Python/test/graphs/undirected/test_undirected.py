import math

import pytest

from src.graphs.undirected.abstract_graph import AbstractGraph
from src.graphs.undirected.edge import Edge
from src.graphs.undirected.edge_graph import EdgeGraph
from src.graphs.undirected.graph_edges import GraphEdges
from src.graphs.undirected.graph_simple import GraphSimple
from src.graphs.undirected.position import Position, PositionXY

# NOTE this package had no Python tests at all, against four Java test classes --
# of whose thirteen tests six were empty bodies. So the real Java coverage was
# seven tests. These cover the package properly, and the six empty Java tests
# have been written to match.
#
# Everything which touches a graph is reported as SKIPPED until Bag_Array's
# growth exercise is written: AbstractGraph stores adjacency in a BagArray, whose
# _grow_from raises TO BE IMPLEMENTED. That is correct rather than unfortunate --
# the graph really does depend on the bag -- but it does mean these tests only
# start running once that earlier exercise is done.


class TestEdge:
    def test_get_and_get_other(self):
        target = Edge(1, 2, "hello")
        assert target.get() == 1
        assert target.get_other(1) == 2
        assert target.get_other(2) == 1
        assert target.attribute == "hello"

    def test_an_edge_is_symmetric(self):
        # The property which matters: this is an UNDIRECTED edge, so which vertex
        # was named first must not affect equality.
        assert Edge(1, 2, "hello") == Edge(2, 1, "hello")

    def test_the_attribute_is_part_of_identity(self):
        assert Edge(1, 2, "hello") != Edge(1, 2, "goodbye")

    def test_different_vertices_are_different_edges(self):
        assert Edge(1, 2, "hello") != Edge(1, 3, "hello")

    def test_the_hash_is_symmetric_too(self):
        # Equality without matching hashes would be a silent bug: the two edges
        # would compare equal yet land in different buckets, so a set would hold
        # both. This is the Java's hashCodeTest, which was an empty body.
        assert hash(Edge(1, 2, "hello")) == hash(Edge(2, 1, "hello"))
        assert len({Edge(1, 2, "hello"), Edge(2, 1, "hello")}) == 1

    def test_a_self_loop(self):
        target = Edge(1, 1, "loop")
        assert target.get() == 1
        assert target.get_other(1) == 1

    def test_str(self):
        assert str(Edge(1, 2, "hello")) == "1-2: hello"

    def test_it_is_not_equal_to_a_non_edge(self):
        assert Edge(1, 2, "hello") != "1-2: hello"


class TestGraphSimple:
    """Vertices are ints and edges carry nothing."""

    def test_adjacent(self):
        graph = GraphSimple()
        graph.add_edge(1, 2)
        assert len(graph.adjacent(1)) == 1
        assert len(graph.adjacent(2)) == 1
        assert graph.adjacent(1).contains(2)
        assert graph.adjacent(2).contains(1)

    def test_an_edge_is_recorded_at_both_ends(self):
        # This is what makes the graph undirected.
        graph = GraphSimple()
        graph.add_edge(1, 2)
        assert set(graph.vertices()) == {1, 2}

    def test_several_edges(self):
        graph = GraphSimple()
        for v1, v2 in [(1, 2), (1, 3), (2, 3)]:
            graph.add_edge(v1, v2)
        assert set(graph.vertices()) == {1, 2, 3}
        assert len(graph.adjacent(1)) == 2
        assert sorted(graph.adjacent(1).as_array()) == [2, 3]

    def test_a_self_loop_is_recorded_twice(self):
        # add_edge adds at each end unconditionally, and for a self-loop both
        # ends are the same vertex. Worth pinning down rather than discovering.
        graph = GraphSimple()
        graph.add_edge(1, 1)
        assert len(graph.adjacent(1)) == 2

    def test_a_parallel_edge_is_kept(self):
        # A Bag, not a Set: adding the same edge twice records it twice.
        graph = GraphSimple()
        graph.add_edge(1, 2)
        graph.add_edge(1, 2)
        assert len(graph.adjacent(1)) == 2

    def test_add_vertex_without_an_edge(self):
        graph = GraphSimple()
        graph.add_vertex(7)
        assert set(graph.vertices()) == {7}
        assert len(graph.adjacent(7)) == 0

    def test_an_unknown_vertex_gives_an_empty_bag(self):
        # NOTE a deliberate divergence from the Java, which returns the map's
        # null and so hands the caller a NullPointerException in waiting.
        graph = GraphSimple()
        graph.add_edge(1, 2)
        assert len(graph.adjacent(99)) == 0

    def test_asking_about_an_unknown_vertex_does_not_create_it(self):
        graph = GraphSimple()
        graph.add_edge(1, 2)
        graph.adjacent(99)
        assert set(graph.vertices()) == {1, 2}

    def test_str(self):
        graph = GraphSimple()
        graph.add_edge(1, 2)
        assert str(graph) == \
            "{1: BagArray(items=[2], count=1), 2: BagArray(items=[1], count=1)}"


class TestGraphEdges:
    """Vertices are anything and the adjacency type is the Edge itself."""

    def test_an_empty_graph(self):
        target = GraphEdges()
        assert len(list(target.edges())) == 0
        assert len(list(target.vertices())) == 0

    def test_add_edge(self):
        target = GraphEdges()
        edge = Edge(1, 2, math.pi)
        target.add_edge(edge)
        assert len(list(target.edges())) == 1
        assert len(list(target.vertices())) == 2
        assert list(target.edges())[0] == edge

    def test_add_edge_by_vertices(self):
        # The Java spells this as an overload of addEdge; Python cannot overload,
        # so it has its own name. This is the Java's empty addEdge1 test.
        target = GraphEdges()
        target.add_edge_vertices(1, 2, math.pi)
        assert list(target.edges()) == [Edge(1, 2, math.pi)]

    def test_a_false_predicate_rejects_the_edge(self):
        target = GraphEdges()
        target.add_edge(Edge(1, 2, 1.0), lambda e: False)
        assert len(list(target.edges())) == 0
        assert len(list(target.vertices())) == 0, \
            "a rejected edge must not leave its vertices behind"

    def test_a_true_predicate_accepts_the_edge(self):
        target = GraphEdges()
        target.add_edge(Edge(1, 2, 1.0), lambda e: True)
        assert len(list(target.edges())) == 1

    def test_a_selective_predicate(self):
        target = GraphEdges()
        for attribute in [1.0, 2.0, 3.0, 4.0]:
            target.add_edge_vertices(0, int(attribute), attribute,
                                     lambda e: e.attribute > 2.0)
        assert sorted(e.attribute for e in target.edges()) == [3.0, 4.0]

    def test_an_edge_is_stored_twice_but_reported_once(self):
        # An edge is incident on both of its vertices, so it appears in both bags
        # -- and edges() reports it once all the same, by collecting an edge only
        # from the bag of the vertex Edge.get() returns. An algorithm walking the
        # graph by adjacency needs the edges AT v, not the edges written with v
        # first.
        target = GraphEdges()
        target.add_edge(Edge(1, 2, 1.0))
        assert len(list(target.edges())) == 1
        assert len(list(target.adjacent(1))) == 1
        assert len(list(target.adjacent(2))) == 1

    def test_a_self_loop_is_held_once(self):
        # A self-loop is incident on one vertex, so it belongs in one bag once,
        # and must not be reported twice by edges() either.
        target = GraphEdges()
        target.add_edge(Edge(1, 1, 1.0))
        assert len(list(target.edges())) == 1
        assert len(list(target.adjacent(1))) == 1
        assert len(list(target.vertices())) == 1

    def test_edges_gathers_from_every_vertex(self):
        target = GraphEdges()
        target.add_edge_vertices(1, 2, 1.0)
        target.add_edge_vertices(3, 4, 2.0)
        assert sorted(e.attribute for e in target.edges()) == [1.0, 2.0]

    def test_str(self):
        # This is the Java's empty toStringTest.
        target = GraphEdges()
        target.add_edge(Edge(1, 2, 1.0))
        assert str(target) == \
            "{1: BagArray(items=[1-2: 1.0], count=1), 2: BagArray(items=[1-2: 1.0], count=1)}"


class TestAbstractGraph:
    def test_graph_edges_is_an_edge_graph(self):
        # The Java says "implements EdgeGraph<V, E>"; the Python had dropped it,
        # which left EdgeGraph with no implementations at all.
        assert isinstance(GraphEdges(), EdgeGraph)

    def test_it_can_be_instantiated_although_the_java_forbids_it(self):
        # A divergence with no clean fix. Java marks the class "abstract" even
        # though it implements every method it declares; Python has no way to say
        # that -- ABCMeta refuses instantiation only while an abstract method is
        # still unimplemented, and here none is. Recorded rather than contorted
        # around, since nothing goes wrong if someone does instantiate it.
        assert AbstractGraph() is not None


class TestPosition:
    """
    The Java's Position is an interface, implemented by classes in graphs/tunnels
    and graphs/gis which are otherwise unrelated. Here it is a Protocol, so those
    classes will satisfy it without having to inherit from anything.
    """

    def test_position_xy_satisfies_it(self):
        assert isinstance(PositionXY(1.0, 2.0), Position)

    def test_anything_with_the_coordinates_satisfies_it(self):
        # The point of a Protocol: no inheritance required.
        class Building:
            def __init__(self, x, y):
                self.x = x
                self.y = y

        assert isinstance(Building(1.0, 2.0), Position)

    def test_something_without_them_does_not(self):
        assert not isinstance(object(), Position)

    def test_the_coordinates(self):
        p = PositionXY(1.5, -2.5)
        assert (p.x, p.y) == (1.5, -2.5)

    def test_it_is_immutable(self):
        import dataclasses
        with pytest.raises(dataclasses.FrozenInstanceError):
            PositionXY(1.0, 2.0).x = 3.0
