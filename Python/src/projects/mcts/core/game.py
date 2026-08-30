"""
Ported from projects/mcts/core/Game.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from .state import State


class Game(ABC):
    """
    A game: where it starts, and who moves first.
    """

    @abstractmethod
    def start(self) -> State:
        """
        :return: the starting state.
        """

    @abstractmethod
    def opener(self) -> int:
        """
        :return: the player who moves first.
        """
