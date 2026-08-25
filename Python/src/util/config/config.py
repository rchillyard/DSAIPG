"""
Configuration read from an ini file, ported from util/config/Config.java.

The Java uses ini4j; this uses the standard library's ``configparser``. They do
not agree out of the box, so the parser is configured to match ini4j in the four
places where the defaults differ. Each is noted at the point where it is set,
because a silent disagreement here would change the behaviour of every sort in
the tree.
"""

from __future__ import annotations

import configparser
import inspect
from collections.abc import Mapping
from pathlib import Path

from src.util.logging.lazy_logger import LazyLogger

#: The name of the file we look for, as in the Java.
CONFIG_INI = "config.ini"

#: Section and option names used by ``section_to_string``. The Java takes these
#: from Config_Benchmark and Instrument; they are repeated here to avoid making
#: this module depend on the sort package, which is ported separately.
HELPER = "helper"
INSTRUMENT = "instrument"
INSTRUMENTING = "instrumenting"

logger = LazyLogger(__name__)

# NOTE this is module level, not per-instance, matching the Java: a Config that
# is copied has not necessarily had all its enquiries made yet, and a per-
# instance record would log every key again for every copy.
_logged: dict[str, bool] = {}


class Config:
    """
    Configuration data, as read from an ini file.
    """

    def __init__(self, parser: configparser.ConfigParser) -> None:
        """
        Primary constructor.

        :param parser: the parsed configuration. Prefer ``load`` or
                       ``from_text`` unless you are building one by hand.
        """
        self._parser = parser
        self._comments: dict[str, str] = {}

    @staticmethod
    def load(clazz: type | None = None) -> Config:
        """
        Load the appropriate configuration.

        If clazz is given, we look for config.ini in the directory of the module
        that defines it. If clazz is None, or the file is not there, we look in
        the resources root. This mirrors the Java, which looks for the file
        relative to a class and then at the root of the classpath.

        :param clazz: the class near which to look (may be None).
        :return: a new Config.
        :raises FileNotFoundError: if config.ini is in neither place.
        """
        if clazz is not None:
            beside = Path(inspect.getfile(clazz)).parent / CONFIG_INI
            if beside.is_file():
                return Config.from_path(beside)
        root = _resources_root() / CONFIG_INI
        if root.is_file():
            return Config.from_path(root)
        raise FileNotFoundError(f"resource {CONFIG_INI} not found for {clazz}")

    @staticmethod
    def from_path(path: Path | str) -> Config:
        """
        Read a Config from a file.

        :param path: the file to read.
        :return: a new Config.
        """
        return Config.from_text(Path(path).read_text(encoding="utf-8"))

    @staticmethod
    def from_text(text: str) -> Config:
        """
        Read a Config from the text of an ini file.

        :param text: the ini text.
        :return: a new Config.
        """
        result = Config(_new_parser())
        result._parser.read_string(text)
        result._comments = _section_comments(text)
        return result

    def copy(self, section_name: str, option_name: str, value: str) -> Config:
        """
        Copy this Config, setting section_name.option_name to value. The section
        is added if it is not already present. This Config is unchanged.

        :param section_name: the section name.
        :param option_name: the option name.
        :param value: the new value.
        :return: a new Config as described.
        """
        parser = _new_parser()
        for name in self._parser.sections():
            parser.add_section(name)
            for key, existing in self._parser.items(name):
                parser.set(name, key, existing)
        if not parser.has_section(section_name):
            parser.add_section(section_name)
        parser.set(section_name, option_name, value)
        result = Config(parser)
        result._comments = dict(self._comments)
        return result

    def get(self, section_name: str, option_name: str) -> str | None:
        """
        Retrieve an option as a string.

        NOTE an absent option gives None, whereas an option that is present but
        has no value gives the empty string. The two are different, and callers
        such as ``get_int`` rely on that.

        :param section_name: the section to look in.
        :param option_name: the option to retrieve.
        :return: the value, or None if the section or option is absent.
        """
        result = self._parser.get(section_name, option_name, fallback=None)
        key = f"{section_name}.{option_name}"
        if _un_logged(key):
            logger.debug(lambda: f"Config.get({section_name}, {option_name}) = {result}")
        return result

    def get_boolean(self, section_name: str, option_name: str) -> bool:
        """
        Retrieve an option as a boolean.

        NOTE only the exact text "true" (in any case) is true. Everything else is
        false, including an absent option, an empty one, and "yes", "on" and "1",
        all three of which configparser's own getboolean would accept. The Java
        converts with ini4j, which accepts only "true".

        :param section_name: the section to look in.
        :param option_name: the option to retrieve.
        :return: the value as a boolean.
        """
        return (self.get(section_name, option_name) or "").strip().lower() == "true"

    def get_int(self, section_name: str, option_name: str, default_value: int) -> int:
        """
        Retrieve an option as an int, using default_value if it is absent or
        empty.

        :param section_name: the section to look in.
        :param option_name: the option to retrieve.
        :param default_value: what to return when there is nothing to parse.
        :return: the value as an int, or default_value.
        :raises ValueError: if the value is present and not a number.
        """
        s = self.get(section_name, option_name)
        if s is None or s == "":
            return default_value
        return int(s)

    def get_long(self, section_name: str, option_name: str, default_value: int) -> int:
        """
        Retrieve an option as a long.

        Python's int is unbounded, so this is ``get_int``. It is kept because the
        Java has both, and code being ported across will ask for it.
        """
        return self.get_int(section_name, option_name, default_value)

    def get_string(self, section_name: str, option_name: str, default_value: str) -> str:
        """
        Retrieve an option as a string, using default_value if it is absent or
        empty.

        :param section_name: the section to look in.
        :param option_name: the option to retrieve.
        :param default_value: what to return when there is no value.
        :return: the value, or default_value.
        """
        s = self.get(section_name, option_name)
        if s is None or s == "":
            return default_value
        return s

    def get_comment(self, key: str) -> str | None:
        """
        Retrieve the comment attached to a section: that is, the run of comment
        lines immediately above its header.

        :param key: the section name.
        :return: the comment with its leading "#" or ";" removed, or None.
        """
        comment = self._comments.get(key)
        if _un_logged(key):
            logger.debug(lambda: f"Config.getComment({key}) = {comment}")
        return comment

    def get_section(self, key: str) -> Mapping[str, str] | None:
        """
        Retrieve a whole section.

        :param key: the section name.
        :return: the section as a read-only mapping, or None if it is absent.
        """
        if not self._parser.has_section(key):
            return None
        return dict(self._parser.items(key))

    def sections(self) -> list[str]:
        """
        :return: the section names, in the order they appear in the file.
        """
        return self._parser.sections()

    def section_to_string(self, section_name: str) -> str:
        """
        Render one section, omitting options with no value.

        Two sections are suppressed entirely: "sortbenchmark", which holds only
        the version, and "instrumenting", which means nothing unless
        helper.instrument is true.

        :param section_name: the section to render.
        :return: the rendered section, or the empty string.
        """
        if section_name == "sortbenchmark":
            return ""
        if section_name == INSTRUMENTING and not self.get_boolean(HELPER, INSTRUMENT):
            return ""
        result = [f"{section_name}: "]
        for key, value in self._parser.items(section_name):
            if value is not None and value.strip() != "":
                result.append(f"{key}={value}, ")
        result.append("\n")
        return "".join(result)

    def __str__(self) -> str:
        return "".join(self.section_to_string(s) for s in self._parser.sections())


def get_config(clazz: type | None = None) -> Config:
    """
    Load the configuration, turning a missing file into a RuntimeError.

    :param clazz: the class near which to look (may be None).
    :return: the Config.
    :raises RuntimeError: if config.ini cannot be found.
    """
    try:
        return Config.load(clazz)
    except OSError as e:
        raise RuntimeError("get_config") from e


class _CaseSensitiveParser(configparser.ConfigParser):
    """
    A parser that keeps option names as written.

    configparser lowercases them by default, but ini4j does not, and config.ini
    relies on it: showStats, shareInstrumenter, LSD and MSD would all be
    lowercased and then never found again.
    """

    def optionxform(self, optionstr: str) -> str:
        return optionstr


def _new_parser() -> configparser.ConfigParser:
    """
    Build a parser configured to agree with ini4j.

    :return: the parser.
    """
    return _CaseSensitiveParser(
        delimiters=("=",),
        # "#" starts a comment only at the beginning of a line, so the rest of
        # "words = 1000 # ..." would be part of the value. configparser agrees by
        # default; this is written out to stop anyone "fixing" it later.
        inline_comment_prefixes=None,
        comment_prefixes=("#", ";"),
        # "%" appears in no value today, but BasicInterpolation would make one an
        # error rather than a value, which ini4j would not.
        interpolation=None,
    )


def _resources_root() -> Path:
    """
    :return: the directory holding config.ini, which is src/resources.
    """
    return Path(__file__).resolve().parents[2] / "resources"


def _section_comments(text: str) -> dict[str, str]:
    """
    Collect the comment lines immediately above each section header.

    configparser discards comments, so they are recovered here from the raw text
    to support ``get_comment``.

    :param text: the ini text.
    :return: a map from section name to comment.
    """
    result: dict[str, str] = {}
    pending: list[str] = []
    for line in text.splitlines():
        stripped = line.strip()
        if stripped.startswith("#") or stripped.startswith(";"):
            pending.append(stripped[1:].strip())
        elif stripped.startswith("[") and stripped.endswith("]"):
            if pending:
                result[stripped[1:-1]] = "\n".join(pending)
            pending = []
        else:
            pending = []
    return result


def _un_logged(s: str) -> bool:
    """
    Check whether s has been logged, and record that it now has been.

    :param s: the key.
    :return: true if s had not been logged before.
    """
    value = _logged.get(s)
    if value is None:
        _logged[s] = True
        return True
    return not value
