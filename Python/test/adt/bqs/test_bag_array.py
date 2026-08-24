from random import Random

from src.adt.bqs.bag_array import BagArray

# These tests mirror BagTest.java. BagArray grows its backing store via
# `_grow_from`, which is `TO BE IMPLEMENTED`, and the constructor grows from
# empty to a capacity of 32 -- exactly as Bag_Array's constructor does. So every
# test here fails until that exercise is done, which is also true of BagTest in
# the Java tree.
#
# One deliberate divergence: BagTest.testBagAdd2 asserts that a Random seeded
# with 1 yields 15 as the first item. That value is specific to Java's RNG, so
# the equivalent test below asserts only that iteration yields a permutation.


def test_bag_add_basic():
    b = BagArray()
    assert len(b) == 0
    assert b.is_empty()
    assert next(iter(b), None) is None
    b.add(1)
    assert len(b) == 1
    assert not b.is_empty()
    assert next(iter(b)) == 1


def test_bag_grows_past_initial_capacity():
    # 33 items forces one grow, from the initial capacity of 32 to 64.
    b = BagArray(rnd=Random(1))
    assert b.is_empty()
    for i in range(33):
        b.add(i)
    assert len(b) == 33
    assert not b.is_empty()
    assert sorted(b) == list(range(33))
    assert b.as_array() == list(range(33))


def test_bag_iterator_is_a_permutation():
    b = BagArray(rnd=Random(1))
    for i in range(10):
        b.add(i)
    seen = list(b)
    assert len(seen) == 10
    assert sorted(seen) == list(range(10))


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
    # as_array returns a copy, so mutating it must not affect the bag.
    arr.append(99)
    assert len(b) == 4


def test_as_array_excludes_unused_capacity():
    b = BagArray()
    b.add(1)
    # The backing store holds 32 slots, but only the one item is exposed.
    assert b.as_array() == [1]


def test_clear():
    b = BagArray()
    for i in range(10):
        b.add(i)
    assert len(b) == 10
    b.clear()
    assert b.is_empty()
    assert b.as_array() == []


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


def test_multiplicity_of_empty_bag():
    assert BagArray().multiplicity(0) == 0


def test_repr():
    b = BagArray()
    for i in range(10):
        b.add(i)
    s = repr(b)
    assert "BagArray(items=" in s
    assert "count=10" in s
