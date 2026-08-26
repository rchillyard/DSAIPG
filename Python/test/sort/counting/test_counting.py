import random

import pytest

from src.sort.counting import radix_sort
from src.sort.counting.lsd_string_sort import LSDStringSort, char_at, find_max_length
from src.sort.counting.msd_string_sort import MSDStringSort
from src.sort.helper.helper_factory import create
from src.util.config.config_benchmark import setup_config
from src.util.general.code_point_mapper import ASCII, ASCIIExt, English
from src.util.general.suffix_comparator import SuffixComparator

PLAIN = setup_config("false", "", "0", "0", "", "")
INSTRUMENTED = setup_config("true", "false", "0", "0", "", "")

WORDS = ["banana", "apple", "cherry", "date", "elderberry", "fig", "grape",
         "apricot", "blueberry", "coconut", "damson", "kiwi", "lemon", "mango"]


def lsd(n, w=0, config=PLAIN):
    return LSDStringSort(create("LSDStringSort", n, config, ASCII.comparator), w)


def msd(n, mapper=ASCIIExt, config=PLAIN):
    return MSDStringSort(create("MSDStringSort", n, config, mapper.comparator), mapper)


class TestRadixSort:
    def test_a_small_list(self):
        xs = [170, 45, 75, 90, 802, 24, 2, 66]
        radix_sort.sort(xs, 0, len(xs) - 1)
        assert xs == sorted(xs)

    def test_a_random_list(self):
        rng = random.Random(42)
        xs = [rng.randint(0, 99999) for _ in range(500)]
        expected = sorted(xs)
        radix_sort.sort(xs, 0, len(xs) - 1)
        assert xs == expected

    def test_duplicates(self):
        xs = [5, 3, 5, 3, 1, 1, 5]
        radix_sort.sort(xs, 0, len(xs) - 1)
        assert xs == sorted([5, 3, 5, 3, 1, 1, 5])

    def test_already_sorted(self):
        xs = list(range(50))
        radix_sort.sort(xs, 0, 49)
        assert xs == list(range(50))

    def test_zeros(self):
        xs = [0, 0, 0]
        radix_sort.sort(xs, 0, 2)
        assert xs == [0, 0, 0]

    def test_a_sub_range(self):
        # NOTE to is INCLUSIVE here, unlike everywhere else in the tree.
        xs = [99, 5, 3, 4, 1, 99]
        radix_sort.sort(xs, 1, 4)
        assert xs == [99, 1, 3, 4, 5, 99]

    def test_a_single_element(self):
        xs = [7]
        radix_sort.sort(xs, 0, 0)
        assert xs == [7]

    def test_from_equal_to_does_nothing(self):
        xs = [3, 1, 2]
        radix_sort.sort(xs, 1, 1)
        assert xs == [3, 1, 2]

    def test_from_after_to(self):
        with pytest.raises(ValueError, match="From value should be less than to"):
            radix_sort.sort([3, 1, 2], 2, 1)

    def test_indices_out_of_range(self):
        with pytest.raises(IndexError):
            radix_sort.sort([3, 1, 2], 0, 9)
        with pytest.raises(IndexError):
            radix_sort.sort([3, 1, 2], -1, 2)

    def test_find_max_int(self):
        assert radix_sort.find_max_int([3, 9, 2, 7], 0, 3) == 9
        assert radix_sort.find_max_int([3, 9, 2, 7], 2, 3) == 7

    def test_count_sort_is_stable(self):
        # Stability is what makes the successive passes work. These all have the
        # same units digit, so a pass by units must leave them exactly as they
        # are -- otherwise the order an earlier pass established would be lost.
        xs = [21, 11, 31]
        radix_sort.count_sort(xs, 1, 0, 2)
        assert xs == [21, 11, 31]

    def test_count_sort_orders_by_the_selected_digit(self):
        xs = [21, 11, 31]
        radix_sort.count_sort(xs, 10, 0, 2)
        assert xs == [11, 21, 31]


class TestLSDStringSort:
    def test_equal_length_strings(self):
        xs = ["dab", "cab", "bad", "abc"]
        assert lsd(len(xs), 3).sort(xs) == ["abc", "bad", "cab", "dab"]

    def test_unequal_length_strings(self):
        xs = list(WORDS)
        result = lsd(len(xs)).sort(xs)
        assert result == sorted(xs)

    def test_a_random_list(self):
        rng = random.Random(7)
        xs = ["".join(rng.choice("abcdefg") for _ in range(rng.randint(1, 6)))
              for _ in range(200)]
        assert lsd(200).sort(xs) == sorted(xs)

    def test_duplicates(self):
        xs = ["cat", "ant", "cat", "ant", "bee"]
        assert lsd(len(xs)).sort(xs) == sorted(xs)

    def test_a_single_string(self):
        assert lsd(1).sort(["solo"]) == ["solo"]

    def test_an_empty_list(self):
        assert lsd(0).sort([]) == []

    def test_it_sorts_when_instrumented(self):
        xs = list(WORDS)
        assert lsd(len(xs), 0, INSTRUMENTED).sort(xs) == sorted(xs)

    def test_char_at(self):
        assert char_at("abc", 0) == ord("a")
        assert char_at("abc", 9) == 0, "past the end reads as zero, so it sorts first"

    def test_find_max_length(self):
        assert find_max_length(["a", "abc", "ab"]) == 3
        assert find_max_length([]) == 0
        assert find_max_length(["a", "abcd", "ab"], 0, 2) == 4


class TestMSDStringSort:
    """
    NOTE the recursion is an exercise, so these are reported as skipped until it
    is written.
    """

    def test_a_small_list(self):
        xs = ["dab", "cab", "bad", "abc"]
        assert msd(len(xs)).sort(xs) == ["abc", "bad", "cab", "dab"]

    def test_unequal_length_strings(self):
        # MSD's advantage over LSD: nothing is padded to the longest string.
        xs = list(WORDS)
        assert msd(len(xs)).sort(xs) == sorted(xs)

    def test_a_random_list(self):
        rng = random.Random(11)
        xs = ["".join(rng.choice("abcdefg") for _ in range(rng.randint(1, 8)))
              for _ in range(300)]
        assert msd(300).sort(xs) == sorted(xs)

    def test_common_prefixes(self):
        # The case MSD handles well: it stops as soon as a group is settled.
        xs = ["abcdef", "abcdeg", "abcdeh", "abcdei", "abc", "ab", "a"]
        assert msd(len(xs)).sort(xs) == sorted(xs)

    def test_duplicates(self):
        xs = ["cat", "ant", "cat", "ant", "bee"]
        assert msd(len(xs)).sort(xs) == sorted(xs)

    def test_a_single_string(self):
        assert msd(1).sort(["solo"]) == ["solo"]

    def test_an_empty_list(self):
        assert msd(0).sort([]) == []

    def test_with_the_english_mapper(self):
        # English folds case, so the result must be checked by that ordering, not
        # by the natural one.
        xs = ["Banana", "apple", "Cherry", "date"]
        result = msd(len(xs), English).sort(xs)
        for i in range(1, len(result)):
            assert English.compare(result[i - 1], result[i]) <= 0

    def test_it_records_the_memory_it_uses(self):
        xs = list(WORDS)
        sorter = msd(len(xs), ASCIIExt, INSTRUMENTED)
        sorter.init(len(xs))
        sorter.sort(xs)
        assert sorter.get_memory_factor() > 1.0, "it allocates an auxiliary list"


class TestCodePointMapper:
    def test_ascii_maps_to_seven_bits(self):
        assert ASCII.map_code_point(ord("a")) == ord("a")
        assert ASCII.range == 128

    def test_english_folds_case(self):
        assert English.map_code_point(ord("A")) == English.map_code_point(ord("a"))

    def test_english_maps_non_letters_to_zero(self):
        assert English.map_code_point(ord(" ")) == 0
        assert English.map_code_point(ord("'")) == 0

    def test_english_letters_are_one_to_twenty_six(self):
        assert English.map_code_point(ord("a")) == 1
        assert English.map_code_point(ord("z")) == 26

    def test_the_comparator_agrees_with_the_mapper(self):
        # This is the property that matters: a radix sort groups by the mapped
        # character, so anything checking the result must agree about which
        # characters are equal.
        assert English.compare("ARAB", "arab") == 0
        assert English.compare("abroad", "Arab") < 0

    def test_map_string(self):
        assert ASCII.map_string("abc") == "abc"

    def test_out_of_range_is_rejected(self):
        from src.util.general.code_point_mapper import CodePointMapper
        bad = CodePointMapper("bad", lambda x: 999, 10, lambda a, b: 0)
        with pytest.raises(RuntimeError, match="out of range"):
            bad.map_code_point(ord("a"))

    def test_str(self):
        assert "English" in str(English)


class TestSuffixComparator:
    def test_it_compares_from_the_given_depth(self):
        cmp = SuffixComparator(ASCII.comparator, 2)
        assert cmp("xxabc", "yyabd") < 0
        assert cmp("xxabc", "yyabc") == 0

    def test_a_depth_of_zero_compares_everything(self):
        cmp = SuffixComparator(ASCII.comparator)
        assert cmp("abc", "abd") < 0

    def test_past_the_end_gives_a_space(self):
        # discriminate_string returns " " past the end, so a short string sorts
        # before a longer one sharing its prefix.
        cmp = SuffixComparator(ASCII.comparator, 3)
        assert cmp("abc", "abcd") < 0
