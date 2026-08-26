import pytest

from src.sort.helper.instrument import COMPARES, FIXES, INSTRUMENTING, SWAPS
from src.util.config.config_benchmark import (
    CUTOFF,
    CUTOFF_DEFAULT,
    HELPER,
    INSTRUMENT,
    INSURANCE,
    MERGESORT,
    NOCOPY,
    SEED,
    get_seed,
    is_instrumented,
    setup_config,
    setup_config2,
    setup_config_fixes,
)


class TestIsInstrumented:
    def test_true(self):
        assert is_instrumented(setup_config("true", "", "0", "0", "", ""))

    def test_false(self):
        assert not is_instrumented(setup_config("false", "", "0", "0", "", ""))

    def test_empty(self):
        assert not is_instrumented(setup_config("", "", "0", "0", "", ""))


class TestGetSeed:
    def test_the_configured_seed(self):
        assert get_seed(setup_config("true", "", "42", "0", "", "")) == 42

    def test_no_seed_gives_something_clock_like(self):
        # With no seed, runs should differ, so the default is the current time
        # in milliseconds. Just check it is plausible rather than exact.
        assert get_seed(setup_config("true", "", "", "0", "", "")) > 1_600_000_000_000


class TestSetupConfig:
    def test_the_helper_section(self):
        config = setup_config("true", "false", "42", "0", "7", "")
        assert config.get(HELPER, INSTRUMENT) == "true"
        assert config.get(HELPER, SEED) == "42"
        assert config.get_int(HELPER, CUTOFF, -1) == 7

    def test_the_instrumenting_section_follows_the_instrumenting_argument(self):
        config = setup_config("true", "false", "0", "0", "", "")
        assert config.get_boolean(INSTRUMENTING, SWAPS)
        assert config.get_boolean(INSTRUMENTING, COMPARES)

    def test_fixes_is_set_separately(self):
        # Counting fixes is expensive, so it is not simply tied to the rest.
        assert not setup_config("true", "false", "0", "0", "", "").get_boolean(INSTRUMENTING, FIXES)
        assert setup_config("true", "true", "0", "0", "", "").get_boolean(INSTRUMENTING, FIXES)

    def test_an_empty_cutoff_leaves_the_default_to_the_caller(self):
        assert setup_config("true", "", "0", "0", "", "").get_int(HELPER, CUTOFF, 99) == 99


class TestSetupConfig2:
    def test_insurance_and_nocopy_land_in_the_mergesort_section(self):
        # The Java put these in [helper], but MergeSort reads [mergesort], so
        # both always came back false and the four combinations these arguments
        # describe were all the same run.
        config = setup_config2("true", "0", "1", "", "", "true", "true")
        assert config.get_boolean(MERGESORT, INSURANCE)
        assert config.get_boolean(MERGESORT, NOCOPY)

    @pytest.mark.parametrize("insurance,no_copy", [
        ("false", "false"), ("false", "true"), ("true", "false"), ("true", "true"),
    ])
    def test_all_four_combinations_are_distinct(self, insurance, no_copy):
        config = setup_config2("true", "0", "1", "", "", insurance, no_copy)
        assert config.get_boolean(MERGESORT, INSURANCE) == (insurance == "true")
        assert config.get_boolean(MERGESORT, NOCOPY) == (no_copy == "true")

    def test_it_still_sets_up_instrumenting(self):
        config = setup_config2("true", "0", "1", "", "", "false", "false")
        assert is_instrumented(config)
        assert config.get_boolean(INSTRUMENTING, COMPARES)


class TestSetupConfigFixes:
    def test_it_instruments_and_counts_fixes(self):
        config = setup_config_fixes()
        assert is_instrumented(config)
        assert config.get_boolean(INSTRUMENTING, FIXES)

    def test_the_default_cutoff(self):
        assert setup_config_fixes().get_int(HELPER, CUTOFF, -1) == CUTOFF_DEFAULT == 20
