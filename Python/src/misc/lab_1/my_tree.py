"""
Ported from misc/lab_1/MyTree.java.

NOTE Node is a frozen dataclass, so it already compares by value, and replace
finds a child the caller has described rather than one the caller already holds --
which is what the Java now does too, having gained equals and hashCode. Comparing
or hashing a Node descends through its children, so both are O(n) in the size of
the subtree: fine for comparing trees, but not for a loop over a large one.
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Generic, TypeVar

X = TypeVar("X")


@dataclass(frozen=True)
class Node(Generic[X]):
    """
    A node of a general tree: a value, and any number of children.

    Immutable, so adding a child does not change this node -- it returns a new one.
    That is the whole shape of the exercise.
    """

    x: X
    children: tuple[Node[X], ...] = field(default_factory=tuple)

    def add_child(self, y: Node[X]) -> Node[X]:
        """
        :param y: the child to add.
        :return: a new Node with that child added, this one being unchanged.
        """
        # TO BE IMPLEMENTED  what should go here?
        raise NotImplementedError("TO BE IMPLEMENTED")

    def add_child_value(self, xx: X) -> Node[X]:
        """
        NOTE named separately because Python cannot overload; the Java has this as
        a second addChild.

        :param xx: the value of the child to add.
        :return: a new Node with that child added.
        """
        return self.add_child(Node(xx))

    def replace(self, y: Node[X], z: Node[X]) -> Node[X]:
        """
        Children are compared by value, so y need not be the very Node held here.
        Where several children are equal to y, only the first is replaced, and z
        takes that child's position rather than being appended. A Node with no
        child equal to y is returned unchanged.

        :param y: the child to remove.
        :param z: the child to put in its place.
        :return: a new Node with the replacement made.
        """
        # TO BE IMPLEMENTED  what should go here?
        raise NotImplementedError("TO BE IMPLEMENTED")

    def replace_value(self, y: Node[X], z: X) -> Node[X]:
        """
        NOTE named separately because Python cannot overload; the Java has this as
        a second replace.

        :param y: the child to remove.
        :param z: the value of the child to put in its place.
        :return: a new Node with the replacement made.
        """
        return self.replace(y, Node(z))


class MyTree(Generic[X]):
    """
    A general tree, which is to say a root Node.
    """

    def __init__(self, root: Node[X] | X) -> None:
        """
        :param root: the root node, or the value to make one from.
        """
        self.root: Node[X] = root if isinstance(root, Node) else Node(root)

    def get_root(self) -> Node[X]:
        """
        :return: the root node.
        """
        return self.root

    def add_child(self, y: Node[X]) -> MyTree[X]:
        """
        :param y: the child to add to the root.
        :return: a new tree, this one being unchanged.
        """
        return MyTree(self.root.add_child(y))
