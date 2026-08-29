"""
Tests for misc/equable and the Tuple classes that exercise it.
"""

from __future__ import annotations

import math

import pytest

from src.misc.equable.base_equable import BaseEquable
from src.misc.equable.equable import (
    ComparableEquable,
    ComparableEquableException,
    Equable,
)
from src.misc.tuple import ComparableTuple, Tuple, index


class TestEquable:
    def test_equal_elements(self):
        assert Equable([1, 2, 3]) == Equable([1, 2, 3])
        assert hash(Equable([1, 2, 3])) == hash(Equable([1, 2, 3]))

    def test_different_elements(self):
        assert Equable([1, 2, 3]) != Equable([1, 2, 4])

    def test_order_matters(self):
        assert Equable([1, 2]) != Equable([2, 1])

    def test_equality_is_symmetric(self):
        # Equality must be symmetric -- every hash-based collection relies on it
        # -- so a shorter Equable matching a prefix of a longer one is not equal
        # to it, whichever way round the question is asked.
        shorter, longer = Equable([1, 2]), Equable([1, 2, 3])
        assert shorter != longer
        assert longer != shorter

    def test_a_comparable_equable_is_not_an_equable(self):
        # the Java's getClass() != o.getClass()
        assert Equable([1, 2]) != ComparableEquable([1, 2])

    def test_empty(self):
        assert Equable([]) == Equable([])


class TestComparableEquable:
    def test_equal_elements(self):
        assert ComparableEquable([1, 2, 3]).compare_to(ComparableEquable([1, 2, 3])) == 0

    def test_smaller_elements(self):
        assert ComparableEquable([1, 2, 2]).compare_to(ComparableEquable([1, 2, 3])) == -1

    def test_larger_elements(self):
        assert ComparableEquable([1, 2, 4]).compare_to(ComparableEquable([1, 2, 3])) == 1

    def test_the_first_element_that_differs_decides(self):
        assert ComparableEquable([1, 9, 9]).compare_to(ComparableEquable([2, 0, 0])) == -1

    def test_different_lengths_either_way_round(self):
        # the Java's rule only fired when THIS one was the longer, so the shorter
        # compared with the longer reported 0 -- equal -- where the reverse raised
        shorter, longer = ComparableEquable([1, 2]), ComparableEquable([1, 2, 3])
        with pytest.raises(ComparableEquableException):
            longer.compare_to(shorter)
        with pytest.raises(ComparableEquableException):
            shorter.compare_to(longer)

    def test_elements_that_cannot_be_ordered(self):
        with pytest.raises(ComparableEquableException):
            ComparableEquable([object()]).compare_to(ComparableEquable([object()]))

    def test_sorting(self):
        equables = [ComparableEquable([2, 1]), ComparableEquable([1, 2]), ComparableEquable([1, 1])]
        assert [e.elements for e in sorted(equables)] == [[1, 1], [1, 2], [2, 1]]


class TestTuple:
    def test_equality(self):
        # NOTE the Java's TupleTest asserts exact hash codes -- 340594883 for
        # Tuple(1, PI). Those are Java's, and nothing here should try to match them:
        # Python's hash of a tuple is a different function, and for str keys it is
        # salted per process. What must hold is the contract -- equal objects hash
        # alike -- so that is what is asserted.
        assert Tuple(1, math.pi) == Tuple(1, math.pi)
        assert Tuple(1, math.pi) != Tuple(2, math.e)
        assert hash(Tuple(1, math.pi)) == hash(Tuple(1, math.pi))
        assert hash(Tuple(1, math.pi)) != hash(Tuple(2, math.e))

    def test_str(self):
        assert str(Tuple(1, math.pi)) == "Tuple(1, 3.141592653589793)"

    def test_getters(self):
        assert (Tuple(1, 2.0).get_x(), Tuple(1, 2.0).get_y()) == (1, 2.0)

    def test_a_tuple_is_not_a_comparable_tuple(self):
        # different classes are never equal, even holding the same pair
        assert Tuple(1, 2.0) != ComparableTuple(1, 2.0)

    def test_it_is_a_base_equable(self):
        assert isinstance(Tuple(1, 2.0), BaseEquable)


class TestComparableTuple:
    def test_ordering(self):
        assert ComparableTuple(1, 2.0).compare_to(ComparableTuple(1, 2.0)) == 0
        assert ComparableTuple(1, 2.0).compare_to(ComparableTuple(1, 3.0)) == -1
        assert ComparableTuple(2, 0.0).compare_to(ComparableTuple(1, 9.0)) == 1

    def test_sorting(self):
        tuples = [ComparableTuple(2, 1.0), ComparableTuple(1, 2.0), ComparableTuple(1, 1.0)]
        assert [str(t) for t in sorted(tuples)] == [
            "Tuple(1, 1.0)", "Tuple(1, 2.0)", "Tuple(2, 1.0)"]

    def test_equality(self):
        assert ComparableTuple(1, 2.0) == ComparableTuple(1, 2.0)
        assert ComparableTuple(1, 2.0) != ComparableTuple(1, 3.0)


class TestIndex:
    def test_folds_a_hash_into_sixteen_bits(self):
        assert index(0x00000000) == 0
        assert index(0x0000ABCD) == 0xABCD
        assert index(0xABCD0000) == 0xABCD
        assert index(0xABCDABCD) == 0, "a value XOR itself is zero"
        assert index(0x12345678) == 0x1234 ^ 0x5678
