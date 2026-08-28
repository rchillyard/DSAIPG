import pytest

from src.graphs.traversal.bfs import BFS
from src.graphs.traversal.edge import Edge
from src.graphs.traversal.edge_weighted_graph import EdgeWeightedGraph
from src.graphs.traversal.graph import Graph
from src.graphs.traversal.prims import Prims

# NOTE the Java has these classes twice, in graphs/traversal and in
# graphs/generic_BFS_and_prims: StdRandom is byte-identical between them,
# EdgeWeightedGraph differs by four lines, and MinPQ and Edge are near-copies.
# The Python has one of each. StdRandom is not ported at all -- it is 578 lines
# reimplementing what `random` already does -- and MinPQ is not ported either,
# because the tree already has PriorityQueueBinaryHeap and Prims here uses heapq.


def directed(edges, order=0):
    """A Graph built with the directed convention BFS.addEdge uses."""
    g: Graph = Graph(order)
    for v, w in edges:
        g.add_directed_edge(v, w)
    return g


class TestEdge:
    def test_either_and_other(self):
        e = Edge(1, 2, 0.5)
        assert e.either() == 1
        assert e.other(1) == 2
        assert e.other(2) == 1

    def test_other_rejects_a_vertex_not_on_the_edge(self):
        with pytest.raises(ValueError, match="Illegal endpoint"):
            Edge(1, 2, 0.5).other(3)

    def test_a_self_loop(self):
        e = Edge(1, 1, 0.5)
        assert e.either() == 1
        assert e.other(1) == 1

    def test_ordering_is_by_weight_alone(self):
        assert Edge(9, 9, 1.0) < Edge(0, 0, 2.0)
        assert sorted([Edge(0, 1, 3.0), Edge(2, 3, 1.0), Edge(4, 5, 2.0)]) \
               == [Edge(2, 3, 1.0), Edge(4, 5, 2.0), Edge(0, 1, 3.0)]

    def test_negative_vertices_are_rejected(self):
        with pytest.raises(ValueError, match="non-negative"):
            Edge(-1, 2, 1.0)

    def test_nan_weight_is_rejected(self):
        with pytest.raises(ValueError, match="NaN"):
            Edge(1, 2, float("nan"))

    def test_str(self):
        assert str(Edge(1, 2, 0.5)) == "1-2 0.50000"


class TestBFS:
    """
    The three cases from BFSTest, which pin exact traversal orders. Those are
    reproducible here because the adjacency lists are ordered by insertion --
    unlike the bags in graphs/dag, whose order is deliberately random.
    """

    def test_bfs1(self):
        g = directed([(0, 1), (0, 2), (1, 2), (2, 0), (2, 3), (3, 3)], 4)
        assert BFS(g).traverse(2) == [2, 0, 3, 1]

    def test_bfs2(self):
        g = directed([(0, 1), (0, 3), (0, 4), (4, 5), (3, 5), (1, 2),
                      (1, 0), (2, 1), (4, 1), (3, 1), (5, 4), (5, 3)], 7)
        assert BFS(g).traverse(0) == [0, 1, 3, 4, 2, 5]

    def test_bfs3(self):
        g = directed([(0, 1), (0, 2), (0, 3), (2, 4), (2, 5)], 6)
        assert BFS(g).traverse(0) == [0, 1, 2, 3, 4, 5]

    def test_an_undirected_graph(self):
        g: Graph = Graph()
        for v, w in [("A", "B"), ("B", "C"), ("A", "D")]:
            g.add_edge(v, w)
        assert BFS(g).traverse("A") == ["A", "B", "D", "C"]

    def test_it_reaches_only_the_source_component(self):
        g: Graph = Graph()
        g.add_edge(1, 2)
        g.add_edge(3, 4)
        assert BFS(g).traverse(1) == [1, 2]

    def test_a_lone_vertex(self):
        g: Graph = Graph()
        g.add_vertex("A")
        assert BFS(g).traverse("A") == ["A"]

    def test_it_visits_each_vertex_once_despite_a_cycle(self):
        g: Graph = Graph()
        for v, w in [(1, 2), (2, 3), (3, 1)]:
            g.add_edge(v, w)
        seen = BFS(g).traverse(1)
        assert sorted(seen) == [1, 2, 3]

    def test_it_can_be_run_twice(self):
        # NOTE the Java's GBFS cannot: `marked` is a field, so a second call
        # returns only the source. Here the visited set is local to the traversal.
        g: Graph = Graph()
        g.add_edge("A", "B")
        bfs = BFS(g)
        assert bfs.traverse("A") == bfs.traverse("A") == ["A", "B"]


class TestGraph:
    def test_len_counts_the_vertices_actually_present(self):
        # NOTE the Java's V() returns the constructor argument, which addVertex
        # never updates -- so it reports what the caller once said rather than the
        # truth. order_declared keeps that number under an honest name.
        g: Graph = Graph(100)
        g.add_edge("A", "B")
        assert len(g) == 2
        assert g.order_declared == 100

    def test_add_edge_creates_the_vertices(self):
        # The Java throws NullPointerException unless addVertex was called first.
        g: Graph = Graph()
        g.add_edge("A", "B")
        assert sorted(g.vertices()) == ["A", "B"]

    def test_adj_of_an_unknown_vertex_is_empty(self):
        assert list(Graph().adj("nobody")) == []

    def test_edge_count(self):
        g: Graph = Graph()
        g.add_edge(1, 2)
        g.add_edge(2, 3)
        assert g.e() == 2


class TestEdgeWeightedGraph:
    def build(self):
        g = EdgeWeightedGraph(4)
        for v, w, weight in [(0, 1, 1.0), (1, 2, 2.0), (0, 2, 3.0), (2, 3, 4.0)]:
            g.add_edge(Edge(v, w, weight))
        return g

    def test_counts(self):
        g = self.build()
        assert g.v() == 4
        assert g.e() == 4

    def test_an_edge_is_recorded_at_both_ends(self):
        g = self.build()
        assert g.degree(0) == 2
        assert g.degree(3) == 1

    def test_edges_yields_each_edge_once(self):
        g = self.build()
        assert len(g.edges()) == 4

    def test_a_self_loop_is_yielded_once(self):
        # It is stored twice at its single vertex, so edges() has to take care.
        g = EdgeWeightedGraph(2)
        g.add_edge(Edge(0, 0, 1.0))
        assert g.edges() == [Edge(0, 0, 1.0)]
        assert g.degree(0) == 2

    def test_an_out_of_range_vertex_is_rejected(self):
        g = EdgeWeightedGraph(2)
        with pytest.raises(ValueError, match="not between"):
            g.add_edge(Edge(0, 5, 1.0))
        with pytest.raises(ValueError, match="not between"):
            g.degree(9)

    def test_a_negative_vertex_count_is_rejected(self):
        with pytest.raises(ValueError, match="non-negative"):
            EdgeWeightedGraph(-1)

    def test_random_edges_are_repeatable(self):
        from random import Random
        a = EdgeWeightedGraph(10, 20, Random(42))
        b = EdgeWeightedGraph(10, 20, Random(42))
        assert str(a) == str(b)
        assert a.e() == 20

    def test_copy_is_independent(self):
        g = self.build()
        copy = EdgeWeightedGraph.copy_of(g)
        assert copy.v() == g.v()
        assert copy.e() == g.e()
        assert sorted(copy.edges()) == sorted(g.edges())
        g.add_edge(Edge(0, 3, 9.0))
        assert copy.e() == 4, "the copy must not follow the original"


class TestPrims:
    def test_the_minimum_spanning_tree(self):
        g = EdgeWeightedGraph(4)
        for v, w, weight in [(0, 1, 1.0), (1, 2, 2.0), (0, 2, 3.0), (2, 3, 4.0)]:
            g.add_edge(Edge(v, w, weight))
        p = Prims(g)
        assert p.weight() == 7.0
        assert sorted(e.weight for e in p.edges()) == [1.0, 2.0, 4.0], \
            "the 3.0 edge is redundant once 0-1 and 1-2 are taken"

    def test_it_spans_every_vertex(self):
        g = EdgeWeightedGraph(5)
        for v, w, weight in [(0, 1, 2.0), (1, 2, 3.0), (2, 3, 1.0), (3, 4, 4.0), (0, 4, 9.0)]:
            g.add_edge(Edge(v, w, weight))
        p = Prims(g)
        assert len(p.edges()) == 4, "a spanning tree of n vertices has n-1 edges"
        touched = set()
        for e in p.edges():
            touched.add(e.either())
            touched.add(e.other(e.either()))
        assert touched == {0, 1, 2, 3, 4}

    def test_it_prefers_the_lighter_of_two_routes(self):
        g = EdgeWeightedGraph(3)
        g.add_edge(Edge(0, 1, 1.0))
        g.add_edge(Edge(1, 2, 1.0))
        g.add_edge(Edge(0, 2, 100.0))
        assert Prims(g).weight() == 2.0

    def test_a_disconnected_graph_gives_only_the_source_component(self):
        # RECORDED, not endorsed. The Java says "assumes G is connected" and this
        # is what that assumption costs: vertices 2 and 3 are simply not in the
        # result, with no indication that anything is missing.
        g = EdgeWeightedGraph(4)
        g.add_edge(Edge(0, 1, 1.0))
        g.add_edge(Edge(2, 3, 1.0))
        p = Prims(g)
        assert len(p.edges()) == 1
        assert p.weight() == 1.0

    def test_an_empty_graph(self):
        assert Prims(EdgeWeightedGraph(0)).edges() == []

    def test_a_single_vertex(self):
        assert Prims(EdgeWeightedGraph(1)).edges() == []

    def test_equal_weights_do_not_break_the_queue(self):
        # Edge orders on weight alone, so equal weights would make heapq compare
        # the Edges themselves if the tie-break counter were not there.
        g = EdgeWeightedGraph(4)
        for v, w in [(0, 1), (1, 2), (2, 3), (0, 3)]:
            g.add_edge(Edge(v, w, 1.0))
        assert Prims(g).weight() == 3.0
