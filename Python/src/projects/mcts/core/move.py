"""
Ported from projects/mcts/core/Move.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod


class Move(ABC):
    """
    One move in a game, and whose it is.
    """

    @abstractmethod
    def player(self) -> int:
        """
        :return: the player making this move.
        """
