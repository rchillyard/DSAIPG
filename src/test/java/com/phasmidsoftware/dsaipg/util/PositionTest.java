package com.phasmidsoftware.dsaipg.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PositionTest {

    /**
     * This test class verifies the behavior of the `equals` method in the `Position` class.
     * The `equals` method ensures that two Position objects are considered equal
     * if their latitude and longitude values are within a defined tolerance.
     */

    @Test
    public void testEquals_SameObject() {
        GeoConversions.Position position = new GeoConversions.Position(45.0, 90.0);
        assertEquals(position, position);
    }

    @Test
    public void testEquals_EqualObjects() {
        GeoConversions.Position position1 = new GeoConversions.Position(45.0, 90.0);
        GeoConversions.Position position2 = new GeoConversions.Position(45.0, 90.0);
        assertEquals(position1, position2);
    }

    @Test
    public void testEquals_NearlyEqualObjects() {
        GeoConversions.Position position1 = new GeoConversions.Position(45.00001, 90.00001);
        GeoConversions.Position position2 = new GeoConversions.Position(45.00002, 90.00002);
        assertEquals(position1, position2);
    }

    @Test
    public void testEquals_DifferentLatitude() {
        GeoConversions.Position position1 = new GeoConversions.Position(45.0, 90.0);
        GeoConversions.Position position2 = new GeoConversions.Position(46.0, 90.0);
        assertNotEquals(position1, position2);
    }

    @Test
    public void testEquals_DifferentLongitude() {
        GeoConversions.Position position1 = new GeoConversions.Position(45.0, 90.0);
        GeoConversions.Position position2 = new GeoConversions.Position(45.0, 91.0);
        assertNotEquals(position1, position2);
    }

    @Test
    public void testEquals_NullObject() {
        GeoConversions.Position position = new GeoConversions.Position(45.0, 90.0);
        assertNotEquals(null, position);
    }

    @Test
    public void testEquals_DifferentClassObject() {
        GeoConversions.Position position = new GeoConversions.Position(45.0, 90.0);
        String differentObject = "NotAPosition";
        assertNotEquals(position, differentObject);
    }
}