package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Position;

class MockGeoPoint implements GeoPoint {

    private final String name;
    private final Position position;

    public MockGeoPoint(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * NOTE added so that an Edge between two of these prints something a test can
     * assert. Without it, Edge.toString reports the default Object form, which is
     * an address.
     */
    @Override
    public String toString() {
        return name;
    }
}
