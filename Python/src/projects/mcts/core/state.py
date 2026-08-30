"""
Ported from projects/mcts/core/State.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import Collection, Iterator
from random import Random

from src.adt.bqs.unordered_iterator import UnorderedIterator

from .game import Game
from .move import Move


class State(ABC):
    """
    A position in a game, together with everything needed to carry on from it.
    """

    @abstractmethod
    def game(self) -> Game:
        """
        :return: the game this is a state of.
        """

    @abstractmethod
    def is_terminal(self) -> bool:
        """
        :return: whether the game has ended here, won or drawn.
        """

    @abstractmethod
    def player(self) -> int:
        """
        :return: the player whose turn it is.
        """

    @abstractmethod
    def winner(self) -> int | None:
        """
        NOTE the Java returns Optional<Integer>; None is the Python spelling of an
        empty Optional.

        :return: the winning player, or None if there is not one -- which covers
                 both a draw and a game still in progress.
        """

    @abstractmethod
    def random(self) -> Random:
        """
        :return: the source of randomness, so that a game can be repeated.
        """

    @abstractmethod
    def moves(self, player: int) -> Collection[Move]:
        """
        :param player: the player to move.
        :return: every move available to them here.
        """

    @abstractmethod
    def next(self, move: Move) -> State:
        """
        :param move: the move to make.
        :return: the state it leads to.
        """

    def move_iterator(self, player: int) -> Iterator[Move]:
        """
        The available moves, in a random but repeatable order.

        :param player: the player to move.
        :return: an iterator over their moves.
        :raises RuntimeError: if moves returns None.
        """
        moves = self.moves(player)
        if moves is None:
            raise RuntimeError("moves returned null")
        return UnorderedIterator(moves, self.random())

    def choose_move(self, player: int) -> Move:
        """
        :param player: the player to move.
        :return: one of their moves, chosen at random.
        :raises RuntimeError: if there are no moves to choose from.
        """
        iterator = self.move_iterator(player)
        for move in iterator:
            return move
        raise RuntimeError("empty move iterator")
