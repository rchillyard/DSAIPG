"""
The Classifier abstraction, ported from sort/generic/Classifier.java.

A Classifier places a value into one of a number of buckets. It is what makes a
counting or radix sort possible: instead of comparing two elements, you ask
which bucket each belongs in.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Generic, TypeVar

X = TypeVar("X")
Y = TypeVar("Y")


class Classifier(ABC, Generic[X, Y]):
    """
    Something which can classify a value, given some additional information.
    """

    @abstractmethod
    def classify(self, x: X, y: Y) -> int:
        """
        Classify x.

        :param x: the value to classify.
        :param y: the additional information, for example which character of a
                  string is being looked at.
        :return: the bucket that x belongs in.
        """

    @abstractmethod
    def classify_at(self, xs: list[X], i: int, y: Y) -> int:
        """
        Classify the element at index i of xs.

        NOTE named classify_at rather than classify, because Python cannot
        overload. This form exists so that the array access can be counted.

        :param xs: the list.
        :param i: the index of the element to classify.
        :param y: the additional information.
        :return: the bucket that the element belongs in.
        """
