import pytest

from src.sort.helper.helper import discriminate_string, natural_comparison
from src.sort.helper.helper_exception import HelperException
from src.sort.helper.helper_factory import create
from src.sort.helper.instrumented_helper import InstrumentedHelper, count_inversions
from src.sort.helper.non_instrumenting_helper import NonInstrumentingHelper
from src.util.config.config_benchmark import setup_config, setup_config_fixes

# These mirror HelperTest.java and InstrumentedComparatorHelperTest.java.


def instrumented(n=0, fixes="false") -> InstrumentedHelper:
    """
    An instrumented Helper counting everything except, by default, fixes.

    NOTE n defaults to 0, meaning "not yet known", so that a test is free to
    call init. Setting it here and then calling init with a different value is
    an error, and rightly so.
    """
    return InstrumentedHelper("test", setup_config("true", fixes, "0", "0", "", ""), n=n)


def plain(n=10) -> NonInstrumentingHelper:
    """A Helper which counts nothing."""
    return NonInstrumentingHelper("test", setup_config("false", "", "0", "0", "", ""), n=n)


class TestNaturalComparison:
    def test_it_orders(self):
        assert natural_comparison(1, 2) == -1
        assert natural_comparison(2, 1) == 1
        assert natural_comparison(1, 1) == 0

    def test_it_works_for_strings(self):
        assert natural_comparison("a", "b") == -1

    def test_a_helper_uses_it_when_no_comparator_is_given(self):
        assert plain().compare(1, 2) < 0

    def test_a_comparator_overrides_it(self):
        reverse = NonInstrumentingHelper(
            "reverse", setup_config("false", "", "0", "0", "", ""),
            comparator=lambda v, w: natural_comparison(w, v))
        assert reverse.compare(1, 2) > 0


class TestPlainHelperDoesNotCount:
    def test_nothing_is_counted(self):
        helper = plain()
        xs = [3, 1, 2]
        helper.get(xs, 0)
        helper.compare_at(xs, 0, 1)
        helper.swap(xs, 0, 1)
        assert helper.get_hits() == 0
        assert helper.get_compares() == 0
        assert helper.get_swaps() == 0

    def test_it_still_sorts_correctly(self):
        helper = plain()
        xs = [3, 1, 2]
        helper.swap(xs, 0, 1)
        assert xs == [1, 3, 2]

    def test_instrumented_reports_false(self):
        assert not plain().instrumented()


class TestHits:
    """
    The whole point of the _v and _w variants: a value the caller already holds
    is one array access that does not have to happen again.
    """

    def test_get_counts_one_hit(self):
        helper = instrumented()
        helper.get([1, 2, 3], 0)
        assert helper.get_hits() == 1

    def test_swap_reads_both_and_writes_both(self):
        helper = instrumented()
        xs = [1, 2]
        helper.swap(xs, 0, 1)
        assert xs == [2, 1]
        assert helper.get_swaps() == 1
        assert helper.get_hits() == 4, "two reads and two writes"

    def test_swap_v_does_not_re_read_the_value_it_was_given(self):
        helper = instrumented()
        xs = [1, 2]
        helper.swap_v(xs[0], xs, 0, 1)
        assert xs == [2, 1]
        assert helper.get_hits() == 3, "one read and two writes"

    def test_swap_w_does_not_re_read_the_value_it_was_given(self):
        helper = instrumented()
        xs = [1, 2]
        helper.swap_w(xs[1], xs, 0, 1)
        assert xs == [2, 1]
        assert helper.get_hits() == 3, "one read and two writes"

    def test_swap_vw_reads_nothing(self):
        helper = instrumented()
        xs = [1, 2]
        helper.swap_vw(xs[0], xs[1], xs, 0, 1)
        assert xs == [2, 1]
        assert helper.get_hits() == 2, "two writes and no reads"
        assert helper.get_swaps() == 1

    def test_swapping_an_element_with_itself_costs_nothing(self):
        helper = instrumented()
        xs = [1, 2]
        helper.swap_vw(xs[0], xs[0], xs, 0, 0)
        assert helper.get_swaps() == 0
        assert helper.get_hits() == 0
        assert xs == [1, 2]

    def test_the_other_swaps_require_distinct_indices(self):
        # The assert is deliberate: swap, swap_v and swap_w read the list, and
        # reading the same element twice to exchange it with itself is waste.
        helper = instrumented()
        with pytest.raises(AssertionError):
            helper.swap([1, 2], 0, 0)


class TestCompares:
    def test_compare_counts_one(self):
        helper = instrumented()
        helper.compare(1, 2)
        assert helper.get_compares() == 1
        assert helper.get_hits() == 0

    def test_compare_at_reads_both(self):
        helper = instrumented()
        helper.compare_at([1, 2], 0, 1)
        assert helper.get_compares() == 1
        assert helper.get_hits() == 2

    def test_compare_v_reads_one(self):
        helper = instrumented()
        xs = [1, 2]
        helper.compare_v(xs, xs[0], 1)
        assert helper.get_compares() == 1
        assert helper.get_hits() == 1

    def test_compare_w_reads_one(self):
        helper = instrumented()
        xs = [1, 2]
        helper.compare_w(xs, 0, xs[1])
        assert helper.get_compares() == 1
        assert helper.get_hits() == 1

    def test_comparing_an_element_with_itself_is_free(self):
        helper = instrumented()
        assert helper.compare_at([1, 2], 1, 1) == 0
        assert helper.get_compares() == 0
        assert helper.get_hits() == 0

    def test_pure_comparison_counts_nothing(self):
        helper = instrumented()
        assert helper.pure_comparison(1, 2) < 0
        assert helper.get_compares() == 0

    def test_compare_with_lookups(self):
        helper = instrumented()
        helper.compare_with_lookups([1, 2], 0, 1, 2)
        assert helper.get_lookups() == 2

    def test_compare_with_lookups_rejects_too_many(self):
        with pytest.raises(AssertionError):
            instrumented().compare_with_lookups([1, 2], 0, 1, 3)

    def test_lookup_counts_one(self):
        helper = instrumented()
        assert helper.lookup(7) == 7
        assert helper.get_lookups() == 1


class TestCopies:
    def test_copy_counts_one_copy_and_one_hit(self):
        helper = instrumented()
        target = [0, 0]
        helper.copy(5, target, 0)
        assert target == [5, 0]
        assert helper.get_copies() == 1
        assert helper.get_hits() == 1

    def test_copy_at_also_reads(self):
        helper = instrumented()
        target = [0, 0]
        helper.copy_at([7, 8], 0, target, 1)
        assert target == [0, 7]
        assert helper.get_copies() == 1
        assert helper.get_hits() == 2

    def test_copy_block_between_lists(self):
        helper = instrumented()
        target = [0, 0, 0, 0]
        helper.copy_block([1, 2, 3], 0, target, 1, 3)
        assert target == [0, 1, 2, 3]
        assert helper.get_copies() == 3
        assert helper.get_hits() == 6, "a read and a write for each element"

    def test_copy_block_within_one_list(self):
        helper = instrumented()
        xs = [1, 2, 3, 4]
        helper.copy_block(xs, 0, xs, 1, 3)
        assert xs == [1, 1, 2, 3]
        assert helper.get_hits() == 4, "n + 1 when the source and target are the same"

    def test_copy_array(self):
        helper = instrumented()
        assert helper.copy_array([1, 2, 3]) == [1, 2, 3]
        assert helper.get_copies() == 3
        assert helper.get_hits() == 6

    def test_distribute_block(self):
        helper = instrumented()
        target = [0] * 3
        helper.distribute_block([2, 0, 1], 0, 3, target, lambda x: x)
        assert target == [0, 1, 2]
        assert helper.get_copies() == 3
        assert helper.get_hits() == 6


class TestConditionalSwaps:
    def test_it_swaps_when_out_of_order(self):
        helper = instrumented()
        xs = [2, 1]
        assert helper.swap_conditional(xs, 0, 1)
        assert xs == [1, 2]

    def test_it_leaves_an_ordered_pair_alone(self):
        helper = instrumented()
        xs = [1, 2]
        assert not helper.swap_conditional(xs, 0, 1)
        assert xs == [1, 2]

    def test_the_same_index_twice_does_nothing(self):
        helper = instrumented()
        assert not helper.swap_conditional([1, 2], 1, 1)

    def test_reversed_indices_still_work(self):
        helper = instrumented()
        xs = [2, 1]
        assert helper.swap_conditional_vw(xs, xs[1], 1, 0, xs[0])
        assert xs == [1, 2]

    def test_swap_stable_conditional(self):
        helper = instrumented()
        xs = [1, 3, 2]
        assert helper.swap_stable_conditional(xs, 2)
        assert xs == [1, 2, 3]

    def test_fix_inversion_with_two_indices(self):
        helper = plain()
        xs = [3, 1]
        helper.fix_inversion(xs, 0, 1)
        assert xs == [1, 3]

    def test_fix_inversion_with_one_index(self):
        helper = plain()
        xs = [1, 3, 2]
        helper.fix_inversion(xs, 2)
        assert xs == [1, 2, 3]

    def test_sort_pair(self):
        helper = plain()
        xs = [2, 1]
        assert helper.sort_pair(xs, 0, 2)
        assert xs == [1, 2]

    def test_sort_pair_ignores_a_wrong_sized_range(self):
        assert not plain().sort_pair([3, 2, 1], 0, 3)

    @pytest.mark.parametrize("xs", [
        [1, 2, 3], [1, 3, 2], [2, 1, 3], [2, 3, 1], [3, 1, 2], [3, 2, 1],
    ])
    def test_sort_trio_sorts_every_arrangement(self, xs):
        helper = instrumented()
        values = list(xs)
        helper.sort_trio(values, 0, 3)
        assert values == [1, 2, 3]

    def test_sort_trio_ignores_a_wrong_sized_range(self):
        xs = [2, 1]
        plain().sort_trio(xs, 0, 2)
        assert xs == [2, 1]


class TestSwapInto:
    def test_it_moves_an_element_down_and_shifts_the_rest_up(self):
        helper = plain()
        xs = [2, 3, 4, 1]
        helper.swap_into(xs, 0, 3)
        assert xs == [1, 2, 3, 4]

    def test_it_does_nothing_when_the_element_is_already_in_place(self):
        helper = plain()
        xs = [1, 2, 3]
        helper.swap_into(xs, 1, 1)
        assert xs == [1, 2, 3]

    def test_the_counts(self):
        # Checked against the Java, which reports the same figures.
        helper = instrumented(fixes="true")
        xs = [2, 3, 4, 1]
        helper.swap_into(xs, 0, 3)
        assert helper.get_swaps() == 1
        assert helper.get_copies() == 3, "one copy per element shifted, not two"
        assert helper.get_fixes() == 3, "one inversion fixed for each element shifted up"
        assert helper.get_hits() == 6

    @pytest.mark.parametrize("n", [1, 2, 3, 5])
    def test_it_copies_each_element_once(self, n):
        # The Java counted these twice: once explicitly and once inside
        # copy_block, so every half-swap reported 2n copies for n elements.
        helper = instrumented()
        xs = list(range(n + 1))
        helper.swap_into(xs, 0, n)
        assert helper.get_copies() == n

    def test_swap_into_sorted(self):
        helper = plain()
        xs = [1, 3, 5, 7, 4]
        helper.swap_into_sorted(xs, 0, 4)
        assert xs == [1, 3, 4, 5, 7]

    def test_swap_into_sorted_when_it_belongs_at_the_front(self):
        helper = plain()
        xs = [2, 4, 6, 1]
        helper.swap_into_sorted(xs, 0, 3)
        assert xs == [1, 2, 4, 6]

    def test_swap_into_sorted_when_it_is_already_in_place(self):
        helper = plain()
        xs = [1, 2, 3, 4]
        helper.swap_into_sorted(xs, 0, 3)
        assert xs == [1, 2, 3, 4]


class TestBinarySearch:
    def test_a_hit_gives_the_index(self):
        assert plain().binary_search([1, 3, 5, 7], 0, 4, 5) == 2

    def test_a_miss_gives_the_insertion_point_encoded(self):
        # -(insertion point) - 1, following Java rather than bisect, because
        # swap_into_sorted has to tell a hit from a miss.
        assert plain().binary_search([1, 3, 5, 7], 0, 4, 4) == -3

    def test_a_miss_past_the_end(self):
        assert plain().binary_search([1, 3, 5], 0, 3, 9) == -4

    def test_a_miss_before_the_start(self):
        assert plain().binary_search([2, 4, 6], 0, 3, 1) == -1

    def test_an_empty_range(self):
        assert plain().binary_search([1, 2, 3], 1, 1, 2) == -2


class TestSortedness:
    def test_is_sorted(self):
        assert plain().is_sorted([1, 2, 3])
        assert not plain().is_sorted([1, 3, 2])

    def test_an_empty_or_single_list_is_sorted(self):
        assert plain().is_sorted([])
        assert plain().is_sorted([1])

    def test_equal_elements_are_sorted(self):
        assert plain().is_sorted([1, 1, 1])

    def test_find_inversion(self):
        assert plain().find_inversion([1, 2, 3]) == -1
        assert plain().find_inversion([1, 3, 2]) == 2

    def test_find_inversion_over_a_range(self):
        assert plain().find_inversion([9, 1, 2, 3], 1, 4) == -1

    def test_in_sequence_counts_nothing(self):
        helper = instrumented()
        helper.in_sequence([1, 2], 1, 1)
        assert helper.get_compares() == 0
        assert helper.get_hits() == 0

    def test_inverted_and_not_inverted(self):
        helper = plain()
        assert helper.inverted(2, 1)
        assert not helper.inverted(1, 2)
        assert helper.not_inverted(1, 2)
        assert not helper.not_inverted(2, 1)

    def test_inverted_variants(self):
        helper = plain()
        xs = [2, 1]
        assert helper.inverted_at(xs, 0, 1)
        assert helper.inverted_v(xs, 2, 1)
        assert helper.inverted_w(xs, 0, 1)

    def test_not_inverted_variants(self):
        helper = plain()
        xs = [1, 2]
        assert helper.not_inverted_at(xs, 0, 1)
        assert helper.not_inverted_v(xs, 1, 1)
        assert helper.not_inverted_w(xs, 0, 2)

    def test_not_inverted_with_lookups(self):
        helper = instrumented()
        assert helper.not_inverted_with_lookups([1, 2], 0, 1, 2)
        assert helper.get_lookups() == 2


class TestCountInversions:
    def test_a_sorted_list_has_none(self):
        assert count_inversions([1, 2, 3, 4], natural_comparison) == 0

    def test_a_reversed_list_has_them_all(self):
        # n(n-1)/2 for n = 4.
        assert count_inversions([4, 3, 2, 1], natural_comparison) == 6

    def test_one_inversion(self):
        assert count_inversions([1, 3, 2], natural_comparison) == 1

    def test_it_matches_a_direct_count(self):
        xs = [5, 2, 9, 1, 7, 3]
        direct = sum(1 for i in range(len(xs)) for j in range(i + 1, len(xs)) if xs[i] > xs[j])
        assert count_inversions(xs, natural_comparison) == direct

    def test_equal_elements_are_not_inversions(self):
        assert count_inversions([1, 1, 1], natural_comparison) == 0

    def test_it_leaves_the_list_alone(self):
        xs = [3, 1, 2]
        count_inversions(xs, natural_comparison)
        assert xs == [3, 1, 2]

    def test_the_helper_reports_it(self):
        assert instrumented().inversions([3, 2, 1]) == 3

    def test_a_plain_helper_reports_zero(self):
        # Counting inversions costs more than the sort, so it is not done unless
        # the Helper is instrumented.
        assert plain().inversions([3, 2, 1]) == 0


class TestFixes:
    def test_an_exchange_fixes_at_least_the_pair(self):
        helper = instrumented(fixes="true")
        xs = [2, 1]
        helper.swap_conditional(xs, 0, 1)
        assert helper.get_fixes() == 1

    def test_an_exchange_over_a_distance_fixes_more(self):
        # Exchanging the 3 and the 1 also puts the 2 between them right, which
        # is two further inversions.
        helper = instrumented(fixes="true")
        xs = [3, 2, 1]
        helper.swap_conditional(xs, 0, 2)
        assert helper.get_fixes() == 3

    def test_fixes_are_not_counted_unless_asked_for(self):
        helper = instrumented(fixes="false")
        helper.swap_conditional([2, 1], 0, 1)
        assert helper.get_fixes() == 0

    def test_count_fixes_reports_the_setting(self):
        assert instrumented(fixes="true").count_fixes()
        assert not instrumented(fixes="false").count_fixes()


class TestPostProcess:
    def test_it_accepts_a_sorted_list(self):
        helper = instrumented()
        helper.init(3)
        helper.post_process([1, 2, 3])

    def test_it_rejects_an_unsorted_list(self):
        helper = instrumented()
        helper.init(3)
        with pytest.raises(HelperException, match="not sorted"):
            helper.post_process([1, 3, 2])

    def test_it_gathers_the_statistics(self):
        helper = instrumented()
        helper.init(2)
        helper.compare(1, 2)
        helper.post_process([1, 2])
        assert helper.get_compares() == 0, "gathering resets the counters"

    def test_a_plain_helper_checks_only_when_asked(self):
        helper = plain()
        helper.post_process([1, 3, 2])  # checksorted is not set, so no complaint

    def test_a_plain_helper_checks_when_asked(self):
        config = setup_config("false", "", "0", "0", "", "").copy("helper", "checksorted", "true")
        helper = NonInstrumentingHelper("test", config, n=3)
        with pytest.raises(HelperException, match="not sorted"):
            helper.post_process([1, 3, 2])


class TestInitAndN:
    def test_init_sets_n(self):
        helper = plain(n=0)
        helper.init(5)
        assert helper.get_n() == 5

    def test_init_twice_with_the_same_n_is_fine(self):
        helper = plain(n=0)
        helper.init(5)
        helper.init(5)
        assert helper.get_n() == 5

    def test_init_with_a_different_n_is_an_error(self):
        helper = plain(n=0)
        helper.init(5)
        with pytest.raises(HelperException, match="already set"):
            helper.init(6)

    def test_an_instrumented_helper_re_initializes_its_instrument(self):
        helper = instrumented(n=0)
        helper.init(5)
        helper.init(5)
        assert helper.get_n() == 5

    def test_an_instrumented_helper_also_rejects_a_different_n(self):
        helper = instrumented(n=0)
        helper.init(5)
        with pytest.raises(HelperException, match="already set"):
            helper.init(6)


class TestCutoff:
    def test_the_default(self):
        assert plain().cutoff() == 20

    def test_a_configured_cutoff(self):
        helper = NonInstrumentingHelper("test", setup_config("false", "", "0", "0", "7", ""))
        assert helper.cutoff() == 7

    def test_a_cutoff_below_one_falls_back_to_the_default(self):
        # A cutoff of zero would make a recursive sort recurse for ever.
        helper = NonInstrumentingHelper("test", setup_config("false", "", "0", "0", "0", ""))
        assert helper.cutoff() == 20

    def test_the_msd_cutoff(self):
        assert instrumented().msd_cutoff() == 256

    def test_the_msd_cutoff_does_not_depend_on_instrumentation(self):
        # The Java had MSDCutoff() only on the instrumented Helper, so MSD radix
        # sort cut over to quicksort at 20 for an ordinary run and at 256 when
        # measured -- the measurements described a different algorithm.
        assert plain().msd_cutoff() == instrumented().msd_cutoff() == 256

    def test_a_configured_msd_cutoff(self):
        config = setup_config("false", "", "0", "0", "", "").copy("helper", "msdcutoff", "64")
        assert NonInstrumentingHelper("test", config).msd_cutoff() == 64


class TestRandom:
    def test_it_builds_a_list_of_the_right_length(self):
        helper = plain()
        assert len(helper.random(10, lambda r: r.randint(0, 99))) == 10

    def test_zero_elements_is_an_error(self):
        with pytest.raises(HelperException, match="zero random elements"):
            plain().random(0, lambda r: r.random())

    def test_the_same_seed_gives_the_same_list(self):
        config = setup_config("false", "", "42", "0", "", "")
        first = create("a", 10, config).random(10, lambda r: r.randint(0, 999))
        second = create("b", 10, config).random(10, lambda r: r.randint(0, 999))
        assert first == second


class TestDepth:
    def test_it_remembers_the_deepest(self):
        helper = instrumented()
        helper.register_depth(3)
        helper.register_depth(7)
        helper.register_depth(5)
        assert helper.max_depth() == 7

    def test_a_plain_helper_records_nothing(self):
        helper = plain()
        helper.register_depth(7)
        assert helper.max_depth() == 0


class TestDiscriminate:
    def test_a_substring(self):
        assert discriminate_string("hello", 2) == "llo"

    def test_past_the_end_gives_a_space(self):
        # So that a short string sorts before a longer one sharing its prefix.
        assert discriminate_string("ab", 5) == " "

    def test_the_helper_discriminates_strings(self):
        helper = NonInstrumentingHelper("test", setup_config("false", "", "0", "0", "", ""))
        assert helper.discriminate("hello", 1) == "ello"

    def test_it_rejects_a_non_string(self):
        from src.sort.generic.sort_exception import SortException
        with pytest.raises(SortException):
            plain().discriminate(42, 1)

    def test_compare_substrings(self):
        helper = NonInstrumentingHelper("test", setup_config("false", "", "0", "0", "", ""))
        assert helper.compare_substrings("xabc", "xabd", 1) < 0
        assert helper.compare_substrings("xabc", "yabc", 1) == 0


class TestFactory:
    def test_it_reads_the_configuration(self):
        assert create("test", 10, setup_config("true", "", "0", "0", "", "")).instrumented()
        assert not create("test", 10, setup_config("false", "", "0", "0", "", "")).instrumented()

    def test_it_can_be_overridden(self):
        config = setup_config("false", "", "0", "0", "", "")
        assert create("test", 10, config, instrumented=True).instrumented()

    def test_it_passes_the_comparator_through(self):
        helper = create("test", 10, setup_config("false", "", "0", "0", "", ""),
                        comparator=lambda v, w: natural_comparison(w, v))
        assert helper.compare(1, 2) > 0

    def test_the_helper_knows_its_description(self):
        assert create("my sort", 10, setup_config("false", "", "0", "0", "", "")).get_description() \
            == "my sort"


class TestClone:
    def test_a_clone_has_the_new_description(self):
        assert instrumented().clone("other").get_description() == "other"

    def test_a_clone_keeps_the_comparator(self):
        helper = InstrumentedHelper("test", setup_config("true", "", "0", "0", "", ""),
                                    comparator=lambda v, w: natural_comparison(w, v))
        assert helper.clone("other").compare(1, 2) > 0

    def test_a_shared_instrumenter_pools_the_counts(self):
        # This is what makes a hybrid sort report one set of totals rather than
        # two: the cutoff sort counts into the same Instrument.
        helper = instrumented()
        clone = helper.clone("inner", share_instrumenter=True)
        clone.compare(1, 2)
        assert helper.get_compares() == 1

    def test_an_unshared_instrumenter_counts_separately(self):
        helper = instrumented()
        clone = helper.clone("inner", share_instrumenter=False)
        clone.compare(1, 2)
        assert helper.get_compares() == 0
        assert clone.get_compares() == 1

    def test_a_plain_helper_clones_to_a_plain_helper(self):
        assert not plain().clone("other").instrumented()


class TestReporting:
    def test_str(self):
        assert "Instrumenting helper for test" in str(instrumented())
        assert "Helper for test" in str(plain())

    def test_show_stats(self):
        helper = instrumented()
        helper.init(2)
        helper.compare(1, 2)
        helper.gather_statistic()
        assert "test" in helper.show_stats()

    def test_show_stats_with_a_context(self):
        helper = instrumented()
        helper.init(2)
        assert "test/merge" in helper.show_stats("merge")

    def test_a_plain_helper_shows_nothing(self):
        assert plain().show_stats() == ""

    def test_it_closes_at_the_end_of_a_with_block(self):
        with plain() as helper:
            assert helper.get_n() == 10


class TestPreProcess:
    def test_it_counts_inversions_for_the_configured_number_of_samples(self):
        # inversions = 1, so the first list is counted and the second is not.
        helper = InstrumentedHelper("test", setup_config("true", "", "0", "1", "", ""), n=3)
        helper.init(3)
        helper.pre_process([3, 2, 1])
        assert helper.get_stat_pack().get_count("inversions") == 1
        helper.pre_process([3, 2, 1])
        assert helper.get_stat_pack().get_count("inversions") == 1

    def test_it_counts_none_when_not_asked(self):
        helper = instrumented()
        helper.init(3)
        helper.pre_process([3, 2, 1])
        assert helper.get_stat_pack().get_count("inversions") == 0

    def test_it_returns_the_list(self):
        helper = instrumented()
        helper.init(3)
        xs = [3, 2, 1]
        assert helper.pre_process(xs) is xs


class TestSetupConfigFixes:
    def test_a_helper_built_from_it_counts_fixes(self):
        helper = InstrumentedHelper("test", setup_config_fixes(), n=3)
        assert helper.count_fixes()
        assert helper.cutoff() == 20
