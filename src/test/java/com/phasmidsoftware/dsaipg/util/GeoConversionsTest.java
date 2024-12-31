package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import static com.phasmidsoftware.dsaipg.util.GeoConversions.Degrees;
import static com.phasmidsoftware.dsaipg.util.GeoConversions.UTM;
import static org.junit.Assert.assertEquals;

public class GeoConversionsTest {

    /**
     * CN Tower. See <a href="https://en.wikipedia.org/wiki/Universal_Transverse_Mercator_coordinate_system#Locating_a_position_using_UTM_coordinates">Locating a position using UTM coordinates</a>
     */
    @Test
    public void testPosition2UTM_0() {
        double lat = new Degrees(43, 38, 33.23).decimal; // Actually, it should be 33.24 seconds.
        double lon = new Degrees(79, 23, 13.7).decimal;
        UTM utm = GeoConversions.position2UTM(new GeoConversions.Position(lat, -lon));
        assertEquals(630084, utm.Easting);
        assertEquals(4833438, utm.Northing);
        assertEquals(17, utm.Zone);
        assertEquals('N', utm.Letter);
    }

    @Test
    public void testPosition2UTM_1() {
        double lat = new Degrees(13, 24, 45).decimal;
        double lon = new Degrees(103, 52, 0).decimal;
        UTM utm = GeoConversions.position2UTM(new GeoConversions.Position(lat, lon));
        assertEquals(377299, utm.Easting);
        assertEquals(1483035, utm.Northing);
        assertEquals(48, utm.Zone);
        assertEquals('N', utm.Letter);
    }

    @Test
    public void testPosition2UTM_2() {
        UTM utm = GeoConversions.position2UTM(new GeoConversions.Position(51.253819, 1.0086));
        assertEquals(361034, utm.Easting);
        assertEquals(5679935, utm.Northing);
        assertEquals(31, utm.Zone);
        assertEquals('N', utm.Letter);
    }

    @Test
    public void testUTM2Position_0() {
        GeoConversions.Position position = GeoConversions.UTM2Position("17 N 630084 4833438");
        assertEquals(-79.387139, position.longitude, DELTA);
        assertEquals(43.642567, position.latitude, DELTA);
    }

    @Test
    public void UTM2Position_1() {
        GeoConversions.Position position = GeoConversions.UTM2Position("48 N 377299 1483035");
        assertEquals(103.86667, position.longitude, DELTA);
        assertEquals(13.4125, position.latitude, DELTA);
    }

    @Test
    public void UTM2Position_2() {
        GeoConversions.Position position = GeoConversions.UTM2Position("31 N 361034 5679935");
        assertEquals(1.0086, position.longitude, DELTA);
        assertEquals(51.253819, position.latitude, DELTA);
    }

    /**
     * CN Tower. See <a href="https://en.wikipedia.org/wiki/Universal_Transverse_Mercator_coordinate_system#Locating_a_position_using_UTM_coordinates">Locating a position using UTM coordinates</a>
     */
    @Test
    public void testToPosition_0() {
        double lat = new Degrees(43, 38, 33.23).decimal; // Actually, it should be 33.24 seconds.
        double lon = new Degrees(79, 23, 13.7).decimal;
        GeoConversions.Position target = new GeoConversions.Position(lat, -lon);
        UTM utm = GeoConversions.position2UTM(target);
        GeoConversions.Position position = utm.toPosition();
        assertEquals(target, position);
    }

    public static final double DELTA = 0.00001;

    //    @Test
    // TODO sort this out
    public void testPosition2UTM_3() {
        double lat = new Degrees(0, 0, 0).decimal;
        double lon = new Degrees(0, 0, 0).decimal;
        UTM utm = GeoConversions.position2UTM(new GeoConversions.Position(lat, lon));
        assertEquals(500000, utm.Easting);
        assertEquals(0, utm.Northing);
        assertEquals(31, utm.Zone);
        assertEquals('N', utm.Letter);
    }

    //    @Test
    // TODO sort this out
    public void testPosition2UTM_4() {
        double lat = new Degrees(66, 0, 0).decimal;
        double lon = new Degrees(135, 0, 0).decimal;
        UTM utm = GeoConversions.position2UTM(new GeoConversions.Position(lat, lon));
        assertEquals(500000, utm.Easting, DELTA);
        assertEquals(7320000, utm.Northing, DELTA);
        assertEquals(54, utm.Zone);
        assertEquals('N', utm.Letter);
    }

    //    @Test
    // TODO sort this out
    public void testPosition2UTM_5() {
        double lat = new Degrees(-45, 0, 0).decimal;
        double lon = new Degrees(170, 0, 0).decimal;
        UTM utm = GeoConversions.position2UTM(new GeoConversions.Position(lat, lon));
        assertEquals(500000, utm.Easting, DELTA);
        assertEquals(5010000, utm.Northing, DELTA);
        assertEquals(59, utm.Zone);
        assertEquals('S', utm.Letter);
    }

    @Test
    public void getPosition() {
        GeoConversions.Position position = GeoConversions.getPosition(31, 'N', 361034, 5679935);
        assertEquals(new GeoConversions.Position(51.2538193, 1.0085989), position);
    }
}