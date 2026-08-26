"""
The do-nothing Instrument, ported from sort/helper/InstrumenterDummy.java.

This is what a sort uses when instrumentation is switched off. Every method is
empty or returns zero, so the sort runs at full speed and the counting code does
not have to be written twice -- once with counters and once without.
"""

from __future__ import annotations

from src.sort.helper.instrument import Instrument
from src.util.benchmark.stat_pack import StatPack
from src.util.config.config import Config


class InstrumenterDummy(Instrument):
    """
    An Instrument which counts nothing.
    """

    def __init__(self, config: Config | None = None) -> None:
        """
        :param config: accepted and ignored, so that this can stand in for
                       Instrumenter wherever one is built from configuration.
        """

    def init(self, n: int, n_runs: int) -> None:
        pass

    def get_stat_pack(self) -> StatPack | None:
        """
        :return: None. There are no statistics, because nothing was counted.
        """
        return None

    def get_compares(self) -> int:
        return 0

    def get_swaps(self) -> int:
        return 0

    def get_fixes(self) -> int:
        return 0

    def get_hits(self) -> int:
        return 0

    def get_lookups(self) -> int:
        return 0

    def get_copies(self) -> int:
        return 0

    def increment_copies(self, n: int) -> None:
        pass

    def increment_hits(self, n: int) -> None:
        pass

    def increment_lookups(self, n: int) -> None:
        pass

    def increment_fixes(self, n: int) -> None:
        pass

    def increment_compares(self) -> None:
        pass

    def increment_swaps(self, n: int) -> None:
        pass

    def count_fixes(self) -> bool:
        return False

    def gather_statistic(self) -> None:
        pass

    def is_show_stats(self) -> bool:
        return False

    def __str__(self) -> str:
        return "InstrumenterDummy{}"
