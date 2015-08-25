package com.github.davidmoten.rtree.geometry;

import java.util.Arrays;

import com.github.davidmoten.util.ObjectsHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

public final class Circle implements Geometry {

    private final float[] coord;
    private final float radius;
    private final Zone mbr;

    protected Circle(float[] coord, float radius) {
        this.coord = coord;
        this.radius = radius;
        float[] coord1 = new float[coord.length];
        float[] coord2 = new float[coord.length];
        for (int i=0; i<this.coord.length; i++){
            coord1[i] = coord[i] - radius;
            coord2[i] = coord[i] + radius;
        }
        this.mbr = Zone.create(coord1, coord2);
    }

    public static Circle create(double[] coord, double radius) {
        float[] newArray = new float[coord.length];
        for (int i=0; i<coord.length; i++){
            newArray[i] = (float) coord[i];
        }
        return new Circle(newArray, (float) radius);
    }

    public float coord(int i) {
        return coord[i];
    }

    @Override
    public Zone mbr() {
        return mbr;
    }

    @Override
    public double distance(Zone r) {
        return Math.max(0, new Point(coord).distance(r) - radius);
    }

    @Override
    public boolean intersects(Zone r) {
        return distance(r) == 0;
    }

    public boolean intersects(Circle c) {
        double total = radius + c.radius;
        return new Point(coord).distanceSquared(new Point(c.coord)) <= total * total;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Arrays.hashCode(coord), radius);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<Circle> other = ObjectsHelper.asClass(obj, Circle.class);
        if (other.isPresent()) {
            return Arrays.equals(coord, other.get().coord)
                    && Objects.equal(radius, other.get().radius);
        } else
            return false;
    }

    public boolean intersects(Point point) {
        float diff = 0.0F;
        float sqrSum = 0.0F;
        for (int i=0; i<coord.length; i++){
            diff = coord[i] - point.coord(i);
            sqrSum += diff * diff;
        }
        return Math.sqrt(sqrSum) <= radius;
    }

    private float sqr(float x) {
        return x * x;
    }
    
    @Override
    public String toString(){
        return "Circle [ coord="+ Arrays.toString(coord) + ", radius=" + radius + ", mbr=" + (null != mbr ? mbr.toString() : "null") + " ]";
    }
}
