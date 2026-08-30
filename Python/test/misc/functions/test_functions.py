"""
Tests for misc/functions: Either, Newton's method and the Lambert W function.
"""

from __future__ import annotations

import math

import pytest

from src.misc.functions.either import Either
from src.misc.functions.lambert_w import LambertException, LambertW
from src.misc.functions.newton import Newton

TOLERANCE = 1e-6


class TestEither:
    def test_right(self):
        e: Either[str, int] = Either.right(42)
        assert e.is_right() and not e.is_left()
        assert e.get_right() == 42
        assert e.get_left() is None

    def test_left(self):
        e: Either[str, int] = Either.left("no")
        assert e.is_left() and not e.is_right()
        assert e.get_left() == "no"
        assert e.get_right() is None

    def test_a_side_may_hold_none(self):
        # which is why "absent" is a sentinel rather than None
        e: Either[str, int] = Either.right(None)
        assert e.is_right()
        assert e.get_right() is None

    def test_map_collapses_to_one_value(self):
        assert Either.right(6).map(len, lambda r: r * 7) == 42
        assert Either.left("abc").map(len, lambda r: r * 7) == 3

    def test_map_left_leaves_a_right_alone(self):
        assert Either.right(42).map_left(str).get_right() == 42
        assert Either.left(42).map_left(str).get_left() == "42"

    def test_map_right_leaves_a_left_alone(self):
        assert Either.left("no").map_right(str).get_left() == "no"
        assert Either.right(42).map_right(str).get_right() == "42"

    def test_apply_runs_only_the_side_in_hand(self):
        seen = []
        Either.right(42).apply(lambda x: seen.append(("left", x)), lambda x: seen.append(("right", x)))
        Either.left("no").apply(lambda x: seen.append(("left", x)), lambda x: seen.append(("right", x)))
        assert seen == [("right", 42), ("left", "no")]

    def test_str(self):
        assert str(Either.right(42)) == "Right(42)"
        assert str(Either.left("no")) == "Left(no)"


class TestNewton:
    def test_cos_x_equals_x(self):
        # the Dottie number, the one real fixed point of cosine
        newton = Newton("cos(x) - x", lambda x: math.cos(x) - x, lambda x: -math.sin(x) - 1)
        result = newton.solve(1.0, 200, 1e-7)
        assert result.is_right()
        assert result.get_right() == pytest.approx(0.739085, abs=1e-6)

    def test_a_square_root(self):
        newton = Newton("x^2 - 2", lambda x: x * x - 2, lambda x: 2 * x)
        assert newton.solve(1.0, 200, 1e-10).get_right() == pytest.approx(math.sqrt(2))

    def test_it_reports_failure_to_converge(self):
        # a solvable problem, but not in one step from here
        newton = Newton("cos(x) - x", lambda x: math.cos(x) - x, lambda x: -math.sin(x) - 1)
        result = newton.solve(1.0, 1, 1e-7)
        assert result.is_left()
        assert "did not converge" in result.get_left()
        assert "cos(x) - x" in result.get_left(), "the message names the equation"
        assert newton.solve(1.0, 200, 1e-7).is_right(), "given enough tries it gets there"

    def test_it_reports_a_zero_derivative(self):
        # starting at the turning point divides by zero on the first step
        newton = Newton("x^2 - 2", lambda x: x * x - 2, lambda x: 2 * x)
        result = newton.solve(0.0, 20, 1e-7)
        assert result.is_left()
        assert "Exception thrown" in result.get_left()

    def test_no_tries_at_all(self):
        newton = Newton("x", lambda x: x, lambda x: 1.0)
        assert newton.solve(0.0, 0, 1e-7).is_left()


class TestLambertW:
    """
    W is the inverse of x -> x e^x, so the test of any value it returns is that
    feeding it back through x e^x gives what we started with.
    """

    def test_branch_zero_below_its_domain(self):
        with pytest.raises(LambertException):
            LambertW().w(0, -1, TOLERANCE)

    def test_branch_minus_one_above_its_domain(self):
        with pytest.raises(LambertException):
            LambertW().w(-1, 0.001, TOLERANCE)

    def test_an_unreal_branch(self):
        with pytest.raises(LambertException):
            LambertW().w(1, 1.0, TOLERANCE)

    def test_branch_zero(self):
        w = LambertW()
        assert w.w(0, -1 / math.e, TOLERANCE) == pytest.approx(-1, abs=5e-3)
        assert w.w(0, 0, TOLERANCE) == pytest.approx(0, abs=1e-8)
        assert w.w(0, 0.99, TOLERANCE) == pytest.approx(0.56351356, abs=1e-7)
        assert w.w(0, 1, TOLERANCE) == pytest.approx(0.56714329, abs=1e-7)
        assert w.w(0, math.e, TOLERANCE) == pytest.approx(1, abs=1e-7)
        assert w.w(0, 2 * math.log(2), TOLERANCE) == pytest.approx(math.log(2), abs=1e-7)
        assert w.w(0, math.exp(math.e + 1), TOLERANCE) == pytest.approx(math.e, abs=1e-7)

    def test_branch_minus_one(self):
        w = LambertW()
        assert w.w(-1, -1 / math.e, TOLERANCE) == pytest.approx(-1, abs=5e-3)
        assert w.w(-1, -0.1, TOLERANCE) == pytest.approx(-3.6, abs=3e-2)

    def test_it_really_is_the_inverse(self):
        w = LambertW()
        for z in (0.1, 0.5, 1.0, 2.0, 10.0, math.e):
            result = w.w(0, z, TOLERANCE)
            assert result * math.exp(result) == pytest.approx(z, abs=1e-5)
        for z in (-0.05, -0.1, -0.2, -0.3):
            result = w.w(-1, z, TOLERANCE)
            assert result * math.exp(result) == pytest.approx(z, abs=1e-5)

    def test_the_merge_sort_cutoff(self):
        # the solutions of x - 4 log2(x) = 0, which is where the cutoff from merge
        # sort to insertion sort comes from. The larger root is 16.
        z = -math.log(2) / 4
        ws = LambertW().w_branches(z, TOLERANCE)
        assert len(ws) == 2, "both real branches are defined here"
        assert ws[0] / z == pytest.approx(1.23962773, abs=1e-6)
        assert ws[1] / z == pytest.approx(16, abs=1e-6)

    def test_only_branch_zero_where_z_is_positive(self):
        ws = LambertW().w_branches(0.5, TOLERANCE)
        assert len(ws) == 1
        assert ws[0] == pytest.approx(0.3517337, abs=1e-6)

    def test_branch_minus_one_is_undefined_at_zero(self):
        # NOTE the Java admits x = 0 to the domain and then fails to converge from
        # an estimate of -Infinity, raising RuntimeException where the caller is
        # watching for LambertException -- so its W(0.0, tolerance) blows up rather
        # than reporting the one branch that does exist there.
        with pytest.raises(LambertException):
            LambertW().w(-1, 0.0, TOLERANCE)
        assert LambertW().w_branches(0.0, TOLERANCE) == [pytest.approx(0.0, abs=1e-8)]
