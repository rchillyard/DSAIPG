"""
Lucas numbers, ported from graphs/dynamicProgramming/lucas/Lucas.java.
"""

from __future__ import annotations

from src.graphs.dynamic_programming.lucas.recurrence import Recurrence


class Lucas(Recurrence):
    """
    2, 1, 3, 4, 7, 11, ... -- the same recurrence as Fibonacci, seeded 2 and 1.
    """

    def __init__(self) -> None:
        super().__init__(2, 1)
