"""
Tests for misc/reduction: three answers to the same puzzle, the third by turning
it round.
"""

from __future__ import annotations

import pytest

from src.misc.reduction.moves import Moves1, Moves2, Moves2A, Moves3
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
    Forwards, depth first: the case study's first algorithm, with the base case
    that makes it terminate.
    """

    def test_the_five_conditions_it_can_answer(self):
        assert Moves1(1, 1).valid_xy(1, 1)
        assert not Moves1(2, 2).valid_xy(1, 1)
        assert Moves1(3, 5).valid_xy(1, 1)
        assert not Moves1(12, 8).valid_xy(9, 5)
        assert Moves1(99, 100).valid_xy(1, 1)

    def test_in_bounds_is_the_base_case(self):
        # Both coordinates only ever grow, so once either has passed the target
        # that path is dead. Without this there is no base case for failure at all
        # and the search runs for ever.
        m = Moves1(3, 5)
        assert m.in_bounds(Point(1, 1))
        assert m.in_bounds(Point(3, 5))
        assert not m.in_bounds(Point(4, 5))
        assert not m.in_bounds(Point(3, 6))

    def test_the_two_moves(self):
        m = Moves1(99, 100)
        assert m.move(Point(2, 3), True) == Point(2, 5), "grow y"
        assert m.move(Point(2, 3), False) == Point(5, 3), "grow x"


class TestMoves2:
    """
    Forwards, breadth first: the same search with a queue.
    """

    def test_reachable(self):
        assert Moves2.of(3, 5).valid(1, 1)
        assert Moves2.of(1, 1).valid(1, 1)

    def test_unreachable(self):
        assert not Moves2.of(2, 2).valid(1, 1)
        assert not Moves2.of(12, 8).valid(9, 5)

    def test_the_stack_runs_out_sooner_here_than_in_java(self):
        # _inner recurses once per point taken OFF the queue, so its depth is the
        # number of points examined -- twelve thousand for this case -- not the
        # length of the path. Java survives that and answers; Python's default
        # limit is 1000, so the same call raises. Same lesson, met sooner.
        with pytest.raises(RecursionError):
            Moves2.of(99, 100).valid(1, 1)

    def test_the_two_moves(self):
        m = Moves2.of(1, 1)
        assert m.move(Point(2, 3), True) == Point(2, 5)
        assert m.move(Point(2, 3), False) == Point(5, 3)


class TestMoves2A:
    """
    The queue search as a real iteration, with the two improvements the case study
    proposes. The measurements matter as much as the answers, so they are asserted.
    """

    def test_the_five_conditions(self):
        assert Moves2A.of(1, 1).valid_xy(1, 1)
        assert not Moves2A.of(2, 2).valid_xy(1, 1)
        assert Moves2A.of(3, 5).valid_xy(1, 1)
        assert not Moves2A.of(12, 8).valid_xy(9, 5)
        assert Moves2A.of(99, 100).valid_xy(1, 1), "the case Moves2 cannot reach here"

    def test_the_cache_never_hits(self):
        # From a given start every reachable point has exactly one predecessor --
        # of (x-y, y) and (x, y-x) only one has both coordinates positive -- so no
        # point is ever arrived at twice and there is nothing to remember. It is
        # the same observation that makes Moves3 work.
        m = Moves2A.of(99, 100)
        assert m.valid_xy(1, 1)
        assert m.cache_hits == 0
        small = Moves2A.of(3, 5)
        small.valid_xy(1, 1)
        assert small.cache_hits == 0

    def test_the_ordering_does_not_reduce_the_work(self):
        # A queue is level-by-level, so whichever successor goes on first, both are
        # dealt with before anything they lead to. 12,090 is what a plain queue
        # examines too -- and what the Java examines, exactly.
        m = Moves2A.of(99, 100)
        assert m.valid_xy(1, 1)
        assert m.examined == 12090

    def test_the_work_grows_as_a_power_of_the_target(self):
        # Consecutive Fibonacci numbers are the worst targets, needing the longest
        # path. Stepping from one pair to the next multiplies the target by phi and
        # roughly doubles the work, so no constant factor -- which is all that
        # ordering or caching could be worth -- can rescue this approach.
        counts = []
        for tx, ty in [(5, 8), (13, 21), (34, 55), (89, 144), (144, 233)]:
            m = Moves2A.of(tx, ty)
            m.valid_xy(1, 1)
            counts.append(m.examined)
        assert counts == [24, 96, 384, 1536, 2048]
        assert counts[-1] / counts[0] > 80, "85-fold work for a 29-fold target"


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

    def test_a_target_below_the_start_is_never_reachable(self):
        # Both coordinates only ever grow. The aligned rules divide by a modulus,
        # and a modulus does not care about sign: (1-2) % 1 is 0, so a rule applied
        # before checking this reports a point below the start as reachable. The
        # missed-area test has to come FIRST.
        assert not Moves3.of(1, 2).valid_xy(1, 1)
        assert not Moves3.of(2, 1).valid_xy(1, 1)
        assert not Moves3.of(5, 5).valid_xy(3, 3)

    def test_it_agrees_with_the_forward_searches_from_any_start(self):
        # The earlier sweep held the start at (1,1), where nothing can be below it,
        # so it could not have caught the above. This varies the start too.
        for sx in range(1, 7):
            for sy in range(1, 7):
                for tx in range(1, 16):
                    for ty in range(1, 16):
                        assert Moves3.of(sx, sy).valid_xy(tx, ty) == Moves1(tx, ty).valid(
                            Point(sx, sy)), f"{sx},{sy}->{tx},{ty}"

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
