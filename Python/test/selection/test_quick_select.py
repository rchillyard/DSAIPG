import unittest

from src.selection.quick_select import QuickSelect

# These tests mirror QuickSelectTest.java: they assert what a *correct*
# QuickSelect returns.  The range checks in `select` run before the
# `TO BE IMPLEMENTED` stub, so the ValueError tests pass today; the rest fail
# until the exercise is done, which is the intended state.
#
# QuickSelectTest.testPartition has no counterpart here because it needs
# Partition/Partitioner from sort.linearithmic, which has not been ported.


class TestQuickSelect(unittest.TestCase):

    def test_select_with_integers(self):
        a = [34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89]
        expected = sorted(a)
        qs = QuickSelect()
        for k in range(len(a)):
            # select may reorder its argument, so give it a copy each time.
            self.assertEqual(expected[k], qs.select(list(a), k))

    def test_select_with_characters(self):
        a = ['a', 'Z', 'e', 'R', '2', 'w', 'B', '9', 'z', '0', 'A', 'r', 'b', '3', 'E', 'W', '$', '%', '9', 'Z', 'e',
             '!', '#', '2', 'b']
        expected = sorted(a)
        qs = QuickSelect()
        for k in range(len(a)):
            self.assertEqual(expected[k], qs.select(list(a), k))

    def test_select_with_strings(self):
        a = ["Texas", "new Mexico", "Florida", "alabama", "Oregon",
             "Michigan", "utah", "New York", "california", "georgia",
             "Idaho", "south Dakota", "Louisiana", "ohio", "massachusetts",
             "Colorado", "nevada", "Wyoming", "North Dakota", "maine",
             "kentucky", "New Jersey", "missouri", "Alaska", "virginia",
             "Minnesota", "hawaii", "Arkansas", "indiana", "Washington",
             "Pennsylvania", "illinois", "west Virginia", "South Carolina", "arizona",
             "Iowa", "rhode Island", "new Hampshire", "Tennessee", "Maryland",
             "connecticut", "Montana", "Wisconsin", "delaware", "north Carolina",
             "vermont", "Kansas", "mississippi", "Oklahoma", "nebraska"]
        expected = sorted(a)
        qs = QuickSelect()
        for k in range(len(a)):
            self.assertEqual(expected[k], qs.select(list(a), k))

    def test_select_first_element(self):
        a = [34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89]
        self.assertEqual(-100, QuickSelect().select(a, 0))

    def test_select_last_element(self):
        a = [34, -2, 45, 0, 11, -9, 22, 89, 33, 45, -100, 67, 89, 23, 0, -2, -9, 11, 34, 56, -100, 76, 45, 89]
        self.assertEqual(89, QuickSelect().select(a, len(a) - 1))

    def test_select_with_single_element(self):
        self.assertEqual(42, QuickSelect().select([42], 0))

    def test_select_with_duplicates(self):
        self.assertEqual(5, QuickSelect().select([5, 1, 5, 3, 5, 2], 3))

    def test_select_with_empty_array(self):
        with self.assertRaises(ValueError):
            QuickSelect().select([], 0)

    def test_select_with_invalid_k_negative(self):
        with self.assertRaises(ValueError):
            QuickSelect().select([1, 2, 3, 4, 5], -1)

    def test_select_with_invalid_k_out_of_bounds(self):
        with self.assertRaises(ValueError):
            QuickSelect().select([1, 2, 3, 4, 5], 5)
