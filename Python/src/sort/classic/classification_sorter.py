"""
A Sort which orders by classifying rather than comparing, ported from
sort/classic/ClassificationSorter.java.

A classifier maps an element to an int, and the sort places elements by that int
alone. That is what lets bucket and radix sorts beat the n log n bound: they
never compare two elements at all.
"""

from __future__ import annotations

from abc import ABC
from collections.abc import Callable
from typing import TypeVar

from src.sort.generic.classifier import Classifier
from src.sort.generic.sort_exception import SortException
from src.sort.generic.sort_with_helper import SortWithHelper

X = TypeVar("X")
Y = TypeVar("Y")


class ClassificationSorter(SortWithHelper[X], Classifier[X, Y], ABC):
    """
    A Sort which places elements according to a classifier.
    """

    def __init__(self, helper, classifier: Callable[[X, Y], int] | None = None) -> None:
        """
        :param helper: the Helper to sort through.
        :param classifier: maps an element and some additional information to a
                           class. May be None if it is set later.
        """
        super().__init__(helper)
        self.classifier = classifier

    def classify(self, x: X, y: Y) -> int:
        """
        Classify a value.

        :param x: the value.
        :param y: the additional information, for example which character of a
                  string is being looked at.
        :return: the class.
        :raises SortException: if no classifier has been set.
        """
        self.get_helper().increment_lookups(1)
        if self.classifier is None:
            raise SortException("Classifier is not set")
        return self.classifier(x, y)

    def classify_at(self, xs: list[X], i: int, y: Y) -> int:
        """
        Classify the element at index i, counting the access.

        :param xs: the list.
        :param i: the index.
        :param y: the additional information.
        :return: the class.
        """
        return self.classify(self.get_helper().get(xs, i), y)

    def get_classifier(self) -> Callable[[X, Y], int] | None:
        """:return: the classifier, which may be None."""
        return self.classifier

    def set_classifier(self, classifier: Callable[[X, Y], int]) -> None:
        """:param classifier: the classifier to use."""
        self.classifier = classifier


def ignoring_second(f: Callable[[X], int]) -> Callable[[X, Y], int]:
    """
    Adapt a classifier that needs no additional information.

    The Java calls this convertToBiFunction; it is the same idea.

    :param f: a classifier taking only the element.
    :return: a classifier taking the element and an ignored second argument.
    """
    if f is None:
        return None
    return lambda x, _: f(x)
