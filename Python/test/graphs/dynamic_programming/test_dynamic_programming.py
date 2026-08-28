import pytest

from src.graphs.dag.di_graph import DiGraph
from src.graphs.dag.edge import Edge
from src.graphs.dynamic_programming.coins.coin_changer import CoinChanger
from src.graphs.dynamic_programming.house_robber.house_robber import solve_house_robber
from src.graphs.dynamic_programming.knapsack.bellman_ford import bellman_ford
from src.graphs.dynamic_programming.knapsack.vertex import Vertex
from src.graphs.dynamic_programming.lucas.fibonacci import Fibonacci
from src.graphs.dynamic_programming.lucas.lucas import Lucas
from src.graphs.dynamic_programming.lucas.pell import Pell


class TestLucasFamily:
    """
    The Java has these as three classes, each with its own list and its own copy
    of the same loop. They differ only in their two seeds and in whether the
    previous term is doubled, so the Python has one Recurrence and three thin
    subclasses.
    """

    def test_fibonacci(self):
        assert [Fibonacci().get(n) for n in range(8)] == [1, 1, 2, 3, 5, 8, 13, 21]

    def test_lucas(self):
        assert [Lucas().get(n) for n in range(8)] == [2, 1, 3, 4, 7, 11, 18, 29]

    def test_pell(self):
        assert [Pell().get(n) for n in range(12)] == \
               [0, 1, 2, 5, 12, 29, 70, 169, 408, 985, 2378, 5741]

    def test_pell_larger(self):
        assert Pell().get(38) == 124145519261542
        assert Pell().get(50) == 4866752642924153522

    def test_pell_beyond_a_64_bit_long(self):
        # The Java's PellTest used to assert get(90) == 7052354271195710746, which
        # is NOT the 90th Pell number but what a long holds once that value has
        # wrapped. Both trees use arbitrary precision now and assert this value.
        assert Pell().get(90) == 9960168529794442859224531878561050
        assert Pell().get(90) > 2 ** 63 - 1

    def test_fibonacci_beyond_a_32_bit_int(self):
        # The Java used to store these in an ArrayList<Integer>, so it wrapped at
        # n = 47 while its test stopped at get(7). BigInteger now, in both trees.
        assert Fibonacci().get(47) == 4807526976
        assert Fibonacci().get(47) > 2 ** 31 - 1

    @pytest.mark.parametrize("cls", [Fibonacci, Lucas, Pell])
    def test_a_negative_index_is_rejected(self, cls):
        with pytest.raises(ValueError, match="negative"):
            cls().get(-1)

    def test_memoisation_does_not_change_the_answer(self):
        # get extends the table and keeps it, so a second call is free; the answer
        # must be the same either way.
        pell = Pell()
        assert pell.get(20) == Pell().get(20)
        assert pell.get(5) == 29, "asking for a smaller term afterwards still works"

    @pytest.mark.parametrize("cls", [Fibonacci, Lucas, Pell])
    def test_the_naive_version_agrees_with_the_memoised_one(self, cls):
        # `bad` is exponential, which is the lesson; small n only.
        instance = cls()
        for n in range(12):
            assert instance.bad(n) == instance.get(n)


class TestCoinChanger:
    """The three cases from CoinChangerTest."""

    def test_minimum_coins_0(self):
        assert CoinChanger([1, 2, 5, 7, 9]).minimum_coins(100) == 12

    def test_minimum_coins_1(self):
        assert CoinChanger([1, 11, 13, 15]).minimum_coins(40) == 4

    def test_minimum_coins_2(self):
        assert CoinChanger([3, 6, 9, 2, 11]).minimum_coins(82) == 8

    def test_zero_needs_no_coins(self):
        assert CoinChanger([1, 5]).minimum_coins(0) == 0

    def test_an_exact_denomination(self):
        assert CoinChanger([1, 5, 10]).minimum_coins(10) == 1

    def test_greedy_would_get_this_wrong(self):
        # 6 = 3 + 3, two coins. Greedy takes 4 first and then needs two 1s: three
        # coins. Worth having, because it is the reason to do this by dynamic
        # programming at all.
        assert CoinChanger([1, 3, 4]).minimum_coins(6) == 2


class TestHouseRobber:
    """The six cases from HouseRobberTest."""

    @pytest.mark.parametrize("values,expected", [
        ([], 0.0),
        ([1, 2, 3, 1], 4.0),
        ([2, 7, 9, 3, 1], 12.0),
        ([5, 3, 4, 11, 2], 16.0),
        ([5, 1, 1, 1, 1, 11, 2], 17.0),
        ([2, 12, 9, 3, 4], 16.0),
    ])
    def test_the_java_cases(self, values, expected):
        assert solve_house_robber([float(v) for v in values]) == expected

    def test_a_single_house(self):
        assert solve_house_robber([7.0]) == 7.0

    def test_two_houses_takes_the_better(self):
        assert solve_house_robber([3.0, 8.0]) == 8.0

    def test_adjacent_houses_cannot_both_be_taken(self):
        # 10 + 10 would be 20 if adjacency were allowed; the rule forces 10 + 1.
        assert solve_house_robber([10.0, 10.0, 1.0]) == 11.0


class TestBellmanFord:
    def graph_of(self, edges):
        vertices = {}
        graph: DiGraph = DiGraph()
        for a, b, w in edges:
            for name in (a, b):
                vertices.setdefault(name, Vertex(name, 0))
            graph.add_edge(Edge(vertices[a], vertices[b], float(w)))
        return graph, vertices

    def test_the_java_case(self):
        graph, v = self.graph_of([("A", "B", -1), ("A", "C", 4), ("B", "E", 2),
                                  ("B", "D", 2), ("B", "C", 3), ("D", "B", 1),
                                  ("D", "C", 5), ("E", "D", -3)])
        assert bellman_ford(graph, v["A"], v["E"]) == 1.0
        assert bellman_ford(graph, v["A"], v["B"]) == -1.0
        assert bellman_ford(graph, v["A"], v["C"]) == 2.0
        assert bellman_ford(graph, v["A"], v["D"]) == -2.0
        assert bellman_ford(graph, v["A"], v["A"]) == 0.0

    def test_negative_weights(self):
        # The reason for Bellman-Ford rather than Dijkstra: both applications here
        # negate a value so that the shortest path is the most valuable one.
        graph, v = self.graph_of([("A", "B", 5), ("A", "C", 10), ("B", "C", -20)])
        assert bellman_ford(graph, v["A"], v["C"]) == -15.0

    def test_an_unreachable_target_is_reported(self):
        # The Java returned the map lookup directly, so this was a
        # NullPointerException while unboxing, out of a method returning double.
        graph, v = self.graph_of([("A", "B", 1)])
        island = Vertex("Z", 0)
        graph.add_vertex(island)
        with pytest.raises(ValueError, match="not reachable"):
            bellman_ford(graph, v["A"], island)

    def test_an_edge_needs_no_prior_add_vertex(self):
        # The Java's own Graph could not do this: addEdge used
        # getOrDefault(u, new LinkedList<>()), which returns the default WITHOUT
        # putting it in the map, so the edge was added to a throwaway list and
        # lost -- while E++ still counted it.
        a, b = Vertex("A", 0), Vertex("B", 0)
        graph: DiGraph = DiGraph()
        graph.add_edge(Edge(a, b, 3.0))
        assert len(list(graph.edges())) == 1
        assert bellman_ford(graph, a, b) == 3.0


class TestVertex:
    def test_it_carries_its_state(self):
        v = Vertex("01", 7.5)
        assert v.get_id() == "01"
        assert v.get_current_bag_weight() == 7.5

    def test_two_vertices_with_the_same_id_are_different(self):
        # Identity semantics, matching the Java, which has no equals either.
        # Value equality would merge states reached by different routes -- an
        # improvement, arguably, but a change of algorithm rather than a port.
        assert Vertex("A", 0) != Vertex("A", 0)

    def test_str(self):
        assert str(Vertex("A", 1.0)) == "Vertex{id='A', currentBagWeight=1.0}"
