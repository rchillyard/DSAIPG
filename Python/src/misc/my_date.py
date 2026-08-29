"""
Ported from misc/MyDate.java.
"""

from __future__ import annotations

import datetime
from dataclasses import dataclass, field


@dataclass(frozen=True)
class MyDate:
    """
    A year, a month and a day, ordered as dates are: year first, then month, then
    day. A minimal Comparable, for sorting exercises.
    """

    year: int
    month: int
    day: int
    _day_of_week: list[int] = field(default_factory=lambda: [-1], compare=False, repr=False)

    def get_year(self) -> int:
        """
        :return: the year.
        """
        return self.year

    def get_month(self) -> int:
        """
        :return: the month, from 1 to 12.
        """
        return self.month

    def get_day(self) -> int:
        """
        :return: the day of the month.
        """
        return self.day

    def get_day_of_week(self) -> int:
        """
        Worked out on demand and then remembered, as in the Java -- which is the
        point of the field: a date is asked for its day of the week far more often
        than it changes, and it never changes.

        :return: the day of the week, Monday being 1 and Sunday 7.
        """
        if self._day_of_week[0] == -1:
            self._day_of_week[0] = datetime.date(self.year, self.month, self.day).isoweekday()
        return self._day_of_week[0]

    def compare_to(self, that: MyDate) -> int:
        """
        :param that: the date to compare with.
        :return: negative, zero or positive as this date falls before, on, or after it.
        """
        mine = (self.year, self.month, self.day)
        theirs = (that.year, that.month, that.day)
        return (mine > theirs) - (mine < theirs)

    def __lt__(self, that: MyDate) -> bool:
        return self.compare_to(that) < 0

    def __str__(self) -> str:
        return f"{self.year}-{self.month}-{self.day}"
