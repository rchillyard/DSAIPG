import math

import pytest

from src.sort.generic.sort_exception import SortException
from src.util.benchmark.statistics import Statistics, normalizer_linearithmic_natural

# These mirror StatisticsTest.java.


def identity(x: float) -> float:
    """The normalizer used by the Java tests: it leaves the size unchanged."""
    return x * 1.0


class TestStatistics:
    def test_mean_with_negative_and_positive_values(self):
        statistics = Statistics("test", identity, 5, 1)
        for x in (-3, -2, 0, 2, 3):
            statistics.add(x)
        assert statistics.mean() == pytest.approx(0, abs=1e-7)

    def test_mean_with_zero_values(self):
        statistics = Statistics("test", identity, 3, 1)
        for _ in range(3):
            statistics.add(0)
        assert statistics.mean() == pytest.approx(0, abs=1e-7)

    def test_add(self):
        statistics = Statistics("test", identity, 3, 1)
        for x in (-1, 0, 1):
            statistics.add(x)
        assert statistics.get_count() == 3

    def test_mean(self):
        statistics = Statistics("test", identity, 4, 1)
        for x in (-1, 0, 1, 2):
            statistics.add(x)
        assert statistics.mean() == pytest.approx(0.5, abs=1e-7)

    def test_std_dev(self):
        statistics = Statistics("test", identity, 4, 1)
        for x in (-1, 0, 1, 4):
            statistics.add(x)
        assert statistics.std_dev() == pytest.approx(math.sqrt(3.5), abs=1e-7)

    def test_str(self):
        statistics = Statistics("test", normalizer_linearithmic_natural, 4, 2)
        for x in (-1, 0, 1, 4):
            statistics.add(x)
        assert str(statistics) == "test: n=4; mean=1; stdDev=2; normalized=0.721"

    def test_str_before_anything_is_added(self):
        assert str(Statistics("test", identity, 4, 2)) == "test: <unset>"

    def test_normalized_mean(self):
        statistics = Statistics("test", normalizer_linearithmic_natural, 2, 2)
        statistics.add(1)
        statistics.add(1)
        # mean is 1; the normalizer of size 2 is 2 ln 2.
        assert statistics.normalized_mean() == pytest.approx(1 / (2 * math.log(2)), abs=1e-7)

    def test_total_is_cached_but_invalidated_by_add(self):
        statistics = Statistics("test", identity, 4, 1)
        statistics.add(1)
        assert statistics.total() == 1
        statistics.add(2)
        assert statistics.total() == 3

    def test_grows_beyond_the_declared_number_of_runs(self):
        # The Java version doubles its array when full, so adding more than
        # n_runs values is allowed rather than an error.
        statistics = Statistics("test", identity, 2, 1)
        for x in (1, 2, 3, 4, 5):
            statistics.add(x)
        assert statistics.get_count() == 5
        assert statistics.mean() == pytest.approx(3.0)

    def test_add_with_no_room_at_all_raises(self):
        with pytest.raises(SortException):
            Statistics("test", identity, 0, 1).add(1)


class TestNormalizer:
    def test_linearithmic_natural(self):
        assert normalizer_linearithmic_natural(1) == pytest.approx(0.0)
        assert normalizer_linearithmic_natural(2) == pytest.approx(2 * math.log(2))
