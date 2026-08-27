from random import Random

import pytest

from src.graphs.dag.dag_impl import DAGImpl
from src.graphs.dag.di_graph import DiGraph, Kernel
from src.graphs.dag.edge import Edge
from src.graphs.undirected.edge import Edge as UndirectedEdge

# NOTE the Java tests here pin exact traversal orders -- DAGTest.testSorted
# expects 3, 6, 0, 5, 2, 1, 4 -- which depend on java.util.Random seeded with 0L
# driving Bag_Array's UnorderedIterator. Python's generator is not Java's, so
# those sequences cannot be reproduced, and pinning Python's own would be
# asserting an accident. These check the defining PROPERTY instead: that every
# edge points forwards in the order returned. That is what a topological sort
# promises, and it holds whatever the bags happen to do.
#
# Two of the Java tests, testDag2 and testReverse, are commented out upstream
# with "this fails because bags are iterated randomly now" -- the same problem,
# left unsolved. The property form below is the fix they wanted.

STANDARD_EDGES = [(0, 1), (0, 2), (0, 5), (1, 4), (3, 2), (3, 4),
                  (3, 5), (3, 6), (5, 2), (6, 0), (6, 4)]

# The Java's DiGraphTest graph: A->B->C->A is a cycle, D<->E is another, F alone.
#      /------->---------D------->------F
#     A--->B           ^  |
#      <-   |          | ->
#       \   >           E
#        ---C
CYCLIC_EDGES = [("A", "B", 1), ("B", "C", 2), ("C", "A", 3), ("A", "D", 4),
                ("D", "E", 5), ("E", "D", 6), ("D", "F", 7)]


def standard_dag(seed: int = 0) -> DAGImpl:
    """The seven-vertex DAG the Java tests use throughout."""
    target: DAGImpl = DAGImpl(Random(seed))
    for from_, to in STANDARD_EDGES:
        target.add_edge_vertices(from_, to, 1.0)
    return target


def cyclic_digraph() -> DiGraph:
    graph: DiGraph = DiGraph()
    for from_, to, weight in CYCLIC_EDGES:
        graph.add_edge(Edge(from_, to, weight))
    return graph


class TestEdge:
    def test_the_ends_and_the_attribute(self):
        target = Edge(1, 2, "hello")
        assert target.get_from() == 1
        assert target.get_to() == 2
        assert target.get_attributes() == "hello"

    def test_direction_matters(self):
        # The defining difference from graphs.undirected.Edge, where these two
        # would be equal.
        assert Edge(1, 2, "x") != Edge(2, 1, "x")
        assert UndirectedEdge(1, 2, "x") == UndirectedEdge(2, 1, "x")

    def test_reverse(self):
        assert Edge(1, 2, "x").reverse() == Edge(2, 1, "x")

    def test_reverse_twice_is_the_original(self):
        target = Edge(1, 2, "x")
        assert target.reverse().reverse() == target

    def test_reverse_keeps_the_attribute(self):
        assert Edge(1, 2, "x").reverse().get_attributes() == "x"

    def test_equality_and_hashing(self):
        # NOTE the Java Edge has no equals, so its edges compare by identity and
        # this would fail there. See the class docstring.
        assert Edge(1, 2, "x") == Edge(1, 2, "x")
        assert len({Edge(1, 2, "x"), Edge(1, 2, "x")}) == 1

    def test_the_attribute_is_part_of_identity(self):
        assert Edge(1, 2, "x") != Edge(1, 2, "y")

    def test_str(self):
        assert str(Edge(1, 2, 3.0)) == "3.0: 1->2"

    def test_a_self_loop(self):
        target = Edge(1, 1, "x")
        assert target.get_from() == target.get_to()
        assert target.reverse() == target


class TestDiGraph:
    def test_an_empty_graph(self):
        target: DiGraph = DiGraph()
        assert len(list(target.edges())) == 0
        assert len(list(target.vertices())) == 0

    def test_add_edge(self):
        target: DiGraph = DiGraph()
        edge = Edge("A", "B", 3)
        target.add_edge(edge)
        assert list(target.edges()) == [edge]
        assert set(target.vertices()) == {"A", "B"}

    def test_adjacent_holds_the_outgoing_edge(self):
        target: DiGraph = DiGraph()
        edge = Edge("A", "B", 3)
        target.add_edge(edge)
        assert list(target.adjacent("A")) == [edge]

    def test_the_destination_gets_a_vertex_but_no_edge(self):
        # An edge is recorded once, at the vertex it leaves. B is a vertex, but
        # nothing leads away from it.
        target: DiGraph = DiGraph()
        target.add_edge(Edge("A", "B", 3))
        assert len(list(target.adjacent("B"))) == 0
        assert "B" in set(target.vertices())

    def test_add_edge_vertices(self):
        target: DiGraph = DiGraph()
        target.add_edge_vertices("A", "B", 3)
        assert list(target.edges()) == [Edge("A", "B", 3)]

    def test_reverse(self):
        target = cyclic_digraph()
        reversed_ = target.reverse()
        assert {(e.to, e.from_) for e in target.edges()} \
               == {(e.from_, e.to) for e in reversed_.edges()}

    def test_reverse_keeps_every_vertex(self):
        target = cyclic_digraph()
        assert set(target.reverse().vertices()) == set(target.vertices())

    def test_reverse_keeps_a_vertex_with_no_edges(self):
        # The case reverse() used to drop, since it rebuilt from the edges alone.
        target: DiGraph = DiGraph()
        target.add_edge(Edge("A", "B", 1))
        target.add_vertex("Z")
        assert set(target.reverse().vertices()) == {"A", "B", "Z"}

    def test_reverse_of_a_graph_with_no_edges(self):
        target: DiGraph = DiGraph()
        target.add_vertex("A")
        target.add_vertex("B")
        reversed_ = target.reverse()
        assert set(reversed_.vertices()) == {"A", "B"}
        assert len(list(reversed_.edges())) == 0

    def test_reverse_twice_gives_the_original_edges(self):
        target = cyclic_digraph()
        assert set(target.reverse().reverse().edges()) == set(target.edges())

    def test_str(self):
        target: DiGraph = DiGraph()
        target.add_edge(Edge("A", "B", 3))
        assert str(target) == \
            "{'A': BagArray(items=[3: A->B], count=1), 'B': BagArray(items=[], count=0)}"


class TestKernelDAG:
    """
    Collapsing the strongly connected components. The test graph has three:
    {A, B, C} and {D, E} are cycles, and F stands alone.
    """

    def test_it_finds_the_three_components(self):
        kernels = list(cyclic_digraph().kernel_dag().vertices())
        assert len(kernels) == 3
        assert {frozenset(k.vertices) for k in kernels} \
               == {frozenset("ABC"), frozenset("DE"), frozenset("F")}

    def test_the_edges_between_components(self):
        # A->D and D->F survive; everything inside a component disappears.
        kernel_dag = cyclic_digraph().kernel_dag()
        edges = list(kernel_dag.edges())
        assert len(edges) == 2
        assert {(frozenset(e.from_.vertices), frozenset(e.to.vertices))
                for e in edges} \
               == {(frozenset("ABC"), frozenset("DE")),
                   (frozenset("DE"), frozenset("F"))}

    def test_the_result_is_acyclic(self):
        # Which is the whole point: collapsing every cycle must leave none.
        kernel_dag = cyclic_digraph().kernel_dag()
        order = list(kernel_dag.sorted())
        position = {id(k): i for i, k in enumerate(order)}
        for edge in kernel_dag.edges():
            assert position[id(edge.from_)] < position[id(edge.to)]

    def test_an_acyclic_graph_collapses_to_itself(self):
        target = standard_dag()
        kernels = list(target.kernel_dag().vertices())
        assert len(kernels) == len(list(target.vertices()))
        assert all(len(k.vertices) == 1 for k in kernels)

    def test_a_single_isolated_vertex_is_its_own_kernel(self):
        # kernel_dag walks the reversed graph, so this used to produce NO kernels
        # at all: reverse() rebuilt from edges alone and lost the vertex. It went
        # unnoticed because every test graph in this package, in both trees, has
        # an edge at every vertex.
        target: DiGraph = DiGraph()
        target.add_vertex("A")
        assert [k.vertices for k in target.kernel_dag().vertices()] == [["A"]]

    def test_an_isolated_vertex_alongside_a_component(self):
        target: DiGraph = DiGraph()
        target.add_edge(Edge("A", "B", 1))
        target.add_vertex("Z")
        kernels = list(target.kernel_dag().vertices())
        assert {frozenset(k.vertices) for k in kernels} \
               == {frozenset("A"), frozenset("B"), frozenset("Z")}


class TestKernel:
    def test_add(self):
        kernel: Kernel = Kernel()
        kernel.add("A")
        kernel.add("B")
        assert kernel.vertices == ["A", "B"]

    def test_it_can_be_built_from_a_collection(self):
        assert Kernel(["A", "B"]).vertices == ["A", "B"]

    def test_two_kernels_with_the_same_vertices_are_still_different(self):
        # kernel_dag asks whether an edge's ends landed in the SAME kernel, by
        # identity. Value equality would merge distinct components.
        assert Kernel(["A"]) != Kernel(["A"])

    def test_it_is_hashable(self):
        # It has to be: a Kernel is a vertex of the resulting DAG, so it becomes
        # a dictionary key.
        assert len({Kernel(["A"]), Kernel(["A"])}) == 2

    def test_str(self):
        assert str(Kernel(["A", "B"])) == "['A', 'B']"


class TestDAGImpl:
    def test_an_empty_dag(self):
        target: DAGImpl = DAGImpl(Random(0))
        assert len(list(target.edges())) == 0
        assert len(list(target.vertices())) == 0
        assert list(target.sorted()) == []

    def test_add_edge(self):
        target: DAGImpl = DAGImpl(Random(0))
        edge = Edge(1, 2, 3.14)
        target.add_edge(edge)
        assert list(target.edges()) == [edge]
        assert set(target.vertices()) == {1, 2}

    def test_the_standard_dag(self):
        target = standard_dag()
        assert len(list(target.edges())) == 11
        assert len(list(target.vertices())) == 7

    def test_sorted_is_a_topological_order(self):
        # The property the Java test pins an exact sequence for. Several orders
        # are valid; what matters is that every edge points forwards.
        for seed in range(5):
            target = standard_dag(seed)
            order = list(target.sorted())
            position = {v: i for i, v in enumerate(order)}
            for edge in target.edges():
                assert position[edge.from_] < position[edge.to], \
                    f"edge {edge} points backwards in {order}"

    def test_sorted_visits_every_vertex_once(self):
        target = standard_dag()
        order = list(target.sorted())
        assert sorted(order) == [0, 1, 2, 3, 4, 5, 6]

    def test_sorted_includes_an_isolated_vertex(self):
        target = standard_dag()
        target.add_vertex(99)
        assert 99 in list(target.sorted())

    def test_dfs_reaches_everything_reachable(self):
        # From 0 you can reach 1, 2, 4, 5 -- but not 3 or 6, which lead to 0.
        target = standard_dag()
        seen: list[int] = []
        target.dfs(0, seen.append, None)
        assert sorted(seen) == [0, 1, 2, 4, 5]

    def test_dfs_visits_each_vertex_once(self):
        target = standard_dag()
        seen: list[int] = []
        target.dfs(0, seen.append, None)
        assert len(seen) == len(set(seen))

    def test_dfs_pre_comes_before_post(self):
        target = standard_dag()
        events: list[tuple[str, int]] = []
        target.dfs(0, lambda v: events.append(("pre", v)),
                   lambda v: events.append(("post", v)))
        for _, vertex in events:
            pre = events.index(("pre", vertex))
            post = events.index(("post", vertex))
            assert pre < post

    def test_dfs_finishes_descendants_first(self):
        # The property that makes reverse post-order a topological order: a
        # vertex's post fires only after the post of everything below it.
        target = standard_dag()
        post_order: list[int] = []
        target.dfs(0, None, post_order.append)
        assert post_order[-1] == 0, "the starting vertex must finish last"

    def test_dfs_from_an_unknown_vertex(self):
        # NOTE the Java reads the adjacency map directly here and would raise a
        # NullPointerException. This follows the rest of the Python tree in
        # treating an unknown vertex as one with no edges.
        target = standard_dag()
        seen: list[int] = []
        target.dfs(99, seen.append, None)
        assert seen == [99]

    def test_a_dfs_needs_something_to_do(self):
        target = standard_dag()
        with pytest.raises(ValueError, match="cannot both be None"):
            target.dfs(0, None, None)

    def test_sorted_on_a_cyclic_graph_still_returns_every_vertex(self):
        # Nothing checks acyclicity, so this returns an order -- just not a
        # topological one, since none exists. The Java's testSortedWithCycle is
        # commented out for exactly this reason.
        target = standard_dag()
        target.add_edge_vertices(4, 3, 1.0)
        assert sorted(target.sorted()) == [0, 1, 2, 3, 4, 5, 6]
