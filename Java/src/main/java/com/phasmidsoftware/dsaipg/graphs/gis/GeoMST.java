package com.phasmidsoftware.dsaipg.graphs.gis;

public interface GeoMST<V extends GeoPoint, X extends Comparable<X> & Sequenced> {
    Geo<V, X> getGeoMST(Geo<V, X> geoGraph);
}
