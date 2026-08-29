package com.phasmidsoftware.dsaipg.util.general;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Guards against test methods with empty bodies.
 * <p>
 * The IDE's "generate tests" command creates a stub per method, with nothing in
 * the body. Such a test always passes, so it is easy to forget -- and it is worse
 * than no test at all, because it inflates the count and looks like coverage.
 * <p>
 * There were 129 of these. While they sat there, three real defects survived in
 * code they nominally covered: a doubled copy count in swapInto, an MSD cutoff
 * that depended on whether instrumentation was on, and a QuickSort_3way branch
 * that ignored the Helper's comparator.
 * <p>
 * NOTE marking them {@code @Ignore} would be the wrong fix. This repository
 * relies on skipped tests meaning exactly one thing -- a solution has gone
 * missing from the student tree -- so INFO6205 must report zero skips.
 * <p>
 * KNOWN is a ratchet: it may shrink, never grow. Filling one in means deleting
 * its entry here.
 */
public class NoVacuousTestsTest {

    /**
     * The empty test bodies that remain, by file. Each will be written when its
     * area of the tree is next worked on.
     */
    private static final Set<String> KNOWN = new HashSet<>(Arrays.asList(
            "compression/HuffmanCodingTest.java:testAdd",
            "projects/mcts/tictactoe/PositionTest.java:testReflect",
            "projects/mcts/tictactoe/PositionTest.java:testRotate",
            "sort/classic/BucketSortTest.java:init",
            "sort/classic/BucketSortTest.java:postProcess",
            "sort/classic/BucketSortTest.java:close",
            "sort/linearithmic/MergeSortBasicTest.java:testSort",
            "sort/linearithmic/MergeSortBasicTest.java:mutatingSort",
            "sort/linearithmic/MergeSortBasicTest.java:testSort1",
            "sort/linearithmic/MergeSortBasicTest.java:testSort2",
            "sort/linearithmic/MergeSortBasicTest.java:testSort3"
    ));

    /**
     * Matches an @Test annotation followed by a method whose body is empty or
     * contains only whitespace and comments.
     */
    private static final Pattern VACUOUS = Pattern.compile(
            "@Test[^\\n]*\\n\\s*public void (\\w+)\\(\\)\\s*(?:throws [\\w, .]+)?\\{\\s*}");

    @Test
    public void noNewVacuousTests() throws IOException {
        Path root = Paths.get("src/test/java/com/phasmidsoftware/dsaipg");
        assertTrue("cannot find the test sources at " + root.toAbsolutePath(), Files.isDirectory(root));
        Set<String> found = new TreeSet<>();
        try (Stream<Path> paths = Files.walk(root)) {
            List<Path> javaFiles = new ArrayList<>();
            paths.filter(NoVacuousTestsTest::isTestClass).forEach(javaFiles::add);
            for (Path p : javaFiles) {
                String relative = root.relativize(p).toString();
                Matcher m = VACUOUS.matcher(Files.readString(p));
                while (m.find()) found.add(relative + ":" + m.group(1));
            }
        }
        Set<String> unexpected = new TreeSet<>(found);
        unexpected.removeAll(KNOWN);
        assertEquals("These test methods have empty bodies, so they assert nothing and always pass. "
                + "Write them, or delete them. If one is genuinely not ready, add it to KNOWN with a "
                + "note saying when it will be written.", "[]", unexpected.toString());

        Set<String> written = presentIn(KNOWN, root);
        written.removeAll(found);
        assertEquals("These entries in KNOWN are no longer vacuous -- remove them, so the ratchet "
                + "keeps tightening.", "[]", written.toString());
    }

    /**
     * Methods which look like tests but carry no live {@code @Test}, so JUnit never
     * runs them. Some are legitimate — helpers, and overrides of an interface method
     * inside an anonymous class — and are listed here to say so. The rest are
     * disabled tests, which are worse than vacuous ones: an empty body at least
     * looks empty, whereas these read as perfectly good tests and simply never run.
     */
    private static final Set<String> KNOWN_DISABLED = new HashSet<>(Arrays.asList(
            // legitimate: helpers and un-annotated interface overrides
            "sort/counting/RadixSortStepDefinition/RadixSortTest.java:buildIntArrayFromString",
            // genuinely disabled: an @Test was commented out
            "adt/symbolTable/tree/BSTBenchmarkTest.java:testRunBenchmarkWithValidSupplier",
            "adt/symbolTable/tree/BSTBenchmarkTest.java:testRunBenchmarkWithEmptyArray",
            "adt/symbolTable/tree/BSTBenchmarkTest.java:testRunBenchmarkWithLargeInput",
            "misc/reduction/MovesTest.java:test2_5",
            "projects/life/base/MatrixTest.java:testConstructor3",
            "select/EntropyTest.java:testGetEntropy0",
            "select/EntropyTest.java:testGetEntropy1",
            "sort/counting/LSDStringSortStepDefinition/LSDStringSortTest.java:testSort4",
            "sort/counting/MSDStringSortTest.java:sort5",
            "sort/helper/BaseComparableHelperTest.java:inversions",
            "sort/linearithmic/QuickSort3WayTest.java:testSortHuge",
            "util/general/FastInverseSquareRootTest.java:testInvSqrtNegative",
            "util/general/GeoConversionsTest.java:testPosition2UTM_3",
            "util/general/GeoConversionsTest.java:testPosition2UTM_4",
            "util/general/GeoConversionsTest.java:testPosition2UTM_5",
            "util/general/UTMTest.java:testToPositionSouthernHemisphere",
            "util/general/UTMTest.java:testToPositionEdgeCaseEquator",
            "util/general/UTMTest.java:testToPositionEdgeCasePoleHemisphereChange",
            // genuinely disabled: never annotated at all
            "adt/symbolTable/hashtable/HashTableLPTest.java:testHashTable5",
            "misc/lab_1/MyTreeTest.java:Node1",
            "sort/helper/InstrumentedComparableHelperTest.java:testMergeSortMany",
            "sort/linearithmic/QuickSort_ClassicTest.java:testSortDetailedRandom",
            "util/benchmark/SortBenchmarkTest.java:testMinComparisons",
            "util/config/ConfigTest.java:testUnLogged",
            "util/logging/LazyLoggerTest.java:testTraceLazyException",
            "util/logging/LazyLoggerTest.java:testDebugLazyException"
    ));

    /**
     * A public void method which could be a test.
     */
    private static final Pattern TEST_SHAPED = Pattern.compile(
            "^[ \\t]*public void (\\w+)\\s*\\([^)]*\\)\\s*(?:throws [\\w, .]+)?\\{?[ \\t]*$");

    /**
     * JUnit's own lifecycle methods, which are not tests and need no annotation to
     * be found (they have one, but it is simpler to name them).
     */
    private static final Set<String> LIFECYCLE = new HashSet<>(Arrays.asList(
            "setUp", "tearDown", "beforeClass", "afterClass", "before", "after", "evaluate"));

    /**
     * A method which looks like a test but has no live annotation is invisible to
     * {@link #noNewVacuousTests}, which matches empty bodies, and invisible to
     * JUnit, which only runs what is annotated. It therefore reads as a working
     * test forever while doing nothing at all — the same failure as a vacuous test
     * but better disguised, since the body is full of plausible assertions.
     * <p>
     * The ratchet works the same way: the list may shrink, never grow.
     */
    @Test
    public void noNewDisabledTests() throws IOException {
        Path root = Paths.get("src/test/java/com/phasmidsoftware/dsaipg");
        assertTrue("cannot find the test sources at " + root.toAbsolutePath(), Files.isDirectory(root));
        Set<String> found = new TreeSet<>();
        try (Stream<Path> paths = Files.walk(root)) {
            List<Path> javaFiles = new ArrayList<>();
            paths.filter(NoVacuousTestsTest::isTestClass).forEach(javaFiles::add);
            for (Path p : javaFiles) {
                String relative = root.relativize(p).toString();
                List<String> lines = Files.readAllLines(p);
                int depth = 0;
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    Matcher m = TEST_SHAPED.matcher(line);
                    // depth 1 is the body of the top-level class. Anything deeper
                    // belongs to a nested class, which JUnit does not run.
                    if (depth == 1 && m.matches()) {
                        String name = m.group(1);
                        if (!LIFECYCLE.contains(name) && !hasLiveAnnotation(lines, i))
                            found.add(relative + ":" + name);
                    }
                    depth += netBraces(line);
                }
            }
        }
        Set<String> unexpected = new TreeSet<>(found);
        unexpected.removeAll(KNOWN_DISABLED);
        assertEquals("These methods look like tests but have no live @Test, so JUnit never runs "
                + "them. Annotate them, delete them, or add them to KNOWN_DISABLED with a reason.",
                "[]", unexpected.toString());

        Set<String> revived = presentIn(KNOWN_DISABLED, root);
        revived.removeAll(found);
        assertEquals("These entries in KNOWN_DISABLED are no longer disabled -- remove them, so the "
                + "ratchet keeps tightening.", "[]", revived.toString());
    }

    /**
     * How much deeper a line leaves us, in braces.
     * <p>
     * NOTE this is what tells a test method from a method of a nested helper class.
     * The scan had no idea about nesting, so a `Route` or a `Cost` declared inside
     * a test class had its setters read as tests somebody had disabled. That is
     * where the `PrimTest.java:setSequence` entries in KNOWN_DISABLED came from:
     * they were never disabled tests, and listing them there hid the flaw rather
     * than fixing it.
     * <p>
     * Braces inside comments and literals do not count, or a string containing one
     * would throw the depth off for the rest of the file.
     *
     * @param line       one line of the file.
     * @return the number of braces opened less the number closed.
     */
    private int netBraces(String line) {
        int result = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inBlockComment) {
                if (c == '*' && i + 1 < line.length() && line.charAt(i + 1) == '/') {
                    inBlockComment = false;
                    i++;
                }
                continue;
            }
            if (c == '/' && i + 1 < line.length()) {
                char next = line.charAt(i + 1);
                if (next == '/') return result;          // rest of the line is a comment
                if (next == '*') {
                    inBlockComment = true;
                    i++;
                    continue;
                }
            }
            if (c == '"' || c == '\'') {
                char quote = c;
                while (++i < line.length()) {
                    if (line.charAt(i) == '\\') i++;
                    else if (line.charAt(i) == quote) break;
                }
                continue;
            }
            if (c == '{') result++;
            else if (c == '}') result--;
        }
        return result;
    }

    /**
     * Whether {@link #netBraces} is part-way through a block comment. An instance
     * field because the state has to survive from one line to the next; JUnit makes
     * a fresh instance per test, so it starts false.
     */
    private boolean inBlockComment = false;

    /**
     * Walk back from a method declaration over its contiguous annotations, comments
     * and blank lines, looking for an annotation that would make JUnit or Cucumber
     * run it.
     * <p>
     * NOTE contiguous. Walking past the first line of real code finds the PREVIOUS
     * method's annotations and reports every disabled test as live -- which is
     * exactly the mistake that made an earlier version of this scan report six
     * disabled methods where there are thirty-six.
     *
     * @param lines the file.
     * @param i     the index of the method declaration.
     * @return true if the method is annotated such that something will run it.
     */
    private static boolean hasLiveAnnotation(List<String> lines, int i) {
        for (int j = i - 1; j >= 0; j--) {
            String s = lines.get(j).trim();
            if (s.isEmpty()) continue;
            if (s.startsWith("@")) {
                if (s.startsWith("@Test") || s.startsWith("@Before") || s.startsWith("@After")
                        || s.startsWith("@Given") || s.startsWith("@When") || s.startsWith("@Then")
                        || s.startsWith("@Override") || s.startsWith("@ParameterizedTest")) return true;
                continue;
            }
            if (s.startsWith("*") || s.startsWith("/*") || s.startsWith("//")) continue;
            return false;
        }
        return false;
    }

    /**
     * Only a class Surefire would actually run counts as a test class.
     * <p>
     * NOTE not every .java file under the test tree. An ordinary helper sitting
     * there -- a shared fixture, say -- would have its methods read as tests that
     * someone had disabled. Surefire's own includes are {@code **}{@code /*Test.java}
     * and friends, so a method in a class named anything else can never run
     * whatever it is annotated with.
     *
     * @param path a file found under the test tree.
     * @return true if Surefire would treat it as a test class.
     */
    private static boolean isTestClass(Path path) {
        String name = path.getFileName().toString();
        return name.endsWith("Test.java") || name.endsWith("Tests.java") || name.startsWith("Test");
    }

    /**
     * Restrict a KNOWN list to the entries whose file is actually present.
     * <p>
     * NOTE this test runs in the generated student tree too, and that tree is a
     * SUBSET: CleanTree's exclusions drop {@code projects/life}, {@code admin},
     * {@code madhava} and more. Without this filter the "no longer disabled" check
     * fires there for every listed entry in an excluded file -- which is what
     * happened for projects/life/base/MatrixTest, green here and red there.
     *
     * @param known the list.
     * @param root  the test source root.
     * @return the entries whose file exists.
     */
    private static Set<String> presentIn(Set<String> known, Path root) {
        Set<String> result = new TreeSet<>();
        for (String entry : known)
            if (Files.isRegularFile(root.resolve(entry.substring(0, entry.lastIndexOf(':')))))
                result.add(entry);
        return result;
    }
}
