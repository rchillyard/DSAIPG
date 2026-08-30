"""
HelperException, ported from sort/helper/HelperException.java.

The Java declares this three times: once as a top-level class and again as a
nested class inside each of NonInstrumentingComparableHelper and
NonInstrumentingComparatorHelper. There is one here, which the whole helper
package shares.
"""

from __future__ import annotations


class HelperException(RuntimeError):
    """
    Raised when a Helper is asked to do something it cannot.
    """
