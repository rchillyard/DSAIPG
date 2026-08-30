"""
Fibonacci numbers, ported from graphs/dynamicProgramming/lucas/Fibonacci.java.
"""

from __future__ import annotations

from src.graphs.dynamic_programming.lucas.recurrence import Recurrence


class Fibonacci(Recurrence):
    """
    1, 1, 2, 3, 5, 8, ... -- the convention which starts at 1, not 0.

    There is no ceiling: Python integers are of arbitrary precision, and the Java
    uses BigInteger for the same reason. An int would overflow at n = 47, where
    fib(47) is 4,807,526,976 against an int maximum of 2,147,483,647.
    """

    def __init__(self) -> None:
        super().__init__(1, 1)
