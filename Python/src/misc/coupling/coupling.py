"""
Ported from misc/coupling/CouplingNone.java and CouplingTight.java.

Two ways of writing the same pair of classes, side by side so the difference is
visible. In the loose version A and B each own their own field and know nothing of
each other. In the tight version they are inner classes writing to a field of the
enclosing object, so neither can be understood, tested or moved on its own, and
changing one can break the other.

NOTE Java draws the distinction with `static class` against inner `class`, which
Python has no equivalent of -- a nested class in Python is never implicitly bound
to an instance of its enclosing one. The tight version therefore has to hold the
enclosing object explicitly, which is in fact what Java's inner class does behind
the scenes, and arguably makes the coupling easier to see rather than harder.
"""

from __future__ import annotations


class CouplingNone:
    """
    Two classes with no coupling: each holds its own state.
    """

    class A:
        """
        Holds an int, and nothing else knows about it.
        """

        def __init__(self, a: int) -> None:
            self._a = a

        def get_a(self) -> int:
            """
            :return: the value this A was given.
            """
            return self._a

    class B:
        """
        Holds an int, and nothing else knows about it.
        """

        def __init__(self, b: int) -> None:
            self._b = b

        def get_b(self) -> int:
            """
            :return: the value this B was given.
            """
            return self._b


class CouplingTight:
    """
    The same two classes, coupled: both write to fields of the enclosing object, so
    each can change what the other sees.
    """

    def __init__(self) -> None:
        self.a = 0
        self.b = 0

    class A:
        """
        Writes its value into the enclosing object.
        """

        def __init__(self, outer: CouplingTight, a: int) -> None:
            self.outer = outer
            outer.a = a

        def get_a(self) -> int:
            """
            :return: whatever is in the enclosing object now -- which is not
                     necessarily what this A was given.
            """
            return self.outer.a

    class B:
        """
        Writes its value into the enclosing object.
        """

        def __init__(self, outer: CouplingTight, b: int) -> None:
            self.outer = outer
            outer.b = b

        def get_b(self) -> int:
            """
            :return: whatever is in the enclosing object now.
            """
            return self.outer.b
