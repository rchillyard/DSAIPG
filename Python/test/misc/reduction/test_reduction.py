"""
Tests for misc/reduction: three answers to the same puzzle, the third by turning
it round.
"""

from __future__ import annotations

from src.misc.reduction.moves import Moves1, Moves2, Moves3
from src.misc.reduction.point import Point


class TestPoint:
    def test_valid(self):
        assert Point(1, 1).valid()
        assert not Point(0, 1).valid()
        assert not Point(1, 0).valid()
        assert not Point(-1, -1).valid()

    def test_equality_and_str(self):
        assert Point(1, 2) == Point(1, 2)
        assert Point(1, 2) != Point(2, 1)
        assert str(Point(1, 2)) == "Point{x=1, y=2}"


class TestMoves1:
    """
    Forwards, depth first. The Java's own cases, less the large one, which this
    search cannot finish.
    """

    def test_reachable(self):
        assert Moves1(3, 5).valid_xy(1, 1)
        assert Moves1(1, 1).valid_xy(1, 1)
        assert Moves1(99, 100).valid_xy(1, 1)

    def test_unreachable(self):
        assert not Moves1(2, 2).valid_xy(1, 1)

    def test_move_is_not_used(self):
        # Moves1 implements the interface but searches by recursion, so its move
        # returns None -- faithfully to the Java
        assert Moves1(1, 1).move(Point(1, 1), True) is None


class TestMoves2:
    """
    Forwards, breadth first: the same search with a queue instead of the stack.
    """

    def test_reachable(self):
        assert Moves2.of(3, 5).valid(1, 1)
        assert Moves2.of(1, 1).valid(1, 1)
        # NOTE the Java had this case disabled: its search recursed once per point
        # taken off the queue and overflowed the stack. A loop has no such limit.
        # Fixed in the Java too, and its test2_4 is reinstated.
        assert Moves2.of(99, 100).valid(1, 1)

    def test_unreachable(self):
        assert not Moves2.of(2, 2).valid(1, 1)

    def test_the_large_case_is_beyond_a_forward_search(self):
        # The Java's test2_5 asks Moves2 whether (35,13) reaches (455955547,
        # 420098884). It is disabled there and has no counterpart here, and not
        # because of any language: searching FORWARDS reaches over 11 million
        # points without exhausting the queue, and the queue is still growing.
        # Moves3 answers the same question at once, which is the point of the
        # package -- see TestMoves3.test_unreachable.
        assert not Moves3.of(35, 13).valid_xy(455955547, 420098884)

    def test_the_two_moves(self):
        m = Moves2.of(1, 1)
        assert m.move(Point(2, 3), True) == Point(2, 5)
        assert m.move(Point(2, 3), False) == Point(5, 3)


class TestMoves3:
    """
    Backwards. Each step is forced, so there is nothing to search -- which is why
    this is the only one of the three that answers the large case at all.
    """

    def test_reachable(self):
        assert Moves3.of(1, 1).valid_xy(3, 5)
        assert Moves3.of(1, 1).valid_xy(1, 1)
        assert Moves3.of(1, 1).valid_xy(99, 100)

    def test_unreachable(self):
        assert not Moves3.of(1, 1).valid_xy(2, 2)
        assert not Moves3.of(35, 13).valid_xy(455955547, 420098884)

    def test_the_move_goes_backwards(self):
        m = Moves3.of(1, 1)
        assert m.move(Point(2, 5), True) == Point(2, 3), "take the smaller from the larger"
        assert m.move(Point(5, 3), True) == Point(2, 3)

    def test_it_agrees_with_the_forward_searches(self):
        # the reduction is only worth anything if it gives the same answers
        for x in range(1, 12):
            for y in range(1, 12):
                assert Moves3.of(1, 1).valid_xy(x, y) == Moves1(x, y).valid_xy(1, 1), (x, y)

    def test_it_answers_a_case_the_others_cannot(self):
        # 20 digits apiece. A forward search would not finish this side of the heat
        # death of the universe; backwards it is two steps -- (10^20, 10^20 + 1)
        # came from (10^20, 1), which is on the axis through the start.
        assert Moves3.of(1, 1).valid_xy(10**20, 10**20 + 1)
        assert Moves3.of(1, 1).valid_xy(10**20, 1), "straight along the axis"
