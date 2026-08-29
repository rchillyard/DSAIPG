"""
Tests for misc/coupling, which show the difference the coupling makes.
"""

from __future__ import annotations

from src.misc.call_by_value import CallByValue
from src.misc.coupling.coupling import CouplingNone, CouplingTight


class TestCouplingNone:
    def test_each_holds_its_own(self):
        a, b = CouplingNone.A(0), CouplingNone.B(1)
        assert (a.get_a(), b.get_b()) == (0, 1)

    def test_two_of_a_kind_are_independent(self):
        assert (CouplingNone.A(0).get_a(), CouplingNone.A(5).get_a()) == (0, 5)


class TestCouplingTight:
    def test_it_works_when_nothing_else_intervenes(self):
        outer = CouplingTight()
        a, b = CouplingTight.A(outer, 0), CouplingTight.B(outer, 1)
        assert (a.get_a(), b.get_b()) == (0, 1)

    def test_but_a_second_a_changes_the_first(self):
        # this is the cost of the coupling: A does not own its value, so making
        # another A silently changes what the first one reports
        outer = CouplingTight()
        first = CouplingTight.A(outer, 0)
        CouplingTight.A(outer, 99)
        assert first.get_a() == 99, "the first A now reports the second A's value"

    def test_and_the_enclosing_object_can_change_it_too(self):
        outer = CouplingTight()
        a = CouplingTight.A(outer, 0)
        outer.a = 42
        assert a.get_a() == 42


class TestCallByValue:
    """
    Which of these can the caller see? The same question the Java asks, and the
    same answers: rebinding a name is invisible, mutating an object is not.
    """

    def test_rebinding_a_parameter_is_invisible(self):
        assert CallByValue.increment_number1(0) == 1, "the return value carries it"
        n = 0
        CallByValue.increment_number1(n)
        assert n == 0, "but the caller's own name is untouched"

    def test_incrementing_a_field_is_visible(self):
        c = CallByValue()
        assert c.increment_number2() == 1
        assert c.number == 1

    def test_mutating_what_a_parameter_refers_to_is_visible(self):
        xs = [0]
        CallByValue.increment_array1(xs)
        assert xs == [1], "the caller sees this"

    def test_rebinding_a_parameter_to_a_new_list_is_not(self):
        xs = [0]
        assert CallByValue.increment_array2(xs) == [1]
        assert xs == [0], "the caller's list is untouched"

    def test_mutating_a_field_list(self):
        c = CallByValue()
        kept = c.array
        c.increment_array3()
        assert c.array == [1]
        assert kept is c.array, "the same list, changed"

    def test_rebinding_a_field_list(self):
        c = CallByValue()
        kept = c.array
        c.increment_array4()
        assert c.array == [1]
        assert kept is not c.array, "a different list; whoever kept the old one still has it"
        assert kept == [0]
