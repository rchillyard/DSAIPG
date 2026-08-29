"""
Ported from misc/functions/Newton.java.
"""

from __future__ import annotations

from collections.abc import Callable

from .either import Either


class Newton:
    """
    Newton's method: solve f(x) = 0 by repeatedly stepping from a guess x to
    x - f(x) / f'(x), which is where the tangent at x crosses the axis.

    It can fail -- by not converging within the tries allowed, or by dividing by a
    zero derivative -- so the answer comes back as an ``Either``: the root on the
    right, or the reason there is not one on the left.
    """

    def __init__(
        self,
        equation: str,
        f: Callable[[float], float],
        dfbydx: Callable[[float], float],
    ) -> None:
        """
        :param equation: the equation, for the failure message.
        :param f: the function to find a zero of.
        :param dfbydx: its derivative.
        """
        self.equation = equation
        self.f = f
        self.dfbydx = dfbydx

    def solve(self, x0: float, max_tries: int, tolerance: float) -> Either[str, float]:
        """
        :param x0: where to start.
        :param max_tries: how many steps to allow before giving up.
        :param tolerance: how near zero f(x) must come.
        :return: the root on the right, or on the left why there is not one.
        """
        x = x0
        for _ in range(max_tries):
            try:
                y = self.f(x)
                if abs(y) < tolerance:
                    return Either.right(x)
                x = x - y / self.dfbydx(x)
            except (ArithmeticError, ValueError, OverflowError) as e:
                return Either.left(
                    f"Exception thrown solving {self.equation}=0, given x0={x0}, "
                    f"maxTries={max_tries}, and tolerance={tolerance} because {e}"
                )
        return Either.left(
            f"{self.equation}=0 did not converge given x0={x0}, "
            f"maxTries={max_tries}, and tolerance={tolerance}"
        )
