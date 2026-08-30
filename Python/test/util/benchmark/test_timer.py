import pytest

from src.util.benchmark.timer import Timer, TimerException, progress_function

# These mirror TimerTest.java.
#
# NOTE Timer carries three exercises -- _do_repeat_for_iteration, _get_clock and
# _to_millisecs -- so until those are written, constructing a Timer raises
# NotImplementedError and conftest.py reports every test here as skipped rather
# than failed. That is the intent: an unwritten exercise is grey, not red.


def swallow(_: str) -> None:
    """A progress function that shows nothing, so tests do not print."""


class TestTimer:
    def test_construction_starts_it_running(self):
        assert Timer(swallow)._is_running()

    def test_lap_increments_the_counter(self):
        timer = Timer(swallow)
        timer.lap()
        assert timer._get_laps() == 1

    def test_lap_when_not_running_raises(self):
        timer = Timer(swallow)
        timer.pause()
        with pytest.raises(TimerException):
            timer.lap()

    def test_pause_leaves_the_lap_counter_alone(self):
        timer = Timer(swallow)
        timer.pause()
        assert timer._get_laps() == 0
        assert not timer._is_running()

    def test_pause_and_lap_increments_the_lap_counter(self):
        timer = Timer(swallow)
        timer.pause_and_lap()
        assert timer._get_laps() == 1
        assert not timer._is_running()

    def test_resume_when_already_running_raises(self):
        with pytest.raises(TimerException):
            Timer(swallow).resume()

    def test_pause_then_resume(self):
        timer = Timer(swallow)
        timer.pause()
        timer.resume()
        assert timer._is_running()

    def test_millisecs_while_running_raises(self):
        with pytest.raises(TimerException):
            Timer(swallow).millisecs()

    def test_mean_lap_time_while_running_raises(self):
        with pytest.raises(TimerException):
            Timer(swallow).mean_lap_time()

    def test_millisecs_is_not_negative(self):
        timer = Timer(swallow)
        timer.pause()
        assert timer.millisecs() >= 0

    def test_stop_returns_the_mean_lap_time(self):
        timer = Timer(swallow)
        result = timer.stop()
        assert result >= 0
        assert not timer._is_running()

    def test_repeat_simple_counts_every_run(self):
        timer = Timer(swallow)
        calls = []
        timer.repeat_simple(10, lambda: calls.append(1))
        assert len(calls) == 10
        assert timer._get_laps() == 10

    def test_repeat_simple_leaves_it_running(self):
        timer = Timer(swallow)
        timer.repeat_simple(2, lambda: None)
        assert timer._is_running()

    def test_repeat_runs_the_supplier_and_the_function(self):
        timer = Timer(swallow)
        supplied, applied = [], []
        timer.repeat(5, lambda: supplied.append(1) or len(supplied), lambda t: applied.append(t))
        assert len(supplied) == 5
        assert len(applied) == 5

    def test_repeat_runs_the_pre_and_post_functions(self):
        timer = Timer(swallow)
        pre, post = [], []

        def pre_function(t):
            pre.append(t)
            return t * 2

        timer.repeat(4, lambda: 3, lambda t: t + 1, pre_function, post.append)
        assert pre == [3, 3, 3, 3]
        assert post == [7, 7, 7, 7], "the pre-function's result should reach the function"

    def test_repeat_leaves_it_running(self):
        timer = Timer(swallow)
        timer.repeat(2, lambda: 1, lambda t: t)
        assert timer._is_running()

    def test_repeat_counts_one_lap_per_run(self):
        timer = Timer(swallow)
        timer.repeat(6, lambda: 1, lambda t: t)
        assert timer._get_laps() == 6

    def test_str(self):
        timer = Timer(swallow)
        timer.pause()
        assert str(timer).startswith("Timer{ticks=")
        assert "laps=0" in str(timer)
        assert "running=false" in str(timer)


class TestProgress:
    def test_progress_function_when_off_shows_nothing(self, capsys):
        progress_function(False)("hello")
        assert capsys.readouterr().out == ""

    def test_progress_function_when_on_prints_without_a_newline(self, capsys):
        progress_function(True)("hello")
        assert capsys.readouterr().out == "hello"

    def test_repeat_reports_progress(self):
        shown = []
        timer = Timer(shown.append)
        timer.repeat(20, lambda: 1, lambda t: t)
        assert shown, "repeat should report progress through the show_progress function"
        assert shown[-1] == "\r", "repeat should end by returning to the start of the line"

    def test_warmup_shows_w(self):
        shown = []
        timer = Timer(shown.append)
        timer.repeat(3, lambda: 1, lambda t: t, warmup=True)
        assert "W" in shown


class TestDoPrintStatus:
    """_do_print_status is not an exercise, so these run even before Timer is written."""

    def test_a_multiple_of_ten_counts_down(self):
        shown = []
        timer = _timer_without_a_clock(shown)
        assert timer._do_print_status(-1, 30) == 30
        assert shown == ["7"]

    def test_anything_else_is_a_dot(self):
        shown = []
        timer = _timer_without_a_clock(shown)
        timer._do_print_status(-1, 34)
        assert shown == ["."]

    def test_an_unchanged_value_shows_nothing(self):
        shown = []
        timer = _timer_without_a_clock(shown)
        timer._do_print_status(30, 30)
        assert shown == []


def _timer_without_a_clock(shown: list) -> Timer:
    """
    Build a Timer without running its constructor, which would read the clock.

    This lets the _do_print_status tests run whether or not the clock exercises
    have been written.
    """
    timer = Timer.__new__(Timer)
    timer._show_progress = shown.append
    return timer


def test_mean_lap_time_with_no_laps():
    # Zero laps have no mean, so this raises rather than returning Infinity --
    # which is >= 0, and would satisfy a test that only checked the sign.
    with pytest.raises(TimerException, match="no laps"):
        Timer(lambda s: None).repeat(0, lambda: 1, lambda x: x)
