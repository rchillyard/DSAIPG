import hashlib
from pathlib import Path

import pytest

from src.util.config.config import CONFIG_INI, Config, get_config

# These mirror ConfigTest.java, plus tests for the four places where
# configparser's defaults disagree with ini4j.

SAMPLE = """\
[helper]
# a comment about instrument
instrument =
cutoff = 7
seed = 12345

[instrumenting]
showStats = true
fixes = false
inversions = 0

[timer]
showprogress = true
"""


@pytest.fixture
def config() -> Config:
    return Config.from_text(SAMPLE)


class TestGet:
    def test_present(self, config):
        assert config.get("helper", "cutoff") == "7"

    def test_empty_value_is_the_empty_string_not_none(self, config):
        # The distinction matters: get_int uses it to decide whether to fall
        # back to its default.
        assert config.get("helper", "instrument") == ""

    def test_absent_option_is_none(self, config):
        assert config.get("helper", "nosuchoption") is None

    def test_absent_section_is_none(self, config):
        assert config.get("nosuchsection", "cutoff") is None


class TestGetBoolean:
    def test_true(self, config):
        assert config.get_boolean("instrumenting", "showStats")

    def test_false(self, config):
        assert not config.get_boolean("instrumenting", "fixes")

    def test_empty_is_false(self, config):
        assert not config.get_boolean("helper", "instrument")

    def test_absent_is_false(self, config):
        assert not config.get_boolean("helper", "nosuchoption")
        assert not config.get_boolean("nosuchsection", "whatever")

    @pytest.mark.parametrize("value", ["yes", "on", "1", "True "])
    def test_only_the_word_true_is_true(self, value):
        # configparser's own getboolean accepts yes/on/1; ini4j does not, so
        # neither do we. "True " with a trailing space is accepted, because we
        # strip, as does ini4j.
        config = Config.from_text(f"[s]\nk = {value}\n")
        assert config.get_boolean("s", "k") == (value.strip().lower() == "true")


class TestGetInt:
    def test_present(self, config):
        assert config.get_int("helper", "cutoff", -1) == 7

    def test_empty_gives_the_default(self, config):
        assert config.get_int("helper", "instrument", -1) == -1

    def test_absent_gives_the_default(self, config):
        assert config.get_int("helper", "nosuchoption", 42) == 42

    def test_zero_is_not_treated_as_absent(self, config):
        assert config.get_int("instrumenting", "inversions", -1) == 0

    def test_unparseable_raises(self):
        config = Config.from_text("[s]\nk = not a number\n")
        with pytest.raises(ValueError):
            config.get_int("s", "k", -1)


class TestGetLongAndString:
    def test_get_long(self, config):
        assert config.get_long("helper", "seed", -1) == 12345

    def test_get_long_is_unbounded(self):
        # Python has no long/int distinction, so a value too big for a Java long
        # is still fine here.
        big = 2 ** 70
        config = Config.from_text(f"[s]\nk = {big}\n")
        assert config.get_long("s", "k", -1) == big

    def test_get_string(self, config):
        assert config.get_string("helper", "cutoff", "d") == "7"

    def test_get_string_empty_gives_the_default(self, config):
        assert config.get_string("helper", "instrument", "d") == "d"

    def test_get_string_absent_gives_the_default(self, config):
        assert config.get_string("helper", "nosuchoption", "d") == "d"


class TestIni4jCompatibility:
    def test_option_names_keep_their_case(self, config):
        # showStats, shareInstrumenter, LSD and MSD all appear in config.ini in
        # mixed case; configparser would lowercase them by default.
        assert config.get("instrumenting", "showStats") == "true"
        assert config.get("instrumenting", "showstats") is None

    def test_an_inline_hash_is_part_of_the_value(self):
        # ini4j starts a comment only at the beginning of a line. We match it, so
        # that a value which works in one tree works in the other.
        config = Config.from_text("[s]\nk = 1000 # a comment\n")
        assert config.get("s", "k") == "1000 # a comment"

    def test_a_leading_hash_is_a_comment(self):
        config = Config.from_text("[s]\n# k = 1000\n")
        assert config.get("s", "k") is None

    def test_a_percent_sign_is_just_a_character(self):
        # With configparser's default interpolation this would raise.
        config = Config.from_text("[s]\nk = 50%\n")
        assert config.get("s", "k") == "50%"

    def test_a_colon_does_not_separate(self):
        # configparser accepts ":" as a delimiter by default; ini4j does not.
        config = Config.from_text("[s]\nk = a:b\n")
        assert config.get("s", "k") == "a:b"


class TestCopy:
    def test_sets_the_value(self, config):
        assert config.copy("helper", "cutoff", "99").get_int("helper", "cutoff", -1) == 99

    def test_leaves_the_original_alone(self, config):
        config.copy("helper", "cutoff", "99")
        assert config.get_int("helper", "cutoff", -1) == 7

    def test_adds_a_missing_section(self, config):
        assert config.copy("newsection", "k", "v").get("newsection", "k") == "v"

    def test_keeps_the_other_values(self, config):
        assert config.copy("helper", "cutoff", "99").get_long("helper", "seed", -1) == 12345


class TestComment:
    def test_get_comment(self):
        config = Config.from_text("# about s\n[s]\nk = v\n")
        assert config.get_comment("s") == "about s"

    def test_multiple_lines_are_joined(self):
        config = Config.from_text("# one\n# two\n[s]\nk = v\n")
        assert config.get_comment("s") == "one\ntwo"

    def test_no_comment(self):
        config = Config.from_text("[s]\nk = v\n")
        assert config.get_comment("s") is None

    def test_a_comment_inside_the_previous_section_does_not_attach(self):
        config = Config.from_text("[a]\nk = v\n# about k, not about b\n\n[b]\nj = w\n")
        assert config.get_comment("b") is None


class TestRendering:
    def test_section_to_string_omits_empty_options(self, config):
        assert config.section_to_string("helper") == "helper: cutoff=7, seed=12345, \n"

    def test_sortbenchmark_is_suppressed(self):
        config = Config.from_text("[sortbenchmark]\nversion = 1.0.8\n")
        assert config.section_to_string("sortbenchmark") == ""

    def test_instrumenting_is_suppressed_unless_instrumenting(self, config):
        assert config.section_to_string("instrumenting") == ""

    def test_instrumenting_is_shown_when_instrument_is_true(self, config):
        instrumented = config.copy("helper", "instrument", "true")
        assert "showStats=true" in instrumented.section_to_string("instrumenting")

    def test_str_covers_every_section(self, config):
        rendered = str(config)
        assert "helper: " in rendered
        assert "timer: " in rendered


class TestLoad:
    def test_load_finds_the_root_config(self):
        assert Config.load().get_boolean("timer", "showprogress")

    def test_load_beside_a_class_falls_back_to_the_root(self):
        # There is no config.ini beside Config itself, so this exercises the
        # fallback rather than the first branch.
        assert Config.load(Config).get("sortbenchmark", "version") is not None

    def test_get_config(self):
        assert get_config().get("sortbenchmark", "version") is not None

    def test_from_path_missing(self):
        with pytest.raises(FileNotFoundError):
            Config.from_path(Path("no", "such", CONFIG_INI))


class TestTheRealConfigIni:
    """The shipped config.ini must parse, and must agree with the Java tree's."""

    def test_the_shipped_values(self):
        config = Config.load()
        assert config.get_boolean("instrumenting", "shareInstrumenter")
        assert config.get_boolean("benchmarkstringsorters", "MSD")
        assert not config.get_boolean("helper", "instrument")
        assert config.get_int("helper", "cutoff", -1) == -1
        assert config.get_int("benchmarkintegersorters", "mode", -1) == 4

    def test_words_and_runs_parse_as_numbers(self):
        # Neither may carry an inline comment: ini4j keeps one as part of the
        # value, and reading that as a number throws. Comments sit on their own
        # lines.
        config = Config.load()
        assert config.get_int("benchmarkstringsorters", "words", -1) == 1000
        assert config.get_int("benchmarkstringsorters", "runs", -1) == 1000

    def test_it_matches_the_java_tree(self):
        # parents[3] is the Python tree; its parent holds both trees.
        dsaipg = Path(__file__).resolve().parents[4]
        java = dsaipg / "Java" / "src" / "main" / "resources" / CONFIG_INI
        if not java.is_file():
            pytest.skip(f"the Java tree is not present at {java}")
        python = dsaipg / "Python" / "src" / "resources" / CONFIG_INI
        assert hashlib.md5(java.read_bytes()).hexdigest() == hashlib.md5(python.read_bytes()).hexdigest(), (
            "config.ini has drifted between the Java and Python trees; the Java one is generated from "
            "INFO6205/src/main/resources/config.ini, so change that and re-run Clean, then copy it here."
        )
