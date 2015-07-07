package com.github.davidmoten.rtree.geometry;

import com.google.common.annotations.VisibleForTesting;

public final class Geometries {

    private Geometries() {
        // prevent instantiation
    }

    public static Point point(double x, double y) {
        return Point.create(x, y);
    }

    public static Zone zone(double x1, double y1, double x2, double y2) {
        return Zone.create(x1, y1, x2, y2);
    }

    public static Circle circle(double x, double y, double radius) {
        return Circle.create(x, y, radius);
    }

    public static Zone rectangleGeographic(double lon1, double lat1, double lon2, double lat2) {
        double x1 = normalizeLongitude(lon1);
        double x2 = normalizeLongitude(lon2);
        if (x2 < x1) {
            x2 += 360;
        }
        return zone(x1, lat1, x2, lat2);
    }

    public static Point pointGeographic(double lon, double lat) {
        return point(normalizeLongitude(lon), lat);
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
