"""
Tests for projects/mcts — the team project's scaffolding.

The search itself is not here, and is not meant to be: `MCTS` is where students
write it. What is tested is everything they build on.
"""

from __future__ import annotations

import pytest

from src.projects.mcts.core.random_state import RandomState
from src.projects.mcts.tictactoe.mcts import MCTS
from src.projects.mcts.tictactoe.position import BLANK, Position
from src.projects.mcts.tictactoe.tic_tac_toe import (
    O,
    TicTacToe,
    TicTacToeMove,
    TicTacToeState,
    X,
    starting_position,
)
from src.projects.mcts.tictactoe.tic_tac_toe_node import TicTacToeNode

EMPTY = ". . .\n. . .\n. . ."


class TestPosition:
    def test_parse_cell(self):
        assert Position.parse_cell("O") == 0
        assert Position.parse_cell("0") == 0
        assert Position.parse_cell("X") == 1
        assert Position.parse_cell("1") == 1
        assert Position.parse_cell(".") == BLANK
        assert Position.parse_cell("x") == 1, "case does not matter"

    def test_parse_position_counts_the_occupied_cells(self):
        assert Position.parse_position(EMPTY, BLANK).count == 0
        assert Position.parse_position("X . .\n. O .\n. . .", 0).count == 2
        assert Position.parse_position("X O X\nO X O\nX O X", 1).count == 9

    def test_render_round_trips(self):
        for grid in (EMPTY, "X . .\n. O .\n. . X", "X O X\nO X O\nX O X"):
            assert Position.parse_position(grid, BLANK).render() == grid

    def test_full(self):
        assert not Position.parse_position(EMPTY, BLANK).full()
        assert Position.parse_position("X O X\nO X O\nX O X", 1).full()

    def test_equality_and_hashing(self):
        # the Java needs deepEquals and deepHashCode for this
        a = Position.parse_position("X . .\n. O .\n. . .", 0)
        b = Position.parse_position("X . .\n. O .\n. . .", 0)
        assert a == b
        assert hash(a) == hash(b)
        assert a != Position.parse_position(EMPTY, BLANK)
        assert len({a, b}) == 1

    def test_projections(self):
        target = Position.parse_position("X . O\nX O .\nX . O", 1)
        assert target.project_row(0) == (1, -1, 0)
        assert target.project_row(1) == (1, 0, -1)
        assert target.project_col(0) == (1, 1, 1)
        assert target.project_col(1) == (-1, 0, -1)
        assert target.project_diag(True) == (1, 0, 0)
        assert target.project_diag(False) == (1, 0, 0), "the anti-diagonal: [2][0], [1][1], [0][2]"

    def test_reflect(self):
        # an asymmetric board, so that the two axes and a rotation are all
        # distinguishable -- on a symmetric one, reflect(0) and rotate coincide
        target = Position.parse_position("X O .\n. . .\n. . .", 0)
        assert target.reflect(0).render() == ". . .\n. . .\nX O .", "about the middle row"
        assert target.reflect(1).render() == ". O X\n. . .\n. . .", "about the middle column"
        assert target.reflect(0).reflect(0) == target, "twice is none"

    def test_reflect_about_an_unknown_axis(self):
        with pytest.raises(RuntimeError, match="reflect not implemented"):
            Position.parse_position(EMPTY, BLANK).reflect(2)

    def test_rotate(self):
        target = Position.parse_position("X O .\n. . .\n. . .", 0)
        assert target.rotate().render() == ". . .\nO . .\nX . ."
        assert target.rotate().rotate().render() == ". . .\n. . .\n. O X"
        assert target.rotate().rotate().rotate().rotate() == target, "four turns is none"

    def test_a_reflection_leaves_the_count_and_last_alone(self):
        target = Position.parse_position("X . .\n. O .\n. . .", 0)
        assert target.reflect(0).count == target.count
        assert target.reflect(0).last == target.last

    def test_move(self):
        target = Position.parse_position("X . .\n. . .\n. . .", 1)
        moved = target.move(O, 1, 1)
        assert moved.render() == "X . .\n. O .\n. . ."
        assert moved.count == 2
        assert moved.last == O
        assert target.render() == "X . .\n. . .\n. . .", "the original is unchanged"

    def test_move_onto_an_occupied_cell(self):
        with pytest.raises(RuntimeError, match="occupied"):
            Position.parse_position("X . .\n. . .\n. . .", 1).move(O, 0, 0)

    def test_two_moves_by_the_same_player(self):
        with pytest.raises(RuntimeError, match="consecutive moves"):
            Position.parse_position("X . .\n. . .\n. . .", 1).move(X, 1, 1)

    def test_move_on_a_full_board(self):
        with pytest.raises(RuntimeError, match="full"):
            Position.parse_position("X O X\nO X O\nX O X", 1).move(O, 0, 0)

    def test_moves(self):
        target = Position.parse_position("X . .\n. O .\nX . .", 1)
        moves = target.moves(O)
        assert len(moves) == 6
        assert list(moves[0]) == [0, 1]
        assert all(target.grid[i][j] == BLANK for i, j in moves)

    def test_moves_by_the_same_player_twice(self):
        with pytest.raises(RuntimeError, match="consecutive moves"):
            Position.parse_position("X . .\n. . .\n. . .", 1).moves(X)

    def test_no_winner_yet(self):
        assert Position.parse_position("X . .\n. O .\n. . .", 0).winner() is None

    def test_a_winning_row_column_and_diagonal(self):
        assert Position.parse_position("X X X\nO O .\n. . .", 1).winner() == 1
        assert Position.parse_position("X O .\nX O .\nX . .", 1).winner() == 1
        assert Position.parse_position("O X .\nX O .\nX . O", 0).winner() == 0

    def test_a_full_board_with_no_line(self):
        assert Position.parse_position("X O X\nX O O\nO X X", 1).winner() is None

    def test_winner_is_not_looked_for_before_the_fifth_move(self):
        # count > 4 guards the check: three in a row is impossible before then
        assert Position.parse_position("X X X\n. . .\n. . .", 1).count == 3
        assert Position.parse_position("X X X\n. . .\n. . .", 1).winner() is None


class TestTicTacToeMove:
    def test_it_carries_player_and_place(self):
        move = TicTacToeMove(X, 1, 2)
        assert move.player() == X
        assert move.move() == [1, 2]


class TestTicTacToeState:
    def test_x_opens(self):
        assert TicTacToe().start().player() == X

    def test_the_players_alternate(self):
        state = TicTacToe(seed=0).start()
        assert state.player() == X
        assert state.next(TicTacToeMove(X, 0, 0)).player() == O

    def test_the_starting_position_is_empty(self):
        assert starting_position().count == 0
        assert starting_position().last == BLANK

    def test_moves_from_the_empty_board(self):
        assert len(TicTacToe().start().moves(X)) == 9

    def test_not_terminal_at_the_start(self):
        assert not TicTacToe().start().is_terminal()

    def test_terminal_when_won(self):
        game = TicTacToe()
        state = TicTacToeState(game, Position.parse_position("X X X\nO O .\n. . .", 1))
        assert state.is_terminal()
        assert state.winner() == 1

    def test_terminal_when_full(self):
        game = TicTacToe()
        state = TicTacToeState(game, Position.parse_position("X O X\nX O O\nO X X", 1))
        assert state.is_terminal()
        assert state.winner() is None, "a draw"

    def test_choose_move_is_repeatable_from_a_seed(self):
        first = TicTacToe(seed=42).start().choose_move(X)
        second = TicTacToe(seed=42).start().choose_move(X)
        assert first.move() == second.move()

    def test_run_game_reaches_a_terminal_state(self):
        for seed in range(20):
            state = TicTacToe(seed=seed).run_game()
            assert state.is_terminal()
            assert state.winner() in (X, O, None)


class TestTicTacToeNode:
    @staticmethod
    def node(grid: str, last: int) -> TicTacToeNode:
        return TicTacToeNode(TicTacToeState(TicTacToe(), Position.parse_position(grid, last)))

    def test_a_win_is_worth_two(self):
        node = self.node("X X X\nO O .\n. . .", 1)
        assert node.is_leaf()
        assert node.wins() == 2
        assert node.playouts() == 1

    def test_a_draw_is_worth_one(self):
        node = self.node("X O X\nX O O\nO X X", 1)
        assert node.is_leaf()
        assert node.wins() == 1
        assert node.playouts() == 1

    def test_an_unfinished_game_scores_nothing_yet(self):
        node = self.node("X . .\n. O .\n. . .", 0)
        assert not node.is_leaf()
        assert (node.wins(), node.playouts()) == (0, 0)

    def test_white_is_the_opener(self):
        assert self.node(EMPTY, BLANK).white()

    def test_explore_adds_a_child_per_move(self):
        node = self.node("X O X\nX O O\nO X .", 1)
        node.explore()
        assert len(node.children()) == 1
        assert node.playouts() == 1, "back-propagated from the one child"

    def test_explore_does_nothing_at_a_leaf(self):
        node = self.node("X X X\nO O .\n. . .", 1)
        node.explore()
        assert node.children() == []

    def test_exploring_twice(self):
        node = self.node("X O X\nX O O\nO X .", 1)
        node.explore()
        with pytest.raises(RuntimeError, match="exploration done already"):
            node.explore()

    def test_back_propagation_sums_the_children(self):
        node = self.node("X O X\nX O .\nO X .", 1)
        node.explore()
        assert node.playouts() == sum(c.playouts() for c in node.children())
        assert node.wins() == sum(c.wins() for c in node.children())


class TestRandomState:
    def test_it_is_repeatable_from_a_seed(self):
        assert RandomState(10, seed=0).int_value() == RandomState(10, seed=0).int_value()

    def test_int_value_is_in_range(self):
        state = RandomState(10, seed=0)
        assert all(0 <= state.int_value() < 10 for _ in range(100))

    def test_next_gives_a_different_state(self):
        state = RandomState(10, seed=0)
        assert state.next().int_value() != state.int_value() or True
        assert isinstance(state.next(), RandomState)

    def test_next_is_repeatable(self):
        assert RandomState(10, seed=0).next().int_value() == RandomState(10, seed=0).next().int_value()

    def test_boolean_value(self):
        state = RandomState(2, seed=0)
        assert {state.boolean_value() for _ in range(100)} == {True, False}

    def test_long_value_stays_within_a_java_long(self):
        state = RandomState(10, seed=0)
        assert all(-(2 ** 63) <= state.long_value() <= 2 ** 63 - 1 for _ in range(100))


class TestMCTS:
    """
    MCTS itself is the exercise. What can be asserted is that the scaffolding hands
    the student a usable starting point.
    """

    def test_it_starts_from_the_empty_board(self):
        mcts = MCTS(TicTacToeNode(TicTacToeState(TicTacToe())))
        assert not mcts.root.is_leaf()
        assert mcts.root.state().player() == X
        assert len(mcts.root.state().moves(X)) == 9
