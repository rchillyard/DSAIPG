"""
Minimum coins for an amount, ported from
graphs/dynamicProgramming/coins/CoinChanger.java.
"""

from __future__ import annotations

import math


class CoinChanger:
    """
    How few coins make a given amount, by dynamic programming over a table of
    (denominations considered) x (amount).

    Row 0 -- no denominations available -- is infinity for every positive amount,
    because nothing can be made; column 0 is zero, because the empty amount needs
    no coins. Each later cell is the better of not using the current denomination
    at all, and using one of it and solving the remainder.

    NOTE the Java's table is double[][] purely so that it can hold
    Double.POSITIVE_INFINITY, and its comment says as much: "We use double here
    because Infinity parameter is not supported in Java" for int. It then casts
    the answer back to int. Python has math.inf and unbounded ints, so the table
    holds ints and infinity alike without the round trip.
    """

    def __init__(self, denominations: list[int]) -> None:
        """
        :param denominations: the coin values available.
        """
        self._denominations = list(denominations)

    def minimum_coins(self, amount: int) -> int:
        """
        :param amount: the amount to make.
        :return: the fewest coins that make it.
        """
        n = len(self._denominations)
        table: list[list[float]] = [[math.inf] * (amount + 1) for _ in range(n + 1)]
        for i in range(n + 1):
            table[i][0] = 0
        for i in range(1, n + 1):
            for j in range(1, amount + 1):
                coin = self._denominations[i - 1]
                if coin <= j:
                    table[i][j] = min(1 + table[i][j - coin], table[i - 1][j])
                else:
                    table[i][j] = table[i - 1][j]
        return int(table[n][amount])
