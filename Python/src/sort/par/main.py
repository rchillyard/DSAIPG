"""
Measuring where the parallel cutoff should be, ported in spirit from
sort/par/Main.java.

The Java sweeps the cutoff from 500,000 down over a two-million element array and
writes the timings to a CSV. This does the same thing as a function that returns
the timings, so it can be called from a notebook or a test rather than only from
a command line.

What the sweep shows is not the same in the two languages, and that is the
interesting part. Java's threads share the heap, so splitting costs almost
nothing and a fairly small cutoff pays. Here every chunk is serialized, sent to
another process, and the result sent back, so the cutoff has to be large enough
for the sorting to outweigh that. Running this is how you find out how much
larger.
"""

from __future__ import annotations

import random
import time
from array import array
from concurrent.futures import ProcessPoolExecutor

from src.sort.par import par_sort


def random_array(n: int, seed: int = 0, bound: int = 10_000_000) -> array:
    """
    :param n: how many values.
    :param seed: the seed, so a sweep compares like with like.
    :param bound: one more than the largest value.
    :return: an array of n random values.
    """
    rng = random.Random(seed)
    return array(par_sort.TYPE_CODE, [rng.randrange(bound) for _ in range(n)])


def time_sort(xs: array, cutoff: int, executor: ProcessPoolExecutor | None,
              repetitions: int = 1) -> float:
    """
    Time sorting a copy of xs, at a given cutoff.

    :param xs: the values; a fresh copy is sorted each time, so the timing is not
               flattered by the array already being in order.
    :param cutoff: the size below which to sort sequentially.
    :param executor: where to run the halves, or None for no parallelism at all.
    :param repetitions: how many times to sort.
    :return: the mean time for one sort, in seconds.
    """
    original = par_sort.cutoff
    par_sort.cutoff = cutoff
    try:
        start = time.perf_counter()
        for _ in range(repetitions):
            values = array(par_sort.TYPE_CODE, xs)
            par_sort.sort(values, 0, len(values), executor)
        return (time.perf_counter() - start) / repetitions
    finally:
        par_sort.cutoff = original


def sweep(n: int = 1_000_000, cutoffs: list[int] | None = None,
          repetitions: int = 1) -> list[tuple[int, float]]:
    """
    Time the sort at a range of cutoffs, and sequentially for comparison.

    :param n: how many values to sort.
    :param cutoffs: the cutoffs to try. None uses a spread from an eighth of n up
                    to n, the last of which means no parallelism at all.
    :param repetitions: how many sorts to time at each cutoff.
    :return: pairs of cutoff and mean seconds, with a cutoff of n meaning the
             sequential case.
    """
    if cutoffs is None:
        cutoffs = [n // 8, n // 4, n // 2, n]
    xs = random_array(n)
    results = []
    with ProcessPoolExecutor() as executor:
        for c in cutoffs:
            results.append((c, time_sort(xs, c, executor, repetitions)))
    return results


def main() -> None:  # pragma: no cover - a driver, not part of the library
    """Print a cutoff sweep, as the Java's main does."""
    import os
    n = 1_000_000
    print(f"  cores available: {os.cpu_count()}")
    print(f"  sorting {n:,} ints\n")
    print(f"  {'cutoff':>10}  {'seconds':>8}")
    for c, seconds in sweep(n):
        label = f"{c:,}" + (" (sequential)" if c >= n else "")
        print(f"  {label:>10}  {seconds:>8.3f}")


if __name__ == "__main__":  # pragma: no cover
    main()
