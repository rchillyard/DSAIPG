
from .trie_node import TrieNode


class Trie:
    """
    A Trie (prefix tree) data structure.
    """

    def __init__(self):
        """
        Initialize the Trie with a root node.
        """
        self.root = TrieNode()

    def insert(self, word: str) -> None:
        """
        Insert a word into the Trie.
        
        Args:
            word (str): The word to insert.
        """
        current = self.root
        for char in word:
            if char not in current.children:
                current.children[char] = TrieNode()
            current = current.children[char]
        current.is_end_of_word = True

    def search(self, word: str) -> bool:
        """
        Search for a word in the Trie.
        
        Args:
            word (str): The word to search for.
            
        Returns:
            bool: True if the word exists, False otherwise.
        """
        current = self.root
        for char in word:
            if char not in current.children:
                return False
            current = current.children[char]
        return current.is_end_of_word

    def autocomplete(self, prefix: str) -> list[str]:
        """
        Find all words in the Trie that start with the given prefix.
        
        Args:
            prefix (str): The prefix to match.
            
        Returns:
            List[str]: A list of matching words.
        """
        results: list[str] = []
        current = self.root
        for char in prefix:
            if char not in current.children:
                return results
            current = current.children[char]
        self._dfs(current, prefix, results)
        return results

    def _dfs(self, node: TrieNode, prefix: str, results: list[str]) -> None:
        """
        Helper method for depth-first search to collect words.
        
        Args:
            node (TrieNode): Current node.
            prefix (str): Current prefix string.
            results (List[str]): List to accumulate results.
        """
        if node.is_end_of_word:
            results.append(prefix)
        for char, child_node in node.children.items():
            self._dfs(child_node, prefix + char, results)

    def delete(self, word: str) -> bool:
        """
        Delete a word from the Trie.
        
        Args:
            word (str): The word to delete.
            
        Returns:
            bool: True if the word was deleted, False otherwise.
        """
        if not self.search(word):
            return False
        self._delete(self.root, word, 0)
        return True

    def _delete(self, current: TrieNode, word: str, index: int) -> bool:
        """
        Recursive helper method to delete a word.
        
        Args:
            current (TrieNode): Current node.
            word (str): The word to delete.
            index (int): Current character index.
            
        Returns:
            bool: True if the current node should be deleted (it has no children and isn't a word end).
        """
        if index == len(word):
            if not current.is_end_of_word:
                return False
            current.is_end_of_word = False
            return len(current.children) == 0

        char = word[index]
        if char not in current.children:
            return False
        
        node = current.children[char]
        should_delete_current_node = self._delete(node, word, index + 1)

        if should_delete_current_node:
            del current.children[char]
            return len(current.children) == 0 and not current.is_end_of_word

        return False
