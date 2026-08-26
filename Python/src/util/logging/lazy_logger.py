"""
A logger whose expensive messages are built only if they will be logged,
ported from util/logging/LazyLogger.java.

The Java class extends log4j's Logger, so most of its 500 lines are delegation.
Only a handful of methods carry the idea, and those are what appear here:
``trace`` and ``debug`` take a function that produces the message, and call it
only when the level is enabled.

Usage mirrors the Java::

    logger = LazyLogger(QuickSort)                  # or LazyLogger(__name__)
    logger.debug(lambda: f"sorted {len(xs)} elements")

The lambda is not called at all unless DEBUG is enabled, so the f-string is
never built and the ``len`` never runs.
"""

import logging
from collections.abc import Callable

#: Python's logging module has no TRACE level, so we add one below DEBUG (10),
#: matching log4j where TRACE sits below DEBUG.
TRACE = 5
logging.addLevelName(TRACE, "TRACE")

#: Prefix applied to messages that were passed as an already-built string rather
#: than as a function. It is a diagnostic, not decoration: seeing it in a log
#: means the message was constructed whether or not anyone wanted it.
NOT_LAZY = "NOT lazy: "

Message = str | Callable[[], str]


class LazyLogger:
    """
    Wrap a standard library logger, adding message functions for the two levels
    where the cost of building a message actually matters.
    """

    def __init__(self, source: type | str) -> None:
        """
        :param source: the class doing the logging (as in the Java, where the
                       argument is a Class), or a logger name.
        """
        name = source if isinstance(source, str) else f"{source.__module__}.{source.__qualname__}"
        self._logger = logging.getLogger(name)

    def trace(self, message: Message, exc_info: BaseException | None = None) -> None:
        """
        Log at TRACE, building the message only if TRACE is enabled.

        :param message: a function returning the message, or a string (which has
                        already been built, and so is prefixed with NOT_LAZY).
        :param exc_info: an exception to log alongside the message, or None.
        """
        self._log(TRACE, message, exc_info)

    def debug(self, message: Message, exc_info: BaseException | None = None) -> None:
        """
        Log at DEBUG, building the message only if DEBUG is enabled.

        :param message: a function returning the message, or a string (which has
                        already been built, and so is prefixed with NOT_LAZY).
        :param exc_info: an exception to log alongside the message, or None.
        """
        self._log(logging.DEBUG, message, exc_info)

    def info(self, message: Message, exc_info: BaseException | None = None) -> None:
        """
        Log at INFO. A function is still accepted and still deferred, but INFO
        messages are usually cheap, which is why the Java class does not treat
        this level specially.
        """
        self._log(logging.INFO, message, exc_info)

    def warn(self, message: Message, exc_info: BaseException | None = None) -> None:
        """Log at WARNING. Named for the Java method rather than the Python level."""
        self._log(logging.WARNING, message, exc_info)

    def error(self, message: Message, exc_info: BaseException | None = None) -> None:
        """Log at ERROR."""
        self._log(logging.ERROR, message, exc_info)

    def fatal(self, message: Message, exc_info: BaseException | None = None) -> None:
        """Log at CRITICAL. Named for the Java method (log4j calls this level FATAL)."""
        self._log(logging.CRITICAL, message, exc_info)

    def is_trace_enabled(self) -> bool:
        """
        :return: true if TRACE is enabled.
        """
        return self._logger.isEnabledFor(TRACE)

    def is_debug_enabled(self) -> bool:
        """
        :return: true if DEBUG is enabled.
        """
        return self._logger.isEnabledFor(logging.DEBUG)

    def get_logger(self) -> logging.Logger:
        """
        :return: the underlying logger, for callers that need to set a level or
                 attach a handler.
        """
        return self._logger

    def _log(self, level: int, message: Message, exc_info: BaseException | None) -> None:
        """
        Log at the given level, resolving the message only if the level is
        enabled.

        NOTE the level is tested on each call, as it is in the Java. That costs
        one comparison, which is the very thing the lazy evaluation exists to
        protect, and it means a level changed after construction takes effect.
        """
        if not self._logger.isEnabledFor(level):
            return
        text = message() if callable(message) else NOT_LAZY + message
        self._logger.log(level, text, exc_info=exc_info, stacklevel=3)
