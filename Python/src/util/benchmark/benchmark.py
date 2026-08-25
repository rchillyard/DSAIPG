"""
The Benchmark abstraction, ported from util/benchmark/Benchmark.java.

A Benchmark runs something m times and reports the mean time for one run.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import Callable
from typing import Generic, TypeVar

T = TypeVar("T")


class Benchmark(ABC, Generic[T]):
    """
    Something which can be run repeatedly and timed.
    """

    def run(self, t: T, m: int) -> float:
        """
        Run the benchmark m times on the same value.

        NOTE the same value is used for every run, so anything that mutates it
        will see it already modified on the second run. Use ``run_from_supplier``
        with a supplier that produces a fresh value when that matters.

        :param t: the value to run on.
        :param m: the number of runs.
        :return: the mean time for one run, in milliseconds.
        """
        return self.run_from_supplier(lambda: t, m)

    @abstractmethod
    def run_from_supplier(self, supplier: Callable[[], T], m: int) -> float:
        """
        Run the benchmark m times, taking a fresh value from supplier each time.

        :param supplier: supplies a value for each run.
        :param m: the number of runs.
        :return: the mean time for one run, in milliseconds.
        """
