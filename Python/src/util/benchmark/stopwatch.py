"""
A stopwatch for timing laps, ported from util/benchmark/Stopwatch.java.

The Java implements AutoCloseable, so it is used with try-with-resources; here
it is a context manager, used with ``with``.
"""

from __future__ import annotations

import time

MILLION = 1_000_000

_TIME_FACTORS = {
    "milliseconds": MILLION,
    "seconds": 1_000 * MILLION,
    "microseconds": 1_000,
    "nanoseconds": 1,
}


class Stopwatch:
    """
    Time successive laps, in units fixed when the Stopwatch is created.

    Each call to ``lap`` reports the time since the previous call (or since
    construction) and starts the next lap.
    """

    def __init__(self, units: str | None = "milliseconds") -> None:
        """
        :param units: one of "nanoseconds", "microseconds", "milliseconds" or
                      "seconds". None means milliseconds.
        :raises ValueError: if units is none of those.
        """
        self._time_factor = _calculate_time_factor(units)
        self._start: int | None = _clock()

    def lap(self) -> int:
        """
        End the current lap and begin the next.

        :return: the duration of the lap just ended, in this Stopwatch's units.
        """
        assert self._start is not None, "Stopwatch is closed"
        lap_start = self._start
        self._start = _clock()
        # NOTE each reading is divided before the subtraction, following the
        # Java. That is not the same as dividing the difference: it can be off by
        # one unit, because the two readings truncate independently.
        return self._start // self._time_factor - lap_start // self._time_factor

    def close(self) -> None:
        """
        Close this Stopwatch. Calling ``lap`` afterwards is an error.
        """
        self._start = None

    def __enter__(self) -> Stopwatch:
        return self

    def __exit__(self, exc_type: object, exc_value: object, traceback: object) -> None:
        self.close()


def _calculate_time_factor(units: str | None) -> int:
    """
    :param units: the name of the units.
    :return: the number of nanoseconds in one of those units.
    :raises ValueError: if the units are not recognized.
    """
    if units is None:
        return MILLION
    factor = _TIME_FACTORS.get(units)
    if factor is None:
        raise ValueError(f"Invalid time units: {units}")
    return factor


def _clock() -> int:
    """
    :return: the reading of a monotonic clock, in nanoseconds. Monotonic because
             the wall clock can move backwards, which would give a negative lap.
    """
    return time.monotonic_ns()
