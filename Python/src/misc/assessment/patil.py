"""
Ported from misc/assessment/Patil.java.

A hash table with linear probing -- or meant to be. Ported FAITHFULLY, including
two faults, because this sits in ``assessment`` and is named after a person: it
reads as submitted work kept as a case study, and quietly correcting it would
destroy whatever it was kept for. Nothing calls it. The Java's main is empty and
there are no tests in either tree.

The faults, both asserted by the tests so that they cannot be mistaken for the
port's own:

1. It stores the value TWICE. The first ``if`` writes x into its home slot, and
   then the loop starts at that same slot, finds it occupied -- by x, just now --
   and writes x again into the next free one.
2. The probe never wraps. It runs from the home slot to the end of the table only,
   so a value hashing near the end with nothing free after it is silently dropped.

A correct linear-probing put is in ``adt/symbol_table/hashtable``; this is not it.
"""

from __future__ import annotations

#: How many slots the table has.
CAPACITY = 5000


def new_table(capacity: int = CAPACITY) -> list[int | None]:
    """
    :param capacity: how many slots the table should have.
    :return: an empty table.
    """
    return [None] * capacity


def get_hash_index(x: int, table: list[int | None]) -> int:
    """
    :param x: the value to place.
    :param table: the table to place it in.
    :return: the slot it belongs in.
    """
    return x % len(table)


def put(x: int, table: list[int | None]) -> None:
    """
    Place a value in the table. See the module docstring: this does not do what it
    looks like it does.

    :param x: the value to place.
    :param table: the table to place it in.
    """
    hash_index = get_hash_index(x, table)
    if table[hash_index] is None:
        table[hash_index] = x
    for i in range(hash_index, len(table)):
        if table[i] is None:
            table[i] = x
            return
