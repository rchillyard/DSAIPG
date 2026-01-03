import unittest
from src.selection.shuffle import Shuffle
from src.selection.entropy import Entropy

class TestShuffle(unittest.TestCase):

    def test_shuffle_0(self):
        a = []
        expected = []
        # Calculate N bits just to initialize entropy, though empty array shouldn't need randoms
        entropy = Entropy(Shuffle.calculate_n_bits(0))
        shuffler = Shuffle(a, entropy)
        result = shuffler.shuffle_list()
        self.assertEqual(expected, result)

    def test_shuffle_1(self):
        a = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        # We cannot check for exact expected array because randomization differs from Java
        # But we can check that it has the same elements
        Entropy.seed = 0xAAAAAAAA
        entropy = Entropy(Shuffle.calculate_n_bits(10))
        shuffler = Shuffle(a, entropy)
        result = shuffler.shuffle_list()
        
        self.assertEqual(len(a), len(result))
        self.assertEqual(sorted(a), sorted(result))
        self.assertNotEqual(a, result) # Unlikely to be same order
        Entropy.seed = 0

    def test_shuffle_3(self):
        a = list(range(1, 53))
        shuffler = Shuffle(a)
        result = shuffler.shuffle_list()
        self.assertEqual(len(a), len(result))
        self.assertEqual(sorted(a), sorted(result))

    def test_powers_of_two(self):
        self.assertEqual(1, Shuffle._powers_of_2(0))
        self.assertEqual(2, Shuffle._powers_of_2(1))
        self.assertEqual(4, Shuffle._powers_of_2(2))
        self.assertEqual(8, Shuffle._powers_of_2(3))
        self.assertEqual(16, Shuffle._powers_of_2(4))

    def test_calculate_n_bits(self):
        self.assertEqual(0, Shuffle.calculate_n_bits(0))
        self.assertEqual(0, Shuffle.calculate_n_bits(1))
        self.assertEqual(1, Shuffle.calculate_n_bits(2))
        self.assertEqual(3, Shuffle.calculate_n_bits(3))
        self.assertEqual(5, Shuffle.calculate_n_bits(4))
        self.assertEqual(8, Shuffle.calculate_n_bits(5))
        # Java values:
        # 6 -> 11
        self.assertEqual(11, Shuffle.calculate_n_bits(6))
        # 16 -> 49
        self.assertEqual(49, Shuffle.calculate_n_bits(16))
        # 32 -> 129
        self.assertEqual(129, Shuffle.calculate_n_bits(32))

if __name__ == '__main__':
    unittest.main()
