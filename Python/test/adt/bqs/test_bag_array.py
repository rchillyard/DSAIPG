from random import Random

from src.adt.bqs.bag_array import INITIAL_CAPACITY, BagArray

# These tests mirror BagTest.java.
#
# `_grow_from` is `TO BE IMPLEMENTED`, so the tests below which push a bag past
# its initial capacity are reported as skipped until that exercise is written.
# The rest run, because neither the constructor nor `of` goes through it -- they
# allocate their storage directly. That is deliberate: the constructor used to
# grow from empty to 32, which meant a bag could not be built at all until the
# exercise was done, and 22 tests of graphs and classification sorts were held
# hostage to it. Only genuine growth depends on it now, and the growth tests
# below are what guard it.
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


def test_growth_doubles_the_capacity_rather_than_adding_one():
    # Doubling is what makes the amortized cost of an add O(1) rather than O(n).
    #
    # NOTE what this does and does not guard. The factor is chosen by `add`, which
    # calls `_grow(self._items, 2 * self._capacity())`, and add is not part of the
    # exercise -- so a student cannot get the doubling wrong. What it does catch is
    # _grow_from returning a list of the wrong length, and a later change to add's
    # growth policy. Growing by a constant would pass every other test here, so
    # the policy is worth pinning down somewhere.
    b = BagArray()
    for i in range(INITIAL_CAPACITY):
        b.add(i)
    assert b._capacity() == INITIAL_CAPACITY, "no growth until it is actually full"
    b.add(99)
    assert b._capacity() == 2 * INITIAL_CAPACITY


def test_grow_from_honours_the_requested_size():
    # A direct test of the exercise's contract rather than of its use, and it
    # earns its place: `add` only ever calls `_grow(items, 2 * capacity())`, where
    # source is the whole backing list, so an implementation returning
    # `[None] * (len(source) * 2)` is accidentally right at every call site and
    # passes every other test in this file -- while ignoring the size it was
    # given. Only calling it directly can tell the difference.
    assert BagArray._grow_from([1, 2], 5) == [1, 2, None, None, None]
    assert BagArray._grow_from([], 3) == [None, None, None]
    assert BagArray._grow_from([1, 2, 3], 3) == [1, 2, 3]


def test_growth_preserves_the_items_and_their_order():
    # The bag iterates in random order, but the backing store must not be
    # scrambled by a growth: as_array reads the first _count slots, so a copy
    # which lost or reordered anything would show up here.
    b = BagArray()
    expected = list(range(INITIAL_CAPACITY + 1))
    for i in expected:
        b.add(i)
    assert b.as_array() == expected


def test_growth_leaves_the_new_slots_empty():
    # The unused tail must be None, not stale references, so that nothing the bag
    # no longer holds is kept alive by it. (contains and multiplicity now stop at
    # _count, so they no longer see the tail either way -- which is the fix for
    # the clear() staleness below.)
    b = BagArray()
    for i in range(INITIAL_CAPACITY + 1):
        b.add(i)
    assert b._items is not None
    assert all(slot is None for slot in b._items[len(b):])


def test_repeated_growth():
    # Several doublings in a row, not just the first.
    b = BagArray()
    n = INITIAL_CAPACITY * 8 + 1
    for i in range(n):
        b.add(i)
    assert len(b) == n
    assert b.as_array() == list(range(n))
    assert b._capacity() >= n


def test_growth_after_clear():
    # clear() only resets the count, so the capacity stays where growth left it.
    b = BagArray()
    for i in range(INITIAL_CAPACITY + 1):
        b.add(i)
    grown = b._capacity()
    b.clear()
    assert b.is_empty()
    assert b._capacity() == grown
    b.add(1)
    assert b.as_array() == [1]


class TestOf:
    """
    The varargs constructor. It allocates its storage directly, so unlike add it
    never needs _grow_from -- which is the point of it.
    """

    def test_it_holds_the_items_given(self):
        b = BagArray.of(1, 2, 3)
        assert len(b) == 3
        assert b.as_array() == [1, 2, 3]

    def test_no_items(self):
        b = BagArray.of()
        assert b.is_empty()
        assert b.as_array() == []

    def test_it_leaves_room_to_grow(self):
        # Sizing exactly would leave the bag full, so the very next add would
        # need _grow_from and the dependency would be back.
        b = BagArray.of(1, 2, 3)
        assert b._capacity() > len(b)
        b.add(4)
        assert b.as_array() == [1, 2, 3, 4]

    def test_more_items_than_the_initial_capacity(self):
        items = list(range(INITIAL_CAPACITY * 2))
        b = BagArray.of(*items)
        assert len(b) == len(items)
        assert b.as_array() == items
        assert b._capacity() > len(items)

    def test_duplicates_are_kept(self):
        # A bag, not a set.
        b = BagArray.of(1, 1, 2)
        assert len(b) == 3
        assert b.multiplicity(1) == 2

    def test_it_accepts_a_random_source(self):
        b = BagArray.of(1, 2, 3, rnd=Random(1))
        assert sorted(b) == [1, 2, 3]

    def test_the_unused_tail_is_empty(self):
        b = BagArray.of(1, 2, 3)
        assert b._items is not None
        assert all(slot is None for slot in b._items[3:])


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


def test_clear_really_forgets_the_items():
    # clear() only resets the count, so contains used to scan past it and still
    # answer True for items the bag no longer held -- while multiplicity answered
    # zero, because it happened to have an is_empty() guard that contains lacked.
    b = BagArray()
    b.add("x")
    b.add("y")
    b.clear()
    assert b.is_empty()
    assert b.as_array() == []
    assert not b.contains("x")
    assert b.multiplicity("x") == 0


def test_contains_and_multiplicity_agree_after_clear():
    # The two must answer the same question the same way.
    b = BagArray()
    for item in ["x", "y", "x"]:
        b.add(item)
    b.clear()
    for item in ["x", "y"]:
        assert b.contains(item) == (b.multiplicity(item) > 0)


def test_a_stale_slot_is_not_visible_after_reuse():
    # The staleness was position-dependent, which would have made a bug report
    # baffling: adding one item overwrote slot 0, so the old first element
    # stopped being found while the second was still there.
    b = BagArray()
    b.add("x")
    b.add("y")
    b.clear()
    b.add("z")
    assert b.as_array() == ["z"]
    assert not b.contains("x")
    assert not b.contains("y")


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
