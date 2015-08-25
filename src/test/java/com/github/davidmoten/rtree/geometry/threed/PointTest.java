package com.github.davidmoten.rtree.geometry.threed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Zone;

public class PointTest {

    private static final double PRECISION = 0.000001;

    @Test
    public void testCoordinates() {
        Point point = Geometries.point(new double[]{1,2,3});
        assertEquals(1, point.coord(0), PRECISION);
        assertEquals(2, point.coord(1), PRECISION);
        assertEquals(3, point.coord(2), PRECISION);
    }

    @Test
    public void testDistanceToCuboid1() {
        Point p1 = Geometries.point(new double[]{1, 1, 3});
        Zone r = Geometries.zone(new double[]{0, 0, 8}, new double[]{4, 6, 9});
        assertEquals(5, p1.distance(r), PRECISION);
    }

    @Test
    public void testDistanceToCuboid2() {
        Point p1 = Geometries.point(new double[]{1, 0, 3});
        Zone r = Geometries.zone(new double[]{0, 3, 7}, new double[]{4, 6, 9});
        assertEquals(5, p1.distance(r), PRECISION);
    }
    
    @Test
    public void testDistanceToPoint1() {
        Point p1 = Geometries.point(new double[]{1, 1, 3});
        Point p2 = Geometries.point(new double[]{1, 1, 8});
        assertEquals(5, p1.distance(p2), PRECISION);
    }

    @Test
    public void testDistanceToPoint2() {
        Point p1 = Geometries.point(new double[]{1, 0, 3});
        Point p2 = Geometries.point(new double[]{1, 3, 7});
        assertEquals(5, p1.distance(p2), PRECISION);
    }
    
    @Test
    public void testMbr() {
        Point p = Geometries.point(new double[]{1, 2, 3});
        Zone r = Geometries.zone(new double[]{1, 2, 3}, new double[]{1, 2, 3});
        assertEquals(r, p.mbr());
    }

    @Test
    public void testPointIntersectsItself() {
        Point p = Geometries.point(new double[]{1, 2, 3});
        assertTrue(p.distance(p.mbr()) == 0);
    }

    @Test
    public void testIntersectIsFalseWhenPointsDiffer() {
        Point p1 = Geometries.point(new double[]{1, 2, 3});
        Point p2 = Geometries.point(new double[]{1, 2.000001, 3.000001});
        assertFalse(p1.distance(p2.mbr()) == 0);
    }

    @Test
    public void testEquality() {
        Point p1 = Geometries.point(new double[]{1, 2, 3});
        Point p2 = Geometries.point(new double[]{1, 2, 3});
        assertTrue(p1.equals(p2));
    }

    @Test
    public void testInequality() {
        Point p1 = Geometries.point(new double[]{1, 2, 3});
        Point p2 = Geometries.point(new double[]{1, 2, 4});
        assertFalse(p1.equals(p2));
    }

    @Test
    public void testInequalityToNull() {
        Point p1 = Geometries.point(new double[]{1, 2, 3});
        assertFalse(p1.equals(null));
    }

    @Test
    public void testHashCode() {
        Point p = Geometries.point(new double[]{1, 2, 3});
        assertEquals(-133263424, p.hashCode());
    }
}
