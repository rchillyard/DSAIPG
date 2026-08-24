package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The Kml class is responsible for creating a KML (Keyhole Markup Language) file
 * representation of a geographic graph structure. The geographic graph consists
 * of vertices representing geographic points and edges that connect these points,
 * sorted by a sequence attribute.
 *
 * @param <V> the vertex type representing a geographic point, which must implement
 *            the GeoPoint interface
 * @param <E> the edge attribute type, which must implement the Sequenced interface
 *            to allow sorting of edges by a sequence attribute
 */
public class Kml<V extends GeoPoint, E extends Sequenced> {

    /**
     * Creates a KML (Keyhole Markup Language) file representing the graph, including its vertices and edges,
     * and writes it to the specified file.
     * <p>
     * The method generates KML content by iterating through the vertices and edges of the graph.
     * Vertices are represented as KML points, and edges are represented as KML lines. The
     * KML file is written in the following structure:
     * - The preamble is written first (e.g., the KML headers).
     * - Each vertex is processed and added as a point.
     * - Each edge is processed, sorted by sequence, and added as a line.
     * - The colophon is appended at the end of the file.
     *
     * @param file the file to which the KML content will be written. If the file does not exist,
     *             it will be created. If it exists, its content will be overwritten.
     * @throws IOException if an I/O error occurs while creating the file, writing to it, or closing the writer.
     */
    public void createKML(File file) throws IOException {
        boolean x = file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(preamble);
        Iterable<V> vertices = graph.vertices();
        for (V vertex : vertices) writer.write(asPoint(vertex));

        Iterable<Edge<V, E>> edges = graph.edges();
        List<Edge<V, E>> list = new ArrayList<>();
        for (Edge<V, E> edge : edges) list.add(edge);
        list.sort(Comparator.comparingInt(o -> o.getAttribute().getSequence()));
        for (Edge<V, E> edge : list) writer.write(asLine(edge));
        writer.write(colophon);
        writer.close();

    }

    /**
     * Constructor for the Kml class.
     * Initializes a Kml instance with a given graph.
     *
     * @param graph the EdgeGraph instance representing the graph to be processed
     *              in KML (Keyhole Markup Language) format.
     */
    public Kml(EdgeGraph<V, E> graph) {
        this.graph = graph;
    }

    /**
     * Represents the graph structure used by the Kml class to perform operations
     * on vertices and edges based on the EdgeGraph interface. The graph is final,
     * meaning it cannot be reassigned after initialization, and it serves as the
     * primary data structure for modeling and manipulating the underlying graph
     * relationships in the KML file generation process.
     *
     * @param <V> the type of vertices in the graph
     * @param <E> the type of attributes associated with the edges in the graph
     */
    private final EdgeGraph<V, E> graph;

    /**
     * Converts a vertex to a KML (Keyhole Markup Language) string representation for geographic visualization.
     *
     * @param vertex the vertex to be converted, representing a geographic point with a name and position.
     * @return a string containing the KML representation of the vertex, including its name, description,
     *         and coordinates in the form of a Placemark with a Point geometry.
     */
    private String asPoint(V vertex) {
        return "      <Placemark>\n" + "      <name>" + vertex.getName() +
                "</name>\n" +
                "      <description>" + vertex +
                "</description>\n" +
//                "      <styleUrl>#icon-1899-0288D1-nodesc</styleUrl>\n" +
                "      <Point>\n" +
                "        <coordinates>\n" + vertex.getPosition() +
                "         \n" +
                "        </coordinates>\n" +
                "      </Point>\n" +
                "      </Placemark>\n";
    }

    /**
     * Converts the given edge into a KML representation of a LineString element.
     *
     * @param edge the edge to be represented, containing two vertices and an optional attribute
     * @return a formatted string representing the edge as a KML LineString element
     */
    private String asLine(Edge<V, E> edge) {
        V v1 = edge.get();
        V v2 = edge.getOther(v1);

        // TODO understand why this doesn't work
//        Tunnel e = (Tunnel) edge;
//        Building v1 = e.get();
//        Building v2 = e.getOther(v1);

        return "      <Placemark>\n" + "      <name>" + v1.getName() + "--" + v2.getName() +
                "</name>\n" +
                "      <description>" + edge +
                "</description>\n" +
                "      <LineString>\n" +
                "        <tessellate>1</tessellate>\n" +
                "        <coordinates>\n" +
                v1.getPosition() +
                "\n" +
                v2.getPosition() +
                "\n" +
                "        </coordinates>\n" +
                "      </LineString>\n" +
                "      </Placemark>\n";
    }

    /**
     * Represents the KML preamble string that defines the initial structure of a KML document.
     * This string includes the XML declaration, namespace, and an initial `<Document>` element
     * containing a name and description of the document.
     *
     * The preamble is a constant and is used as the starting point for constructing a KML file,
     * typically followed by additional elements and structures specific to the application's purpose.
     * In this case, the document name is "NEU Tunnel System," and the description outlines
     * a potential design for a tunnel system on the Northeastern University Campus in Boston, MA.
     */
    private final static String preamble = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
            "  <Document>\n" +
            "    <name>NEU Tunnel System</name>\n" +
            "    <description>A possible design for a future tunnel system for the Northeastern University Campus in Boston, MA.</description>\n"
//            "    <Style id=\"yellowLineGreenPoly\">\n" +
//            "      <LineStyle>\n" +
//            "        <color>7f00ffff</color>\n" +
//            "        <width>4</width>\n" +
//            "      </LineStyle>\n" +
//            "      <PolyStyle>\n" +
//            "        <color>7f00ff00</color>\n" +
//            "      </PolyStyle>\n" +
//            "    </Style>"
            ;

    private final static String colophon = "  </Document>\n" +
            "</kml>\n";
}


