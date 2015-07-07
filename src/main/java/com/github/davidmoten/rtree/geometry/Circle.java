package com.github.davidmoten.rtree.geometry;

import com.github.davidmoten.util.ObjectsHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

public final class Circle implements Geometry {

    private final float x, y, radius;
    private final Zone mbr;

    protected Circle(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.mbr = Zone.create(x - radius, y - radius, x + radius, y + radius);
    }

    public static Circle create(double x, double y, double radius) {
        return new Circle((float) x, (float) y, (float) radius);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    @Override
    public Zone mbr() {
        return mbr;
    }

    @Override
    public double distance(Zone r) {
        return Math.max(0, new Point(x, y).distance(r) - radius);
    }

    @Override
    public boolean intersects(Zone r) {
        return distance(r) == 0;
    }

    public boolean intersects(Circle c) {
        double total = radius + c.radius;
        return new Point(x, y).distanceSquared(new Point(c.x, c.y)) <= total * total;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y, radius);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<Circle> other = ObjectsHelper.asClass(obj, Circle.class);
        if (other.isPresent()) {
            return Objects.equal(x, other.get().x) && Objects.equal(y, other.get().y)
                    && Objects.equal(radius, other.get().radius);
        } else
            return false;
    }

    public boolean intersects(Point point) {
        return Math.sqrt(sqr(x - point.x()) + sqr(y - point.y())) <= radius;
    }

    private float sqr(float x) {
        return x * x;
    }
}
