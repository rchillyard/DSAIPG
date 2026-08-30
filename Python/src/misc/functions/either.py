"""
Ported from misc/functions/Either.java.
"""

from __future__ import annotations

from collections.abc import Callable
from typing import Generic, TypeVar

L = TypeVar("L")
R = TypeVar("R")
T = TypeVar("T")

#: Distinguishes "no value on this side" from a value which happens to be None.
_ABSENT = object()


class Either(Generic[L, R]):
    """
    One of two things: a left or a right.

    Conventionally the right is the answer and the left is why there isn't one --
    which is how ``Newton`` uses it, returning the root it found or the reason it
    could not find one. A caller has to look at which it got, so a failure cannot
    be read as a result by accident.
    """

    def __init__(self, left: object = _ABSENT, right: object = _ABSENT) -> None:
        """
        :param left: the left value, if there is one.
        :param right: the right value, if there is one.
        """
        self._left = left
        self._right = right

    @staticmethod
    def left(value: L) -> Either[L, R]:
        """
        :param value: the value to hold on the left.
        :return: an Either holding it.
        """
        return Either(left=value)

    @staticmethod
    def right(value: R) -> Either[L, R]:
        """
        :param value: the value to hold on the right.
        :return: an Either holding it.
        """
        return Either(right=value)

    def is_right(self) -> bool:
        """
        :return: whether this holds a right and not a left.
        """
        return self._right is not _ABSENT and self._left is _ABSENT

    def is_left(self) -> bool:
        """
        NOTE not in the Java, which offers only isRight. Asking the question the
        other way round reads better at a call site that cares about the failure.

        :return: whether this holds a left and not a right.
        """
        return self._left is not _ABSENT and self._right is _ABSENT

    def get_left(self) -> L | None:
        """
        :return: the left value, or None if there is not one.
        """
        return None if self._left is _ABSENT else self._left  # type: ignore[return-value]

    def get_right(self) -> R | None:
        """
        :return: the right value, or None if there is not one.
        """
        return None if self._right is _ABSENT else self._right  # type: ignore[return-value]

    def map(self, l_func: Callable[[L], T], r_func: Callable[[R], T]) -> T | None:
        """
        Collapse to a single value, by whichever function fits the side in hand.

        :param l_func: what to do with a left.
        :param r_func: what to do with a right.
        :return: the result, or None if this holds neither.
        """
        if self._left is not _ABSENT:
            return l_func(self._left)  # type: ignore[arg-type]
        if self._right is not _ABSENT:
            return r_func(self._right)  # type: ignore[arg-type]
        return None

    def map_left(self, l_func: Callable[[L], T]) -> Either[T, R]:
        """
        :param l_func: what to do with a left, if there is one.
        :return: an Either with the left replaced and the right untouched.
        """
        return Either(
            left=_ABSENT if self._left is _ABSENT else l_func(self._left),  # type: ignore[arg-type]
            right=self._right,
        )

    def map_right(self, r_func: Callable[[R], T]) -> Either[L, T]:
        """
        :param r_func: what to do with a right, if there is one.
        :return: an Either with the right replaced and the left untouched.
        """
        return Either(
            left=self._left,
            right=_ABSENT if self._right is _ABSENT else r_func(self._right),  # type: ignore[arg-type]
        )

    def apply(self, l_func: Callable[[L], None], r_func: Callable[[R], None]) -> None:
        """
        Do something with whichever side is present, for its effect.

        :param l_func: what to do with a left.
        :param r_func: what to do with a right.
        """
        if self._left is not _ABSENT:
            l_func(self._left)  # type: ignore[arg-type]
        if self._right is not _ABSENT:
            r_func(self._right)  # type: ignore[arg-type]

    def __str__(self) -> str:
        if self.is_left():
            return f"Left({self._left})"
        if self.is_right():
            return f"Right({self._right})"
        return "Either(neither)"
