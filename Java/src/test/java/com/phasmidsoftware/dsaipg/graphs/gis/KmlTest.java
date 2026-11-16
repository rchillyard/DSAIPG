package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.Graph_Edges;
import com.phasmidsoftware.dsaipg.graphs.undirected.Position;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link Kml#createKML}.
 * <p>
 * The point of interest is the encoding. `preamble` declares
 * {@code <?xml version="1.0" encoding="UTF-8"?>}, so the file must actually be written in UTF-8.
 * It used to be written with {@code new FileWriter(file)}, which uses the JVM's default charset —
 * so on any machine where that is not UTF-8, the file asserted an encoding it was not written in,
 * and a single accented character produced a KML that Google Earth rejects.
 */
public class KmlTest {

    /**
     * A building name that is unremarkable in Boston and not representable in ASCII.
     */
    private static final String ACCENTED_NAME = "Café Hallé — Zoë";

    private record Place(String name) implements GeoPoint {
        public String getName() {
            return name;
        }

        public Position getPosition() {
            return new Position() {
                public double getX() {
                    return -71.0896;
                }

                public double getY() {
                    return 42.3398;
                }
            };
        }
    }

    private static final class Link implements Sequenced {
        private int sequence;

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }
    }

    private File writeKml() throws IOException {
        Graph_Edges<Place, Link> graph = new Graph_Edges<>();
        graph.addEdge(new Edge<>(new Place(ACCENTED_NAME), new Place("Plain"), new Link()));
        File file = Files.createTempFile("kmltest", ".kml").toFile();
        file.deleteOnExit();
        new Kml<Place, Link>(graph).createKML(file);
        return file;
    }

    /**
     * The file must be readable as UTF-8 — which is what it says it is — with the name intact.
     * Read as raw bytes and decoded explicitly, so the test does not depend on the default charset
     * any more than the code under test should.
     */
    @Test
    public void createKMLWritesUtf8AsItsPreambleClaims() throws IOException {
        File file = writeKml();
        String contents = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        assertTrue("the preamble should declare UTF-8", contents.contains("encoding=\"UTF-8\""));
        assertTrue("the accented name should survive a UTF-8 round trip, but the file read as:\n"
                + contents.substring(0, Math.min(400, contents.length())), contents.contains(ACCENTED_NAME));
    }

    /**
     * The writer must be closed even though nothing here forces an exception — if it were leaked,
     * the content might not be flushed at all.
     */
    @Test
    public void createKMLFlushesAndClosesItsWriter() throws IOException {
        File file = writeKml();
        String contents = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        assertTrue("the colophon should be present, which means the writer was flushed",
                contents.contains("</kml>"));
    }
}
