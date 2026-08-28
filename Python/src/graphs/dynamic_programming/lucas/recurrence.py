"""
The second-order linear recurrence behind Fibonacci, Lucas and Pell.

Ported from graphs/dynamicProgramming/lucas/{Fibonacci,Lucas,Pell}.java.
"""

from __future__ import annotations


class Recurrence:
    """
    x[i] = x[i-2] + multiplier * x[i-1], memoised.

    NOTE the Java has this three times, as Fibonacci, Lucas and Pell, each with
    its own ArrayList and its own copy of the same loop. They differ in exactly
    two things: the two seed values, and whether the previous term is multiplied
    by one or by two. Writing it once makes that visible, which is the point the
    package is making -- these are the same recurrence, not three algorithms.

    The memoisation is the other point. `get` extends the table only as far as it
    must and keeps it, so a second call costs nothing; `bad` recomputes from
    scratch every time. See `bad` for what that costs.
    """

    def __init__(self, first: int, second: int, multiplier: int = 1) -> None:
        """
        :param first: x[0].
        :param second: x[1].
        :param multiplier: the coefficient of x[i-1].
        """
        self._multiplier = multiplier
        self._terms = [first, second]

    def get(self, n: int) -> int:
        """
        The nth term, computed once and remembered.

        :param n: which term.
        :return: x[n].
        :raises ValueError: if n is negative.
        """
        if n < 0:
            raise ValueError(f"{type(self).__name__}.get is not supported for negative n")
        if n < len(self._terms):
            return self._terms[n]
        return self._evaluate(n)

    def _evaluate(self, n: int) -> int:
        for i in range(len(self._terms), n + 1):
            self._terms.append(self._terms[i - 2] + self._multiplier * self._terms[i - 1])
        return self._terms[n]

    def bad(self, n: int) -> int:
        """
        The same recurrence computed by naive recursion, which is exponential.

        Every call recomputes both predecessors from scratch, so the number of
        calls is itself the recurrence: bad(40) makes on the order of a hundred
        million of them. Kept because contrasting it with `get` is the whole
        lesson of dynamic programming.

        NOTE the Java has this only on Lucas, though it applies equally to all
        three. It is on the shared class here.

        :param n: which term.
        :return: x[n].
        :raises ValueError: if n is negative.
        """
        if n < 0:
            raise ValueError(f"{type(self).__name__}.bad is not supported for negative n")
        if n == 0:
            return self._terms[0]
        if n == 1:
            return self._terms[1]
        return self.bad(n - 2) + self._multiplier * self.bad(n - 1)
