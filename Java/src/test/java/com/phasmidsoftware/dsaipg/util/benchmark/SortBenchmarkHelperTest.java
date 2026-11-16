package com.phasmidsoftware.dsaipg.util.benchmark;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link SortBenchmarkHelper#getWords}, and in particular for the resource name.
 * <p>
 * `getWords` reads its resource with `getResourceAsStream`, which resolves a name WITHOUT a
 * leading slash relative to the calling class's package rather than the classpath root. These
 * files live at the root, so a bare name has to be normalised. When the method was changed from
 * `FileReader` to `getResourceAsStream`, the slash was added at one of six call sites; the other
 * five were left throwing the very `FileNotFoundException` the change was meant to fix, and
 * nothing caught it because nothing tested this method.
 */
public class SortBenchmarkHelperTest {

    private static final String RESOURCE = "3000-common-words.txt";

    private static List<String> lineAsList(String line) {
        return Arrays.asList(line.split(" "));
    }

    /**
     * The case the four surviving call sites use.
     */
    @Test
    public void getWordsAcceptsABareResourceName() throws FileNotFoundException {
        String[] words = SortBenchmarkHelper.getWords(RESOURCE, SortBenchmarkHelperTest::lineAsList);
        assertTrue("expected some words from " + RESOURCE, words.length > 0);
    }

    /**
     * The case SortBenchmark uses, which already had the slash.
     */
    @Test
    public void getWordsAcceptsAnAbsoluteResourceName() throws FileNotFoundException {
        String[] words = SortBenchmarkHelper.getWords("/" + RESOURCE, SortBenchmarkHelperTest::lineAsList);
        assertTrue("expected some words from /" + RESOURCE, words.length > 0);
    }

    /**
     * Both spellings must name the same resource — that is the whole point of normalising.
     */
    @Test
    public void bothSpellingsGiveTheSameWords() throws FileNotFoundException {
        String[] bare = SortBenchmarkHelper.getWords(RESOURCE, SortBenchmarkHelperTest::lineAsList);
        String[] absolute = SortBenchmarkHelper.getWords("/" + RESOURCE, SortBenchmarkHelperTest::lineAsList);
        assertArrayEquals(bare, absolute);
    }

    /**
     * A genuinely missing resource must still fail, and name itself when it does.
     */
    @Test
    public void getWordsThrowsForAMissingResource() {
        FileNotFoundException e = assertThrows(FileNotFoundException.class,
                () -> SortBenchmarkHelper.getWords("no-such-resource-39c1f.txt", SortBenchmarkHelperTest::lineAsList));
        assertTrue("the message should name the resource, but was: " + e.getMessage(),
                e.getMessage().contains("no-such-resource-39c1f.txt"));
    }
}
