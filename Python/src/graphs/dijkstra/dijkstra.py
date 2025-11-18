import heapq
from typing import List, Tuple, Optional
from .directed_edge import DirectedEdge
from .edge_weighted_digraph import EdgeWeightedDigraph


class Dijkstra:
    """
    Solves the single-source shortest paths problem in edge-weighted digraphs
    where the edge weights are non-negative.

    This implementation uses Dijkstra's algorithm with a binary heap.
    """

    def __init__(self, G: EdgeWeightedDigraph):
        """
        Constructs a Dijkstra object with the given edge-weighted directed graph.
        The graph must not contain negative edge weights.

        :param G: the edge-weighted directed graph
        :raises ValueError: if the graph contains an edge with negative weight
        """
        self.G = G
        self.n = G.V()
        for e in G.edges():
            if e.weight < 0:
                raise ValueError(f"edge {e} has negative weight")

    def shortest_paths(self, s: int) -> "ShortestPaths":
        """
        Solve Dijkstra's shortest paths algorithm for the starting point s.

        :param s: the starting vertex
        :return: a ShortestPaths object
        """
        result = ShortestPaths(self.G)
        result.solve(s)
        return result


class ShortestPaths:
    """
    Computes and stores shortest paths from a source vertex to all other vertices
    in a weighted directed graph using Dijkstra's algorithm.
    """

    def __init__(self, G: EdgeWeightedDigraph):
        self.G = G
        self.n = G.V()
        self._dist_to: List[float] = [float("inf")] * self.n
        self.edge_to: List[Optional[DirectedEdge]] = [None] * self.n
        self.pq: List[Tuple[float, int]] = []

    def solve(self, s: int):
        """
        Compute shortest paths from source vertex s.

        :param s: the source vertex
        :raises ValueError: if s is not a valid vertex
        """
        self._validate_vertex(s)
        self._dist_to[s] = 0.0
        heapq.heappush(self.pq, (0.0, s))

        while self.pq:
            dist_v, v = heapq.heappop(self.pq)
            if dist_v != self._dist_to[v]:
                continue
            for e in self.G.adj(v):
                self._relax(e)

        assert self._check(s)

    def dist_to(self, v: int) -> float:
        """
        Return the length of the shortest path from the source to vertex v.

        :param v: the vertex
        :return: shortest path distance
        :raises ValueError: if v is invalid
        """
        self._validate_vertex(v)
        return self._dist_to[v]

    def has_path_to(self, v: int) -> bool:
        """
        Check if there is a path to vertex v.

        :param v: the vertex
        :return: True if a path exists, False otherwise
        :raises ValueError: if v is invalid
        """
        self._validate_vertex(v)
        return self._dist_to[v] < float("inf")

    def path_to(self, v: int) -> Optional[List[DirectedEdge]]:
        """
        Return the shortest path to vertex v as a list of edges.

        :param v: the destination vertex
        :return: list of edges or None if no path exists
        :raises ValueError: if v is invalid
        """
        self._validate_vertex(v)
        if not self.has_path_to(v):
            return None
        path = []
        e = self.edge_to[v]
        while e is not None:
            path.append(e)
            e = self.edge_to[e.from_vertex]
        path.reverse()
        return path

    def _relax(self, e: DirectedEdge):
        v, w = e.from_vertex, e.to_vertex
        new_dist = self._dist_to[v] + e.weight
        if self._dist_to[w] > new_dist:
            self._dist_to[w] = new_dist
            self.edge_to[w] = e
            heapq.heappush(self.pq, (new_dist, w))

    def _validate_vertex(self, v: int):
        if v < 0 or v >= self.n:
            raise ValueError(f"vertex {v} is not between 0 and {self.n - 1}")

    def _check(self, s: int) -> bool:
        if self._check_weights():
            return False
        if self._check_consistency1(s):
            return False
        if self._check_consistency2(s):
            return False
        if self._check_relaxation():
            return False
        return not self._check_distances()

    def _check_weights(self) -> bool:
        for e in self.G.edges():
            if e.weight < 0:
                print("negative edge weight detected")
                return True
        return False

    def _check_consistency1(self, s: int) -> bool:
        if self._dist_to[s] != 0.0 or self.edge_to[s] is not None:
            print("distTo[s] and edgeTo[s] inconsistent")
            return True
        return False

    def _check_consistency2(self, s: int) -> bool:
        for v in range(self.n):
            if v == s:
                continue
            if self.edge_to[v] is None and self._dist_to[v] != float("inf"):
                print("distTo[] and edgeTo[] inconsistent")
                return True
        return False

    def _check_relaxation(self) -> bool:
        for v in range(self.n):
            for e in self.G.adj(v):
                w = e.to_vertex
                if self._dist_to[v] + e.weight < self._dist_to[w]:
                    print(f"edge {e} not relaxed")
                    return True
        return False

    def _check_distances(self) -> bool:
        for w in range(self.n):
            e = self.edge_to[w]
            if e is None:
                continue
            v = e.from_vertex
            if w != e.to_vertex:
                return True
            if abs(self._dist_to[v] + e.weight - self._dist_to[w]) > 1e-12:
                print(f"edge {e} on shortest path not tight")
                return True
        return False
