"""
Ported from misc/greedy/Greedy.java.
"""

from __future__ import annotations

from collections.abc import Callable
from typing import Generic, TypeVar

T = TypeVar("T")
R = TypeVar("R")


class Greedy(Generic[T, R]):
    """
    The greedy method, as a shape rather than an algorithm: while there is anything
    left to do, take the best thing available now, record it, and reduce the
    problem by what was taken.

    Whether that gives the right answer depends entirely on the problem. It does
    for Zeckendorf's representation and for US coinage; it does not for coinage in
    general -- with denominations 1, 3 and 4, greedy makes 6 from 4 + 1 + 1 where
    3 + 3 would do.
    """

    def __init__(
        self,
        f_greedy: Callable[[T], T],
        f_adjust: Callable[[T, T], T],
        f_result: Callable[[T, R], R],
        f_done: Callable[[T], bool],
    ) -> None:
        """
        :param f_greedy: the best thing available now.
        :param f_adjust: what is left after taking it.
        :param f_result: the result so far, with it added.
        :param f_done: whether there is anything left to do.
        """
        self.f_greedy = f_greedy
        self.f_adjust = f_adjust
        self.f_result = f_result
        self.f_done = f_done

    def run(self, t: T, r: R) -> R:
        """
        :param t: the problem.
        :param r: the empty result to build on.
        :return: the result.
        """
        while not self.f_done(t):
            greedy = self.f_greedy(t)
            r = self.f_result(greedy, r)
            t = self.f_adjust(t, greedy)
        return r
