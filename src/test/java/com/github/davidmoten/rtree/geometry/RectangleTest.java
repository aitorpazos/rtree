package com.github.davidmoten.rtree.geometry;

import static com.github.davidmoten.rtree.geometry.Geometries.zone;
import static org.junit.Assert.*;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Zone;

public class RectangleTest {

    private static final double PRECISION = 0.00001;

    @Test
    public void testDistanceToSelfIsZero() {
        Zone r = zone(0, 0, 1, 1);
        assertEquals(0, r.distance(r), PRECISION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testXParametersWrongOrderThrowsException() {
        zone(2, 0, 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testYParametersWrongOrderThrowsException() {
        zone(0, 2, 1, 1);
    }

    @Test
    public void testDistanceToOverlapIsZero() {
        Zone r = zone(0, 0, 2, 2);
        Zone r2 = zone(1, 1, 3, 3);

        assertEquals(0, r.distance(r2), PRECISION);
        assertEquals(0, r2.distance(r), PRECISION);
    }

    @Test
    public void testDistanceWhenSeparatedByXOnly() {
        Zone r = zone(0, 0, 2, 2);
        Zone r2 = zone(3, 0, 4, 2);

        assertEquals(1, r.distance(r2), PRECISION);
        assertEquals(1, r2.distance(r), PRECISION);
    }

    @Test
    public void testDistanceWhenSeparatedByXOnlyAndOverlapOnY() {
        Zone r = zone(0, 0, 2, 2);
        Zone r2 = zone(3, 1.5f, 4, 3.5f);

        assertEquals(1, r.distance(r2), PRECISION);
        assertEquals(1, r2.distance(r), PRECISION);
    }

    @Test
    public void testDistanceWhenSeparatedByDiagonally() {
        Zone r = zone(0, 0, 2, 1);
        Zone r2 = zone(3, 6, 10, 8);

        assertEquals(Math.sqrt(26), r.distance(r2), PRECISION);
        assertEquals(Math.sqrt(26), r2.distance(r), PRECISION);
    }

    @Test
    public void testInequalityWithNull() {
        assertFalse(zone(0, 0, 1, 1).equals(null));
    }

    @Test
    public void testSimpleEquality() {
        Zone r = zone(0, 0, 2, 1);
        Zone r2 = zone(0, 0, 2, 1);

        assertTrue(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality1() {
        Zone r = zone(0, 0, 2, 1);
        Zone r2 = zone(0, 0, 2, 2);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality2() {
        Zone r = zone(0, 0, 2, 1);
        Zone r2 = zone(1, 0, 2, 1);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality3() {
        Zone r = zone(0, 0, 2, 1);
        Zone r2 = zone(0, 1, 2, 1);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality4() {
        Zone r = zone(0, 0, 2, 2);
        Zone r2 = zone(0, 0, 1, 2);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testGeometry() {
        Zone r = zone(0, 0, 2, 1);
        assertTrue(r.equals(r.geometry()));
    }

    @Test
    public void testIntersects() {
        Zone a = zone(14, 14, 86, 37);
        Zone b = zone(13, 23, 50, 80);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    public void testIntersectsNoRectangleContainsCornerOfAnother() {
        Zone a = zone(10, 10, 50, 50);
        Zone b = zone(28.0, 4.0, 34.0, 85.0);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    public void testIntersectsOneRectangleContainsTheOther() {
        Zone a = zone(10, 10, 50, 50);
        Zone b = zone(20, 20, 40, 40);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }
    
    @Test
    public void testContains() {
        Zone r = zone(10,20,30,40);
        assertTrue(r.contains(20,30));
    }
    
    @Test
    public void testContainsReturnsFalseWhenLessThanMinY() {
        Zone r = zone(10,20,30,40);
        assertFalse(r.contains(20,19));
    }
    
    @Test
    public void testContainsReturnsFalseWhenGreaterThanMaxY() {
        Zone r = zone(10,20,30,40);
        assertFalse(r.contains(20,41));
    }
    
    @Test
    public void testContainsReturnsFalseWhenGreaterThanMaxX() {
        Zone r = zone(10,20,30,40);
        assertFalse(r.contains(31,30));
    }
    
    @Test
    public void testContainsReturnsFalseWhenLessThanMinX() {
        Zone r = zone(10,20,30,40);
        assertFalse(r.contains(9,30));
    }

}