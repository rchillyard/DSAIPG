"""
Pytest configuration for the DSAIPG Python project.

This file puts the ``Python`` directory itself onto ``sys.path`` so that the
``src.*`` packages resolve no matter which directory ``pytest`` was invoked
from.  Left to its own devices, ``pytest`` only adds the first directory above
each test file that has no ``__init__.py``, which makes the imports sensitive
to the current working directory--and to whatever an IDE happens to choose as
its working directory when it runs a single test.

``pythonpath`` in ``pyproject.toml`` covers the common case; this file covers
the case where ``pytest`` picks a rootdir above ``Python`` (for instance when
it is run from the top of the repository) and therefore never reads that
setting.
"""

import sys
from pathlib import Path

_PYTHON_DIR = Path(__file__).resolve().parent

if str(_PYTHON_DIR) not in sys.path:
    sys.path.insert(0, str(_PYTHON_DIR))
