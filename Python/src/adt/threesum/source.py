"""
A source of test data for the two-sum and three-sum benchmarks, ported from
adt/threesum/Source.java.
"""

from __future__ import annotations

from collections.abc import Callable
from random import Random


class Source:
    """
    Generates lists of n distinct ordered ints drawn from the range -m/2 to m/2.

    Ordered and distinct because that is what the faster algorithms require. The
    generator therefore cannot simply take n random values: duplicates would
    break the very implementations being measured.

    NOTE the sequence differs from the Java's. Source uses ``java.util.Random``,
    whose generator is not Python's; QuickRandom is the one in this tree which
    reproduces Java exactly, and it is used where the two languages have to sort
    identical data. Here they do not -- each language only has to be measured
    against itself.
    """

    def __init__(self, n: int, m: int, seed: int | None = None,
                 random: Random | None = None) -> None:
        """
        :param n: how many ints to generate.
        :param m: the range: each int lies between -m/2 and m/2.
        :param seed: seeds a new Random, so a benchmark can be repeated.
        :param random: the source of entropy, taking precedence over seed. None
                       and no seed means an unseeded Random.
        """
        self._n = n
        self._m = m
        self._random = random if random is not None else Random(seed)

    def ints_supplier(self, safety_factor: int) -> Callable[[], list[int]]:
        """
        Build a supplier of n distinct ordered ints.

        The safety factor is why this works: it generates ``safety_factor * n``
        values over ``safety_factor * m``, discards the duplicates, and then
        samples n of what is left at even intervals. Generating n values directly
        would leave far fewer than n after deduplication.

        :param safety_factor: how much to over-generate by.
        :return: a function returning a fresh list each time it is called.
        """

        def supplier() -> list[int]:
            offset = safety_factor * self._m // 2
            values = [self._random.randrange(safety_factor * self._m) - offset
                      for _ in range(safety_factor * self._n)]
            distinct = sorted(set(values))
            # NOTE integer division, as in the Java. If fewer than n distinct
            # values survived, the stride is zero and every element of the result
            # is distinct[0] -- degenerate, but still of length n, which is why
            # the length check in the benchmarks can never fire.
            stride = len(distinct) // self._n
            return [distinct[i * stride] for i in range(self._n)]

        return supplier
