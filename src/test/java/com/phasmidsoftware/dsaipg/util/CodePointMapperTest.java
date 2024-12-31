package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import java.util.Comparator;

import static com.phasmidsoftware.dsaipg.util.CodePointMapper.ASCIIExt;
import static com.phasmidsoftware.dsaipg.util.CodePointMapper.English;
import static org.junit.Assert.*;

public class CodePointMapperTest {

    @Test
    public void testEnglish() {
        CodePointMapper mapper = English;
        assertEquals(0, mapper.map(' '));
        assertEquals(1, mapper.map('A'));
        assertEquals(26, mapper.map('Z'));
        assertEquals(1, mapper.map('a'));
        assertEquals(26, mapper.map('z'));
    }

    @Test
    public void testEnglishRange() {
        CodePointMapper mapper = English;
        assertTrue(mapper.inRange(0));
        assertTrue(mapper.inRange(31));
        assertFalse(mapper.inRange(-1));
        assertFalse(mapper.inRange(32));
    }

    @Test
    public void testASCIIMapper() {
        CodePointMapper mapper = ASCIIExt;
        assertEquals(0xFF, mapper.map(-1)); // DEL
        assertEquals(0, mapper.map(0)); // NUL
        assertEquals(32, mapper.map(' '));
        assertEquals(65, mapper.map('A'));
        assertEquals(90, mapper.map('Z'));
        assertEquals(97, mapper.map('a'));
        assertEquals(122, mapper.map('z'));
    }

    @Test
    public void testASCIIComparator() {
        Comparator<String> comparator = ASCIIExt.comparator;
        assertEquals(-1, comparator.compare("a", "b"));
        assertEquals(-2, comparator.compare("a", "c"));
        assertEquals(-118, comparator.compare("de", "develop"));
    }

    @Test
    public void testASCIIRange() {
        CodePointMapper mapper = ASCIIExt;
        assertTrue(mapper.inRange(0));
        assertTrue(mapper.inRange(255));
        assertFalse(mapper.inRange(-1));
        assertFalse(mapper.inRange(256));
    }
}