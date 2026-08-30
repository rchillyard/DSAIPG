"""
Huffman Coding Implementation

This module provides a Pythonic implementation of Huffman coding for data compression.
It includes tree construction, encoding, and decoding functionality.
"""

import heapq
from collections.abc import Callable
from dataclasses import dataclass, field
from itertools import count
from typing import Any, Optional


@dataclass(order=True)
class Node:
    """
    Represents a node in a Huffman tree.
    
    Nodes are ordered by frequency (lower frequency = higher priority),
    with ties broken by insertion order via the counter.
    """
    frequency: int
    _counter: int = field(compare=True)
    symbol: str | None = field(default=None, compare=False)
    zero: Optional['Node'] = field(default=None, compare=False, repr=False)
    one: Optional['Node'] = field(default=None, compare=False, repr=False)
    
    def is_leaf(self) -> bool:
        """Check if this node is a leaf (has no children)."""
        return self.zero is None and self.one is None
    
    def dfs(
        self,
        depth_function: Callable[[Any, int | None], Any],
        consumer: Callable[['Node', Any], None],
        depth_indicator: Any,
        branch: int | None
    ) -> None:
        """Perform depth-first traversal of the tree."""
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")


@dataclass(frozen=True)
class Code:
    """Represents a Huffman code with its binary value and bit length."""
    value: int
    length: int
    
    def __str__(self) -> str:
        """Return binary representation of the code."""
        if self.length == 0:
            return ""
        return bin(self.value)[2:].zfill(self.length)


class BitBuffer:
    """
    Manages binary encoding operations within a 64-bit container.
    
    This class handles packing variable-length codes into 64-bit integers,
    splitting codes that don't fit into the current buffer.
    """
    
    def __init__(self, value: int = 0, available: int = 64):
        self.value = value
        self.available = available
    
    def encode(self, code: Code) -> Code | None:
        """
        Encode a code into the buffer.
        
        Returns:
            Remaining code if it doesn't fit, None otherwise.
        """
        if self.available < code.length:
            # Code doesn't fit - split it
            remaining_len = code.length - self.available
            
            # Extract the lower bits that won't fit
            remainder_value = code.value & ((1 << remaining_len) - 1)
            remainder = Code(remainder_value, remaining_len)
            
            # Encode the upper bits that do fit
            upper_value = code.value >> remaining_len
            self._pack_bits(upper_value, self.available)
            
            return remainder
        
        self._pack_bits(code.value, code.length)
        return None
    
    def _pack_bits(self, value: int, length: int) -> None:
        """Pack bits into the buffer."""
        self.value = (self.value << length) | value
        self.available -= length
    
    def close(self) -> None:
        """Fill remaining bits with zeros."""
        self._pack_bits(0, self.available)


class HuffmanEncoder:
    """Encodes symbols using Huffman coding."""
    
    EMOJI_SELECTOR = '\uFE0F'
    
    def __init__(self, root: Node):
        self._codebook = self._build_codebook(root)
    
    def _build_codebook(self, node: Node) -> dict[str, Code]:
        """Build the encoding dictionary by traversing the tree."""
        # TO BE IMPLEMENTED  build a dict of symbol -> Code by traversing the tree
        raise NotImplementedError("TO BE IMPLEMENTED")
    
    def encode(self, symbols: list[str | None]) -> list[int]:
        """Encode a sequence of symbols into a list of 64-bit integers."""
        buffers = []
        current = BitBuffer()
        
        for symbol in symbols:
            code = self._get_code(symbol)
            if code is None:
                raise ValueError(f"Unknown symbol: {symbol!r}")
            
            remainder = current.encode(code)
            if remainder is not None:
                # Current buffer is full, start a new one
                buffers.append(current)
                current = BitBuffer()
                current.encode(remainder)
        
        current.close()
        buffers.append(current)
        return [buf.value for buf in buffers]
    
    def _get_code(self, symbol: str | None) -> Code | None:
        """Get code for a symbol, with fallback to emoji selector variant."""
        code = self._codebook.get(symbol)
        if code is None and symbol is not None:
            code = self._codebook.get(symbol + self.EMOJI_SELECTOR)
        return code
    
    def __str__(self) -> str:
        """Return string representation of the codebook."""
        lines = [f"{key}={code}" for key, code in self._codebook.items()]
        return "\n".join(lines) + "\n"


class HuffmanDecoder:
    """Decodes Huffman-encoded binary data."""
    
    def __init__(self, root: Node):
        self._root = root
    
    def decode(self, encoded: list[int]) -> str:
        """Decode a list of 64-bit integers back to symbols."""
        symbols = []
        state = self._root
        
        for value in encoded:
            state = self._decode_value(symbols, state, value)
            if state is None:
                break
        
        return "".join(symbols)
    
    def _decode_value(
        self,
        symbols: list[str],
        state: Node,
        value: int
    ) -> Node | None:
        """Decode a single 64-bit value."""
        # TO BE IMPLEMENTED  consume all 64 bits of value, one per step down the tree,
        # emitting a symbol at each leaf
        raise NotImplementedError("TO BE IMPLEMENTED")


class HuffmanCoding:
    """Main class for Huffman coding operations."""
    
    def __init__(self, nodes: list[Node] | None = None):
        self._heap = list(nodes) if nodes else []
        if self._heap:
            heapq.heapify(self._heap)
    
    def add(self, node: Node) -> None:
        """Add a node to the priority queue."""
        heapq.heappush(self._heap, node)
    
    def build_tree(self) -> Node | None:
        """
        Build the Huffman tree by repeatedly combining the two lowest-frequency nodes.
        
        Returns:
            The root node of the Huffman tree, or None if the heap is empty.
        """
        while len(self._heap) > 1:
            left = heapq.heappop(self._heap)
            right = heapq.heappop(self._heap)
            
            # Create parent node with combined frequency
            parent = Node(
                frequency=left.frequency + right.frequency,
                _counter=left._counter,  # Maintain ordering
                symbol=f"{left.symbol}-{right.symbol}",
                zero=left,
                one=right
            )
            self.add(parent)
        
        return heapq.heappop(self._heap) if self._heap else None
    
    @staticmethod
    def create_default() -> 'HuffmanCoding':
        """
        Create a Huffman coding instance with predefined symbol frequencies.
        
        This is a specific configuration for encoding bridge game data.
        """
        counter = count()
        huffman = HuffmanCoding()
        
        # Define symbols with their frequencies
        symbols = [
            (28, "W"), (28, "N"), (28, "E"), (28, "Z"),
            (20, "P"),
            (14, "2"),
            (12, "1"), (12, "3"), (12, "S\uFE0F"), (12, "H\uFE0F"),
            (12, "D\uFE0F"), (12, "C\uFE0F"),
            (11, "4"),
            (10, "5"),
            (9, "6"),
            (8, "7"), (8, "8"), (8, "9"), (8, "T"),
            (8, "A"), (8, "K"), (8, "Q"), (8, "J"),
            (3, "NT"),
            (2, "X"),
            (1, None), (1, "XX"),
        ]
        
        for freq, symbol in symbols:
            huffman.add(Node(frequency=freq, _counter=next(counter), symbol=symbol))
        
        return huffman
    
    @staticmethod
    def show_tree(root: Node) -> None:
        """Print the tree structure to stdout."""
        def format_depth(depth: Any, branch: int | None) -> str:
            return f"{depth}  {branch if branch is not None else ''}"
        
        def print_node(node: Node, depth: str) -> None:
            print(f"{depth}: {node.symbol} ({node.frequency}) ")
        
        root.dfs(format_depth, print_node, "", None)
    
    @staticmethod
    def parse_lin(lin_string: str) -> list[str | None]:
        """
        Parse a LIN (bridge hand) format string into individual symbols.
        
        Args:
            lin_string: A LIN format string containing bridge game data.
            
        Returns:
            List of symbols extracted from the LIN string.
        """
        parts = lin_string.upper().split("|")
        
        # Extract deal (part 5, skip first 2 chars)
        symbols = [c for c in parts[5][2:] if c not in ' ,']
        
        # Extract play sequence (every other part starting from 13)
        for i in range(13, len(parts), 2):
            symbols.extend(c for c in parts[i] if c not in ' ,')
        
        symbols.append(None)  # Terminator
        return symbols


def main() -> None:
    """Demonstration of Huffman coding."""
    huffman = HuffmanCoding.create_default()
    tree = huffman.build_tree()
    
    if tree:
        encoder = HuffmanEncoder(tree)
        print(encoder)
        HuffmanCoding.show_tree(tree)


if __name__ == "__main__":
    main()
