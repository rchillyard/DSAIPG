"""
A named collection of Statistics, ported from util/benchmark/StatPack.java.
"""

from collections.abc import Callable

from src.util.benchmark.statistics import Statistics


class StatPack:
    """
    Keeps one Statistics per key, so that a benchmark can record several
    properties -- compares, swaps, hits -- side by side over the same runs.
    """

    def __init__(self, normalizer: Callable[[float], float] | None, n_runs: int,
                 size: int, *keys: str) -> None:
        """
        :param normalizer: the normalizer passed to each Statistics.
        :param n_runs: the number of runs.
        :param size: the size of the problem.
        :param keys: the properties to track.
        """
        self._n = n_runs
        self._map: dict[str, Statistics] = {
            key: Statistics(key, normalizer, n_runs, size) for key in keys
        }

    def add(self, key: str, x: float) -> None:
        """
        Add a value to the Statistics for the given key.

        :param key: the property.
        :param x: the value to add.
        """
        self.get_statistics(key).add(x)

    def get_statistics(self, key: str) -> Statistics:
        """
        :param key: the property.
        :return: the Statistics for that property.
        :raises KeyError: if the key was not declared at construction.
        """
        statistics = self._map.get(key)
        if statistics is None:
            raise KeyError(f"StatPack.get_statistics({key}): key not valid")
        return statistics

    def get_count(self, key: str) -> int:
        """
        :param key: the property.
        :return: how many values have been added for it.
        """
        return self.get_statistics(key).get_count()

    def total(self, key: str) -> float:
        """
        :param key: the property.
        :return: the total of its values.
        """
        return self.get_statistics(key).total()

    def mean(self, key: str) -> float:
        """
        :param key: the property.
        :return: the mean of its values.
        """
        return self.get_statistics(key).mean()

    def std_dev(self, key: str) -> float:
        """
        :param key: the property.
        :return: the standard deviation of its values.
        """
        return self.get_statistics(key).std_dev()

    def is_invalid(self) -> bool:
        """
        :return: True if this StatPack was built for no runs at all.
        """
        return self._n <= 0

    def __str__(self) -> str:
        if not self._map:
            return "StatPack {<empty>}"
        return "StatPack {" + "; ".join(str(s) for s in self._map.values()) + "}"


# The Java original exposes a shared EMPTY constant. It is a function here
# because a module-level instance would be shared mutable state, and a StatPack
# is mutable -- add() on a shared EMPTY would raise, but only at the point of
# use, and confusingly.
def empty() -> StatPack:
    """
    :return: a StatPack tracking nothing, for which is_invalid() is True.
    """
    return StatPack(None, 0, 0)
