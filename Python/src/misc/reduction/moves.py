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
        self.t = Point(tx, ty)

    def valid(self, p: Point) -> bool:
        """
        NOTE the in-bounds test is the base case for FAILURE, and it is the only
        reason this terminates. Without it the search walks q1, q1, q1, ... for
        ever, never reaching its second recursive call, and can only ever return
        True -- and then only if the target happens to lie on that one path.

        :param p: the point to start from.
        :return: whether the target can be reached from it.
        """
        return self.in_bounds(p) and (
            p == self.t or self.valid(self.move(p, True)) or self.valid(self.move(p, False))
        )

    def in_bounds(self, p: Point) -> bool:
        """
        :param p: a point.
        :return: whether neither coordinate has passed the target's. Both only ever
                 grow, so once either has, that path is dead.
        """
        return p.x <= self.t.x and p.y <= self.t.y

    def valid_xy(self, x: int, y: int) -> bool:
        """
        :param x: the starting x coordinate.
        :param y: the starting y coordinate.
        :return: whether the target can be reached.
        """
        return self.valid(Point(x, y))

    def move(self, p: Point, which: bool) -> Point:
        """
        :param p: the point to move from.
        :param which: True to grow y, False to grow x.
        :return: where that move lands.
        """
        return Point(p.x, p.x + p.y) if which else Point(p.x + p.y, p.y)


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
        Take the next point off the queue, and recurse.

        NOTE it recurses, as the Java does and as the case study writes it. That is
        the point: moving the work still to be done onto a queue does not by itself
        make an algorithm an iteration, and the stack still runs out. The depth is
        the number of points EXAMINED, not the length of the path found -- the queue
        holds the outstanding work and the stack then holds it a second time.

        Python's ceiling is far lower than Java's. The Java answers the case study's
        fifth condition, 1,1 to 99,100, examining some twelve thousand points; here
        the default recursion limit is 1000, so the same call raises RecursionError.
        Same lesson, met sooner. ``Moves2A`` is the search written as a real
        iteration and has no such limit.

        :param points: the points still to consider.
        :param result: the answer so far.
        :return: whether the target was reached.
        """
        if not points:
            return result
        x = points.popleft()
        if x == self.t:
            return True
        if x.x > self.t.x or x.y > self.t.y:
            return self._inner(points, False)
        points.append(self.move(x, True))
        points.append(self.move(x, False))
        return self._inner(points, result)


class Moves2A(Moves):
    """
    The forward search with the two improvements that suggest themselves once the
    plain queue search has been written, and as a real iteration.

    The first is that ``Moves2`` puts both successors on the queue without troubling
    over which should be dealt with first, though it can make a great difference
    which path is followed. So the successor nearer the target goes on first.

    The second is to remember the points already eliminated, in a set, so that no
    point is examined twice.

    NOTE both are worth measuring rather than assuming, and the tests measure them.
    The cache NEVER hits: from a given start every reachable point has exactly one
    predecessor -- of (x-y, y) and (x, y-x) only one can have both coordinates
    positive -- so no point can be arrived at twice. That is the same observation
    which makes ``Moves3`` work, met here as an improvement worth nothing. The
    ordering changes nothing either, a queue being level-by-level: whichever
    successor goes on first, both are dealt with before anything they lead to.

    What the iteration does buy is a ceiling: this cannot run out of stack, where
    ``Moves2`` can. It still gets nowhere near the sixth condition.
    """

    def __init__(self, t: Point) -> None:
        """
        :param t: the target.
        """
        self.t = t
        self.examined = 0
        self.cache_hits = 0

    @staticmethod
    def of(x: int, y: int) -> Moves2A:
        """
        NOTE named separately because Python cannot overload; the Java has this as
        a second constructor.

        :param x: the target's x coordinate.
        :param y: the target's y coordinate.
        :return: a Moves2A aiming at that point.
        """
        return Moves2A(Point(x, y))

    def move(self, p: Point, which: bool) -> Point:
        """
        :param p: the point to move from.
        :param which: True to grow y, False to grow x.
        :return: where that move lands.
        """
        return Point(p.x, p.y + p.x) if which else Point(p.x + p.y, p.y)

    def valid(self, p: Point) -> bool:
        """
        :param p: the point to start from.
        :return: whether the target can be reached from it.
        """
        self.examined = 0
        self.cache_hits = 0
        points: deque[Point] = deque([p])
        eliminated: set[Point] = set()
        while points:
            self.examined += 1
            q = points.popleft()
            if q == self.t:
                return True
            if q.x > self.t.x or q.y > self.t.y:
                continue  # overshot: this path is dead
            if q in eliminated:
                self.cache_hits += 1
                continue
            eliminated.add(q)
            a, b = self.move(q, True), self.move(q, False)
            # the nearer of the two goes on first
            if self._distance(a) <= self._distance(b):
                points.append(a)
                points.append(b)
            else:
                points.append(b)
                points.append(a)
        return False

    def valid_xy(self, x: int, y: int) -> bool:
        """
        :param x: the starting x coordinate.
        :param y: the starting y coordinate.
        :return: whether the target can be reached from there.
        """
        return self.valid(Point(x, y))

    def _distance(self, p: Point) -> int:
        """
        :param p: a point not beyond the target.
        :return: how far it is from the target, counting the units still to cover
                 in each direction.
        """
        return (self.t.x - p.x) + (self.t.y - p.y)


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
