import random
from typing import List, Iterable, Optional
from .directed_edge import DirectedEdge


class EdgeWeightedDigraph:
    """
    An edge-weighted digraph of vertices named 0 through V-1, where each
    directed edge is of type DirectedEdge and has a real-valued weight.
    Supports adding a directed edge and iterating over all edges incident from a given vertex.
    Also provides methods for returning the indegree or outdegree of a vertex,
    the number of vertices V in the digraph, and the number of edges E in the digraph.
    Parallel edges and self-loops are permitted.

    This implementation uses an adjacency-lists representation.
    """

    def __init__(
        self, V: int, E: Optional[int] = None, G: Optional["EdgeWeightedDigraph"] = None
    ):
        """
        Initialize an edge-weighted digraph.

        Args:
            V: Number of vertices.
            E: Optional number of edges for random graph generation.
            G: Optional existing graph to deep copy.
        """
        if G is not None:
            self._V = G.V()
            self._E = G.E()
            self._indegree = [G.indegree(v) for v in range(G.V())]
            self._adj = [[] for _ in range(G.V())]
            for v in range(G.V()):
                # reverse to maintain order
                reverse = list(G.adj(v))
                reverse.reverse()
                self._adj[v].extend(reverse)
            return

        if V < 0:
            raise ValueError("Number of vertices in a Digraph must be non-negative")
        self._V = V
        self._E = 0
        self._indegree = [0] * V
        self._adj: List[List[DirectedEdge]] = [[] for _ in range(V)]

        if E is not None:
            if E < 0:
                raise ValueError("Number of edges in a Digraph must be non-negative")
            for _ in range(E):
                v = random.randint(0, V - 1)
                w = random.randint(0, V - 1)
                weight = 0.01 * random.randint(0, 99)
                self.add_edge(DirectedEdge(v, w, weight))

    def V(self) -> int:
        """Return the number of vertices."""
        return self._V

    def E(self) -> int:
        """Return the number of edges."""
        return self._E

    def add_edge(self, e: DirectedEdge) -> None:
        """
        Add a directed edge to the digraph.

        Args:
            e: The edge to add.

        Raises:
            ValueError: If edge endpoints are out of range.
        """
        v = e.from_vertex
        w = e.to_vertex
        self._validate_vertex(v)
        self._validate_vertex(w)
        self._adj[v].append(e)
        self._indegree[w] += 1
        self._E += 1

    def adj(self, v: int) -> Iterable["DirectedEdge"]:
        """
        Return the directed edges incident from vertex v.

        Args:
            v: The vertex.

        Returns:
            An iterable of edges.

        Raises:
            ValueError: If vertex index is invalid.
        """
        self._validate_vertex(v)
        return self._adj[v]

    def outdegree(self, v: int) -> int:
        """
        Return the outdegree of vertex v.

        Args:
            v: The vertex.

        Returns:
            Outdegree of v.

        Raises:
            ValueError: If vertex index is invalid.
        """
        self._validate_vertex(v)
        return len(self._adj[v])

    def indegree(self, v: int) -> int:
        """
        Return the indegree of vertex v.

        Args:
            v: The vertex.

        Returns:
            Indegree of v.

        Raises:
            ValueError: If vertex index is invalid.
        """
        self._validate_vertex(v)
        return self._indegree[v]

    def edges(self) -> Iterable["DirectedEdge"]:
        """Return all directed edges in the digraph."""
        for v in range(self._V):
            for e in self._adj[v]:
                yield e

    def __str__(self) -> str:
        """Return a string representation of the digraph."""
        lines = [f"{self._V} {self._E}"]
        for v in range(self._V):
            line = f"{v}: "
            line += "  ".join(str(e) for e in self._adj[v])
            lines.append(line)
        return "\n".join(lines)

    def _validate_vertex(self, v: int) -> None:
        """
        Validate vertex index.

        Args:
            v: Vertex index.

        Raises:
            ValueError: If index is out of range.
        """
        if v < 0 or v >= self._V:
            raise ValueError(f"vertex {v} is not between 0 and {self._V - 1}")
