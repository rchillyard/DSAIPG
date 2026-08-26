"""
Bucket sort, ported from sort/classic/BucketSort.java.

Elements are distributed into buckets by a classifier, the buckets are emptied
back into the list in order, and a single insertion sort finishes the job. That
last pass is cheap because the buckets are already in order, so no element has
far to travel -- which is the whole bargain: classify in linear time, and leave
only local work behind.
"""

from __future__ import annotations

import bisect
from collections.abc import Callable
from typing import TypeVar

from src.sort.classic.classification_sorter import ClassificationSorter, ignoring_second
from src.sort.elementary.insertion_sort import InsertionSort
from src.sort.generic.sort_exception import SortException
from src.util.logging.lazy_logger import LazyLogger

X = TypeVar("X")

DESCRIPTION = "Bucket sort"

#: A space, then the lower-case letters, so that a space classifies as 0.
ALPHABET = " abcdefghijklmnopqrstuvwxyz"
ALPHABET_SIZE = len(ALPHABET)
DIGRAPHS_SIZE = ALPHABET_SIZE * ALPHABET_SIZE

logger = LazyLogger(__name__)

#: Built on first use, as in the Java.
_DIGRAPHS: list[str] | None = None


def classify_string_initial(s: str) -> int:
    """
    :param s: the string.
    :return: the position of its first character in ALPHABET, or -1 if it is not
             a letter or a space.
    """
    return ALPHABET.find(s.lower()[0])


def classify_string_digraph(s: str) -> int:
    """
    Classify a string by its first two characters together, which spreads strings
    over 27 x 27 buckets rather than 27.

    :param s: the string.
    :return: the index of its first two characters among all digraphs, or a
             negative number if the pair is not one of them.
    """
    global _DIGRAPHS
    if _DIGRAPHS is None:
        _DIGRAPHS = [c1 + c2 for c1 in ALPHABET for c2 in ALPHABET]
    digraph = (s.lower() + " ")[:2]
    i = bisect.bisect_left(_DIGRAPHS, digraph)
    if i < len(_DIGRAPHS) and _DIGRAPHS[i] == digraph:
        return i
    return -(i + 1)


def number_classifier(minimum: float, gap: float, n_buckets: int) -> Callable[[float], int]:
    """
    :param minimum: the smallest value expected.
    :param gap: the width of one bucket. Zero means every value is the same, so
                everything belongs in the first bucket.
    :param n_buckets: the number of buckets.
    :return: a classifier placing a number in a bucket by its distance above the
             minimum, clamped to the range.
    """
    def classify(x: float) -> int:
        # NOTE the zero-gap case is handled here rather than left to the
        # arithmetic. The Java has no such test and survives by accident: with a
        # gap of zero it computes 0.0/0.0, and since Math.floor(NaN) is NaN and
        # (int) NaN is 0, every value lands in bucket 0 -- which is right, but
        # only because casting NaN to int gives zero. Python raises instead.
        if gap == 0:
            return 0
        index = int((x - minimum) // gap)
        return max(0, min(index, n_buckets - 1))

    return classify


def get_number_classifier(xs: list[float], from_: int, to: int,
                          classes: int) -> Callable[[float], int]:
    """
    Build a classifier that spreads the values evenly over the buckets.

    :param xs: the values.
    :param from_: the index of the first to consider.
    :param to: one past the index of the last.
    :param classes: the number of buckets.
    :return: the classifier.
    """
    minimum = min(xs[from_:to])
    maximum = max(xs[from_:to])
    gap = (maximum - minimum) / classes
    logger.debug(lambda: f"creating numeric classifier with gap size: {gap}")
    return number_classifier(minimum, gap, classes)


class BucketSort(ClassificationSorter[X, None]):
    """
    Bucket sort, finishing with a single insertion sort.
    """

    def __init__(self, helper, classifier: Callable[[X], int] | None = None,
                 n_buckets: int = ALPHABET_SIZE) -> None:
        """
        :param helper: the Helper to sort through.
        :param classifier: places an element in a bucket. None means work one out
                           from the values, which requires them to be numbers.
        :param n_buckets: the number of buckets.
        """
        super().__init__(helper, ignoring_second(classifier))
        self.buckets: list[list[X]] = [[] for _ in range(n_buckets)]
        self.insertion_sort = InsertionSort(
            helper.clone("bucket sort: insertion sort", share_instrumenter=True))

    def get_description(self) -> str:
        return DESCRIPTION

    def sort_range(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort xs by distributing into buckets and finishing with an insertion sort.

        :param xs: the list to sort.
        :param from_: the index of the first element.
        :param to: the index one past the last.
        :raises SortException: if no classifier was given and the elements are not
                               numbers.
        """
        if self.classifier is None:
            if all(isinstance(x, (int, float)) for x in xs[from_:to]):
                self.set_classifier(ignoring_second(
                    get_number_classifier(xs, from_, to, len(self.buckets))))
            else:
                raise SortException(
                    "BucketSort: classifier undefined AND the type being sorted is not a Number")
        self._clear_buckets()
        self._assign_to_buckets(xs, from_, to)
        self._check_buckets(to - from_)
        self._unload_buckets(xs, from_, to)
        self.insertion_sort.sort_range(xs, from_, to)

    def _clear_buckets(self) -> None:
        """Empty every bucket, so that the sort may be run more than once."""
        for bucket in self.buckets:
            bucket.clear()

    def _assign_to_buckets(self, xs: list[X], from_: int, to: int) -> None:
        """
        Put each element in the bucket its class names.

        :param xs: the list.
        :param from_: the index of the first element.
        :param to: one past the index of the last.
        """
        helper = self.get_helper()
        # NOTE counted up front: one copy and one hit per element, for putting it
        # in a bucket. The loop below appends to a list rather than writing to the
        # list being sorted, so nothing else would count it.
        helper.increment_copies(to - from_)
        helper.increment_hits(to - from_)
        for i in range(from_, to):
            x = helper.get(xs, i)
            index = self.classify(x, None)
            index = max(0, min(index, len(self.buckets) - 1))
            self.buckets[index].append(x)

    def _check_buckets(self, expected: int) -> None:
        """
        NOTE compared against the size of the range being sorted, not the length
        of the whole list. It used to be the latter, so sorting any sub-range
        raised: sort_range(xs, 1, 4) on five elements gave "incorrect number of
        buckets: 3, 5".

        :param expected: the number of elements that should have been distributed.
        :raises RuntimeError: if the buckets do not hold exactly that many.
        """
        count = sum(len(bucket) for bucket in self.buckets)
        if count != expected:
            raise RuntimeError(f"incorrect number of buckets: {count}, {expected}")

    def _unload_buckets(self, xs: list[X], from_: int, to: int) -> None:
        """
        Empty the buckets back into xs[from_:to], in bucket order.

        NOTE the elements go back into the range, not from index 0. Starting at
        zero was the other half of why a sub-range could not be sorted.

        :param xs: the list to write into.
        :param from_: the index at which to start writing.
        :param to: one past the last index that may be written.
        """
        helper = self.get_helper()
        i = from_
        for bucket in self.buckets:
            helper.increment_copies(len(bucket))
            helper.increment_hits(2 * len(bucket))
            for x in bucket:
                if i >= to:
                    raise RuntimeError(f"unload_buckets: index out of bounds: {to}")
                xs[i] = x
                i += 1
