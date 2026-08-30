class DirectedEdge:
    """
    Represents a weighted edge in an edge-weighted directed graph.

    Each edge consists of two integers (naming the two vertices) and a real-value weight.
    Provides access to the two endpoints and the weight.
    """

    def __init__(self, v: int, w: int, weight: float):
        """
        Initialize a directed edge from vertex `v` to vertex `w` with the given `weight`.

        Args:
            v: The tail vertex.
            w: The head vertex.
            weight: The weight of the directed edge.

        Raises:
            ValueError: If either `v` or `w` is negative, or if `weight` is NaN.
        """
        if v < 0:
            raise ValueError("Vertex names must be non-negative integers")
        if w < 0:
            raise ValueError("Vertex names must be non-negative integers")
        if weight != weight:  # NaN check
            raise ValueError("Weight is NaN")
        self._v = v
        self._w = w
        self._weight = weight

    @property
    def from_vertex(self) -> int:
        """Return the tail vertex of the directed edge."""
        return self._v

    @property
    def to_vertex(self) -> int:
        """Return the head vertex of the directed edge."""
        return self._w

    @property
    def weight(self) -> float:
        """Return the weight of the directed edge."""
        return self._weight

    def __repr__(self) -> str:
        """Return a string representation of the directed edge."""
        return f"{self._v}->{self._w} {self._weight:5.2f}"
