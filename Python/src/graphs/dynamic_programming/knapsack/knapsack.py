"""
The 0-1 knapsack problem, ported from graphs/dynamicProgramming/knapsack/Knapsack.java.
"""

from __future__ import annotations

from dataclasses import dataclass

from src.graphs.dag.di_graph import DiGraph
from src.graphs.dag.edge import Edge


@dataclass(frozen=True)
class Item:
    """
    One object that might be packed: what it weighs, and what it is worth.
    """

    id: str
    weight: int
    value: int

    def __str__(self) -> str:
        return f"{self.id}({self.weight}, {self.value})"


@dataclass(frozen=True)
class Key:
    """
    A sub-problem: the greatest value obtainable from the first `kappa` items
    within a weight of `omega`.

    It is both the key under which a sub-solution is memoised and a vertex of the
    dependency graph -- these are the same thing, which is why the book recommends
    a hash table with "the key from k and w".
    """

    kappa: int
    omega: int

    def __str__(self) -> str:
        return f"Key{{{self.kappa}, {self.omega}}}"


@dataclass(frozen=True)
class Solution:
    """
    A packing: the items chosen, and what they are worth together.
    """

    value: int
    items: tuple[Item, ...] = ()

    def increment(self, item: Item) -> Solution:
        """
        :param item: the item to add to this packing.
        :return: a new Solution with that item packed as well.
        """
        return Solution(self.value + item.value, (*self.items, item))

    def __str__(self) -> str:
        return f"Solution{{value={self.value}, items={list(self.items)}}}"


#: The packing that takes nothing, which is always available and worth nothing.
EMPTY = Solution(0, ())


def solution_of(items: list[Item]) -> Solution:
    """
    :param items: the items to pack.
    :return: the Solution that packs exactly those items.
    """
    result = EMPTY
    for item in items:
        result = result.increment(item)
    return result


class Knapsack:
    """
    The 0-1 knapsack problem by dynamic programming, expressed as a walk over an
    explicit graph of sub-problems.

    A vertex is a `Key` (kappa, omega). An edge is a decision about item number
    kappa, and it carries as its attribute the item that decision packs -- or None
    where the decision is to leave that item behind. So the two recursive cases
    given in the book become the two edges out of a vertex:

    - leave item kappa: an edge to (kappa - 1, omega) with no item attached;
    - take item kappa: an edge to (kappa - 1, omega - w_kappa) attributed with the
      item -- present only when the item actually fits.

    The value of a packing is the sum of the values along the path, so the answer
    is the *longest* path from (n, W) to a base case. Contrast `Coins`, whose edges
    each cost exactly one coin and whose answer is therefore a shortest path.

    Every edge decreases kappa by exactly one, so the graph is acyclic however the
    weights fall, and the recursion terminates at kappa = 0.

    NOTE the edges out of a vertex are built as that vertex is first visited, not
    in advance. This is the point the book makes about the bottom-up method:
    solving naively "will evaluate all nW values of m, including many that will
    never be needed". Building lazily means the graph holds exactly the
    sub-problems the search reached.

    NOTE also that this walk does not depend on the order in which the adjacency
    bag offers the two edges, which matters because a BagArray iterates in a random
    order. The two decisions are told apart by whether an edge carries an item, and
    a tie between them is settled in favour of leaving the item behind.
    """

    def __init__(self, items: list[Item]) -> None:
        """
        :param items: the objects that might be packed.
        """
        self.items = list(items)
        self.graph: DiGraph = DiGraph()
        self._memo: dict[Key, Solution] = {}

    def value(self, max_weight: int) -> Solution:
        """
        :param max_weight: the greatest weight the knapsack can carry.
        :return: the most valuable packing within that weight.
        """
        return self.mu(len(self.items), max_weight)

    def sub_problems(self) -> int:
        """
        :return: how many sub-problems have been solved and remembered.
        """
        return len(self._memo)

    def _add_edges(self, key: Key) -> None:
        """
        Add the edges leading out of a sub-problem: the decision to leave item
        kappa behind, and -- if it fits -- the decision to pack it.

        Called exactly once per sub-problem, because `mu` memoises the result of
        every vertex whose edges it builds and returns early thereafter.

        :param key: the sub-problem, whose kappa is at least 1.
        """
        item = self.items[key.kappa - 1]
        self.graph.add_edge(Edge(key, Key(key.kappa - 1, key.omega), None))
        if item.weight <= key.omega:
            self.graph.add_edge(Edge(key, Key(key.kappa - 1, key.omega - item.weight), item))

    def mu(self, kappa: int, omega: int) -> Solution:
        """
        Solve for one sub-problem, using the memoised solutions of its dependencies.

        :param kappa: how many of the items may be considered.
        :param omega: the weight still available.
        :return: the most valuable packing of the first kappa items within omega.
        """
        key = Key(kappa, omega)
        remembered = self._memo.get(key)
        if remembered is not None:
            return remembered
        if kappa < 1:
            return EMPTY
        self._add_edges(key)
        leave, take = EMPTY, None
        # the options come from the graph: each outgoing edge is one decision, and
        # its attribute says which item that decision packs, if any
        for edge in self.graph.adjacent(key):
            to = edge.get_to()
            item = edge.get_attributes()
            if item is None:
                leave = self.mu(to.kappa, to.omega)
            else:
                take = self.mu(to.kappa, to.omega).increment(item)
        value = take if take is not None and take.value > leave.value else leave
        self._memo[key] = value
        return value
