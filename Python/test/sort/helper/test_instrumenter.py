import pytest

from src.sort.helper.instrument import COMPARES, SWAPS
from src.sort.helper.instrumenter import Instrumenter
from src.sort.helper.instrumenter_dummy import InstrumenterDummy
from src.util.config.config import Config
from src.util.config.config_benchmark import setup_config

# These mirror InstrumenterTest.java.


def all_on() -> Instrumenter:
    """An Instrumenter that counts everything."""
    return Instrumenter(True, True, True, True, True, True, True)


class TestCounting:
    def test_nothing_counted_to_begin_with(self):
        instrumenter = all_on()
        assert instrumenter.get_compares() == 0
        assert instrumenter.get_swaps() == 0
        assert instrumenter.get_copies() == 0
        assert instrumenter.get_fixes() == 0
        assert instrumenter.get_hits() == 0
        assert instrumenter.get_lookups() == 0

    def test_each_counter(self):
        instrumenter = all_on()
        instrumenter.increment_compares()
        instrumenter.increment_compares()
        instrumenter.increment_swaps(3)
        instrumenter.increment_copies(4)
        instrumenter.increment_fixes(5)
        instrumenter.increment_hits(6)
        instrumenter.increment_lookups(7)
        assert instrumenter.get_compares() == 2
        assert instrumenter.get_swaps() == 3
        assert instrumenter.get_copies() == 4
        assert instrumenter.get_fixes() == 5
        assert instrumenter.get_hits() == 6
        assert instrumenter.get_lookups() == 7

    def test_a_counter_that_is_switched_off_stays_at_zero(self):
        # compares on, everything else off.
        instrumenter = Instrumenter(False, False, True, False, False, False, False)
        instrumenter.increment_compares()
        instrumenter.increment_swaps(3)
        instrumenter.increment_hits(6)
        assert instrumenter.get_compares() == 1
        assert instrumenter.get_swaps() == 0
        assert instrumenter.get_hits() == 0

    def test_count_fixes_reports_whether_fixes_are_counted(self):
        assert all_on().count_fixes()
        assert not Instrumenter(True, True, True, False, True, True, True).count_fixes()

    def test_is_show_stats(self):
        assert all_on().is_show_stats()
        assert not Instrumenter(True, True, True, True, True, True, False).is_show_stats()

    def test_str(self):
        instrumenter = all_on()
        instrumenter.increment_compares()
        assert "compares=1" in str(instrumenter)


class TestGathering:
    def test_gather_moves_the_counts_into_the_statistics(self):
        instrumenter = all_on()
        instrumenter.init(100, 2)
        instrumenter.increment_compares()
        instrumenter.increment_swaps(3)
        instrumenter.gather_statistic()
        assert instrumenter.get_stat_pack().get_count(COMPARES) == 1
        assert instrumenter.get_stat_pack().mean(SWAPS) == pytest.approx(3.0)

    def test_gather_resets_the_counters(self):
        instrumenter = all_on()
        instrumenter.init(100, 2)
        instrumenter.increment_compares()
        instrumenter.gather_statistic()
        assert instrumenter.get_compares() == 0

    def test_two_runs_both_land_in_the_statistics(self):
        instrumenter = all_on()
        instrumenter.init(100, 2)
        instrumenter.increment_swaps(2)
        instrumenter.gather_statistic()
        instrumenter.init(100, 2)
        instrumenter.increment_swaps(4)
        instrumenter.gather_statistic()
        assert instrumenter.get_stat_pack().get_count(SWAPS) == 2
        assert instrumenter.get_stat_pack().mean(SWAPS) == pytest.approx(3.0)

    def test_init_does_not_discard_what_has_been_gathered(self):
        # Replacing the StatPack on a second init would throw away the first
        # run, which is exactly what the second run is meant to add to.
        instrumenter = all_on()
        instrumenter.init(100, 2)
        first = instrumenter.get_stat_pack()
        instrumenter.init(100, 2)
        assert instrumenter.get_stat_pack() is first

    def test_init_resets_the_counters(self):
        instrumenter = all_on()
        instrumenter.init(100, 2)
        instrumenter.increment_compares()
        instrumenter.init(100, 2)
        assert instrumenter.get_compares() == 0

    def test_gather_before_init_does_nothing(self):
        # get_stat_pack falls back to an empty StatPack, which is invalid, so
        # gather_statistic returns without touching it.
        instrumenter = all_on()
        instrumenter.increment_compares()
        instrumenter.gather_statistic()
        assert instrumenter.get_stat_pack().is_invalid()

    def test_a_switched_off_counter_contributes_nothing(self):
        instrumenter = Instrumenter(False, False, True, False, False, False, False)
        instrumenter.init(100, 1)
        instrumenter.increment_compares()
        instrumenter.gather_statistic()
        assert instrumenter.get_stat_pack().get_count(COMPARES) == 1
        assert instrumenter.get_stat_pack().get_count(SWAPS) == 0


class TestFromConfig:
    def test_it_reads_the_instrumenting_section(self):
        instrumenter = Instrumenter.from_config(setup_config("true", "true", "0", "0", "", ""))
        assert instrumenter.counting_compares
        assert instrumenter.counting_swaps
        assert instrumenter.counting_fixes

    def test_fixes_is_separate_from_the_rest(self):
        instrumenter = Instrumenter.from_config(setup_config("true", "false", "0", "0", "", ""))
        assert instrumenter.counting_compares
        assert not instrumenter.counting_fixes

    def test_the_shipped_config(self):
        # config.ini has showStats, swaps, compares, copies, hits and lookups
        # true, and fixes false because counting them is expensive.
        instrumenter = Instrumenter.from_config(Config.load())
        assert instrumenter.counting_compares
        assert instrumenter.counting_hits
        assert instrumenter.counting_lookups
        assert not instrumenter.counting_fixes
        assert instrumenter.show_stats


class TestInstrumenterDummy:
    def test_every_counter_stays_at_zero(self):
        dummy = InstrumenterDummy()
        dummy.init(100, 2)
        dummy.increment_compares()
        dummy.increment_swaps(3)
        dummy.increment_copies(4)
        dummy.increment_fixes(5)
        dummy.increment_hits(6)
        dummy.increment_lookups(7)
        assert dummy.get_compares() == 0
        assert dummy.get_swaps() == 0
        assert dummy.get_copies() == 0
        assert dummy.get_fixes() == 0
        assert dummy.get_hits() == 0
        assert dummy.get_lookups() == 0

    def test_no_stat_pack(self):
        assert InstrumenterDummy().get_stat_pack() is None

    def test_it_never_counts_fixes_or_shows_stats(self):
        assert not InstrumenterDummy().count_fixes()
        assert not InstrumenterDummy().is_show_stats()

    def test_gather_is_harmless(self):
        InstrumenterDummy().gather_statistic()

    def test_it_accepts_a_config_and_ignores_it(self):
        assert InstrumenterDummy(Config.load()).get_hits() == 0

    def test_it_can_stand_in_for_an_instrumenter(self):
        # The point of the dummy: the sort does not need to know which it has.
        for instrument in (all_on(), InstrumenterDummy()):
            instrument.init(10, 1)
            instrument.increment_hits(1)
            instrument.gather_statistic()
