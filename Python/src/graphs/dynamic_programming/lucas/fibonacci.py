"""
Fibonacci numbers, ported from graphs/dynamicProgramming/lucas/Fibonacci.java.
"""

from __future__ import annotations

from src.graphs.dynamic_programming.lucas.recurrence import Recurrence


class Fibonacci(Recurrence):
    """
    1, 1, 2, 3, 5, 8, ... -- the convention which starts at 1, not 0.

    NOTE the Java stores these in an ArrayList<Integer> and returns int, so it
    overflows silently at n = 47: fib(47) is 4,807,526,976 against an int maximum
    of 2,147,483,647. FibonacciTest only reaches get(7), so it never shows.
    Python's integers do not overflow, so the port has no such limit.
    """

    def __init__(self) -> None:
        super().__init__(1, 1)
