import pytest

from src.adt.array.ordered_array import OrderedArray


def test_constructor0():
    integers = [3, 1, 4, 2, 0]
    target = OrderedArray(integers, make_copy=False)
    # Original array IS modified because make_copy=False
    assert integers[0] == 0
    assert target.get(0) == 0

def test_constructor_copy():
    integers = [3, 1, 4, 2, 0]
    target = OrderedArray(integers) # Default make_copy=True
    # Original array is NOT modified
    assert integers[0] == 3
    assert target.get(0) == 0

def test_constructor1():
    integers = [3, 1, 4, 2, 0]
    target = OrderedArray(integers)
    assert target.get(0) == 0

def test_constructor2():
    lst = [1, 0]
    target = OrderedArray(lst)
    assert target.get(0) == 0

def test_add_elements():
    ordered_array = OrderedArray.from_values(3, 1, 4)
    ordered_array.add_elements([2, 0])
    iterator = iter(ordered_array)
    assert next(iterator) == 0
    # Verify full order
    assert list(ordered_array) == [0, 1, 2, 3, 4]

def test_iterator():
    ordered_array = OrderedArray.from_values(3, 1, 4, 2, 0)
    iterator = iter(ordered_array)
    assert next(iterator) == 0
    assert list(ordered_array) == [0, 1, 2, 3, 4]

def test_index_of():
    ordered_array = OrderedArray.from_values(3, 1, 4, 2, 0)
    assert ordered_array.index_of(4) == 4
    assert ordered_array.index_of(-2) == -1
    # Test missing element that would be inserted in middle
    assert ordered_array.index_of(1.5) == -1

def test_pythonic_methods():
    ordered_array = OrderedArray.from_values(3, 1, 4, 2, 0)
    
    # __getitem__
    assert ordered_array[0] == 0
    assert ordered_array[4] == 4
    
    # __contains__
    assert 3 in ordered_array
    assert 10 not in ordered_array
    
    # index (raises ValueError)
    assert ordered_array.index(4) == 4
    with pytest.raises(ValueError):
        ordered_array.index(10)
        
    # extend
    ordered_array.extend([5, -1])
    assert list(ordered_array) == [-1, 0, 1, 2, 3, 4, 5]
    assert len(ordered_array) == 7
