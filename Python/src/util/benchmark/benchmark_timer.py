"""
A Benchmark implemented with a Timer, ported from
util/benchmark/Benchmark_Timer.java.
"""

from __future__ import annotations

from collections.abc import Callable
from typing import TypeVar

from src.util.benchmark.benchmark import Benchmark
from src.util.benchmark.timer import Timer
from src.util.general.utilities import format_whole
from src.util.logging.lazy_logger import LazyLogger

T = TypeVar("T")

logger = LazyLogger(__name__)


def get_warmup_runs(m: int) -> int:
    """
    The number of warmup runs to perform before timing anything: a fifteenth of
    the total, but at least one and at most three.

    :param m: the number of timed runs.
    :return: the number of warmup runs.
    """
    return max(1, min(3, m // 15))


class Benchmark_Timer(Benchmark[T]):
    """
    A Benchmark which warms up, then times a function over m runs.

    The warmup matters: the first few runs of anything are slower, and including
    them in the measurement would flatter whichever implementation happened to
    run second.
    """

    def __init__(self, description: str, config, f_run: Callable[[T], None],
                 f_pre: Callable[[T], T] | None = None,
                 f_post: Callable[[T], None] | None = None) -> None:
        """
        :param description: what is being benchmarked, for the log.
        :param config: the configuration, which supplies the Timer.
        :param f_run: the function to time.
        :param f_pre: pre-processes each value, untimed (may be None).
        :param f_post: consumes each value afterwards, untimed (may be None).
        """
        self._description = description
        self._config = config
        self._f_pre = f_pre
        self._f_run = f_run
        self._f_post = f_post

    def run_from_supplier(self, supplier: Callable[[], T], m: int) -> float:
        """
        Warm up, then run the function m times, taking a fresh value from
        supplier each time.

        :param supplier: supplies a value for each run.
        :param m: the number of timed runs.
        :return: the mean time for one run, in milliseconds.
        """
        logger.info(lambda: f"Begin run: {self._description} with {format_whole(m)} runs")

        def function(t: T) -> T:
            self._f_run(t)
            return t

        # Warmup phase
        Timer.from_config(self._config).repeat(
            get_warmup_runs(m), supplier, function, self._f_pre, None, warmup=True)

        # Timed phase
        return Timer.from_config(self._config).repeat(
            m, supplier, function, self._f_pre, self._f_post, warmup=False)
