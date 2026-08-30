"""
Ported from misc/randomwalk/RandomWalk.java.
"""

from __future__ import annotations

import math
import random as random_module


class RandomWalk:
    """
    A walk on a square lattice: each step goes one unit north, south, east or west,
    chosen at random.

    The expected distance from the origin after m steps grows as the square root of
    m, not as m -- which is the point of the exercise, and what
    ``random_walk_multi`` measures.
    """

    def __init__(self, random: random_module.Random | None = None) -> None:
        """
        :param random: the source of randomness; a seeded one makes a walk
                       repeatable, which the Java has no way to ask for.
        """
        self.x = 0
        self.y = 0
        self.random = random if random is not None else random_module.Random()

    def distance(self) -> float:
        """
        :return: how far the walk has ended up from where it started.
        """
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def _move(self, dx: int, dy: int) -> None:
        """
        :param dx: how far to move in x.
        :param dy: how far to move in y.
        """
        # TO BE IMPLEMENTED  do move
        raise NotImplementedError("TO BE IMPLEMENTED")

    def _random_walk(self, m: int) -> None:
        """
        :param m: how many steps to take.
        """
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def _random_move(self) -> None:
        """
        Take one step, in one of the four directions.
        """
        ns = self.random.choice([True, False])
        step = 1 if self.random.choice([True, False]) else -1
        self._move(step if ns else 0, 0 if ns else step)


def random_walk_multi(m: int, n: int, random: random_module.Random | None = None) -> float:
    """
    :param m: how many steps in each walk.
    :param n: how many walks to average over.
    :param random: the source of randomness, if the result is to be repeatable.
    :return: the mean distance from the origin after m steps.
    """
    total_distance = 0.0
    for _ in range(n):
        walk = RandomWalk(random)
        walk._random_walk(m)
        total_distance += walk.distance()
    return total_distance / n


def expected_distance(m: int) -> float:
    """
    The distance a walk of m steps is expected to end up from the origin.

    NOTE not in the Java, which leaves the reader to notice the pattern in the
    numbers its main prints. It is sqrt(pi * m / 4) for a walk on a square lattice
    in two dimensions -- the square root is what matters, the constant less so.

    :param m: how many steps.
    :return: the expected distance.
    """
    return math.sqrt(math.pi * m / 4)
