"""
Ported from misc/Complex.java.

NOTE Python has a built-in ``complex``, which does everything this does and more.
This exists to mirror the Java, where there is no such type.
"""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class Complex:
    """
    A complex number: a real part and an imaginary one.
    """

    real: float
    imag: float = 0.0
