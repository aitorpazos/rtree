package com.github.davidmoten.rtree.geometry;

import rx.functions.Func2;

public class Intersects {
    
    private Intersects() {
        // prevent instantiation
    }

	public static final Func2<Zone, Circle, Boolean> rectangleIntersectsCircle = new Func2<Zone, Circle, Boolean>() {
		@Override
		public Boolean call(Zone zone, Circle circle) {
			return circle.intersects(zone);
		}
	};

	public static final Func2<Point, Circle, Boolean> pointIntersectsCircle = new Func2<Point, Circle, Boolean>() {
		@Override
		public Boolean call(Point point, Circle circle) {
			return circle.intersects(point);
		}
	};

}
