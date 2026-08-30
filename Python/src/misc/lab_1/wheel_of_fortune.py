"""
Ported from misc/lab_1/WheelOfFortune.java.
"""

from __future__ import annotations

import random as random_module
from dataclasses import dataclass
from typing import Generic, TypeVar

E = TypeVar("E")
T = TypeVar("T")


@dataclass(frozen=True)
class Event(Generic[E]):
    """
    An outcome, and how often it comes up relative to the others.
    """

    event: E
    frequency: int


def value_of(event: E, frequency: int) -> Event[E]:
    """
    :param event: the outcome.
    :param frequency: how often it comes up relative to the others.
    :return: the Event.
    """
    return Event(event, frequency)


class WheelOfFortune(Generic[T]):
    """
    A wheel whose sectors have different widths: an outcome with twice the
    frequency of another comes up twice as often.

    One random number in [0, total) picks a point on the wheel, and the outcomes
    are walked in turn subtracting their widths until the point falls inside one.
    """

    def __init__(self, *events: Event[T], random: random_module.Random | None = None) -> None:
        """
        NOTE Python takes the random as a keyword after the events, where the Java
        has three constructors -- one taking a Random, one a seed, one neither --
        because varargs must come last in Java and cannot in Python.

        :param events: the outcomes and their frequencies.
        :param random: the source of randomness; a seeded one makes the wheel repeatable.
        """
        self.events = events
        self.random = random if random is not None else random_module.Random()
        self.total = self._get_total()

    @staticmethod
    def seeded(seed: int, *events: Event[T]) -> WheelOfFortune[T]:
        """
        :param seed: the seed for the source of randomness.
        :param events: the outcomes and their frequencies.
        :return: a wheel which will always give the same sequence.
        """
        return WheelOfFortune(*events, random=random_module.Random(seed))

    def get(self) -> T:
        """
        Spin the wheel.

        :return: one of the outcomes, chosen with probability proportional to its
                 frequency.
        """
        r = self.random.randrange(self.total)  # noqa: F841  scaffolding for the exercise
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def _get_total(self) -> int:
        """
        :return: the sum of the frequencies, which is the circumference of the wheel.
        """
        return sum(event.frequency for event in self.events)
