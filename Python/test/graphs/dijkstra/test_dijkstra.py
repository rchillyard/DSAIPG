from pytest import approx, raises

from src.graphs.dijkstra.edge_weighted_digraph import EdgeWeightedDigraph
from src.graphs.dijkstra.dijkstra import Dijkstra
from src.graphs.dijkstra.directed_edge import DirectedEdge


def test1():
    g = EdgeWeightedDigraph(9)
    e = DirectedEdge(0, 1, 4)
    e1 = DirectedEdge(0, 7, 8)
    e2 = DirectedEdge(1, 2, 8)
    e3 = DirectedEdge(1, 7, 11)
    e4 = DirectedEdge(7, 6, 1)
    e5 = DirectedEdge(7, 8, 11)
    e6 = DirectedEdge(2, 8, 2)
    e7 = DirectedEdge(2, 3, 7)
    e8 = DirectedEdge(2, 5, 4)
    e9 = DirectedEdge(8, 6, 6)
    e10 = DirectedEdge(6, 5, 2)
    e11 = DirectedEdge(3, 5, 14)
    e12 = DirectedEdge(3, 4, 9)
    e13 = DirectedEdge(5, 4, 10)

    g.add_edge(e)
    g.add_edge(e1)
    g.add_edge(e2)
    g.add_edge(e3)
    g.add_edge(e4)
    g.add_edge(e5)
    g.add_edge(e6)
    g.add_edge(e7)
    g.add_edge(e8)
    g.add_edge(e9)
    g.add_edge(e10)
    g.add_edge(e11)
    g.add_edge(e12)
    g.add_edge(e13)

    dijkstra = Dijkstra(g)
    shortestPaths = dijkstra.shortest_paths(0)
    dist = []
    for i in range(g.V()):
        if shortestPaths.has_path_to(i):
            dist.append(shortestPaths.dist_to(i))
    assert dist[0] == 0.0
    assert dist[1] == 4.0
    assert dist[2] == 12.0
    assert dist[3] == 19.0
    assert dist[4] == 21.0
    assert dist[5] == 11.0
    assert dist[6] == 9.0
    assert dist[7] == 8.0
    assert dist[8] == 14.0


def test2():
    g = EdgeWeightedDigraph(8)
    e = DirectedEdge(0, 4, 0.38)
    e1 = DirectedEdge(6, 4, 0.93)
    e2 = DirectedEdge(0, 2, 0.26)
    e3 = DirectedEdge(5, 7, 0.28)
    e4 = DirectedEdge(4, 7, 0.36)
    e5 = DirectedEdge(7, 5, 0.28)
    e6 = DirectedEdge(2, 7, 0.34)
    e7 = DirectedEdge(7, 3, 0.39)
    e8 = DirectedEdge(1, 3, 0.29)
    e9 = DirectedEdge(6, 0, 0.58)
    e10 = DirectedEdge(6, 2, 0.4)
    e11 = DirectedEdge(5, 1, 0.32)
    e12 = DirectedEdge(3, 6, 0.52)
    e13 = DirectedEdge(4, 5, 0.35)
    e14 = DirectedEdge(5, 4, 0.35)

    g.add_edge(e)
    g.add_edge(e1)
    g.add_edge(e2)
    g.add_edge(e3)
    g.add_edge(e4)
    g.add_edge(e5)
    g.add_edge(e6)
    g.add_edge(e7)
    g.add_edge(e8)
    g.add_edge(e9)
    g.add_edge(e10)
    g.add_edge(e11)
    g.add_edge(e12)
    g.add_edge(e13)
    g.add_edge(e14)

    dijkstra = Dijkstra(g)
    shortestPaths = dijkstra.shortest_paths(0)
    dist = []
    for i in range(g.V()):
        if shortestPaths.has_path_to(i):
            dist.append(shortestPaths.dist_to(i))

    assert approx(dist[0], 0.2) == 0
    assert approx(dist[1], 0.2) == 1.05
    assert approx(dist[2], 0.2) == 0.26
    assert approx(dist[3], 0.1) == 0.99
    assert approx(dist[4], 0.2) == 0.38
    assert approx(dist[5], 0.2) == 0.73
    assert approx(dist[6], 0.2) == 1.51
    assert approx(dist[7], 0.2) == 0.60


def test3():
    g = EdgeWeightedDigraph(7)
    e = DirectedEdge(0, 1, 3)
    e1 = DirectedEdge(0, 3, 2)
    e2 = DirectedEdge(0, 6, 6)
    e3 = DirectedEdge(1, 2, 6)
    e4 = DirectedEdge(1, 4, 1)
    e5 = DirectedEdge(2, 5, 1)
    e6 = DirectedEdge(3, 4, 3)
    e7 = DirectedEdge(3, 1, 2)
    e8 = DirectedEdge(4, 5, 4)
    e9 = DirectedEdge(6, 5, 2)

    g.add_edge(e)
    g.add_edge(e1)
    g.add_edge(e2)
    g.add_edge(e3)
    g.add_edge(e4)
    g.add_edge(e5)
    g.add_edge(e6)
    g.add_edge(e7)
    g.add_edge(e8)
    g.add_edge(e9)

    dijkstra = Dijkstra(g)
    shortestPaths = dijkstra.shortest_paths(0)
    dist = []
    for i in range(g.V()):
        if shortestPaths.has_path_to(i):
            dist.append(shortestPaths.dist_to(i))

    assert dist[0] == 0.0
    assert dist[1] == 3.0
    assert dist[2] == 9.0
    assert dist[3] == 2.0
    assert dist[4] == 4.0
    assert dist[5] == 8.0
    assert dist[6] == 6.0


def test4():
    g = EdgeWeightedDigraph(7)
    e = DirectedEdge(0, 1, 2)
    e1 = DirectedEdge(0, 2, 6)
    e2 = DirectedEdge(1, 3, 5)
    e3 = DirectedEdge(2, 3, 8)
    e4 = DirectedEdge(3, 5, 15)
    e5 = DirectedEdge(3, 4, 10)
    e6 = DirectedEdge(5, 6, 6)
    e7 = DirectedEdge(4, 6, 2)
    e8 = DirectedEdge(4, 5, 6)

    g.add_edge(e)
    g.add_edge(e1)
    g.add_edge(e2)
    g.add_edge(e3)
    g.add_edge(e4)
    g.add_edge(e5)
    g.add_edge(e6)
    g.add_edge(e7)
    g.add_edge(e8)

    dijkstra = Dijkstra(g)
    shortestPaths = dijkstra.shortest_paths(0)
    dist = []
    for i in range(g.V()):
        if shortestPaths.has_path_to(i):
            dist.append(shortestPaths.dist_to(i))

    assert dist[0] == 0.0
    assert dist[1] == 2.0
    assert dist[2] == 6.0
    assert dist[3] == 7.0
    assert dist[4] == 17.0
    assert dist[5] == 22.0
    assert dist[6] == 19.0


def test5():
    with raises(ValueError):
        g = EdgeWeightedDigraph(4)
        e = DirectedEdge(0, 1, 2)
        e1 = DirectedEdge(0, 2, -6)
        e2 = DirectedEdge(1, 3, 5)

        g.add_edge(e)
        g.add_edge(e1)
        g.add_edge(e2)

        dijkstra = Dijkstra(g)
        dijkstra.shortest_paths(0)
