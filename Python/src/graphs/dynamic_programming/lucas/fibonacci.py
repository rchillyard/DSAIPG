"""
Fibonacci numbers, ported from graphs/dynamicProgramming/lucas/Fibonacci.java.
"""

from __future__ import annotations

from src.graphs.dynamic_programming.lucas.recurrence import Recurrence


class Fibonacci(Recurrence):
    """
    1, 1, 2, 3, 5, 8, ... -- the convention which starts at 1, not 0.

    NOTE the Java used to store these in an ArrayList<Integer> and return int, so
    it overflowed silently at n = 47: fib(47) is 4,807,526,976 against an int
    maximum of 2,147,483,647, and FibonacciTest stopped at get(7) so it never
    showed. It uses BigInteger now, and both trees have no ceiling.
    """

    def __init__(self) -> None:
        super().__init__(1, 1)
