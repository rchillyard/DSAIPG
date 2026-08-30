package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.util.general.CancelOnNotImplemented;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Position_SphericalTest {
    @Rule
    public final TestRule cancelOnNotImplemented = new CancelOnNotImplemented();

    /**
     * Boston's Logan airport, roughly: 42°21′51″N, 71°0′18″W.
     */
    private final Position_Spherical boston = new Position_Spherical(42.3644, -71.0053);

    @Test
    public void getLatitude() {
        assertEquals(42.3644, boston.getLatitude(), 1E-10);
        assertEquals("south of the equator is negative",
                -33.9, new Position_Spherical(-33.9, 18.6).getLatitude(), 1E-10);
    }

    @Test
    public void getLongitude() {
        assertEquals(-71.0053, boston.getLongitude(), 1E-10);
        assertEquals("east of Greenwich is positive",
                18.6, new Position_Spherical(-33.9, 18.6).getLongitude(), 1E-10);
    }

    /**
     * NOTE x is the LATITUDE, which is the vertical axis on a map. That is the
     * wrong way round from the way a map is usually drawn, and it is why
     * {@link GeoGraphSpherical} can read a Position without knowing it is spherical.
     */
    @Test
    public void getX() {
        assertEquals(boston.getLatitude(), boston.getX(), 1E-10);
    }

    @Test
    public void getY() {
        assertEquals(boston.getLongitude(), boston.getY(), 1E-10);
    }

    /**
     * The string form is for KML, which wants longitude, latitude, altitude -- the
     * opposite order to the constructor's, and the reason this is not simply the
     * fields printed out.
     */
    @Test
    public void toStringTest() {
        assertEquals("-71.0053,42.3644,0", boston.toString());
        assertEquals("0.0,0.0,0", new Position_Spherical(0, 0).toString());
    }

    @Test
    public void equalsAndHashCode() {
        assertEquals(boston, new Position_Spherical(42.3644, -71.0053));
        assertEquals(boston.hashCode(), new Position_Spherical(42.3644, -71.0053).hashCode());
        assertNotEquals("latitude and longitude are not interchangeable",
                boston, new Position_Spherical(-71.0053, 42.3644));
    }
}
