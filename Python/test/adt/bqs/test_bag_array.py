# Python/tests/adt/test_bag_array.py
from random import Random
from src.adt.bqs.bag_array import BagArray


def test_bag_add_basic():
    b = BagArray()
    assert len(b) == 0
    assert b.is_empty()
    assert next(iter(b), None) is None
    b.add(1)
    assert len(b) == 1
    assert not b.is_empty()
    assert next(iter(b)) == 1


def test_bag_add_with_seed():
    b = BagArray(rnd=Random(1))
    for i in range(33):
        b.add(i)
    first = next(iter(b))
    indices = list(range(len(b)))
    r = Random(1)
    r.shuffle(indices)
    expected_first = b.as_array()[indices[0]]
    assert first == expected_first


def test_bag_iterator_sum():
    b = BagArray()
    for i in range(1, 5):
        b.add(i)
    assert len(b) == 4
    assert sum(x for x in b) == 10


def test_as_array_copy():
    b = BagArray()
    for i in range(1, 5):
        b.add(i)
    arr = b.as_array()
    assert isinstance(arr, list)
    assert sum(arr) == 10
    arr.append(99)
    assert len(b) == 4


def test_clear():
    b = BagArray()
    for i in range(10):
        b.add(i)
    assert len(b) == 10
    b.clear()
    assert b.is_empty()


def test_contains_and_multiplicity():
    b = BagArray()
    for i in range(10):
        b.add(i)
    for i in range(0, 10, 2):
        b.add(i)
    assert 0 in b
    assert 9 in b
    assert 10 not in b
    assert b.contains(0)
    assert b.multiplicity(0) == 2
    assert b.multiplicity(9) == 1
    assert b.multiplicity(10) == 0


def test_repr():
    b = BagArray()
    for i in range(10):
        b.add(i)
    s = repr(b)
    assert "BagArray(items=" in s
    assert "count=10" in s
