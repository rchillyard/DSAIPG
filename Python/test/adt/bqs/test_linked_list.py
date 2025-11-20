import pytest
from src.adt.bqs.linked_list_elements import LinkedListElements
from src.adt.bqs.bqs_exception import BQSException


class TestLinkedListElements:
    def test_add_single_element(self):
        ll = LinkedListElements[str]()
        ll.add("Test1")
        assert not ll.is_empty()
        assert ll.get_head() == "Test1"

    def test_add_multiple_elements(self):
        ll = LinkedListElements[int]()
        ll.add(1)
        ll.add(2)
        ll.add(3)
        assert not ll.is_empty()
        assert ll.get_head() == 3

    def test_add_null_element(self):
        ll = LinkedListElements[str]()
        ll.add(None)
        assert not ll.is_empty()
        assert ll.get_head() is None

    def test_add_to_empty_list(self):
        ll = LinkedListElements[str]()
        ll.add("First")
        ll.add("Second")
        assert ll.get_head() == "Second"
        ll.add("Third")
        assert ll.get_head() == "Third"

    def test_order_preservation_after_add(self):
        ll = LinkedListElements[int]()
        ll.add(10)
        ll.add(20)
        ll.add(30)
        assert ll.get_head() == 30
        ll.remove()
        assert ll.get_head() == 20
        ll.remove()
        assert ll.get_head() == 10

    def test_rapid_additions_update_head(self):
        ll = LinkedListElements[str]()
        ll.add("Head1")
        ll.add("Head2")
        ll.add("Head3")
        assert ll.get_head() == "Head3"

    def test_equals_and_hash_code(self):
        list1 = LinkedListElements[int]()
        list1.add(10)
        list1.add(20)
        list2 = LinkedListElements[int]()
        list2.add(10)
        
        # Hash codes might differ in Python due to object identity if not explicitly handled for content
        # But our implementation uses head hash, so let's check equality
        assert list1 != list2
        
        list1.remove()
        # Now both have [10]
        assert list1 == list2
        assert hash(list1) == hash(list2)
        
        list1.remove()
        assert list1 != list2
