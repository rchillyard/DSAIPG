import unittest
from src.adt.symbol_table.hashtable.hash_table_lp import HashTableLP
from src.adt.symbol_table.hashtable.hash_table_sc import HashTableSC
from src.adt.symbol_table.hashtable.st_map import STMap
from src.adt.symbol_table.hashtable.frequency_counter import FrequencyCounter

class TestHashTable(unittest.TestCase):

    def test_st_map(self):
        st = STMap()
        self.assertTrue(st.is_empty())
        st.put("A", 1)
        self.assertFalse(st.is_empty())
        self.assertEqual(st.get("A"), 1)
        self.assertEqual(st.size(), 1)
        st.put("B", 2)
        self.assertEqual(st.get("B"), 2)
        self.assertEqual(st.size(), 2)
        st.delete("A")
        self.assertIsNone(st.get("A"))
        self.assertEqual(st.size(), 1)

    def test_hash_table_lp(self):
        ht = HashTableLP(16)
        self.assertTrue(ht.is_empty())
        ht.put("A", 1)
        self.assertEqual(ht.get("A"), 1)
        self.assertEqual(ht.size(), 1)
        
        # Test collision (Linear Probing)
        # We can't easily force hash collision without knowing hash function internals or mocking,
        # but we can test multiple inserts.
        ht.put("B", 2)
        ht.put("C", 3)
        self.assertEqual(ht.get("B"), 2)
        self.assertEqual(ht.get("C"), 3)
        self.assertEqual(ht.size(), 3)
        
        # Test update
        ht.put("A", 10)
        self.assertEqual(ht.get("A"), 10)
        self.assertEqual(ht.size(), 3)

        # Test full table exception
        ht_small = HashTableLP(2)
        ht_small.put("1", 1)
        with self.assertRaises(Exception): # HashTableException
            ht_small.put("2", 2) # Should trigger full table check (size >= m-1)

    def test_hash_table_sc(self):
        ht = HashTableSC(16)
        self.assertTrue(ht.is_empty())
        ht.put("A", 1)
        self.assertEqual(ht.get("A"), 1)
        self.assertEqual(ht.size(), 1)
        
        ht.put("B", 2)
        self.assertEqual(ht.get("B"), 2)
        
        # Test delete
        ht.delete("A")
        self.assertIsNone(ht.get("A"))
        self.assertEqual(ht.size(), 1)
        self.assertEqual(ht.get("B"), 2)

    def test_frequency_counter(self):
        fc = FrequencyCounter()
        fc.increment("A")
        fc.increment("B")
        fc.increment("A")
        
        self.assertEqual(fc.get("A"), 2)
        self.assertEqual(fc.get("B"), 1)
        self.assertEqual(fc.total(), 3)
        
        self.assertAlmostEqual(fc.relative_frequency("A"), 2/3)
        self.assertAlmostEqual(fc.relative_frequency("B"), 1/3)

if __name__ == '__main__':
    unittest.main()
