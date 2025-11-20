import pytest
from src.adt.bqs.element import Element


class TestElement:
    def test_equals_same_object(self):
        element = Element(1)
        assert element == element

    def test_equals_different_type(self):
        element = Element(1)
        assert element != "Test"

    def test_equals_same_value_no_next(self):
        element1 = Element(1)
        element2 = Element(1)
        assert element1 == element2

    def test_equals_different_value_no_next(self):
        element1 = Element(1)
        element2 = Element(2)
        assert element1 != element2

    def test_equals_same_value_with_next(self):
        next1 = Element(2)
        element1 = Element(1, next1)
        next2 = Element(2)
        element2 = Element(1, next2)
        assert element1 == element2

    def test_equals_same_value_different_next(self):
        next1 = Element(2)
        element1 = Element(1, next1)
        next2 = Element(3)
        element2 = Element(1, next2)
        assert element1 != element2

    def test_equals_null_next(self):
        element1 = Element(1, None)
        element2 = Element(1)
        assert element1 == element2

    def test_str(self):
        element = Element(1)
        assert str(element) == "1 (last)"
        element2 = Element(1, Element(2))
        assert str(element2) == "1"
