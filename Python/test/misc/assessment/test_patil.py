"""
Tests for misc/assessment/patil, which record what it does rather than what it
was meant to do. See the module docstring for why it is left that way.
"""

from __future__ import annotations

from src.misc.assessment.patil import get_hash_index, new_table, put


class TestPatil:
    def test_the_hash_is_the_remainder(self):
        table = new_table(10)
        assert get_hash_index(0, table) == 0
        assert get_hash_index(7, table) == 7
        assert get_hash_index(17, table) == 7

    def test_it_stores_the_value_twice(self):
        # the first if writes x into its home slot; the loop then starts at that
        # same slot, finds it occupied -- by x, just now -- and writes x again
        table = new_table(10)
        put(3, table)
        assert table[3] == 3
        assert table[4] == 3, "and again in the next slot"
        assert [i for i, v in enumerate(table) if v is not None] == [3, 4]

    def test_the_probe_never_wraps(self):
        # a value hashing to the last slot, with that slot taken, is dropped
        table = new_table(10)
        table[9] = 99
        put(9, table)
        assert table.count(9) == 0, "silently not stored"
        assert table[9] == 99

    def test_a_collision_lands_next_door(self):
        table = new_table(10)
        table[3] = 33
        put(3, table)
        assert table[4] == 3, "stored once here, since the home slot was taken"
        assert table.count(3) == 1
