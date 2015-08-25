package com.github.davidmoten.rtree.geometry;

import com.google.common.annotations.VisibleForTesting;

public final class Geometries {

    private Geometries() {
        // prevent instantiation
    }

    public static Point point(double[] coord) {
        return Point.create(coord);
    }

    public static Zone zone(double[] coord1, double[] coord2) {
        return Zone.create(coord1, coord2);
    }

    public static Circle circle(double[] coord, double radius) {
        return Circle.create(coord, radius);
    }

    public static Zone rectangleGeographic(double lon1, double lat1, double lon2, double lat2) {
        double x1 = normalizeLongitude(lon1);
        double x2 = normalizeLongitude(lon2);
        if (x2 < x1) {
            x2 += 360;
        }
        double[] coord1 = {x1, lat1};
        double[] coord2 = {x2, lat2};
        return zone(coord1, coord2);
    }

    public static Point pointGeographic(double lon, double lat) {
        double[] coord = {normalizeLongitude(lon), lat};
        return point(coord);
    }

    @VisibleForTesting
    static double normalizeLongitude(double d) {
        double sign = Math.signum(d);
        double x = Math.abs(d) / 360;
        double x2 = (x - Math.floor(x)) * 360;
        if (x2 >= 180)
            x2 -= 360;
        return x2 * sign;
    }
}
