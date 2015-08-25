package com.github.davidmoten.rtree.geometry.threed;

import static com.github.davidmoten.rtree.geometry.Geometries.circle;
import static com.github.davidmoten.rtree.geometry.Geometries.zone;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Intersects;
import com.github.davidmoten.util.TestingUtil;

public class IntersectsTest {

    @Test
    public void testConstructorIsPrivate() {
        TestingUtil.callConstructorAndCheckIsPrivate(Intersects.class);
    }
    
    @Test
    public void testRectangleIntersectsCircle() {
        assertTrue(Intersects.rectangleIntersectsCircle.call(zone(new double[]{0, 0, 0}, new double[]{0, 0, 0}), circle(new double[]{0, 0, 0}, 1)));
    }
    
    @Test
    public void testRectangleDoesNotIntersectCircle() {
        assertFalse(Intersects.rectangleIntersectsCircle.call(zone(new double[]{0, 0, 0}, new double[]{0, 0, 0}), circle(new double[]{100, 100, 100}, 1)));
    }
    
}
