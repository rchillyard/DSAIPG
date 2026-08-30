"""
Pytest configuration for the DSAIPG Python project.

Two things live here: the ``sys.path`` fix-up, and the rule which reports a test
that hit an unwritten exercise as skipped rather than failed.

The ``sys.path`` part puts the ``Python`` directory itself on the path so that
the ``src.*`` packages resolve no matter which directory ``pytest`` was invoked
from.  Left to its own devices, ``pytest`` only adds the first directory above
each test file that has no ``__init__.py``, which makes the imports sensitive to
the current working directory--and to whatever an IDE happens to choose as its
working directory when it runs a single test.

``pythonpath`` in ``pyproject.toml`` covers the common case; this file covers the
case where ``pytest`` picks a rootdir above ``Python`` (for instance when it is
run from the top of the repository) and therefore never reads that setting.
"""

import os
import sys
from pathlib import Path

import pytest

_PYTHON_DIR = Path(__file__).resolve().parent

if str(_PYTHON_DIR) not in sys.path:
    sys.path.insert(0, str(_PYTHON_DIR))


_MARKER = "TO BE IMPLEMENTED"
_MAX_CAUSE_DEPTH = 20


def _unimplemented(exception: BaseException | None) -> BaseException | None:
    """
    Find an unwritten-exercise exception, at the top of the given exception or
    anywhere in its chain.

    The chain is followed because a stub reached through a comprehension, a
    generator or a callback often arrives wrapped: ``__cause__`` for an explicit
    ``raise ... from ...``, ``__context__`` for one raised while handling
    another.

    :param exception: the exception which escaped the test.
    :return: the unwritten-exercise exception, or None if this is a real failure
             which should be reported as such.
    """
    seen = set()
    for _ in range(_MAX_CAUSE_DEPTH):
        if exception is None or id(exception) in seen:
            return None
        seen.add(id(exception))
        if isinstance(exception, NotImplementedError) and _MARKER in str(exception):
            return exception
        exception = exception.__cause__ or exception.__context__
    return None


def _where(exception: BaseException) -> str:
    """
    Describe where the work is, from the innermost frame of the traceback--which
    is the ``raise`` in the stub itself.

    The Java tree gets this for free, because ImplementationMissing builds the
    message in its own constructor.  Here the stubs raise a plain
    NotImplementedError carrying no location, so it is recovered from the
    traceback instead.

    :param exception: the unwritten-exercise exception.
    :return: a location such as ``src/adt/bqs/bag_array.py:118``.
    """
    traceback = exception.__traceback__
    if traceback is None:
        return "an unknown location"
    while traceback.tb_next is not None:
        traceback = traceback.tb_next
    filename = Path(traceback.tb_frame.f_code.co_filename)
    try:
        # Relative to Python/ if we can manage it: basenames repeat across
        # packages, and "bst.py:31" on its own is not much help.
        shown = filename.resolve().relative_to(_PYTHON_DIR)
    except ValueError:
        shown = Path(os.path.basename(filename))
    return f"{shown}:{traceback.tb_lineno}"


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """
    Report a test which reached a ``TO BE IMPLEMENTED`` stub as skipped, naming
    the stub, instead of failing it.

    Roughly a fifth of the tests here fail purely because the exercise has not
    been done yet, so red is the *correct* result--which makes the pass/fail
    signal useless.  A student cannot tell a genuine mistake from work not yet
    started, and neither can CI.  With this, an untouched checkout runs green
    with a list of skips, each naming the file and line to go and write, and
    anything red is a real problem.

    This is the counterpart of the CancelOnNotImplemented mixin in the Scala
    repositories and of the JUnit rule of the same name in the Java tree.  All
    three report the same way, so "grey means not written yet" holds across the
    three languages.  Unlike the Java one, this needs no per-class declaration:
    a conftest hook applies to every test in the tree.

    NOTE this rewrites the *report* rather than replacing the exception during
    the call.  The obvious hook, ``pytest_runtest_call``, only works for plain
    pytest tests: pytest's unittest integration catches the exception inside
    ``TestCase.run`` so it never escapes, which quietly left every
    ``unittest.TestCase`` in this tree still red.  ``call.excinfo`` is populated
    either way.

    NOTE also that a test which swallows the exception--inside a
    ``try``/``except``, or a ``pytest.raises`` naming a type broad enough to
    catch NotImplementedError--denies the hook its chance.
    """
    outcome = yield
    report = outcome.get_result()
    if call.excinfo is None:
        return
    exception = _unimplemented(call.excinfo.value)
    if exception is None:
        return
    report.outcome = "skipped"
    # The 3-tuple is how pytest represents a skip: where the test is, and why it
    # did not run.  So the summary names the test and the reason names the work.
    report.longrepr = (
        str(item.path),
        item.location[1] + 1,
        f"Skipped: You need to implement the code at {_where(exception)}",
    )
