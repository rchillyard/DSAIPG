import unittest
from src.adt.symbol_table.tree.bst_opt_del import BSTOptimisedDeletion

class TestBSTOptimisedDeletion(unittest.TestCase):

    def test_put_single_element(self):
        bst = BSTOptimisedDeletion()
        value = bst.put(10, "A")
        self.assertEqual(value, "A")
        self.assertTrue(10 in bst)
        self.assertEqual(bst.get(10), "A")
        self.assertEqual(bst.size, 1)

    def test_put_update_existing_key(self):
        bst = BSTOptimisedDeletion()
        bst.put(10, "A")
        updated_value = bst.put(10, "B")
        self.assertEqual(updated_value, "B") # Note: Python impl returns new value or None, checking behavior
        self.assertTrue(10 in bst)
        self.assertEqual(bst.get(10), "B")
        self.assertEqual(bst.size, 1)

    def test_put_multiple_elements(self):
        bst = BSTOptimisedDeletion()
        bst.put(10, "A")
        bst.put(5, "B")
        bst.put(15, "C")

        self.assertTrue(10 in bst)
        self.assertTrue(5 in bst)
        self.assertTrue(15 in bst)
        self.assertEqual(bst.get(10), "A")
        self.assertEqual(bst.get(5), "B")
        self.assertEqual(bst.get(15), "C")
        self.assertEqual(bst.size, 3)

    def test_put_sorted_order(self):
        bst = BSTOptimisedDeletion()
        for i in range(1, 6):
            bst.put(i, f"Value{i}")

        self.assertEqual(bst.size, 5)
        for i in range(1, 6):
            self.assertTrue(i in bst)
            self.assertEqual(bst.get(i), f"Value{i}")

    def test_put_duplicate_keys(self):
        bst = BSTOptimisedDeletion()
        bst.put(20, "FirstValue")
        bst.put(20, "UpdatedValue")

        self.assertEqual(bst.size, 1)
        self.assertEqual(bst.get(20), "UpdatedValue")

    def test_put_into_empty_tree(self):
        bst = BSTOptimisedDeletion()
        value = bst.put(50, "RootValue")

        self.assertEqual(value, "RootValue")
        self.assertTrue(50 in bst)
        self.assertEqual(bst.get(50), "RootValue")
        self.assertEqual(bst.size, 1)

    def test_put_complex_tree(self):
        bst = BSTOptimisedDeletion()
        bst.put(30, "Root")
        bst.put(20, "Left")
        bst.put(40, "Right")
        bst.put(10, "LeftLeft")
        bst.put(25, "LeftRight")
        bst.put(35, "RightLeft")
        bst.put(50, "RightRight")

        self.assertEqual(bst.size, 7)
        self.assertEqual(bst.get(30), "Root")
        self.assertEqual(bst.get(20), "Left")
        self.assertEqual(bst.get(40), "Right")
        self.assertEqual(bst.get(10), "LeftLeft")
        self.assertEqual(bst.get(25), "LeftRight")
        self.assertEqual(bst.get(35), "RightLeft")
        self.assertEqual(bst.get(50), "RightRight")

if __name__ == '__main__':
    unittest.main()
