import math

import pytest

from src.util.benchmark.stat_pack import StatPack, empty
from src.util.benchmark.statistics import Statistics

# These mirror StatPackTest.java.


def identity(x: float) -> float:
    """The normalizer used by the Java tests: it leaves the size unchanged."""
    return x * 1.0


class TestStatPack:
    def test_add_and_count(self):
        pack = StatPack(identity, 4, 1, "compares", "swaps")
        pack.add("compares", 1)
        pack.add("compares", 3)
        pack.add("swaps", 2)
        assert pack.get_count("compares") == 2
        assert pack.get_count("swaps") == 1

    def test_get_statistics_returns_the_right_one(self):
        pack = StatPack(identity, 4, 1, "compares", "swaps")
        statistics = pack.get_statistics("compares")
        assert isinstance(statistics, Statistics)
        assert statistics is pack.get_statistics("compares")

    def test_get_statistics_rejects_an_undeclared_key(self):
        pack = StatPack(identity, 4, 1, "compares")
        with pytest.raises(KeyError):
            pack.get_statistics("hits")

    def test_total_mean_and_std_dev(self):
        pack = StatPack(identity, 4, 1, "x")
        for value in (-1, 0, 1, 4):
            pack.add("x", value)
        assert pack.total("x") == pytest.approx(4.0)
        assert pack.mean("x") == pytest.approx(1.0)
        assert pack.std_dev("x") == pytest.approx(math.sqrt(3.5), abs=1e-7)

    def test_keys_are_independent(self):
        pack = StatPack(identity, 4, 1, "a", "b")
        pack.add("a", 10)
        pack.add("b", 20)
        assert pack.mean("a") == pytest.approx(10.0)
        assert pack.mean("b") == pytest.approx(20.0)

    def test_is_invalid(self):
        assert StatPack(identity, 0, 0).is_invalid()
        assert not StatPack(identity, 1, 1, "x").is_invalid()

    def test_str_when_empty(self):
        assert str(StatPack(identity, 4, 1)) == "StatPack {<empty>}"

    def test_str_reports_each_statistic(self):
        pack = StatPack(identity, 4, 1, "x")
        pack.add("x", 2)
        rendered = str(pack)
        assert rendered.startswith("StatPack {")
        assert rendered.endswith("}")
        assert "x: " in rendered

    def test_empty_is_invalid_and_fresh_each_time(self):
        # empty() is a function rather than a shared constant, because a
        # StatPack is mutable and a shared one would be shared mutable state.
        assert empty().is_invalid()
        assert empty() is not empty()
