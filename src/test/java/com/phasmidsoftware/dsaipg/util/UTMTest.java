package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UTMTest {

    /**
     * Test file for UTM.toPosition method.
     * This method is responsible for converting the UTM coordinates (easting, northing, zone, letter)
     * back into a geographic position (latitude, longitude).
     */

    @Test
    public void testToPositionNorthernHemisphere() {
        GeoConversions.UTM utm = new GeoConversions.UTM(500000, 4649776, 33, 'T');
        GeoConversions.Position expected = new GeoConversions.Position(42.0, 15.0); // Expected latitude and longitude
        GeoConversions.Position actual = utm.toPosition();
        assertEquals("Latitude should match within tolerance",
                expected.latitude, actual.latitude, 1E-4);
        assertEquals("Longitude should match within tolerance",
                expected.longitude, actual.longitude, 1E-4);
    }

    //    @Test
    // TODO sort this out.
    public void testToPositionSouthernHemisphere() {
        GeoConversions.UTM utm = new GeoConversions.UTM(500000, 4649776, 33, 'K');
        GeoConversions.Position expected = new GeoConversions.Position(-30.0, 21.0); // Expected latitude and longitude
        GeoConversions.Position actual = utm.toPosition();
        assertEquals("Latitude should match within tolerance",
                expected.latitude, actual.latitude, 1E-4);
        assertEquals("Longitude should match within tolerance",
                expected.longitude, actual.longitude, 1E-4);
    }

    //    @Test
    // TODO sort this out
    public void testToPositionEdgeCaseEquator() {
        GeoConversions.UTM utm = new GeoConversions.UTM(500000, 0, 33, 'N');
        GeoConversions.Position expected = new GeoConversions.Position(0.0, 12.0); // Expected latitude and longitude
        GeoConversions.Position actual = utm.toPosition();
        assertEquals("Latitude should match within tolerance",
                expected.latitude, actual.latitude, 1E-4);
        assertEquals("Longitude should match within tolerance",
                expected.longitude, actual.longitude, 1E-4);
    }

    //    @Test
// TODO sort this out
    public void testToPositionEdgeCasePoleHemisphereChange() {
        GeoConversions.UTM utm = new GeoConversions.UTM(500000, 10000000, 31, 'X');
        GeoConversions.Position expected = new GeoConversions.Position(90.0, 3.0); // Expected latitude and longitude
        GeoConversions.Position actual = utm.toPosition();
        assertEquals("Latitude should match within tolerance",
                expected.latitude, actual.latitude, 1E-2);
        assertEquals("Longitude should match within tolerance",
                expected.longitude, actual.longitude, 1E-4);
    }
}