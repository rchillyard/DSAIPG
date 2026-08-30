"""
Insertion sort through a Helper, ported from
sort/elementary/InsertionSortComparator.java.

The Java has this as a separate class from InsertionSort because one is written
against a Comparator and the other against Comparable. Here they differ in what
they actually do: this one works entirely through swap_stable_conditional, which
is the version the book uses to make the "one swap per inversion" property
visible.

That property is what count_inversions relies on.
"""

from __future__ import annotations

from collections.abc import Callable
from typing import TypeVar

from src.sort.generic.sort_with_helper import SortWithHelper
from src.sort.helper.instrumented_helper import get_runs_config
from src.util.config.config_benchmark import setup_config_fixes

X = TypeVar("X")

DESCRIPTION = "Insertion sort"


class InsertionSortComparator(SortWithHelper[X]):
    """
    Insertion sort, exchanging adjacent elements through the Helper.
    """

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs between from_ and to.

        Each element moves down while it is smaller than the one below it. Every
        exchange fixes exactly one inversion, so the number of exchanges is the
        number of inversions in the input.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """
        helper = self.get_helper()  # noqa: F841 - part of the skeleton; the exercise uses it
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")


def string_sorter_case_insensitive(n: int, config) -> InsertionSortComparator[str]:
    """
    :param n: the number of strings to be sorted.
    :param config: the configuration.
    :return: a sorter which orders strings without regard to case.
    """
    return InsertionSortComparator.from_config(
        DESCRIPTION, config, n,
        comparator=lambda v, w: (v.lower() > w.lower()) - (v.lower() < w.lower()),
        n_runs=get_runs_config(config))


def count_inversions_by_sorting(ts: list[X], comparator: Callable[[X, X], int]) -> int:
    """
    Count the inversions in a list by sorting a copy of it and asking how many
    inversions the sort had to fix.

    This is the Java's method, and it is the interesting one pedagogically:
    insertion sort fixes exactly one inversion per exchange, so its own counter
    is the answer. It costs n squared, which is why
    ``instrumented_helper.count_inversions`` computes the same number while
    merging when the count is wanted for real work.

    :param ts: the list.
    :param comparator: the comparison function.
    :return: the number of inversions.
    """
    config = setup_config_fixes()
    with InsertionSortComparator.from_config(
            DESCRIPTION, config, len(ts), comparator=comparator,
            n_runs=get_runs_config(config)) as sorter:
        helper = sorter.get_helper()
        sorter.sort(ts, make_copy=True)
        return helper.get_fixes()
