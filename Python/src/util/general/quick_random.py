"""
A fast, poor random number generator, ported from
util/general/QuickRandom.java.

This is an xorshift generator. It is nothing like as good as a proper one, but
it needs no state beyond a single integer and no division until the very end,
which makes it cheap enough to call inside a sorting loop without the generator
dominating what is being measured.
"""

from __future__ import annotations

import time

#: Mask keeping the state to 32 bits, since Python integers do not overflow the
#: way Java's int does. Without this the state would grow without limit and the
#: sequence would not match.
_MASK32 = 0xFFFFFFFF


class QuickRandom:
    """
    An xorshift generator producing values in a fixed range.
    """

    def __init__(self, n: int = 2 ** 31 - 1, seed: int | None = None) -> None:
        """
        :param n: one more than the largest value to be produced.
        :param seed: the seed; None means seed from the clock.
        :raises ValueError: if n is not positive.
        """
        if n <= 0:
            raise ValueError("N must be positive")
        self.n = n
        if seed is None:
            seed = int(time.time() * 1000)
        # NOTE a seed of 0 gives a degenerate sequence, so it is mixed first.
        self.r = _to_signed32((seed ^ 0xAAAAAAAA) & _MASK32)

    def get(self, m: int = 0) -> int:
        """
        :param m: the smallest value to produce.
        :return: a value between m and n - 1.
        :raises ValueError: if m is negative.
        """
        if m < 0:
            raise ValueError("m must be non-negative")
        r = self.r
        r ^= (r << 13) & _MASK32
        r = _to_signed32(r & _MASK32)
        # NOTE an arithmetic shift, as in Java's >>, which keeps the sign.
        r ^= r >> 17
        r ^= (r << 5) & _MASK32
        r = _to_signed32(r & _MASK32)
        r &= 0x7FFFFFFF
        self.r = r
        return r % (self.n - m) + m


def _to_signed32(x: int) -> int:
    """
    :param x: a 32-bit value.
    :return: the same bits read as a signed Java int, so that the shifts below
             behave as they do in Java.
    """
    return x - 0x100000000 if x & 0x80000000 else x
