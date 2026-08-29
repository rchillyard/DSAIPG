"""
A point in two dimensions, ported from graphs/undirected/Position.java.
"""

from __future__ import annotations

from dataclasses import dataclass
from typing import Protocol, runtime_checkable


@runtime_checkable
class Position(Protocol):
    """
    Anything with an x and a y.

    NOTE the Java's Position is an *interface*, and its implementers -- Building
    in graphs/tunnels, Position_Spherical in graphs/gis -- are otherwise unrelated
    classes which happen to have coordinates. So this must be something they can
    satisfy without inheriting from it, not a value class of its own.

    A Protocol rather than an ABC, for two reasons. It matches how the interface
    is actually used: implementers only need to *have* coordinates, not to
    inherit from anything. And an ABC would be a trap here -- a frozen dataclass
    inheriting an ABC with an abstract ``x`` property does not override it, since
    a field without a default creates no class attribute, so the dataclass could
    not be instantiated at all.
    """

    @property
    def x(self) -> float:
        """
        :return: the x-coordinate.
        """
        ...

    @property
    def y(self) -> float:
        """
        :return: the y-coordinate.
        """
        ...


@dataclass(frozen=True)
class PositionXY:
    """
    The obvious implementation of Position: a pair of coordinates and nothing else.

    Kept because it is genuinely useful for tests and simple cases. It satisfies
    Position structurally, without inheriting from it -- which is the point of a
    Protocol.
    """

    x: float
    y: float
