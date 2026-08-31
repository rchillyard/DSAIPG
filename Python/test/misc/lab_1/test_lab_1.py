"""
Tests for misc/lab_1.
"""

from __future__ import annotations

import random

import pytest

from src.misc.lab_1.my_tree import MyTree, Node
from src.misc.lab_1.wheel_of_fortune import Event, WheelOfFortune, value_of


class TestWheelOfFortune:
    def test_a_certainty(self):
        wheel = WheelOfFortune(value_of("only", 1))
        assert wheel.get() == "only"

    def test_the_total_is_the_circumference(self):
        wheel = WheelOfFortune(value_of("a", 3), value_of("b", 7))
        assert wheel.total == 10

    def test_frequencies_are_honoured(self):
        # 'a' three times as often as 'b', within sampling error over 6000 spins
        wheel = WheelOfFortune.seeded(0, value_of("a", 3), value_of("b", 1))
        counts = {"a": 0, "b": 0}
        for _ in range(6000):
            counts[wheel.get()] += 1
        assert counts["a"] + counts["b"] == 6000
        assert counts["a"] / counts["b"] == pytest.approx(3.0, abs=0.2)

    def test_every_outcome_comes_up(self):
        wheel = WheelOfFortune.seeded(1, *(value_of(c, 1) for c in "abcde"))
        assert {wheel.get() for _ in range(500)} == set("abcde")

    def test_an_outcome_of_zero_frequency_never_comes_up(self):
        wheel = WheelOfFortune.seeded(2, value_of("never", 0), value_of("always", 1))
        assert {wheel.get() for _ in range(100)} == {"always"}

    def test_a_seeded_wheel_repeats(self):
        first = WheelOfFortune.seeded(42, value_of("a", 1), value_of("b", 1))
        second = WheelOfFortune.seeded(42, value_of("a", 1), value_of("b", 1))
        assert [first.get() for _ in range(20)] == [second.get() for _ in range(20)]

    def test_an_explicit_random(self):
        wheel = WheelOfFortune(value_of("a", 1), value_of("b", 1), random=random.Random(7))
        assert wheel.get() in ("a", "b")

    def test_event_equality(self):
        assert value_of("a", 1) == Event("a", 1)
        assert value_of("a", 1) != value_of("a", 2)


class TestMyTree:
    """
    NOTE these skip until add_child and replace are written, like any other
    exercise. That was not always the reason: the Java had no reference answer
    either, both SOLUTION blocks reading "return null", so there was nothing to
    port. Written on 2026-08-31, along with equals and hashCode, so the two trees
    now agree that a Node is a value.
    """

    def test_a_root_on_its_own(self):
        tree = MyTree(1)
        assert tree.get_root().x == 1
        assert tree.get_root().children == ()

    def test_a_root_from_a_node(self):
        assert MyTree(Node(1)).get_root().x == 1

    def test_adding_a_child(self):
        tree = MyTree(1).add_child(Node(2))
        children = tree.get_root().children
        assert len(children) == 1
        assert children[0].x == 2

    def test_a_node_is_immutable(self):
        # adding a child returns a new node and leaves this one alone, which is
        # what the exercise is about
        root = Node(1)
        grown = root.add_child(Node(2))
        assert root.children == ()
        assert len(grown.children) == 1

    def test_adding_a_child_by_value(self):
        assert Node(1).add_child_value(2).children[0].x == 2

    def test_replace(self):
        two, three = Node(2), Node(3)
        root = Node(1).add_child(two)
        assert root.replace(two, three).children == (three,)

    def test_replace_matches_by_value_not_by_identity(self):
        root = Node(1).add_child(Node(2))
        assert root.replace(Node(2), Node(9)).children[0].x == 9

    def test_replace_leaves_a_node_with_no_such_child_alone(self):
        root = Node(1).add_child(Node(2))
        assert root.replace(Node(7), Node(9)) == root

    def test_replace_changes_only_the_first_of_several_equal_children(self):
        root = Node(1).add_child(Node(2)).add_child(Node(2))
        replaced = root.replace(Node(2), Node(9))
        assert replaced.children[0].x == 9
        assert replaced.children[1].x == 2

    def test_replace_keeps_the_position(self):
        root = Node(1).add_child(Node(2)).add_child(Node(3)).add_child(Node(4))
        replaced = root.replace(Node(2), Node(9))
        assert [c.x for c in replaced.children] == [9, 3, 4]

    def test_replace_by_value(self):
        two = Node(2)
        root = Node(1).add_child(two)
        assert root.replace_value(two, 3).children[0].x == 3
