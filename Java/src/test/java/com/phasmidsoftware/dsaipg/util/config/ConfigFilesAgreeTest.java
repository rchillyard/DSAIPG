package com.phasmidsoftware.dsaipg.util.config;

import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * There are two config.ini files — one in main/resources and one in
 * test/resources — and they are NOT merged. {@link Config#load} resolves
 * {@code config.ini} as a single classpath resource, and in test scope
 * test/resources comes first, so it shadows the other completely. Every setting
 * a test sees comes from the test copy, whether or not it was meant to differ.
 * <p>
 * That makes the two files a duplication which drifts silently, and it already
 * had. The test copy kept {@code words = 1000 # ...} and {@code runs = 1000 # ditto}
 * long after main/resources was fixed and given a NOTE explaining the hazard:
 * ini4j treats "#" as starting a comment only at the start of a line, so an inline
 * comment becomes part of the value and {@code getInt} then throws. Nothing caught
 * it because the only reader of those two keys is commented out — it would have
 * surfaced the day that benchmark was reinstated, and only under the test config.
 * <p>
 * So this test pins the relationship: the two files must define exactly the same
 * keys, and may differ in value only where the difference is deliberate and listed
 * below. A new difference fails here rather than years later.
 * <p>
 * NOTE this is why the settings which look redundant in the test copy should not
 * simply be deleted. An absent key falls back to the code's default, which is not
 * the same as main's value: dropping {@code instrumenting.swaps = true} would give
 * false and quietly stop every instrumented test counting swaps.
 */
public class ConfigFilesAgreeTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * The settings which are meant to differ, and why.
     */
    private static final Map<String, String> APPROVED_DIFFERENCES = new LinkedHashMap<>() {{
        put("helper.checksorted", "tests verify that a sort really sorted; benchmarks must not pay for the check");
        put("helper.seed", "tests want repeatable random data");
        put("benchmarkintegersorters.totalwork", "tests do far less work than a real benchmark");
        put("benchmarkstringsorters.totalwork", "ditto");
    }};

    @Test
    public void theTwoConfigFilesDefineTheSameKeys() throws IOException {
        Map<String, String> main = read(Paths.get("src/main/resources/config.ini"));
        Map<String, String> test = read(Paths.get("src/test/resources/config.ini"));

        Set<String> onlyInMain = new TreeSet<>(main.keySet());
        onlyInMain.removeAll(test.keySet());
        Set<String> onlyInTest = new TreeSet<>(test.keySet());
        onlyInTest.removeAll(main.keySet());

        assertEquals("settings present in main/resources but missing from test/resources. "
                + "The files are not merged, so a test would silently get the code default instead.",
                new TreeSet<String>(), onlyInMain);
        assertEquals("settings present in test/resources but missing from main/resources. "
                + "Add them to main, or delete them from test.",
                new TreeSet<String>(), onlyInTest);
    }

    @Test
    public void theTwoConfigFilesDifferOnlyWhereIntended() throws IOException {
        Map<String, String> main = read(Paths.get("src/main/resources/config.ini"));
        Map<String, String> test = read(Paths.get("src/test/resources/config.ini"));

        Set<String> differing = new TreeSet<>();
        for (String key : main.keySet())
            if (test.containsKey(key) && !main.get(key).equals(test.get(key)))
                differing.add(key);

        assertEquals("the two config.ini files differ in settings which are not on the approved list. "
                + "Either make them agree, or add the key to APPROVED_DIFFERENCES with a reason.",
                new TreeSet<>(APPROVED_DIFFERENCES.keySet()), differing);
    }

    /**
     * A value must not carry an inline "#" comment: ini4j keeps it as part of the
     * value, so {@code getInt} throws on what looks like a perfectly good number.
     * This is the drift that prompted the test above.
     */
    @Test
    public void noValueCarriesAnInlineComment() throws IOException {
        for (String file : new String[]{"src/main/resources/config.ini", "src/test/resources/config.ini"})
            for (Map.Entry<String, String> entry : read(Paths.get(file)).entrySet())
                assertTrue(file + ": " + entry.getKey() + " has an inline comment in its value: '"
                                + entry.getValue() + "'. Put the comment on its own line.",
                        !entry.getValue().contains("#"));
    }

    /**
     * Read an ini file as section-qualified key to value.
     *
     * @param path the file.
     * @return the settings, with blank lines and whole-line comments discarded.
     */
    private static Map<String, String> read(Path path) throws IOException {
        assertTrue("cannot find " + path.toAbsolutePath(), Files.exists(path));
        Map<String, String> result = new TreeMap<>();
        String section = "";
        for (String raw : Files.readAllLines(path)) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            if (line.startsWith("[") && line.endsWith("]")) section = line.substring(1, line.length() - 1);
            else if (line.contains("=")) {
                int i = line.indexOf('=');
                result.put(section + "." + line.substring(0, i).trim(), line.substring(i + 1).trim());
            }
        }
        return result;
    }
}
