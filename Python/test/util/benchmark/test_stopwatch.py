import pytest

from src.util.benchmark.stopwatch import MILLION, Stopwatch


class TestStopwatch:
    def test_lap_is_not_negative(self):
        with Stopwatch() as stopwatch:
            assert stopwatch.lap() >= 0

    def test_successive_laps(self):
        with Stopwatch("nanoseconds") as stopwatch:
            first = stopwatch.lap()
            second = stopwatch.lap()
        assert first > 0
        assert second > 0

    def test_a_lap_in_nanoseconds_is_larger_than_one_in_milliseconds(self):
        with Stopwatch("nanoseconds") as nanos:
            sum(range(200_000))
            in_nanos = nanos.lap()
        assert in_nanos > 1_000, "200,000 additions should take more than a microsecond"

    def test_default_units_are_milliseconds(self):
        assert Stopwatch()._time_factor == MILLION
        assert Stopwatch(None)._time_factor == MILLION

    @pytest.mark.parametrize("units,factor", [
        ("nanoseconds", 1),
        ("microseconds", 1_000),
        ("milliseconds", MILLION),
        ("seconds", 1_000 * MILLION),
    ])
    def test_units(self, units, factor):
        assert Stopwatch(units)._time_factor == factor

    def test_invalid_units(self):
        with pytest.raises(ValueError, match="Invalid time units: fortnights"):
            Stopwatch("fortnights")

    def test_close_then_lap(self):
        stopwatch = Stopwatch()
        stopwatch.close()
        with pytest.raises(AssertionError, match="Stopwatch is closed"):
            stopwatch.lap()

    def test_the_context_manager_closes_it(self):
        with Stopwatch() as stopwatch:
            pass
        assert stopwatch._start is None

    def test_it_closes_even_when_the_body_raises(self):
        stopwatch = None
        with pytest.raises(RuntimeError):  # noqa: PT012 - we need the body to raise
            with Stopwatch() as s:
                stopwatch = s
                raise RuntimeError("boom")
        assert stopwatch._start is None
