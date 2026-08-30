"""
Ported from graphs/gis/Sequenced.java.
"""

from __future__ import annotations

from typing import Protocol, runtime_checkable


@runtime_checkable
class Sequenced(Protocol):
    """
    Something that can be told where it comes in an order.

    An MST algorithm numbers each edge attribute as it chooses that edge, so that
    a drawing of the tree can be made in the order it was built -- which is what
    ``Kml`` does with it.

    NOTE a Protocol rather than an ABC, for the same reason as ``Position``: an
    implementer only needs to *have* these methods, not to inherit from anything.
    """

    def get_sequence(self) -> int:
        """
        :return: where this comes in the order.
        """
        ...

    def set_sequence(self, sequence: int) -> None:
        """
        :param sequence: where this comes in the order.
        """
        ...
