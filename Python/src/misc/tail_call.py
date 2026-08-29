"""
Ported from misc/TailCall.java.
"""

from __future__ import annotations

from collections.abc import Callable
from typing import Generic, TypeVar

T = TypeVar("T")


class TailCall(Generic[T]):
    """
    A tail call not yet made: either the next step, or the answer.

    Neither Java nor Python eliminates tail calls, so a recursion deep enough will
    exhaust the stack in either. Trampolining is the way round it: instead of
    calling itself, a step RETURNS the next step, and ``invoke`` runs them in a
    loop -- so the recursion is as deep as you like and the stack never grows.
    """

    def __init__(self, next_call: Callable[[], TailCall[T]] | None, value: T | None = None,
                 complete: bool = False) -> None:
        """
        :param next_call: what to do next, if this is not the last step.
        :param value: the answer, if it is.
        :param complete: whether this is the last step.
        """
        self._next = next_call
        self._value = value
        self._complete = complete

    def is_complete(self) -> bool:
        """
        :return: whether this holds the answer rather than another step.
        """
        return self._complete

    def result(self) -> T:
        """
        :return: the answer.
        :raises RuntimeError: if this is not the last step.
        """
        if not self._complete:
            raise RuntimeError("not implemented")
        return self._value  # type: ignore[return-value]

    def get(self) -> TailCall[T]:
        """
        :return: the next step.
        :raises RuntimeError: if this is the last step.
        """
        if self._complete:
            raise RuntimeError("never called")
        return self._next()  # type: ignore[misc]

    def invoke(self) -> T:
        """
        Run the steps until one of them holds the answer.

        NOTE a loop, where the Java writes Stream.iterate(...).filter(...).findFirst().
        Same thing, and the loop is what makes it plain that the stack does not grow.

        :return: the answer.
        """
        current = self
        while not current.is_complete():
            current = current.get()
        return current.result()


def call(next_call: Callable[[], TailCall[T]]) -> TailCall[T]:
    """
    :param next_call: what to do next.
    :return: a TailCall which will do it.
    """
    return TailCall(next_call)


def done(value: T) -> TailCall[T]:
    """
    :param value: the answer.
    :return: a TailCall holding it.
    """
    return TailCall(None, value, complete=True)
