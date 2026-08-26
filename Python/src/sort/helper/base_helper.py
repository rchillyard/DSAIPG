"""
The common part of every Helper, ported from sort/helper/BaseHelper.java and
BaseComparatorHelper.java.

This holds the description, the comparison function, the random source, the
configuration and the Instrument, and delegates all the counting to that
Instrument. It knows nothing about whether the counting is real.

The Java splits this in two -- BaseHelper plus BaseComparableHelper or
BaseComparatorHelper -- only so that a Comparable type can be used without
supplying a Comparator. Here the comparator is optional and defaults to the
natural ordering, so one class does both.
"""

from __future__ import annotations

from abc import abstractmethod
from collections.abc import Callable
from random import Random
from typing import TypeVar

from src.sort.helper.helper import Helper, natural_comparison
from src.sort.helper.helper_exception import HelperException
from src.sort.helper.instrument import Instrument
from src.util.benchmark.stat_pack import StatPack
from src.util.config.config import Config
from src.util.config.config_benchmark import CUTOFF, CUTOFF_DEFAULT, HELPER
from src.util.general.utilities import fill_random_array

X = TypeVar("X")


class BaseHelper(Helper[X]):
    """
    A Helper which delegates its counting to an Instrument.
    """

    def __init__(self, description: str, config: Config,
                 comparator: Callable[[X, X], int] | None = None,
                 n: int = 0, random: Random | None = None,
                 instrumenter: Instrument | None = None) -> None:
        """
        :param description: a description, used when reporting results.
        :param config: the configuration.
        :param comparator: the comparison function; None means use the natural
                           ordering of the elements, which stands in for Java's
                           `X extends Comparable<X>`.
        :param n: the number of elements to be managed, if known yet.
        :param random: the source of random elements; None means seed from the
                       configuration.
        :param instrumenter: the Instrument to count into.
        """
        self.description = description
        self.config = config
        self.comparator = comparator if comparator is not None else natural_comparison
        self.n = n
        self.random_source = random if random is not None else Random()
        self.instrumenter = instrumenter
        self.configured_cutoff = config.get_int(HELPER, CUTOFF, 0)
        self.random_array: list[X] | None = None

    @abstractmethod
    def instrumented(self) -> bool:
        """
        :return: true if this Helper counts what it does.
        """

    # ---- the parts Helper requires ---------------------------------------

    def pure_comparison(self, v: X, w: X) -> int:
        return self.comparator(v, w)

    def get_comparator(self) -> Callable[[X, X], int]:
        return self.comparator

    def get_description(self) -> str:
        return self.description

    def get_config(self) -> Config:
        return self.config

    def get_n(self) -> int:
        return self.n

    def init(self, n: int) -> None:
        """
        Record the number of elements to be managed.

        :param n: the number of elements.
        :raises HelperException: if a different, non-zero, n was already set.
        """
        if self.n == 0 or self.n == n:
            self.n = n
        else:
            raise HelperException("Helper: n is already set to a different value")

    def cutoff(self) -> int:
        """
        :return: the configured cutoff, or the default.

        NOTE a cutoff of zero or less would make any recursive sort that used it
        recurse for ever, so anything below one is treated as unset.
        """
        return self.configured_cutoff if self.configured_cutoff >= 1 else CUTOFF_DEFAULT

    def close(self) -> None:
        pass

    def random(self, m: int, f: Callable[[Random], X]) -> list[X]:
        """
        Build a list of m random elements.

        :param m: the number of elements.
        :param f: builds one element from a Random.
        :return: the list.
        :raises HelperException: if m is not positive, which usually means the
                                 Helper was never initialized.
        """
        if m <= 0:
            raise HelperException(
                "Helper.random: requesting zero random elements (helper not initialized?)")
        self.random_array = fill_random_array(self.random_source, m, f)
        return self.random_array

    # ---- the Instrument methods, all delegated --------------------------

    def init_instrument(self, n: int, n_runs: int) -> None:
        """
        NOTE the Java names this init too, overloading on arity. Python cannot,
        and this one is called from a different place, so it gets its own name.

        :param n: the size of the problem.
        :param n_runs: the number of runs.
        """
        self.instrumenter.init(n, n_runs)

    def get_stat_pack(self) -> StatPack | None:
        return self.instrumenter.get_stat_pack()

    def get_compares(self) -> int:
        return self.instrumenter.get_compares()

    def get_swaps(self) -> int:
        return self.instrumenter.get_swaps()

    def get_fixes(self) -> int:
        return self.instrumenter.get_fixes()

    def get_hits(self) -> int:
        return self.instrumenter.get_hits()

    def get_lookups(self) -> int:
        return self.instrumenter.get_lookups()

    def get_copies(self) -> int:
        return self.instrumenter.get_copies()

    def increment_copies(self, n: int) -> None:
        self.instrumenter.increment_copies(n)

    def increment_hits(self, n: int) -> None:
        self.instrumenter.increment_hits(n)

    def increment_lookups(self, n: int) -> None:
        self.instrumenter.increment_lookups(n)

    def increment_fixes(self, n: int) -> None:
        self.instrumenter.increment_fixes(n)

    def increment_compares(self) -> None:
        self.instrumenter.increment_compares()

    def increment_swaps(self, n: int) -> None:
        self.instrumenter.increment_swaps(n)

    def count_fixes(self) -> bool:
        return self.instrumenter.count_fixes()

    def gather_statistic(self) -> None:
        self.instrumenter.gather_statistic()

    def is_show_stats(self) -> bool:
        return self.instrumenter.is_show_stats()
