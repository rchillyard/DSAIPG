package com.phasmidsoftware.dsaipg.graphs.tunnels;

import com.phasmidsoftware.dsaipg.graphs.gis.*;
import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The Tunnels_Northeastern class is responsible for managing the tunnel system
 * between various buildings on the Northeastern University campus. It uses
 * graph-based algorithms to compute minimum spanning trees (MSTs), process
 * geographical data, compute tunnel properties, and analyze cost factors and
 * zone information for efficient construction and tunneling.
 * <p>
 * This class includes functionalities for:
 * - Configuring zones and tunnel data.
 * - Loading building-related information.
 * - Generating graphs of potential building connections based on predicates.
 * - Calculating the MST of the graph using algorithms like Prim, Kruskal,
 * or Borůvka adapted for geographical settings.
 * - Creating geographical representations and exporting them in formats like KML.
 */
public class Tunnels_Northeastern implements Iterable<Edge<Building, TunnelProperties>> {

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    public Iterator<Edge<Building, TunnelProperties>> iterator() {
        return mst.iterator();
    }

    /**
     * Constructs an instance of the Tunnels_Northeastern class. This constructor initializes
     * the zones and tunnel data, loads the buildings, creates a graph with all possible edges
     * satisfying the provided predicate, and computes the minimum spanning tree (MST) of the graph.
     * Additionally, it generates a geographical representation of the MST using spherical graph
     * computations.
     *
     * @param mstFunction a function that accepts an edge graph of buildings and tunnel properties
     *                    as input and returns a minimum spanning tree (MST) of buildings and
     *                    tunnel properties.
     */
    public Tunnels_Northeastern(Function<EdgeGraph<Building, TunnelProperties>, MST<Building, TunnelProperties>> mstFunction) {
        setupZones();
        setupTunnels();
        List<Building> buildings = BuildingLoader.createBuildings();
        // We arbitrarily limit the the length of any tunnel to 250m
        graph = createGraph(buildings, e -> e.getAttribute().length <= 250);
        mst = mstFunction.apply(graph);
        GeoMST<Building, TunnelProperties> geoMST = (GeoMST<Building, TunnelProperties>) mst;
        geo = geoMST.getGeoMST(new GeoGraphSpherical<>());
    }

    /**
     * Represents the Minimum Spanning Tree (MST) structure containing the connections
     * between {@code Building} objects using edges defined by {@code TunnelProperties}.
     * This field is used to efficiently calculate and represent the minimum-cost network
     * of tunnels required to connect multiple buildings in the campus.
     * <p>
     * The MST is implemented as a generic type parameterized by {@code Building} and
     * {@code TunnelProperties}, where:
     * - {@code Building}: Represents the nodes or vertices in the graph.
     * - {@code TunnelProperties}: Represents the properties or attributes of the edges
     * connecting the buildings, such as cost, length, or construction phase.
     * <p>
     * This field is immutable and initialized through the provided MST creation function,
     * ensuring that the optimal spanning tree is calculated based on the input graph.
     */
    private final MST<Building, TunnelProperties> mst;
    /**
     * Represents a geographical structure or mapping of connected entities on a campus,
     * where each entity is a {@code Building} and their connecting properties are defined
     * by {@code TunnelProperties}. This variable stores a graph representation of
     * the tunnels system, which includes nodes (buildings) and their associated
     * properties (like cost, length, and phase of construction) for connectivity.
     * <p>
     * The {@code Geo<Building, TunnelProperties>} instance provides functionality
     * for modeling, managing, and querying a geographical graph, enabling operations
     * such as pathfinding, computation of cost and distance, and generation of tunnel
     * blueprints.
     */
    Geo<Building, TunnelProperties> geo;
    /**
     * Represents a graph structure where nodes are buildings and edges are tunnels with associated properties.
     * The graph is geo-referenced and used to model the connectivity among buildings within a tunnel system.
     * <p>
     * The graph consists of nodes of type {@link Building} and edges defined by {@link TunnelProperties}.
     * It supports operations to calculate metrics like cost and length of tunnels, and is integral to
     * constructing a Minimum Spanning Tree (MST) for optimizing tunnel connections.
     * <p>
     * This variable is central to the application's functionality, serving as the primary container
     * for representing and processing all possible building connections.
     */
    EdgeGraph<Building, TunnelProperties> graph;

    /**
     * The entry point for the application. This method initializes a geo-referenced
     * graph representation of tunnels, processes the edges to calculate total cost
     * and length, outputs the data related to the tunnels, and generates a KML file
     * representing the graph.
     *
     * @param args command line arguments passed to the application; not used in this method.
     * @throws IOException if an I/O error occurs during the creation of the KML file.
     */
    public static void main(String[] args) throws IOException {
        doMain();
    }

    static void doMain() throws IOException {
        Tunnels_Northeastern tunnels = new Tunnels_Northeastern(GeoKruskal::new);
        double totalCost = 0.;
        double totalLength = 0.;
        SizedIterable<Edge<Building, TunnelProperties>> edges = tunnels.geo.geoEdges();
        for (Edge<Building, TunnelProperties> e : edges) {
            totalCost += e.getAttribute().cost;
            totalLength += e.getAttribute().length;
            System.out.println(e);
        }
//        SizedIterable<Edge<Building, TunnelProperties>> edges = kruskal.getGeoMST(new GeoGraphSpherical<>()).geoEdges();

        Kml<Building, TunnelProperties> kml = new Kml<>(tunnels.graph); // CONSIDER does this have the right data?
        String filename = "tunnels.kml";
        kml.createKML(new File(filename));
        System.out.println("Tunnels_Northeastern output to KML file: " + filename);
        System.out.println("Total cost: " + totalCost + ", total length: " + totalLength);
    }

    /**
     * Creates a geographical representation of a Borůvka's algorithm for computing
     * Minimum Spanning Trees (MST) of a given graph.
     *
     * @param graph the edge graph consisting of buildings as nodes and tunnel properties as edges
     * @return an instance of {@code GeoBoruvka} initialized with the provided graph
     */
    @NotNull
    static GeoBoruvka<Building, TunnelProperties> createGeoBoruvka(EdgeGraph<Building, TunnelProperties> graph) {
        return new GeoBoruvka<>(graph);
    }

    /**
     * Creates an instance of GeoPrim for the specified edge graph. GeoPrim is a geographical
     * representation of Prim's algorithm, used to compute minimum spanning trees for a graph
     * consisting of buildings and their associated tunnel properties.
     *
     * @param graph the edge graph consisting of buildings as nodes and tunnel properties as edges
     * @return a GeoPrim instance configured with the provided edge graph
     */
    @NotNull
    static GeoPrim<Building, TunnelProperties> createGeoPrim(EdgeGraph<Building, TunnelProperties> graph) {
        return new GeoPrim<>(graph);
    }

    /**
     * Creates a geographical representation of the minimum spanning tree (MST) using the Kruskal algorithm.
     * This method initializes and returns an instance of {@code GeoKruskal} with the provided edge graph.
     *
     * @param graph the edge graph containing buildings as vertices and tunnel properties as edge attributes.
     * @return a {@code GeoKruskal} instance representing the computation of the MST.
     */
    @NotNull
    static GeoKruskal<Building, TunnelProperties> createGeoKruskal(EdgeGraph<Building, TunnelProperties> graph) {
        return new GeoKruskal<>(graph);
    }

    /**
     * Create a graph consisting of all possible edges connection the buildings.
     * The resulting graph should contain N(N-1)/2 edges where N is length of the list buildings.
     *
     * @param buildings a list of Buildings.
     * @param predicate the predicate: only edges satisfying this predicate will be added to the graph.
     * @return a Geo&lt;Building, TunnelProperties&gt;
     */
    private static EdgeGraph<Building, TunnelProperties> createGraph(List<Building> buildings, Predicate<Edge<Building, TunnelProperties>> predicate) {
        GeoGraphSpherical<Building, TunnelProperties> graph = new GeoGraphSpherical<>();
        int len = buildings.size();
        for (int i = 0; i < len; i++) {
            Building b1 = buildings.get(i);
            for (int j = i + 1; j < len; j++) {
                Building b2 = buildings.get(j);
                double length = graph.getDistance(b1, b2);
                graph.addEdge(b1, b2, getTunnelProperties(b1, b2, length), predicate);
            }
        }
        SizedIterable<Edge<Building, TunnelProperties>> edges = graph.edges();
        System.out.println("created " + edges.size() + " edges");
        return graph;
    }

    /**
     * Constructs and returns a TunnelProperties object representing the properties of a tunnel
     * between two buildings based on their relationship and the specified tunnel length.
     *
     * @param b1     the first building involved in the tunnel
     * @param b2     the second building involved in the tunnel
     * @param length the length of the tunnel in meters
     * @return a TunnelProperties object containing the computed cost, length, phase, and sequence of the tunnel
     */
    private static TunnelProperties getTunnelProperties(Building b1, Building b2, double length) {
        return new TunnelProperties(Math.round(getCostFactor(b1, b2) * length), (int) Math.round(length), getPhase(b1, b2), 0);
    }

    /**
     * Determine when the tunnel should be built (0 implies existing).
     *
     * @param b1 building at one end.
     * @param b2 building at other end.
     * @return 0 if the tunnel is existing
     */
    private static int getPhase(Building b1, Building b2) {
        if (b1.isAlreadyTunneled && b2.isAlreadyTunneled && connected(b1, b2)) return 0;
        return 1; // TODO create later phases
    }

    /**
     * Determines whether two buildings are directly connected by an existing tunnel.
     *
     * @param b1 the first building to check connectivity for
     * @param b2 the second building to check connectivity for
     * @return true if there is an existing tunnel connecting b1 and b2, false otherwise
     */
    private static boolean connected(Building b1, Building b2) {
        for (Tunnels_Kruskal.ExistingTunnel tunnel : tunnels) if (tunnel.matches(b1, b2)) return true;
        return false;
    }

    /**
     * Get the cost factor in $ per meter.
     *
     * @param b1 building at one end.
     * @param b2 building at other end.
     * @return the cost factor.
     */
    private static int getCostFactor(Building b1, Building b2) {
        if (getPhase(b1, b2) == 0) return 10;
        if (Objects.equals(b1.zone, b2.zone)) return 1000;
        return crossZoneExpense(b1.zone, b2.zone);
    }

    /**
     * Calculates the expense of crossing from one zone to another based on predefined zone crossing costs.
     *
     * @param zone1 the name of the first zone
     * @param zone2 the name of the second zone
     * @return the cost factor associated with crossing between the two specified zones,
     * or a default value of 10000 if the zones do not have a specific crossing cost
     */
    private static int crossZoneExpense(String zone1, String zone2) {
        int i1 = zones.indexOf(zone1);
        int i2 = zones.indexOf(zone2);
        Tunnels_Kruskal.ZoneCross cross = new Tunnels_Kruskal.ZoneCross(i1, i2, 0);

        if (cross.equals(railroad)) return railroad.costFactor;
        if (cross.equals(huntAve1)) return huntAve1.costFactor;
        if (cross.equals(huntAve2)) return huntAve2.costFactor;
        if (cross.equals(massAve1)) return massAve1.costFactor;
        if (cross.equals(massAve2)) return massAve2.costFactor;
        if (cross.equals(forsyth1)) return forsyth1.costFactor;
        if (cross.equals(forsyth2)) return forsyth2.costFactor;
        if (cross.equals(leon1)) return leon1.costFactor;
        if (cross.equals(leon2)) return leon2.costFactor;
        if (cross.equals(leon3)) return leon3.costFactor;
        if (cross.equals(hemenway)) return hemenway.costFactor;
        if (cross.equals(columbus)) return columbus.costFactor;
        if (cross.equals(gainsboro1)) return gainsboro1.costFactor;
        return 10000;
    }

    /**
     * Represents the crossing between two zones and the associated cost factor.
     * This class is used to model a zone connection with its respective expense.
     */
    static class ZoneCross {

        /**
         * Determines whether the specified object is equal to the current ZoneCross object.
         * Two ZoneCross objects are considered equal if they have the same zones regardless of the order.
         *
         * @param o the object to be compared for equality with the current ZoneCross object
         * @return true if the specified object is equal to the current ZoneCross object, false otherwise
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tunnels_Kruskal.ZoneCross zoneCross = (Tunnels_Kruskal.ZoneCross) o;
            return zone1 == zoneCross.zone1 &&
                    zone2 == zoneCross.zone2 || zone1 == zoneCross.zone2 &&
                    zone2 == zoneCross.zone1;
        }

        /**
         * Computes the hash code of the ZoneCross object.
         * The hash code is calculated based on the combination of its zone identifiers, ensuring that
         * the order of the zones does not affect the hash value.
         *
         * @return an integer representing the hash code of the ZoneCross object
         */
        @Override
        public int hashCode() {
            return Objects.hash(zone1, zone2) + Objects.hash(zone2, zone1);
        }

        /**
         * Constructs a ZoneCross object representing the crossing between two zones,
         * including the associated cost factor.
         *
         * @param zone1      the first zone in the crossing
         * @param zone2      the second zone in the crossing
         * @param costFactor the cost factor associated with crossing between the two zones
         */
        ZoneCross(int zone1, int zone2, int costFactor) {
            this.zone1 = zone1;
            this.zone2 = zone2;
            this.costFactor = costFactor;
        }

        /**
         * Represents the identifier of the first zone in a connection.
         * This field is used to define one of the two zones forming the crossing.
         * It is a unique identifier for a specific zone in the context of zone connections.
         */
        private final int zone1;
        /**
         * Represents the second zone of a connection in the ZoneCross relationship.
         * This variable is used to define the endpoint of a zone-to-zone crossing.
         * It is a final integer value that remains constant for the lifetime of the ZoneCross object.
         */
        private final int zone2;
        /**
         * Represents the cost factor associated with crossing between two zones.
         * This value indicates the expense or difficulty involved in traversing the connection
         * between the zones and is used as an edge weight in graph-based computations.
         */
        private final int costFactor;
    }

    private static final Tunnels_Kruskal.ZoneCross railroad = new Tunnels_Kruskal.ZoneCross(0, 8, 3000);
    private static final Tunnels_Kruskal.ZoneCross huntAve1 = new Tunnels_Kruskal.ZoneCross(0, 2, 2500);
    private static final Tunnels_Kruskal.ZoneCross huntAve2 = new Tunnels_Kruskal.ZoneCross(10, 11, 2500);
    private static final Tunnels_Kruskal.ZoneCross massAve1 = new Tunnels_Kruskal.ZoneCross(10, 12, 2500);
    private static final Tunnels_Kruskal.ZoneCross massAve2 = new Tunnels_Kruskal.ZoneCross(6, 11, 2500);
    private static final Tunnels_Kruskal.ZoneCross gainsboro1 = new Tunnels_Kruskal.ZoneCross(6, 0, 1100);
    private static final Tunnels_Kruskal.ZoneCross forsyth1 = new Tunnels_Kruskal.ZoneCross(0, 5, 1500);
    private static final Tunnels_Kruskal.ZoneCross forsyth2 = new Tunnels_Kruskal.ZoneCross(3, 0, 1500);
    private static final Tunnels_Kruskal.ZoneCross hemenway = new Tunnels_Kruskal.ZoneCross(1, 2, 1500);
    private static final Tunnels_Kruskal.ZoneCross leon1 = new Tunnels_Kruskal.ZoneCross(4, 3, 1200);
    private static final Tunnels_Kruskal.ZoneCross leon2 = new Tunnels_Kruskal.ZoneCross(4, 5, 1200);
    private static final Tunnels_Kruskal.ZoneCross leon3 = new Tunnels_Kruskal.ZoneCross(3, 5, 1200);
    private static final Tunnels_Kruskal.ZoneCross columbus = new Tunnels_Kruskal.ZoneCross(7, 8, 1750);

    /**
     * Initializes the list of zones for the Tunnels_Northeastern class.
     * This method populates the `zones` list with predefined zone names, assigning each a specific index.
     * The zones defined represent various locations or regions related to the tunnel system.
     * The indices are used to reference these zones programmatically.
     */
    private static void setupZones() {
        zones.add(0, "Center");
        zones.add(1, "Fenway");
        zones.add(2, "North");
        zones.add(3, "Plaza");
        zones.add(4, "West Village");
        zones.add(5, "Centennial");
        zones.add(6, "Matthews");
        zones.add(7, "Columbus");
        zones.add(8, "Strip");
        zones.add(9, "St. Stephens");
        zones.add(10, "Pool");
        zones.add(11, "Theater");
        zones.add(12, "Symphony");
    }

    /**
     * Initializes the list of pre-existing tunnels for the Tunnels_Northeastern class.
     * This method adds predefined tunnel connections between specific points to the `tunnels` collection.
     * Each tunnel is represented as an instance of the `Tunnels_Kruskal.ExistingTunnel` class,
     * specifying the connection between two locations using their identifiers.
     */
    private static void setupTunnels() {
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(55, 58));
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(55, 54));
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(53, 54));
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(53, 59));
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(53, 55));
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(53, 42));
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(53, 41));
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(53, 52));
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(52, 50));
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(52, 43));
        tunnels.add(new Tunnels_Kruskal.ExistingTunnel(52, 48));
    }

    /**
     * Represents an existing tunnel connection between two map identifiers.
     * This class is used to check if a tunnel exists between two specific buildings
     * based on their respective map identifiers.
     */
    static class ExistingTunnel {

        /**
         * Determines if there is an existing tunnel connection between two buildings
         * based on their associated map identifiers.
         *
         * @param b1 the first building to check
         * @param b2 the second building to check
         * @return true if the two buildings belong to the maps associated with this tunnel, false otherwise
         */
        boolean matches(Building b1, Building b2) {
            return b1.getMap() == map1 && b2.getMap() == map2 || b1.getMap() == map2 && b2.getMap() == map1;
        }

        /**
         * Constructs an ExistingTunnel instance that represents a connection between two maps.
         *
         * @param map1 the identifier of the first map
         * @param map2 the identifier of the second map
         */
        ExistingTunnel(int map1, int map2) {
            this.map1 = map1;
            this.map2 = map2;
        }

        /**
         * Represents the identifier of the first map associated with a tunnel connection.
         * This value is used to determine if a tunnel exists between two buildings
         * based on their respective map identifiers.
         */
        private final int map1;
        /**
         * Represents the identifier of the second map in a tunnel connection.
         * This variable is used to determine if a tunnel exists between two specific buildings
         * that are associated with this map identifier.
         */
        private final int map2;
    }

    /**
     * A static, final list that stores a collection of pre-existing tunnels represented as
     * `Tunnels_Kruskal.ExistingTunnel` objects. Each element in the list defines a tunnel
     * connecting two specified locations, including relevant attributes of the tunnel.
     * <p>
     * This list is primarily used to manage and reference established tunnel connections
     * within the larger tunnel system for the Tunnels_Northeastern class. It is initialized
     * during the setup process executed by the `setupTunnels` method.
     */
    private static final List<Tunnels_Kruskal.ExistingTunnel> tunnels = new ArrayList<>();
    /**
     * A list representing the zones associated with the tunnel system in the Tunnels_Northeastern class.
     * This list is initialized during the setup process and contains predefined zone names.
     * The `zones` list is used to reference and manage specific locations or regions related to tunnels.
     */
    private static final List<String> zones = new ArrayList<>();
}
