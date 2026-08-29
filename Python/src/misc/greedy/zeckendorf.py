"""
Ported from misc/greedy/Zeckendorf.java.
"""

from __future__ import annotations

from .fibonacci import Fibonacci


class Zeckendorf:
    """
    Zeckendorf's representation: every positive integer is the sum of one or more
    non-consecutive Fibonacci numbers, in exactly one way.

    Greedy finds it -- take the largest Fibonacci number that fits, subtract, and
    repeat -- and here greedy is not merely a heuristic but provably right, which
    is what makes this worth showing next to the coin problem, where it is not.
    """

    def __init__(self) -> None:
        self.fibonacci = Fibonacci()

    def get(self, x: int) -> list[int]:
        """
        :param x: the value to represent.
        :return: the Fibonacci numbers summing to it, largest first.
        """
        self.fibonacci.ensure(x)
        return self._get_zeckendorf_representation(x)

    def _get_zeckendorf_representation(self, x: int) -> list[int]:
        """
        :param x: the value to represent.
        :return: the Fibonacci numbers summing to it, largest first.
        """
        result = []
        remainder = x
        while remainder > 0:
            greedy = self.fibonacci.get_largest(remainder)
            result.append(greedy)
            remainder -= greedy
        return result
