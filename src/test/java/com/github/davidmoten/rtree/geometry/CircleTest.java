package com.github.davidmoten.rtree.geometry;

import static com.github.davidmoten.rtree.geometry.Geometries.circle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CircleTest {
    private static final double PRECISION = 0.000001;

    @Test
    public void testCoordinates() {
        Circle circle = circle(new double[]{1,2}, 3);
        assertEquals(1, circle.coord(0), PRECISION);
        assertEquals(2, circle.coord(1), PRECISION);
    }

    @Test
    public void testDistance() {
        Circle circle = circle(new double[]{0,0}, 1);
        Zone r = Geometries.zone(new double[]{1,1}, new double[]{2,2});
        assertEquals(Math.sqrt(2) - 1, circle.distance(r), PRECISION);
    }

    @Test
    public void testMbr() {
        Circle circle = circle(new double[]{1,2}, 3);
        Zone r = Geometries.zone(new double[]{-2, -1}, new double[]{4, 5});
        assertEquals(r, circle.mbr());
    }

    @Test
    public void testEquality() {
        Circle circle1 = circle(new double[]{1,2}, 3);
        Circle circle2 = circle(new double[]{1,2}, 3);
        assertEquals(circle1, circle2);
    }

    @Test
    public void testInequalityRadius() {
        Circle circle1 = circle(new double[]{1,2}, 3);
        Circle circle2 = circle(new double[]{1,2}, 4);
        assertNotEquals(circle1, circle2);
    }

    @Test
    public void testInequalityX() {
        Circle circle1 = circle(new double[]{1,2}, 3);
        Circle circle2 = circle(new double[]{2,2}, 3);
        assertNotEquals(circle1, circle2);
    }

    @Test
    public void testInequalityY() {
        Circle circle1 = circle(new double[]{1,2}, 3);
        Circle circle2 = circle(new double[]{1,3}, 3);
        assertNotEquals(circle1, circle2);
    }

    @Test
    public void testInequalityWithNull() {
        Circle circle = circle(new double[]{1,2}, 3);
        assertFalse(circle.equals(null));
    }

    @Test
    public void testHashCode() {
        Circle circle = circle(new double[]{1,2}, 3);
        assertEquals(1606449184, circle.hashCode());
    }

    @Test
    public void testDistanceIsZeroWhenIntersects() {
        Circle circle = circle(new double[]{0,0}, 1);
        assertTrue(circle.distance(Geometries.zone(new double[]{0,1}, new double[]{0,1})) == 0);
    }

    @Test
    public void testIntersects2() {
        Circle circle = circle(new double[]{0,0}, 1);
        assertTrue(circle.distance(Geometries.zone(new double[]{0,1.1}, new double[]{0,1.1})) != 0);
    }

    @Test
    public void testIntersects3() {
        Circle circle = circle(new double[]{0,0}, 1);
        assertTrue(circle.distance(Geometries.zone(new double[]{1,1}, new double[]{1,1})) != 0);
    }

    @Test
    public void testIntersectsReturnsTrue() {
        assertTrue(circle(new double[]{0,0}, 1).intersects(Geometries.zone(new double[]{0,0}, new double[]{1,1})));
    }

    @Test
    public void testIntersectsReturnsFalse() {
        assertFalse(circle(new double[]{0,0}, 1).intersects(Geometries.zone(new double[]{10,10}, new double[]{11,11})));
    }
    
    @Test
    public void testIntersects() {
        Circle a = circle(new double[]{0,0},1);
        Circle b = circle(new double[]{0.1,0.1}, 1);
        assertTrue(a.intersects(b));
    }
    
    @Test
    public void testDoNotIntersect() {
        Circle a = circle(new double[]{0,0},1);
        Circle b = circle(new double[]{100,100}, 1);
        assertFalse(a.intersects(b));
    }
    
    @Test
    public void testIntersectsPoint() {
        assertTrue(circle(new double[]{0,0},1).intersects(Geometries.point(new double[]{0,0})));
    }
    
    @Test
    public void testDoesNotIntersectPoint() {
        assertFalse(circle(new double[]{0,0},1).intersects(Geometries.point(new double[]{100,100})));
    }
}
