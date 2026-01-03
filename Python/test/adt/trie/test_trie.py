import unittest
from src.adt.trie.trie import Trie

class TestTrie(unittest.TestCase):
    def test_insert_and_search(self):
        trie = Trie()
        trie.insert("apple")
        trie.insert("app")
        self.assertTrue(trie.search("apple"))
        self.assertTrue(trie.search("app"))
        self.assertFalse(trie.search("appl"))

    def test_autocomplete(self):
        trie = Trie()
        words = ["cat", "car", "cart", "carbon", "dog"]
        for word in words:
            trie.insert(word)

        results = trie.autocomplete("car")
        expected = ["car", "cart", "carbon"]
        
        # Sort both lists to ensure order-independent comparison if needed
        # (Though DFS usually yields a specific order, it's safer to sort)
        results.sort()
        expected.sort()
        
        self.assertEqual(results, expected)

    def test_autocomplete_no_match(self):
        trie = Trie()
        trie.insert("hello")
        self.assertEqual(trie.autocomplete("xyz"), [])

    def test_delete_word(self):
        trie = Trie()
        trie.insert("apple")
        trie.insert("app")

        self.assertTrue(trie.search("apple"))
        self.assertTrue(trie.delete("apple"))
        self.assertFalse(trie.search("apple"))
        self.assertTrue(trie.search("app"))
        
        # Verify deleting a non-existent word or prefix logic if applicable
        # The Java test didn't explicitly test False return for delete, but we can verify our delete logic
        self.assertFalse(trie.delete("banana")) 

if __name__ == '__main__':
    unittest.main()
