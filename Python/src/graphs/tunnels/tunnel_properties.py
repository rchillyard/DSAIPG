"""
Ported from graphs/tunnels/TunnelProperties.java.
"""

from __future__ import annotations


class TunnelProperties:
    """
    What one possible tunnel would cost, how long it would be, and which phase of
    the works it belongs to. Phase 0 means the tunnel is already there.

    Tunnels are ordered by cost, which is what makes this usable as an MST edge
    attribute, and it carries the sequence number the MST algorithm assigns.
    """

    def __init__(self, cost: int, length: int, phase: int, sequence: int = 0) -> None:
        """
        :param cost: what the tunnel would cost, in dollars.
        :param length: how long it would be, in metres.
        :param phase: 0 if the tunnel already exists, otherwise 1.
        :param sequence: where it comes in the order the MST chose.
        """
        self.cost = cost
        self.length = length
        self.phase = phase
        self.sequence = sequence

    def get_sequence(self) -> int:
        """
        :return: where this tunnel comes in the order the MST chose.
        """
        return self.sequence

    def set_sequence(self, sequence: int) -> None:
        """
        :param sequence: where this tunnel comes in the order the MST chose.
        """
        self.sequence = sequence

    def __lt__(self, other: TunnelProperties) -> bool:
        return self.cost < other.cost

    def __eq__(self, other: object) -> bool:
        return (
            isinstance(other, TunnelProperties)
            and (self.cost, self.length, self.phase) == (other.cost, other.length, other.phase)
        )

    def __hash__(self) -> int:
        return hash((self.cost, self.length, self.phase))

    def __str__(self) -> str:
        phase = "existing" if self.phase == 0 else "new"
        return (
            f"sequence: {self.sequence}, phase: {phase} tunnel of length: "
            f"{self.length}m at cost: ${self.cost:,}"
        )
