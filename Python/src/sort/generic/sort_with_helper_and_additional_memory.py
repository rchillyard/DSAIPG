"""
A classifying Sort that keeps track of the memory it uses, ported from
sort/generic/SortWithHelperAndAdditionalMemory.java.

Radix sorts buy their speed with space: an auxiliary list as large as the input,
plus a counting list as large as the alphabet. Recording that makes the trade
visible, so a sort can be judged on both axes rather than on time alone.
"""

from __future__ import annotations

from abc import ABC
from collections.abc import Callable
from typing import TypeVar

from src.sort.classic.classification_sorter import ClassificationSorter
from src.sort.generic.has_additional_memory import HasAdditionalMemory
from src.sort.generic.sort_exception import SortException
from src.util.logging.lazy_logger import LazyLogger

X = TypeVar("X")

logger = LazyLogger(__name__)


class SortWithHelperAndAdditionalMemory(ClassificationSorter[X, int], HasAdditionalMemory, ABC):
    """
    A classifying Sort which records how much extra memory it uses.
    """

    def __init__(self, helper, classifier: Callable[[X, int], int] | None = None) -> None:
        """
        :param helper: the Helper to sort through.
        :param classifier: maps an element and a depth to a class.
        """
        super().__init__(helper, classifier)
        self.array_memory = -1
        self.additional = 0
        self.max_memory = 0

    def set_array_memory(self, n: int) -> None:
        """
        Record the size of the list being sorted, once.

        :param n: the number of elements.
        """
        if self.array_memory == -1:
            self.array_memory = n
            self.additional_memory(n)

    def additional_memory(self, n: int) -> None:
        """
        Record a change in the extra memory in use, remembering the peak.

        :param n: the change, negative when memory is given back.
        """
        self.additional += n
        if self.max_memory < self.additional:
            self.max_memory = self.additional

    def get_memory_factor(self) -> float:
        """
        :return: the peak memory as a multiple of the size of the list.
        :raises SortException: if the size was never recorded.
        """
        if self.array_memory == -1:
            raise SortException("Array memory has not been set")
        return 1.0 * self.max_memory / self.array_memory

    def init(self, n: int) -> None:
        """
        :param n: the number of elements to be sorted.
        """
        self.set_array_memory(n)
        super().init(n)

    def close(self) -> None:
        """Finish, reporting how much extra memory was used."""
        super().close()
        factor = self.get_memory_factor()
        logger.info(lambda: f"{self}: memory factor: {factor}")
