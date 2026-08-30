"""
The Instrument abstraction, ported from sort/helper/Instrument.java.

An Instrument counts what a sort actually does: how many comparisons it makes,
how many elements it swaps or copies, how many times it touches the list. Those
counts are the point of the exercise -- they are what let you check a sort
against its predicted growth, independently of how fast the machine happens to
be that day.
"""

from __future__ import annotations

from abc import ABC, abstractmethod

from src.util.benchmark.stat_pack import StatPack

#: Names of the things that can be counted. These double as the keys of the
#: [instrumenting] section of config.ini, and as the keys of the StatPack.
SWAPS = "swaps"
COMPARES = "compares"
COPIES = "copies"
INVERSIONS = "inversions"
FIXES = "fixes"
HITS = "hits"
LOOKUPS = "lookups"

#: The name of the [instrumenting] section itself.
INSTRUMENTING = "instrumenting"

#: The option controlling whether statistics are printed at the end of a run.
SHOW_STATS = "showStats"


class Instrument(ABC):
    """
    Something which counts the work a sort does.
    """

    @abstractmethod
    def init(self, n: int, n_runs: int) -> None:
        """
        Prepare to count, for n_runs runs over a problem of size n.

        :param n: the size of the problem.
        :param n_runs: the number of runs.
        """

    @abstractmethod
    def get_stat_pack(self) -> StatPack | None:
        """
        :return: the accumulated statistics.
        """

    @abstractmethod
    def get_compares(self) -> int:
        """:return: the number of comparisons since the last gather."""

    @abstractmethod
    def get_swaps(self) -> int:
        """:return: the number of swaps since the last gather."""

    @abstractmethod
    def get_fixes(self) -> int:
        """:return: the number of inversions fixed since the last gather."""

    @abstractmethod
    def get_hits(self) -> int:
        """:return: the number of list accesses since the last gather."""

    @abstractmethod
    def get_lookups(self) -> int:
        """:return: the number of lookups since the last gather."""

    @abstractmethod
    def get_copies(self) -> int:
        """:return: the number of copies since the last gather."""

    @abstractmethod
    def increment_copies(self, n: int) -> None:
        """:param n: the number of copies to add."""

    @abstractmethod
    def increment_hits(self, n: int) -> None:
        """:param n: the number of list accesses to add."""

    @abstractmethod
    def increment_lookups(self, n: int) -> None:
        """:param n: the number of lookups to add."""

    @abstractmethod
    def increment_fixes(self, n: int) -> None:
        """:param n: the number of fixed inversions to add."""

    @abstractmethod
    def increment_compares(self) -> None:
        """Count one comparison."""

    @abstractmethod
    def increment_swaps(self, n: int) -> None:
        """:param n: the number of swaps to add."""

    @abstractmethod
    def count_fixes(self) -> bool:
        """
        :return: true if inversions fixed are being counted. Counting them is
                 expensive, so it is off by default.
        """

    @abstractmethod
    def gather_statistic(self) -> None:
        """
        Add the current counts to the statistics and reset them, ready for the
        next run.
        """

    @abstractmethod
    def is_show_stats(self) -> bool:
        """
        :return: true if statistics should be printed at the end of a run.
        """
