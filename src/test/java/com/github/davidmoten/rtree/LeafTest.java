package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Zone;

public class LeafTest {

    private static Context context = new Context(2, 4, new SelectorMinimalAreaIncrease(),
            new SplitterQuadratic());

    @Test(expected = IllegalArgumentException.class)
    public void testCannotHaveZeroChildren() {
        new Leaf<Object, Zone>(new ArrayList<Entry<Object, Zone>>(), context);
    }

    @Test
    public void testMbr() {
        Zone r1 = Geometries.zone(0, 1, 3, 5);
        Zone r2 = Geometries.zone(1, 2, 4, 6);
        @SuppressWarnings("unchecked")
        Zone r = new Leaf<Object, Zone>(Arrays.asList(Entry.entry(new Object(), r1),
                Entry.entry(new Object(), r2)), context).geometry().mbr();
        assertEquals(r1.add(r2), r);
    }
}
