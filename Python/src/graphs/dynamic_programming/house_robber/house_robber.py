"""
The house-robber problem, ported from
graphs/dynamicProgramming/houseRobber/houseRobber.java.
"""

from __future__ import annotations

from src.graphs.dag.di_graph import DiGraph
from src.graphs.dag.edge import Edge
from src.graphs.dynamic_programming.knapsack.bellman_ford import bellman_ford
from src.graphs.dynamic_programming.knapsack.vertex import Vertex


def solve_house_robber(house_values: list[float]) -> float:
    """
    The largest total obtainable without robbing two adjacent houses.

    The graph is the decision tree. At each house a path either skips it, at no
    cost, or takes it, at a cost of MINUS its value -- so the shortest path
    through the tree is the most valuable selection, and Bellman-Ford finds it
    because it tolerates negative weights.

    A vertex's id records the decisions so far as a string of 0s and 1s, and the
    no-two-adjacent rule is enforced by refusing to append a 1 to an id which
    already ends in one.

    :param house_values: the value of each house, in a row.
    :return: the best total.
    """
    source = Vertex("0", 0)
    target = Vertex("9999", 0)
    graph: DiGraph = DiGraph()
    graph.add_vertex(source)
    graph.add_vertex(target)
    _build_the_graph(graph, house_values, source, target, 0)
    # abs, because the weights are negated so that "shortest" means "most valuable"
    return abs(bellman_ford(graph, source, target))


def _build_the_graph(graph: DiGraph, house_values: list[float],
                     source: Vertex, target: Vertex, counter: int) -> DiGraph:
    """
    Build the decision tree below source, recursively.

    :param graph: the graph to add to.
    :param house_values: the values.
    :param source: the decisions reached so far.
    :param target: the vertex every complete decision leads to.
    :param counter: which house is being decided.
    :return: the graph.
    """
    if counter >= len(house_values):
        graph.add_edge(Edge(source, target, 0.0))
        return graph
    skip = Vertex(source.get_id() + "0", source.get_current_bag_weight())
    graph.add_edge(Edge(source, skip, 0.0))
    _build_the_graph(graph, house_values, skip, target, counter + 1)
    if not source.get_id().endswith("1"):
        take = Vertex(source.get_id() + "1",
                      source.get_current_bag_weight() + house_values[counter])
        graph.add_edge(Edge(source, take, -house_values[counter]))
        _build_the_graph(graph, house_values, take, target, counter + 1)
    return graph
