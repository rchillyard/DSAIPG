/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.util;

import java.util.Locale;
import java.util.Objects;

/**
 * For an explanation of UTM, see <a href="https://en.wikipedia.org/wiki/Universal_Transverse_Mercator_coordinate_system">Universal Transverse Mercator coordinate system</a>
 * Original code thanks to <a href="https://stackoverflow.com/users/2548538/user2548538">...</a>.
 * Mind you the code was about as bad as any code could possibly be. I have tried to clean it up.
 * Currently, there are errors, particularly in not using the zone (it only recognizes N or S when converting from UTC).
 * A good online conversion tool is here: <a href="https://awsm-tools.com/utm-to-lat-long">AWSM</a>.
 */
public class GeoConversions {

    public static final int HalfCircleDegrees = 180;
    public static final double DEG2RAD = Math.PI / HalfCircleDegrees;
    public static final double RADIUS = 6366197.724; // radius that is used (but always adjusted by K_0) in meters
    // adjusted radius: 6363651.2449104
    public static final double RADIUS_P = 6399593.625; // polar radius in meters
    public static final double RADIUS_E = 6378137; // equatorial radius in meters (not used)
    public static final int E_0 = 500000; // in meters
    public static final double K_0 = 0.9996;
    public static final int N_0 = 10000000; // in meters
    public static final double E_constant = 0.0820944379;
    public static final double N_constant = 0.006739496742;
    public static final double N_constant_2 = 0.005054622556;
    public static final double N_constant3 = 4.258201531e-05;
    public static final int DEG_ZONE = 6;
    public static final int OFFSET_DEG = 183;

    /**
     * The {@code Degrees} class represents an angular measurement in decimal degrees format.
     * It accepts an angle in degrees, minutes, and seconds and converts it into a decimal representation,
     * where the conversion is performed by combining these components with appropriate scaling factors.
     */
    static class Degrees {
        final double decimal;

        /**
         * Constructs a Degrees instance by converting the provided angle in degrees, minutes,
         * and seconds into its decimal degrees representation.
         *
         * @param degrees the whole-degree part of the angle.
         * @param minutes the minutes part of the angle, where 60 minutes make up one degree.
         * @param seconds the seconds part of the angle, where 3600 seconds make up one degree.
         */
        Degrees(int degrees, int minutes, double seconds) {
            decimal = degrees * 1.0 + minutes / 60.0 + seconds / 3600.0;
        }
    }

    /**
     * The UTM class represents a location in the Universal Transverse Mercator (UTM) coordinate system.
     * This system divides the Earth into a grid of zones and provides coordinates using an easting
     * and northing system within each zone.
     * <p>
     * The UTM class encapsulates the zone, hemisphere letter, easting, and northing for a specific location,
     * and it provides a method to convert the UTM coordinates back into geographic coordinates
     * (latitude and longitude).
     */
    public static class UTM {
        /**
         * Represents the easting coordinate (X-coordinate) in meters for the Universal Transverse Mercator (UTM) coordinate system.
         * The easting indicates the distance eastwards from the central meridian of the UTM zone.
         * This value is typically measured in meters and is used in conjunction with the northing coordinate
         * to specify a location within a specific UTM zone.
         */
        final int Easting;
        /**
         * Represents the northing coordinate (Y-coordinate) in meters for the UTM (Universal Transverse Mercator) system.
         * The northing indicates the distance northwards (or southwards in the southern hemisphere) from the equator
         * within a specific UTM zone.
         */
        final int Northing;
        /**
         * Represents the UTM zone number for a location in the Universal Transverse Mercator (UTM) coordinate system.
         * The Earth is divided longitudinally into 60 zones, numbered from 1 to 60, each spanning 6 degrees of longitude.
         * The UTM zone helps identify the specific region for the easting and northing coordinates.
         */
        final int Zone;
        /**
         * Represents the hemispherical latitude band in the UTM (Universal Transverse Mercator) coordinate system.
         * The value of this variable is a single character typically in the range 'C' to 'X', excluding 'I' and 'O'.
         * It is used to indicate the latitude band of the UTM coordinate, where each band spans 8 degrees of latitude.
         */
        final char Letter;

        /**
         * Constructor for creating a UTM object representing a location in the Universal Transverse Mercator (UTM) coordinate system.
         *
         * @param easting  the easting coordinate (X-coordinate) in meters, representing the distance eastwards from the central meridian of the UTM zone.
         * @param northing the northing coordinate (Y-coordinate) in meters, representing the distance northwards (or southwards in the southern hemisphere) from the equator.
         * @param zone     the UTM zone number, which varies from 1 to 60, representing the longitudinal zone.
         * @param letter   the hemisphere letter indicating the location's latitude band, typically ranging from 'C' to 'X' (excluding 'I' and 'O').
         */
        public UTM(int easting, int northing, int zone, char letter) {
            Easting = easting;
            Northing = northing;
            Zone = zone;
            Letter = letter;
        }

        /**
         * Converts the current UTM coordinates (zone, letter, easting, and northing) into a geographic position
         * represented by latitude and longitude values.
         *
         * @return a {@code Position} object containing the latitude and longitude corresponding to the UTM coordinates.
         */
        public Position toPosition() {
            return getPosition(Zone, Letter, Easting, Northing);
        }
    }

    /**
     * Converts a geographic position, expressed as latitude and longitude, into Universal Transverse Mercator (UTM) coordinates.
     * This involves determining the appropriate UTM zone, calculating easting and northing values,
     * and selecting the correct hemisphere letter based on the latitude.
     *
     * @param p the geographic position to be converted, represented as a Position object containing
     *          latitude and longitude values in decimal degrees.
     * @return a UTM object expressing the provided position in UTM coordinates, including easting,
     *         northing, zone, and hemisphere letter ('N' for northern hemisphere or 'S' for southern hemisphere).
     */
    public static UTM position2UTM(Position p) {
        int zone = (int) Math.floor(p.longitude / DEG_ZONE + 31);
        double adjustedLon = adjustLongitude(p.longitude, zone);
        double radiansLat = p.latitude * DEG2RAD;
        double sineLong = Math.sin(adjustedLon);
        double cosLong = Math.cos(adjustedLon);
        double cosLat = Math.cos(radiansLat);
        double product = cosLat * sineLong;
        double eConstantSqr = square(E_constant);
        double logValue = Math.log((1 + product) / (1 - product));
        double easting = 0.5 * logValue * K_0 * RADIUS_P / Math.pow((1 + eConstantSqr * square(cosLat)), 0.5) * (1 + eConstantSqr / 2 * square(0.5 * Math.log((1 + cosLat * Math.sin(adjustLongitude(p.longitude, zone))) / (1 - product))) * square(cosLat) / 3) + E_0;
        double var4 = radiansLat + sineOfHalfDegrees(p.latitude) / 2;
        double northing = (Math.atan(Math.tan(radiansLat) / cosLong) - radiansLat) * K_0 * RADIUS_P / Math.sqrt(1 + N_constant * square(cosLat)) * (1 + N_constant / 2 * square(0.5 * logValue) * square(cosLat)) + K_0 * RADIUS_P * (radiansLat - N_constant_2 * var4 + N_constant3 * (3 * var4 + sineOfHalfDegrees(p.latitude) * square(cosLat)) / 4 - 1.674057895e-07 * (5 * (3 * var4 + sineOfHalfDegrees(p.latitude) * square(cosLat)) / 4 + sineOfHalfDegrees(p.latitude) * square(cosLat) * square(cosLat)) / 3);
        char letter = 'N';
        if (getLetter(p.latitude) < 'M') {
            northing = northing + N_0;
            letter = 'S';
        }
        return new UTM(round(easting), round(northing), zone, letter);
    }

    /**
     * Represents a geographical position with latitude and longitude values.
     */
    public static class Position {
        /**
         * Represents the latitude component of a geographical position.
         * The latitude is a double value that specifies the north-south
         * position in degrees, with valid values typically ranging
         * from -90 (south pole) to 90 (north pole).
         */
        final double latitude;
        /**
         * Represents the longitude coordinate of a geographical position.
         * Measured in degrees, it specifies the east-west position of a point on the Earth's surface.
         * Valid values for longitude range from -180.0 to 180.0, where:
         * - Negative values correspond to western longitudes.
         * - Positive values correspond to eastern longitudes.
         */
        final double longitude;

        /**
         * Constructs a Position instance with the specified latitude and longitude.
         *
         * @param latitude the latitude of the geographical position, where values range from -90.0 to 90.0.
         * @param longitude the longitude of the geographical position, where values range from -180.0 to 180.0.
         */
        public Position(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        /**
         * Compares this Position object to the specified object for equality.
         * Two Position objects are considered equal if their latitude and longitude
         * values are equal within a small tolerance, as determined by the
         * equalCoordinates method.
         *
         * @param o the object to compare this Position against.
         * @return true if the specified object is a Position with matching latitude
         *         and longitude values; false otherwise.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Position position)) return false;
            return equalCoordinates(position.latitude, latitude) && equalCoordinates(position.longitude, longitude);
        }

        /**
         * Computes a hash code for this Position object based on its latitude and longitude values.
         *
         * @return an integer hash code representing this Position object.
         */
        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude);
        }

        /**
         * Returns a string representation of the Position object.
         * The string includes the latitude and longitude values formatted
         * in the style: "Position{latitude=value, longitude=value}".
         *
         * @return a string representation of the Position object.
         */
        @Override
        public String toString() {
            return "Position{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }

        /**
         * Determines if two coordinate values are approximately equal, considering a small tolerance.
         *
         * @param x the first coordinate value
         * @param y the second coordinate value
         * @return true if the difference between x and y is less than 0.0001, false otherwise
         */
        public boolean equalCoordinates(double x, double y) {
            return Math.abs(x - y) < 1E-4;
        }
    }

    static Position UTM2Position(String utm) {
        String[] parts = utm.split(" ");
        int zone = Integer.parseInt(parts[0]);
        char letter = parts[1].toUpperCase(Locale.ENGLISH).charAt(0);
        double easting = Double.parseDouble(parts[2]);
        double northing = Double.parseDouble(parts[3]);
        return getPosition(zone, letter, easting, northing);
    }

    static Position getPosition(int zone, char letter, double easting, double northing) {
        double hemisphere = letter > 'M' ? 'N' : 'S';
        double north = hemisphere == 'S' ? northing - N_0 : northing;
        double adjustedRadius = RADIUS * K_0;
        double northAdjusted = north / adjustedRadius;
        double v1 = square(Math.cos(northAdjusted));
        double v2 = K_0 * RADIUS_P / Math.sqrt(1 + N_constant * v1);
        double v3 = N_constant * square((easting - E_0) / v2) / 2;
        double v4 = v3 * v1 / 3;
        double v5 = square(N_constant * 3 / 4) * 5 / 3;
        double v6 = Math.sin(2 * northAdjusted);
        double v7 = northAdjusted + v6 / 2;
        double v8 = 3 * v7 + v6 * v1;
        double v9 = Math.pow(N_constant * 3 / 4, 3) * 35 / 27;
        double v10 = northAdjusted - N_constant * 3 / 4 * v7 + v5 * v8 / 4 - v9 * (5 * v8 / 4 + v6 * v1 * v1) / 3;
        double v11 = (north - K_0 * RADIUS_P * v10) / v2 * (1 - v3 * v1);
        double v12 = Math.atan(sinh((easting - E_0) / v2 * (1 - v4)) / Math.cos(v11 + northAdjusted));
        double v13 = Math.cos(v12) * Math.tan(v11 + northAdjusted);
        double v14 = Math.atan(v13) - northAdjusted;
        double v15 = (north / RADIUS / K_0 + (1 + N_constant * v1 - N_constant * Math.sin(northAdjusted) * Math.cos(northAdjusted) * v14 * 3 / 2) * v14) * HalfCircleDegrees / Math.PI;
        return new Position(roundFactored(v15, N_0), roundFactored(v12 * HalfCircleDegrees / Math.PI + zone * DEG_ZONE - OFFSET_DEG, N_0));
    }

    private static char getLetter(double lat) {
        if (lat < -72)
            return 'C';
        else if (lat < -64)
            return 'D';
        else if (lat < -56)
            return 'E';
        else if (lat < -48)
            return 'F';
        else if (lat < -40)
            return 'G';
        else if (lat < -32)
            return 'H';
        else if (lat < -24)
            return 'J';
        else if (lat < -16)
            return 'K';
        else if (lat < -8)
            return 'L';
        else if (lat < 0)
            return 'M';
        else if (lat < 8)
            return 'N';
        else if (lat < 16)
            return 'P';
        else if (lat < 24)
            return 'Q';
        else if (lat < 32)
            return 'R';
        else if (lat < 40)
            return 'S';
        else if (lat < 48)
            return 'T';
        else if (lat < 56)
            return 'U';
        else if (lat < 64)
            return 'V';
        else if (lat < 72)
            return 'W';
        else
            return 'X';
    }

    private static int round(double x) {
        return (int) Math.round(x);
    }

    private static double roundFactored(double x, int factor) {
        double y = Math.round(x * factor);
        return y / factor;
    }

    private static double sineOfHalfDegrees(final double x) {
        return Math.sin(2 * x * DEG2RAD);
    }

    private static double square(final double x) {
        return Math.pow(x, 2);
    }

    private static double adjustLongitude(double lon, final int zone) {
        return (lon - (DEG_ZONE * zone - OFFSET_DEG)) * DEG2RAD;
    }

    private static double sinh(double x) {
        return (Math.exp(x) - Math.exp(-x)) / 2;
    }
}