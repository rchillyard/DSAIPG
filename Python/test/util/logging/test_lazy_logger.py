import logging

from src.util.logging.lazy_logger import NOT_LAZY, TRACE, LazyLogger


class Counter:
    """A message function that records how often it was called."""

    def __init__(self, message: str = "message") -> None:
        self.calls = 0
        self._message = message

    def __call__(self) -> str:
        self.calls += 1
        return self._message


class TestLaziness:
    def test_the_message_is_not_built_when_the_level_is_disabled(self, caplog):
        logger = LazyLogger("test.lazy.disabled")
        logger.get_logger().setLevel(logging.WARNING)
        counter = Counter()
        with caplog.at_level(logging.WARNING, logger="test.lazy.disabled"):
            logger.debug(counter)
            logger.trace(counter)
        assert counter.calls == 0

    def test_the_message_is_built_when_the_level_is_enabled(self, caplog):
        logger = LazyLogger("test.lazy.enabled")
        counter = Counter()
        with caplog.at_level(TRACE, logger="test.lazy.enabled"):
            logger.debug(counter)
        assert counter.calls == 1
        assert "message" in caplog.text

    def test_trace_sits_below_debug(self, caplog):
        logger = LazyLogger("test.lazy.levels")
        counter = Counter()
        with caplog.at_level(logging.DEBUG, logger="test.lazy.levels"):
            logger.trace(counter)
        assert counter.calls == 0, "TRACE should be off when the level is DEBUG"

    def test_the_level_can_be_raised_after_construction(self, caplog):
        # The level is tested on each call, not cached at construction, so
        # raising it afterwards takes effect.
        logger = LazyLogger("test.lazy.late")
        logger.get_logger().setLevel(logging.WARNING)
        counter = Counter()
        with caplog.at_level(logging.DEBUG, logger="test.lazy.late"):
            logger.debug(counter)
        assert counter.calls == 1


class TestNotLazy:
    def test_a_string_message_is_marked(self, caplog):
        logger = LazyLogger("test.lazy.string")
        with caplog.at_level(logging.DEBUG, logger="test.lazy.string"):
            logger.debug("already built")
        assert NOT_LAZY + "already built" in caplog.text

    def test_a_function_message_is_not_marked(self, caplog):
        logger = LazyLogger("test.lazy.function")
        with caplog.at_level(logging.DEBUG, logger="test.lazy.function"):
            logger.debug(lambda: "built on demand")
        assert NOT_LAZY not in caplog.text
        assert "built on demand" in caplog.text


class TestLevels:
    def test_each_level_reaches_the_log(self, caplog):
        logger = LazyLogger("test.lazy.each")
        with caplog.at_level(TRACE, logger="test.lazy.each"):
            logger.trace(lambda: "t")
            logger.debug(lambda: "d")
            logger.info(lambda: "i")
            logger.warn(lambda: "w")
            logger.error(lambda: "e")
            logger.fatal(lambda: "f")
        assert [r.levelno for r in caplog.records] == [
            TRACE, logging.DEBUG, logging.INFO, logging.WARNING, logging.ERROR, logging.CRITICAL]

    def test_is_enabled(self):
        logger = LazyLogger("test.lazy.enabledfor")
        logger.get_logger().setLevel(logging.DEBUG)
        assert logger.is_debug_enabled()
        assert not logger.is_trace_enabled()
        logger.get_logger().setLevel(TRACE)
        assert logger.is_trace_enabled()

    def test_an_exception_can_be_attached(self, caplog):
        logger = LazyLogger("test.lazy.exception")
        with caplog.at_level(logging.ERROR, logger="test.lazy.exception"):
            logger.error(lambda: "it broke", exc_info=RuntimeError("why"))
        assert "RuntimeError" in caplog.text


class TestName:
    def test_a_class_gives_a_qualified_name(self):
        assert LazyLogger(Counter).get_logger().name.endswith("Counter")

    def test_a_string_is_used_as_it_stands(self):
        assert LazyLogger("some.name").get_logger().name == "some.name"
