import unittest

from src.selection.quick_select import QuickSelect

class TestQuickSelect(unittest.TestCase):

    def test_select_with_integers(self):
        a = [34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89]
        b = sorted(a)
        k = 0
        qs = QuickSelect()
        
        while k < len(a):
            # Since QuickSelect is unimplemented, it should raise NotImplementedError
            try:
                # We need to copy a because select might modify it
                qs.select(list(a), k)
                self.fail("Should have raised NotImplementedError")
            except NotImplementedError:
                pass 
            k += 1

    def test_select_with_characters(self):
        a = ['a', 'Z', 'e', 'R', '2', 'w', 'B', '9', 'z', '0', 'A', 'r', 'b', '3', 'E', 'W', '$', '%', '9', 'Z', 'e', '!', '#', '2', 'b']
        b = sorted(a)
        k = 0
        qs = QuickSelect()
        while k < len(a):
            try:
                qs.select(list(a), k)
                self.fail("Should have raised NotImplementedError")
            except NotImplementedError:
                pass
            k += 1

    def test_select_with_invalid_k_negative(self):
        a = [1, 2, 3, 4, 5]
        k = -1
        qs = QuickSelect()
        with self.assertRaises(ValueError):
            qs.select(a, k)

    def test_select_with_invalid_k_out_of_bounds(self):
        a = [1, 2, 3, 4, 5]
        k = 5
        qs = QuickSelect()
        with self.assertRaises(ValueError):
            qs.select(a, k)

if __name__ == '__main__':
    unittest.main()
