"""
Benchmarking of the three-sum implementations, ported from
adt/threesum/ThreeSumBenchmark.java.
"""

from __future__ import annotations

from collections.abc import Callable

from src.adt.threesum.source import Source
from src.adt.threesum.three_sum_cubic import ThreeSumCubic
from src.adt.threesum.three_sum_quadratic import ThreeSumQuadratic
from src.adt.threesum.three_sum_quadrithmic import ThreeSumQuadrithmic
from src.util.benchmark.time_logger import TimeLogger
from src.util.config.config import get_config
from src.util.general.utilities import lg

#: Loggers for the cubic implementation: the raw time, and the time divided by
#: n^3/6 -- the number of triples considered. If the complexity is right, the
#: normalized figure stays flat as n grows.
TIME_LOGGERS_CUBIC = [
    TimeLogger("Raw time per run (mSec): ", None),
    TimeLogger("Normalized time per run (n^3): ", lambda n: 1.0 / 6 * n * n * n),
]

#: Loggers for the quadrithmic implementation, normalized by n^2 lg n.
TIME_LOGGERS_QUADRITHMIC = [
    TimeLogger("Raw time per run (mSec): ", None),
    TimeLogger("Normalized time per run (n^2 log n): ", lambda n: n * n * lg(n)),
]

#: Loggers for the quadratic implementation, normalized by n^2/2.
TIME_LOGGERS_QUADRATIC = [
    TimeLogger("Raw time per run (mSec): ", None),
    TimeLogger("Normalized time per run (n^2): ", lambda n: 1.0 / 2 * n * n),
]

#: Above this size the cubic implementation is skipped: it would take longer than
#: the rest of the sweep put together, and its growth is already clear.
CUBIC_LIMIT = 4000


class ThreeSumBenchmark:
    """
    Times the three three-sum implementations on the same generated data.

    The point of the sweep is not the raw times but the normalized ones. Each
    implementation is divided by its own predicted growth, so a row which stays
    flat confirms the prediction and a row which drifts refutes it.
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
        self._config = get_config(ThreeSumBenchmark)

    def run_benchmarks(self) -> None:
        """
        Run all three implementations, logging the results.
        """
        print(f"ThreeSumBenchmark: N={self._n}")
        self.benchmark_three_sum(
            "ThreeSumQuadratic", lambda xs: ThreeSumQuadratic(xs).get_triples(),
            self._n, TIME_LOGGERS_QUADRATIC)
        self.benchmark_three_sum(
            "ThreeSumQuadrithmic", lambda xs: ThreeSumQuadrithmic(xs).get_triples(),
            self._n, TIME_LOGGERS_QUADRITHMIC)
        self.benchmark_three_sum(
            "ThreeSumCubic", lambda xs: ThreeSumCubic(xs).get_triples(),
            self._n, TIME_LOGGERS_CUBIC)

    def benchmark_three_sum(self, description: str, function: Callable[[list[int]], object],
                            n: int, time_loggers: list[TimeLogger]) -> None:
        """
        Time one implementation and log the result through each of the loggers.

        :param description: which implementation, for the log.
        :param function: the implementation, as something to apply to a list.
        :param n: the size of the problem, which the loggers normalize by.
        :param time_loggers: how to report the result.
        """
        if description == "ThreeSumCubic" and n > CUBIC_LIMIT:
            return
        # TO BE IMPLEMENTED : run function over self._supplier for self._runs runs
        # using Benchmark_Timer, then log the mean time through each TimeLogger.
        raise NotImplementedError("TO BE IMPLEMENTED")


def main() -> None:  # pragma: no cover - a driver, not part of the library
    """
    Sweep n from 250 to 16000, halving the number of runs as n doubles.
    """
    for runs, n in [(100, 250), (50, 500), (20, 1000), (10, 2000),
                    (5, 4000), (3, 8000), (2, 16000)]:
        ThreeSumBenchmark(runs, n, n).run_benchmarks()


if __name__ == "__main__":  # pragma: no cover
    main()
