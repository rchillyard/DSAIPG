package com.phasmidsoftware.dsaipg.compression;

import org.junit.Test;

import java.io.PrintWriter;
import java.util.PriorityQueue;
import java.util.Set;
import java.io.StringWriter;

import static com.phasmidsoftware.dsaipg.compression.HuffmanCoding.showTree;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class HuffmanCodingTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    @Test
    public void testGenerateCode() {
        HuffmanCoding huffmanCoding = HuffmanCoding.createHuffmanCoding();
        HuffmanCoding.Node tree = huffmanCoding.generateCode();
        assertNotNull(tree);
    }

    /**
     * add puts a Node into the priority queue, which orders by frequency, so the
     * rarest symbol comes out first however the nodes went in. That is what makes
     * the tree Huffman's: the two least frequent symbols are always the pair
     * combined next.
     * <p>
     * NOTE asserted by identity, since Node keeps its symbol and frequency private.
     */
    @Test
    public void testAdd() {
        PriorityQueue<HuffmanCoding.Node> queue = new PriorityQueue<>();
        HuffmanCoding huffmanCoding = new HuffmanCoding(queue);
        HuffmanCoding.Node e = new HuffmanCoding.Node("E", 120);
        HuffmanCoding.Node z = new HuffmanCoding.Node("Z", 1);
        HuffmanCoding.Node t = new HuffmanCoding.Node("T", 90);
        huffmanCoding.add(e);
        huffmanCoding.add(z);
        huffmanCoding.add(t);
        assertEquals(3, queue.size());
        assertSame("the rarest symbol is at the head", z, queue.poll());
        assertSame(t, queue.poll());
        assertSame(e, queue.poll());
        assertTrue(queue.isEmpty());
    }

    /**
     * Nodes of equal frequency may come out in either order -- compareTo looks only
     * at the frequency -- but both must come out, and before anything commoner.
     */
    @Test
    public void testAddEqualFrequencies() {
        PriorityQueue<HuffmanCoding.Node> queue = new PriorityQueue<>();
        HuffmanCoding huffmanCoding = new HuffmanCoding(queue);
        HuffmanCoding.Node a = new HuffmanCoding.Node("A", 5);
        HuffmanCoding.Node b = new HuffmanCoding.Node("B", 5);
        HuffmanCoding.Node c = new HuffmanCoding.Node("C", 9);
        huffmanCoding.add(a);
        huffmanCoding.add(b);
        huffmanCoding.add(c);
        Set<HuffmanCoding.Node> firstTwo = Set.of(queue.poll(), queue.poll());
        assertEquals(Set.of(a, b), firstTwo);
        assertSame(c, queue.poll());
    }
    @Test
    public void testShowTree() {
        HuffmanCoding huffmanCoding = HuffmanCoding.createHuffmanCoding();
        HuffmanCoding.Node tree = huffmanCoding.generateCode();
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        showTree(tree, pw);
        pw.flush();
        pw.close();
        String s = stringWriter.toString();
        // CONSIDER just testing the length of this string.
        assertEquals("  : N-2-NT-X-null-XX-9-Q-8-J-T-A-K-7-6-P-5-4-D️-1-3-S️-C️-H️-W-Z-E (319) \n" +
                "    0: N-2-NT-X-null-XX-9-Q-8-J-T-A-K-7-6 (122) \n" +
                "    0  0: N-2-NT-X-null-XX-9 (57) \n" +
                "    0  0  0: N (28) \n" +
                "    0  0  1: 2-NT-X-null-XX-9 (29) \n" +
                "    0  0  1  0: 2 (14) \n" +
                "    0  0  1  1: NT-X-null-XX-9 (15) \n" +
                "    0  0  1  1  0: NT-X-null-XX (7) \n" +
                "    0  0  1  1  0  0: NT (3) \n" +
                "    0  0  1  1  0  1: X-null-XX (4) \n" +
                "    0  0  1  1  0  1  0: X (2) \n" +
                "    0  0  1  1  0  1  1: null-XX (2) \n" +
                "    0  0  1  1  0  1  1  0: null (1) \n" +
                "    0  0  1  1  0  1  1  1: XX (1) \n" +
                "    0  0  1  1  1: 9 (8) \n" +
                "    0  1: Q-8-J-T-A-K-7-6 (65) \n" +
                "    0  1  0: Q-8-J-T (32) \n" +
                "    0  1  0  0: Q-8 (16) \n" +
                "    0  1  0  0  0: Q (8) \n" +
                "    0  1  0  0  1: 8 (8) \n" +
                "    0  1  0  1: J-T (16) \n" +
                "    0  1  0  1  0: J (8) \n" +
                "    0  1  0  1  1: T (8) \n" +
                "    0  1  1: A-K-7-6 (33) \n" +
                "    0  1  1  0: A-K (16) \n" +
                "    0  1  1  0  0: A (8) \n" +
                "    0  1  1  0  1: K (8) \n" +
                "    0  1  1  1: 7-6 (17) \n" +
                "    0  1  1  1  0: 7 (8) \n" +
                "    0  1  1  1  1: 6 (9) \n" +
                "    1: P-5-4-D️-1-3-S️-C️-H️-W-Z-E (197) \n" +
                "    1  0: P-5-4-D️-1-3-S️ (89) \n" +
                "    1  0  0: P-5-4 (41) \n" +
                "    1  0  0  0: P (20) \n" +
                "    1  0  0  1: 5-4 (21) \n" +
                "    1  0  0  1  0: 5 (10) \n" +
                "    1  0  0  1  1: 4 (11) \n" +
                "    1  0  1: D️-1-3-S️ (48) \n" +
                "    1  0  1  0: D️-1 (24) \n" +
                "    1  0  1  0  0: D️ (12) \n" +
                "    1  0  1  0  1: 1 (12) \n" +
                "    1  0  1  1: 3-S️ (24) \n" +
                "    1  0  1  1  0: 3 (12) \n" +
                "    1  0  1  1  1: S️ (12) \n" +
                "    1  1: C️-H️-W-Z-E (108) \n" +
                "    1  1  0: C️-H️-W (52) \n" +
                "    1  1  0  0: C️-H️ (24) \n" +
                "    1  1  0  0  0: C️ (12) \n" +
                "    1  1  0  0  1: H️ (12) \n" +
                "    1  1  0  1: W (28) \n" +
                "    1  1  1: Z-E (56) \n" +
                "    1  1  1  0: Z (28) \n" +
                "    1  1  1  1: E (28) \n", s);
    }

    @Test
    public void testEncode1() {
        HuffmanCoding huffmanCoding = HuffmanCoding.createHuffmanCoding();
        HuffmanCoding.Node code = huffmanCoding.generateCode();
        HuffmanCoding.HuffmanEncoder huffmanEncoder = new HuffmanCoding.HuffmanEncoder(code);
        Long[] longs = huffmanEncoder.encode(new String[]{null});
        assertEquals(1, longs.length);
        assertEquals(0x3600000000000000L, longs[0].longValue());
    }

    @Test
    public void testEncode2() {
        HuffmanCoding huffmanCoding = HuffmanCoding.createHuffmanCoding();
        HuffmanCoding.Node code = huffmanCoding.generateCode();
        HuffmanCoding.HuffmanEncoder huffmanEncoder = new HuffmanCoding.HuffmanEncoder(code);
        String board = "https://www.bridgebase.com/tools/handviewer.html?lin=st||pn|Beowulf,blueceo,yinyichen,xinyu320|md|1SK2H862D976CKQT97,S4HQJ974DAK5CJ432,SAJT86HAKTDQJT3C8,SQ9753H53D842CA65|sv|e|rh||ah|Board%2019|mb|P|mb|1H|mb|1S|mb|P|mb|2C|mb|P|mb|3N|mb|P|mb|P|mb|P|pc|H5|pc|H2|pc|HJ|pc|HA|pc|DT|pc|D2|pc|D6|pc|DK|pc|S4|pc|ST|pc|SQ|pc|SK|pc|D7|pc|D5|pc|DQ|pc|D4|pc|D3|pc|D8|pc|D9|pc|DA|pc|H4|pc|HT|pc|H3|pc|H6|pc|DJ|pc|C5|pc|C7|pc|C4|pc|SA|pc|S3|pc|S2|pc|H7|pc|C8|pc|CA|pc|C9|pc|C2|pc|S5|pc|CT|pc|C3|pc|S6|pc|HK|pc|C6|pc|H8|pc|H9|pc|SJ|pc|S7|pc|CQ|pc|CJ|pc|S8|pc|S9|pc|CK|pc|HQ|";
        assertEquals(560, board.length());
        String[] strings = HuffmanCoding.parseLin(board);
        assertEquals(186, strings.length);
        StringBuilder sb = new StringBuilder();
        for (String string : strings) sb.append(string);
//        System.out.println(sb.toString());
        // number of bytes in strings = 2 * 186 = 372
        Long[] longs = huffmanEncoder.encode(strings);
        assertEquals(15, longs.length);
        // number of bytes in Huffman code = 8 * 15 = 120
        assertEquals(0x69652F2A1DCFC350L, longs[0].longValue());
    }

    @Test
    public void testParseLin() {
        String board = "https://www.bridgebase.com/tools/handviewer.html?lin=st||pn|Beowulf,blueceo,yinyichen,xinyu320|md|1SK2H862D976CKQT97,S4HQJ974DAK5CJ432,SAJT86HAKTDQJT3C8,SQ9753H53D842CA65|sv|e|rh||ah|Board%2019|mb|P|mb|1H|mb|1S|mb|P|mb|2C|mb|P|mb|3N|mb|P|mb|P|mb|P|pc|H5|pc|H2|pc|HJ|pc|HA|pc|DT|pc|D2|pc|D6|pc|DK|pc|S4|pc|ST|pc|SQ|pc|SK|pc|D7|pc|D5|pc|DQ|pc|D4|pc|D3|pc|D8|pc|D9|pc|DA|pc|H4|pc|HT|pc|H3|pc|H6|pc|DJ|pc|C5|pc|C7|pc|C4|pc|SA|pc|S3|pc|S2|pc|H7|pc|C8|pc|CA|pc|C9|pc|C2|pc|S5|pc|CT|pc|C3|pc|S6|pc|HK|pc|C6|pc|H8|pc|H9|pc|SJ|pc|S7|pc|CQ|pc|CJ|pc|S8|pc|S9|pc|CK|pc|HQ|";
        String[] strings = HuffmanCoding.parseLin(board);
//        System.out.println(Arrays.toString(strings));
        assertEquals(186, strings.length);
    }

    /**
     * Encode a whole board and decode it again.
     * <p>
     * NOTE the other encode and decode tests each stay inside a single 64-bit word:
     * testEncode2 asserts only on longs[0], and the decode tests hand-build one word
     * holding one symbol. This test is the one that crosses word boundaries, which is
     * where the packing of a code split across two words has to line up with the
     * unpacking of it.
     * <p>
     * The selector is stripped from the result because encoding is many-to-one:
     * see HuffmanEncoder.get.
     */
    @Test
    public void testRoundTrip() {
        String board = "https://www.bridgebase.com/tools/handviewer.html?lin=st||pn|Beowulf,blueceo,yinyichen,xinyu320|md|1SK2H862D976CKQT97,S4HQJ974DAK5CJ432,SAJT86HAKTDQJT3C8,SQ9753H53D842CA65|sv|e|rh||ah|Board%2019|mb|P|mb|1H|mb|1S|mb|P|mb|2C|mb|P|mb|3N|mb|P|mb|P|mb|P|pc|H5|pc|H2|pc|HJ|pc|HA|pc|DT|pc|D2|pc|D6|pc|DK|pc|S4|pc|ST|pc|SQ|pc|SK|pc|D7|pc|D5|pc|DQ|pc|D4|pc|D3|pc|D8|pc|D9|pc|DA|pc|H4|pc|HT|pc|H3|pc|H6|pc|DJ|pc|C5|pc|C7|pc|C4|pc|SA|pc|S3|pc|S2|pc|H7|pc|C8|pc|CA|pc|C9|pc|C2|pc|S5|pc|CT|pc|C3|pc|S6|pc|HK|pc|C6|pc|H8|pc|H9|pc|SJ|pc|S7|pc|CQ|pc|CJ|pc|S8|pc|S9|pc|CK|pc|HQ|";
        HuffmanCoding.Node code = HuffmanCoding.createHuffmanCoding().generateCode();
        String[] symbols = HuffmanCoding.parseLin(board);

        StringBuilder sb = new StringBuilder();
        for (String symbol : symbols) if (symbol != null) sb.append(symbol);
        String expected = sb.toString();

        Long[] encoded = new HuffmanCoding.HuffmanEncoder(code).encode(symbols);
        assertTrue("the encoding must span several words to be worth testing", encoded.length > 1);
        long[] longs = new long[encoded.length];
        for (int i = 0; i < encoded.length; i++) longs[i] = encoded[i];

        String decoded = new HuffmanCoding.HuffmanDecoder(code).decode(longs);
        assertEquals(expected, decoded.replace("️", ""));
    }

    /**
     * A code that must be split across a word boundary comes back whole.
     * <p>
     * NOTE the padding varies the offset at which the split falls, so that the split
     * lands at a different point within the code each time round. The interesting part
     * is the second half: the bits that did not fit must go into the next word, and only
     * those bits.
     */
    @Test
    public void testEncodeAcrossWordBoundary() {
        HuffmanCoding.Node code = HuffmanCoding.createHuffmanCoding().generateCode();
        HuffmanCoding.HuffmanEncoder encoder = new HuffmanCoding.HuffmanEncoder(code);
        HuffmanCoding.HuffmanDecoder decoder = new HuffmanCoding.HuffmanDecoder(code);
        // XX is the rarest symbol, so it has the longest code: whichever offset it
        // falls at, one of these boards splits it across the boundary.
        for (int pad = 0; pad < 12; pad++) {
            String[] symbols = new String[40 + pad];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < symbols.length - 1; i++) {
                symbols[i] = i < pad ? "P" : "XX";
                sb.append(symbols[i]);
            }
            symbols[symbols.length - 1] = null;
            Long[] encoded = encoder.encode(symbols);
            long[] longs = new long[encoded.length];
            for (int i = 0; i < encoded.length; i++) longs[i] = encoded[i];
            assertEquals("at pad " + pad, sb.toString(), decoder.decode(longs).replace("️", ""));
        }
    }

    @Test
    public void testDecode1() {
        HuffmanCoding huffmanCoding = HuffmanCoding.createHuffmanCoding();
        HuffmanCoding.Node code = huffmanCoding.generateCode();
        HuffmanCoding.HuffmanDecoder huffmanDecoder = new HuffmanCoding.HuffmanDecoder(code);
        long empty = 0b00110110L << 56;
        long[] longs = {empty};
        String s = huffmanDecoder.decode(longs);
        assertEquals("", s);
    }

    @Test
    public void testDecode2() {
        HuffmanCoding huffmanCoding = HuffmanCoding.createHuffmanCoding();
        HuffmanCoding.Node code = huffmanCoding.generateCode();
        HuffmanCoding.HuffmanDecoder huffmanDecoder = new HuffmanCoding.HuffmanDecoder(code);
        long empty = 0b110100110110L << 52;
        long[] longs = {empty};
        String s = huffmanDecoder.decode(longs);
        assertEquals("W", s);
    }


}