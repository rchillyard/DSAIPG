"""
The Helper which does not count, ported from
sort/helper/NonInstrumentingComparatorHelper.java and
NonInstrumentingComparableHelper.java.

It inherits every method from Helper unchanged, so a sort using it runs at full
speed. The only thing it adds is an optional check, after the sort, that the
list really did come out sorted.
"""

from __future__ import annotations

from collections.abc import Callable
from random import Random
from typing import TypeVar

from src.sort.helper.base_helper import BaseHelper
from src.sort.helper.helper import Helper
from src.sort.helper.helper_exception import HelperException
from src.sort.helper.instrumenter_dummy import InstrumenterDummy
from src.util.config.config import Config

X = TypeVar("X")

#: The [helper] option which turns on the after-the-fact check. It only applies
#: when instrument is false; an instrumented Helper always checks.


class NonInstrumentingHelper(BaseHelper[X]):
    """
    A Helper which counts nothing.
    """

    def __init__(self, description: str, config: Config,
                 comparator: Callable[[X, X], int] | None = None,
                 n: int = 0, random: Random | None = None) -> None:
        """
        :param description: a description, used when reporting results.
        :param config: the configuration.
        :param comparator: the comparison function; None means natural ordering.
        :param n: the number of elements to be managed, if known yet.
        :param random: the source of random elements.
        """
        super().__init__(description, config, comparator, n, random, InstrumenterDummy())

    def instrumented(self) -> bool:
        return False

    def post_process(self, xs: list[X]) -> None:
        """
        Check that the list is sorted, if the configuration asks for it.

        :param xs: the sorted list.
        :raises HelperException: if the list is not sorted.
        """
        super().post_process(xs)
        if self.check_sorted and not self.is_sorted(xs):
            raise HelperException("NonInstrumentingHelper.post_process: array is not sorted")

    def clone(self, description: str, n: int | None = None,
              comparator: Callable[[X, X], int] | None = None,
              share_instrumenter: bool = False) -> Helper[X]:
        """
        Make a Helper like this one.

        :param description: the description for the new Helper.
        :param n: the number of elements, defaulting to this Helper's.
        :param comparator: the comparison function, defaulting to this one's.
        :param share_instrumenter: ignored, because nothing is counted.
        :return: the new Helper.
        """
        return NonInstrumentingHelper(
            description, self.config,
            comparator if comparator is not None else self.comparator,
            self.n if n is None else n,
            self.random_source,
        )

    def __str__(self) -> str:
        return f"Helper for {self.description} with {self.n} elements"
