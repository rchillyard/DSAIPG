import random
from array import array
from concurrent.futures import ProcessPoolExecutor

import pytest

from src.sort.par import par_sort
from src.sort.par.main import random_array, time_sort
from src.sort.par.par_sort import TYPE_CODE, do_merge, sort, sort_recursive

# NOTE most of these avoid starting a process pool. Spawning interpreters is slow
# and the point being tested -- that the merge and the split are right -- does not
# need it. The two that do use a pool are marked, and use a small one.


def _elapsed(f) -> float:
    """
    :param f: something to run.
    :return: how long it took, in seconds.
    """
    import time
    start = time.perf_counter()
    f()
    return time.perf_counter() - start


def ints(values) -> array:
    """An array.array of the given values, as ParSort takes."""
    return array(TYPE_CODE, values)


class TestDoMerge:
    def test_two_sorted_arrays(self):
        assert list(do_merge(ints([1, 3, 5]), ints([2, 4, 6]))) == [1, 2, 3, 4, 5, 6]

    def test_one_empty(self):
        assert list(do_merge(ints([1, 2]), ints([]))) == [1, 2]
        assert list(do_merge(ints([]), ints([1, 2]))) == [1, 2]

    def test_both_empty(self):
        assert list(do_merge(ints([]), ints([]))) == []

    def test_disjoint_ranges(self):
        assert list(do_merge(ints([1, 2]), ints([8, 9]))) == [1, 2, 8, 9]
        assert list(do_merge(ints([8, 9]), ints([1, 2]))) == [1, 2, 8, 9]

    def test_equal_elements(self):
        assert list(do_merge(ints([2, 2]), ints([2, 2]))) == [2, 2, 2, 2]

    def test_it_takes_from_the_first_when_equal(self):
        # Which side wins a tie decides whether the sort is stable. These are
        # bare ints so it cannot be observed here, but the rule is: xs2 is taken
        # only when strictly smaller.
        assert list(do_merge(ints([1, 1]), ints([1]))) == [1, 1, 1]

    def test_uneven_lengths(self):
        assert list(do_merge(ints([1, 5, 9]), ints([2]))) == [1, 2, 5, 9]

    def test_a_random_pair(self):
        rng = random.Random(7)
        a = sorted(rng.randint(0, 999) for _ in range(50))
        b = sorted(rng.randint(0, 999) for _ in range(70))
        assert list(do_merge(ints(a), ints(b))) == sorted(a + b)

    def test_it_does_not_disturb_its_arguments(self):
        a, b = ints([1, 3]), ints([2, 4])
        do_merge(a, b)
        assert list(a) == [1, 3]
        assert list(b) == [2, 4]


class TestSequentialSort:
    """Below the cutoff, or with no executor, nothing is parallel."""

    def test_it_sorts(self):
        xs = ints([5, 3, 9, 1, 7])
        sort(xs, 0, len(xs))
        assert list(xs) == [1, 3, 5, 7, 9]

    def test_an_empty_range(self):
        xs = ints([])
        sort(xs, 0, 0)
        assert list(xs) == []

    def test_a_single_element(self):
        xs = ints([7])
        sort(xs, 0, 1)
        assert list(xs) == [7]

    def test_a_sub_range(self):
        xs = ints([99, 5, 3, 4, 1, 99])
        sort(xs, 1, 5)
        assert list(xs) == [99, 1, 3, 4, 5, 99]

    def test_duplicates(self):
        xs = ints([3, 1, 3, 1, 2])
        sort(xs, 0, len(xs))
        assert list(xs) == [1, 1, 2, 2, 3] or list(xs) == sorted([3, 1, 3, 1, 2])

    def test_a_random_array(self):
        rng = random.Random(42)
        values = [rng.randint(0, 99999) for _ in range(2000)]
        xs = ints(values)
        sort(xs, 0, len(xs))
        assert list(xs) == sorted(values)

    def test_negative_values(self):
        # Unlike RadixSort, this compares rather than classifies, so negative
        # values are fine.
        xs = ints([3, -7, 0, -1])
        sort(xs, 0, len(xs))
        assert list(xs) == [-7, -1, 0, 3]

    def test_no_executor_means_no_parallelism(self):
        # Even far above the cutoff, without an executor it sorts sequentially.
        rng = random.Random(1)
        values = [rng.randint(0, 999) for _ in range(par_sort.cutoff * 3)]
        xs = ints(values)
        sort(xs, 0, len(xs), None)
        assert list(xs) == sorted(values)


class TestSortRecursive:
    """
    NOTE this is an exercise, so these are reported as skipped until it is
    written. It is what runs in a worker process, which is why it takes and
    returns a whole array rather than an array with indices.
    """

    def test_it_returns_a_sorted_copy(self):
        assert list(sort_recursive(ints([3, 1, 2]))) == [1, 2, 3]

    def test_it_leaves_its_argument_alone(self):
        chunk = ints([3, 1, 2])
        sort_recursive(chunk)
        assert list(chunk) == [3, 1, 2], "the chunk must not be modified in place"

    def test_an_empty_chunk(self):
        assert list(sort_recursive(ints([]))) == []

    def test_it_returns_an_array_not_a_list(self):
        # It has to cross a process boundary, and that is the whole reason for
        # using array.array: it pickles as a buffer rather than as a million
        # separate objects.
        assert isinstance(sort_recursive(ints([2, 1])), array)


class TestParallelSort:
    """
    NOTE the parallel branch of sort is an exercise, so these are skipped until it
    is written. They start a real process pool, so there are only a few.
    """

    def test_it_sorts_above_the_cutoff(self, monkeypatch):
        monkeypatch.setattr(par_sort, "cutoff", 100)
        rng = random.Random(3)
        values = [rng.randint(0, 99999) for _ in range(1000)]
        xs = ints(values)
        with ProcessPoolExecutor(max_workers=2) as executor:
            sort(xs, 0, len(xs), executor)
        assert list(xs) == sorted(values)

    def test_it_sorts_a_sub_range_above_the_cutoff(self, monkeypatch):
        monkeypatch.setattr(par_sort, "cutoff", 50)
        rng = random.Random(5)
        values = [rng.randint(0, 9999) for _ in range(400)]
        xs = ints([10 ** 9] + values + [10 ** 9])
        with ProcessPoolExecutor(max_workers=2) as executor:
            sort(xs, 1, len(values) + 1, executor)
        assert list(xs) == [10 ** 9] + sorted(values) + [10 ** 9]

    def test_the_parallel_and_sequential_results_agree(self, monkeypatch):
        rng = random.Random(11)
        values = [rng.randint(0, 999999) for _ in range(2000)]
        sequential = ints(values)
        sort(sequential, 0, len(sequential), None)
        monkeypatch.setattr(par_sort, "cutoff", 200)
        parallel = ints(values)
        with ProcessPoolExecutor(max_workers=2) as executor:
            sort(parallel, 0, len(parallel), executor)
        assert list(parallel) == list(sequential)


class TestTheBenchmarkHelpers:
    def test_random_array_is_repeatable(self):
        assert list(random_array(50, seed=1)) == list(random_array(50, seed=1))

    def test_random_array_has_the_right_length_and_type(self):
        xs = random_array(20)
        assert len(xs) == 20
        assert xs.typecode == TYPE_CODE

    def test_time_sort_restores_the_cutoff(self):
        before = par_sort.cutoff
        time_sort(random_array(50), 10, None)
        assert par_sort.cutoff == before, "the module-level cutoff must not leak"

    def test_time_sort_returns_a_duration(self):
        assert time_sort(random_array(200), 1000, None) >= 0


class TestPicklingCost:
    """
    Why array.array and not list. This is the measurement that decided it, kept as
    a test so the reasoning is not lost -- if a future change starts sending lists
    across, the cost goes up by more than an order of magnitude.
    """

    def test_an_array_pickles_far_faster_than_a_list(self):
        # The margin is deliberately loose. Measured on a million ints the gap is
        # about 40x for a round trip, so a threshold of 5x is far outside any
        # plausible noise while not being brittle on a loaded machine.
        import pickle
        n = 200_000
        values = list(range(n))
        as_array = array(TYPE_CODE, values)
        list_bytes = pickle.dumps(values, protocol=5)
        array_bytes = pickle.dumps(as_array, protocol=5)

        def best(f, reps=5):
            return min(_elapsed(f) for _ in range(reps))

        list_time = best(lambda: pickle.dumps(values, protocol=5)) \
            + best(lambda: pickle.loads(list_bytes))
        array_time = best(lambda: pickle.dumps(as_array, protocol=5)) \
            + best(lambda: pickle.loads(array_bytes))
        assert array_time * 5 < list_time, (
            f"a round trip should be far cheaper for an array: "
            f"{array_time * 1000:.2f} ms against {list_time * 1000:.2f} ms")

    def test_the_saving_is_in_time_and_not_in_size(self):
        # Worth recording, because the obvious guess is wrong: a list of SMALL
        # ints pickles SMALLER than the array, since each needs only a byte or
        # two while the array always spends four. Choosing array.array for its
        # size would be choosing it for the wrong reason -- and the wrong reason
        # would point the other way for values like these.
        import pickle
        small = [i % 200 for i in range(200_000)]
        assert len(pickle.dumps(small, protocol=5)) \
               < len(pickle.dumps(array(TYPE_CODE, small), protocol=5))

    def test_for_large_values_the_array_is_smaller_too(self):
        # The size comparison depends entirely on the magnitude of the values,
        # which is another reason not to reason about it.
        import pickle
        # above 65,535 pickle spends five bytes on a list element and the array
        # still spends four; these also stay inside a signed 32-bit int, which
        # array('i') requires.
        large = [i + 100_000 for i in range(200_000)]
        assert len(pickle.dumps(array(TYPE_CODE, large), protocol=5)) \
               < len(pickle.dumps(large, protocol=5))

    def test_an_array_round_trips(self):
        import pickle
        xs = array(TYPE_CODE, [3, 1, 2])
        assert list(pickle.loads(pickle.dumps(xs, protocol=5))) == [3, 1, 2]


def test_the_cutoff_is_configurable():
    before = par_sort.cutoff
    try:
        par_sort.cutoff = 12345
        assert par_sort.cutoff == 12345
    finally:
        par_sort.cutoff = before


@pytest.mark.parametrize("n", [0, 1, 2, 3, 17])
def test_small_arrays_sort_sequentially(n):
    rng = random.Random(n)
    values = [rng.randint(0, 99) for _ in range(n)]
    xs = ints(values)
    sort(xs, 0, len(xs))
    assert list(xs) == sorted(values)
