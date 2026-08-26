import logging
import math

import pytest

from src.util.benchmark.time_logger import TimeLogger, format_time


class TestFormatTime:
    @pytest.mark.parametrize("value,expected", [
        (0.5, "0.5000"),
        (0.0, "0.0000"),
        (12.3456789, "12.3457"),
        (1234567.5, "1234567.5000"),
        (1e6, "1000000.0000"),
        (0.00004, "0.0000"),
    ])
    def test_it_matches_the_java(self, value, expected):
        # Checked against DecimalFormat with the pattern "#####0.0000". Note that
        # the integer part is never truncated to the five "#" of the pattern.
        assert format_time(value) == expected

    def test_a_small_negative_number_keeps_its_sign(self):
        assert format_time(-0.5) == "-0.5000"

    def test_no_thousands_separators(self):
        assert "," not in format_time(1234567.0)


class TestTimeLogger:
    def test_a_raw_time_is_logged_unchanged(self, caplog):
        with caplog.at_level(logging.INFO, logger="src.util.benchmark.time_logger"):
            TimeLogger("Raw time per run (mSec):", None).log("MergeSort", 0.5, 1000)
        assert "MergeSort: Raw time per run (mSec): 0.5000" in caplog.text

    def test_a_normalized_time_is_divided_by_the_complexity(self, caplog):
        # time / (n lg n) * 1e6, with time 2.0 and n 1024: 1024 * 10 = 10240.
        with caplog.at_level(logging.INFO, logger="src.util.benchmark.time_logger"):
            TimeLogger("Normalized:", lambda n: n * math.log2(n)).log("MergeSort", 2.0, 1024)
        assert format_time(2.0 / 10240 * 1e6) in caplog.text

    def test_the_normalized_figure_stays_put_as_n_grows(self):
        # This is the point of normalizing: for an algorithm that really is
        # n lg n, doubling n should not move the number much.
        def complexity(n: int) -> float:
            return n * math.log2(n)

        small, large = 1024, 2048
        time_small, time_large = 1.0, 1.0 * complexity(large) / complexity(small)
        normalized_small = time_small / complexity(small) * 1e6
        normalized_large = time_large / complexity(large) * 1e6
        assert normalized_small == pytest.approx(normalized_large)
