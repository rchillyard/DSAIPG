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
            "graphs/gis/GeoEdgeTest.java:getAttribute",
            "graphs/gis/GeoEdgeTest.java:get",
            "graphs/gis/GeoEdgeTest.java:getOther",
            "graphs/gis/GeoEdgeTest.java:hashCodeTest",
            "graphs/gis/GeoEdgeTest.java:toStringTest",
            "graphs/gis/GeoEdgeTest.java:create",
            "graphs/gis/GeoGraphSphericalTest.java:edges",
            "graphs/gis/GeoGraphSphericalTest.java:addEdge",
            "graphs/gis/GeoGraphSphericalTest.java:toStringTest",
            "graphs/gis/GeoGraphSphericalTest.java:vertices",
            "graphs/gis/GeoGraphSphericalTest.java:getDistance",
            "graphs/gis/GeoKruskalTest.java:getMST",
            "graphs/gis/GeoKruskalTest.java:iterator",
            "graphs/gis/GeoKruskalTest.java:getGeoMST",
            "graphs/gis/Position_SphericalTest.java:getLatitude",
            "graphs/gis/Position_SphericalTest.java:getLongitude",
            "graphs/gis/Position_SphericalTest.java:getX",
            "graphs/gis/Position_SphericalTest.java:getY",
            "graphs/gis/Position_SphericalTest.java:toStringTest",
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
            paths.filter(p -> p.toString().endsWith(".java")).forEach(javaFiles::add);
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

        Set<String> written = new TreeSet<>(KNOWN);
        written.removeAll(found);
        assertEquals("These entries in KNOWN are no longer vacuous -- remove them, so the ratchet "
                + "keeps tightening.", "[]", written.toString());
    }
}
