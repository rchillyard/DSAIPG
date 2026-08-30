"""
Ported from projects/mcts/core/RandomState.java.
"""

from __future__ import annotations

import time
from random import Random

#: The bounds of a Java long, which is what next() and long_value() work in.
_LONG_MIN = -(2 ** 63)
_LONG_MAX = 2 ** 63 - 1


class RandomState:
    """
    A source of pseudo-random numbers which can hand out its successor, so that a
    sequence of them is reproducible from a seed.
    """

    def __init__(self, x: int, seed: int | None = None, random: Random | None = None) -> None:
        """
        NOTE Python cannot overload, so the Java's three constructors -- (x, seed),
        (x), and the private (x, Random) -- are one signature here.

        :param x: the exclusive upper bound for int_value.
        :param seed: the seed; the current time in milliseconds if not given.
        :param random: an existing source, used in place of a seed.
        """
        self.x = x
        if random is not None:
            self.random = random
        else:
            self.random = Random(seed if seed is not None else int(time.time() * 1000))

    def next(self) -> RandomState:
        """
        :return: the next RandomState, seeded from this one.
        """
        return RandomState(self.x, seed=self.long_value())

    def int_value(self) -> int:
        """
        :return: a value in [0, x).
        """
        return self.random.randrange(self.x)

    def long_value(self) -> int:
        """
        NOTE bounded to the range of a Java long, which Python integers are not.
        Left bounded so that a seed derived from this behaves as the Java's does.

        :return: a value in the range of a 64-bit signed integer.
        """
        return self.random.randint(_LONG_MIN, _LONG_MAX)

    def boolean_value(self) -> bool:
        """
        :return: True or False, evenly.
        """
        return self.random.choice([True, False])

    def __str__(self) -> str:
        return f"RandomState{{x={self.x}, random={self.random}}}"
