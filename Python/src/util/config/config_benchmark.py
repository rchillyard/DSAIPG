"""
Configuration helpers for benchmarking, ported from
util/config/Config_Benchmark.java.

Mostly this is the names of the [helper] options, plus a few functions that
build a Config from scratch. The Java tests lean on those heavily, because they
need a configuration that differs from the shipped config.ini in one particular.
"""

from __future__ import annotations

import time

from src.sort.helper.instrument import (
    COMPARES,
    COPIES,
    FIXES,
    HITS,
    INSTRUMENTING,
    INVERSIONS,
    LOOKUPS,
    SWAPS,
)
from src.util.config.config import Config

#: The section holding the options that apply to every Helper.
HELPER = "helper"

#: Whether to instrument at all. Nothing in [instrumenting] means anything
#: unless this is true.
INSTRUMENT = "instrument"

SEED = "seed"
CUTOFF = "cutoff"
MSDCUTOFF = "msdcutoff"
#: Whether a Helper's post_process should verify that the list really is
#: sorted. Left off in config.ini so benchmarks do not measure the check;
#: setup_config turns it on, so every test gets it.
CHECKSORTED = "checksorted"

#: The cutoff below which a linearithmic sort hands over to insertion sort.
#: 20 is the value benchmarking settled on for merge sort and dual-pivot
#: quicksort.
CUTOFF_DEFAULT = 20

#: The [mergesort] section and its two options. The Java declares these on
#: MergeSort itself; they live here so that this module does not have to import
#: a sort in order to name a configuration key.
MERGESORT = "mergesort"
INSURANCE = "insurance"
NOCOPY = "nocopy"


def get_seed(config: Config) -> int:
    """
    The seed for the random numbers to be sorted.

    :param config: the configuration.
    :return: the configured seed, or the current time in milliseconds if there
             is none -- so that runs differ unless a seed is set deliberately.
    """
    return config.get_long(HELPER, SEED, int(time.time() * 1000))


def is_instrumented(config: Config) -> bool:
    """
    :param config: the configuration.
    :return: true if sorts should count what they do.
    """
    return config.get_boolean(HELPER, INSTRUMENT)


def setup_config(instrumenting: str, fixes: str, seed: str, inversions: str,
                 cutoff: str, interim_inversions: str) -> Config:
    """
    Build a Config for a test, with everything in [instrumenting] following the
    instrumenting argument except fixes, which is set separately because
    counting fixes is expensive.

    :param instrumenting: "true" to instrument.
    :param fixes: "true" to count inversions fixed.
    :param seed: the seed for random numbers.
    :param inversions: the number of inversions to count.
    :param cutoff: the cutoff for hybrid sorts.
    :param interim_inversions: whether to count interim inversions.
    :return: the Config.
    """
    return Config.from_text(_ini_text({
        HELPER: {INSTRUMENT: instrumenting, SEED: seed, CUTOFF: cutoff,
                 CHECKSORTED: "true"},
        INSTRUMENTING: {
            INVERSIONS: inversions,
            SWAPS: instrumenting,
            COMPARES: instrumenting,
            COPIES: instrumenting,
            FIXES: fixes,
            HITS: instrumenting,
            LOOKUPS: instrumenting,
        },
        "huskyhelper": {"countinteriminversions": interim_inversions},
    }))


def setup_config2(instrumenting: str, seed: str, inversions: str, cutoff: str,
                  interim_inversions: str, insurance: str, no_copy: str) -> Config:
    """
    Build a Config for a merge sort test, adding the [mergesort] insurance and
    nocopy options.

    NOTE insurance and nocopy go in [mergesort], which is where MergeSort reads
    them. The Java put them in [helper], so both always came back false and the
    four combinations these arguments describe were all the same run; fixed in
    both trees.

    :param instrumenting: "true" to instrument.
    :param seed: the seed for random numbers.
    :param inversions: the number of inversions to count.
    :param cutoff: the cutoff for hybrid sorts.
    :param interim_inversions: whether to count interim inversions.
    :param insurance: whether to check for a partially ordered input.
    :param no_copy: whether to use the no-copy merge.
    :return: the Config.
    """
    return Config.from_text(_ini_text({
        HELPER: {INSTRUMENT: instrumenting, SEED: seed, CUTOFF: cutoff,
                 CHECKSORTED: "true"},
        MERGESORT: {INSURANCE: insurance, NOCOPY: no_copy},
        INSTRUMENTING: {
            INVERSIONS: inversions,
            SWAPS: instrumenting,
            COMPARES: instrumenting,
            COPIES: instrumenting,
            FIXES: instrumenting,
        },
        "huskyhelper": {"countinteriminversions": interim_inversions},
    }))


def setup_config_fixes() -> Config:
    """
    Build a Config which instruments and counts fixes, with the default cutoff.

    :return: the Config.
    """
    return Config.from_text(_ini_text({
        HELPER: {INSTRUMENT: "true", CUTOFF: str(CUTOFF_DEFAULT)},
        INSTRUMENTING: {FIXES: "true"},
    }))


def _ini_text(sections: dict[str, dict[str, str]]) -> str:
    """
    Render sections as the text of an ini file.

    The Java builds an Ini object directly; configparser has no equally direct
    way in, and going through the text keeps the parsing rules identical to
    those used for config.ini itself.

    :param sections: section name to option name to value.
    :return: the ini text.
    """
    lines = []
    for name, options in sections.items():
        lines.append(f"[{name}]")
        lines.extend(f"{key} = {value}" for key, value in options.items())
        lines.append("")
    return "\n".join(lines)
