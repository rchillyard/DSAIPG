import pytest

from src.sort.generic.classifier import Classifier
from src.sort.generic.has_additional_memory import HasAdditionalMemory
from src.sort.generic.sort import ProcessingSort, Sort


class SelectionSortForTesting(Sort[int]):
    """
    A minimal Sort, so that the machinery in the base class can be tested
    without depending on a real sort. It records what it was asked to do.
    """

    def __init__(self) -> None:
        self.inits: list[int] = []
        self.closed = False

    def get_description(self) -> str:
        return "selection sort for testing"

    def sort_range(self, xs, from_, to):
        for i in range(from_, to):
            least = min(range(i, to), key=lambda j: xs[j])
            xs[i], xs[least] = xs[least], xs[i]

    def init(self, n):
        self.inits.append(n)

    def close(self):
        self.closed = True


class TestSort:
    def test_sort_range_sorts_only_the_range(self):
        xs = [5, 4, 3, 2, 1]
        SelectionSortForTesting().sort_range(xs, 1, 4)
        assert xs == [5, 2, 3, 4, 1]

    def test_sort_returns_a_sorted_copy_and_leaves_the_original(self):
        xs = [3, 1, 2]
        result = SelectionSortForTesting().sort(xs)
        assert result == [1, 2, 3]
        assert xs == [3, 1, 2]

    def test_sort_without_a_copy_sorts_in_place(self):
        xs = [3, 1, 2]
        result = SelectionSortForTesting().sort(xs, make_copy=False)
        assert result is xs
        assert xs == [1, 2, 3]

    def test_mutating_sort(self):
        xs = [3, 1, 2]
        SelectionSortForTesting().mutating_sort(xs)
        assert xs == [1, 2, 3]

    def test_sort_calls_init_with_the_length(self):
        sorter = SelectionSortForTesting()
        sorter.sort([3, 1, 2])
        assert sorter.inits == [3]

    def test_sort_collection(self):
        assert SelectionSortForTesting().sort_collection((3, 1, 2)) == [1, 2, 3]

    def test_sort_collection_of_nothing(self):
        assert SelectionSortForTesting().sort_collection([]) == []

    def test_sort_collection_does_not_disturb_the_source(self):
        source = [3, 1, 2]
        SelectionSortForTesting().sort_collection(source)
        assert source == [3, 1, 2]

    def test_an_empty_list(self):
        assert SelectionSortForTesting().sort([]) == []

    def test_a_single_element(self):
        assert SelectionSortForTesting().sort([1]) == [1]

    def test_it_closes_at_the_end_of_a_with_block(self):
        with SelectionSortForTesting() as sorter:
            sorter.sort([2, 1])
        assert sorter.closed

    def test_it_closes_even_when_the_body_raises(self):
        sorter = SelectionSortForTesting()
        with pytest.raises(RuntimeError):  # noqa: PT012 - the body must raise
            with sorter:
                raise RuntimeError("boom")
        assert sorter.closed

    def test_it_cannot_be_instantiated_without_sort_range(self):
        with pytest.raises(TypeError):
            Sort()


class CountingProcessingSort(ProcessingSort[int]):
    """A ProcessingSort which records that post_process ran."""

    def __init__(self) -> None:
        self.post_processed: list[list[int]] = []

    def get_description(self):
        return "counting"

    def sort_range(self, xs, from_, to):
        xs[from_:to] = sorted(xs[from_:to])

    def init(self, n):
        pass

    def close(self):
        pass

    def post_process(self, xs):
        self.post_processed.append(list(xs))


class TestProcessingSort:
    def test_pre_process_returns_the_list_it_was_given(self):
        sorter = CountingProcessingSort()
        xs = [3, 1, 2]
        assert sorter.pre_process(xs) is xs

    def test_post_process_is_called_with_the_sorted_list(self):
        sorter = CountingProcessingSort()
        xs = [3, 1, 2]
        sorter.mutating_sort(xs)
        sorter.post_process(xs)
        assert sorter.post_processed == [[1, 2, 3]]

    def test_it_cannot_be_instantiated_without_post_process(self):
        with pytest.raises(TypeError):
            ProcessingSort()


class TestAbstractions:
    def test_classifier_cannot_be_instantiated(self):
        with pytest.raises(TypeError):
            Classifier()

    def test_has_additional_memory_cannot_be_instantiated(self):
        with pytest.raises(TypeError):
            HasAdditionalMemory()

    def test_a_classifier_can_be_written(self):
        class ByDigit(Classifier[int, int]):
            def classify(self, x, y):
                return (x // (10 ** y)) % 10

            def classify_at(self, xs, i, y):
                return self.classify(xs[i], y)

        by_digit = ByDigit()
        assert by_digit.classify(345, 0) == 5
        assert by_digit.classify(345, 2) == 3
        assert by_digit.classify_at([345], 0, 1) == 4
