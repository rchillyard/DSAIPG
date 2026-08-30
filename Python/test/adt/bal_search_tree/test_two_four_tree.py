import unittest

from src.adt.bal_search_tree.two_four_tree import TwoFourTree

# TwoFourTree is a skeleton: its Java counterpart says "Ignore this class.
# There are no unit tests," and has none.  These tests therefore only pin down
# the skeleton's current shape -- notably that `get` is a placeholder that
# always returns None -- rather than any 2-4 tree behaviour.


class TestTwoFourTree(unittest.TestCase):
    def test_initialization(self):
        """Test that the tree initializes with a None root."""
        tree = TwoFourTree[int, str]()
        self.assertIsNone(tree.root)

    def test_get_is_a_placeholder(self):
        """`get` is a stub that returns None for every key, implemented or not."""
        tree = TwoFourTree[int, str]()
        self.assertIsNone(tree.get(10))

    def test_cf_node_logic(self):
        """Test the _cf helper method logic completely."""
        tree = TwoFourTree[int, str]()
        
        # Create some dummy nodes for testing
        node_match = tree._Node("match", 10, 20, 30)
        node_alt = tree._Node("alt", 5, 15, 25)
        
        # 1. Test key == k (should return node_match)
        # key=10, k=10
        result = tree._cf(10, node_match, 10, node_alt)
        self.assertEqual(result, node_match)

        # 2. Test key < k (should return node_alt)
        # key=5, k=10
        result = tree._cf(5, node_match, 10, node_alt)
        self.assertEqual(result, node_alt)

        # 3. Test key > k (should return None)
        # key=15, k=10
        result = tree._cf(15, node_match, 10, node_alt)
        self.assertIsNone(result)

    def test_cf_comparable_requirement(self):
        """Verify that _cf works with standard comparable types (like int and str)."""
        tree_str = TwoFourTree[str, int]()
        node_a = tree_str._Node(1, "a", "b", "c")
        node_b = tree_str._Node(2, "d", "e", "f")

        # "apple" == "apple" -> node_a
        self.assertEqual(tree_str._cf("apple", node_a, "apple", node_b), node_a)
        
        # "apple" < "banana" -> node_b
        self.assertEqual(tree_str._cf("apple", node_a, "banana", node_b), node_b)
        
        # "cherry" > "banana" -> None
        self.assertIsNone(tree_str._cf("cherry", node_a, "banana", node_b))

if __name__ == '__main__':
    unittest.main()
