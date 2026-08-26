"""
The Helper abstraction, ported from sort/helper/Helper.java.

A Helper is what a sort uses to touch the list it is sorting: every comparison,
every swap, every read and write goes through it. That indirection is the whole
trick -- an instrumented Helper counts the work while a plain one does not, and
the sort itself is written once either way.

The Java splits this across ComparableHelper and NonComparableHelper, because it
cannot treat `X extends Comparable<X>` and `Comparator<X>` uniformly. Python
can: a Helper takes an optional comparison function, and falls back to `<` when
there is none. The six Comparable/Comparator twins therefore become three
classes here.

Java also overloads heavily -- four ways to swap, four to compare, four to swap
conditionally. Python cannot overload, and the variants cannot be told apart at
run time either, because an element of the list may itself be an int. They are
given distinct names, following one convention throughout:

  - a bare name takes indices, and reads the list itself;
  - a _v suffix means the caller already holds the value of xs[i];
  - a _w suffix means the caller already holds the value of xs[j];
  - a _vw suffix means the caller holds both.

That is not cosmetic. Each value passed in is one array access that does not
have to be made, and the hit counts differ accordingly.
"""

from __future__ import annotations

from abc import abstractmethod
from collections.abc import Callable
from typing import Generic, TypeVar

from src.sort.generic.sort_exception import SortException
from src.sort.helper.instrument import Instrument

X = TypeVar("X")

#: Distinguishes "no value supplied" from a value that happens to be falsy or
#: None. A sentinel rather than None, because None could in principle be an
#: element.
_UNSET = object()

#: The cutoff below which a linearithmic sort hands over to insertion sort.
CUTOFF_DEFAULT = 20


def natural_comparison(v: X, w: X) -> int:
    """
    Compare two values using their own ordering.

    This is what a Helper uses when no comparator is supplied, and it stands in
    for Java's `X extends Comparable<X>`.

    :param v: the first value.
    :param w: the second value.
    :return: -1, 0 or 1 as v is less than, equal to, or greater than w.
    """
    if v < w:
        return -1
    if w < v:
        return 1
    return 0


class Helper(Instrument, Generic[X]):
    """
    Everything a sort needs in order to touch the list it is sorting.

    The methods here do no counting. The counting versions are in
    InstrumentedHelper, which overrides them.
    """

    # ---- the parts a concrete Helper must supply -------------------------

    @abstractmethod
    def pure_comparison(self, v: X, w: X) -> int:
        """
        Compare two values and do absolutely nothing else -- in particular, do
        not count the comparison.

        :param v: the first value.
        :param w: the second value.
        :return: -1, 0 or 1 as appropriate.
        """

    @abstractmethod
    def get_comparator(self) -> Callable[[X, X], int]:
        """
        :return: the comparison function this Helper orders by.
        """

    @abstractmethod
    def get_description(self) -> str:
        """:return: a description of this Helper, used when reporting results."""

    @abstractmethod
    def get_config(self):
        """:return: the Config this Helper was built from."""

    @abstractmethod
    def init(self, n: int) -> None:
        """
        :param n: the number of elements to be managed.
        :raises HelperException: if n is inconsistent with a previous call.
        """

    @abstractmethod
    def get_n(self) -> int:
        """:return: the number of elements being managed."""

    @abstractmethod
    def close(self) -> None:
        """Finish with this Helper, reporting statistics if there are any."""

    @abstractmethod
    def random(self, m: int, f: Callable) -> list[X]:
        """
        Build a list of m random elements.

        NOTE the Java also takes a Class, because it has to reflect an array
        into existence. Python does not.

        :param m: the number of elements.
        :param f: builds one element from a Random.
        :return: the list.
        """

    @abstractmethod
    def clone(self, description: str, n: int | None = None,
              comparator: Callable[[X, X], int] | None = None,
              share_instrumenter: bool = False) -> Helper[X]:
        """
        Make a Helper like this one.

        The Java has three overloads; they differ only in which arguments are
        defaulted, so here they are optional parameters.

        :param description: the description for the new Helper.
        :param n: the number of elements, defaulting to this Helper's.
        :param comparator: the comparison function, defaulting to this one's.
        :param share_instrumenter: if true, the clone counts into the same
                                   Instrument, so a hybrid sort reports one set
                                   of totals rather than two.
        :return: the new Helper.
        """

    def instrumented(self) -> bool:
        """
        :return: true if this Helper counts what it does.
        """
        return False

    # ---- reading and writing --------------------------------------------

    def get(self, xs: list[X], i: int) -> X:
        """
        Read xs[i]. This is the one place a sort should read from, so that the
        access can be counted.

        :param xs: the list.
        :param i: the index.
        :return: the value at i.
        """
        return xs[i]

    def set(self, xs: list[X], i: int, x: X) -> None:
        """
        Write x to xs[i].

        :param xs: the list.
        :param i: the index.
        :param x: the value.
        """
        xs[i] = x

    def copy_array(self, a: list[X]) -> list[X]:
        """
        :param a: the list to copy.
        :return: a new list with the same elements.
        """
        return list(a)

    def copy(self, x: X, target: list[X], j: int) -> None:
        """
        Copy a value into target[j].

        :param x: the value.
        :param target: the destination list.
        :param j: the destination index.
        """
        target[j] = x

    def copy_at(self, source: list[X], i: int, target: list[X], j: int) -> None:
        """
        Copy source[i] into target[j].

        :param source: the source list.
        :param i: the source index.
        :param target: the destination list.
        :param j: the destination index.
        """
        self.copy(self.get(source, i), target, j)

    def copy_block(self, source: list[X], i: int, target: list[X], j: int, n: int) -> None:
        """
        Copy n elements from source[i:] into target[j:].

        NOTE this must behave correctly when source is target and the ranges
        overlap, which is exactly how swap_into uses it. Taking a slice of the
        source first is what makes that safe.

        :param source: the source list.
        :param i: the first source index.
        :param target: the destination list.
        :param j: the first destination index.
        :param n: the number of elements.
        """
        target[j:j + n] = source[i:i + n]

    def distribute_block(self, source: list[X], from_: int, to: int, target: list[X],
                         f: Callable[[X], int]) -> None:
        """
        Move each element of source[from_:to] to the place in target that f
        chooses for it. This is what a counting sort does once it knows where
        each bucket begins.

        :param source: the source list.
        :param from_: the first source index.
        :param to: one past the last source index.
        :param target: the destination list.
        :param f: chooses the destination index for a value.
        """
        for i in range(from_, to):
            value = source[i]
            target[f(value)] = value

    # ---- swapping --------------------------------------------------------

    def swap(self, xs: list[X], i: int, j: int) -> None:
        """
        Exchange xs[i] and xs[j], reading both.

        :param xs: the list.
        :param i: the lower index.
        :param j: the higher index.
        """
        x = xs[j]
        xs[j] = xs[i]
        xs[i] = x

    def swap_v(self, v: X, xs: list[X], i: int, j: int) -> None:
        """
        Exchange xs[i] and xs[j] when the caller already holds xs[i].

        :param v: the value of xs[i].
        :param xs: the list.
        :param i: the lower index.
        :param j: the higher index.
        """
        xs[i] = xs[j]
        xs[j] = v

    def swap_w(self, w: X, xs: list[X], i: int, j: int) -> None:
        """
        Exchange xs[i] and xs[j] when the caller already holds xs[j].

        :param w: the value of xs[j].
        :param xs: the list.
        :param i: the lower index.
        :param j: the higher index.
        """
        xs[j] = xs[i]
        xs[i] = w

    def swap_vw(self, v: X, w: X, xs: list[X], i: int, j: int) -> None:
        """
        Exchange xs[i] and xs[j] when the caller already holds both values, so
        that neither has to be read.

        :param v: the value of xs[i].
        :param w: the value of xs[j].
        :param xs: the list.
        :param i: the lower index.
        :param j: the higher index.
        """
        xs[j] = v
        xs[i] = w

    def swap_stable(self, xs: list[X], i: int) -> None:
        """
        Exchange the adjacent elements xs[i-1] and xs[i].

        :param xs: the list.
        :param i: the higher of the two indices.
        """
        self.swap(xs, i - 1, i)

    def swap_into(self, xs: list[X], i: int, j: int, x: X = _UNSET) -> None:
        """
        Move xs[j] down to index i, shifting xs[i:j] up one place.

        This is the half-exchange that insertion sort uses: one move rather than
        a run of swaps.

        :param xs: the list.
        :param i: the destination index.
        :param j: the index of the element to move.
        :param x: the value of xs[j], if the caller already holds it.
        """
        if x is _UNSET:
            x = self.get(xs, j)
        if j > i:
            self.copy_block(xs, i, xs, i + 1, j - i)
            xs[i] = x

    def swap_into_sorted(self, xs: list[X], from_: int, i: int) -> None:
        """
        Move xs[i] down into its place within the sorted run xs[from_:i].

        :param xs: the list, whose elements from_ through i-1 must be sorted.
        :param from_: the first index of the sorted run.
        :param i: the index of the element to insert.
        """
        x = self.get(xs, i)
        j = self.binary_search(xs, from_, i, x)
        # NOTE binary_search returns -(insertion point) - 1 when it does not find
        # the value, so the insertion point is -j - 1 regardless of from_. The
        # Java read "from - j - 1", which is the same thing only when from is
        # zero: sorting a sub-range starting anywhere else came out reversed.
        if j < 0:
            j = -j - 1
        if j < i:
            self.swap_into(xs, j, i, x)

    # ---- comparing -------------------------------------------------------

    def compare(self, v: X, w: X) -> int:
        """
        Compare two values.

        :param v: the first value.
        :param w: the second value.
        :return: -1, 0 or 1 as appropriate.
        """
        return self.pure_comparison(v, w)

    def compare_at(self, xs: list[X], i: int, j: int) -> int:
        """
        Compare xs[i] with xs[j], reading both.

        :param xs: the list.
        :param i: the first index.
        :param j: the second index.
        :return: -1, 0 or 1 as appropriate.
        """
        return self.compare(xs[i], xs[j])

    def compare_v(self, xs: list[X], v: X, j: int) -> int:
        """
        Compare a value the caller holds with xs[j].

        :param xs: the list.
        :param v: the first value.
        :param j: the index of the second value.
        :return: -1, 0 or 1 as appropriate.
        """
        return self.compare(v, xs[j])

    def compare_w(self, xs: list[X], i: int, w: X) -> int:
        """
        Compare xs[i] with a value the caller holds.

        :param xs: the list.
        :param i: the index of the first value.
        :param w: the second value.
        :return: -1, 0 or 1 as appropriate.
        """
        return self.compare(xs[i], w)

    def compare_with_lookups(self, xs: list[X], i: int, j: int, lookups: int) -> int:
        """
        Compare xs[i] with xs[j], counting the given number of lookups.

        :param xs: the list.
        :param i: the first index.
        :param j: the second index.
        :param lookups: the number of lookups to count; 0, 1 or 2.
        :return: -1, 0 or 1 as appropriate.
        """
        assert 0 <= lookups <= 2
        self.increment_lookups(lookups)
        return self.compare_at(xs, i, j)

    def lookup(self, x: X) -> X:
        """
        Fetch a value from the heap. This does nothing except count the lookup,
        but the count is the point: it is what distinguishes holding a reference
        from having dereferenced it.

        :param x: the value.
        :return: the same value.
        """
        self.increment_lookups(1)
        return x

    # ---- inversions ------------------------------------------------------

    def inverted(self, v: X, w: X) -> bool:
        """
        :param v: the first value.
        :param w: the second value.
        :return: true if v is greater than w, that is, the pair is out of order.
        """
        return self.compare(v, w) > 0

    def inverted_at(self, xs: list[X], i: int, j: int) -> bool:
        """
        :param xs: the list.
        :param i: the first index.
        :param j: the second index.
        :return: true if xs[i] is greater than xs[j].
        """
        return self.compare_at(xs, i, j) > 0

    def inverted_v(self, xs: list[X], v: X, j: int) -> bool:
        """
        :param xs: the list.
        :param v: the first value, which the caller holds.
        :param j: the index of the second value.
        :return: true if v is greater than xs[j].
        """
        return self.compare(v, xs[j]) > 0

    def inverted_w(self, xs: list[X], i: int, w: X) -> bool:
        """
        :param xs: the list.
        :param i: the index of the first value.
        :param w: the second value, which the caller holds.
        :return: true if xs[i] is greater than w.
        """
        return self.compare(xs[i], w) > 0

    def not_inverted(self, v: X, w: X) -> bool:
        """
        :param v: the first value.
        :param w: the second value.
        :return: true if v is less than w.
        """
        return self.compare(v, w) < 0

    def not_inverted_at(self, xs: list[X], i: int, j: int) -> bool:
        """
        :param xs: the list.
        :param i: the first index.
        :param j: the second index.
        :return: true if xs[i] is less than xs[j].
        """
        return self.not_inverted_v(xs, xs[i], j)

    def not_inverted_v(self, xs: list[X], v: X, j: int) -> bool:
        """
        :param xs: the list.
        :param v: the first value, which the caller holds.
        :param j: the index of the second value.
        :return: true if v is less than xs[j].
        """
        return self.not_inverted(v, xs[j])

    def not_inverted_w(self, xs: list[X], i: int, w: X) -> bool:
        """
        :param xs: the list.
        :param i: the index of the first value.
        :param w: the second value, which the caller holds.
        :return: true if xs[i] is less than w.
        """
        return self.not_inverted(xs[i], w)

    def not_inverted_with_lookups(self, xs: list[X], i: int, j: int, lookups: int) -> bool:
        """
        :param xs: the list.
        :param i: the first index.
        :param j: the second index.
        :param lookups: the number of lookups to count.
        :return: true if xs[i] is less than xs[j].
        """
        return self.not_inverted_v(xs, xs[i], j)

    def in_sequence(self, xs: list[X], x: X, i: int) -> X | None:
        """
        Check that x is not greater than xs[i].

        NOTE this affects no statistics. It is not an equivalent of compare or
        inverted, and is deliberately kept out of the counting so that checking
        whether a list is sorted does not show up as work the sort did.

        :param xs: the list.
        :param x: the left-hand value, which should be the smaller.
        :param i: the index of the right-hand value.
        :return: xs[i] if x <= xs[i], otherwise None.
        """
        x1 = xs[i]
        return x1 if self.pure_comparison(x, x1) <= 0 else None

    def find_inversion(self, xs: list[X], from_: int = 0, to: int | None = None) -> int:
        """
        Find the first place where the list goes backwards.

        :param xs: the list.
        :param from_: the first index to look at.
        :param to: one past the last index, defaulting to the end.
        :return: the index of the offending element, or -1 if there is none.
        """
        if to is None:
            to = len(xs)
        if to - from_ < 1:
            return -1
        x = xs[from_]
        for i in range(from_ + 1, to):
            x = self.in_sequence(xs, x, i)
            if x is None:
                return i
        return -1

    def is_sorted(self, xs: list[X], from_: int = 0, to: int | None = None) -> bool:
        """
        :param xs: the list.
        :param from_: the first index to check.
        :param to: one past the last index, defaulting to the end.
        :return: true if the list has no inversions.
        """
        if to is None:
            to = len(xs)
            if len(xs) < 2:
                return True
        return self.find_inversion(xs, from_, to) == -1

    def inversions(self, xs: list[X]) -> int:
        """
        :param xs: the list.
        :return: the number of inversions. Zero unless this Helper counts them,
                 because counting them is far more work than the sort itself.
        """
        return 0

    # ---- conditional swaps ----------------------------------------------

    def swap_conditional(self, xs: list[X], i: int, j: int) -> bool:
        """
        Exchange xs[i] and xs[j], but only if they are out of order.

        :param xs: the list.
        :param i: the lower index.
        :param j: the upper index.
        :return: true if there was an inversion, and it was fixed.
        """
        if i == j:
            return False
        return self.swap_conditional_v(xs, xs[i], i, j)

    def swap_conditional_w(self, xs: list[X], i: int, j: int, w: X) -> bool:
        """
        As swap_conditional, when the caller already holds xs[j].

        :param xs: the list.
        :param i: the lower index.
        :param j: the upper index.
        :param w: the value of xs[j].
        :return: true if there was an inversion, and it was fixed.
        """
        return self.swap_conditional_vw(xs, xs[i], i, j, w)

    def swap_conditional_v(self, xs: list[X], v: X, i: int, j: int) -> bool:
        """
        As swap_conditional, when the caller already holds xs[i].

        :param xs: the list.
        :param v: the value of xs[i].
        :param i: the lower index.
        :param j: the upper index.
        :return: true if there was an inversion, and it was fixed.
        """
        if i == j:
            return False
        return self.swap_conditional_vw(xs, v, i, j, xs[j])

    def swap_conditional_vw(self, xs: list[X], v: X, i: int, j: int, w: X) -> bool:
        """
        As swap_conditional, when the caller already holds both values.

        :param xs: the list.
        :param v: the value of xs[i].
        :param i: the lower index.
        :param j: the upper index.
        :param w: the value of xs[j].
        :return: true if there was an inversion, and it was fixed.
        """
        if i == j:
            return False
        if i > j:
            return self.swap_conditional_vw(xs, w, j, i, v)
        exchange = self.compare(v, w) > 0
        if exchange:
            self.swap_vw(v, w, xs, i, j)
        return exchange

    def swap_stable_conditional(self, xs: list[X], i: int) -> bool:
        """
        Exchange xs[i-1] and xs[i], but only if they are out of order.

        :param xs: the list.
        :param i: the upper index.
        :return: true if there was an inversion, and it was fixed.
        """
        return self.swap_conditional(xs, i - 1, i)

    def fix_inversion(self, xs: list[X], i: int, j: int | None = None) -> None:
        """
        Fix an inversion between two elements, or between xs[i-1] and xs[i] when
        j is not given.

        :param xs: the list.
        :param i: the lower index, or the upper one of an adjacent pair.
        :param j: the upper index, if there is one.
        """
        if j is None:
            self.swap_stable_conditional(xs, i)
        else:
            self.swap_conditional(xs, i, j)

    def sort_pair(self, xs: list[X], from_: int, to: int) -> bool:
        """
        Sort two adjacent elements. The caller must ensure to - from_ == 2.

        :param xs: the list.
        :param from_: the index of the first element.
        :param to: one past the index of the second.
        :return: true if they had to be exchanged.
        """
        if to == from_ + 2:
            return self.swap_conditional(xs, from_, to - 1)
        return False

    def sort_trio(self, xs: list[X], from_: int, to: int) -> None:
        """
        Sort three adjacent elements. The caller must ensure to - from_ == 3.

        :param xs: the list.
        :param from_: the index of the first element.
        :param to: one past the index of the third.
        """
        if to != from_ + 3:
            return
        from_1 = from_ + 1
        x_from = self.get(xs, from_)
        x_from1 = self.get(xs, from_1)
        swapped_xy = self.swap_conditional_vw(xs, self.lookup(x_from), from_, from_1,
                                              self.lookup(x_from1))
        if swapped_xy:
            x_from = xs[from_]
            x_from1 = xs[from_1]
        from_2 = from_ + 2
        x_from2 = self.get(xs, from_2)
        swapped_yz = self.swap_conditional_vw(xs, x_from1, from_1, from_2, self.lookup(x_from2))
        if not swapped_xy and not swapped_yz:
            return
        if swapped_yz:
            self.swap_conditional_vw(xs, x_from, from_, from_1, xs[from_1])
        else:
            self.swap_conditional_vw(xs, x_from, from_, from_2, xs[from_2])

    # ---- searching -------------------------------------------------------

    def binary_search(self, xs: list[X], from_: int, to: int, x: X) -> int:
        """
        Find x within the sorted run xs[from_:to].

        NOTE the return value follows Java's Arrays.binarySearch rather than
        Python's bisect: a match gives its index, and a miss gives
        -(insertion point) - 1. swap_into_sorted depends on being able to tell
        the two apart, which a bare insertion point cannot do.

        :param xs: the list, which must be sorted over the given range.
        :param from_: the first index to search.
        :param to: one past the last index to search.
        :param x: the value to look for.
        :return: the index of x, or -(insertion point) - 1.
        """
        low = from_
        high = to - 1
        while low <= high:
            mid = (low + high) >> 1
            cmp = self.pure_comparison(xs[mid], x)
            if cmp < 0:
                low = mid + 1
            elif cmp > 0:
                high = mid - 1
            else:
                return mid
        return -(low + 1)

    # ---- discrimination, for radix sorts ---------------------------------

    def discriminate(self, x: X, d: int) -> X:
        """
        Take the part of x that matters at depth d. For a string, that is the
        substring starting at index d.

        :param x: the value.
        :param d: the index of the first significant character.
        :return: the discriminated value.
        :raises SortException: if x is not a string.
        """
        if isinstance(x, str):
            return discriminate_string(x, d)
        raise SortException(f"discriminate not defined for {type(x).__name__}")

    def compare_substrings(self, x1: X, x2: X, d: int) -> int:
        """
        Compare two values from depth d onwards.

        :param x1: the first value.
        :param x2: the second value.
        :param d: the depth.
        :return: -1, 0 or 1 as appropriate.
        """
        return self.compare(self.discriminate(x1, d), self.discriminate(x2, d))

    # ---- the rest --------------------------------------------------------

    def pre_process(self, xs: list[X]) -> list[X]:
        """
        :param xs: the list about to be sorted.
        :return: the list to sort.
        """
        return xs

    def post_process(self, xs: list[X]) -> None:
        """
        :param xs: the sorted list.
        """

    def cutoff(self) -> int:
        """
        :return: the size below which a recursive sort hands over to insertion
                 sort.
        """
        return CUTOFF_DEFAULT

    def msd_cutoff(self) -> int:
        """
        :return: the cutoff used by MSD radix sort.
        """
        return CUTOFF_DEFAULT

    def register_depth(self, depth: int) -> None:
        """
        :param depth: a recursion depth reached by the sort.
        """

    def max_depth(self) -> int:
        """
        :return: the deepest recursion reached.
        """
        return 0

    def show_stats(self, context: str | None = None) -> str:
        """
        :param context: what the statistics describe, if anything.
        :return: the statistics, or the empty string if none were gathered.
        """
        return ""

    def __enter__(self) -> Helper[X]:
        return self

    def __exit__(self, exc_type: object, exc_value: object, traceback: object) -> None:
        self.close()


def discriminate_string(x: str, d: int) -> str:
    """
    Take the substring of x starting at d.

    :param x: the string.
    :param d: the index of the first significant character.
    :return: the substring, or a single space if d is past the end -- so that a
             short string sorts before a longer one with the same prefix.
    """
    return x[d:] if d < len(x) else " "
