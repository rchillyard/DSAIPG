"""
The counting Helper, ported from sort/helper/InstrumentedComparatorHelper.java
and InstrumentedComparableHelper.java.

Every method here overrides one that does the same work without counting. The
counts are not arbitrary: reading an element is one hit, and an exchange is one
swap plus two hits for the two assignments. The values a caller passes in are
values it has already read, so they are not counted again -- which is why there
are four ways to swap rather than one.
"""

from __future__ import annotations

from collections.abc import Callable
from random import Random
from typing import TypeVar

from src.sort.helper.base_helper import BaseHelper
from src.sort.helper.helper import _UNSET, Helper
from src.sort.helper.helper_exception import HelperException
from src.sort.helper.instrument import INSTRUMENTING, INVERSIONS, Instrument
from src.sort.helper.instrumenter import Instrumenter
from src.util.config.config import Config
from src.util.config.config_benchmark import HELPER, get_seed
from src.util.general.utilities import format_whole
from src.util.logging.lazy_logger import LazyLogger

X = TypeVar("X")

logger = LazyLogger(__name__)

#: The default number of runs, when [helper] runs is not set.
DEFAULT_RUNS = 1


def get_runs_config(config: Config) -> int:
    """
    :param config: the configuration.
    :return: the configured number of runs, or the default.
    """
    return config.get_int(HELPER, "runs", DEFAULT_RUNS)


class InstrumentedHelper(BaseHelper[X]):
    """
    A Helper which counts everything a sort does.
    """

    def __init__(self, description: str, config: Config,
                 comparator: Callable[[X, X], int] | None = None,
                 n: int = 0, random: Random | None = None,
                 n_runs: int | None = None,
                 instrumenter: Instrument | None = None) -> None:
        """
        :param description: a description, used when reporting results.
        :param config: the configuration.
        :param comparator: the comparison function; None means natural ordering.
        :param n: the number of elements to be managed, if known yet.
        :param random: the source of random elements; None means seed from the
                       configuration.
        :param n_runs: the number of runs; None means read it from the
                       configuration.
        :param instrumenter: the Instrument to count into; None means build one
                             from the configuration.
        """
        if random is None:
            random = Random(get_seed(config))
        if instrumenter is None:
            instrumenter = Instrumenter.from_config(config)
        super().__init__(description, config, comparator, n, random, instrumenter)
        self.count_inversions = config.get_int(INSTRUMENTING, INVERSIONS, 0)
        self.n_runs = get_runs_config(config) if n_runs is None else n_runs
        self.max_depth_reached = 0

    def instrumented(self) -> bool:
        return True

    # ---- reading and writing --------------------------------------------

    def get(self, xs: list[X], i: int) -> X:
        """
        Read xs[i], counting one hit.
        """
        self.instrumenter.increment_hits(1)
        return xs[i]

    def copy(self, x: X, target: list[X], j: int) -> None:
        """
        Copy a value into target[j]: one copy and one hit for the assignment.
        """
        self.instrumenter.increment_copies(1)
        self.instrumenter.increment_hits(1)
        target[j] = x

    def copy_at(self, source: list[X], i: int, target: list[X], j: int) -> None:
        self.copy(self.get(source, i), target, j)

    def copy_block(self, source: list[X], i: int, target: list[X], j: int, n: int) -> None:
        """
        Copy a block: n copies, and a hit for each element read and written.

        NOTE 2n whether or not the source and target are the same list: n
        elements are read and n are written either way. This used to charge
        n + 1 for the same-list case, following the Java, whose comment asked
        whether that was right. It was not -- counting the accesses a list
        actually performs shows 2n, and InsertionSortOpt, which is nothing but
        block moves, was reporting 55% of the accesses it made.
        """
        super().copy_block(source, i, target, j, n)
        self.instrumenter.increment_copies(n)
        self.instrumenter.increment_hits(2 * n)

    def distribute_block(self, source: list[X], from_: int, to: int, target: list[X],
                         f: Callable[[X], int]) -> None:
        super().distribute_block(source, from_, to, target, f)
        self.instrumenter.increment_copies(to - from_)
        self.instrumenter.increment_hits((to - from_) * 2)

    def copy_array(self, a: list[X]) -> list[X]:
        self.instrumenter.increment_copies(len(a))
        self.instrumenter.increment_hits(2 * len(a))
        return super().copy_array(a)

    # ---- swapping --------------------------------------------------------

    def swap(self, xs: list[X], i: int, j: int) -> None:
        """
        Exchange xs[i] and xs[j], reading both: four hits in total, and one swap.
        """
        assert i != j
        self.swap_v(self.get(xs, i), xs, i, j)

    def swap_v(self, v: X, xs: list[X], i: int, j: int) -> None:
        """
        Exchange when xs[i] is already in hand: three hits, and one swap.
        """
        assert i != j
        self.swap_vw(v, self.get(xs, j), xs, i, j)

    def swap_w(self, w: X, xs: list[X], i: int, j: int) -> None:
        """
        Exchange when xs[j] is already in hand: three hits, and one swap.
        """
        assert i != j
        self.swap_vw(self.get(xs, i), w, xs, i, j)

    def swap_vw(self, v: X, w: X, xs: list[X], i: int, j: int) -> None:
        """
        Exchange when both values are in hand: two hits for the assignments, and
        one swap. Nothing is read, because nothing needs to be.

        NOTE unlike the other three, this is expected to be called with i == j
        sometimes, and returns without counting anything when it is.
        """
        if i == j:
            return
        self.instrumenter.increment_swaps(1)
        if self.instrumenter.count_fixes():
            self._enumerate_fixes(xs, i, j, _signum(self.pure_comparison(v, w)))
        if logger.is_debug_enabled():
            _check_element_consistency(xs, v, i, j, w)
        # NOTE these are the two assignments. The reads, if any, were counted by
        # whoever did them.
        self.instrumenter.increment_hits(2)
        super().swap_vw(v, w, xs, i, j)

    def swap_into(self, xs: list[X], i: int, j: int, x: X = _UNSET) -> None:
        """
        Move xs[j] down to index i: one swap, one fix for each element shifted
        up, and a hit for the final assignment.

        NOTE the copies are counted by copy_block, not here. Counting them in
        both places made every half-swap report twice as many copies as it
        performed, which is what the Java did until this was corrected.
        """
        if x is _UNSET:
            x = self.get(xs, j)
        self.instrumenter.increment_swaps(1)
        self.instrumenter.increment_fixes(j - i)
        self.instrumenter.increment_hits(1)
        if j > i:
            self.copy_block(xs, i, xs, i + 1, j - i)
            xs[i] = x

    # ---- comparing -------------------------------------------------------

    def compare(self, v: X, w: X) -> int:
        """
        Compare two values, counting one comparison.
        """
        self.instrumenter.increment_compares()
        return self.pure_comparison(v, w)

    def compare_v(self, xs: list[X], v: X, j: int) -> int:
        return self.compare(v, self.get(xs, j))

    def compare_w(self, xs: list[X], i: int, w: X) -> int:
        return self.compare(self.get(xs, i), w)

    def compare_at(self, xs: list[X], i: int, j: int) -> int:
        """
        Compare xs[i] with xs[j].

        NOTE comparing an element with itself is free, and counts nothing.
        """
        if i == j:
            return 0
        return self.compare_w(xs, i, self.get(xs, j))

    # ---- inversions ------------------------------------------------------

    def not_inverted_v(self, xs: list[X], v: X, j: int) -> bool:
        return self.not_inverted(v, self.get(xs, j))

    def not_inverted_w(self, xs: list[X], i: int, w: X) -> bool:
        return self.not_inverted(self.get(xs, i), w)

    def not_inverted_at(self, xs: list[X], i: int, j: int) -> bool:
        return self.not_inverted_v(xs, self.get(xs, i), j)

    def not_inverted_with_lookups(self, xs: list[X], i: int, j: int, lookups: int) -> bool:
        assert 0 <= lookups <= 2
        self.increment_lookups(lookups)
        return self.not_inverted_at(xs, i, j)

    def inversions(self, xs: list[X]) -> int:
        """
        :param xs: the list.
        :return: the number of inversions.
        """
        return count_inversions(xs, self.get_comparator())

    # ---- conditional swaps ----------------------------------------------

    def swap_conditional(self, xs: list[X], i: int, j: int) -> bool:
        return self.swap_conditional_v(xs, self.lookup(self.get(xs, i)), i, j)

    def swap_conditional_w(self, xs: list[X], i: int, j: int, w: X) -> bool:
        return self.swap_conditional_vw(xs, self.lookup(self.get(xs, i)), i, j, w)

    def swap_conditional_v(self, xs: list[X], v: X, i: int, j: int) -> bool:
        return self.swap_conditional_vw(xs, v, i, j, self.lookup(self.get(xs, j)))

    def swap_stable_conditional(self, xs: list[X], i: int) -> bool:
        return self.swap_conditional(xs, i - 1, i)

    # ---- depth, initialization and reporting ----------------------------

    def init(self, n: int) -> None:
        """
        Prepare to sort n elements, and prepare the Instrument too.
        """
        self.instrumenter.init(n, self.n_runs)
        if n == self.n:
            return
        super().init(n)

    def pre_process(self, xs: list[X]) -> list[X]:
        """
        Count the inversions in the list before it is sorted.

        NOTE counting inversions is slow, so it is done for only a configured
        number of samples, after which count_inversions has run down to zero.
        """
        result = super().pre_process(xs)
        if self.count_inversions > 0:
            self.count_inversions -= 1
            stat_pack = self.instrumenter.get_stat_pack()
            if stat_pack is None:
                raise HelperException("InstrumentedHelper.pre_process: no StatPack")
            stat_pack.add(INVERSIONS, self.inversions(result))
        return result

    def post_process(self, xs: list[X]) -> None:
        """
        Check that the list really is sorted, then gather the statistics.

        :raises HelperException: if the list is not sorted, naming the place
                                 where the order first goes wrong.
        """
        super().post_process(xs)
        index = self.find_inversion(xs)
        if index != -1:
            raise HelperException(
                f"{self}: Array is not sorted at index: {index}: {xs[index - 1]}, {xs[index]}")
        self.instrumenter.gather_statistic()

    def register_depth(self, depth: int) -> None:
        if depth > self.max_depth_reached:
            self.max_depth_reached = depth

    def max_depth(self) -> int:
        return self.max_depth_reached

    def show_stats(self, context: str | None = None) -> str:
        """
        :param context: what the statistics describe, if anything.
        :return: the statistics as text.
        """
        stat_pack = self.instrumenter.get_stat_pack()
        where = self.description if context is None else f"{self.description}/{context}"
        return f"{where}: {stat_pack}"

    def clone(self, description: str, n: int | None = None,
              comparator: Callable[[X, X], int] | None = None,
              share_instrumenter: bool = False) -> Helper[X]:
        """
        Make an instrumented Helper like this one.

        :param description: the description for the new Helper.
        :param n: the number of elements, defaulting to this Helper's.
        :param comparator: the comparison function, defaulting to this one's.
        :param share_instrumenter: if true, the clone counts into this Helper's
                                   Instrument, so that a hybrid sort reports one
                                   set of totals rather than two.
        :return: the new Helper.
        """
        return InstrumentedHelper(
            description, self.config,
            comparator if comparator is not None else self.comparator,
            self.n if n is None else n,
            self.random_source,
            self.n_runs,
            self.instrumenter if share_instrumenter else None,
        )

    def _enumerate_fixes(self, xs: list[X], i: int, j: int, sense: int) -> None:
        """
        Count the inversions that this one exchange puts right.

        An exchange of xs[i] and xs[j] fixes the pair itself, and also two
        inversions for every element between them that lies in value between the
        two -- which is why counting fixes costs so much more than sorting.

        :param xs: the list.
        :param i: the lower index.
        :param j: the upper index.
        :param sense: 1 if the pair was inverted, -1 if it was not.
        """
        self.instrumenter.increment_fixes(sense)
        v = xs[i]
        w = xs[j]
        for k in range(i + 1, j):
            x = xs[k]
            if self.pure_comparison(w, x) < 0 and self.pure_comparison(x, v) < 0:
                self.instrumenter.increment_fixes(2 * sense)

    def __str__(self) -> str:
        return f"Instrumenting helper for {self.description} with {format_whole(self.n)} elements"


def count_inversions(xs: list[X], comparator: Callable[[X, X], int]) -> int:
    """
    Count the inversions in a list: the number of pairs that are out of order.

    NOTE the Java gets this number by running an instrumented insertion sort over
    a copy and reading its fix count, which works because insertion sort fixes
    exactly one inversion per swap. This counts them directly while merging,
    which gives the same answer in n log n rather than n squared -- and the
    equivalence is worth checking once InsertionSortComparator is ported.

    :param xs: the list.
    :param comparator: the comparison function.
    :return: the number of inversions.
    """
    def merge_count(values: list[X]) -> tuple[list[X], int]:
        if len(values) < 2:
            return values, 0
        middle = len(values) // 2
        left, a = merge_count(values[:middle])
        right, b = merge_count(values[middle:])
        merged: list[X] = []
        count = a + b
        i = j = 0
        while i < len(left) and j < len(right):
            if comparator(right[j], left[i]) < 0:
                # right[j] is smaller, so it is inverted with every remaining
                # element of left.
                count += len(left) - i
                merged.append(right[j])
                j += 1
            else:
                merged.append(left[i])
                i += 1
        merged.extend(left[i:])
        merged.extend(right[j:])
        return merged, count

    return merge_count(list(xs))[1]


def _signum(x: int) -> int:
    """
    :param x: a number.
    :return: -1, 0 or 1 according to its sign.
    """
    return (x > 0) - (x < 0)


def _check_element_consistency(xs: list[X], v: X, i: int, j: int, w: X) -> None:
    """
    Warn if the values a caller passed in are not the ones actually in the list.

    Passing a stale value is the one way the _v and _w swap variants can go
    wrong, so this is worth checking -- but only when debug logging is on,
    because it defeats the point of passing the values in at all.

    :param xs: the list.
    :param v: the value the caller believes is at i.
    :param i: the lower index.
    :param j: the upper index.
    :param w: the value the caller believes is at j.
    """
    if xs[i] is not v and xs[i] != v:
        logger.warn(lambda: f"swap: WARNING: v={v} is not equal to xs[{i}]: {xs[i]}")
    if xs[j] is not w and xs[j] != w:
        logger.warn(lambda: f"swap: WARNING: w={w} is not equal to xs[{j}]: {xs[j]}")
