"""
Ported from projects/mcts/tictactoe/TicTacToeNode.java.
"""

from __future__ import annotations

from collections.abc import Collection

from src.projects.mcts.core.node import Node
from src.projects.mcts.core.state import State


class TicTacToeNode(Node):
    """
    A node of the search tree for tic-tac-toe.

    A leaf is scored as it is created: 2 for a win, 1 for a draw. Every other node
    takes its score from its children, which is what back_propagate does.
    """

    def __init__(self, state: State) -> None:
        """
        :param state: the state this node stands for.
        """
        self._state = state
        self._children: list[Node] = []
        self._wins = 0
        self._playouts = 0
        self._initialize_node_data()

    def is_leaf(self) -> bool:
        """
        :return: whether the game has ended here.
        """
        return self.state().is_terminal()

    def state(self) -> State:
        """
        :return: the state this node stands for.
        """
        return self._state

    def white(self) -> bool:
        """
        :return: whether it is the opening player's turn here.
        """
        return self._state.player() == self._state.game().opener()

    def children(self) -> Collection[Node]:
        """
        :return: the nodes reachable in one move.
        """
        return self._children

    def add_child(self, state: State) -> None:
        """
        :param state: the state to add as a child.
        """
        self._children.append(TicTacToeNode(state))

    def back_propagate(self) -> None:
        """
        Recompute this node's wins and playouts from its children.
        """
        self._playouts = 0
        self._wins = 0
        for child in self._children:
            self._wins += child.wins()
            self._playouts += child.playouts()

    def wins(self) -> int:
        """
        :return: how many playouts through this node were won.
        """
        return self._wins

    def playouts(self) -> int:
        """
        :return: how many playouts have gone through this node.
        """
        return self._playouts

    def _initialize_node_data(self) -> None:
        """
        Score a leaf: one playout, worth 2 for a win and 1 for a draw.
        """
        if self.is_leaf():
            self._playouts = 1
            self._wins = 2 if self._state.winner() is not None else 1
