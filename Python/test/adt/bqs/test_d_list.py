from src.adt.bqs.d_list import DList

# These tests mirror DListTest.java.  They assert what a *correct* DList does,
# so all but the first fail until the `TO BE IMPLEMENTED` methods
# (add_before_element, add_after_element, remove_element, find_first,
# find_last) have been written.  That is the intended state of the exercise.
#
# One deliberate divergence from Java: DList.__str__ joins with ", " and so
# yields "1, 2", where Java's toString() appends a separator after every item
# and yields "1, 2, ".


class TestDList:
    def test_empty(self):
        d = DList[int]()
        assert d.is_empty()
        assert d.size() == 0
        assert list(d) == []

    def test_add_before_element_to_empty(self):
        d = DList[int]()
        d.add_before_element(1, None)
        assert not d.is_empty()
        assert d.size() == 1
        assert list(d) == [1]

    def test_init_with_item(self):
        d = DList[int](1)
        assert not d.is_empty()
        assert d.size() == 1
        assert list(d) == [1]

    def test_find_first_then_remove_element(self):
        d = DList[int]()
        d.add_before_element(1, None)
        assert not d.is_empty()
        assert str(d) == "1"
        first = d.find_first(1)
        assert first.item == 1
        d.remove_element(first)
        assert d.size() == 0

    def test_remove_by_item(self):
        d = DList[int](1)
        d.remove(1)
        assert d.is_empty()

    def test_add_after_element_found_by_find_first(self):
        d = DList[int]()
        d.add_before_element(1, None)
        first = d.find_first(1)
        assert first.item == 1
        d.add_after_element(2, first)
        assert str(d) == "1, 2"
        assert d.size() == 2

    def test_add_after_element_found_by_find_last(self):
        d = DList[int]()
        d.add_before_element(1, None)
        last = d.find_last(1)
        assert last.item == 1
        d.add_after_element(2, last)
        assert str(d) == "1, 2"
        assert d.size() == 2

    def test_add_after_by_item(self):
        d = DList[int](1)
        d.add_after(2, 1)
        assert str(d) == "1, 2"
        assert d.size() == 2
