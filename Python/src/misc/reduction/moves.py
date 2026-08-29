"""
Ported from misc/reduction/Moves.java, Moves1.java, Moves2.java and Moves3.java.

The puzzle: starting from a point, a move replaces (x, y) with either (x, x + y) or
(x + y, y). Given a start and a target, can the target be reached?

Three answers to the same question, which is why they belong together. Moves1
searches forwards depth-first and Moves2 forwards breadth-first, both of which
explore a tree that grows exponentially. Moves3 runs the moves BACKWARDS from the
target, where each step is forced -- there is only one way to have arrived -- so
the search is a straight line and the whole thing collapses to arithmetic. That
reduction is the point of the package.
"""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections import deque

from .point import Point


class Moves(ABC):
    """
    Something that can say whether a point is reachable, and make a move.
    """

    @abstractmethod
    def valid(self, p: Point) -> bool:
        """
        :param p: the point to test.
        :return: whether it is reachable.
        """

    @abstractmethod
    def move(self, p: Point, which: bool) -> Point | None:
        """
        :param p: the point to move from.
        :param which: which of the two moves to make.
        :return: where the move lands.
        """


class Moves1(Moves):
    """
    Forwards, depth first: from p, try both moves and see if either reaches the
    target. Every point beyond the target is a dead end, which is the only thing
    keeping the search finite.
    """

    def __init__(self, tx: int, ty: int) -> None:
        """
        :param tx: the target's x coordinate.
        :param ty: the target's y coordinate.
        """
        self.tx = tx
        self.ty = ty

    def valid(self, p: Point) -> bool:
        """
        :param p: the point to start from.
        :return: whether the target can be reached from it.
        """
        if p.x == self.tx and p.y == self.ty:
            return True
        if p.x > self.tx or p.y > self.ty:
            return False
        return self.valid(Point(p.x, p.x + p.y)) or self.valid(Point(p.x + p.y, p.y))

    def valid_xy(self, x: int, y: int) -> bool:
        """
        :param x: the starting x coordinate.
        :param y: the starting y coordinate.
        :return: whether the target can be reached.
        """
        return self.valid(Point(x, y))

    def move(self, p: Point, which: bool) -> Point | None:
        """
        NOTE returns None, as the Java does. Moves1 implements the interface but
        searches by recursion rather than by asking for a move, so this is never
        called.

        :param p: ignored.
        :param which: ignored.
        :return: None.
        """
        return None


class Moves2:
    """
    Forwards, breadth first: the same search with a queue instead of the call
    stack. It finds the same answers and does the same amount of work.

    NOTE the Java does not implement Moves, though it has both of its methods --
    presumably an oversight, since Moves1 and Moves3 do. Left as it is.
    """

    def __init__(self, t: Point) -> None:
        """
        :param t: the target.
        """
        self.t = t

    @staticmethod
    def of(x: int, y: int) -> Moves2:
        """
        NOTE named separately because Python cannot overload; the Java has this as
        a second constructor.

        :param x: the target's x coordinate.
        :param y: the target's y coordinate.
        :return: a Moves2 aiming at that point.
        """
        return Moves2(Point(x, y))

    def move(self, p: Point, which: bool) -> Point:
        """
        :param p: the point to move from.
        :param which: True to grow y, False to grow x.
        :return: where the move lands.
        """
        return Point(p.x, p.y + p.x) if which else Point(p.x + p.y, p.y)

    def valid(self, x: int, y: int) -> bool:
        """
        :param x: the starting x coordinate.
        :param y: the starting y coordinate.
        :return: whether the target can be reached.
        """
        points: deque[Point] = deque([Point(x, y)])
        return self._inner(points, False)

    def _inner(self, points: deque[Point], result: bool) -> bool:
        """
        NOTE a loop where the Java recurses. The Java's recursion is in tail
        position, which Java does not eliminate and neither does Python -- but the
        Java gets away with it because the JVM's stack is deeper than the search,
        while Python's default limit of 1000 is not. See ``TailCall`` for the
        general way round this.

        :param points: the points still to consider.
        :param result: the answer so far.
        :return: whether the target was reached.
        """
        while points:
            x = points.popleft()
            if x == self.t:
                return True
            if x.x > self.t.x or x.y > self.t.y:
                result = False
                continue
            points.append(self.move(x, True))
            points.append(self.move(x, False))
        return result


class Moves3(Moves):
    """
    Backwards, from the target towards the start. Each move forwards adds the
    smaller coordinate to the larger, so going back subtracts -- and there is only
    ever one way to have arrived, because the other would need a negative
    coordinate. So there is nothing to search: it is the subtractive Euclidean
    algorithm, and the answer falls out of a remainder.
    """

    def __init__(self, s: Point) -> None:
        """
        :param s: the point the walk starts from.
        """
        self.s = s

    @staticmethod
    def of(x: int, y: int) -> Moves3:
        """
        NOTE named separately because Python cannot overload; the Java has this as
        a second constructor.

        :param x: the start's x coordinate.
        :param y: the start's y coordinate.
        :return: a Moves3 starting from that point.
        """
        return Moves3(Point(x, y))

    def move(self, p: Point, which: bool) -> Point:
        """
        One move backwards: take the smaller coordinate away from the larger.

        :param p: the point to move back from.
        :param which: ignored -- going backwards there is only one choice.
        :return: where the move came from.
        """
        return Point(p.x, p.y - p.x) if p.y > p.x else Point(p.x - p.y, p.y)

    def valid(self, t: Point) -> bool:
        """
        :param t: the target to test.
        :return: whether it can be reached from the start.
        """
        p = t  # noqa: F841  scaffolding for the exercise, as in Moves3.java
        while True:
            # TO BE IMPLEMENTED  Sorry, but you have to do this one yourself!
            raise NotImplementedError("TO BE IMPLEMENTED")

    def valid_xy(self, x: int, y: int) -> bool:
        """
        :param x: the target's x coordinate.
        :param y: the target's y coordinate.
        :return: whether it can be reached from the start.
        """
        return self.valid(Point(x, y))
