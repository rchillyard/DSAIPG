package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Position;

import java.util.Objects;

/**
 * A class representing a position in a spherical coordinate system defined by latitude and longitude.
 * This class implements the {@link Position} interface, which provides methods to retrieve coordinate values.
 * It models geographical coordinates where latitude specifies the angular distance north or south of the equator,
 * and longitude specifies the angular distance east or west of the prime meridian.
 */
public class Position_Spherical implements Position {

    /**
     * Retrieves the latitude value of this position.
     *
     * @return the latitude, which represents the angular distance north or south of the equator.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Retrieves the longitude value of this position in the spherical coordinate system.
     *
     * @return the longitude, representing the angular distance east or west of the prime meridian.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Retrieves the x-coordinate value, representing the latitude
     * in the spherical coordinate system.
     *
     * @return the latitude as a double value.
     */
    public double getX() {
        return latitude;
    }

    /**
     * Retrieves the longitude value associated with this position.
     *
     * @return the longitude coordinate, representing the angular distance east or west of the prime meridian.
     */
    public double getY() {
        return longitude;
    }

    /**
     * Constructs a Position_Spherical object with specified latitude and longitude.
     *
     * @param latitude  the angular distance in degrees north or south of the equator,
     *                  where positive values represent the northern hemisphere
     *                  and negative values represent the southern hemisphere.
     * @param longitude the angular distance in degrees east or west of the prime meridian,
     *                  where positive values represent the eastern hemisphere
     *                  and negative values represent the western hemisphere.
     */
    public Position_Spherical(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return longitude + "," + latitude + ",0";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position_Spherical that = (Position_Spherical) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    private final double latitude;
    private final double longitude;
}
