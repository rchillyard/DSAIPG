"""
Bellman-Ford shortest paths, ported from
graphs/dynamicProgramming/knapsack/BellmanFord.java.
"""

from __future__ import annotations

from src.graphs.dag.di_graph import DiGraph
from src.graphs.dynamic_programming.knapsack.vertex import Vertex


def bellman_ford(graph: DiGraph, source: Vertex, target: Vertex) -> float:
    """
    The length of the shortest path from source to target.

    Relax every edge, |V| - 1 times. After k passes every shortest path using k
    edges or fewer has been found, and in a graph without negative cycles no
    shortest path uses more than |V| - 1 edges, so that many passes suffice.

    Unlike Dijkstra it copes with negative weights, which is exactly why it is
    used here: both the knapsack and the house-robber formulations give an edge a
    NEGATIVE weight for the value gained, so that the shortest path is the most
    valuable choice.

    NOTE the Java ran this on a Graph, Edge and Vertex declared in its own
    package -- a fourth graph implementation in a tree that already had three.
    Both trees now use DiGraph, which models it exactly. See `Deferred work.md`
    for the four defects that went with the local classes.

    :param graph: the graph, whose edge attributes are the weights.
    :param source: where to start.
    :param target: where to finish.
    :return: the total weight of the shortest path.
    :raises ValueError: if target cannot be reached from source.
    """
    shortest: dict[Vertex, float] = {source: 0.0}
    all_edges = list(graph.edges())
    for _ in range(1, len(list(graph.vertices()))):
        for edge in all_edges:
            from_distance = shortest.get(edge.get_from())
            if from_distance is None:
                continue  # not reached yet
            candidate = from_distance + edge.get_attributes()
            to_distance = shortest.get(edge.get_to())
            if to_distance is None or candidate < to_distance:
                shortest[edge.get_to()] = candidate
    result = shortest.get(target)
    if result is None:
        raise ValueError(f"bellman_ford: {target} is not reachable from {source}")
    return result
