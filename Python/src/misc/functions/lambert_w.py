"""
Ported from misc/functions/LambertW.java.
"""

from __future__ import annotations

import math

from .newton import Newton

#: How many steps Newton is allowed, and how many terms the series estimate uses.
MAX_TRIES = 20


class LambertException(Exception):
    """
    Raised when W(j, x) is asked for outside the branch's domain.
    """


class LambertW:
    """
    The Lambert W function: the inverse of x -> x e^x.

    Since x e^x is not one-to-one, W has branches. Only two are real: branch 0 for
    x >= -1/e, and branch -1 for -1/e <= x <= 0. Each is found by taking a
    reasonable estimate and handing it to Newton.
    """

    def w(self, j: int, z: float, tolerance: float) -> float:
        """
        :param j: which branch: 0 or -1.
        :param z: the value to invert.
        :param tolerance: how near zero the residual must come.
        :return: W_j(z).
        :raises LambertException: if z is outside the branch's domain.
        :raises RuntimeError: if Newton fails to converge.
        """
        estimate = self.estimate_w(j, z)
        newton = Newton(
            "x exp(x) - z = 0",
            lambda x: x * math.exp(x) - z,
            lambda x: (1 + x) * math.exp(x),
        )
        solution = newton.solve(estimate, MAX_TRIES, tolerance)
        if solution.is_right():
            return solution.get_right()
        raise RuntimeError(solution.get_left())

    def w_branches(self, z: float, tolerance: float) -> list[float]:
        """
        Every real branch defined at z: two where -1/e <= z < 0, one where z >= 0.

        NOTE named separately because Python cannot overload; the Java has this as
        a second W(double, double).

        :param z: the value to invert.
        :param tolerance: how near zero the residual must come.
        :return: the values of W at z, branch 0 first.
        """
        result = []
        for j in (0, -1):
            try:
                result.append(self.w(j, z, tolerance))
            except LambertException:
                pass  # this branch is not defined at z
        return result

    def estimate_w(self, j: int, x: float) -> float:
        """
        A starting guess for Newton, good enough for it to converge from.

        :param j: which branch: 0 or -1.
        :param x: the value to invert.
        :return: an estimate of W_j(x).
        :raises LambertException: if x is outside the branch's domain.
        """
        if j == 0:
            if -1 < x < 1 / math.e:
                # the series expansion about zero
                return sum(_term0(x, i) for i in range(1, MAX_TRIES))
            if x >= 1 / math.e:
                log_x = math.log(x)
                return log_x - (math.log(log_x) if log_x > 0 else 0)
            raise LambertException("LambertW: W(j,x): not supported for j=0, x < -1")
        if j == -1:
            # NOTE the Java writes -1/e <= x <= 0, including zero. Branch -1 tends
            # to minus infinity as x approaches zero, so W(-1, 0) is undefined --
            # and the Java does not return it either: its estimate comes out as
            # -Infinity and Newton then fails, so it raises RuntimeException where
            # LambertException is what the caller is watching for. Excluding zero
            # from the domain says the same thing in the right way, and it is what
            # lets w_branches report one branch at zero rather than blowing up.
            if -1 / math.e <= x < 0:
                log_x = math.log(-x)
                return log_x - (math.log(log_x) if log_x > 0 else 0)
            raise LambertException(
                "LambertW: W(j,x): not supported for j=-1, x >= 0 or x < -1/e"
            )
        raise LambertException(f"LambertW: W(j,x): not supported for j={j}")


def _term0(x: float, p: int) -> float:
    """
    :param x: the value to invert.
    :param p: which term of the series.
    :return: the p-th term of the series for W about zero.
    """
    return x**p * (-p) ** (p - 1) / math.factorial(p)
