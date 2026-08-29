"""
A Sort which does its work through a Helper, ported from
sort/generic/SortWithHelper.java and SortWithComparableHelper.java.

Almost every sort in the tree extends this. It supplies the lifecycle -- init,
pre-process, post-process, close -- so that each sort only has to write the one
method that actually sorts.

The Java has SortWithComparableHelper as well, differing only in which
HelperFactory method it calls. Here the comparator is optional, so one class
does both.
"""

from __future__ import annotations

from abc import ABC
from typing import TypeVar

from src.sort.generic.sort import ProcessingSort
from src.sort.helper.helper import Helper
from src.sort.helper.helper_exception import HelperException
from src.sort.helper.helper_factory import create
from src.util.config.config import Config
from src.util.logging.lazy_logger import LazyLogger

X = TypeVar("X")

logger = LazyLogger(__name__)


class SortWithHelper(ProcessingSort[X], ABC):
    """
    A Sort which touches its list only through a Helper.
    """

    def __init__(self, helper: Helper[X]) -> None:
        """
        :param helper: the Helper to sort through. Use ``from_config`` to build
                       one from configuration instead.
        """
        self.helper = helper
        self.close_helper = False
        self._open = True

    @classmethod
    def from_config(cls, description: str, config: Config, n: int = 0,
                    comparator=None, n_runs: int | None = None, **kwargs) -> SortWithHelper[X]:
        """
        Build a Sort whose Helper comes from configuration.

        :param description: a description, used when reporting results.
        :param config: the configuration.
        :param n: the number of elements to be sorted.
        :param comparator: the comparison function; None means natural ordering.
        :param n_runs: the number of runs; None means ask the configuration.
        :param kwargs: passed to the constructor of the concrete sort.
        :return: the Sort, which will close its own Helper.
        """
        helper = create(description, n, config, comparator, n_runs=n_runs)
        result = cls(helper, **kwargs)
        result.close_helper = True
        return result

    def get_helper(self) -> Helper[X]:
        """
        :return: the Helper this Sort works through.
        """
        return self.helper

    def get_description(self) -> str:
        return self.helper.get_description()

    def init(self, n: int) -> None:
        self.helper.init(n)

    def pre_process(self, xs: list[X]) -> list[X]:
        return self.helper.pre_process(xs)

    def post_process(self, xs: list[X]) -> None:
        """
        Hand the sorted list to the Helper, which checks it and gathers the
        statistics.

        A HelperException means the Helper found the result unacceptable -- in
        practice, that the list is not in order -- and is re-raised. A sort which
        did not sort must not be able to look like a success.

        Anything else came from the post-processing itself rather than from the
        sort, so it is logged and swallowed.

        NOTE a HelperException is re-raised rather than logged, so that a sort
        which did not sort cannot pass quietly.

        :param xs: the sorted list.
        :raises HelperException: if the Helper found the result unacceptable.
        """
        try:
            self.helper.post_process(xs)
        except HelperException:
            raise
        except Exception as e:
            # NOTE the message is built here, not inside the lambda: Python
            # unbinds the exception variable when the except block ends, so a
            # lazily-built message would raise NameError instead of logging.
            message = f"{self.get_description()}: post_process: exception: {e}"
            logger.info(lambda: message)

    def is_sorted(self, xs: list[X]) -> bool:
        """
        :param xs: the list.
        :return: true if it is sorted.
        """
        return self.helper.is_sorted(xs)

    def close(self) -> None:
        """
        Close this Sort, and its Helper if this Sort created it.

        Closing twice is harmless, which matters because a sort may be closed
        explicitly and again by leaving a ``with`` block.
        """
        if not self._open:
            return
        self._open = False
        if self.close_helper:
            self.helper.close()

    def __str__(self) -> str:
        return str(self.helper)
