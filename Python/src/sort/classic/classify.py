"""
The Classify abstraction, ported from sort/classic/Classify.java.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Generic, TypeVar

X = TypeVar("X")


class Classify(ABC, Generic[X]):
    """
    Something which can say which class it belongs to.
    """

    @abstractmethod
    def classify(self) -> int:
        """
        Classify this element.

        The value returned does two things: it says which class the element
        belongs to, and it says where that class comes in the order. Classes are
        visited in ASCENDING order of this value, so a classification sort leaves
        the elements ordered by class -- which is what allows a following pass,
        typically an insertion sort, to have very little left to do.

        Any int is permitted; the values need not be dense, nor start at zero, nor
        be positive. This must be a pure function: two calls on an unchanged
        element must give the same answer, or the classes will not group.

        NOTE returning ``hash(self)`` satisfies the letter of this contract and is
        never what is wanted. A hash says nothing about order, so the classes
        would be visited in an arbitrary sequence and the following pass would
        have as much work as if nothing had been classified at all.

        :return: an integer giving both the class of this element and its position
                 in the order in which classes are visited.
        """
