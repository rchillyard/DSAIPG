"""
The Sort abstraction, ported from sort/generic/Sort.java and ProcessingSort.java.

A Sort knows how to order a list in place between two indices. Everything else
-- sorting a whole list, sorting a copy, sorting a collection -- is built on
that one operation.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import Iterable, Sequence
from typing import Generic, TypeVar

X = TypeVar("X")


class Sort(ABC, Generic[X]):
    """
    Something which can sort a list.

    The Java implements AutoCloseable, so it is used with try-with-resources.
    Here it is a context manager, used with ``with``: closing is what makes a
    sort report its statistics.
    """

    @abstractmethod
    def get_description(self) -> str:
        """
        :return: a description of this Sort, used when reporting results.
        """

    @abstractmethod
    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs in place between from_ (inclusive) and to (exclusive).

        This is the one operation a Sort must provide.

        NOTE named sort_range rather than sort, because Python cannot overload:
        the Java distinguishes sort(X[], int, int) from sort(X[]) by arity.

        :param xs: the list to sort.
        :param from_: the index of the first element to sort.
        :param to: the index one past the last element to sort.
        """

    @abstractmethod
    def init(self, n: int) -> None:
        """
        Prepare to sort n elements.

        :param n: the number of elements about to be sorted.
        """

    @abstractmethod
    def close(self) -> None:
        """
        Finish with this Sort, reporting statistics if there are any.
        """

    def sort(self, xs: list[X], make_copy: bool = True) -> list[X]:
        """
        Sort a list, by default leaving the original alone.

        :param xs: the list to sort.
        :param make_copy: if true (the default) sort a copy and return it,
                          leaving xs as it was; if false, sort xs itself.
        :return: the sorted list, which is xs itself when make_copy is false.
        """
        self.init(len(xs))
        result = list(xs) if make_copy else xs
        self.sort_range(result, 0, len(result))
        return result

    def mutating_sort(self, xs: list[X]) -> None:
        """
        Sort a list in place.

        :param xs: the list to sort.
        """
        self.sort(xs, make_copy=False)

    def sort_collection(self, xs: Iterable[X]) -> Sequence[X]:
        """
        Sort any iterable, returning a new sorted list.

        :param xs: the elements to sort.
        :return: the sorted elements.
        """
        result = list(xs)
        if not result:
            return result
        self.mutating_sort(result)
        return result

    def __enter__(self) -> Sort[X]:
        return self

    def __exit__(self, exc_type: object, exc_value: object, traceback: object) -> None:
        self.close()


class ProcessingSort(Sort[X], ABC):
    """
    A Sort with a pre-processing and a post-processing step.

    Post-processing is where a sort checks its own work and gathers statistics,
    so it is the hook that makes instrumentation possible.
    """

    def pre_process(self, xs: list[X]) -> list[X]:
        """
        Prepare to sort xs.

        :param xs: the list about to be sorted.
        :return: the list to sort, which by default is xs itself.
        """
        self.init(len(xs))
        return xs

    @abstractmethod
    def post_process(self, xs: list[X]) -> None:
        """
        Called once xs has been sorted.

        :param xs: the sorted list.
        """
