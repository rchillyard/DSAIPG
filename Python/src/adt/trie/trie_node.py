from typing import Dict

class TrieNode:
    """
    Represents a single node in a Trie (prefix tree).
    """
    def __init__(self):
        """
        Initialize a TrieNode.
        
        Attributes:
            children (Dict[str, TrieNode]): Dictionary mapping characters to child TrieNodes.
            is_end_of_word (bool): Flag indicating if this node represents the end of a word.
        """
        self.children: Dict[str, TrieNode] = {}
        self.is_end_of_word: bool = False
