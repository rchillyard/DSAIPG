"""
Ported from projects/mcts/tictactoe/MCTS.java.

This is the team project. The scaffolding around it -- Game, Move, Node, State,
RandomState, TicTacToe, Position, TicTacToeNode -- is given; the search is not.
"""

from __future__ import annotations

from src.projects.mcts.core.node import Node

from .tic_tac_toe import TicTacToe, TicTacToeState
from .tic_tac_toe_node import TicTacToeNode


class MCTS:
    """
    Monte Carlo tree search over a game of tic-tac-toe.
    """

    def __init__(self, root: Node) -> None:
        """
        :param root: the node to search from.
        """
        self.root = root


def main() -> None:
    """
    Run the search from the empty board.
    """
    mcts = MCTS(TicTacToeNode(TicTacToeState(TicTacToe())))
    root = mcts.root  # noqa: F841  the search starts here
    # This is where you process the MCTS to try to win the game.


if __name__ == "__main__":
    main()
