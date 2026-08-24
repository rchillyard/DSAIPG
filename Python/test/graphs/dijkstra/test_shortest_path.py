import pytest

from src.graphs.dijkstra.dijkstra import Dijkstra
from src.graphs.dijkstra.directed_edge import DirectedEdge
from src.graphs.dijkstra.edge_weighted_digraph import EdgeWeightedDigraph


class TestShortestPaths:
    """
    Test cases for Dijkstra's shortest-path algorithm.
    """

    def test_solve_single_vertex(self):
        """
        Test case: Verify the correct behavior of the solve method for a graph with a single vertex.
        """
        graph = EdgeWeightedDigraph(1)
        dijkstra = Dijkstra(graph)
        shortest_paths = dijkstra.shortest_paths(0)

        assert shortest_paths.dist_to(0) == pytest.approx(0.0, rel=1e-4)
        assert shortest_paths.has_path_to(0) is True
        assert list(shortest_paths.path_to(0)) == []

    def test_solve_small_graph(self):
        """
        Test case: Verify the solve method correctly calculates shortest paths for a small graph with multiple edges.
        """
        graph = EdgeWeightedDigraph(5)
        zero = 0.0
        dist01 = 2.0
        dist02 = 4.0
        dist12 = 1.0
        dist13 = 7.0
        dist24 = 3.0
        graph.add_edge(DirectedEdge(0, 1, dist01))
        graph.add_edge(DirectedEdge(0, 2, dist02))
        graph.add_edge(DirectedEdge(1, 2, dist12))
        graph.add_edge(DirectedEdge(1, 3, dist13))
        graph.add_edge(DirectedEdge(2, 4, dist24))

        dijkstra = Dijkstra(graph)
        shortest_paths = dijkstra.shortest_paths(0)

        assert shortest_paths.dist_to(0) == pytest.approx(zero, rel=1e-4)
        assert shortest_paths.dist_to(1) == pytest.approx(dist01, rel=1e-4)
        assert shortest_paths.dist_to(2) == pytest.approx(dist01 + dist12, rel=1e-4)
        assert shortest_paths.dist_to(3) == pytest.approx(dist01 + dist13, rel=1e-4)
        assert shortest_paths.dist_to(4) == pytest.approx(
            dist01 + dist12 + dist24, rel=1e-4
        )

        assert shortest_paths.has_path_to(4) is True
        path = shortest_paths.path_to(4)
        assert path is not None

    def test_solve_disconnected_graph(self):
        """
        Test case: Verify the behavior when the source vertex is disconnected from the rest of the graph.
        """
        graph = EdgeWeightedDigraph(4)
        graph.add_edge(DirectedEdge(1, 2, 5.0))
        graph.add_edge(DirectedEdge(2, 3, 10.0))

        dijkstra = Dijkstra(graph)
        shortest_paths = dijkstra.shortest_paths(0)

        assert shortest_paths.dist_to(0) == pytest.approx(0.0, rel=1e-4)
        assert shortest_paths.has_path_to(1) is False
        assert shortest_paths.path_to(1) is None

    def test_solve_graph_with_cycle(self):
        """
        Test case: Verify the solve method correctly calculates paths in a graph with a cycle.
        """
        graph = EdgeWeightedDigraph(4)
        graph.add_edge(DirectedEdge(0, 1, 1.0))
        graph.add_edge(DirectedEdge(1, 2, 2.0))
        graph.add_edge(DirectedEdge(2, 0, 3.0))
        graph.add_edge(DirectedEdge(2, 3, 4.0))

        dijkstra = Dijkstra(graph)
        shortest_paths = dijkstra.shortest_paths(0)

        assert shortest_paths.dist_to(0) == pytest.approx(0.0, rel=1e-4)
        assert shortest_paths.dist_to(1) == pytest.approx(1.0, rel=1e-4)
        assert shortest_paths.dist_to(2) == pytest.approx(3.0, rel=1e-4)
        assert shortest_paths.dist_to(3) == pytest.approx(7.0, rel=1e-4)

        assert shortest_paths.has_path_to(3) is True
        path = shortest_paths.path_to(3)
        assert path is not None

    def test_solve_invalid_vertex(self):
        """
        Test case: Verify the exception for invalid vertex validation when solving.
        """
        graph = EdgeWeightedDigraph(3)
        dijkstra = Dijkstra(graph)
        with pytest.raises(ValueError):
            dijkstra.shortest_paths(4)  # Invalid vertex, should raise ValueError.
