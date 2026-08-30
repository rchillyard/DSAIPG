from src.compression.huffman_coding import HuffmanCoding, HuffmanDecoder, HuffmanEncoder


def test_generate_code():
    huffman = HuffmanCoding.create_default()
    tree = huffman.build_tree()
    assert tree is not None


def test_create_huffman_coding():
    huffman = HuffmanCoding.create_default()
    assert huffman is not None


def test_show_tree(capsys):
    huffman = HuffmanCoding.create_default()
    tree = huffman.build_tree()
    # show_tree prints to stdout; just ensure it runs without error
    HuffmanCoding.show_tree(tree)
    captured = capsys.readouterr()
    assert captured.out  # some output produced


def test_encode1():
    huffman = HuffmanCoding.create_default()
    tree = huffman.build_tree()
    encoder = HuffmanEncoder(tree)
    longs = encoder.encode([None])
    assert len(longs) == 1
    assert longs[0] == 0x3600000000000000


def test_encode2():
    huffman = HuffmanCoding.create_default()
    tree = huffman.build_tree()
    encoder = HuffmanEncoder(tree)
    decoder = HuffmanDecoder(tree)
    board = (
        "https://www.bridgebase.com/tools/handviewer.html?lin=st||pn|Beowulf,blueceo,yinyichen,xinyu320|md|"
        "1SK2H862D976CKQT97,S4HQJ974DAK5CJ432,SAJT86HAKTDQJT3C8,SQ9753H53D842CA65|sv|e|rh||"
        "ah|Board%2019|mb|P|mb|1H|mb|1S|mb|P|mb|2C|mb|P|mb|3N|mb|P|mb|P|mb|P|"
        "pc|H5|pc|H2|pc|HJ|pc|HA|pc|DT|pc|D2|pc|D6|pc|DK|pc|S4|pc|ST|pc|SQ|pc|SK|pc|"
        "D7|pc|D5|pc|DQ|pc|D4|pc|D3|pc|D8|pc|D9|pc|DA|pc|H4|pc|HT|pc|H3|pc|H6|pc|DJ|pc|"
        "C5|pc|C7|pc|C4|pc|SA|pc|S3|pc|S2|pc|H7|pc|C8|pc|CA|pc|C9|pc|C2|pc|S5|pc|CT|pc|C3|pc|"
        "S6|pc|HK|pc|C6|pc|H8|pc|H9|pc|SJ|pc|S7|pc|CQ|pc|CJ|pc|S8|pc|S9|pc|CK|pc|HQ|"
    )
    assert len(board) == 560
    strings = HuffmanCoding.parse_lin(board)
    assert len(strings) == 186
    longs = encoder.encode(strings)
    assert len(longs) == 15

    # NOTE the Java asserts on longs[0], which this cannot do: Node.compareTo there
    # compares frequency alone, so its PriorityQueue orders equal-frequency nodes
    # arbitrarily, while Node here breaks the tie by insertion order. Both codes are
    # optimal and of the same expected length, but they are not the same code. Round
    # tripping tests the property that matters and does not depend on the tie.
    decoded = decoder.decode(longs)
    # Encoding is many-to-one -- the suits are held with a U+FE0F variation selector,
    # and "S" finds "S️" -- so a round trip returns the selector whether or not the
    # input had it. Strip it before comparing.
    expected = ''.join(s for s in strings if s is not None)
    assert decoded.replace('️', '') == expected


def test_encode_across_word_boundary():
    """
    A code split across a 64-bit word boundary comes back whole.

    NOTE every other encode and decode test here stays inside a single word, which is
    why they can all pass while the packing and the unpacking disagree about what to do
    at a boundary. Varying the padding puts the split at a different point within the
    code each time round.
    """
    tree = HuffmanCoding.create_default().build_tree()
    encoder = HuffmanEncoder(tree)
    decoder = HuffmanDecoder(tree)
    for pad in range(12):
        # XX is the rarest symbol, so it carries the longest code.
        symbols = ["P"] * pad + ["XX"] * (39 - pad)
        longs = encoder.encode(symbols + [None])
        assert decoder.decode(longs).replace('️', '') == ''.join(symbols), f"at pad {pad}"


def test_parse_lin():
    board = (
        "https://www.bridgebase.com/tools/handviewer.html?lin=st||pn|Beowulf,blueceo,yinyichen,xinyu320|md|"
        "1SK2H862D976CKQT97,S4HQJ974DAK5CJ432,SAJT86HAKTDQJT3C8,SQ9753H53D842CA65|sv|e|rh||"
        "ah|Board%2019|mb|P|mb|1H|mb|1S|mb|P|mb|2C|mb|P|mb|3N|mb|P|mb|P|mb|P|"
        "pc|H5|pc|H2|pc|HJ|pc|HA|pc|DT|pc|D2|pc|D6|pc|DK|pc|S4|pc|ST|pc|SQ|pc|SK|pc|"
        "D7|pc|D5|pc|DQ|pc|D4|pc|D3|pc|D8|pc|D9|pc|DA|pc|H4|pc|HT|pc|H3|pc|H6|pc|DJ|pc|"
        "C5|pc|C7|pc|C4|pc|SA|pc|S3|pc|S2|pc|H7|pc|C8|pc|CA|pc|C9|pc|C2|pc|S5|pc|CT|pc|C3|pc|"
        "S6|pc|HK|pc|C6|pc|H8|pc|H9|pc|SJ|pc|S7|pc|CQ|pc|CJ|pc|S8|pc|S9|pc|CK|pc|HQ|"
    )
    strings = HuffmanCoding.parse_lin(board)
    assert len(strings) == 186


def test_decode1():
    huffman = HuffmanCoding.create_default()
    tree = huffman.build_tree()
    decoder = HuffmanDecoder(tree)
    empty = 0b00110110 << 56
    result = decoder.decode([empty])
    assert result == ""


def test_decode2():
    huffman = HuffmanCoding.create_default()
    tree = huffman.build_tree()
    decoder = HuffmanDecoder(tree)
    empty = 0b110100110110 << 52
    result = decoder.decode([empty])
    assert result == "W"
