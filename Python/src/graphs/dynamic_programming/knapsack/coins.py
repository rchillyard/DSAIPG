"""
The coin chooser, ported from graphs/dynamicProgramming/knapsack/Coins.java.
"""

from __future__ import annotations

from dataclasses import dataclass, field

from src.graphs.dag.di_graph import DiGraph
from src.graphs.dag.edge import Edge

#: The United States denominations, which the book uses throughout.
US = [1, 5, 10, 25]

#: What a Solution's coin count is when no solution exists.
NO_SOLUTION = -1


@dataclass(frozen=True)
class Solution:
    """
    A count of each denomination, and the total number of coins.

    NOTE the Java uses Integer.MAX_VALUE for "not a solution", so that its
    compareTo makes any real solution smaller. Python has no int maximum, so the
    sentinel is an explicit flag; `is_solution` says which, and comparison refuses
    to order a non-solution as though it were merely a large one.
    """

    n: int
    counts: tuple[int, ...] = field(compare=True)

    @property
    def is_solution(self) -> bool:
        """
        :return: whether this represents a real answer.
        """
        return self.n != NO_SOLUTION

    def increment(self, i: int) -> Solution:
        """
        :param i: the index of a denomination to add one of.
        :return: a new Solution with one more coin of that denomination.
        """
        if not self.is_solution:
            return self
        counts = list(self.counts)
        counts[i] += 1
        return Solution(self.n + 1, tuple(counts))

    def better_than(self, other: Solution) -> bool:
        """
        :param other: the Solution to compare with.
        :return: whether this uses fewer coins. A non-solution is never better.
        """
        if not self.is_solution:
            return False
        if not other.is_solution:
            return True
        return self.n < other.n

    def total(self, coins: list[int]) -> int:
        """
        :param coins: the denominations, in the order the counts follow.
        :return: the value this Solution adds up to.
        """
        return sum(c * v for c, v in zip(self.counts, coins))

    def __str__(self) -> str:
        return f"Solution{{coins={self.n}, counts={list(self.counts)}}}"


class Coins:
    """
    The fewest coins making a given value, by dynamic programming over an explicit
    dependency graph.

    This is Figure 10.11 of the book. A vertex is a remaining value -- a
    sub-problem, "how few coins make x" -- and an edge from x to x - c records the
    choice of one coin of denomination c. Every edge costs exactly one coin, so the
    fewest coins making v is the shortest path from v to 0::

        m(0) = 0
        m(x) = min over i of { m(x - c[i]) : x >= c[i] } + 1

    Every edge goes to a strictly smaller value, so the graph is acyclic -- the
    book's "no complication due to cycles" -- and descending order is a topological
    order, so one memoised pass suffices. Time and space are both Theta(v).

    The graph is built rather than left implicit in the recursion, so that the
    structure the book draws is something a caller can inspect: see `graph`.
    """

    def __init__(self, coins: list[int] | None = None) -> None:
        """
        :param coins: the denominations available; None means the US ones.
        """
        self.coins = list(coins) if coins is not None else list(US)
        self.zero = Solution(0, tuple(0 for _ in self.coins))
        self._no_solution = Solution(NO_SOLUTION, tuple(0 for _ in self.coins))
        self.graph: DiGraph = DiGraph()
        self._built = 0
        self._memo: dict[int, Solution] = {}

    def number(self, amount: int) -> Solution:
        """
        :param amount: the value to make.
        :return: the Solution using the fewest coins.
        """
        return self.mu(amount)

    def sub_problems(self) -> int:
        """
        :return: how many sub-problems have been solved and remembered.
        """
        return len(self._memo)

    def _build_graph(self, amount: int) -> None:
        """
        Extend the graph to cover every value reachable from amount.

        :param amount: the largest value needed.
        """
        for x in range(self._built + 1, amount + 1):
            for i, coin in enumerate(self.coins):
                if coin <= x:
                    self.graph.add_edge(Edge(x, x - coin, i))
        self._built = max(self._built, amount)

    def mu(self, amount: int) -> Solution:
        """
        Solve for one value, using the memoised solutions of its dependencies.

        :param amount: the value to make.
        :return: the best Solution, or a non-solution if there is none.
        """
        remembered = self._memo.get(amount)
        if remembered is not None:
            return remembered
        if amount < 0:
            return self._no_solution
        if amount == 0:
            return self.zero
        self._build_graph(amount)
        result = self._no_solution
        # the options come from the graph: each outgoing edge is one coin spent,
        # and its attribute says which denomination that was
        for edge in self.graph.adjacent(amount):
            option = self.mu(edge.get_to()).increment(edge.get_attributes())
            if option.better_than(result):
                result = option
        self._memo[amount] = result
        return result
