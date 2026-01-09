from src.adt.bqs.stack_linked_list import StackLinkedList


class TestStackLinkedList:
    def test_push_single_item(self):
        stack = StackLinkedList[int]()
        stack.push(1)
        assert not stack.is_empty()
        assert stack.peek() == 1

    def test_push_multiple_items(self):
        stack = StackLinkedList[str]()
        stack.push("First")
        stack.push("Second")
        stack.push("Third")
        assert not stack.is_empty()
        assert stack.peek() == "Third"

    def test_push_null_item(self):
        stack = StackLinkedList[object]()
        stack.push(None)
        assert not stack.is_empty()
        assert stack.peek() is None

    def test_push_and_pop(self):
        stack = StackLinkedList[float]()
        stack.push(1.5)
        stack.push(2.5)
        assert not stack.is_empty()
        assert stack.pop() == 2.5
        assert stack.pop() == 1.5
        assert stack.is_empty()

    def test_push_large_number_of_elements(self):
        stack = StackLinkedList[int]()
        size = 1000
        for i in range(1, size + 1):
            stack.push(i)
        assert not stack.is_empty()
        assert stack.peek() == size
