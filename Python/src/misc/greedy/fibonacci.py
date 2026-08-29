"""
Ported from misc/greedy/Fibonacci.java.
"""

from __future__ import annotations


class Fibonacci:
    """
    The Fibonacci numbers, kept in a list which grows as far as it is asked to.

    NOTE the Java holds them in a fixed-length array which it doubles, copying the
    old values across, exactly as Bag_Array does -- the growth being the point on
    show. A Python list already does that, so this appends, and the doubling shows
    only in ``size``, which stays a power of two so that the Java's test numbers
    still mean something.
    """

    def __init__(self) -> None:
        self.fibonacci = [1, 1]

    def get_largest(self, x: int) -> int:
        """
        :param x: the value not to exceed.
        :return: the largest Fibonacci number no greater than x.
        """
        index = len(self.fibonacci) - 1
        while self.fibonacci[index] > x:
            index -= 1
        return self.fibonacci[index]

    def ensure(self, x: int) -> None:
        """
        Grow the series until it reaches at least x.

        :param x: the value to reach.
        """
        while self.fibonacci[-1] < x:
            self._extend()

    def _extend(self) -> None:
        """
        Double the length of the series, filling the new half.
        """
        length = len(self.fibonacci)
        for _ in range(length):
            self.fibonacci.append(self.fibonacci[-2] + self.fibonacci[-1])

    def size(self) -> int:
        """
        :return: how many Fibonacci numbers are held. For testing only.
        """
        return len(self.fibonacci)

    def fib(self, x: int) -> int:
        """
        :param x: an index into the series.
        :return: the Fibonacci number there. For testing only.
        """
        return self.fibonacci[x]
