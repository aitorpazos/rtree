package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Zone;

final class RectangleDepth {
    private final Zone zone;
    private final int depth;

    RectangleDepth(Zone zone, int depth) {
        super();
        this.zone = zone;
        this.depth = depth;
    }

    Zone getRectangle() {
        return zone;
    }

    int getDepth() {
        return depth;
    }

}
