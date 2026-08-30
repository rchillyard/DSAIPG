"""
The exception raised by the sorting code, ported from
sort/generic/SortException.java.
"""


class SortException(Exception):
    """
    Raised when a sort, or something a sort depends on, is asked to do something
    it cannot.
    """
