"""
Pell numbers, ported from graphs/dynamicProgramming/lucas/Pell.java.
"""

from __future__ import annotations

from src.graphs.dynamic_programming.lucas.recurrence import Recurrence


class Pell(Recurrence):
    """
    0, 1, 2, 5, 12, 29, ... -- x[i] = x[i-2] + 2*x[i-1].

    NOTE these grow by a factor of about 2.414 each time, so they pass a 64-bit
    long at around n = 62. The Java used to hold them in a long and its PellTest
    asserted ``get(90) == 7052354271195710746`` -- not the 90th Pell number, but
    what a long holds once that value has wrapped. The Java uses BigInteger now,
    so both trees give the true 9,960,168,529,794,442,859,224,531,878,561,050 and
    there is no divergence left to document. Python's integers were arbitrary
    precision all along, which is what brought the discrepancy to light.
    """

    def __init__(self) -> None:
        super().__init__(0, 1, multiplier=2)
