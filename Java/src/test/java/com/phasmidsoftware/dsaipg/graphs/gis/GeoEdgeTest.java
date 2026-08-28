package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.*;

public class GeoEdgeTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    private final GeoPoint london = new MockGeoPoint("London", new Position_Spherical(51.5, 0));
    private final GeoPoint boston = new MockGeoPoint("Boston", new Position_Spherical(42.3, -71));
    private final Edge<GeoPoint, String> target = new GeoEdge<>(london, boston, "across the pond");

    @Test
    public void getAttribute() {
        assertEquals("across the pond", target.getAttribute());
        assertNull("an edge may carry nothing at all",
                new GeoEdge<GeoPoint, String>(london, boston, null).getAttribute());
    }

    /**
     * An undirected edge has no from and no to, but it does have to hand back one
     * of its two vertices when asked, so that getOther has something to work from.
     * It is always the first one given to the constructor.
     */
    @Test
    public void get() {
        assertEquals(london, target.get());
        assertEquals(boston, new GeoEdge<>(boston, london, "the other way").get());
    }

    @Test
    public void getOther() {
        assertEquals(boston, target.getOther(london));
        assertEquals(london, target.getOther(boston));
        assertEquals("the far end of the edge from whichever end you name",
                target.getOther(target.get()), boston);
    }

    @Test
    public void equalsTest() {
        Edge<GeoPoint, String> target1 = new GeoEdge<>(london, boston, "across the pond");
        Edge<GeoPoint, String> target2 = new GeoEdge<>(boston, london, "across the pond");
        assertEquals(target1, target2);
        assertNotEquals("the attribute counts too",
                target1, new GeoEdge<>(london, boston, "by another route"));
    }

    /**
     * equals ignores which way round the vertices are, so hashCode must too --
     * otherwise two equal edges could land in different buckets, and a HashSet
     * would hold both.
     */
    @Test
    public void hashCodeTest() {
        Edge<GeoPoint, String> forwards = new GeoEdge<>(london, boston, "across the pond");
        Edge<GeoPoint, String> backwards = new GeoEdge<>(boston, london, "across the pond");
        assertEquals(forwards, backwards);
        assertEquals(forwards.hashCode(), backwards.hashCode());
    }

    @Test
    public void toStringTest() {
        assertEquals("London-Boston: across the pond", target.toString());
    }

    /**
     * create makes a GeoEdge out of an ordinary Edge, keeping both vertices and the
     * attribute, and keeping them the same way round.
     */
    @Test
    public void create() {
        Edge<GeoPoint, String> plain = new Edge<>(london, boston, "across the pond");
        Edge<GeoPoint, String> geo = GeoEdge.create(plain);
        assertTrue("the point of create is the type", geo instanceof GeoEdge);
        assertEquals(london, geo.get());
        assertEquals(boston, geo.getOther(london));
        assertEquals("across the pond", geo.getAttribute());
        // NOTE worth knowing: the result is NOT equal to the edge it came from.
        // Edge.equals begins with getClass() != o.getClass(), so a GeoEdge and a
        // plain Edge never match however alike their contents. Anything that
        // converts a graph's edges with create -- GeoMST.getGeoMST does exactly
        // that -- gives back edges that will not compare equal to the originals.
        assertNotEquals(plain, geo);
        assertNotEquals(geo, plain);
    }
}
