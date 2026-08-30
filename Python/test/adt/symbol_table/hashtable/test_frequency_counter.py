import unittest

from src.adt.symbol_table.hashtable.frequency_counter import FrequencyCounter


class TestFrequencyCounter(unittest.TestCase):

    def test_increment0(self):
        fc = FrequencyCounter()
        x = "X"
        self.assertEqual(0, fc.get(x))
        fc.increment(x)
        self.assertEqual(1, fc.get(x))

    def test_increment_negative_scenario(self):
        fc = FrequencyCounter()
        with self.assertRaises(ValueError):
            fc.increment(None)
        # In Python, get(None) raises ValueError due to validate_key, unlike Java which might return null or 0 depending on impl
        with self.assertRaises(ValueError):
            fc.get(None)

    def test_increment_repeatedly_same_key(self):
        fc = FrequencyCounter()
        key = "RepeatedKey"
        for i in range(1, 10001):
            fc.increment(key)
            self.assertEqual(i, fc.get(key))

    def test_increment_single_key(self):
        fc = FrequencyCounter()
        key = "A"
        for i in range(1, 6):
            fc.increment(key)
            self.assertEqual(i, fc.get(key))

    def test_increment_multiple_keys(self):
        fc = FrequencyCounter()
        fc.increment("A")
        fc.increment("B")
        fc.increment("A")
        fc.increment("C")
        fc.increment("B")
        self.assertEqual(2, fc.get("A"))
        self.assertEqual(2, fc.get("B"))
        self.assertEqual(1, fc.get("C"))
        self.assertEqual(0, fc.get("D"))

    def test_relative_frequency(self):
        fc = FrequencyCounter()
        x = "X"
        for _ in range(42):
            fc.increment(x)
        self.assertAlmostEqual(1.0, fc.relative_frequency(x), delta=0.0000001)
        self.assertAlmostEqual(0.0, fc.relative_frequency("y"), delta=0.0000001)

    def test_relative_frequency_as_percentage(self):
        fc = FrequencyCounter()
        for _ in range(49):
            fc.increment("X")
        fc.increment("Y")
        self.assertAlmostEqual(98.0, fc.relative_frequency_as_percentage("X"), delta=0.0000001)
        self.assertAlmostEqual(2.0, fc.relative_frequency_as_percentage("Y"), delta=0.0000001)
        self.assertAlmostEqual(0.0, fc.relative_frequency_as_percentage("Z"), delta=0.0000001)

    def test_keys(self):
        fc = FrequencyCounter()
        self.assertEqual(set(), fc.keys())
        fc.increment("X")
        self.assertEqual({"X"}, fc.keys())
        fc.increment("Y")
        self.assertEqual({"X", "Y"}, fc.keys())

    def test_total(self):
        fc = FrequencyCounter()
        x = "X"
        self.assertEqual(0, fc.total())
        for _ in range(42):
            fc.increment(x)
        self.assertEqual(42, fc.total())

if __name__ == '__main__':
    unittest.main()
