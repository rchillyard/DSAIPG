"""
Building the right Helper, ported from sort/helper/HelperFactory.java.

The choice between counting and not counting is made once, here, from the
configuration. Nothing else in the tree has to know which kind it has -- that is
what makes it possible to write each sort once.
"""

from __future__ import annotations

from collections.abc import Callable
from random import Random
from typing import TypeVar

from src.sort.helper.helper import Helper
from src.sort.helper.instrumented_helper import InstrumentedHelper
from src.sort.helper.non_instrumenting_helper import NonInstrumentingHelper
from src.util.config.config import Config
from src.util.config.config_benchmark import get_seed, is_instrumented

X = TypeVar("X")


def create(description: str, n_elements: int, config: Config,
           comparator: Callable[[X, X], int] | None = None,
           instrumented: bool | None = None,
           seed: int | None = None,
           n_runs: int | None = None) -> Helper[X]:
    """
    Build a Helper.

    The Java has five overloads of create plus createGeneric; they differ only
    in which arguments are supplied, so here they are optional parameters.

    :param description: a description, used when reporting results.
    :param n_elements: the number of elements to be sorted.
    :param config: the configuration.
    :param comparator: the comparison function; None means use the natural
                       ordering of the elements.
    :param instrumented: whether to count; None means ask the configuration.
    :param seed: the seed for random elements; None means ask the configuration.
    :param n_runs: the number of runs; None means ask the configuration.
    :return: an InstrumentedHelper or a NonInstrumentingHelper.
    """
    if instrumented is None:
        instrumented = is_instrumented(config)
    random = Random(get_seed(config) if seed is None else seed)
    if instrumented:
        return InstrumentedHelper(description, config, comparator, n_elements, random, n_runs)
    return NonInstrumentingHelper(description, config, comparator, n_elements, random)
