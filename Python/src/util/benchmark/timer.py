"""
Timing the running of functions, ported from util/benchmark/Timer.java.

A Timer is running from the moment it is constructed. It accumulates ticks while
running and counts "laps" (repetitions), so that the mean time for one
repetition can be reported.

NOTE this class carries three exercises, at the same three places as the Java:
``_do_repeat_for_iteration``, ``_get_clock`` and ``_to_millisecs``. Until they
are written, constructing a Timer raises, because the constructor resumes the
timer and resuming reads the clock.
"""

from __future__ import annotations

from collections.abc import Callable
from typing import TYPE_CHECKING, Any

from src.util.logging.lazy_logger import LazyLogger

if TYPE_CHECKING:
    from src.util.config.config import Config

logger = LazyLogger(__name__)


class TimerException(RuntimeError):
    """
    Raised when a Timer is asked to do something its state does not allow:
    stopping one that is not running, or resuming one that already is.
    """


class Timer:
    """
    A timer which can be paused and resumed, and which counts laps.
    """

    def __init__(self, show_progress: Callable[[str], None]) -> None:
        """
        Construct a Timer and start it running.

        :param show_progress: consumes progress messages, for display or
                              logging. Use ``Timer.from_config`` to take this
                              from configuration instead.
        """
        self._ticks = 0
        self._laps = 0
        self._running = False
        self._show_progress = show_progress
        self._do_trace(lambda: "create new timer")
        self.resume()

    @classmethod
    def from_config(cls, config: Config) -> Timer:
        """
        Construct a Timer whose progress display comes from configuration, via
        the boolean timer.showprogress.

        :param config: the configuration.
        :return: a new, running, Timer.
        """
        return cls(progress_function(config.get_boolean("timer", "showprogress")))

    def repeat_simple(self, n: int, function: Callable[[], Any]) -> float:
        """
        Run function n times, once per lap, and return the mean lap time.

        The clock is running when this is called and still running when it
        returns. This is the simplest form of repeat; it corresponds to the Java
        overload ``repeat(int, Supplier<T>)``.

        :param n: the number of repetitions.
        :param function: the function to run.
        :return: the average milliseconds per repetition.
        """
        for _ in range(n):
            function()
            self.lap()
        self.pause()
        result = self.mean_lap_time()
        self.resume()
        return result

    def repeat(self, n: int, supplier: Callable[[], Any], function: Callable[[Any], Any],
               pre_function: Callable[[Any], Any] | None = None,
               post_function: Callable[[Any], None] | None = None,
               warmup: bool = False) -> float:
        """
        Pause without counting a lap, then run supplier and function n times --
        once per lap -- and return the mean lap time.

        Only ``function`` is timed. The supplier, pre_function and post_function
        run with the clock paused, so the cost of preparing an input and of
        checking a result does not land in the measurement.

        This corresponds to both remaining Java overloads: calling it without
        pre_function, post_function or warmup is ``repeat(int, Supplier,
        Function)``.

        :param n: the number of repetitions.
        :param supplier: supplies a fresh value for each repetition.
        :param function: the function to time.
        :param pre_function: pre-processes the supplied value, untimed (may be
                             None). Its result is passed to function.
        :param post_function: consumes the result of function, untimed (may be
                              None).
        :param warmup: true if this is the warmup phase.
        :return: the average milliseconds per repetition.
        """
        # NOTE the timer is running when this is called and is still running
        # when it returns.
        self.pause()
        self._do_trace(lambda: f"repeat: with {n} runs")
        self._do_trace_if(warmup, lambda: "warmup")
        lastx = -1
        for i in range(n):
            lastx = self._do_repeat_for_iteration(n, warmup, supplier, function, pre_function, post_function, lastx, i)
        result = self.mean_lap_time()
        self._show_progress("\r")
        self.resume()
        return result

    def stop(self) -> float:
        """
        Stop this Timer and return the mean lap time in milliseconds.

        :return: the average milliseconds used by each lap.
        :raises TimerException: if this Timer is not running.
        """
        self.pause_and_lap()
        self._do_trace(lambda: "stop timer")
        return self.mean_lap_time()

    def mean_lap_time(self) -> float:
        """
        :return: the average milliseconds used by each lap.
        :raises TimerException: if this Timer is running, or if no lap was
                                recorded -- zero laps have no mean.
        """
        if self._running:
            raise TimerException()
        # NOTE without this guard the Java gave ticks/0, which is Infinity, as the
        # answer to "how long did each run take"; Python would raise
        # ZeroDivisionError, which is at least loud but says nothing useful.
        # repeat(0, ...) reaches it. The Java's test accepted the Infinity,
        # because it asserted only that the time was >= 0.
        if self._laps <= 0:
            raise TimerException("mean_lap_time: no laps were recorded")
        return _to_millisecs(self._ticks) / self._laps

    def pause_and_lap(self) -> None:
        """
        Pause this timer at the end of a lap, incrementing the lap counter.

        :raises TimerException: if this Timer is not running.
        """
        self.lap()
        self._ticks += _get_clock()
        self._running = False
        self._do_trace(lambda: f"pause timer and lap after millisecs: {self._ticks * 1.0E-6}")

    def resume(self) -> None:
        """
        Resume this timer to begin a new lap.

        :raises TimerException: if this Timer is already running.
        """
        if self._running:
            raise TimerException()
        self._ticks -= _get_clock()
        self._do_trace(lambda: "resume timer")
        self._running = True

    def lap(self) -> None:
        """
        Increment the lap counter without pausing: the equivalent of pause
        followed by resume.

        :raises TimerException: if this Timer is not running.
        """
        if not self._running:
            raise TimerException()
        self._laps += 1
        self._do_trace(lambda: f"lap {self._laps}")

    def pause(self) -> None:
        """
        Pause this timer during a lap, leaving the lap counter unchanged.

        :raises TimerException: if this Timer is not running.
        """
        self.pause_and_lap()
        self._laps -= 1
        self._do_trace(lambda: "pause timer")

    def millisecs(self) -> float:
        """
        :return: the total number of milliseconds elapsed for this timer.
        :raises TimerException: if this Timer is running.
        """
        if self._running:
            raise TimerException()
        return _to_millisecs(self._ticks)

    def __str__(self) -> str:
        return f"Timer{{ticks={self._ticks}, laps={self._laps}, running={str(self._running).lower()}}}"

    def _do_repeat_for_iteration(self, n: int, warmup: bool, supplier: Callable[[], Any],
                                 function: Callable[[Any], Any],
                                 pre_function: Callable[[Any], Any] | None,
                                 post_function: Callable[[Any], None] | None,
                                 lastx: int, i: int) -> int:
        """
        Run one iteration of a timed operation, timing only ``function``.

        The timer is paused when this is invoked and must be paused when it
        returns. You may use ``_do_print_status`` to show progress, but that is
        optional.

        :param n: the total number of iterations.
        :param warmup: true during the warmup phase.
        :param supplier: supplies the input value.
        :param function: the function to time.
        :param pre_function: pre-processes the input, untimed (may be None).
        :param post_function: consumes the result, untimed (may be None).
        :param lastx: the previous progress value.
        :param i: the current iteration index.
        :return: the updated progress value.
        """
        # TO BE IMPLEMENTED
        raise NotImplementedError("TO BE IMPLEMENTED")

    def _do_print_status(self, lastx: int, x: int) -> int:
        """
        Show progress, counting down in tens and marking the steps between with
        dots.

        :param lastx: the previous value; nothing is shown if x has not moved.
        :param x: the current value, as a percentage.
        :return: x.
        """
        if x != lastx:
            if x % 10 == 0:
                self._show_progress(str(10 - x // 10))
            else:
                self._show_progress(".")
        return x

    def _do_trace_if(self, condition: bool, message_function: Callable[[], str]) -> None:
        """
        Log a trace message if condition is true, building it lazily.

        :param condition: whether to log at all.
        :param message_function: produces the message.
        """
        if condition:
            logger.trace(message_function)

    def _do_trace(self, f: Callable[[], str]) -> None:
        """
        Log a trace message, building it lazily.

        :param f: produces the message.
        """
        self._do_trace_if(True, f)

    def _get_ticks(self) -> int:
        """
        NOTE used by unit tests.

        :return: the number of ticks stored in this Timer.
        """
        return self._ticks

    def _get_laps(self) -> int:
        """
        NOTE used by unit tests.

        :return: the number of laps stored in this Timer.
        """
        return self._laps

    def _is_running(self) -> bool:
        """
        NOTE used by unit tests.

        :return: true if this Timer is running.
        """
        return self._running


def progress_function(show_progress: bool) -> Callable[[str], None]:
    """
    :param show_progress: whether progress should be visible.
    :return: a function that prints progress, or one that discards it.
    """
    if show_progress:
        return lambda s: print(s, end="", flush=True)
    return lambda s: None


def _get_clock() -> int:
    """
    Get the number of ticks from the system clock.

    NOTE (maintain consistency) there is more than one system method for getting
    the clock time. Ensure that this is consistent with ``_to_millisecs``.

    NOTE use a monotonic clock. The wall clock can move backwards, which would
    give a negative elapsed time.

    :return: the number of ticks for the system clock.
    """
    # TO BE IMPLEMENTED
    raise NotImplementedError("TO BE IMPLEMENTED")


def _to_millisecs(ticks: int) -> float:
    """
    NOTE (maintain consistency) there is more than one system method for getting
    the clock time. Ensure that this is consistent with ``_get_clock``.

    :param ticks: the number of clock ticks.
    :return: the corresponding number of milliseconds.
    """
    # TO BE IMPLEMENTED
    raise NotImplementedError("TO BE IMPLEMENTED")
