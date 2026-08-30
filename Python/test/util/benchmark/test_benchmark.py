import pytest

from src.util.benchmark.benchmark import Benchmark
from src.util.benchmark.benchmark_timer import Benchmark_Timer, get_warmup_runs
from src.util.config.config import Config


class Counting(Benchmark[int]):
    """A Benchmark that counts its runs instead of timing anything."""

    def __init__(self) -> None:
        self.values: list[int] = []

    def run_from_supplier(self, supplier, m):
        for _ in range(m):
            self.values.append(supplier())
        return 0.0


class TestBenchmark:
    def test_run_defers_to_run_from_supplier(self):
        benchmark = Counting()
        benchmark.run(7, 3)
        assert benchmark.values == [7, 7, 7]

    def test_run_from_supplier_takes_a_fresh_value_each_time(self):
        benchmark = Counting()
        counter = iter(range(3))
        benchmark.run_from_supplier(lambda: next(counter), 3)
        assert benchmark.values == [0, 1, 2]

    def test_it_cannot_be_instantiated_without_run_from_supplier(self):
        with pytest.raises(TypeError):
            Benchmark()


class TestWarmupRuns:
    @pytest.mark.parametrize("m,expected", [
        (0, 1), (1, 1), (14, 1), (15, 1), (30, 2), (45, 3), (100, 3), (1_000_000, 3),
    ])
    def test_at_least_one_and_at_most_three(self, m, expected):
        assert get_warmup_runs(m) == expected


class TestBenchmarkTimer:
    """
    NOTE Benchmark_Timer is built on Timer, which carries exercises, so these are
    reported as skipped until Timer is written.
    """

    def test_it_runs_the_function_m_times_plus_the_warmup(self):
        config = Config.from_text("[timer]\nshowprogress =\n")
        runs = []
        benchmark = Benchmark_Timer("test", config, runs.append)
        benchmark.run(1, 30)
        assert len(runs) == 30 + get_warmup_runs(30)

    def test_it_returns_a_time(self):
        config = Config.from_text("[timer]\nshowprogress =\n")
        assert Benchmark_Timer("test", config, lambda t: None).run(1, 20) >= 0

    def test_the_pre_and_post_functions_run(self):
        config = Config.from_text("[timer]\nshowprogress =\n")
        pre, post = [], []

        def pre_function(t):
            pre.append(t)
            return t

        benchmark = Benchmark_Timer("test", config, lambda t: None, pre_function, post.append)
        benchmark.run(5, 20)
        assert len(pre) == 20 + get_warmup_runs(20), "the pre-function runs during the warmup too"
        assert len(post) == 20, "the post-function runs only in the timed phase"
