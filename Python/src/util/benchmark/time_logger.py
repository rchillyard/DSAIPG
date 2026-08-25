"""
Logging of benchmark times, ported from util/benchmark/TimeLogger.java.

A TimeLogger reports either a raw time or a time normalized by the expected
growth of the algorithm. The normalized figure is the useful one: if the
complexity function is right, it stays roughly constant as N grows, and a
version that drifts is a version whose growth is not what you thought.
"""

from __future__ import annotations

from collections.abc import Callable

from src.util.logging.lazy_logger import LazyLogger

logger = LazyLogger(__name__)


class TimeLogger:
    """
    Log a time, optionally normalized by a complexity function.
    """

    def __init__(self, prefix: str, complexity: Callable[[int], float] | None) -> None:
        """
        :param prefix: describes what the logged number means.
        :param complexity: the expected growth, for example n -> n lg n. None
                           logs the raw time instead.
        """
        self._prefix = prefix
        self._complexity = complexity

    def log(self, description: str, time: float, n: int) -> None:
        """
        Log a time for a problem of size n.

        :param description: what was run.
        :param time: the measured time, in milliseconds.
        :param n: the size of the problem.
        """
        # NOTE the 1e6 turns the normalized figure into something with digits in
        # front of the decimal point, rather than a row of zeros.
        t = time if self._complexity is None else time / self._complexity(n) * 1e6
        logger.info(lambda: f"{description}: {self._prefix} {format_time(t)}")


def format_time(time: float) -> str:
    """
    Format a time to four decimal places.

    NOTE there is no leading zero: 0.5 formats as ".5000", not "0.5000". That is
    what the Java produces, because its DecimalFormat pattern is "######.0000",
    whose integer part is all "#" and so renders a zero as nothing at all.

    :param time: the time to format.
    :return: the formatted time.
    """
    result = f"{time:.4f}"
    if result.startswith("0."):
        return result[1:]
    if result.startswith("-0."):
        return "-" + result[2:]
    return result
