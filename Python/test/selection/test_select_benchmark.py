import unittest

from src.selection.select_benchmark import SelectBenchmark


class TestSelectBenchmark(unittest.TestCase):

    def test_run_benchmarks_small_dataset(self):
        benchmark = SelectBenchmark(10, 100)
        result = benchmark.run_benchmarks()
        self.assertIsNotNone(result)
        self.assertIn("SlowSelect", result)
        self.assertIn("QuickSelect", result)

    def test_run_benchmarks_minimal_runs(self):
        benchmark = SelectBenchmark(1, 1)
        result = benchmark.run_benchmarks()
        self.assertIsNotNone(result)

if __name__ == '__main__':
    unittest.main()
