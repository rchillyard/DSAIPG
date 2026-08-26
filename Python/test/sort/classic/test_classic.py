import random

import pytest

from src.sort.classic.bucket_sort import (
    ALPHABET_SIZE,
    BucketSort,
    classify_string_digraph,
    classify_string_initial,
    get_number_classifier,
)
from src.sort.classic.classic_sort import ClassicSort
from src.sort.classic.classification_sorter import ignoring_second
from src.sort.classic.classify import Classify
from src.sort.generic.sort_exception import SortException
from src.sort.helper.helper_factory import create
from src.util.config.config_benchmark import setup_config

PLAIN = setup_config("false", "", "0", "0", "", "")
INSTRUMENTED = setup_config("true", "false", "0", "0", "", "")


class Item(Classify):
    """
    A value classified by the int it is given. classify() may return any int, so
    sparse and negative values are both legal.
    """

    def __init__(self, cls: int) -> None:
        self.cls = cls

    def classify(self) -> int:
        return self.cls

    def __eq__(self, other) -> bool:
        return isinstance(other, Item) and other.cls == self.cls

    def __repr__(self) -> str:
        return str(self.cls)


def classic(n, config=PLAIN):
    return ClassicSort(create("ClassicSort", n, config))


def bucket(n, classifier=None, n_buckets=ALPHABET_SIZE, config=PLAIN, comparator=None):
    return BucketSort(create("BucketSort", n, config, comparator), classifier, n_buckets)


class TestClassicSort:
    def test_sort_sparse_classes(self):
        # Classes are visited in ascending order whatever their values. Grouping
        # them in a dict and taking the keys in insertion order would give
        # [100, 5, 20] here.
        xs = [Item(100), Item(5), Item(20)]
        assert classic(3).sort(xs) == [Item(5), Item(20), Item(100)]

    def test_sort_negative_classes(self):
        xs = [Item(7), Item(-40), Item(-3), Item(0)]
        assert classic(4).sort(xs) == [Item(-40), Item(-3), Item(0), Item(7)]

    def test_sort_repeated_classes(self):
        # With several elements per class only the class order can be asserted: a
        # Bag iterates in a deliberately arbitrary order, so ClassicSort groups by
        # class but does not order within one. That is the following pass's job,
        # which is what BucketSort does.
        xs = [Item(60), Item(3), Item(60), Item(3), Item(17)]
        result = classic(5).sort(xs)
        classes = [x.classify() for x in result]
        assert classes == sorted(classes)

    def test_it_keeps_every_element(self):
        rng = random.Random(4)
        xs = [Item(rng.randint(-50, 50)) for _ in range(200)]
        result = classic(200).sort(xs)
        assert sorted(x.classify() for x in result) == sorted(x.classify() for x in xs)

    def test_a_single_element(self):
        assert classic(1).sort([Item(5)]) == [Item(5)]

    def test_an_empty_list(self):
        assert classic(0).sort([]) == []

    def test_it_leaves_the_original_alone(self):
        xs = [Item(3), Item(1)]
        classic(2).sort(xs)
        assert xs == [Item(3), Item(1)]

    def test_the_description(self):
        assert classic(1).get_description() == "Classic sort"


class TestBucketSort:
    def test_sort_by_initial_letter(self):
        xs = ["Bravo", "Campion", "Able", "Aardvark", "Beetle"]
        result = bucket(len(xs), classify_string_initial).sort(xs)
        assert result == ["Aardvark", "Able", "Beetle", "Bravo", "Campion"]

    def test_sort_by_digraph(self):
        xs = ["Bravo", "Campion", "Able", "Aardvark", "Beetle", "C"]
        result = bucket(len(xs), classify_string_digraph, 27 * 27).sort(xs)
        assert result == ["Aardvark", "Able", "Beetle", "Bravo", "C", "Campion"]

    def test_sort_numbers_without_a_classifier(self):
        # With no classifier and numeric values, one is worked out from the range.
        rng = random.Random(9)
        xs = [rng.randint(0, 999) for _ in range(200)]
        assert bucket(200, None, 16).sort(xs) == sorted(xs)

    def test_it_rejects_non_numbers_without_a_classifier(self):
        with pytest.raises(SortException, match="not a Number"):
            bucket(3, None, 16).sort(["a", "b", "c"])

    def test_a_random_list(self):
        rng = random.Random(42)
        xs = [rng.randint(0, 9999) for _ in range(500)]
        assert bucket(500, None, 32).sort(xs) == sorted(xs)

    def test_an_already_sorted_list(self):
        xs = list(range(200))
        assert bucket(200, None, 16).sort(xs) == xs

    def test_duplicates(self):
        rng = random.Random(2)
        xs = [rng.randint(0, 5) for _ in range(200)]
        assert bucket(200, None, 8).sort(xs) == sorted(xs)

    def test_a_single_element(self):
        # min == max, so the gap is zero and every value belongs in bucket 0.
        assert bucket(1, None, 4).sort([7]) == [7]

    def test_all_values_equal(self):
        # The same zero-gap case, with more than one element.
        assert bucket(4, None, 16).sort([5, 5, 5, 5]) == [5, 5, 5, 5]

    def test_it_can_be_run_twice(self):
        # The buckets are cleared at the start of each sort.
        sorter = bucket(5, classify_string_initial)
        assert sorter.sort(["cat", "ant", "bee"] + ["dog", "eel"]) \
               == ["ant", "bee", "cat", "dog", "eel"]
        assert sorter.sort(["zoo", "yak", "wren", "vole", "urchin"]) \
               == ["urchin", "vole", "wren", "yak", "zoo"]

    def test_it_cannot_sort_a_sub_range(self):
        # RECORDED, NOT ENDORSED. _check_buckets compares the number of elements
        # distributed against the length of the whole list, so anything but a
        # whole-list sort raises. The Java does the same: sort(xs, 1, 4) on five
        # elements gives "incorrect number of buckets: 3, 5".
        xs = ["zulu", "bravo", "charlie", "alpha", "delta"]
        with pytest.raises(RuntimeError, match="incorrect number of buckets: 3, 5"):
            bucket(5, classify_string_initial).sort_range(xs, 1, 4)


class TestClassifiers:
    def test_classify_string_initial(self):
        assert classify_string_initial("apple") == 1
        assert classify_string_initial("Zebra") == 26
        assert classify_string_initial(" space") == 0

    def test_classify_string_initial_of_a_non_letter(self):
        assert classify_string_initial("9lives") == -1

    def test_classify_string_digraph_orders_by_the_first_two_letters(self):
        assert classify_string_digraph("ab") < classify_string_digraph("ac")
        assert classify_string_digraph("az") < classify_string_digraph("ba")

    def test_classify_string_digraph_pads_a_single_character(self):
        # "c" becomes "c ", which sorts before "ca".
        assert classify_string_digraph("c") < classify_string_digraph("ca")

    def test_the_number_classifier_spreads_over_the_buckets(self):
        classifier = get_number_classifier([0, 100], 0, 2, 10)
        assert classifier(0) == 0
        assert classifier(99) == 9

    def test_the_number_classifier_clamps(self):
        classifier = get_number_classifier([0, 100], 0, 2, 10)
        assert classifier(-50) == 0
        assert classifier(500) == 9

    def test_ignoring_second(self):
        assert ignoring_second(lambda x: x * 2)(21, None) == 42

    def test_ignoring_second_of_none(self):
        assert ignoring_second(None) is None


class TestInstrumented:
    def test_bucket_sort_counts(self):
        rng = random.Random(1)
        xs = [rng.randint(0, 999) for _ in range(200)]
        sorter = bucket(200, None, 16, INSTRUMENTED)
        sorter.sort(xs)
        helper = sorter.get_helper()
        # One copy into a bucket and one out again, for every element.
        assert helper.get_copies() >= 2 * len(xs)
        assert helper.get_compares() > 0

    def test_classify_counts_a_lookup(self):
        sorter = bucket(3, classify_string_initial, 27, INSTRUMENTED)
        sorter.classify("apple", None)
        assert sorter.get_helper().get_lookups() == 1
