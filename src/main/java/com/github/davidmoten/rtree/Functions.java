package com.github.davidmoten.rtree;

import java.util.List;

import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;
import com.github.davidmoten.rtree.geometry.Zone;

/**
 * Utility functions for making {@link Selector}s and {@link Splitter}s.
 *
 */
public final class Functions {

    private Functions() {
        // prevent instantiation
    }

    public static final Func1<ListPair<? extends HasGeometry>, Double> overlapListPair = new Func1<ListPair<? extends HasGeometry>, Double>() {

        @Override
        public Double call(ListPair<? extends HasGeometry> pair) {
            return (double) pair.group1().geometry().mbr()
                    .intersectionArea(pair.group2().geometry().mbr());
        }
    };

    public static Func1<HasGeometry, Double> overlapArea(final Zone r,
            final List<? extends HasGeometry> list) {
        return new Func1<HasGeometry, Double>() {

            @Override
            public Double call(HasGeometry g) {
                Zone gPlusR = g.geometry().mbr().add(r);
                double m = 0;
                for (HasGeometry other : list) {
                    if (other != g) {
                        m += gPlusR.intersectionArea(other.geometry().mbr());
                    }
                }
                return m;
            }
        };
    }

    public static Func1<HasGeometry, Double> areaIncrease(final Zone r) {
        return new Func1<HasGeometry, Double>() {
            @Override
            public Double call(HasGeometry g) {
                Zone gPlusR = g.geometry().mbr().add(r);
                return (double) (gPlusR.content() - g.geometry().mbr().content());
            }
        };
    }

}
