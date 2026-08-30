"""
The working Instrument, ported from sort/helper/Instrumenter.java.

Each kind of count can be switched off independently, through the
[instrumenting] section of config.ini. That matters because the counts are not
equally cheap: counting fixes means detecting inversions, which is far more work
than the sort itself.
"""

from __future__ import annotations

from src.sort.helper.instrument import (
    COMPARES,
    COPIES,
    FIXES,
    HITS,
    INSTRUMENTING,
    INVERSIONS,
    LOOKUPS,
    SHOW_STATS,
    SWAPS,
    Instrument,
)
from src.util.benchmark.stat_pack import StatPack, empty
from src.util.benchmark.statistics import normalizer_linearithmic_natural
from src.util.config.config import Config


class Instrumenter(Instrument):
    """
    An Instrument which really counts.
    """

    def __init__(self, count_copies: bool, count_swaps: bool, count_compares: bool,
                 count_fixes: bool, count_hits: bool, count_lookups: bool,
                 show_stats: bool) -> None:
        """
        :param count_copies: whether to count copies.
        :param count_swaps: whether to count swaps.
        :param count_compares: whether to count comparisons.
        :param count_fixes: whether to count inversions fixed.
        :param count_hits: whether to count list accesses.
        :param count_lookups: whether to count lookups.
        :param show_stats: whether statistics should be printed at the end.
        """
        self.counting_copies = count_copies
        self.counting_swaps = count_swaps
        self.counting_compares = count_compares
        self.counting_fixes = count_fixes
        self.counting_hits = count_hits
        self.counting_lookups = count_lookups
        self.show_stats = show_stats
        self.stat_pack: StatPack | None = None
        self.compares = 0
        self.swaps = 0
        self.copies = 0
        self.fixes = 0
        self.hits = 0
        self.lookups = 0

    @classmethod
    def from_config(cls, config: Config) -> Instrumenter:
        """
        Build an Instrumenter from the [instrumenting] section of a Config.

        :param config: the configuration.
        :return: a new Instrumenter.
        """
        return cls(
            config.get_boolean(INSTRUMENTING, COPIES),
            config.get_boolean(INSTRUMENTING, SWAPS),
            config.get_boolean(INSTRUMENTING, COMPARES),
            config.get_boolean(INSTRUMENTING, FIXES),
            config.get_boolean(INSTRUMENTING, HITS),
            config.get_boolean(INSTRUMENTING, LOOKUPS),
            config.get_boolean(INSTRUMENTING, SHOW_STATS),
        )

    def init(self, n: int, n_runs: int) -> None:
        """
        Reset the counters and, the first time only, create the statistics.

        NOTE it is an error to replace the StatPack if we have been here before:
        doing so would throw away everything gathered so far, which is precisely
        what a second run is meant to add to.

        :param n: the size of the problem.
        :param n_runs: the number of runs.
        """
        self._reset_counters()
        if self.stat_pack is not None:
            return
        self.stat_pack = StatPack(normalizer_linearithmic_natural, n_runs, n,
                                  COMPARES, SWAPS, COPIES, INVERSIONS, FIXES, HITS, LOOKUPS)

    def get_stat_pack(self) -> StatPack:
        """
        :return: the statistics, or an empty StatPack if init has not been called.
        """
        return self.stat_pack if self.stat_pack is not None else empty()

    def get_compares(self) -> int:
        return self.compares

    def get_swaps(self) -> int:
        return self.swaps

    def get_fixes(self) -> int:
        return self.fixes

    def get_hits(self) -> int:
        return self.hits

    def get_lookups(self) -> int:
        return self.lookups

    def get_copies(self) -> int:
        return self.copies

    def increment_copies(self, n: int) -> None:
        if self.counting_copies:
            self.copies += n

    def increment_hits(self, n: int) -> None:
        if self.counting_hits:
            self.hits += n

    def increment_lookups(self, n: int) -> None:
        if self.counting_lookups:
            self.lookups += n

    def increment_fixes(self, n: int) -> None:
        if self.counting_fixes:
            self.fixes += n

    def increment_compares(self) -> None:
        if self.counting_compares:
            self.compares += 1

    def increment_swaps(self, n: int) -> None:
        if self.counting_swaps:
            self.swaps += n

    def count_fixes(self) -> bool:
        return self.counting_fixes

    def is_show_stats(self) -> bool:
        return self.show_stats

    def gather_statistic(self) -> None:
        """
        Add the current counts to the statistics and reset them.

        NOTE the Java guards against getStatPack() being null here and throws a
        HelperException. It cannot be: getStatPack falls back to an empty
        StatPack, which is invalid, so the next test returns first. The guard is
        dropped rather than reproduced.
        """
        stat_pack = self.get_stat_pack()
        if stat_pack.is_invalid():
            return
        if self.counting_compares:
            stat_pack.add(COMPARES, self.get_compares())
        if self.counting_swaps:
            stat_pack.add(SWAPS, self.get_swaps())
        if self.counting_copies:
            stat_pack.add(COPIES, self.get_copies())
        if self.counting_fixes:
            stat_pack.add(FIXES, self.get_fixes())
        if self.counting_hits:
            stat_pack.add(HITS, self.get_hits())
        if self.counting_lookups:
            stat_pack.add(LOOKUPS, self.get_lookups())
        self._reset_counters()

    def __str__(self) -> str:
        return (f"Instrumenter{{compares={self.compares}, copies={self.copies}, "
                f"fixes={self.fixes}, hits={self.hits}, lookups={self.lookups}, "
                f"swaps={self.swaps}}}")

    def _reset_counters(self) -> None:
        """Set every counter back to zero, ready for the next run."""
        self.compares = 0
        self.swaps = 0
        self.copies = 0
        self.fixes = 0
        self.hits = 0
        self.lookups = 0
