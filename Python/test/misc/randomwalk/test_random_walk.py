"""
Tests for misc/randomwalk.
"""

from __future__ import annotations

import random

import pytest

from src.misc.randomwalk.random_walk import (
    RandomWalk,
    expected_distance,
    random_walk_multi,
)


class TestRandomWalk:
    def test_distance_at_the_start(self):
        assert RandomWalk().distance() == pytest.approx(0.0)

    def test_distance_after_one_move(self):
        rw = RandomWalk()
        rw._move(1, 0)
        assert rw.distance() == pytest.approx(1.0)

    def test_the_three_four_five_triangle(self):
        rw = RandomWalk()
        rw._move(3, 4)
        assert rw.distance() == pytest.approx(5.0)
        rw._move(-3, -4)
        assert rw.distance() == pytest.approx(0.0)

    def test_moves_accumulate(self):
        rw = RandomWalk()
        for _ in range(4):
            rw._move(1, 0)
        assert (rw.x, rw.y) == (4, 0)
        assert rw.distance() == pytest.approx(4.0)

    def test_a_walk_takes_the_right_number_of_steps(self):
        rw = RandomWalk(random.Random(0))
        rw._random_walk(100)
        # every step changes exactly one coordinate by one, so the total distance
        # walked is 100 and the displacement has the same parity
        assert (abs(rw.x) + abs(rw.y)) % 2 == 100 % 2
        assert abs(rw.x) + abs(rw.y) <= 100

    def test_a_seeded_walk_repeats(self):
        # which the Java cannot ask for: its Random is created inside the object
        first, second = RandomWalk(random.Random(42)), RandomWalk(random.Random(42))
        first._random_walk(50)
        second._random_walk(50)
        assert (first.x, first.y) == (second.x, second.y)

    def test_distance_grows_as_the_square_root_of_the_steps(self):
        # the point of the exercise: four times the steps is twice the distance,
        # not four times. Averaged over enough walks for the ratio to settle.
        rng = random.Random(1)
        near = random_walk_multi(100, 400, rng)
        far = random_walk_multi(400, 400, rng)
        assert far / near == pytest.approx(2.0, abs=0.25)

    def test_against_the_expected_distance(self):
        rng = random.Random(2)
        assert random_walk_multi(256, 500, rng) == pytest.approx(
            expected_distance(256), rel=0.15)

    def test_expected_distance(self):
        assert expected_distance(0) == 0
        assert expected_distance(4) == pytest.approx(expected_distance(1) * 2)
