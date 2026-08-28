package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.tunnels.Building;
import com.phasmidsoftware.dsaipg.graphs.tunnels.BuildingLoader;
import com.phasmidsoftware.dsaipg.graphs.tunnels.TunnelProperties;
import com.phasmidsoftware.dsaipg.graphs.tunnels.Tunnels_Kruskal;
import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.util.PrivateMethodTester;
import com.phasmidsoftware.dsaipg.util.iteration.SizedIterable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.TestRule;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;

public class GeoGraphSphericalTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    private static final int oneNauticalMile = 1852; // (i.e. one minute of latitude) in meters
    private static final int oneDegreeLongAtEquator = 111321; // in meters
    public static final int londonToBoston = 5239657; // in metres (assuming spherical earth)
    private Building sn;
    private Tunnels_Kruskal ts;
    private GeoKruskal<Building, TunnelProperties> kruskal;
    private final Building la = new Building(34, "LA", "Centennial", -71.0930697, 42.3384215, false, "Lake Hall");
    private final Building ka = new Building(35, "KA", "Plaza", -71.0931943, 42.3386223, false, "Kariotis Hall");
    private final Edge<Building, TunnelProperties> laka = new GeoEdge<>(la, ka, new TunnelProperties(29510, 25, 1, 0));
    private final Building ri = new Building(42, "RI", "Center", -71.0887314, 42.3397321, true, "Richards Hall");
    private final Building ha = new Building(53, "HA", "Center", -71.0885712, 42.3395146, true, "Hayden Hall");
    private final Edge<Building, TunnelProperties> riha = new Edge<>(ri, ha, new TunnelProperties(276L, 28, 0, 0));
    final GeoPoint london = new MockGeoPoint("London", new Position_Spherical(51.5, -0.5)); // Heathrow (approx) 51°28′14″N, 0°27′42″W
    final GeoPoint boston = new MockGeoPoint("Boston", new Position_Spherical(42.35, -71)); // Logan (approx) 42°21′51″N, 71°0′18″W
    final GeoPoint dublin = new MockGeoPoint("Dublin", new Position_Spherical(53.4, -6.3));


    @Before
    public void setUp() {
        ts = new Tunnels_Kruskal(BuildingLoader.createBuildings());
        PrivateMethodTester tsTester = new PrivateMethodTester(ts);
        kruskal = (GeoKruskal<Building, TunnelProperties>) tsTester.invokePrivate("getKruskal");
    }


    @After
    public void tearDown() {
    }

    @Test
    public void goeEdges() {
        Geo<Building, TunnelProperties> mst = kruskal.getGeoMST(new GeoGraphSpherical<>());
        SizedIterable<Edge<Building, TunnelProperties>> edges = mst.geoEdges();
        assertEquals(79, edges.size());
        Iterator<Edge<Building, TunnelProperties>> iterator = kruskal.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(riha, iterator.next());
    }

    /**
     * edges() reports each edge once, however many vertices it touches. Since the
     * fix that made Graph_Edges properly undirected, an edge sits in the adjacency
     * bag of BOTH its endpoints, so this is the assertion that stops it being
     * double counted.
     */
    @Test
    public void edges() {
        GeoGraphSpherical<GeoPoint, String> graph = new GeoGraphSpherical<>();
        assertEquals("an empty graph has no edges", 0, graph.edges().size());
        graph.addEdge(new GeoEdge<>(london, boston, "across the pond"));
        assertEquals(1, graph.edges().size());
        assertEquals("but it is adjacent to each of its two ends",
                1, ((SizedIterable<?>) graph.adjacent(london)).size());
        assertEquals(1, ((SizedIterable<?>) graph.adjacent(boston)).size());
        graph.addEdge(new GeoEdge<>(boston, dublin, "the shorter hop"));
        assertEquals(2, graph.edges().size());
        assertEquals("Boston is now on two of them",
                2, ((SizedIterable<?>) graph.adjacent(boston)).size());
    }

    /**
     * Adding an edge introduces whichever of its vertices the graph has not met.
     */
    @Test
    public void addEdge() {
        GeoGraphSpherical<GeoPoint, String> graph = new GeoGraphSpherical<>();
        graph.addEdge(new GeoEdge<>(london, boston, "across the pond"));
        assertEquals(2, graph.vertices().size());
        graph.addEdge(new GeoEdge<>(boston, dublin, "the shorter hop"));
        assertEquals("Boston was already here, so only Dublin is new",
                3, graph.vertices().size());
        graph.addEdge(new GeoEdge<>(london, boston, "by another route"));
        assertEquals("a second edge between the same pair adds no vertex",
                3, graph.vertices().size());
        assertEquals("but it is a second edge", 3, graph.edges().size());
    }

    /**
     * The string form is the adjacency map: each vertex against the bag of edges at
     * it. Asserted by what it contains rather than in full, because the vertices
     * come out in hash order and the bags in random order.
     */
    @Test
    public void toStringTest() {
        GeoGraphSpherical<GeoPoint, String> graph = new GeoGraphSpherical<>();
        graph.addEdge(new GeoEdge<>(london, boston, "across the pond"));
        String s = graph.toString();
        assertTrue(s, s.startsWith("{") && s.endsWith("}"));
        assertTrue("both vertices appear", s.contains("London") && s.contains("Boston"));
        assertTrue("and each holds the edge", s.contains("London-Boston: across the pond"));
        assertEquals("once for each end of it, since the graph is undirected",
                2, s.split("across the pond", -1).length - 1);
    }

    @Test
    public void vertices() {
        GeoGraphSpherical<GeoPoint, String> graph = new GeoGraphSpherical<>();
        assertEquals(0, graph.vertices().size());
        graph.addEdge(new GeoEdge<>(london, boston, "across the pond"));
        List<GeoPoint> vertices = new ArrayList<>();
        for (GeoPoint v : graph.vertices()) vertices.add(v);
        assertEquals(2, vertices.size());
        assertTrue(vertices.contains(london) && vertices.contains(boston));
    }

    @Test
    public void length0() {
        GeoGraphSpherical<GeoPoint, Object> graph = new GeoGraphSpherical<>();
        MockGeoPoint oneDegreeNorth = new MockGeoPoint("OneDegreeNorth", new Position_Spherical(1, 0));
        MockGeoPoint equator = new MockGeoPoint("EquatorialMeridian", new Position_Spherical(0, 0));
        double polarDistance = graph.getDistance(oneDegreeNorth, equator);
        assertEquals(oneNauticalMile * 60, polarDistance, 200);
    }

    @Test
    public void length1() {
        GeoGraphSpherical<GeoPoint, Object> graph = new GeoGraphSpherical<>();
        MockGeoPoint oneDegreeEast = new MockGeoPoint("OneDegreeEast", new Position_Spherical(0, 1));
        MockGeoPoint equator = new MockGeoPoint("EquatorialMeridian", new Position_Spherical(0, 0));
        double polarDistance = graph.getDistance(oneDegreeEast, equator);
        assertEquals(oneDegreeLongAtEquator, polarDistance, 4);
    }

    @Test
    public void length2() {
        GeoGraphSpherical<GeoPoint, Object> graph = new GeoGraphSpherical<>();
        MockGeoPoint north_pole = new MockGeoPoint("North Pole", new Position_Spherical(90, 0));
        MockGeoPoint south_pole = new MockGeoPoint("South Pole", new Position_Spherical(-90, 0));
        double polarDistance = graph.getDistance(north_pole, south_pole);
        assertEquals(20000000, polarDistance, 100000);
    }

    @Test
    public void length3() {
        Geo<Building, TunnelProperties> graph = kruskal.getGeoMST(new GeoGraphSpherical<>());
        Iterable<Edge<Building, TunnelProperties>> geoEdges = graph.geoEdges();
        assertEquals(25, graph.length(laka), 1);
    }

    @Test
    public void length4() {
        GeoGraphSpherical<GeoPoint, Object> graph = new GeoGraphSpherical<>();
        double bostonLondonApprox = graph.getDistance(boston, london);
        assertEquals(londonToBoston, bostonLondonApprox, 4000);
    }

    /**
     * Two properties that hold wherever the points are, unlike the four length
     * tests above which each pin down one particular distance: a point is no
     * distance from itself, and the distance is the same measured either way.
     */
    @Test
    public void getDistance() {
        GeoGraphSpherical<GeoPoint, Object> graph = new GeoGraphSpherical<>();
        assertEquals("nowhere is any distance from itself", 0, graph.getDistance(boston, boston), 1E-6);
        assertEquals("and the sea is as wide going back",
                graph.getDistance(london, boston), graph.getDistance(boston, london), 1E-6);
        assertEquals("Boston to London is the distance the other tests measure",
                londonToBoston, graph.getDistance(boston, london), 4000);
    }


}