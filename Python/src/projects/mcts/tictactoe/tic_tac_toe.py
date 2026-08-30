"""
Ported from projects/mcts/tictactoe/TicTacToe.java.
"""

from __future__ import annotations

import time
from collections.abc import Collection
from random import Random

from src.projects.mcts.core.game import Game
from src.projects.mcts.core.move import Move
from src.projects.mcts.core.state import State

from .position import Position

#: The players, and the empty cell.
X = 1
O = 0  # noqa: E741  the game's own name for the player, as in the Java
BLANK = -1


def starting_position() -> Position:
    """
    :return: the empty board, with nobody having moved.
    """
    return Position.parse_position(". . .\n. . .\n. . .", BLANK)


class TicTacToeMove(Move):
    """
    A move: which player, and where.
    """

    def __init__(self, player: int, i: int, j: int) -> None:
        """
        :param player: the player moving.
        :param i: the row.
        :param j: the column.
        """
        self._player = player
        self.i = i
        self.j = j

    def player(self) -> int:
        """
        :return: the player making this move.
        """
        return self._player

    def move(self) -> list[int]:
        """
        :return: the coordinates, as [row, column].
        """
        return [self.i, self.j]

    def __eq__(self, other: object) -> bool:
        return (isinstance(other, TicTacToeMove)
                and (self._player, self.i, self.j) == (other._player, other.i, other.j))

    def __hash__(self) -> int:
        return hash((self._player, self.i, self.j))

    def __str__(self) -> str:
        return f"TicTacToeMove{{player={self._player}, {self.i}, {self.j}}}"


class TicTacToeState(State):
    """
    A position in a game of tic-tac-toe.

    NOTE the Java makes this an inner class of TicTacToe, so that it reaches the
    game's random source through TicTacToe.this. Python has no equivalent, so the
    game is held explicitly -- which is what an inner class does behind the scenes.
    """

    def __init__(self, game: TicTacToe, position: Position | None = None) -> None:
        """
        :param game: the game this is a state of.
        :param position: the board; the starting one if not given.
        """
        self._game = game
        self.position = position if position is not None else starting_position()

    def game(self) -> TicTacToe:
        """
        :return: the game this is a state of.
        """
        return self._game

    def player(self) -> int:
        """
        :return: whose turn it is: X opens, and the two alternate.
        """
        match self.position.last:
            case 0 | -1:
                return X
            case 1:
                return O
            case _:
                return BLANK

    def winner(self) -> int | None:
        """
        :return: the winning player, or None if there is not one.
        """
        return self.position.winner()

    def random(self) -> Random:
        """
        :return: the game's source of randomness.
        """
        return self._game.random

    def moves(self, player: int) -> Collection[Move]:
        """
        :param player: the player to move.
        :return: every move available to them.
        :raises RuntimeError: if that player has just moved.
        """
        if player == self.position.last:
            raise RuntimeError(f"consecutive moves by same player: {player}")
        return [TicTacToeMove(player, i, j) for i, j in self.position.moves(player)]

    def next(self, move: Move) -> State:
        """
        :param move: the move to make.
        :return: the state it leads to.
        """
        i, j = move.move()
        return TicTacToeState(self._game, self.position.move(move.player(), i, j))

    def is_terminal(self) -> bool:
        """
        :return: whether the board is full or somebody has won.
        """
        return self.position.full() or self.position.winner() is not None

    def __str__(self) -> str:
        return f"TicTacToe{{\n{self.position}\n}}"


class TicTacToe(Game):
    """
    Noughts and crosses, as the team project's worked example of a Game.
    """

    def __init__(self, seed: int | None = None, random: Random | None = None) -> None:
        """
        NOTE Python cannot overload, so the Java's three constructors are one
        signature here.

        :param seed: the seed for the source of randomness; the current time in
                     milliseconds if not given.
        :param random: an existing source, used in place of a seed.
        """
        if random is not None:
            self.random = random
        else:
            self.random = Random(seed if seed is not None else int(time.time() * 1000))

    def opener(self) -> int:
        """
        :return: X, who always opens.
        """
        return X

    def start(self) -> State:
        """
        :return: the starting state.
        """
        return TicTacToeState(self)

    def run_game(self) -> State:
        """
        Play a whole game, both sides moving at random.

        :return: the final state.
        """
        state = self.start()
        player = self.opener()
        while not state.is_terminal():
            state = state.next(state.choose_move(player))
            player = 1 - player
        return state
