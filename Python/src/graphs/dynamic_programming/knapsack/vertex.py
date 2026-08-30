"""
A vertex of a knapsack-style state graph, ported from
graphs/dynamicProgramming/knapsack/Vertex.java.
"""

from __future__ import annotations


class Vertex:
    """
    A state: which decisions have been taken, and what the bag weighs as a result.

    The id encodes the path taken so far, so distinct decisions give distinct ids.

    NOTE deliberately no __eq__ or __hash__, matching the Java, which has none
    either -- two Vertex objects are the same vertex only if they are the same
    object. That is load-bearing for the graph, which uses vertices as dictionary
    keys. Giving it value equality on the id would merge states reached by
    different routes; that would arguably be an improvement, collapsing the
    house-robber decision tree into a DAG, but it is a change of algorithm rather
    than a port and is not made here.
    """

    def __init__(self, id_: str, current_bag_weight: float) -> None:
        """
        :param id_: the decisions taken so far.
        :param current_bag_weight: the weight accumulated by those decisions.
        """
        self.id = id_
        self._current_bag_weight = current_bag_weight

    def get_id(self) -> str:
        """
        :return: the id.
        """
        return self.id

    def set_id(self, id_: str) -> None:
        """
        :param id_: the new id.
        """
        self.id = id_

    def get_current_bag_weight(self) -> float:
        """
        :return: the weight accumulated so far.
        """
        return self._current_bag_weight

    def __str__(self) -> str:
        return f"Vertex{{id='{self.id}', currentBagWeight={self._current_bag_weight}}}"

    def __repr__(self) -> str:
        return str(self)
