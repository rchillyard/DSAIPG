"""
Benchmarking of the two-sum implementations, ported from
adt/threesum/TwoSumBenchmark.java.
"""

from __future__ import annotations

from collections.abc import Callable

from src.adt.threesum.source import Source
from src.adt.threesum.two_sum_quadratic import TwoSumQuadratic
from src.adt.threesum.two_sum_with_calipers import TwoSumWithCalipers
from src.util.benchmark.time_logger import TimeLogger
from src.util.config.config import get_config

#: Both implementations are logged against n^2/2. That is the honest choice even
#: though the calipers are linear: normalizing each by its own complexity would
#: hide the very difference the benchmark exists to show. Against a common yard-
#: stick the calipers' normalized figure falls away as n grows, which is what
#: being of a lower order looks like.
TIME_LOGGERS_QUADRATIC = [
    TimeLogger("Raw time per run (mSec): ", None),
    TimeLogger("Normalized time per run (n^2): ", lambda n: 1.0 / 2 * n * n),
]

#: Above this size nothing is benchmarked, because the quadratic implementation
#: takes too long. NOTE this skips the calipers too, as the Java does, even though
#: being linear they would run at 16000 without difficulty.
QUADRATIC_LIMIT = 8000


class TwoSumBenchmark:
    """
    Times the two two-sum implementations on the same generated data.
    """

    def __init__(self, runs: int, n: int, m: int, seed: int | None = None) -> None:
        """
        :param runs: how many times to run each implementation.
        :param n: how many values to give it.
        :param m: the range of those values, from -m/2 to m/2.
        :param seed: seeds the data, so a run can be repeated.
        """
        self._runs = runs
        self._supplier = Source(n, m, seed).ints_supplier(10)
        self._n = n
        self._config = get_config(TwoSumBenchmark)

    def run_benchmarks(self) -> None:
        """
        Run both implementations, logging the results.
        """
        print(f"TwoSumBenchmark: N={self._n}")
        self.benchmark_two_sum(
            "TwoSumWithCalipers", lambda xs: TwoSumWithCalipers(xs).get_pairs(),
            self._n, TIME_LOGGERS_QUADRATIC)
        self.benchmark_two_sum(
            "TwoSumQuadratic", lambda xs: TwoSumQuadratic(xs).get_pairs(),
            self._n, TIME_LOGGERS_QUADRATIC)

    def benchmark_two_sum(self, description: str, function: Callable[[list[int]], object],
                          n: int, time_loggers: list[TimeLogger]) -> None:
        """
        Time one implementation and log the result through each of the loggers.

        :param description: which implementation, for the log.
        :param function: the implementation, as something to apply to a list.
        :param n: the size of the problem, which the loggers normalize by.
        :param time_loggers: how to report the result.
        """
        if n > QUADRATIC_LIMIT:
            return
        # TO BE IMPLEMENTED : run function over self._supplier for self._runs runs
        # using Benchmark_Timer, then log the mean time through each TimeLogger.
        raise NotImplementedError("TO BE IMPLEMENTED")


def main() -> None:  # pragma: no cover - a driver, not part of the library
    """
    Sweep n from 250 to 16000; the largest two sizes are skipped as too slow.
    """
    for runs, n in [(100, 250), (50, 500), (20, 1000), (10, 2000),
                    (5, 4000), (3, 8000), (2, 16000)]:
        TwoSumBenchmark(runs, n, n).run_benchmarks()


if __name__ == "__main__":  # pragma: no cover
    main()
