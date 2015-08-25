package com.github.davidmoten.rtree.geometry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.github.davidmoten.util.ObjectsHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public final class Zone implements Geometry, HasGeometry {
    private final float[] coord1, coord2;
    private final int dim;

    protected Zone(float[] coord1, float[] coord2) {
        Preconditions.checkArgument(coord1.length != 0 && coord1.length == coord2.length);
        for (int i = 0; i < coord1.length; i++) {
            Preconditions.checkArgument(coord2[i] >= coord1[i]);
        }

        this.coord1 = coord1;
        this.coord2 = coord2;
        this.dim = coord1.length;
    }

    private class CoordPair {
        private int i;
        private int j;

        public CoordPair(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
    	public boolean equals (Object o){
    		if (!(o instanceof CoordPair)){
    			return false;
    		}
    		CoordPair oPair = (CoordPair) o;
    		if ((this.i == oPair.i && this.j == oPair.j)
    			|| (this.j == oPair.i && this.i == oPair.j)){
    		    return true;
    		} else {
    		    return false;
    		}
    	}

        @Override
        public int hashCode() {
            return i + j;
        }
    }

    public int dim() {
        return dim;
    }

    public float[] coord1() {
        return coord1;
    }

    public float coord1(int dim) {
        Preconditions.checkArgument(this.dim >= dim);
        return coord1[dim];
    }

    public float[] coord2() {
        return coord2;
    }

    public float coord2(int dim) {
        Preconditions.checkArgument(this.dim >= dim);
        return coord2[dim];
    }

    public float content() {
        float result = 1.0F;
        for (int i = 0; i < this.dim; i++) {
            result = result * (coord2[i] - coord1[i]);
        }
        return result;
    }

    public Zone add(Zone r) {
        Preconditions.checkArgument(null != r);
        Preconditions.checkArgument(this.dim == r.dim());

        float[] mins = new float[this.dim];
        float[] maxs = new float[this.dim];

        for (int i = 0; i < this.dim; i++) {
            mins[i] = Math.min(this.coord1[i], r.coord1()[i]);
            maxs[i] = Math.max(this.coord2[i], r.coord2()[i]);
        }
        return new Zone(mins, maxs);
    }

    public static Zone create(double[] coord1, double[] coord2) {
        float[] newCoord1 = new float[coord1.length];
        for (int i=0; i<coord1.length; i++){
            newCoord1[i] = (float) coord1[i];
        }
        float[] newCoord2 = new float[coord2.length];
        for (int i=0; i<coord2.length; i++){
            newCoord2[i] = (float) coord2[i];
        }
        return new Zone(newCoord1, newCoord2);
    }
    
    public static Zone create(float[] coord1, float[] coord2) {
        return new Zone(coord1, coord2);
    }

    public boolean contains(double[] point) {
        Preconditions.checkArgument(this.dim == point.length);

        boolean contained = true;

        for (int i = 0; i < this.dim; i++) {
            if (coord1[i] > point[i] || coord2[i] < point[i]) {
                contained = false;
            }
        }
        return contained;
    }

    @Override
    public boolean intersects(Zone r) {
        // Calculating intersection body
        for (int d = 0; d < this.dim; d++) {
            // If one of mins[d] > maxs[d] they do not intersect
            if (Math.max(this.coord1[d], r.coord1(d)) > Math.min(this.coord2[d], r.coord2(d)))
                return false;
        }

        return true;
    }

    @Override
    public double distance(Zone r) {
        if (intersects(r))
            return 0;
        else {
            double radicand = 0;
            for (int d = 0; d < this.dim; d++) {
                float diff = 0;
                // We need to prevent overlapping dimmensions to be taken into account
                if (this.coord1[d] > r.coord2(d)) {
                    diff = this.coord1[d] - r.coord2(d);
                } else if (this.coord2[d] < r.coord1(d)){ 
                    diff = r.coord1(d) - this.coord2[d];
                }
                radicand += diff * diff;
            }

            return Math.sqrt(radicand);
        }
    }

    @Override
    public Zone mbr() {
        return this;
    }

    @Override
    public String toString() {
        return "Zone [ coord1=" + Arrays.toString(coord1) + ", coord2=" + Arrays.toString(coord2) + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Arrays.hashCode(coord1), Arrays.hashCode(coord2));
    }

    @Override
    public boolean equals(Object obj) {
        Optional<Zone> other = ObjectsHelper.asClass(obj, Zone.class);
        if (other.isPresent()) {
            return Arrays.equals(coord1, other.get().coord1) && Arrays.equals(coord2, other.get().coord2);
        } else
            return false;
    }

    public float intersectionArea(Zone r) {
        if (!intersects(r))
            return 0;
        else {
            float[] mins = new float[this.dim];
            float[] maxs = new float[this.dim];

            // Calculating intersection body
            for (int d = 0; d < this.dim; d++) {
                // If one of mins[d] > maxs[d] they do not intersect
                mins[d] = Math.max(this.coord1[d], r.coord1(d));
                maxs[d] = Math.min(this.coord2[d], r.coord2(d));
            }
            return create(mins, maxs).content();
        }
    }

    public float perimeter() {
        Set<CoordPair> coordPairs = new HashSet<CoordPair>();
        float returnPerimeter = 0.0F;
        for (int i=0; i<this.dim; i++){
            for(int j=0; j<this.dim; j++){
                CoordPair pair = new CoordPair(i, j);
                if (!coordPairs.contains(pair)){
                    coordPairs.add(pair);
                    returnPerimeter += 2.0F * (Math.abs(this.coord2(i) - this.coord1(i)) 
                                                + Math.abs(this.coord2(j) - this.coord1(j)));
                }
                
            }
        }
        return returnPerimeter;
    }

    @Override
    public Geometry geometry() {
        return this;
    }

}