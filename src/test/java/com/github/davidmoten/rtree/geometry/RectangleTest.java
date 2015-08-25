package com.github.davidmoten.rtree.geometry;

import static com.github.davidmoten.rtree.geometry.Geometries.zone;
import static org.junit.Assert.*;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Zone;

public class RectangleTest {

    private static final double PRECISION = 0.00001;

    @Test
    public void testDistanceToSelfIsZero() {
        Zone r = zone(new double[]{0,0}, new double[]{1,1});
        assertEquals(0, r.distance(r), PRECISION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testXParametersWrongOrderThrowsException() {
        zone(new double[]{2,0}, new double[]{1,1});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testYParametersWrongOrderThrowsException() {
        zone(new double[]{0,2}, new double[]{1,1});
    }

    @Test
    public void testDistanceToOverlapIsZero() {
        Zone r = zone(new double[]{0,0}, new double[]{2,2});
        Zone r2 = zone(new double[]{1,1}, new double[]{3,3});

        assertEquals(0, r.distance(r2), PRECISION);
        assertEquals(0, r2.distance(r), PRECISION);
    }

    @Test
    public void testDistanceWhenSeparatedByXOnly() {
        Zone r = zone(new double[]{0,0}, new double[]{2,2});
        Zone r2 = zone(new double[]{3,0}, new double[]{4,2});

        assertEquals(1, r.distance(r2), PRECISION);
        assertEquals(1, r2.distance(r), PRECISION);
    }

    @Test
    public void testDistanceWhenSeparatedByXOnlyAndOverlapOnY() {
        Zone r = zone(new double[]{0,0}, new double[]{2,2});
        Zone r2 = zone(new double[]{3, 1.5f}, new double[]{4, 3.5f});

        assertEquals(1, r.distance(r2), PRECISION);
        assertEquals(1, r2.distance(r), PRECISION);
    }

    @Test
    public void testDistanceWhenSeparatedByDiagonally() {
        Zone r = zone(new double[]{0,0}, new double[]{2,1});
        Zone r2 = zone(new double[]{3,6}, new double[]{10,8});

        assertEquals(Math.sqrt(26), r.distance(r2), PRECISION);
        assertEquals(Math.sqrt(26), r2.distance(r), PRECISION);
    }

    @Test
    public void testInequalityWithNull() {
        assertFalse(zone(new double[]{0,0}, new double[]{1,1}).equals(null));
    }

    @Test
    public void testSimpleEquality() {
        Zone r = zone(new double[]{0,0}, new double[]{2,1});
        Zone r2 = zone(new double[]{0,0}, new double[]{2,1});

        assertTrue(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality1() {
        Zone r = zone(new double[]{0,0}, new double[]{2,1});
        Zone r2 = zone(new double[]{0,0}, new double[]{2,2});

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality2() {
        Zone r = zone(new double[]{0,0}, new double[]{2,1});
        Zone r2 = zone(new double[]{1,0}, new double[]{2,1});

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality3() {
        Zone r = zone(new double[]{0,0}, new double[]{2,1});
        Zone r2 = zone(new double[]{0,1}, new double[]{2,1});

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality4() {
        Zone r = zone(new double[]{0,0}, new double[]{2,2});
        Zone r2 = zone(new double[]{0,0}, new double[]{1,2});

        assertFalse(r.equals(r2));
    }

    @Test
    public void testGeometry() {
        Zone r = zone(new double[]{0,0}, new double[]{2,1});
        assertTrue(r.equals(r.geometry()));
    }

    @Test
    public void testIntersects() {
        Zone a = zone(new double[]{14,14}, new double[]{86,37});
        Zone b = zone(new double[]{13,23}, new double[]{50,80});
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    public void testIntersectsNoRectangleContainsCornerOfAnother() {
        Zone a = zone(new double[]{10,10}, new double[]{50,50});
        Zone b = zone(new double[]{28.0,4.0}, new double[]{34.0,85.0});
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    public void testIntersectsOneRectangleContainsTheOther() {
        Zone a = zone(new double[]{10,10}, new double[]{50,50});
        Zone b = zone(new double[]{20,20}, new double[]{40,40});
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }
    
    @Test
    public void testContains() {
        Zone r = zone(new double[]{10,20},new double[]{30,40});
        assertTrue(r.contains(new double[]{20,30}));
    }
    
    @Test
    public void testContainsReturnsFalseWhenLessThanMinY() {
        Zone r = zone(new double[]{10,20},new double[]{30,40});
        assertFalse(r.contains(new double[]{20,19}));
    }
    
    @Test
    public void testContainsReturnsFalseWhenGreaterThanMaxY() {
        Zone r = zone(new double[]{10,20},new double[]{30,40});
        assertFalse(r.contains(new double[]{20,41}));
    }
    
    @Test
    public void testContainsReturnsFalseWhenGreaterThanMaxX() {
        Zone r = zone(new double[]{10,20},new double[]{30,40});
        assertFalse(r.contains(new double[]{31,30}));
    }
    
    @Test
    public void testContainsReturnsFalseWhenLessThanMinX() {
        Zone r = zone(new double[]{10,20},new double[]{30,40});
        assertFalse(r.contains(new double[]{9,30}));
    }

}