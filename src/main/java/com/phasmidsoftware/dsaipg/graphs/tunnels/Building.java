/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.graphs.tunnels;

import com.phasmidsoftware.dsaipg.graphs.gis.GeoPoint;
import com.phasmidsoftware.dsaipg.graphs.gis.Position_Spherical;
import com.phasmidsoftware.dsaipg.graphs.undirected.Position;

import java.util.Objects;

/**
 * Building Class -- these instances become the vertices of the graph.
 */
public class Building implements GeoPoint {

    public String getName() {
        return code;
    }

    public Position getPosition() {
        return position;
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

    public String getCode() {
        return code;
    }

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

    final boolean isAlreadyTunneled;
    final String zone;
    private final int map;
    private final String code;
    private final String name;
    private final Position position;
}