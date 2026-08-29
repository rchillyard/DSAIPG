"""
Ported from misc/CallByValue.java.

NOTE the Java exists to show that Java passes everything by value: assigning to a
parameter cannot be seen by the caller, while mutating what a parameter refers to
can. Python is the same in both respects and for the same reason -- a name is
bound to an object, and rebinding the name inside a function is invisible outside
it -- so the demonstration carries over exactly. What differs is the vocabulary:
Java says "primitive versus reference type", Python says "immutable versus
mutable", and it comes to the same thing here.
"""

from __future__ import annotations


class CallByValue:
    """
    The same increment written six ways, to show which of them the caller can see.
    """

    def __init__(self) -> None:
        self.number = 0
        self.array = [0]

    @staticmethod
    def increment_number1(number: int) -> int:
        """
        Rebinding a parameter. The caller's int is untouched -- ints are immutable,
        so there is nothing to touch.

        :param number: the value to add one to.
        :return: one more than it.
        """
        number += 1
        return number

    def increment_number2(self) -> int:
        """
        Incrementing a field. Visible to anyone holding this object.

        :return: the field's new value.
        """
        self.number += 1
        return self.number

    @staticmethod
    def increment_array1(array: list[int]) -> list[int]:
        """
        Mutating what the parameter refers to. The caller sees this.

        :param array: the list to change.
        :return: the same list.
        """
        array[0] += 1
        return array

    @staticmethod
    def increment_array2(array: list[int]) -> list[int]:
        """
        Rebinding the parameter to a new list. The caller sees nothing.

        :param array: the list to read.
        :return: a new list, one greater.
        """
        array = [array[0] + 1]
        return array

    def increment_array3(self) -> list[int]:
        """
        Mutating the field's list.

        :return: the field's list, changed.
        """
        self.array[0] += 1
        return self.array

    def increment_array4(self) -> list[int]:
        """
        Rebinding the field to a new list. Anyone who kept the old one still has it.

        :return: the new list.
        """
        self.array = [self.array[0] + 1]
        return self.array
