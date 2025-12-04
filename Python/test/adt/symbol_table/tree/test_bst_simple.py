import unittest
from src.adt.symbol_table.tree.bst_simple import BSTSimple

class TestBSTSimple(unittest.TestCase):

    def test_put_all_with_multiple_entries(self):
        bst = BSTSimple()
        test_map = {10: "Ten", 20: "Twenty", 5: "Five"}
        bst.put_all(test_map)
        
        self.assertEqual(bst.get(10), "Ten")
        self.assertEqual(bst.get(20), "Twenty")
        self.assertEqual(bst.get(5), "Five")
        self.assertEqual(bst.size, 3)

    def test_size_with_two_children(self):
        bst = BSTSimple()
        bst.put(10, "Ten")
        bst.put(5, "Five")
        bst.put(15, "Fifteen")
        bst.put(12, "Twelve")
        bst.put(20, "Twenty")
        
        # Accessing internal nodes for verification (Python allows this easily)
        self.assertEqual(bst.root.count, 5)
        self.assertEqual(bst.root.smaller.count, 1) # 5
        self.assertEqual(bst.root.larger.count, 3) # 15
        self.assertEqual(bst.root.larger.smaller.count, 1) # 12
        self.assertEqual(bst.root.larger.larger.count, 1) # 20

    def test_put_all_with_empty_map(self):
        bst = BSTSimple()
        test_map = {}
        bst.put_all(test_map)
        self.assertEqual(bst.size, 0)

    def test_put_all_with_duplicate_keys(self):
        bst = BSTSimple()
        bst.put(10, "OldValue")
        test_map = {10: "NewValue", 20: "Twenty"}
        bst.put_all(test_map)
        
        self.assertEqual(bst.get(10), "NewValue")
        self.assertEqual(bst.get(20), "Twenty")
        self.assertEqual(bst.size, 2)

    def test_put_all_maintains_existing_entries(self):
        bst = BSTSimple()
        bst.put(1, "One")
        test_map = {2: "Two"}
        bst.put_all(test_map)
        
        self.assertEqual(bst.get(1), "One")
        self.assertEqual(bst.get(2), "Two")
        self.assertEqual(bst.size, 2)

    def test_put_single_entry(self):
        bst = BSTSimple()
        bst.put(10, "Ten")
        self.assertEqual(bst.get(10), "Ten")
        self.assertEqual(bst.size, 1)

    def test_put_duplicate_key(self):
        bst = BSTSimple()
        bst.put(10, "OldValue")
        old_value = bst.put(10, "NewValue")
        
        self.assertEqual(old_value, "OldValue")
        self.assertEqual(bst.get(10), "NewValue")
        self.assertEqual(bst.size, 1)

    def test_put_with_negative_key(self):
        bst = BSTSimple()
        bst.put(-5, "Negative")
        self.assertEqual(bst.get(-5), "Negative")
        self.assertEqual(bst.size, 1)

    def test_put_with_duplicate_values(self):
        bst = BSTSimple()
        bst.put(1, "DuplicateValue")
        bst.put(2, "DuplicateValue")
        
        self.assertEqual(bst.get(1), "DuplicateValue")
        self.assertEqual(bst.get(2), "DuplicateValue")
        self.assertEqual(bst.size, 2)

    def test_put_multiple_sequential_entries(self):
        bst = BSTSimple()
        bst.put(1, "One")
        bst.put(2, "Two")
        bst.put(3, "Three")
        
        self.assertEqual(bst.get(1), "One")
        self.assertEqual(bst.get(2), "Two")
        self.assertEqual(bst.get(3), "Three")
        self.assertEqual(bst.size, 3)

    def test_put_sparse_random_entries(self):
        bst = BSTSimple()
        bst.put(50, "Fifty")
        bst.put(20, "Twenty")
        bst.put(70, "Seventy")
        
        self.assertEqual(bst.get(50), "Fifty")
        self.assertEqual(bst.get(20), "Twenty")
        self.assertEqual(bst.get(70), "Seventy")
        self.assertEqual(bst.size, 3)

    def test_contains_key_in_tree(self):
        bst = BSTSimple()
        bst.put(1, "One")
        bst.put(2, "Two")
        bst.put(3, "Three")
        
        self.assertTrue(1 in bst)
        self.assertTrue(2 in bst)
        self.assertTrue(3 in bst)

    def test_contains_key_not_in_tree(self):
        bst = BSTSimple()
        bst.put(1, "One")
        bst.put(2, "Two")
        
        self.assertFalse(3 in bst)
        self.assertFalse(4 in bst)

    def test_delete_leaf_node(self):
        bst = BSTSimple()
        bst.put(10, "Ten")
        bst.put(5, "Five")
        bst.put(15, "Fifteen")
        
        bst.delete(5)
        
        self.assertIsNone(bst.get(5))
        self.assertEqual(bst.size, 2)
        self.assertTrue(10 in bst)
        self.assertTrue(15 in bst)

    def test_delete_node_with_single_child(self):
        bst = BSTSimple()
        bst.put(10, "Ten")
        bst.put(5, "Five")
        bst.put(15, "Fifteen")
        bst.put(12, "Twelve")
        
        val = bst.delete(15)
        
        self.assertIsNone(bst.get(15))
        self.assertEqual(val, "Fifteen")
        self.assertEqual(bst.size, 3)
        self.assertTrue(10 in bst)
        self.assertTrue(5 in bst)
        self.assertTrue(12 in bst)

    def test_delete_node_with_two_children(self):
        bst = BSTSimple()
        bst.put(10, "Ten")
        bst.put(5, "Five")
        bst.put(15, "Fifteen")
        bst.put(12, "Twelve")
        bst.put(20, "Twenty")
        
        bst.delete(15)
        
        self.assertIsNone(bst.get(15))
        self.assertEqual(bst.size, 4)
        self.assertTrue(10 in bst)
        self.assertTrue(5 in bst)
        self.assertTrue(12 in bst)
        self.assertTrue(20 in bst)

    def test_delete_root_node(self):
        bst = BSTSimple()
        bst.put(10, "Ten")
        bst.put(5, "Five")
        bst.put(15, "Fifteen")
        
        bst.delete(10)
        
        self.assertIsNone(bst.get(10))
        self.assertEqual(bst.size, 2)
        self.assertTrue(5 in bst)
        self.assertTrue(15 in bst)

    def test_delete_non_existent_key(self):
        bst = BSTSimple()
        bst.put(10, "Ten")
        bst.put(5, "Five")
        
        bst.delete(15)
        
        self.assertEqual(bst.size, 2)
        self.assertTrue(10 in bst)
        self.assertTrue(5 in bst)

    def test_delete_on_empty_tree(self):
        bst = BSTSimple()
        bst.delete(10)
        self.assertEqual(bst.size, 0)

if __name__ == '__main__':
    unittest.main()
