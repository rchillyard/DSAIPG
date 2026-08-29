import math
from random import Random

import pytest

from src.util.general.utilities import (
    as_array,
    as_int,
    fill_random_array,
    format_decimal_3_places,
    format_whole,
    format_whole_with_commas,
    lg,
    round_half_up,
)


class TestRoundHalfUp:
    """
    round_half_up must match Java's Math.round, which is NOT what Python's
    built-in round does: the built-in rounds a half to the nearest even integer.
    """

    def test_halves_go_up_where_python_would_go_even(self):
        assert round_half_up(2.5) == 3          # built-in round gives 2
        assert round_half_up(0.5) == 1          # built-in round gives 0
        assert round_half_up(4.5) == 5          # built-in round gives 4

    def test_agrees_with_the_built_in_where_they_agree(self):
        assert round_half_up(3.5) == round(3.5) == 4
        assert round_half_up(-2.5) == round(-2.5) == -2

    def test_ordinary_values(self):
        assert round_half_up(2.4) == 2
        assert round_half_up(2.6) == 3
        assert round_half_up(-2.4) == -2
        assert round_half_up(-2.6) == -3


class TestFormatting:
    def test_format_whole_has_no_separators(self):
        assert format_whole(1024) == "1024"
        assert format_whole(1000000) == "1000000"

    def test_format_whole_with_commas_has_separators(self):
        assert format_whole_with_commas(1024) == "1,024"
        assert format_whole_with_commas(1000000) == "1,000,000"

    def test_as_int_rounds_then_formats(self):
        assert as_int(1.4) == "1"
        assert as_int(1.5) == "2"
        assert as_int(1024.5) == "1025"

    def test_format_decimal_3_places(self):
        assert format_decimal_3_places(0.7213475204444817) == "0.721"
        assert format_decimal_3_places(1.0) == "1.000"
        assert format_decimal_3_places(2.0005) == "2.001"


class TestLg:
    def test_powers_of_two(self):
        assert lg(1) == pytest.approx(0)
        assert lg(2) == pytest.approx(1)
        assert lg(1024) == pytest.approx(10)

    def test_agrees_with_log2(self):
        for n in (3, 7, 100, 12345):
            assert lg(n) == pytest.approx(math.log2(n))


class TestAsArray:
    def test_valid_collection(self):
        assert as_array([1, 2, 3]) == [1, 2, 3]

    def test_copies_rather_than_aliases(self):
        source = [1, 2, 3]
        result = as_array(source)
        result.append(4)
        assert source == [1, 2, 3]

    def test_an_empty_collection_is_fine(self):
        # An empty collection gives an empty list. The Java needs a component
        # type to make an array of and takes it as a parameter; Python does not.
        assert as_array([]) == []

    def test_a_heterogeneous_collection(self):
        # The case that motivated the Java change: it built the array from the
        # first element's class and then threw ArrayStoreException on the rest.
        # Python lists have no component type, so there is nothing to get wrong.
        assert as_array([1, 2.5, "three"]) == [1, 2.5, "three"]


class TestFillRandomArray:
    def test_length(self):
        assert len(fill_random_array(Random(0), 10, lambda r: r.randint(0, 99))) == 10

    def test_is_repeatable_for_a_given_seed(self):
        first = fill_random_array(Random(42), 20, lambda r: r.randint(0, 999))
        second = fill_random_array(Random(42), 20, lambda r: r.randint(0, 999))
        assert first == second

    def test_uses_the_supplied_function(self):
        assert fill_random_array(Random(0), 5, lambda r: 7) == [7, 7, 7, 7, 7]

    def test_zero_length(self):
        assert fill_random_array(Random(0), 0, lambda r: r.random()) == []
