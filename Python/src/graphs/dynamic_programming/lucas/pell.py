"""
Pell numbers, ported from graphs/dynamicProgramming/lucas/Pell.java.
"""

from __future__ import annotations

from src.graphs.dynamic_programming.lucas.recurrence import Recurrence


class Pell(Recurrence):
    """
    0, 1, 2, 5, 12, 29, ... -- x[i] = x[i-2] + 2*x[i-1].

    NOTE these grow by a factor of about 2.414 each time, so they pass a 64-bit
    long at around n = 62 -- get(90) is 9,960,168,529,794,442,859,224,531,878,561,050.
    Python's integers are of arbitrary precision and the Java uses BigInteger, so
    neither has a ceiling.
    """

    def __init__(self) -> None:
        super().__init__(0, 1, multiplier=2)
