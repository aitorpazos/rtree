package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Zone;

public class Mbr implements HasGeometry {

    private final Zone r;

    public Mbr(Zone r) {
        this.r = r;
    }

    @Override
    public Geometry geometry() {
        return r;
    }

}
