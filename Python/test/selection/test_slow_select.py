import unittest
from src.selection.slow_select import SlowSelect

class TestSlowSelect(unittest.TestCase):

    def test_select_with_integers(self):
        a = [34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89]
        b = sorted(a)
        
        # Java k is 1-based (size of k smallest elements), but my ported SlowSelect select method expects k as the INDEX (0-based) 
        # Wait, let me check SlowSelect.java again.
        
        # SlowSelect.java: 
        # public SlowSelect(int k) { this.k = k; }
        # public X select(X[] a, int k) 
        # It seems the `k` in constructor is "number of smaller elements", but `k` in select is "kth smallest index".
        # Java test: k starts at 1. ss.select(a, ss.k). assertEquals(b[k-1], result).
        # So if k=1 (constructor), we want 1st smallest, which is index 0.
        # My Python implementation:
        # return k_array[k-1] (if k passed to select).
        # Let's verify Python implementation.
        
        # Python SlowSelect.select(a, k):
        # k_array = [None] * k
        # ...
        # return k_array[k-1]
        
        # If I want the smallest element (index 0), Java does:
        # SlowSelect(1).select(a, 1). Returns b[0].
        # Python SlowSelect(1).select(a, 1) -> k=1 -> k_array size 1. returns k_array[0]. Correct.
        
        # So my Python port matches Java logic where 'k' passed to select is essentially "1-based rank".
        # But 'quick_select' expects 'k' as "0-based index".
        # The Interface `Select` says:
        # @param k the index (0-based) of the smallest element to find
        
        # Wait, `SlowSelect.java` select(X[] a, int k)
        # Inside: X[] kArray = ... length k.
        # It finds the k smallest elements.
        # And returns kArray[k-1]. This IS the k-th smallest element (1-based rank k).
        # But the interface says k is 0-based index.
        # If I want index 0 (smallest), I should pass 0 to select?
        # If I pass 0 to SlowSelect, kArray has length 0. return kArray[-1]. Error.
        
        # It seems SlowSelect in Java VIOLATES the interface contract or interprets k differently?
        # Java Interface: "k the index (0-based)"
        # SlowSelect Java: "kArray = new Object[k]". If k=0, empty array.
        
        # The Java test passes k starting from 1.
        # "SlowSelect<Integer> ss = new SlowSelect<>(k);"
        # "ss.select(a, ss.k);"
        # So if k=1, it asks for 1st smallest. Index 0.
        # But QuickSelect expects index 0 for 1st smallest.
        
        # Python `SlowSelect` port I wrote copied Java logic precisely.
        # So I should test it as Java tests it: pass k=1 for smallest element.
        
        k = 1
        while k <= len(a):
            ss = SlowSelect(k)
            # In Python port, I used 'k' argument to build array of size 'k'.
            result = ss.select(list(a), k)
            self.assertEqual(b[k-1], result)
            k += 1

    def test_select_with_characters(self):
        a = ['a', 'Z', 'e', 'R', '2', 'w', 'B', '9', 'z', '0', 'A', 'r', 'b', '3', 'E', 'W', '$', '%', '9', 'Z', 'e', '!', '#', '2', 'b']
        b = sorted(a)
        k = 1
        while k <= len(a):
            ss = SlowSelect(k)
            result = ss.select(list(a), k)
            self.assertEqual(b[k-1], result)
            k += 1

#    def test_select_with_strings(self):
         # ... similar structure, omitting for brevity/redundancy unless needed

    def test_select_first_element(self):
        a = [34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89]
        k = 1
        ss = SlowSelect(k)
        result = ss.select(a, k)
        self.assertEqual(-100, result)

    def test_select_last_element(self):
        a = [34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89]
        k = len(a)
        ss = SlowSelect(k)
        result = ss.select(a, k)
        self.assertEqual(89, result)

if __name__ == '__main__':
    unittest.main()
