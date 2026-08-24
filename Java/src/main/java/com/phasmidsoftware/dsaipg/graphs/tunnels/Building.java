package com.phasmidsoftware.dsaipg.graphs.tunnels;

import com.phasmidsoftware.dsaipg.graphs.gis.GeoPoint;
import com.phasmidsoftware.dsaipg.graphs.gis.Position_Spherical;
import com.phasmidsoftware.dsaipg.graphs.undirected.Position;

import java.util.Objects;

/**
 * The Building class represents a geographical structure or facility on a campus map.
 * It implements the GeoPoint interface and provides information about the building's name,
 * position, code, zone, and other properties such as its presence in the campus tunnel system.
 */
public class Building implements GeoPoint {

    /**
     * Retrieves the name of the building.
     *
     * @return the name of the building as a String
     */
    public String getName() {
        return code;
    }

    /**
     * Retrieves the geographical position of the building.
     *
     * @return the position of the building as a {@code Position} object
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Retrieves the code of the building.
     *
     * @return the code of the building as a string
     */
    public String getCode() {
        return code;
    }

    /**
     * Retrieves the map reference associated with the building.
     *
     * @return an integer representing the campus map reference of the building.
     */
    public int getMap() {
        return map;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return isAlreadyTunneled == building.isAlreadyTunneled &&
                map == building.map &&
                zone.equals(building.zone) &&
                code.equals(building.code) &&
                name.equals(building.name) &&
                position.equals(building.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isAlreadyTunneled, zone, map, code, name, position);
    }

    /**
     * @param map               the campus map reference of the building
     * @param code              the code of the building
     * @param zone              the zone of the building
     * @param lon               the longitude of the building
     * @param lat               the latitude of the building
     * @param isAlreadyTunneled is there already a tunnel to this building?
     * @param name              the name of the building
     */
    public Building(int map, String code, String zone, double lon, double lat, boolean isAlreadyTunneled, String name) {
        this.map = map;
        this.code = code;
        this.zone = zone;
        this.name = name;
        this.position = new Position_Spherical(lat, lon);
        this.isAlreadyTunneled = isAlreadyTunneled;
    }

    final boolean isAlreadyTunneled;
    final String zone;
    private final int map;
    private final String code;
    private final String name;
    private final Position position;
}
