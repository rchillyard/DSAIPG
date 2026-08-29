"""
Ported from projects/mcts/core/Node.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import Collection

from .state import State


class Node(ABC):
    """
    A node of the search tree: a state, the nodes reachable from it, and how the
    playouts through it have turned out.
    """

    @abstractmethod
    def is_leaf(self) -> bool:
        """
        :return: whether the game has ended at this node.
        """

    @abstractmethod
    def state(self) -> State:
        """
        :return: the state this node stands for.
        """

    @abstractmethod
    def white(self) -> bool:
        """
        :return: whether it is the opening player's turn here.
        """

    @abstractmethod
    def children(self) -> Collection[Node]:
        """
        :return: the nodes reachable in one move.
        """

    @abstractmethod
    def back_propagate(self) -> None:
        """
        Recompute this node's wins and playouts from its children.
        """

    @abstractmethod
    def add_child(self, state: State) -> None:
        """
        :param state: the state to add as a child of this node.
        """

    @abstractmethod
    def wins(self) -> int:
        """
        :return: how many playouts through this node were won.
        """

    @abstractmethod
    def playouts(self) -> int:
        """
        :return: how many playouts have gone through this node.
        """

    def explore(self) -> None:
        """
        Expand this node: add a child for every move available, then work the
        results back up.

        :raises RuntimeError: if it has been explored already.
        """
        if self.is_leaf():
            return
        if not self.children():
            self._add_children(self.state())
            self.back_propagate()
        else:
            raise RuntimeError(f"exploration done already for {self}")

    def _add_children(self, state: State) -> None:
        """
        :param state: the state whose moves become this node's children.
        """
        for move in state.move_iterator(state.player()):
            self.add_child(state.next(move))
