"""
The HasAdditionalMemory abstraction, ported from
sort/generic/HasAdditionalMemory.java.

Some sorts need memory beyond the list they are sorting -- merge sort needs an
auxiliary list as large as the input, while insertion sort needs none. This
records how much, so that the space cost can be reported alongside the time.
"""

from __future__ import annotations

from abc import ABC, abstractmethod


class HasAdditionalMemory(ABC):
    """
    Something which uses memory in addition to the list being sorted.
    """

    @abstractmethod
    def set_array_memory(self, n: int) -> None:
        """
        Record the size of the list being sorted, against which the additional
        memory is measured.

        :param n: the number of elements in the list.
        """

    @abstractmethod
    def additional_memory(self, n: int) -> None:
        """
        Record that n additional elements of memory have been used.

        :param n: the number of additional elements.
        """

    @abstractmethod
    def get_memory_factor(self) -> float | None:
        """
        :return: the additional memory used as a multiple of the size of the
                 list, or None if the size was never recorded.
        """
