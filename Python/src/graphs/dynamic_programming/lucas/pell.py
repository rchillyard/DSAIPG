"""
Pell numbers, ported from graphs/dynamicProgramming/lucas/Pell.java.
"""

from __future__ import annotations

from src.graphs.dynamic_programming.lucas.recurrence import Recurrence


class Pell(Recurrence):
    """
    0, 1, 2, 5, 12, 29, ... -- x[i] = x[i-2] + 2*x[i-1].

    NOTE these grow by a factor of about 2.414 each time, so they leave a 64-bit
    long behind at around n = 62. The Java's PellTest asserts
    ``get(90) == 7052354271195710746``, which is not the 90th Pell number: it is
    what a long holds after that value has wrapped. The true value is
    9,960,168,529,794,442,859,224,531,878,561,050.

    Python's integers are arbitrary precision, so `get(90)` here gives the real
    number and CANNOT reproduce the Java's assertion. The test says so, and the
    Java's is recorded in `Deferred work.md` as an assertion of an arithmetic
    accident rather than of a Pell number.
    """

    def __init__(self) -> None:
        super().__init__(0, 1, multiplier=2)
