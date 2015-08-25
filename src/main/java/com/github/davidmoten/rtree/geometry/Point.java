package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.util.ObjectsHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

public final class Point implements Geometry {

    private final Zone mbr;

    protected Point(float[] coord) {
        this.mbr = Zone.create(coord, coord);
    }

    public static Point create(double[] coord) {
        float[] newArray = new float[coord.length];
        for (int i=0; i<coord.length; i++){
            newArray[i] = (float) coord[i];
        }
        return new Point(newArray);
    }

    @Override
    public Zone mbr() {
        return mbr;
    }

    @Override
    public double distance(Zone r) {
        return mbr.distance(r);
    }

    public double distance(Point p) {
        return Math.sqrt(distanceSquared(p));
    }

    public double distanceSquared(Point p) {
        float diff;
        double radicand = 0;
        for (int d = 0; d < mbr.dim(); d++) {
            if (this.coord(d) > p.coord(d)) {
                diff = this.coord(d) - p.coord(d);
            } else {
                diff = p.coord(d) - this.coord(d);
            }
            radicand += diff * diff;
        }
        
        return radicand;
    }

    @Override
    public boolean intersects(Zone r) {
        return mbr.intersects(r);
    }

    public float coord(int i) {
        return mbr.coord1(i);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mbr);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<Point> other = ObjectsHelper.asClass(obj, Point.class);
        if (other.isPresent()) {
            return Objects.equal(mbr, other.get().mbr());
        } else
            return false;
    }

    @Override
    public String toString() {
        return "Point coords: " + mbr.toString();
    }

}