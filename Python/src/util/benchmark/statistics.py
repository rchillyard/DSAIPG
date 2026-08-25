"""
Statistical summary of a series of measurements, ported from
util/benchmark/Statistics.java.

NOTE this module shares its name with the standard library's ``statistics``.
That is safe, because everything here is imported by its full path from ``src``
and nothing in the tree puts this directory on ``sys.path``; the name is kept
because the Java class is called Statistics.
"""

import math
from collections.abc import Callable

from src.sort.generic.sort_exception import SortException
from src.util.general.utilities import as_int, format_decimal_3_places


def normalizer_linearithmic_natural(x: float) -> float:
    """
    The linearithmic normalizer, x log x, using natural logarithms.

    :param x: a positive number; the logarithm of zero or a negative number is
              undefined and will raise.
    :return: x * ln(x).
    """
    return math.log(x) * x


class Statistics:
    """
    Analyse and store statistical data for a series of numerical values,
    calculating total, mean, standard deviation, and a normalized mean.
    """

    def __init__(self, property_name: str, normalizer: Callable[[float], float],
                 n_runs: int, size: int) -> None:
        """
        :param property_name: the property being tracked.
        :param normalizer: the normalizer for the results, for example 1/n lg n.
        :param n_runs: the number of runs.
        :param size: the size of the problem.
        """
        self._property = property_name
        self._normalizer = normalizer
        self._doubles: list[float] = [0.0] * n_runs
        self._size = size
        self._count = 0
        self._total: float | None = None
        self._std_dev: float | None = None
        self._updated = False

    def add(self, x: float) -> None:
        """
        Add a value, growing the store if it is full.

        :param x: the value to add.
        :raises SortException: if there is no store to add to.
        """
        if len(self._doubles) == 0:
            raise SortException("Statistics: doubles is empty")
        if self._count >= len(self._doubles):
            self._resize(2 * len(self._doubles))
        self._doubles[self._count] = x
        self._count += 1
        self._stale()

    def get_count(self) -> int:
        """
        :return: the number of values added so far.
        """
        return self._count

    def total(self) -> float:
        """
        The sum of the values added, computed once and then cached until the next
        addition.

        :return: the total.
        """
        if self._total is None:
            self._total = sum(self._doubles[: self._count])
        return self._total

    def mean(self) -> float:
        """
        :return: the arithmetic mean.
        :raises ZeroDivisionError: if no values have been added.
        """
        return self.total() / self._count

    def std_dev(self) -> float:
        """
        The population standard deviation, computed once and then cached until
        the next addition.

        NOTE the divisor is the count, not count - 1, following the Java
        original: this is the population deviation, not the sample estimate.

        :return: the standard deviation.
        """
        if self._std_dev is None:
            mean = self.mean()
            variance = sum((x - mean) ** 2 for x in self._doubles[: self._count])
            self._std_dev = math.sqrt(variance / self._count)
        return self._std_dev

    def normalized_mean(self) -> float:
        """
        The mean divided by the normalizer applied to the size of the problem.

        :return: the normalized mean.
        """
        return self.mean() / self._normalizer(float(self._size))

    def __str__(self) -> str:
        """
        NOTE "n" here is the capacity of the store, not the number of values
        added, which is what the Java original reports.  The two differ once the
        store has been grown.
        """
        if not self._updated:
            return f"{self._property}: <unset>"
        parts = [f"{self._property}: n={len(self._doubles)}", f"mean={as_int(self.mean())}"]
        if self.std_dev() > 0.0:
            parts.append(f"stdDev={as_int(self.std_dev())}")
        parts.append(f"normalized={format_decimal_3_places(self.normalized_mean())}")
        return "; ".join(parts)

    def _resize(self, n: int) -> None:
        """
        Grow the store to n, leaving the added values in place.

        :param n: the new capacity.
        """
        self._doubles = self._doubles + [0.0] * (n - len(self._doubles))

    def _stale(self) -> None:
        """
        Discard the cached total and standard deviation, and record that
        something has been added.
        """
        self._total = None
        self._std_dev = None
        self._updated = True
